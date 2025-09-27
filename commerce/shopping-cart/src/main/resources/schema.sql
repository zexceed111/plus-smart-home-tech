CREATE TABLE IF NOT EXISTS carts
(
    cart_id   UUID PRIMARY KEY,
    user_name VARCHAR,
    state     VARCHAR
);

CREATE TABLE IF NOT EXISTS cart_products
(
    cart_id    UUID,
    product_id UUID,
    quantity   BIGINT,
    CONSTRAINT cart_products_pk PRIMARY KEY (cart_id, product_id),
    CONSTRAINT cart_products_cart_fk FOREIGN KEY (cart_id) REFERENCES carts (cart_id)
);