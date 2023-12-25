CREATE TABLE IF NOT EXISTS customer
(
    id      VARCHAR(10) PRIMARY KEY,
    name    VARCHAR(150) NOT NULL,
    address VARCHAR(400) NOT NULL
);

CREATE TABLE IF NOT EXISTS item
(
    code        VARCHAR(30) PRIMARY KEY,
    description VARCHAR(250) NOT NULL,
    qty         INT,
    unit_price  DECIMAL(8, 2)
);

CREATE TABLE IF NOT EXISTS "order"
(
    id          VARCHAR(10) PRIMARY KEY,
    date        DATE DEFAULT CURRENT_DATE NOT NULL,
    customer_id VARCHAR(10)               NOT NULL,
    CONSTRAINT fk_order FOREIGN KEY (customer_id) REFERENCES customer (id)
);

CREATE TABLE IF NOT EXISTS order_item
(
    order_id   VARCHAR(10),
    item_code  VARCHAR(30),
    CONSTRAINT pk_order_item PRIMARY KEY (order_id, item_code),
    qty        INT           NOT NULL,
    unit_price DECIMAL(8, 2) NOT NULL,
    CONSTRAINT fk_order_item_1 FOREIGN KEY (order_id) REFERENCES "order" (id),
    CONSTRAINT fk_order_item_2 FOREIGN KEY (item_code) REFERENCES item (code)
);