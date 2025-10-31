# 股票批量插入 API 文档

## 概述

高性能、非阻塞的股票批量插入接口，支持一次性插入大量股票数据。

## 接口地址

```
POST /api/stocks/batch
```

## 特性

✅ **异步处理** - 不阻塞请求，立即返回响应
✅ **高性能** - 使用批量插入提高数据库写入效率
✅ **自动识别交易所** - 根据股票代码自动判断所属交易所
✅ **智能去重** - 自动跳过已存在的股票，不会报错
✅ **错误处理** - 记录每条失败记录的详细信息
✅ **状态自动设置** - 股票状态默认为"上市"

## 性能指标

- **线程池配置**：核心5线程，最大10线程
- **队列容量**：100个待处理任务
- **最大批次**：单次最多10000条数据
- **处理速度**：约1000-5000条/秒（取决于数据库性能）

## 请求格式

### 请求头
```
Content-Type: application/json
```

### 请求体

**最简格式**（推荐）：
```json
[
  {
    "stockCode": "600000",
    "stockName": "浦发银行",
    "companyName": "上海浦东发展银行股份有限公司",
    "listingDate": "1999-11-10"
  },
  {
    "stockCode": "000001",
    "stockName": "平安银行",
    "companyName": "平安银行股份有限公司",
    "listingDate": "1991-04-03"
  }
]
```

**完整格式**（可选 industry 字段）：
```json
[
  {
    "stockCode": "600000",
    "stockName": "浦发银行",
    "companyName": "上海浦东发展银行股份有限公司",
    "listingDate": "1999-11-10",
    "industry": "银行"
  },
  {
    "stockCode": "688001",
    "stockName": "华兴源创",
    "companyName": "苏州华兴源创科技股份有限公司",
    "listingDate": "2019-07-22",
    "industry": "电子设备"
  },
  {
    "stockCode": "00700",
    "stockName": "腾讯控股",
    "companyName": "腾讯控股有限公司",
    "listingDate": "2004-06-16",
    "industry": "互联网"
  }
]
```

### 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| stockCode | String | ✅ 是 | 股票代码（自动识别交易所） |
| stockName | String | ✅ 是 | 股票简称 |
| companyName | String | ✅ 是 | 公司全称 |
| listingDate | Date | ✅ 是 | 上市日期（格式：YYYY-MM-DD） |
| industry | String | ❌ 否 | 所属行业（可以为空） |

**注意**：
- `exchange` 字段**不需要**提供，系统自动识别
- `status` 字段**不需要**提供，默认为"上市"（LISTED）

## 响应格式

### 成功响应（立即返回）

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "message": "Batch create task accepted",
    "totalItems": 1000,
    "status": "Processing",
    "note": "Task is running asynchronously. Check server logs for completion status."
  },
  "timestamp": 1698765432000
}
```

### 错误响应

**请求为空**：
```json
{
  "code": 400,
  "message": "Request list cannot be empty",
  "timestamp": 1698765432000
}
```

**批次过大**：
```json
{
  "code": 400,
  "message": "Batch size cannot exceed 10000 items",
  "timestamp": 1698765432000
}
```

## 服务端日志

任务完成后，服务端会输出详细的执行日志：

### 成功完成示例
```
[stock-batch-1] INFO  StockService - Starting batch create stocks: 1000 items
[stock-batch-1] INFO  StockService - Batch saved 950 stocks
[stock-batch-1] INFO  StockService - Batch create completed: total=1000, success=950, failed=20, skipped=30, time=2350ms
```

### 有错误时的日志
```
[stock-batch-1] WARN  StockService - Batch create errors:
  [Row 15: 999999 - Unable to determine exchange for stock code: 999999]
  [Row 42: - Stock code cannot be blank]

[stock-batch-1] INFO  StockService - Batch create skipped items:
  [Row 10: Stock already exists: SH.600000]
  [Row 25: Stock already exists: SZ.000001]
```

## 使用示例

### cURL 示例

```bash
curl -X POST http://localhost:8084/api/stocks/batch \
  -H "Content-Type: application/json" \
  -d '[
    {
      "stockCode": "600000",
      "stockName": "浦发银行",
      "companyName": "上海浦东发展银行股份有限公司",
      "listingDate": "1999-11-10",
      "industry": "银行"
    },
    {
      "stockCode": "000001",
      "stockName": "平安银行",
      "companyName": "平安银行股份有限公司",
      "listingDate": "1991-04-03",
      "industry": "银行"
    }
  ]'
```

### JavaScript/Axios 示例

```javascript
const stocks = [
  {
    stockCode: "600000",
    stockName: "浦发银行",
    companyName: "上海浦东发展银行股份有限公司",
    listingDate: "1999-11-10",
    industry: "银行"
  },
  {
    stockCode: "688001",
    stockName: "华兴源创",
    companyName: "苏州华兴源创科技股份有限公司",
    listingDate: "2019-07-22",
    industry: "电子设备"
  }
];

