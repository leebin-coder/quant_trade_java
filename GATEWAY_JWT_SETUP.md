# 网关 JWT 配置说明

## 技术背景

### 问题

网关使用 **Spring Cloud Gateway**（基于 WebFlux/Reactor），而 `quant-common` 模块依赖了 **spring-boot-starter-web**（基于 Servlet/Tomcat）。这两者不能共存：

```
Spring Cloud Gateway (Reactive/WebFlux)
    ❌ 不兼容
Spring Boot Web (Servlet/Tomcat)
```

如果直接在网关中依赖 `quant-common` 并使用 `JwtTokenUtil`，会导致：
- Bean 无法注入
- 启动冲突
- 类加载器问题

### 解决方案

在网关模块中创建独立的 `JwtConfig` 类，复制 JWT 验证逻辑，避免依赖 `quant-common` 中的 `JwtTokenUtil`。

## 实现架构

```
┌────────────────────────────────────────┐
│         quant-gateway                  │
│    (Spring Cloud Gateway/WebFlux)      │
│                                        │
│  ┌──────────────────────────────────┐ │
│  │  JwtConfig (独立实现)            │ │
│  │  - validateToken()               │ │
│  │  - getUserIdFromToken()          │ │
│  │  - getPhoneFromToken()           │ │
│  └──────────────────────────────────┘ │
│             ↑                          │
│             │                          │
│  ┌──────────────────────────────────┐ │
│  │  AuthFilter                      │ │
│  │  @Autowired JwtConfig            │ │
│  └──────────────────────────────────┘ │
└────────────────────────────────────────┘

┌────────────────────────────────────────┐
│         quant-user                     │
│    (Spring Boot Web/Servlet)           │
│                                        │
│  ┌──────────────────────────────────┐ │
│  │  JwtTokenUtil (quant-common)     │ │
│  │  - generateToken()               │ │
│  │  - validateToken()               │ │
│  └──────────────────────────────────┘ │
│             ↑                          │
│             │                          │
│  ┌──────────────────────────────────┐ │
│  │  AuthService                     │ │
│  │  @Autowired JwtTokenUtil         │ │
│  └──────────────────────────────────┘ │
└────────────────────────────────────────┘
```

## 代码实现

### 1. JwtConfig（网关专用）

位置：`quant-gateway/src/main/java/com/quant/gateway/config/JwtConfig.java`

```java
@Component
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public boolean validateToken(String token) {
        // JWT 验证逻辑
    }

    public Long getUserIdFromToken(String token) {
        // 提取 userId
    }

    public String getPhoneFromToken(String token) {
        // 提取 phone
    }
}
```

### 2. AuthFilter 使用

```java
@Component
public class AuthFilter implements GlobalFilter {

    @Autowired
    private JwtConfig jwtConfig;  // 使用网关的 JwtConfig

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 验证 Token
        if (!jwtConfig.validateToken(token)) {
            return unauthorized(exchange);
        }

        // 提取用户信息
        Long userId = jwtConfig.getUserIdFromToken(token);
        String phone = jwtConfig.getPhoneFromToken(token);

        // 注入请求头
        ServerHttpRequest request = exchange.getRequest().mutate()
            .header("X-User-Id", String.valueOf(userId))
            .header("X-User-Phone", phone)
            .build();

        return chain.filter(exchange.mutate().request(request).build());
    }
}
```

## 配置文件

### application.yml

```yaml
# JWT 配置（必须与 quant-user 一致）
jwt:
  secret: ${JWT_SECRET:quant-trade-secret-key-for-jwt-token-generation-must-be-at-least-256-bits}
  expiration: ${JWT_EXPIRATION:86400000}
```

**重要**：网关和用户服务的 JWT 配置必须完全一致，否则 Token 验证会失败。

## 为什么这样设计？

### 1. 技术隔离
- 网关使用 Reactive 技术栈（WebFlux）
- 微服务使用 Servlet 技术栈（Spring MVC）
- 两者不能混用依赖

### 2. 职责分离
- **quant-common/JwtTokenUtil**：负责生成和验证 Token（用于微服务）
- **quant-gateway/JwtConfig**：仅负责验证 Token（用于网关）

### 3. 避免依赖冲突
```
quant-gateway
  ├── spring-cloud-starter-gateway (WebFlux)
  └── ❌ 不依赖 spring-boot-starter-web

quant-user
  ├── spring-boot-starter-web (Servlet)
  └── quant-common (包含 JwtTokenUtil)
```

## Token 流转

### 1. 用户登录（quant-user）

```java
// AuthService.java
@Autowired
private JwtTokenUtil jwtTokenUtil;  // 来自 quant-common

public LoginResponse login(LoginRequest request) {
    // 验证码校验...

    // 生成 Token
    String token = jwtTokenUtil.generateToken(userId, phone);

    return LoginResponse.builder()
        .token(token)
        .build();
}
```

