-- Market Service Database Schema
-- V1: Initialize market data tables

-- Market symbols table
CREATE TABLE IF NOT EXISTS market_symbols (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(32) NOT NULL UNIQUE,
    base_asset VARCHAR(16) NOT NULL,
    quote_asset VARCHAR(16) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    min_order_quantity DECIMAL(20, 8),
    max_order_quantity DECIMAL(20, 8),
    price_precision INTEGER,
    quantity_precision INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Market klines (candlestick data) table
CREATE TABLE IF NOT EXISTS market_klines (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(32) NOT NULL,
    interval VARCHAR(16) NOT NULL,
    open_time TIMESTAMP NOT NULL,
    close_time TIMESTAMP NOT NULL,
    open_price DECIMAL(20, 8) NOT NULL,
    high_price DECIMAL(20, 8) NOT NULL,
    low_price DECIMAL(20, 8) NOT NULL,
    close_price DECIMAL(20, 8) NOT NULL,
    volume DECIMAL(20, 8) NOT NULL,
    quote_volume DECIMAL(20, 8),
    trades_count INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(symbol, interval, open_time)
);

-- Market tickers table (real-time price data)
CREATE TABLE IF NOT EXISTS market_tickers (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(32) NOT NULL,
    last_price DECIMAL(20, 8) NOT NULL,
    bid_price DECIMAL(20, 8),
    ask_price DECIMAL(20, 8),
    volume_24h DECIMAL(20, 8),
    price_change_24h DECIMAL(20, 8),
    price_change_percent_24h DECIMAL(10, 4),
    high_24h DECIMAL(20, 8),
    low_24h DECIMAL(20, 8),
    timestamp TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_market_symbols_symbol ON market_symbols(symbol) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_market_symbols_status ON market_symbols(status);
CREATE INDEX IF NOT EXISTS idx_market_klines_symbol_interval ON market_klines(symbol, interval);
CREATE INDEX IF NOT EXISTS idx_market_klines_open_time ON market_klines(open_time);
CREATE INDEX IF NOT EXISTS idx_market_tickers_symbol ON market_tickers(symbol);
CREATE INDEX IF NOT EXISTS idx_market_tickers_timestamp ON market_tickers(timestamp);

-- Comments
COMMENT ON TABLE market_symbols IS 'Trading symbols configuration';
COMMENT ON TABLE market_klines IS 'Candlestick/Kline historical data';
COMMENT ON TABLE market_tickers IS 'Real-time market ticker data';
COMMENT ON COLUMN market_klines.interval IS 'Time interval: 1m, 5m, 15m, 1h, 4h, 1d, etc.';
