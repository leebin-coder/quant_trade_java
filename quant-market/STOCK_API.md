# Stock API 文档

## 概述

股票基本信息管理 API，提供完整的增删改查功能。

**Base URL**: `http://localhost:8084/api/stocks`

## 🔥 新功能

- ✅ **批量插入接口**：高性能异步批量插入，支持一次性插入上千条数据 → [详细文档](./BATCH_INSERT_API.md)
- ✅ **交易所自动识别**：根据股票代码自动判断所属交易所 → [详细说明](./AUTO_EXCHANGE_DETECTION.md)

## API 列表

### 1. 创建股票

**接口**: `POST /api/stocks`

**新特性**: `exchange` 字段现在是**可选的**！系统会根据股票代码自动识别交易所。

**请求体示例1（推荐）- 自动识别交易所**:
```json
{
  "stockCode": "600000",
  "stockName": "浦发银行",
  "companyName": "上海浦东发展银行股份有限公司",
  "listingDate": "1999-11-10",
  "industry": "银行"
}
```
> 系统会自动识别 `600000` 为上海证券交易所（SH）

**请求体示例2 - 手动指定交易所（含市场数据）**:
```json
{
  "exchange": "SH",
  "stockCode": "600000",
  "stockName": "浦发银行",
  "companyName": "上海浦东发展银行股份有限公司",
  "listingDate": "1999-11-10",
  "industry": "银行",
  "status": "LISTED",
  "latestPrice": 10.58,
  "prevClosePrice": 10.50,
  "prevPrevClosePrice": 10.45,
  "totalShares": 29352020012.00,
  "circulatingShares": 29352020012.00,
  "totalMarketCap": 310584291726.96,
  "circulatingMarketCap": 310584291726.96
}
```

**市场数据字段说明**（所有字段均为可选）:
- `latestPrice`: 最新股价
- `prevClosePrice`: 上一个交易日收盘价
- `prevPrevClosePrice`: 上上个交易日收盘价
- `totalShares`: 总股本
- `circulatingShares`: 流通股
- `totalMarketCap`: 总市值
- `circulatingMarketCap`: 流通市值

**自动识别规则**:
- SH（上交所）: 60xxxx, 688xxx（科创板）, 900xxx（B股）
- SZ（深交所）: 00xxxx, 002xxx, 003xxx, 300xxx（创业板）, 200xxx（B股）
- BJ（北交所）: 43xxxx, 83xxxx, 87xxxx
- HK（港股）: 5位数字（如：00700）
- US（美股）: 字母或其他格式（如：AAPL）

更多示例请查看 [AUTO_EXCHANGE_DETECTION.md](./AUTO_EXCHANGE_DETECTION.md)

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "exchange": "SH",
    "stockCode": "600000",
    "stockName": "浦发银行",
    "companyName": "上海浦东发展银行股份有限公司",
    "listingDate": "1999-11-10",
    "industry": "银行",
    "status": "LISTED",
    "latestPrice": 10.58,
    "prevClosePrice": 10.50,
    "prevPrevClosePrice": 10.45,
    "totalShares": 29352020012.00,
    "circulatingShares": 29352020012.00,
    "totalMarketCap": 310584291726.96,
    "circulatingMarketCap": 310584291726.96,
    "createdAt": "2025-10-31T16:00:00",
    "updatedAt": "2025-10-31T16:00:00"
  }
}
```

---

### 2. 更新股票

**接口**: `PUT /api/stocks/{id}`

**路径参数**:
- `id`: 股票ID

**请求体**（所有字段均为可选）:
```json
{
  "stockName": "浦发银行",
  "companyName": "上海浦东发展银行股份有限公司",
  "listingDate": "1999-11-10",
  "industry": "银行",
  "status": "LISTED",
  "latestPrice": 10.58,
  "prevClosePrice": 10.50,
  "prevPrevClosePrice": 10.45,
  "totalShares": 29352020012.00,
  "circulatingShares": 29352020012.00,
  "totalMarketCap": 310584291726.96,
  "circulatingMarketCap": 310584291726.96
}
```

**响应**: 同创建股票

---

### 3. 根据ID查询股票

**接口**: `GET /api/stocks/{id}`

**路径参数**:
- `id`: 股票ID

**响应**: 同创建股票

---

### 4. 根据交易所和代码查询

**接口**: `GET /api/stocks/{exchange}/{stockCode}`

**路径参数**:
- `exchange`: 交易所代码 (SH/SZ/BJ/HK/US)
- `stockCode`: 股票代码

**示例**: `GET /api/stocks/sh/600000`

**响应**: 同创建股票

---

### 5. 分页查询股票

**接口**: `GET /api/stocks`

**查询参数**:
- `page`: 页码，从0开始 (默认: 0)
- `size`: 每页大小 (默认: 20)
- `sortBy`: 排序字段 (默认: createdAt)
- `sortDir`: 排序方向 asc/desc (默认: desc)

**示例**: `GET /api/stocks?page=0&size=20&sortBy=createdAt&sortDir=desc`

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [...],
  "page": 0,
  "size": 20,
  "total": 100,
  "totalPages": 5
}
```

