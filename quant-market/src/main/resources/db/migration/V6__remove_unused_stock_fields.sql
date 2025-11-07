-- Remove Unused Stock Fields
-- V6: Remove companyName and all price/market cap related fields

-- Drop price and market cap related columns
ALTER TABLE t_stock_basic DROP COLUMN IF EXISTS company_name;
ALTER TABLE t_stock_basic DROP COLUMN IF EXISTS latest_price;
ALTER TABLE t_stock_basic DROP COLUMN IF EXISTS prev_close_price;
ALTER TABLE t_stock_basic DROP COLUMN IF EXISTS prev_prev_close_price;
ALTER TABLE t_stock_basic DROP COLUMN IF EXISTS total_shares;
ALTER TABLE t_stock_basic DROP COLUMN IF EXISTS circulating_shares;
ALTER TABLE t_stock_basic DROP COLUMN IF EXISTS total_market_cap;
ALTER TABLE t_stock_basic DROP COLUMN IF EXISTS circulating_market_cap;
