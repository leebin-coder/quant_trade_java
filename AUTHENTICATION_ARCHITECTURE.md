# 认证鉴权架构说明

## 整体架构

本系统采用**网关统一鉴权**的架构，所有外部请求必须通过 API 网关，由网关统一进行 JWT Token 验证。

```
┌─────────────┐
│   前端应用   │
└──────┬──────┘
       │ HTTP Request
       │ Authorization: Bearer {token}
       ▼
┌─────────────────────────────────────────┐
│          API Gateway (8080)             │
│  ┌───────────────────────────────────┐  │
│  │      AuthFilter (全局过滤器)      │  │
│  │  1. 检查白名单                     │  │
│  │  2. 验证 JWT Token                │  │
│  │  3. 提取用户信息                   │  │
│  │  4. 注入请求头 (X-User-Id等)      │  │
│  └───────────────────────────────────┘  │
└──────┬──────────────────────────────────┘
       │ 路由转发
       ├─────────────┬─────────────┬──────────────┐
       ▼             ▼             ▼              ▼
┌─────────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
│ User Service│ │  Market  │ │  Trade   │ │   Risk   │
│   (8081)    │ │ (8082)   │ │ (8083)   │ │  (8084)  │
└─────────────┘ └──────────┘ └──────────┘ └──────────┘
```

## 鉴权流程

### 1. 登录流程

```
前端                    网关                    User Service
 │                       │                          │
 ├─POST /api/auth/login─>│                          │
 │ {phone, code}         │                          │
 │                       ├─(白名单,不验证Token)────>│
 │                       │                          │
 │                       │                          ├─验证验证码
 │                       │                          ├─自动注册（如需要）
 │                       │                          ├─生成 JWT Token
 │                       │                          │
 │                       │<─────────────────────────┤
 │<──────────────────────┤  {userId, token, ...}    │
 │  {token, userId}      │                          │
 │                       │                          │
 ├─localStorage.set('token', token)                 │
```

### 2. 受保护资源访问流程

```
前端                    网关                    微服务
 │                       │                          │
 ├─GET /api/stocks/1────>│                          │
 │ Authorization: Bearer token                      │
 │                       │                          │
 │                       ├─检查白名单                │
 │                       ├─提取Token                │
 │                       ├─验证Token签名和有效期      │
 │                       ├─提取userId & phone       │
 │                       │                          │
 │                       ├─添加请求头─────────────>│
 │                       │ X-User-Id: 123           │
 │                       │ X-User-Phone: 138xxx     │
 │                       │                          │
 │                       │                          ├─处理业务逻辑
 │                       │                          │
 │                       │<─────────────────────────┤
 │<──────────────────────┤  {data}                  │
```

### 3. Token 无效/过期流程

```
前端                    网关
 │                       │
 ├─GET /api/stocks/1────>│
 │ Authorization: Bearer invalid_token
 │                       │
 │                       ├─检查白名单(不在)
 │                       ├─提取Token
 │                       ├─验证Token (失败!)
 │                       │
 │<──────────────────────┤
 │  401 Unauthorized     │
 │                       │
 ├─清除本地Token         │
 ├─跳转到登录页          │
```

## 技术实现

### 1. 网关鉴权 (quant-gateway)

**AuthFilter.java** - 全局过滤器

```java
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // 白名单路径
    private static final List<String> WHITELIST_PATHS = Arrays.asList(
        "/api/auth/**",      // 认证接口
        "/actuator/**",  // 监控接口
        "/error",
        "/favicon.ico"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // 1. 白名单检查
        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        // 2. Token 验证
        String token = extractToken(exchange);
        if (!jwtTokenUtil.validateToken(token)) {
            return unauthorized(exchange);
        }

        // 3. 提取用户信息
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        String phone = jwtTokenUtil.getPhoneFromToken(token);

        // 4. 注入请求头
        ServerHttpRequest request = exchange.getRequest().mutate()
            .header("X-User-Id", String.valueOf(userId))
            .header("X-User-Phone", phone)
            .build();

        return chain.filter(exchange.mutate().request(request).build());
    }
}
```

### 2. JWT Token 工具 (quant-common)

**JwtTokenUtil.java**

```java
@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // 生成 Token
    public String generateToken(Long userId, String phone) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("phone", phone);

        return Jwts.builder()
            .claims(claims)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey())
            .compact();
    }

    // 验证 Token
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null && !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }

    // 提取用户 ID
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? (Long) claims.get("userId") : null;
    }
}
```

### 3. 认证服务 (quant-user)

**AuthService.java**

```java
@Service
public class AuthService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserJpaRepository userRepository;

    // 登录
    public LoginResponse login(LoginRequest request) {
        // 1. 验证手机号格式
        if (!PhoneValidator.isValid(request.getPhone())) {
            throw new BusinessException(ResultCode.INVALID_PHONE_FORMAT);
        }

        // 2. 验证验证码
        verifyCode(request.getPhone(), request.getCode());

        // 3. 查找或创建用户
        UserEntity user = userRepository.findByMobile(request.getPhone())
            .orElseGet(() -> registerNewUser(request.getPhone()));

        // 4. 生成 JWT Token
        String token = jwtTokenUtil.generateToken(user.getId(), user.getMobile());

        return LoginResponse.builder()
            .userId(user.getId())
            .token(token)
            .build();
    }
}
```

