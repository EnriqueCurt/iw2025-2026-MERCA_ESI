-- Primero verificar que existe el rol EMPLEADO (debería tener id_rol = 2)
-- Si no existe, crearlo primero:
-- INSERT INTO roles (nombre) VALUES ('EMPLEADO');

-- Insertar 30 empleados
INSERT INTO empleados (nombre, username, contrasena, email, telefono) VALUES
('Juan García López', 'jgarcia', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'jgarcia@mercaesi.com', '600111111'),
('María Rodríguez Pérez', 'mrodriguez', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'mrodriguez@mercaesi.com', '600222222'),
('Carlos Martínez Sánchez', 'cmartinez', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'cmartinez@mercaesi.com', '600333333'),
('Ana López Fernández', 'alopez', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'alopez@mercaesi.com', '600444444'),
('Pedro Gómez Ruiz', 'pgomez', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'pgomez@mercaesi.com', '600555555'),
('Laura Hernández Díaz', 'lhernandez', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'lhernandez@mercaesi.com', '600666666'),
('David Jiménez Moreno', 'djimenez', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'djimenez@mercaesi.com', '600777777'),
('Carmen Álvarez Muñoz', 'calvarez', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'calvarez@mercaesi.com', '600888888'),
('Miguel Romero Torres', 'mromero', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'mromero@mercaesi.com', '600999999'),
('Isabel Navarro Gil', 'inavarro', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'inavarro@mercaesi.com', '601111111'),
('Francisco Ruiz Castro', 'fruiz', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'fruiz@mercaesi.com', '601222222'),
('Rosa Serrano Ortega', 'rserrano', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'rserrano@mercaesi.com', '601333333'),
('Antonio Molina Delgado', 'amolina', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'amolina@mercaesi.com', '601444444'),
('Beatriz Morales Ramírez', 'bmorales', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'bmorales@mercaesi.com', '601555555'),
('José Domínguez Vázquez', 'jdominguez', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'jdominguez@mercaesi.com', '601666666'),
('Teresa Santos Ramos', 'tsantos', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'tsantos@mercaesi.com', '601777777'),
('Manuel Castro Prieto', 'mcastro', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'mcastro@mercaesi.com', '601888888'),
('Pilar Iglesias Méndez', 'piglesias', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'piglesias@mercaesi.com', '601999999'),
('Javier Ortiz Cruz', 'jortiz', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'jortiz@mercaesi.com', '602111111'),
('Dolores Rubio Herrera', 'drubio', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'drubio@mercaesi.com', '602222222'),
('Raúl Blanco Medina', 'rblanco', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'rblanco@mercaesi.com', '602333333'),
('Mercedes Pascual Ibáñez', 'mpascual', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'mpascual@mercaesi.com', '602444444'),
('Enrique Guerrero Peña', 'eguerrero', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'eguerrero@mercaesi.com', '602555555'),
('Lucía Fuentes Aguilar', 'lfuentes', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'lfuentes@mercaesi.com', '602666666'),
('Roberto Núñez León', 'rnunez', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'rnunez@mercaesi.com', '602777777'),
('Cristina Vargas Marín', 'cvargas', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'cvargas@mercaesi.com', '602888888'),
('Fernando Soto Campos', 'fsoto', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'fsoto@mercaesi.com', '602999999'),
('Silvia Cortés Garrido', 'scortes', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'scortes@mercaesi.com', '603111111'),
('Alberto Moya Santana', 'amoya', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'amoya@mercaesi.com', '603222222'),
('Montserrat Parra Carmona', 'mparra', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'mparra@mercaesi.com', '603333333');

-- Asignar el rol EMPLEADO a todos los empleados recién insertados
-- Asumiendo que el rol EMPLEADO tiene id_rol = 2 y los empleados se insertan desde id 1 en adelante
INSERT INTO empleado_rol (id_empleado, id_rol)
SELECT e.id_empleado, 2
FROM empleados e
WHERE e.username IN (
    'jgarcia', 'mrodriguez', 'cmartinez', 'alopez', 'pgomez',
    'lhernandez', 'djimenez', 'calvarez', 'mromero', 'inavarro',
    'fruiz', 'rserrano', 'amolina', 'bmorales', 'jdominguez',
    'tsantos', 'mcastro', 'piglesias', 'jortiz', 'drubio',
    'rblanco', 'mpascual', 'eguerrero', 'lfuentes', 'rnunez',
    'cvargas', 'fsoto', 'scortes', 'amoya', 'mparra'
);