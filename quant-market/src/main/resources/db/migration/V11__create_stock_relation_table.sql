-- 创建股票关联表
-- 用于记录股票与其他实体的关联关系

CREATE TABLE IF NOT EXISTS t_stock_relation (
    id BIGSERIAL PRIMARY KEY,
    stock_code VARCHAR(20) NOT NULL,
    ref_id BIGINT NOT NULL,
    ref_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_stock_relation_stock_code ON t_stock_relation(stock_code);
CREATE INDEX IF NOT EXISTS idx_stock_relation_ref ON t_stock_relation(ref_id, ref_type);
CREATE INDEX IF NOT EXISTS idx_stock_relation_created_at ON t_stock_relation(created_at DESC);

-- 创建复合唯一索引，防止重复关联
CREATE UNIQUE INDEX IF NOT EXISTS uk_stock_relation_unique ON t_stock_relation(stock_code, ref_id, ref_type);

-- 表注释
COMMENT ON TABLE t_stock_relation IS '股票关联表，用于记录股票与其他实体的关联关系';

-- 列注释
COMMENT ON COLUMN t_stock_relation.id IS '主键ID';
COMMENT ON COLUMN t_stock_relation.stock_code IS '股票代码';
COMMENT ON COLUMN t_stock_relation.ref_id IS '关联实体ID';
COMMENT ON COLUMN t_stock_relation.ref_type IS '关联类型（STOCK-股票关联）';
COMMENT ON COLUMN t_stock_relation.created_at IS '创建时间';
COMMENT ON COLUMN t_stock_relation.updated_at IS '更新时间';

-- 创建触发器函数用于自动更新 updated_at
CREATE OR REPLACE FUNCTION update_stock_relation_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 创建触发器
CREATE TRIGGER trigger_update_stock_relation_updated_at
    BEFORE UPDATE ON t_stock_relation
    FOR EACH ROW
    EXECUTE FUNCTION update_stock_relation_updated_at();
