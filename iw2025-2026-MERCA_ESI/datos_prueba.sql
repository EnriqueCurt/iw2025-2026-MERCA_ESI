-- ========================================
-- SCRIPT SQL PARA CREAR DATOS DE PRUEBA
-- Base de Datos: MERCA ESI
-- NOTA: Asume que los roles y el usuario admin ya existen
-- ========================================

-- 3. CREAR CATEGORÍAS
-- ========================================
INSERT INTO categorias (nombre) VALUES
('Pizzas'),
('Pastas'),
('Bebidas'),
('Postres'),
('Entrantes');

-- 4. CREAR INGREDIENTES
-- ========================================
INSERT INTO ingredientes (nombre, descripcion, estado) VALUES
('Mozzarella', 'Queso mozzarella fresca', 1),
('Tomate', 'Salsa de tomate casera', 1),
('Albahaca', 'Hojas de albahaca fresca', 1),
('Pepperoni', 'Pepperoni premium', 1),
('Jamón', 'Jamón york', 1),
('Champiñones', 'Champiñones frescos', 1),
('Parmesano', 'Queso parmesano', 1),
('Gorgonzola', 'Queso gorgonzola', 1),
('Provolone', 'Queso provolone', 1),
('Aceitunas', 'Aceitunas negras', 1);

-- 5. CREAR PRODUCTOS
-- ========================================
INSERT INTO productos (nombre, descripcion, precio, es_oferta, estado, puntos) VALUES
('Pizza Margarita', 'Pizza clásica con tomate, mozzarella y albahaca', 9.50, 0, 1, 0),
('Pizza Pepperoni', 'Pizza con tomate, mozzarella y pepperoni', 10.50, 0, 1, 0),
('Pizza Cuatro Quesos', 'Mozzarella, gorgonzola, parmesano y provolone', 11.00, 1, 1, 0),
('Pizza Hawaiana', 'Pizza con jamón y piña', 10.00, 0, 1, 0),
('Pizza Vegetal', 'Pizza con verduras de temporada', 10.50, 0, 1, 0),
('Pasta Carbonara', 'Pasta con salsa carbonara tradicional', 8.50, 0, 1, 0),
('Pasta Boloñesa', 'Pasta con ragú de carne', 9.00, 0, 1, 0),
('Coca-Cola', 'Refresco de cola 33cl', 2.00, 0, 1, 0),
('Agua Mineral', 'Agua mineral 50cl', 1.50, 0, 1, 0),
('Cerveza', 'Cerveza nacional 33cl', 2.50, 0, 1, 0),
('Tiramisú', 'Postre italiano tradicional', 4.50, 0, 1, 0),
('Panna Cotta', 'Postre de nata con frutos rojos', 4.00, 0, 1, 0),
('Ensalada César', 'Ensalada con pollo y salsa césar', 6.50, 0, 1, 0),
('Alitas de Pollo', 'Alitas picantes con salsa BBQ', 7.00, 0, 1, 0);

-- 6. RELACIONAR PRODUCTOS CON CATEGORÍAS
-- ========================================
-- Pizzas
INSERT INTO producto_categoria (id_producto, id_categoria)
SELECT p.id_producto, c.id_categoria
FROM productos p, categorias c
WHERE p.nombre IN ('Pizza Margarita', 'Pizza Pepperoni', 'Pizza Cuatro Quesos', 'Pizza Hawaiana', 'Pizza Vegetal')
AND c.nombre = 'Pizzas';

-- Pastas
INSERT INTO producto_categoria (id_producto, id_categoria)
SELECT p.id_producto, c.id_categoria
FROM productos p, categorias c
WHERE p.nombre IN ('Pasta Carbonara', 'Pasta Boloñesa')
AND c.nombre = 'Pastas';

-- Bebidas
INSERT INTO producto_categoria (id_producto, id_categoria)
SELECT p.id_producto, c.id_categoria
FROM productos p, categorias c
WHERE p.nombre IN ('Coca-Cola', 'Agua Mineral', 'Cerveza')
AND c.nombre = 'Bebidas';

-- Postres
INSERT INTO producto_categoria (id_producto, id_categoria)
SELECT p.id_producto, c.id_categoria
FROM productos p, categorias c
WHERE p.nombre IN ('Tiramisú', 'Panna Cotta')
AND c.nombre = 'Postres';

-- Entrantes
INSERT INTO producto_categoria (id_producto, id_categoria)
SELECT p.id_producto, c.id_categoria
FROM productos p, categorias c
WHERE p.nombre IN ('Ensalada César', 'Alitas de Pollo')
AND c.nombre = 'Entrantes';

-- 7. RELACIONAR PRODUCTOS CON INGREDIENTES
-- ========================================
-- Pizza Margarita
INSERT INTO producto_ingrediente (id_producto, id_ingrediente)
SELECT p.id_producto, i.id_ingrediente
FROM productos p, ingredientes i
WHERE p.nombre = 'Pizza Margarita' 
AND i.nombre IN ('Tomate', 'Mozzarella', 'Albahaca');

