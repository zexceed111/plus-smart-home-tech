CREATE TABLE IF NOT EXISTS cart (
    id          UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_name        varchar(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_products (
    cart_id          UUID NOT NULL,
    product_id       UUID NOT NULL,
    quantity         BIGINT,
    CONSTRAINT cart_products_pk PRIMARY KEY (cart_id, product_id),
    CONSTRAINT cart_products_cart_fk FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE
);