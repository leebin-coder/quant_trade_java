-- Stock Base Table
-- V2: Create stock base table for stock master data

-- Drop table if exists (in case it was created with wrong schema)
DROP TABLE IF EXISTS t_stock_base;

-- Create stock base table
CREATE TABLE t_stock_base (
    id BIGSERIAL PRIMARY KEY,
    exchange VARCHAR(20) NOT NULL,
    stock_code VARCHAR(20) NOT NULL,
    stock_name VARCHAR(100) NOT NULL,
    company_name VARCHAR(200) NOT NULL,
    listing_date DATE NOT NULL,
    industry VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(exchange, stock_code)
);

-- Create indexes
CREATE INDEX idx_stock_base_exchange ON t_stock_base(exchange);
CREATE INDEX idx_stock_base_code ON t_stock_base(stock_code);
CREATE INDEX idx_stock_base_status ON t_stock_base(status);

-- Comments
COMMENT ON TABLE t_stock_base IS 'Stock master data table';
COMMENT ON COLUMN t_stock_base.exchange IS 'Stock exchange: SSE, SZSE, HKEX, etc.';
COMMENT ON COLUMN t_stock_base.stock_code IS 'Stock code/symbol';
COMMENT ON COLUMN t_stock_base.stock_name IS 'Stock short name';
COMMENT ON COLUMN t_stock_base.company_name IS 'Full company name';
COMMENT ON COLUMN t_stock_base.listing_date IS 'IPO/listing date';
COMMENT ON COLUMN t_stock_base.industry IS 'Industry classification';
COMMENT ON COLUMN t_stock_base.status IS 'Stock status: ACTIVE, SUSPENDED, DELISTED';
