CREATE DATABASE IF NOT EXISTS lojacarro_test;
USE lojacarro_test;

DROP TABLE IF EXISTS carro;

CREATE TABLE carro (
    id BIGINT NOT NULL AUTO_INCREMENT,
    modelo VARCHAR(255) NOT NULL,
    ano INT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO carro (id, modelo, ano) VALUES (1, 'Honda Civic', 2023);
INSERT INTO carro (id, modelo, ano) VALUES (2, 'Toyota Corolla', 2024);
INSERT INTO carro (id, modelo, ano) VALUES (3, 'Fiat Argo', 2022);