---

### 6. 查询所有股票

**接口**: `GET /api/stocks/all`

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [...]
}
```

---

### 7. 按交易所查询

**接口**: `GET /api/stocks/exchange/{exchange}`

**路径参数**:
- `exchange`: 交易所代码 (SH/SZ/BJ/HK/US)

**示例**: `GET /api/stocks/exchange/sh`

---

### 8. 按状态查询

**接口**: `GET /api/stocks/status/{status}`

**路径参数**:
- `status`: 股票状态 (LISTED/DELISTED/SUSPENDED)

**示例**: `GET /api/stocks/status/listed`

---

### 9. 按行业查询

**接口**: `GET /api/stocks/industry/{industry}`

**路径参数**:
- `industry`: 行业名称

**示例**: `GET /api/stocks/industry/银行`

---

### 10. 按股票名称搜索

**接口**: `GET /api/stocks/search/name`

**查询参数**:
- `keyword`: 搜索关键词

**示例**: `GET /api/stocks/search/name?keyword=银行`

---

### 11. 按公司名称搜索

**接口**: `GET /api/stocks/search/company`

**查询参数**:
- `keyword`: 搜索关键词

**示例**: `GET /api/stocks/search/company?keyword=浦东`

---

### 12. 删除股票

**接口**: `DELETE /api/stocks/{id}`

**路径参数**:
- `id`: 股票ID

**响应**:
```json
{
  "code": 200,
  "message": "success"
}
```

---

### 13. 股票统计

**接口**: `GET /api/stocks/stats`

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 5000,
    "sh": 2000,
    "sz": 2500,
    "bj": 300,
    "hk": 150,
    "us": 50
  }
}
```

---

## 枚举类型

### Exchange (交易所)
- `SH`: 上海证券交易所
- `SZ`: 深圳证券交易所
- `BJ`: 北京证券交易所
- `HK`: 香港联合交易所
- `US`: 美国证券交易所

### StockStatus (股票状态)
- `LISTED`: 上市
- `DELISTED`: 退市
- `SUSPENDED`: 停牌

---

## 数据字段说明

### 基础信息字段
| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| stockCode | String | 是 | 股票代码 |
| stockName | String | 是 | 股票简称 |
| companyName | String | 是 | 公司全称 |
| listingDate | Date | 是 | 上市日期 (格式: YYYY-MM-DD) |
| exchange | String | 否 | 交易所代码 (不填则自动识别) |
| industry | String | 否 | 行业分类 |
| status | String | 否 | 股票状态 (默认: LISTED) |

### 市场数据字段（所有字段均为可选）
| 字段名 | 类型 | 说明 | 数据库类型 |
|--------|------|------|------------|
| latestPrice | BigDecimal | 最新股价 | DECIMAL(18,4) |
| prevClosePrice | BigDecimal | 上一个交易日收盘价 | DECIMAL(18,4) |
| prevPrevClosePrice | BigDecimal | 上上个交易日收盘价 | DECIMAL(18,4) |
| totalShares | BigDecimal | 总股本 | DECIMAL(20,2) |
| circulatingShares | BigDecimal | 流通股 | DECIMAL(20,2) |
| totalMarketCap | BigDecimal | 总市值 | DECIMAL(18,2) |
| circulatingMarketCap | BigDecimal | 流通市值 | DECIMAL(18,2) |

### 系统字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 股票ID (系统自动生成) |
| createdAt | DateTime | 创建时间 (系统自动生成) |
| updatedAt | DateTime | 更新时间 (系统自动维护) |

---

## 错误响应

```json
{
  "code": 400,
  "message": "Stock already exists: SH.600000"
}
```

```json
{
  "code": 404,
  "message": "Stock not found: 123"
}
```

---

## 测试示例

### cURL 示例

**创建股票**:
```bash
curl -X POST http://localhost:8084/api/stocks \
  -H "Content-Type: application/json" \
  -d '{
    "exchange": "SH",
    "stockCode": "600000",
    "stockName": "浦发银行",
    "companyName": "上海浦东发展银行股份有限公司",
    "listingDate": "1999-11-10",
    "industry": "银行",
    "status": "LISTED"
  }'
```

**查询股票**:
```bash
curl http://localhost:8084/api/stocks/sh/600000
```

**分页查询**:
```bash
curl "http://localhost:8084/api/stocks?page=0&size=10"
```

**搜索**:
```bash
curl "http://localhost:8084/api/stocks/search/name?keyword=银行"
```

**删除股票**:
```bash
curl -X DELETE http://localhost:8084/api/stocks/1
```