axios.post('http://localhost:8084/api/stocks/batch', stocks)
  .then(response => {
    console.log('Batch task accepted:', response.data);
  })
  .catch(error => {
    console.error('Error:', error.response.data);
  });
```

### Python/Requests 示例

```python
import requests
import json

stocks = [
    {
        "stockCode": "600000",
        "stockName": "浦发银行",
        "companyName": "上海浦东发展银行股份有限公司",
        "listingDate": "1999-11-10",
        "industry": "银行"
    },
    {
        "stockCode": "000001",
        "stockName": "平安银行",
        "companyName": "平安银行股份有限公司",
        "listingDate": "1991-04-03",
        "industry": "银行"
    }
]

response = requests.post(
    'http://localhost:8084/api/stocks/batch',
    headers={'Content-Type': 'application/json'},
    data=json.dumps(stocks)
)

print(response.json())
```

## 从 CSV 文件批量导入示例

### CSV 文件格式
```csv
stockCode,stockName,companyName,listingDate,industry
600000,浦发银行,上海浦东发展银行股份有限公司,1999-11-10,银行
000001,平安银行,平安银行股份有限公司,1991-04-03,银行
688001,华兴源创,苏州华兴源创科技股份有限公司,2019-07-22,电子设备
```

### Python 批量导入脚本

```python
import csv
import requests
import json

def batch_import_from_csv(csv_file, batch_size=1000):
    """
    从 CSV 文件批量导入股票数据

    Args:
        csv_file: CSV 文件路径
        batch_size: 每批次的数量（默认1000）
    """
    api_url = 'http://localhost:8084/api/stocks/batch'

    with open(csv_file, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        batch = []

        for row in reader:
            stock = {
                'stockCode': row['stockCode'],
                'stockName': row['stockName'],
                'companyName': row['companyName'],
                'listingDate': row['listingDate'],
                'industry': row.get('industry', '')  # 可选字段
            }
            batch.append(stock)

            # 达到批次大小，执行插入
            if len(batch) >= batch_size:
                response = requests.post(api_url, json=batch)
                print(f'Submitted batch: {len(batch)} items, Response: {response.json()}')
                batch = []

        # 处理剩余数据
        if batch:
            response = requests.post(api_url, json=batch)
            print(f'Submitted final batch: {len(batch)} items, Response: {response.json()}')

# 使用示例
batch_import_from_csv('stocks.csv', batch_size=500)
```

## 最佳实践

### 1. 批次大小建议
- **小批次（100-500条）**：适合实时导入，响应快
- **中批次（500-2000条）**：平衡性能和响应时间
- **大批次（2000-5000条）**：批量导入，效率最高

### 2. 错误处理
- 接口返回200即表示任务已接受
- 实际执行结果需查看服务端日志
- 建议在导入完成后，通过查询接口验证数据

### 3. 并发控制
- 线程池最大10个并发任务
- 如果队列满，会在调用线程中执行（提供背压）
- 建议控制并发请求数量，避免过载

### 4. 数据验证
- 股票代码必须符合规则，否则会被标记为错误
- 日期格式必须为 YYYY-MM-DD
- 重复的股票会自动跳过，不会报错

## 性能优化建议

1. **数据库索引**：确保 `(exchange, stock_code)` 有唯一索引
2. **批次大小**：建议每批1000-2000条，性能最优
3. **并发控制**：避免同时提交过多批次
4. **网络优化**：如果数据量大，考虑压缩请求体

## 监控和日志

### 关键日志位置
```
[stock-batch-*] - 批处理线程日志
StockService - 服务层日志
StockController - 控制器层日志
```

### 监控指标
- 每批处理耗时
- 成功/失败/跳过数量
- 线程池使用情况

## FAQ

**Q: 接口返回200，但数据没有插入？**
A: 接口返回200只表示任务已接受，实际结果需查看服务端日志。可能是数据验证失败或交易所识别失败。

**Q: 如何知道批量插入是否完成？**
A: 查看服务端日志，会有完整的执行摘要和错误详情。

**Q: 可以同时提交多个批次吗？**
A: 可以，但建议控制并发数，避免超过线程池容量（最大10个并发）。

**Q: 重复的股票会报错吗？**
A: 不会，系统会自动跳过已存在的股票，并在日志中记录。

**Q: industry 字段可以为空吗？**
A: 可以，industry 是可选字段，可以不传或传空值。

**Q: 如何提高导入速度？**
A: 1）增加批次大小（建议1000-2000条）；2）优化数据库连接池；3）使用SSD硬盘。