-- Pizza Pepperoni
INSERT INTO producto_ingrediente (id_producto, id_ingrediente)
SELECT p.id_producto, i.id_ingrediente
FROM productos p, ingredientes i
WHERE p.nombre = 'Pizza Pepperoni' 
AND i.nombre IN ('Tomate', 'Mozzarella', 'Pepperoni');

-- Pizza Cuatro Quesos
INSERT INTO producto_ingrediente (id_producto, id_ingrediente)
SELECT p.id_producto, i.id_ingrediente
FROM productos p, ingredientes i
WHERE p.nombre = 'Pizza Cuatro Quesos' 
AND i.nombre IN ('Mozzarella', 'Gorgonzola', 'Parmesano', 'Provolone');

-- 8. CREAR MENÚS
-- ========================================
INSERT INTO menus (nombre, descripcion, precio, estado) VALUES
('Menú del Día', 'Pizza + Bebida + Postre', 14.50, 1),
('Menú Familiar', '2 Pizzas + 2 Bebidas + 2 Postres', 35.00, 1),
('Menú Romántico', 'Pizza Cuatro Quesos + 2 Cervezas + 2 Tiramisú', 25.00, 1),
('Menú Pasta', 'Pasta + Ensalada + Bebida', 12.50, 1),
('Menú Completo', 'Entrante + Pizza + Postre + Bebida', 18.00, 1);

-- 9. RELACIONAR MENÚS CON PRODUCTOS
-- ========================================
-- Menú del Día: Pizza Margarita + Coca-Cola + Tiramisú
INSERT INTO menu_producto (id_menu, id_producto)
SELECT m.id_menu, p.id_producto
FROM menus m, productos p
WHERE m.nombre = 'Menú del Día' 
AND p.nombre IN ('Pizza Margarita', 'Coca-Cola', 'Tiramisú');

-- Menú Familiar: 2x Pizza Pepperoni + 2x Coca-Cola + 2x Panna Cotta
INSERT INTO menu_producto (id_menu, id_producto)
SELECT m.id_menu, p.id_producto
FROM menus m, productos p
WHERE m.nombre = 'Menú Familiar' 
AND p.nombre IN ('Pizza Pepperoni', 'Coca-Cola', 'Panna Cotta');

-- Menú Romántico: Pizza Cuatro Quesos + 2x Cerveza + 2x Tiramisú
INSERT INTO menu_producto (id_menu, id_producto)
SELECT m.id_menu, p.id_producto
FROM menus m, productos p
WHERE m.nombre = 'Menú Romántico' 
AND p.nombre IN ('Pizza Cuatro Quesos', 'Cerveza', 'Tiramisú');

-- Menú Pasta: Pasta Carbonara + Ensalada César + Agua Mineral
INSERT INTO menu_producto (id_menu, id_producto)
SELECT m.id_menu, p.id_producto
FROM menus m, productos p
WHERE m.nombre = 'Menú Pasta' 
AND p.nombre IN ('Pasta Carbonara', 'Ensalada César', 'Agua Mineral');

-- Menú Completo: Alitas de Pollo + Pizza Hawaiana + Tiramisú + Coca-Cola
INSERT INTO menu_producto (id_menu, id_producto)
SELECT m.id_menu, p.id_producto
FROM menus m, productos p
WHERE m.nombre = 'Menú Completo' 
AND p.nombre IN ('Alitas de Pollo', 'Pizza Hawaiana', 'Tiramisú', 'Coca-Cola');

-- 10. CREAR CLIENTES
-- ========================================
-- Contraseña: "123456" encriptada con BCrypt
INSERT INTO clientes (nombre, username, contrasena, email, telefono, puntos) VALUES
('Juan Pérez', 'cliente1', '$2a$10$N9qo8uLOickgx2ZMRZoMy.bIL6ZX5gQ5x8yKwXPQjOJOJ8Zk4LqH2', 'juan.perez@mail.com', '666555444', 100),
('Ana Martínez', 'cliente2', '$2a$10$N9qo8uLOickgx2ZMRZoMy.bIL6ZX5gQ5x8yKwXPQjOJOJ8Zk4LqH2', 'ana.martinez@mail.com', '666555445', 50),
('Luis Rodríguez', 'cliente3', '$2a$10$N9qo8uLOickgx2ZMRZoMy.bIL6ZX5gQ5x8yKwXPQjOJOJ8Zk4LqH2', 'luis.rodriguez@mail.com', '666555446', 25);

-- 11. CREAR PEDIDOS
-- ========================================
-- Pedido 1: EN_COCINA (hace 15 minutos) - Incluye productos y menú
INSERT INTO pedidos (fecha, estado, total, a_domicilio, para_llevar, direccion, id_cliente, id_empleado, created_at, updated_at) 
SELECT 
    DATE_SUB(NOW(), INTERVAL 15 MINUTE),
    'EN_COCINA',
    36.50,
    0,
    0,
    NULL,
    c.id_cliente,
    NULL,
    NOW(),
    NOW()
FROM clientes c WHERE c.username = 'cliente1';
SET @pedido1_id = LAST_INSERT_ID();

