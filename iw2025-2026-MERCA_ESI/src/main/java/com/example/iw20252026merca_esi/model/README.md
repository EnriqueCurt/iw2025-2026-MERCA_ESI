# Modelos de Datos - MERCA ESI

Este paquete contiene todas las entidades JPA del sistema de gestión de restaurante.

## Entidades Creadas

### 1. **Producto**
- Representa los productos/platos del menú
- Atributos: id, nombre, descripción, precio, esOferta, estado, puntos
- Relaciones:
  - Pertenece a múltiples categorías (ManyToMany)
  - Puede estar en múltiples menús (ManyToMany)
  - Aparece en detalles de pedidos (OneToMany)

### 2. **Categoria**
- Clasifica los productos
- Atributos: id, nombre
- Relaciones:
  - Contiene múltiples productos (ManyToMany)

### 3. **Menu**
- Representa menús especiales que agrupan productos
- Atributos: id, nombre, descripción, precio, esOferta, estado, puntos
- Relaciones:
  - Contiene múltiples productos (ManyToMany)

### 4. **Cliente**
- Representa a los clientes del restaurante
- Atributos: id, nombre, email, teléfono, puntos
- Relaciones:
  - Realiza múltiples pedidos (OneToMany)

### 5. **Empleado**
- Representa a los empleados del restaurante
- Atributos: id, nombre, email, teléfono
- Relaciones:
  - Tiene múltiples roles (ManyToMany)
  - Gestiona múltiples pedidos (OneToMany)

### 6. **Rol**
- Define los roles del sistema (ej: administrador, cocinero, camarero)
- Atributos: id, nombre
- Relaciones:
  - Asignado a múltiples empleados (ManyToMany)

### 7. **Pedido**
- Representa un pedido realizado por un cliente
- Atributos: id, fecha, total, estado
- Relaciones:
  - Pertenece a un cliente (ManyToOne)
  - Asignado a un empleado (ManyToOne)
  - Contiene múltiples detalles (OneToMany)

### 8. **DetallePedido**
- Línea de detalle de un pedido (producto específico y cantidad)
- Clave compuesta: (idPedido, idProducto)
- Atributos: cantidad, precioUnitario
- Relaciones:
  - Pertenece a un pedido (ManyToOne)
  - Incluye un producto (ManyToOne)

### 9. **DetallePedidoId**
- Clase embebible que representa la clave compuesta de DetallePedido
- Atributos: idPedido, idProducto

## Configuración de Base de Datos

Asegúrate de configurar correctamente el archivo `application.properties` con:
- URL de conexión a MySQL
- Usuario y contraseña
- Configuración de Hibernate (ddl-auto, show-sql, etc.)

## Próximos Pasos

1. Crear repositorios (interfaces que extienden JpaRepository)
2. Implementar servicios de negocio
3. Crear controladores REST o vistas Vaadin
4. Añadir validaciones con Bean Validation (@NotNull, @Email, etc.)
5. Implementar Spring Security para autenticación y autorización
