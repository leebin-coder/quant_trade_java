# 部署说明

## 架构说明

本项目使用 Docker 部署微服务，PostgreSQL 数据库单独部署（不在 Docker Compose 中）。

### 组件架构

```
┌─────────────────────────────────────────────┐
│         外部 PostgreSQL 数据库               │
│  - quant_trade (应用数据库)                  │
│  - nacos_config (Nacos 配置数据库)          │
└─────────────────────────────────────────────┘
                    ↑
                    │ (连接)
                    │
┌─────────────────────────────────────────────┐
│          Docker Compose 服务栈               │
├─────────────────────────────────────────────┤
│  • Nacos (8848)  - 服务注册中心              │
│  • Gateway (8080) - API 网关                │
│  • User Service (8081)                      │
│  • Trade Service (8082)                     │
│  • Risk Service (8083)                      │
│  • Market Service (8084)                    │
│  • Strategy Service (8085)                  │
└─────────────────────────────────────────────┘
```

## 前置条件

### 1. 安装 Docker

```bash
# 验证 Docker 安装
docker --version
docker-compose --version
```

### 2. 准备 PostgreSQL 数据库

PostgreSQL 需要单独部署（不在 Docker 中），可以是：
- 本地安装的 PostgreSQL
- 云数据库（如 RDS、CloudSQL）
- 远程服务器上的 PostgreSQL

## 部署步骤

### 步骤 1: 初始化 PostgreSQL 数据库

在 PostgreSQL 中执行初始化脚本：

```bash
# 连接到 PostgreSQL
psql -h <your-postgres-host> -U libin

# 创建数据库
CREATE DATABASE quant_trade;
CREATE DATABASE nacos_config;

# 初始化 Nacos 配置表
\c nacos_config;
\i docker/init-db.sql
```

或者直接执行：

```bash
psql -h <your-postgres-host> -U libin -f docker/init-db.sql
```

### 步骤 2: 配置环境变量

创建 `.env` 文件（从模板复制）：

```bash
cp .env.example .env
```

编辑 `.env` 文件，配置数据库连接：

```env
# PostgreSQL Database Configuration
DB_HOST=192.168.1.100          # PostgreSQL 服务器地址
DB_PORT=5432                    # PostgreSQL 端口
DB_NAME=quant_trade             # 应用数据库名
DB_USERNAME=libin               # 数据库用户名
DB_PASSWORD=libin122351         # 数据库密码

# Nacos Database Configuration
NACOS_DB_NAME=nacos_config      # Nacos 数据库名
```

**重要配置说明：**

| 场景 | DB_HOST 配置 |
|------|------------|
| 本地 PostgreSQL (macOS/Windows) | `host.docker.internal` |
| 本地 PostgreSQL (Linux) | `172.17.0.1` 或宿主机IP |
| 远程 PostgreSQL | 实际IP地址 (如 `192.168.1.100`) |
| 云数据库 | 云数据库地址 |

### 步骤 3: 启动服务

```bash
# 构建并启动所有服务
docker-compose up -d --build

# 查看启动日志
docker-compose logs -f

# 查看服务状态
docker-compose ps
```

### 步骤 4: 验证部署

```bash
# 检查 Nacos
curl http://localhost:8848/nacos/
# 访问 Nacos 控制台: http://localhost:8848/nacos (nacos/nacos)

# 检查 Gateway
curl http://localhost:8080/actuator/health

# 检查其他服务
curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8082/actuator/health  # Trade Service
curl http://localhost:8083/actuator/health  # Risk Service
curl http://localhost:8084/actuator/health  # Market Service
curl http://localhost:8085/actuator/health  # Strategy Service
```

## 常见部署场景

### 场景 1: 本地开发（MacOS/Windows）

```env
# .env
DB_HOST=host.docker.internal
DB_PORT=5432
DB_NAME=quant_trade
DB_USERNAME=libin
DB_PASSWORD=libin122351
NACOS_DB_NAME=nacos_config
```

