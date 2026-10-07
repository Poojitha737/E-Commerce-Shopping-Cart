CREATE DATABASE IF NOT EXISTS ecommerce_db;
USE ecommerce_db;

CREATE TABLE IF NOT EXISTS orders (
    order_id INT PRIMARY KEY,
    amount DOUBLE NOT NULL,
    order_date VARCHAR(100) NOT NULL
);
