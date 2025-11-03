-- Add Previous Close Prices
-- V4: Add previous trading day close prices

-- Add new fields
ALTER TABLE t_stock_basic ADD COLUMN prev_close_price DECIMAL(18,4);
ALTER TABLE t_stock_basic ADD COLUMN prev_prev_close_price DECIMAL(18,4);

-- Add comments
COMMENT ON COLUMN t_stock_basic.prev_close_price IS 'Previous trading day close price';
COMMENT ON COLUMN t_stock_basic.prev_prev_close_price IS 'Close price from 2 trading days ago';
