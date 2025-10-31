# 股票交易所自动识别功能

## 功能说明

创建股票时，`exchange` 字段现在是**可选的**。如果不提供 `exchange`，系统会根据股票代码自动识别所属交易所。

## 股票代码识别规则

### 上海证券交易所 (SH)
- **60xxxx**: 主板股票（如：600000 浦发银行）
- **688xxx**: 科创板股票（如：688001）
- **900xxx**: B股（如：900901）

### 深圳证券交易所 (SZ)
- **00xxxx**: 主板股票（如：000001 平安银行）
- **002xxx**: 中小板（如：002001）
- **003xxx**: 主板（如：003001）
- **300xxx**: 创业板（如：300001）
- **200xxx**: B股（如：200001）

### 北京证券交易所 (BJ)
- **43xxxx**: 精选层（如：430001）
- **83xxxx**: 创新层（如：830001）
- **87xxxx**: 基础层（如：870001）

### 香港联合交易所 (HK)
- **5位数字**: 港股代码（如：00700 腾讯控股）

### 美国证券交易所 (US)
- **字母或混合代码**: 美股（如：AAPL, TSLA）

## API 使用示例

### 方式1：不提供 exchange（推荐）
系统自动识别交易所：

```json
POST /api/stocks

{
  "stockCode": "600000",
  "stockName": "浦发银行",
  "companyName": "上海浦东发展银行股份有限公司",
  "listingDate": "1999-11-10",
  "industry": "银行"
}
```
→ 自动识别为 **SH（上海证券交易所）**

```json
{
  "stockCode": "000001",
  "stockName": "平安银行",
  "companyName": "平安银行股份有限公司",
  "listingDate": "1991-04-03",
  "industry": "银行"
}
```
→ 自动识别为 **SZ（深圳证券交易所）**

```json
{
  "stockCode": "00700",
  "stockName": "腾讯控股",
  "companyName": "腾讯控股有限公司",
  "listingDate": "2004-06-16",
  "industry": "互联网"
}
```
→ 自动识别为 **HK（香港联合交易所）**

```json
{
  "stockCode": "AAPL",
  "stockName": "Apple Inc.",
  "companyName": "Apple Inc.",
  "listingDate": "1980-12-12",
  "industry": "科技"
}
```
→ 自动识别为 **US（美国证券交易所）**

### 方式2：显式指定 exchange
手动指定交易所（优先级高于自动识别）：

```json
POST /api/stocks

{
  "exchange": "SH",
  "stockCode": "600000",
  "stockName": "浦发银行",
  "companyName": "上海浦东发展银行股份有限公司",
  "listingDate": "1999-11-10",
  "industry": "银行"
}
```

### 方式3：使用带交易所前缀的股票代码
自动解析前缀：

```json
{
  "stockCode": "SH.600000",
  "stockName": "浦发银行",
  "companyName": "上海浦东发展银行股份有限公司",
  "listingDate": "1999-11-10",
  "industry": "银行"
}
```
→ 从代码前缀 `SH.` 自动识别为上海证券交易所

## 错误处理

如果股票代码无法识别，系统会抛出异常：

```json
{
  "stockCode": "999999",
  "stockName": "测试股票",
  ...
}
```

响应：
```json
{
  "error": "Unable to determine exchange for stock code: 999999"
}
```

## 技术实现

### Exchange 枚举新增方法

```java
Stock.Exchange.fromStockCode(String stockCode)
```

该方法会：
1. 检查是否有交易所前缀（如 `SH.600000`）
2. 根据数字规则匹配交易所
3. 字母代码默认为美股
4. 无法识别时抛出异常

### CreateStockRequest 自动转换

```java
public Stock toDomain() {
    Stock.Exchange stockExchange;
    if (exchange == null || exchange.trim().isEmpty()) {
        // 自动识别
        stockExchange = Stock.Exchange.fromStockCode(stockCode);
    } else {
        // 使用指定的
        stockExchange = Stock.Exchange.valueOf(exchange.toUpperCase());
    }
    ...
}
```

## 验证规则

- `stockCode`: 必填，不能为空
- `stockName`: 必填，不能为空
- `companyName`: 必填，不能为空
- `listingDate`: 必填，不能为空
- `exchange`: 可选，为空时自动识别
- `industry`: 可选
- `status`: 可选，默认为 LISTED

## 测试用例

推荐测试以下场景：

1. ✅ 上交所主板：600000
2. ✅ 上交所科创板：688001
3. ✅ 深交所主板：000001
4. ✅ 深交所创业板：300001
5. ✅ 北交所：430001
6. ✅ 港股：00700
7. ✅ 美股：AAPL
8. ✅ 带前缀：SH.600000
9. ❌ 无效代码：999999（应报错）
