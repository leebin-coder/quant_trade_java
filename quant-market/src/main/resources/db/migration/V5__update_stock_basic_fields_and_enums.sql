-- Update Stock Basic Table with New Fields and Enum Values
-- V5: Add new fields and update enum values for status, exchange, and is_hs

-- Add new fields
ALTER TABLE t_stock_basic ADD COLUMN IF NOT EXISTS area VARCHAR(100);
ALTER TABLE t_stock_basic ADD COLUMN IF NOT EXISTS fullname VARCHAR(200);
ALTER TABLE t_stock_basic ADD COLUMN IF NOT EXISTS enname VARCHAR(200);
ALTER TABLE t_stock_basic ADD COLUMN IF NOT EXISTS cnspell VARCHAR(50);
ALTER TABLE t_stock_basic ADD COLUMN IF NOT EXISTS market VARCHAR(50);
ALTER TABLE t_stock_basic ADD COLUMN IF NOT EXISTS curr_type VARCHAR(10);
ALTER TABLE t_stock_basic ADD COLUMN IF NOT EXISTS delist_date DATE;
ALTER TABLE t_stock_basic ADD COLUMN IF NOT EXISTS is_hs CHAR(1);
ALTER TABLE t_stock_basic ADD COLUMN IF NOT EXISTS act_name VARCHAR(200);
ALTER TABLE t_stock_basic ADD COLUMN IF NOT EXISTS act_ent_type VARCHAR(50);

-- Update existing enum values for exchange: SH -> SSE, SZ -> SZSE, HK -> HKEX
-- Note: BJ and US are removed from the enum
UPDATE t_stock_basic SET exchange = 'SSE' WHERE exchange = 'SH';
UPDATE t_stock_basic SET exchange = 'SZSE' WHERE exchange = 'SZ';
UPDATE t_stock_basic SET exchange = 'HKEX' WHERE exchange = 'HK';

-- Delete records with exchange BJ or US as they are no longer supported
DELETE FROM t_stock_basic WHERE exchange IN ('BJ', 'US');

-- Update existing enum values for status: LISTED -> L, DELISTED -> D, SUSPENDED -> P
UPDATE t_stock_basic SET status = 'L' WHERE status = 'LISTED';
UPDATE t_stock_basic SET status = 'D' WHERE status = 'DELISTED';
UPDATE t_stock_basic SET status = 'P' WHERE status = 'SUSPENDED';

-- Add comments for new fields
COMMENT ON COLUMN t_stock_basic.area IS 'Stock region/area';
COMMENT ON COLUMN t_stock_basic.fullname IS 'Full stock name';
COMMENT ON COLUMN t_stock_basic.enname IS 'English name';
COMMENT ON COLUMN t_stock_basic.cnspell IS 'Chinese pinyin abbreviation';
COMMENT ON COLUMN t_stock_basic.market IS 'Market type';
COMMENT ON COLUMN t_stock_basic.curr_type IS 'Trading currency';
COMMENT ON COLUMN t_stock_basic.delist_date IS 'Delisting date';
COMMENT ON COLUMN t_stock_basic.is_hs IS 'HuShen-Gang Tong status: N=No, H=Shanghai-HK, S=Shenzhen-HK';
COMMENT ON COLUMN t_stock_basic.act_name IS 'Actual controller name';
COMMENT ON COLUMN t_stock_basic.act_ent_type IS 'Actual controller entity type';
