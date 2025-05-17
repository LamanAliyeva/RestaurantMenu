CREATE TABLE IF NOT EXISTS category (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS dish (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10,2) NOT NULL,
    category_id BIGINT NOT NULL,
    CONSTRAINT fk_dish_category FOREIGN KEY(category_id) REFERENCES category(id)
    );

CREATE TABLE IF NOT EXISTS app_user (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS role (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    name VARCHAR(50) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS user_role (
                                         user_id BIGINT NOT NULL,
                                         role_id BIGINT NOT NULL,
                                         PRIMARY KEY(user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY(user_id) REFERENCES app_user(id),
    CONSTRAINT fk_user_role_role FOREIGN KEY(role_id) REFERENCES role(id)
    );

CREATE TABLE IF NOT EXISTS orders (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      status VARCHAR(20) NOT NULL,
    waiter_id BIGINT NOT NULL,
    CONSTRAINT fk_order_user FOREIGN KEY(waiter_id) REFERENCES app_user(id)
    );

CREATE TABLE IF NOT EXISTS order_item (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          order_id BIGINT NOT NULL,
                                          dish_id BIGINT NOT NULL,
                                          quantity INT NOT NULL,
                                          comment VARCHAR(256),
    price DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_item_order FOREIGN KEY(order_id) REFERENCES orders(id),
    CONSTRAINT fk_item_dish FOREIGN KEY(dish_id) REFERENCES dish(id)
    );