## 安全特性

### 1. JWT Token 安全

- **HMAC-SHA256 签名**：防止 Token 被篡改
- **有效期控制**：默认 24 小时，可配置
- **密钥保护**：通过环境变量配置，不硬编码
- **最小权限**：Token 只包含必要信息（userId, phone）

### 2. 网关安全

- **统一入口**：所有请求必须经过网关
- **白名单机制**：精确控制哪些路径无需鉴权
- **自动拒绝**：无效 Token 自动返回 401
- **用户信息传递**：通过请求头安全传递给下游服务

### 3. 验证码安全

- **有效期限制**：5 分钟过期
- **一次性使用**：验证后立即失效
- **数据库存储**：支持分布式部署

## 配置说明

### 网关配置 (application.yml)

```yaml
spring:
  cloud:
    gateway:
      routes:
        # 认证路由（白名单）
        - id: auth-service
          uri: lb://quant-user
          predicates:
            - Path=/api/auth/**

        # 受保护路由
        - id: user-service
          uri: lb://quant-user
          predicates:
            - Path=/api/user/**

        - id: market-service
          uri: lb://quant-market
          predicates:
            - Path=/api/stocks/**

# JWT 配置
jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION:86400000}
```

## 白名单路径

| 路径模式           | 说明 | 目标服务 |
|----------------|------|----------|
| `/api/auth/**` | 登录、发送验证码 | quant-user |
| `/actuator/**` | 健康检查、监控 | 所有服务 |
| `/error`       | 错误页面 | - |
| `/favicon.ico` | 网站图标 | - |

## 受保护路径

所有**不在白名单**中的路径都需要提供有效的 JWT Token：

| 路径模式               | 说明 | 目标服务 |
|--------------------|------|----------|
| `/api/user/**`     | 用户管理 | quant-user |
| `/api/stocks/**`   | 股票查询 | quant-market |
| `/api/trade/**`    | 交易管理 | quant-trade |
| `/api/risk/**`     | 风控管理 | quant-risk |
| `/api/strategy/**` | 策略管理 | quant-strategy |

## 下游服务获取用户信息

微服务可以通过两种方式获取当前用户信息：

### 方式1：从请求头获取

```java
@RestController
public class StockController {

    @GetMapping("/api/stocks")
    public Result<List<Stock>> getMyStocks(
        @RequestHeader("X-User-Id") Long userId,
        @RequestHeader("X-User-Phone") String phone
    ) {
        // 使用 userId 查询用户的股票
        return Result.success(stockService.getUserStocks(userId));
    }
}
```

### 方式2：使用 UserContext（需要拦截器）

如果需要在 Service 层访问用户信息，可以配置拦截器：

```java
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, ...) {
        String userId = request.getHeader("X-User-Id");
        String phone = request.getHeader("X-User-Phone");

        UserContext.setContext(new UserContext(
            Long.parseLong(userId), phone
        ));
        return true;
    }

    @Override
    public void afterCompletion(...) {
        UserContext.clear();
    }
}
```

然后在 Service 中：

```java
@Service
public class StockService {

    public List<Stock> getMyStocks() {
        Long userId = UserContext.getCurrentUserId();
        // 使用 userId
    }
}
```

## 扩展性

### 添加新的白名单路径

编辑 `quant-gateway/src/main/java/com/quant/gateway/filter/AuthFilter.java`:

```java
private static final List<String> WHITELIST_PATHS = Arrays.asList(
    "/api/auth/**",
    "/actuator/**",
    "/public/**",    // 新增公共接口
    "/error",
    "/favicon.ico"
);
```

### 添加新的路由

编辑 `quant-gateway/src/main/resources/application.yml`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: new-service
          uri: lb://quant-new-service
          predicates:
            - Path=/api/new/**
          filters:
            - StripPrefix=1
```

### 自定义 Token Claims

修改 `JwtTokenUtil.generateToken()` 方法，添加更多用户信息：

```java
public String generateToken(Long userId, String phone, String role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userId);
    claims.put("phone", phone);
    claims.put("role", role);  // 添加角色信息
    return generateToken(claims);
}
```

## 监控和日志

### 网关日志

AuthFilter 会记录以下日志：
- 请求路径和白名单检查结果
- Token 验证失败的警告
- 用户认证成功的调试信息

### 查看网关日志

```bash
# 查看网关日志
tail -f quant-gateway/logs/application.log

# 过滤鉴权相关日志
tail -f quant-gateway/logs/application.log | grep "Gateway filter"
```

## 性能优化

### JWT 验证性能

- JWT 验证是无状态的，不需要查询数据库
- 每个请求的验证时间 < 1ms
- 支持高并发场景

### 网关性能

- 使用 Spring Cloud Gateway 的响应式编程模型
- 异步非阻塞处理
- 支持数千 QPS

## 安全建议

1. **使用 HTTPS**：生产环境必须使用 HTTPS
2. **强密钥**：JWT 密钥至少 256 位
3. **定期轮换**：定期更换 JWT 密钥
4. **监控异常**：监控 401 错误率，发现异常登录
5. **限流保护**：在网关层配置限流规则
6. **IP 白名单**：对敏感接口配置 IP 白名单
