CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(100) NOT NULL
);

INSERT INTO users (username, password, role) VALUES ('user', '{bcrypt}$2a$10$Jj8rkV7y0Vh/OatpBdoZa.rQpgptk0t6NRHYdcgxLtNizjjACkxPC', 'USER');
INSERT INTO users (username, password, role) VALUES ('admin', '{bcrypt}$2a$10$K9Hk02t0Kv2uGi5j3ak98uD.6rg0l0bZ5IlzV06G3fGA7aRsCa5tG', 'ADMIN');
