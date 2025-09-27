CREATE TABLE IF NOT EXISTS products
(
    product_id       UUID PRIMARY KEY,
    name             VARCHAR,
    description      VARCHAR,
    image_src        VARCHAR,
    quantity_state   VARCHAR,
    product_state    VARCHAR,
    product_category VARCHAR,
    price            DOUBLE PRECISION
);