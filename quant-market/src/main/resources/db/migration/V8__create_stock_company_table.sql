-- 创建股票公司信息表
-- Tushare Pro API: https://tushare.pro/document/2?doc_id=112

CREATE TABLE IF NOT EXISTS t_stock_company (
    id BIGSERIAL PRIMARY KEY,
    stock_code VARCHAR(20) NOT NULL,
    com_name VARCHAR(200) NOT NULL,
    com_id VARCHAR(50) NOT NULL,
    exchange VARCHAR(20) NOT NULL,
    chairman VARCHAR(100),
    manager VARCHAR(100),
    secretary VARCHAR(100),
    reg_capital DECIMAL(20,4),
    setup_date DATE,
    province VARCHAR(50),
    city VARCHAR(50),
    introduction TEXT,
    website VARCHAR(200),
    email VARCHAR(100),
    office VARCHAR(500),
    employees INTEGER,
    main_business TEXT,
    business_scope TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_stock_code ON t_stock_company(stock_code);
CREATE INDEX IF NOT EXISTS idx_exchange ON t_stock_company(exchange);
CREATE INDEX IF NOT EXISTS idx_com_name ON t_stock_company(com_name);
CREATE INDEX IF NOT EXISTS idx_com_id ON t_stock_company(com_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_company_info ON t_stock_company(stock_code, com_name, com_id, chairman, exchange);

-- 表注释
COMMENT ON TABLE t_stock_company IS '股票公司信息表';

-- 列注释
COMMENT ON COLUMN t_stock_company.id IS '主键ID';
COMMENT ON COLUMN t_stock_company.stock_code IS '股票代码';
COMMENT ON COLUMN t_stock_company.com_name IS '公司全称';
COMMENT ON COLUMN t_stock_company.com_id IS '统一社会信用代码';
COMMENT ON COLUMN t_stock_company.exchange IS '交易所代码';
COMMENT ON COLUMN t_stock_company.chairman IS '法人代表';
COMMENT ON COLUMN t_stock_company.manager IS '总经理';
COMMENT ON COLUMN t_stock_company.secretary IS '董秘';
COMMENT ON COLUMN t_stock_company.reg_capital IS '注册资本(万元)';
COMMENT ON COLUMN t_stock_company.setup_date IS '注册日期';
COMMENT ON COLUMN t_stock_company.province IS '所在省份';
COMMENT ON COLUMN t_stock_company.city IS '所在城市';
COMMENT ON COLUMN t_stock_company.introduction IS '公司介绍';
COMMENT ON COLUMN t_stock_company.website IS '公司主页';
COMMENT ON COLUMN t_stock_company.email IS '电子邮件';
COMMENT ON COLUMN t_stock_company.office IS '办公室地址';
COMMENT ON COLUMN t_stock_company.employees IS '员工人数';
COMMENT ON COLUMN t_stock_company.main_business IS '主要业务及产品';
COMMENT ON COLUMN t_stock_company.business_scope IS '经营范围';
COMMENT ON COLUMN t_stock_company.created_at IS '创建时间';
COMMENT ON COLUMN t_stock_company.updated_at IS '更新时间';

-- 创建触发器函数用于自动更新 updated_at
CREATE OR REPLACE FUNCTION update_stock_company_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 创建触发器
CREATE TRIGGER trigger_update_stock_company_updated_at
    BEFORE UPDATE ON t_stock_company
    FOR EACH ROW
    EXECUTE FUNCTION update_stock_company_updated_at();
