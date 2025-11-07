-- Normalize Stock Basic Table Column Names
-- V7: Rename columns to follow snake_case naming convention

-- Rename columns to snake_case
ALTER TABLE t_stock_basic RENAME COLUMN fullname TO full_name;
ALTER TABLE t_stock_basic RENAME COLUMN enname TO en_name;
ALTER TABLE t_stock_basic RENAME COLUMN cnspell TO cn_spell;

-- Update column comments
COMMENT ON COLUMN t_stock_basic.full_name IS 'Full stock name';
COMMENT ON COLUMN t_stock_basic.en_name IS 'English name';
COMMENT ON COLUMN t_stock_basic.cn_spell IS 'Chinese pinyin abbreviation';