```bash
docker-compose up -d --build
```

### 场景 2: 本地开发（Linux）

```env
# .env
DB_HOST=172.17.0.1              # Docker 默认网桥 IP
DB_PORT=5432
DB_NAME=quant_trade
DB_USERNAME=libin
DB_PASSWORD=libin122351
NACOS_DB_NAME=nacos_config
```

或者使用宿主机 IP：

```bash
# 获取宿主机 IP
ip addr show docker0
```

### 场景 3: 生产环境（远程数据库）

```env
# .env
DB_HOST=192.168.1.100           # PostgreSQL 服务器 IP
DB_PORT=5432
DB_NAME=quant_trade
DB_USERNAME=libin
DB_PASSWORD=libin122351
NACOS_DB_NAME=nacos_config
```

### 场景 4: 从 GitHub 克隆后部署

```bash
# 1. 克隆项目
git clone https://github.com/your-username/quant-trade-java.git
cd quant-trade-java

# 2. 初始化数据库（在 PostgreSQL 中）
psql -h <postgres-host> -U libin -f docker/init-db.sql

# 3. 配置环境变量
cp .env.example .env
vim .env  # 修改数据库连接信息

# 4. 启动服务
docker-compose up -d --build
```

## 服务管理

### 查看服务状态

```bash
# 查看所有服务
docker-compose ps

# 查看特定服务日志
docker-compose logs -f gateway
docker-compose logs -f nacos
docker-compose logs -f user-service

# 查看所有日志
docker-compose logs -f
```

### 重启服务

```bash
# 重启所有服务
docker-compose restart

# 重启特定服务
docker-compose restart gateway
docker-compose restart nacos
```

### 停止服务

```bash
# 停止所有服务
docker-compose down

# 停止并删除网络
docker-compose down --volumes
```

### 更新服务

```bash
# 拉取最新代码
git pull origin main

# 重新构建并启动
docker-compose up -d --build

# 或者只更新特定服务
docker-compose up -d --build gateway
```

## 故障排查

### 问题 1: 服务无法连接数据库

**现象:** 服务日志显示数据库连接失败

**排查步骤:**

```bash
# 1. 检查数据库是否可访问
ping <DB_HOST>
telnet <DB_HOST> 5432

# 2. 检查 PostgreSQL 配置
# 编辑 postgresql.conf
listen_addresses = '*'

# 编辑 pg_hba.conf，添加
host    all             all             0.0.0.0/0               md5

# 3. 重启 PostgreSQL
sudo systemctl restart postgresql

# 4. 验证容器网络
docker exec -it quant-nacos ping <DB_HOST>
```

### 问题 2: Nacos 启动失败

**现象:** Nacos 容器不断重启

**解决方案:**

```bash
# 1. 查看 Nacos 日志
docker-compose logs nacos

# 2. 检查 Nacos 数据库是否初始化
psql -h <DB_HOST> -U libin -d nacos_config -c "\dt"

# 3. 如果表不存在，重新初始化
psql -h <DB_HOST> -U libin -f docker/init-db.sql

# 4. 重启 Nacos
docker-compose restart nacos
```

### 问题 3: 端口冲突

**现象:** 服务启动失败，提示端口被占用

**解决方案:**

```bash
# 查看端口占用
lsof -i :8080
lsof -i :8848

# 停止占用端口的进程
kill -9 <PID>

# 或修改端口映射（编辑 docker-compose.yml）
ports:
  - "18080:8080"  # 将外部端口改为 18080
```

### 问题 4: 构建失败

**现象:** Maven 构建超时或失败

**解决方案:**

```bash
# 1. 配置 Maven 国内镜像（如果在中国）
# 在 Dockerfile 中添加 settings.xml

# 2. 增加 Docker 构建内存
# 编辑 ~/.docker/daemon.json
{
  "builder": {
    "gc": {
      "defaultKeepStorage": "20GB",
      "enabled": true
    }
  }
}

# 3. 清理并重新构建
docker-compose down
docker system prune -a
docker-compose up -d --build
```

