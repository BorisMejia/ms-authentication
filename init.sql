CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50),
    birthday DATE,
    address VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100) NOT NULL,
    base_salary BIGINT,
    role_id INTEGER REFERENCES roles(id),
    password VARCHAR(255) NOT NULL,
    document VARCHAR(20) NOT NULL
);

-- Insertar roles
INSERT INTO roles (id, code, name) VALUES
    (4, 'ADMIN', 'Administrator'),
    (5, 'ADVISOR', 'Loan Advisor'),
    (6, 'CLIENT', 'Client');

-- Insertar usuario admin genérico
INSERT INTO usuarios (name, last_name, birthday, address, phone, email, base_salary, role_id, password, document)
VALUES (
    'Admin', 'User', '1990-01-01', 'N/A', '3000000000', 'admin@gmail.com', 0, 4,
    '$2a$12$C4iaDFHCRQSqm7jcM793TengeGKLzYfMLx5EmWc/AFe9pMNWq6pqK',
    '123456'
);
