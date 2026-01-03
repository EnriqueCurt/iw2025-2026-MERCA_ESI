-- Primero asegurar que existen las categorías
INSERT INTO categorias (nombre) VALUES 
('Pizzas'),
('Pastas'),
('Bebidas'),
('Postres'),
('Entrantes'),
('Complementos')
ON DUPLICATE KEY UPDATE nombre=nombre;

-- Insertar 50 productos
INSERT INTO productos (nombre, descripcion, precio, es_oferta, estado, puntos) VALUES
-- Pizzas (10 productos)
('Pizza Margarita', 'Pizza clásica con tomate, mozzarella y albahaca fresca', 8.50, 0, 1, 0),
('Pizza Carbonara', 'Crema, bacon, champiñones y queso parmesano', 9.50, 0, 1, 0),
('Pizza Cuatro Quesos', 'Mozzarella, gorgonzola, parmesano y queso de cabra', 10.50, 0, 1, 0),
('Pizza Prosciutto', 'Tomate, mozzarella y jamón serrano', 10.00, 0, 1, 0),
('Pizza Barbacoa', 'Salsa BBQ, pollo, cebolla y bacon', 11.00, 1, 1, 0),
('Pizza Vegetariana', 'Verduras asadas, champiñones y aceitunas', 9.00, 0, 1, 0),
('Pizza Pepperoni', 'Tomate, mozzarella y pepperoni picante', 9.50, 0, 1, 0),
('Pizza Hawaiana', 'Tomate, mozzarella, jamón york y piña', 9.00, 0, 1, 0),
('Pizza Napolitana', 'Tomate, mozzarella, anchoas y alcaparras', 10.00, 0, 1, 0),
('Pizza Diavola', 'Tomate, mozzarella, salami picante y guindilla', 10.50, 0, 1, 0),

-- Pastas (10 productos)
('Spaghetti Carbonara', 'Pasta con bacon, huevo, parmesano y pimienta negra', 8.50, 0, 1, 0),
('Spaghetti Bolognesa', 'Pasta con salsa de carne tradicional italiana', 8.00, 0, 1, 0),
('Penne Arrabiata', 'Pasta con salsa de tomate picante', 7.50, 0, 1, 0),
('Lasaña', 'Capas de pasta con carne, bechamel y queso gratinado', 9.50, 0, 1, 0),
('Ravioli Ricotta', 'Raviolis rellenos de ricotta con salsa de mantequilla y salvia', 9.00, 0, 1, 0),
('Tortellini Panna', 'Tortellini con salsa de nata y jamón', 8.50, 0, 1, 0),
('Tagliatelle al Pesto', 'Pasta fresca con salsa pesto genovés', 8.00, 0, 1, 0),
('Fettuccine Alfredo', 'Pasta con salsa cremosa de parmesano', 8.50, 1, 1, 0),
('Macarrones Cuatro Quesos', 'Pasta con mezcla de quesos gratinados', 9.00, 0, 1, 0),
('Canelones de Espinacas', 'Canelones rellenos de espinacas y ricotta', 9.50, 0, 1, 0),

-- Bebidas (10 productos)
('Coca-Cola', 'Refresco de cola 330ml', 2.00, 0, 1, 0),
('Fanta Naranja', 'Refresco de naranja 330ml', 2.00, 0, 1, 0),
('Agua Mineral', 'Agua mineral natural 500ml', 1.50, 0, 1, 0),
('Cerveza Estrella', 'Cerveza rubia 330ml', 2.50, 0, 1, 0),
('Vino Tinto', 'Copa de vino tinto de la casa', 3.00, 0, 1, 0),
('Vino Blanco', 'Copa de vino blanco de la casa', 3.00, 0, 1, 0),
('Zumo Natural Naranja', 'Zumo de naranja recién exprimido', 3.50, 0, 1, 0),
('Limonada Casera', 'Limonada natural con hierbabuena', 2.50, 0, 1, 0),
('Nestea', 'Té frío de melocotón 330ml', 2.00, 0, 1, 0),
('Café Espresso', 'Café espresso italiano', 1.50, 0, 1, 0),

-- Postres (8 productos)
('Tiramisú', 'Postre italiano con café y mascarpone', 4.50, 0, 1, 0),
('Panna Cotta', 'Crema italiana con coulis de frutos rojos', 4.00, 0, 1, 0),
('Brownie Chocolate', 'Brownie de chocolate con helado de vainilla', 4.50, 1, 1, 0),
('Tarta de Queso', 'Tarta de queso estilo New York', 4.00, 0, 1, 0),
('Helado Artesanal', 'Tres bolas de helado a elegir', 3.50, 0, 1, 0),
('Cannoli Siciliano', 'Cannoli relleno de ricotta dulce', 3.50, 0, 1, 0),
('Profiteroles', 'Profiteroles con chocolate caliente', 4.50, 0, 1, 0),
('Flan Casero', 'Flan de huevo con caramelo', 3.00, 0, 1, 0),