### 问题 5: host.docker.internal 无法解析

**Linux 系统上的解决方案:**

```bash
# 方法 1: 使用 Docker 网桥 IP
echo "DB_HOST=172.17.0.1" >> .env

# 方法 2: 使用宿主机 IP
# 获取宿主机 IP
ip -4 addr show scope global | grep inet | awk '{print $2}' | cut -d'/' -f1

# 更新 .env
echo "DB_HOST=<your-host-ip>" >> .env

# 重启服务
docker-compose down
docker-compose up -d
```

## 数据备份

### 备份 PostgreSQL 数据

```bash
# 备份应用数据库
pg_dump -h <DB_HOST> -U libin quant_trade > backup_quant_trade.sql

# 备份 Nacos 配置数据库
pg_dump -h <DB_HOST> -U libin nacos_config > backup_nacos_config.sql

# 备份所有数据库
pg_dumpall -h <DB_HOST> -U libin > backup_all.sql
```

### 恢复数据

```bash
# 恢复应用数据库
psql -h <DB_HOST> -U libin quant_trade < backup_quant_trade.sql

# 恢复 Nacos 配置数据库
psql -h <DB_HOST> -U libin nacos_config < backup_nacos_config.sql
```

## 性能优化

### PostgreSQL 优化

```sql
-- 调整连接数（postgresql.conf）
max_connections = 200

-- 调整共享内存
shared_buffers = 256MB
effective_cache_size = 1GB

-- 调整工作内存
work_mem = 16MB
maintenance_work_mem = 128MB
```

### Docker 资源限制

编辑 `docker-compose.yml` 添加资源限制：

```yaml
services:
  gateway:
    # ...
    deploy:
      resources:
        limits:
          memory: 1G
          cpus: '1'
        reservations:
          memory: 512M
          cpus: '0.5'
```

## 监控

### 服务监控

```bash
# 实时查看容器资源使用
docker stats

# 查看服务健康状态
docker-compose ps
```

### 日志监控

```bash
# 查看所有服务日志
docker-compose logs -f --tail=100

# 查看特定服务日志
docker-compose logs -f gateway --tail=100

# 查看 Nacos 日志
docker-compose logs -f nacos --tail=100
```

### Nacos 监控

访问 Nacos 控制台: http://localhost:8848/nacos

- 用户名: `nacos`
- 密码: `nacos`

可以查看：
- 服务注册状态
- 配置管理
- 集群状态

## 安全建议

1. **修改默认密码**
   - PostgreSQL 密码
   - Nacos 控制台密码 (登录后在"权限控制-用户列表"中修改)

2. **配置防火墙**
   ```bash
   # 只允许必要的端口
   sudo ufw allow 8080/tcp   # Gateway
   sudo ufw allow 8848/tcp   # Nacos
   ```

3. **PostgreSQL 安全配置**
   ```conf
   # pg_hba.conf - 限制访问来源
   host    all    all    192.168.1.0/24    md5  # 只允许内网访问
   ```

4. **使用 HTTPS**
   - 生产环境建议在 Gateway 前配置 Nginx/Traefik + SSL

## 总结

✅ PostgreSQL 单独部署（外部数据库）
✅ Nacos 使用 PostgreSQL 存储配置（数据持久化）
✅ 所有微服务通过 Docker Compose 管理
✅ 支持从 GitHub 克隆后直接部署
✅ 自动构建（不需要本地 Maven）
✅ 健康检查和自动重启

**核心命令:**

```bash
# 初始化数据库
psql -h <host> -U libin -f docker/init-db.sql

# 配置环境
cp .env.example .env && vim .env

# 启动服务
docker-compose up -d --build

# 查看状态
docker-compose ps
```
