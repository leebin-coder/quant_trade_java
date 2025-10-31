# 快速启动指南

## 前置要求

确保以下服务已安装并运行：

- ✅ JDK 8
- ✅ Maven 3.9+
- ✅ Docker & Docker Compose
- ✅ PostgreSQL 12+ (localhost:5432, 用户: libin, 密码: libin122351)

## 一键启动（5步）

### 1️⃣ 创建数据库

```bash
./init-database.sh
```

**预期输出**:
```
Database 'quant_trade' created successfully!
```

### 2️⃣ 启动基础设施

```bash
./start-infra.sh
```

**预期输出**:
```
Infrastructure services started:
  - PostgreSQL: localhost:5432 (External - using your existing database)
  - RabbitMQ: localhost:5672 (Management: http://localhost:15672, guest/guest)
  - Nacos: http://localhost:8848/nacos (nacos/nacos)
```

等待约15秒后，验证服务：
- Nacos: http://localhost:8848/nacos
- RabbitMQ: http://localhost:15672

### 3️⃣ 构建项目

```bash
cd quant-parent
mvn clean install -DskipTests
```

**预期输出**:
```
[INFO] BUILD SUCCESS
```

### 4️⃣ 启动服务

在 IDEA 中依次启动以下服务（或使用Maven命令）：

| 服务 | 主类 | 端口 | 说明 |
|------|------|------|------|
| 网关 | `GatewayApplication` | 8080 | API网关 |
| 用户 | `UserApplication` | 8081 | 用户服务 |
| 交易 | `TradeApplication` | 8082 | 交易服务 |
| 风控 | `RiskApplication` | 8083 | 风控服务 |
| 市场 | `MarketApplication` | 8084 | 市场数据服务 |
| 策略 | `StrategyApplication` | 8085 | 策略服务 |

**首次启动会自动创建数据库表（Flyway迁移）**

### 5️⃣ 验证服务

```bash
# 检查Nacos服务列表
open http://localhost:8848/nacos

# 测试用户服务健康检查
curl http://localhost:8081/actuator/health

# 通过网关访问
curl http://localhost:8080/api/user/actuator/health
```

## 查看数据库表

```bash
# 连接数据库
psql -h localhost -p 5432 -U libin -d quant_trade

# 查看所有表
\dt

# 示例输出：
#  public | flyway_schema_history       | table | libin
#  public | market_klines                | table | libin
#  public | market_symbols               | table | libin
#  public | market_tickers               | table | libin
#  public | order_executions             | table | libin
#  public | orders                       | table | libin
#  public | risk_alerts                  | table | libin
#  public | risk_rules                   | table | libin
#  public | strategy_backtest_results    | table | libin
#  public | strategy_executions          | table | libin
#  public | strategy_signals             | table | libin
#  public | trading_strategies           | table | libin
#  public | user_risk_limits             | table | libin
#  public | users                        | table | libin

# 查看迁移历史
SELECT * FROM flyway_schema_history;

# 退出
\q
```

## 停止服务

```bash
# 停止基础设施
./stop-infra.sh

# 停止业务服务（在IDE中直接停止）
```

## 故障排查

### ❌ 数据库连接失败

```bash
# 测试数据库连接
psql -h localhost -p 5432 -U libin -d quant_trade

# 如果失败，检查：
# 1. PostgreSQL是否运行
# 2. 用户名密码是否正确
# 3. 数据库是否已创建
```

### ❌ 服务未注册到Nacos

```bash
# 检查Nacos容器
docker ps | grep nacos

# 查看Nacos日志
docker logs quant-nacos

# 重启Nacos
docker restart quant-nacos
```

### ❌ Flyway迁移失败

查看服务启动日志，检查SQL语法错误。

重置数据库：
```bash
psql -h localhost -p 5432 -U libin -c "DROP DATABASE quant_trade;"
./init-database.sh
```

### ❌ 端口被占用

修改服务的 `application.yml` 中的端口号。

## 下一步

1. 查看 [SETUP.md](SETUP.md) 了解详细配置
2. 查看 [README.md](README.md) 了解完整文档
3. 开始开发业务代码

## 常用命令

```bash
# 查看所有Docker容器
docker ps

# 查看容器日志
docker logs quant-nacos
docker logs quant-rabbitmq

# 重启基础设施
./stop-infra.sh && ./start-infra.sh

# 清理Maven构建
cd quant-parent && mvn clean

# 构建特定模块
cd quant-user && mvn clean install -DskipTests
```

## 技术栈

- Java 8
- Spring Boot 2.3.12
- Spring Cloud Hoxton.SR12
- Spring Cloud Alibaba 2.2.9
- Nacos 2.1.0
- PostgreSQL 12
- Flyway 7.15.0
- RabbitMQ 3

---

**祝开发愉快！** 🚀

如有问题，请查看完整文档或提Issue。