-- Pedido 2: EN_COCINA - A domicilio (hace 10 minutos) - Solo menú familiar
INSERT INTO pedidos (fecha, estado, total, a_domicilio, para_llevar, direccion, id_cliente, id_empleado, created_at, updated_at) 
SELECT 
    DATE_SUB(NOW(), INTERVAL 10 MINUTE),
    'EN_COCINA',
    35.00,
    1,
    0,
    'Calle Mayor 123, 3ºB',
    c.id_cliente,
    NULL,
    NOW(),
    NOW()
FROM clientes c WHERE c.username = 'cliente2';
SET @pedido2_id = LAST_INSERT_ID();

-- Pedido 3: LISTO - Para llevar (hace 25 minutos) - Menú del Día
INSERT INTO pedidos (fecha, estado, total, a_domicilio, para_llevar, direccion, id_cliente, id_empleado, created_at, updated_at) 
SELECT 
    DATE_SUB(NOW(), INTERVAL 25 MINUTE),
    'LISTO',
    14.50,
    0,
    1,
    NULL,
    c.id_cliente,
    NULL,
    NOW(),
    NOW()
FROM clientes c WHERE c.username = 'cliente3';
SET @pedido3_id = LAST_INSERT_ID();

-- Pedido 4: PENDIENTE_PAGO (hace 5 minutos) - Mix de productos y menú
INSERT INTO pedidos (fecha, estado, total, a_domicilio, para_llevar, direccion, id_cliente, id_empleado, created_at, updated_at) 
SELECT 
    DATE_SUB(NOW(), INTERVAL 5 MINUTE),
    'PENDIENTE_PAGO',
    30.50,
    0,
    0,
    NULL,
    c.id_cliente,
    NULL,
    NOW(),
    NOW()
FROM clientes c WHERE c.username = 'cliente1';
SET @pedido4_id = LAST_INSERT_ID();

-- 12. CREAR DETALLES DE PEDIDOS (PRODUCTOS)
-- ========================================
-- Pedido 1: 1 Pizza Margarita con notas especiales
INSERT INTO detalle_pedido_producto (id_pedido, id_producto, cantidad, precio_unitario, notas)
SELECT 
    @pedido1_id,
    p.id_producto,
    1,
    p.precio,
    'Sin cebolla, extra de queso'
FROM productos p WHERE p.nombre = 'Pizza Margarita';

-- Pedido 2: Solo menú, sin productos individuales

-- Pedido 3: Solo menú del día

-- Pedido 4: 1 Pasta Carbonara
INSERT INTO detalle_pedido_producto (id_pedido, id_producto, cantidad, precio_unitario, notas)
SELECT 
    @pedido4_id,
    p.id_producto,
    1,
    p.precio,
    'Poco hecha'
FROM productos p WHERE p.nombre = 'Pasta Carbonara';

-- 13. CREAR DETALLES DE PEDIDOS (MENÚS)
-- ========================================
-- Pedido 1: 1x Menú del Día
INSERT INTO detalle_pedido_menu (id_pedido, id_menu, cantidad, precio_unitario)
SELECT 
    @pedido1_id,
    m.id_menu,
    1,
    m.precio
FROM menus m WHERE m.nombre = 'Menú del Día';

-- Pedido 2: 1x Menú Familiar
INSERT INTO detalle_pedido_menu (id_pedido, id_menu, cantidad, precio_unitario)
SELECT 
    @pedido2_id,
    m.id_menu,
    1,
    m.precio
FROM menus m WHERE m.nombre = 'Menú Familiar';

-- Pedido 3: 1x Menú del Día
INSERT INTO detalle_pedido_menu (id_pedido, id_menu, cantidad, precio_unitario)
SELECT 
    @pedido3_id,
    m.id_menu,
    1,
    m.precio
FROM menus m WHERE m.nombre = 'Menú del Día';

-- Pedido 4: 1x Menú Pasta
INSERT INTO detalle_pedido_menu (id_pedido, id_menu, cantidad, precio_unitario)
SELECT 
    @pedido4_id,
    m.id_menu,
    1,
    m.precio
FROM menus m WHERE m.nombre = 'Menú Pasta';

-- ========================================
-- RESUMEN DE DATOS CREADOS
-- ========================================
-- USUARIOS DE PRUEBA:
-- Empleados:
--   Propietario:  propietario / 123456
--   Manager:      manager / 123456
--   Cocina:       cocina / 123456
--   Repartidor:   repartidor / 123456
-- 
-- Clientes:
--   cliente1 / 123456
--   cliente2 / 123456
--   cliente3 / 123456
--
-- PEDIDOS CREADOS:
-- Pedido 1: EN_COCINA - 1 Pizza Margarita + 1 Menú del Día
-- Pedido 2: EN_COCINA (a domicilio) - 1 Menú Familiar
-- Pedido 3: LISTO (para llevar) - 1 Menú del Día
-- Pedido 4: PENDIENTE_PAGO - 1 Pasta Carbonara + 1 Menú Pasta
--
-- NOTA: Los roles y el usuario admin deben existir previamente
-- ========================================