-- 修改股票日线表结构
-- 1. 扩大财务指标字段的精度
-- 2. 修改唯一约束，添加 adjust_flag 字段
-- 3. 更新索引

-- 删除旧的唯一约束
ALTER TABLE t_stock_daily DROP CONSTRAINT IF EXISTS uk_stock_daily_unique;

-- 修改字段精度
ALTER TABLE t_stock_daily ALTER COLUMN pe_ttm TYPE DECIMAL(18,6);
ALTER TABLE t_stock_daily ALTER COLUMN pb_mrq TYPE DECIMAL(18,6);
ALTER TABLE t_stock_daily ALTER COLUMN ps_ttm TYPE DECIMAL(18,6);
ALTER TABLE t_stock_daily ALTER COLUMN pcf_ncf_ttm TYPE DECIMAL(18,6);

-- 添加新的唯一约束（包含 adjust_flag）
ALTER TABLE t_stock_daily ADD CONSTRAINT uk_stock_daily_unique UNIQUE (stock_code, trade_date, adjust_flag);

-- 删除旧索引
DROP INDEX IF EXISTS idx_stock_daily_code_date;

-- 创建新索引（包含 adjust_flag）
CREATE INDEX IF NOT EXISTS idx_stock_daily_code_date_adjust ON t_stock_daily(stock_code, trade_date DESC, adjust_flag);
CREATE INDEX IF NOT EXISTS idx_stock_daily_adjust_flag ON t_stock_daily(adjust_flag);
