-- Stock Base Table Rename and Enhancement
-- V3: Rename t_stock_base to t_stock_basic and add market data fields

-- Rename table
ALTER TABLE t_stock_base RENAME TO t_stock_basic;

-- Add new fields
ALTER TABLE t_stock_basic ADD COLUMN latest_price DECIMAL(18,4);
ALTER TABLE t_stock_basic ADD COLUMN total_shares DECIMAL(20,2);
ALTER TABLE t_stock_basic ADD COLUMN circulating_shares DECIMAL(20,2);
ALTER TABLE t_stock_basic ADD COLUMN total_market_cap DECIMAL(18,2);
ALTER TABLE t_stock_basic ADD COLUMN circulating_market_cap DECIMAL(18,2);

-- Rename indexes
ALTER INDEX idx_stock_base_exchange RENAME TO idx_stock_basic_exchange;
ALTER INDEX idx_stock_base_code RENAME TO idx_stock_basic_code;
ALTER INDEX idx_stock_base_status RENAME TO idx_stock_basic_status;

-- Update table comment
COMMENT ON TABLE t_stock_basic IS 'Stock basic information and market data table';
COMMENT ON COLUMN t_stock_basic.latest_price IS 'Latest stock price';
COMMENT ON COLUMN t_stock_basic.total_shares IS 'Total share capital';
COMMENT ON COLUMN t_stock_basic.circulating_shares IS 'Circulating shares';
COMMENT ON COLUMN t_stock_basic.total_market_cap IS 'Total market capitalization';
COMMENT ON COLUMN t_stock_basic.circulating_market_cap IS 'Circulating market capitalization';