-- Entrantes (8 productos)
('Ensalada César', 'Lechuga, pollo, parmesano y salsa césar', 6.50, 0, 1, 0),
('Ensalada Caprese', 'Tomate, mozzarella fresca y albahaca', 5.50, 0, 1, 0),
('Bruschetta', 'Pan tostado con tomate, ajo y albahaca', 4.50, 0, 1, 0),
('Carpaccio Ternera', 'Finas láminas de ternera con parmesano y rúcula', 8.00, 0, 1, 0),
('Calamares Romana', 'Calamares rebozados con alioli', 7.50, 0, 1, 0),
('Tabla Quesos', 'Selección de quesos italianos', 9.00, 0, 1, 0),
('Focaccia', 'Pan italiano con aceite de oliva y romero', 3.50, 0, 1, 0),
('Alitas de Pollo', 'Alitas de pollo con salsa BBQ o picante', 6.50, 1, 1, 0),

-- Complementos (4 productos)
('Pan de Ajo', 'Pan crujiente con mantequilla de ajo', 2.50, 0, 1, 0),
('Salsa Extra', 'Salsa adicional a elegir (BBQ, César, Picante)', 1.00, 0, 1, 0),
('Queso Extra', 'Porción adicional de mozzarella', 1.50, 0, 1, 0),
('Aceitunas Aliñadas', 'Bol de aceitunas con especias', 2.00, 0, 1, 0);

-- Asignar categorías a productos
-- Pizzas (productos 1-10 → categoría Pizzas id=1)
INSERT INTO producto_categoria (id_producto, id_categoria)
SELECT p.id_producto, c.id_categoria
FROM productos p
CROSS JOIN categorias c
WHERE c.nombre = 'Pizzas'
AND p.nombre IN (
    'Pizza Margarita', 'Pizza Carbonara', 'Pizza Cuatro Quesos', 'Pizza Prosciutto',
    'Pizza Barbacoa', 'Pizza Vegetariana', 'Pizza Pepperoni', 'Pizza Hawaiana',
    'Pizza Napolitana', 'Pizza Diavola'
);

-- Pastas (productos 11-20 → categoría Pastas id=2)
INSERT INTO producto_categoria (id_producto, id_categoria)
SELECT p.id_producto, c.id_categoria
FROM productos p
CROSS JOIN categorias c
WHERE c.nombre = 'Pastas'
AND p.nombre IN (
    'Spaghetti Carbonara', 'Spaghetti Bolognesa', 'Penne Arrabiata', 'Lasaña',
    'Ravioli Ricotta', 'Tortellini Panna', 'Tagliatelle al Pesto', 'Fettuccine Alfredo',
    'Macarrones Cuatro Quesos', 'Canelones de Espinacas'
);

-- Bebidas (productos 21-30 → categoría Bebidas id=3)
INSERT INTO producto_categoria (id_producto, id_categoria)
SELECT p.id_producto, c.id_categoria
FROM productos p
CROSS JOIN categorias c
WHERE c.nombre = 'Bebidas'
AND p.nombre IN (
    'Coca-Cola', 'Fanta Naranja', 'Agua Mineral', 'Cerveza Estrella',
    'Vino Tinto', 'Vino Blanco', 'Zumo Natural Naranja', 'Limonada Casera',
    'Nestea', 'Café Espresso'
);

-- Postres (productos 31-38 → categoría Postres id=4)
INSERT INTO producto_categoria (id_producto, id_categoria)
SELECT p.id_producto, c.id_categoria
FROM productos p
CROSS JOIN categorias c
WHERE c.nombre = 'Postres'
AND p.nombre IN (
    'Tiramisú', 'Panna Cotta', 'Brownie Chocolate', 'Tarta de Queso',
    'Helado Artesanal', 'Cannoli Siciliano', 'Profiteroles', 'Flan Casero'
);

-- Entrantes (productos 39-46 → categoría Entrantes id=5)
INSERT INTO producto_categoria (id_producto, id_categoria)
SELECT p.id_producto, c.id_categoria
FROM productos p
CROSS JOIN categorias c
WHERE c.nombre = 'Entrantes'
AND p.nombre IN (
    'Ensalada César', 'Ensalada Caprese', 'Bruschetta', 'Carpaccio Ternera',
    'Calamares Romana', 'Tabla Quesos', 'Focaccia', 'Alitas de Pollo'
);

-- Complementos (productos 47-50 → categoría Complementos id=6)
INSERT INTO producto_categoria (id_producto, id_categoria)
SELECT p.id_producto, c.id_categoria
FROM productos p
CROSS JOIN categorias c
WHERE c.nombre = 'Complementos'
AND p.nombre IN (
    'Pan de Ajo', 'Salsa Extra', 'Queso Extra', 'Aceitunas Aliñadas'
);