CREATE TABLE IF NOT EXISTS products (
    product_id       UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name             varchar(200) NOT NULL,
    description      varchar(2000) NOT NULL,
    image_src        varchar(1000),
    quantity_state   varchar(20) NOT NULL,
    product_state    varchar(20) NOT NULL,
    rating           DOUBLE PRECISION,
    category         varchar(20),
    price            DOUBLE PRECISION
);