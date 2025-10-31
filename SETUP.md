# 项目配置和启动指南

## 数据库配置

本项目使用外部 PostgreSQL 数据库：

- **Host**: localhost
- **Port**: 5432
- **User**: libin
- **Password**: libin122351
- **Database**: quant_trade

## 数据库表结构管理

本项目使用 **Flyway** 进行数据库迁移管理，所有表结构会在服务启动时自动创建。

### 迁移脚本位置

各服务的数据库迁移脚本位于：

```
quant-user/src/main/resources/db/migration/
  └── V1__init_user_tables.sql

quant-trade/src/main/resources/db/migration/
  └── V1__init_trade_tables.sql

quant-risk/src/main/resources/db/migration/
  └── V1__init_risk_tables.sql

quant-market/src/main/resources/db/migration/
  └── V1__init_market_tables.sql

quant-strategy/src/main/resources/db/migration/
  └── V1__init_strategy_tables.sql
```

### 表结构说明

#### 用户服务表
- `users` - 用户账户表

#### 交易服务表
- `orders` - 交易订单表
- `order_executions` - 订单执行记录表

#### 风控服务表
- `risk_rules` - 风控规则配置表
- `risk_alerts` - 风险告警表
- `user_risk_limits` - 用户风险限额表

#### 市场数据服务表
- `market_symbols` - 交易对配置表
- `market_klines` - K线历史数据表
- `market_tickers` - 实时行情数据表

#### 策略服务表
- `trading_strategies` - 交易策略表
- `strategy_executions` - 策略执行历史表
- `strategy_signals` - 策略信号表
- `strategy_backtest_results` - 回测结果表

## 启动步骤

### 步骤 1: 创建数据库

```bash
# 运行数据库初始化脚本
./init-database.sh
```

或者手动创建：

```bash
psql -h localhost -p 5432 -U libin -c "CREATE DATABASE quant_trade;"
```

### 步骤 2: 启动基础设施服务

```bash
# 启动 RabbitMQ 和 Nacos
./start-infra.sh
```

等待服务启动完成（约15秒），然后可以访问：

- **RabbitMQ 管理界面**: http://localhost:15672 (guest/guest)
- **Nacos 控制台**: http://localhost:8848/nacos (nacos/nacos)

### 步骤 3: 构建项目

```bash
cd quant-parent
mvn clean install -DskipTests
```

### 步骤 4: 运行服务

#### 方式 A: 在 IDE 中运行（推荐开发环境）

在 IDEA 或其他 IDE 中依次运行以下主类：

1. `com.quant.gateway.GatewayApplication` (端口 8080)
2. `com.quant.user.UserApplication` (端口 8081)
3. `com.quant.trade.TradeApplication` (端口 8082)
4. `com.quant.risk.RiskApplication` (端口 8083)
5. `com.quant.market.MarketApplication` (端口 8084)
6. `com.quant.strategy.StrategyApplication` (端口 8085)

**首次启动时，Flyway 会自动执行数据库迁移脚本，创建所有表结构。**

#### 方式 B: 使用 Maven 命令运行

```bash
# 在不同终端窗口中运行
cd quant-gateway && mvn spring-boot:run
cd quant-user && mvn spring-boot:run
cd quant-trade && mvn spring-boot:run
cd quant-risk && mvn spring-boot:run
cd quant-market && mvn spring-boot:run
cd quant-strategy && mvn spring-boot:run
```

### 步骤 5: 验证服务

```bash
# 检查 Nacos 服务注册
open http://localhost:8848/nacos

# 检查用户服务健康状态
curl http://localhost:8081/actuator/health

# 通过网关访问用户服务
curl http://localhost:8080/api/user/actuator/health
```

## 查看数据库表

启动任意一个服务后，Flyway 会自动创建对应的表：

```bash
# 连接到数据库
psql -h localhost -p 5432 -U libin -d quant_trade

# 查看所有表
\dt

# 查看 Flyway 迁移历史
SELECT * FROM flyway_schema_history;

# 查看用户表结构
\d users

# 退出
\q
```

## 环境变量配置

配置文件位于 `.env`：

```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=quant_trade
DB_USERNAME=libin
DB_PASSWORD=libin122351

RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

NACOS_SERVER=localhost:8848
NACOS_NAMESPACE=public
```

## 常见问题

### 1. 数据库连接失败

确保 PostgreSQL 正在运行，并且连接信息正确：

```bash
# 测试连接
psql -h localhost -p 5432 -U libin -d quant_trade
```

### 2. Flyway 迁移失败

查看服务启动日志，检查 SQL 脚本是否有语法错误。

如需重置数据库：

```bash
# 删除数据库
psql -h localhost -p 5432 -U libin -c "DROP DATABASE quant_trade;"

# 重新创建
./init-database.sh
```

### 3. 端口被占用

修改各服务的 `application.yml` 中的 `server.port` 配置。

### 4. 服务未注册到 Nacos

- 确认 Nacos 已启动：`docker ps | grep nacos`
- 检查服务日志中的 Nacos 连接信息
- 访问 Nacos 控制台查看服务列表

## 停止服务

```bash
# 停止基础设施服务
./stop-infra.sh

# 停止业务服务（如果在 IDE 中运行，直接停止即可）
```

## 数据库迁移管理

### 添加新的迁移脚本

创建新的迁移文件：

```
V2__add_user_profile_table.sql
V3__add_order_index.sql
```

文件命名规范：`V{version}__{description}.sql`

- Version: 递增的版本号
- Description: 下划线分隔的描述

### 查看迁移历史

```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

### 手动执行迁移（如有需要）

```bash
cd quant-user
mvn flyway:migrate
```

## 生产环境建议

1. 修改 `ddl-auto` 为 `validate`（已配置）
2. 关闭 SQL 日志输出
3. 使用专用数据库用户
4. 配置数据库备份策略
5. 使用连接池监控