### 2. 网关验证（quant-gateway）

```java
// AuthFilter.java
@Autowired
private JwtConfig jwtConfig;  // 网关专用

public Mono<Void> filter(...) {
    // 验证 Token（使用相同的密钥和算法）
    boolean valid = jwtConfig.validateToken(token);

    if (valid) {
        // 提取用户信息并转发
        Long userId = jwtConfig.getUserIdFromToken(token);
        // ...
    }
}
```

## 一致性保证

### JWT 密钥一致性

两个模块必须使用相同的密钥：

**quant-gateway/application.yml**:
```yaml
jwt:
  secret: ${JWT_SECRET:default-secret-key}
  expiration: ${JWT_EXPIRATION:86400000}
```

**quant-user/application.yml**:
```yaml
jwt:
  secret: ${JWT_SECRET:default-secret-key}
  expiration: ${JWT_EXPIRATION:86400000}
```

### 环境变量配置

在生产环境中，通过环境变量统一配置：

```bash
export JWT_SECRET="your-production-secret-key-at-least-256-bits"
export JWT_EXPIRATION=86400000
```

## 代码同步

`JwtConfig` 和 `JwtTokenUtil` 的验证逻辑必须保持一致：

| 功能 | JwtTokenUtil (quant-common) | JwtConfig (quant-gateway) |
|------|------------------------------|---------------------------|
| 生成 Token | ✅ generateToken() | ❌ 不需要 |
| 验证 Token | ✅ validateToken() | ✅ validateToken() |
| 提取 userId | ✅ getUserIdFromToken() | ✅ getUserIdFromToken() |
| 提取 phone | ✅ getPhoneFromToken() | ✅ getPhoneFromToken() |

**关键算法**：
- 签名算法：HMAC-SHA256
- 库：jjwt 0.12.5
- 时间验证：exp claim

## 测试验证

### 1. 启动服务

```bash
# 启动网关
cd quant-gateway
mvn spring-boot:run

# 启动用户服务
cd quant-user
mvn spring-boot:run
```

### 2. 获取 Token

```bash
# 发送验证码
curl -X POST http://localhost:8080/auth/send-code \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000"}'

# 登录获取 Token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","code":"123456"}'
```

### 3. 验证网关鉴权

```bash
# 无 Token 访问（应返回 401）
curl -X GET http://localhost:8080/user/1

# 带 Token 访问（应返回 200）
curl -X GET http://localhost:8080/user/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 故障排查

### 问题1：Token 验证失败

**症状**：网关返回 401，但 Token 是有效的

**原因**：密钥不一致

**解决**：
```bash
# 检查网关配置
grep "jwt.secret" quant-gateway/src/main/resources/application.yml

# 检查用户服务配置
grep "jwt.secret" quant-user/src/main/resources/application.yml

# 确保两者一致
```

### 问题2：启动报错 Bean not found

**症状**：
```
Field jwtTokenUtil in AuthFilter required a bean of type
'com.quant.common.security.JwtTokenUtil' that could not be found.
```

**原因**：错误地依赖了 `JwtTokenUtil` 而不是 `JwtConfig`

**解决**：
```java
// ❌ 错误
@Autowired
private JwtTokenUtil jwtTokenUtil;

// ✅ 正确
@Autowired
private JwtConfig jwtConfig;
```

### 问题3：WebFlux 和 Servlet 冲突

**症状**：
```
Parameter 0 of method setServletContext in
org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryConfiguration
required a bean of type 'jakarta.servlet.ServletContext'
```

**原因**：网关依赖了 Servlet 相关的包

**解决**：
1. 检查 `quant-gateway/pom.xml`，确保没有依赖 `spring-boot-starter-web`
2. 只依赖 `spring-cloud-starter-gateway`

## 未来优化

### 1. 抽取共享接口

创建 `quant-jwt-api` 模块，定义 JWT 接口：

```java
public interface JwtValidator {
    boolean validateToken(String token);
    Long getUserIdFromToken(String token);
    String getPhoneFromToken(String token);
}
```

网关和用户服务分别实现。

### 2. 使用共享密钥服务

将 JWT 密钥存储在配置中心（如 Nacos），确保所有服务使用相同密钥。

### 3. Token 刷新机制

实现 Refresh Token，避免用户频繁重新登录。

## 总结

由于 Spring Cloud Gateway（WebFlux）和 Spring Boot Web（Servlet）的技术栈不兼容，我们采用了**代码复制**的方式，在网关中独立实现 JWT 验证逻辑。虽然有一定的代码重复，但这是当前技术架构下的最佳实践。

**核心原则**：
1. 网关和用户服务使用相同的 JWT 密钥和算法
2. JwtConfig 和 JwtTokenUtil 的验证逻辑保持一致
3. 避免在网关中引入 Servlet 依赖
