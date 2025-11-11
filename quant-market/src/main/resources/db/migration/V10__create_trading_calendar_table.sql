-- 创建交易日历表
-- 记录所有交易日和非交易日信息

CREATE TABLE IF NOT EXISTS t_trading_calendar (
    id BIGSERIAL PRIMARY KEY,
    trade_date DATE NOT NULL,
    is_trading_day SMALLINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_trading_calendar_date UNIQUE (trade_date)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_trading_calendar_date ON t_trading_calendar(trade_date DESC);
CREATE INDEX IF NOT EXISTS idx_trading_calendar_trading_day ON t_trading_calendar(is_trading_day, trade_date DESC);

-- 表注释
COMMENT ON TABLE t_trading_calendar IS '交易日历表';

-- 列注释
COMMENT ON COLUMN t_trading_calendar.id IS '主键ID';
COMMENT ON COLUMN t_trading_calendar.trade_date IS '交易日期（格式：yyyy-MM-dd）';
COMMENT ON COLUMN t_trading_calendar.is_trading_day IS '是否交易日（1-是，0-否）';
COMMENT ON COLUMN t_trading_calendar.created_at IS '创建时间';
COMMENT ON COLUMN t_trading_calendar.updated_at IS '更新时间';

-- 创建触发器函数用于自动更新 updated_at
CREATE OR REPLACE FUNCTION update_trading_calendar_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 创建触发器
CREATE TRIGGER trigger_update_trading_calendar_updated_at
    BEFORE UPDATE ON t_trading_calendar
    FOR EACH ROW
    EXECUTE FUNCTION update_trading_calendar_updated_at();
