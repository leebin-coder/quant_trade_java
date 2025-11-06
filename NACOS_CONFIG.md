# Nacos 配置说明

## 版本信息
- **Nacos 版本**: 3.1.0
- **数据库**: PostgreSQL (nacos_config)
- **鉴权**: 已启用

## 端口配置

| 端口 | 用途 | 访问方式 |
|------|------|---------|
| **8888** | 控制台 UI | http://localhost:8888/nacos |
| **8848** | 服务端口 (gRPC/HTTP) | 微服务注册发现 |
| **9848** | gRPC 附加端口 | 内部通信 |

### 为什么分离控制台端口？

Nacos 默认将控制台和服务端口都设置为 8848，但在我们的架构中：
- **API Gateway** 使用 **8080** 端口
- 如果 Nacos 控制台也使用常见端口可能造成混淆
- 通过设置 `nacos.console.port=8888`，控制台独立出来，避免端口冲突和混淆

## 鉴权配置

Nacos 3.x 强制启用鉴权，以下是关键配置：

```yaml
NACOS_AUTH_ENABLE: "true"
NACOS_AUTH_TOKEN: VGhpc0lzTXlDdXN0b21TZWNyZXRLZXkwMTIzNDU2Nzg5QUJDREVGNDg5Nzg5Nzg5Nzg5Nzg5Nzg5Nzg5
NACOS_AUTH_IDENTITY_KEY: nacos
NACOS_AUTH_IDENTITY_VALUE: nacos
```

### Token 说明
- **当前 Token**: `VGhpc0lzTXlDdXN0b21TZWNyZXRLZXkwMTIzNDU2Nzg5QUJDREVGNDg5Nzg5Nzg5Nzg5Nzg5Nzg5Nzg5`
- **生成方式**: Base64 编码
- **原始内容**: `ThisIsMyCustomSecretKey0123456789ABCDEF489789789789789789789789`
- **最小长度**: 32 字符

### 生成新的 Token

如果需要生成新的鉴权 token：

```bash
# 方式 1: 使用 OpenSSL (推荐)
openssl rand -base64 64

# 方式 2: 使用 Python
python3 -c "import base64, os; print(base64.b64encode(os.urandom(48)).decode())"

# 方式 3: 使用在线工具
echo -n "YourCustomSecretKey123456789ABCDEF123456789" | base64
```

⚠️ **生产环境必须修改默认 Token！**

## 数据库配置

Nacos 使用 PostgreSQL 存储配置和注册信息：

```yaml
SPRING_DATASOURCE_PLATFORM: postgresql
SPRING_DATASOURCE_URL: jdbc:postgresql://quant_postgres:5432/nacos_config
SPRING_DATASOURCE_USERNAME: libin
SPRING_DATASOURCE_PASSWORD: libin122351
```

### 数据库初始化

初始化脚本位于 `docker/init-db.sql`，包含：
- 数据库创建 (`nacos_config`)
- 所有必需的表结构
- 索引
- 默认管理员用户

## 访问 Nacos

### Web 控制台
```
URL: http://localhost:8888/nacos
用户名: nacos
密码: nacos
```

### API 调用示例

```bash
# 健康检查
curl http://localhost:8848/nacos/v1/console/health/readiness

# 注册服务 (需要鉴权)
curl -X POST 'http://localhost:8848/nacos/v1/ns/instance' \
  -d 'serviceName=example-service&ip=192.168.1.100&port=8080'

# 发现服务
curl 'http://localhost:8848/nacos/v1/ns/instance/list?serviceName=example-service'
```

## 微服务配置

所有微服务通过以下配置连接 Nacos：

```yaml
NACOS_SERVER: nacos:8848
NACOS_NAMESPACE: public
```

**注意**: 微服务使用 **8848** 端口进行服务注册和发现，**不使用** 8888 控制台端口。

## JVM 参数

当前 JVM 配置：
```yaml
JVM_XMS: 512m
JVM_XMX: 512m
JAVA_OPT_EXT: "-Dnacos.console.port=8888"
```

### 参数说明
- `JVM_XMS`: 初始堆内存 (512MB)
- `JVM_XMX`: 最大堆内存 (1GB)
- `JAVA_OPT_EXT`: 额外的 JVM 参数，这里设置控制台端口为 8888

## 故障排查

### 问题 1: 控制台无法访问 8888 端口

```bash
# 检查端口是否正确映射
docker-compose ps

# 检查 Nacos 日志
docker-compose logs nacos | grep "console.port"

# 验证容器内端口
docker exec quant-nacos netstat -tuln | grep 8888
```

### 问题 2: 鉴权失败

```bash
# 检查鉴权配置
docker exec quant-nacos env | grep NACOS_AUTH

# 查看日志中的鉴权信息
docker-compose logs nacos | grep -i auth
```

### 问题 3: 数据库连接失败

```bash
# 测试数据库连接
docker exec quant-nacos pg_isready -h quant_postgres -p 5432

# 查看数据库配置
docker exec quant-nacos env | grep SPRING_DATASOURCE
```

## 安全建议

1. **生产环境修改**:
   - 修改 Nacos 管理员密码
   - 生成新的 `NACOS_AUTH_TOKEN`
   - 限制控制台访问 IP

2. **防火墙配置**:
   ```bash
   # 只开放必要端口
   ufw allow 8848/tcp  # Nacos 服务端口
   ufw allow 8888/tcp  # Nacos 控制台（可选，仅管理员访问）
   ```

3. **使用 HTTPS**:
   - 生产环境建议在 Nginx/Traefik 后配置 SSL
   - 控制台和 API 都应使用 HTTPS

## 参考链接

- [Nacos 官方文档](https://nacos.io/docs/latest/)
- [Nacos 3.x 鉴权配置](https://nacos.io/docs/latest/guide/user/auth/)
- [Nacos PostgreSQL 支持](https://nacos.io/docs/latest/guide/admin/deployment/)
