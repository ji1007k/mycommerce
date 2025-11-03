CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    phone_number CHAR(11),
    provider varchar(50),      -- 'NAVER', 'KAKAO', 'GOOGLE' 등
    provider_id varchar(200),   -- provider가 준 사용자 고유 ID
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    status VARCHAR(20)
);

comment on table users IS '사용자정보';
comment on column users.id IS 'ID';
comment on column users.name IS '이름';
comment on column users.email IS '이메일';
comment on column users.phone_number IS '연락처';
COMMENT ON COLUMN users.provider IS '소셜로그인 제공자(NAVER, KAKAO, GOOGLE 등)';
COMMENT ON COLUMN users.provider_id IS '소셜로그인 ID';
comment on column users.created_at IS '생성일시';
comment on column users.updated_at IS '수정일시';
comment on column users.status IS '계정상태';

CREATE INDEX IF NOT EXISTS idx_users_email on users(email);


create table if not exists orders (
    id bigserial primary key,
    user_id bigint references users(id),
    total_price DECIMAL(15, 0) default 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    status varchar(20)
);

COMMENT ON TABLE orders IS '주문정보';
COMMENT ON COLUMN orders.id IS '주문ID';
COMMENT ON COLUMN orders.user_id IS '사용자ID';
COMMENT ON COLUMN orders.total_price IS '총 주문금액';
COMMENT ON COLUMN orders.created_at IS '생성일시';
COMMENT ON COLUMN orders.updated_at IS '수정일시';
COMMENT ON COLUMN orders.status IS '주문상태';

CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);


create table if not exists products (
    id bigserial primary key,
    name varchar(200),
    description TEXT,
    price DECIMAL(10, 0) default 0,
    stock INTEGER default 0,
    status varchar(20),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0 NOT NULL
);

COMMENT ON TABLE products IS '상품정보';
COMMENT ON COLUMN products.id IS '상품ID';
COMMENT ON COLUMN products.name IS '상품명';
COMMENT ON COLUMN products.description IS '상품설명';
COMMENT ON COLUMN products.price IS '판매가격';
COMMENT ON COLUMN products.stock IS '재고수량';
COMMENT ON COLUMN products.status IS '판매상태';
COMMENT ON COLUMN products.created_at IS '생성일시';
COMMENT ON COLUMN products.updated_at IS '수정일시';
COMMENT ON COLUMN products.version IS '버전(낙관적락)';


create table if not exists order_items (
    id bigserial primary key,
    order_id bigint references orders(id),
    product_id bigint references products(id),
    price DECIMAL(10, 0) default 0,
    quantity INTEGER default 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

COMMENT ON TABLE order_items IS '주문상품정보';
COMMENT ON COLUMN order_items.id IS '주문상품ID';
COMMENT ON COLUMN order_items.order_id IS '주문ID';
COMMENT ON COLUMN order_items.product_id IS '상품ID';
COMMENT ON COLUMN order_items.price IS '주문당시가격';
COMMENT ON COLUMN order_items.quantity IS '주문수량';
COMMENT ON COLUMN order_items.created_at IS '생성일시';
COMMENT ON COLUMN order_items.updated_at IS '수정일시';

CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);