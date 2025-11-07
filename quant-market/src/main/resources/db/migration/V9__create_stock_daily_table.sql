-- 创建股票日线行情表
-- Tushare Pro API: https://tushare.pro/document/2?doc_id=27
-- 未复权行情数据，每交易日15点～16点入库

CREATE TABLE IF NOT EXISTS t_stock_daily (
    id BIGSERIAL PRIMARY KEY,
    stock_code VARCHAR(20) NOT NULL,
    trade_date DATE NOT NULL,
    open_price DECIMAL(10,2),
    high_price DECIMAL(10,2),
    low_price DECIMAL(10,2),
    close_price DECIMAL(10,2),
    pre_close DECIMAL(10,2),
    change_amount DECIMAL(10,2),
    pct_change DECIMAL(10,4),
    volume DECIMAL(20,2),
    amount DECIMAL(20,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_stock_daily_unique UNIQUE (stock_code, trade_date)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_stock_daily_code ON t_stock_daily(stock_code);
CREATE INDEX IF NOT EXISTS idx_stock_daily_date ON t_stock_daily(trade_date);
CREATE INDEX IF NOT EXISTS idx_stock_daily_code_date ON t_stock_daily(stock_code, trade_date DESC);

-- 表注释
COMMENT ON TABLE t_stock_daily IS '股票日线行情表（未复权）';

-- 列注释
COMMENT ON COLUMN t_stock_daily.id IS '主键ID';
COMMENT ON COLUMN t_stock_daily.stock_code IS '股票代码';
COMMENT ON COLUMN t_stock_daily.trade_date IS '交易日期';
COMMENT ON COLUMN t_stock_daily.open_price IS '开盘价';
COMMENT ON COLUMN t_stock_daily.high_price IS '最高价';
COMMENT ON COLUMN t_stock_daily.low_price IS '最低价';
COMMENT ON COLUMN t_stock_daily.close_price IS '收盘价';
COMMENT ON COLUMN t_stock_daily.pre_close IS '昨收价（除权价，前复权）';
COMMENT ON COLUMN t_stock_daily.change_amount IS '涨跌额';
COMMENT ON COLUMN t_stock_daily.pct_change IS '涨跌幅（基于除权后的昨收计算）';
COMMENT ON COLUMN t_stock_daily.volume IS '成交量（手）';
COMMENT ON COLUMN t_stock_daily.amount IS '成交额（千元）';
COMMENT ON COLUMN t_stock_daily.created_at IS '创建时间';
COMMENT ON COLUMN t_stock_daily.updated_at IS '更新时间';

-- 创建触发器函数用于自动更新 updated_at
CREATE OR REPLACE FUNCTION update_stock_daily_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 创建触发器
CREATE TRIGGER trigger_update_stock_daily_updated_at
    BEFORE UPDATE ON t_stock_daily
    FOR EACH ROW
    EXECUTE FUNCTION update_stock_daily_updated_at();
