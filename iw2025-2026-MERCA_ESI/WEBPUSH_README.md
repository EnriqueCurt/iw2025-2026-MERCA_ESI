# Sistema de Web Push - Guía de Correcciones Implementadas

## Problemas Solucionados

### 1. **Campos de Base de Datos Demasiado Cortos**
- **Problema**: Los campos `endpoint`, `p256dh` y `auth` tenían longitud limitada (2000, 512, 512 caracteres)
- **Solución**: Cambiados a `TEXT` (columnDefinition = "TEXT") para soportar cualquier longitud

### 2. **Formato Incorrecto de Datos del Frontend**
- **Problema**: El JavaScript enviaba las claves como campos planos (`p256dh`, `auth`) en lugar de dentro de un objeto `keys`
- **Solución**: Modificado el JavaScript en `MainView.java` para enviar:
  ```json
  {
    "endpoint": "...",
    "keys": {
      "p256dh": "...",
      "auth": "..."
    }
  }
  ```

### 3. **ClassCastException con BouncyCastle**
- **Problema**: `java.lang.ClassCastException: class sun.security.ec.ECPublicKeyImpl cannot be cast to class org.bouncycastle.jce.interfaces.ECPublicKey`
- **Causa**: Las claves VAPID se generaban con el proveedor por defecto de Java (SunEC) pero la librería web-push requiere claves de BouncyCastle
- **Solución**: Modificado `decodeRawUncompressedPublicKeyP256()` y `decodeRawPrivateKeyP256()` para usar explícitamente `BouncyCastleProvider`:
  ```java
  KeyFactory kf = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
  ```

### 4. **Falta de Logs de Depuración**
- **Problema**: Era imposible saber dónde fallaba el proceso
- **Solución**: Agregados logs detallados en:
  - `PushController`: logs de peticiones entrantes
  - `WebPushService.guardarSuscripcion()`: logs de validación y guardado
  - `WebPushService.enviarNotificacionATodos()`: logs de envío y estadísticas

### 5. **Suscripciones Inválidas No se Eliminaban**
- **Problema**: Si una suscripción fallaba al enviar, se quedaba en la BD
- **Solución**: Las suscripciones que fallan se eliminan automáticamente

### 6. **Manejo de Errores Insuficiente**
- **Problema**: Los errores se perdían silenciosamente
- **Solución**: Respuestas HTTP informativas y try-catch con logs detallados

## Archivos Modificados

1. **PushSubscription.java** - Entidad con campos TEXT
2. **WebPushService.java** - Servicio con logs y limpieza automática
3. **PushController.java** - Controlador con logs y respuestas mejoradas
4. **MainView.java** - JavaScript corregido con formato correcto de datos

## Migración de Base de Datos

Si ya tienes datos en la tabla `push_subscriptions`, ejecuta el script:
```bash
mysql -u [usuario] -p [base_de_datos] < migration_push_subscriptions.sql
```

O si usas H2 o la aplicación crea las tablas automáticamente:
1. Detén la aplicación
2. Elimina la tabla antigua: `DROP TABLE push_subscriptions;`
3. Reinicia la aplicación (Hibernate creará la tabla con la estructura correcta)

## Cómo Probar

### 1. Verificar el Registro de Suscripciones
```bash
# Inicia la aplicación
mvnw spring-boot:run

# Abre el navegador en https://localhost:8443 (o tu puerto)
# Abre la consola del navegador (F12)
# Deberías ver logs como:
#   "Iniciando proceso de suscripción push..."
#   "Service Worker registrado"
#   "Suscripción guardada exitosamente"

# En los logs del servidor deberías ver:
#   "Recibida petición de suscripción push"
#   "Suscripción guardada exitosamente con ID: X"
```

### 2. Verificar en Base de Datos
```sql
SELECT id, 
       SUBSTRING(endpoint, 1, 50) as endpoint_preview,
       LENGTH(endpoint) as endpoint_length,
       LENGTH(p256dh) as p256dh_length,
       LENGTH(auth) as auth_length
FROM push_subscriptions;
```

**Valores esperados**:
- `endpoint_length`: > 100 (usualmente 150-300)
- `p256dh_length`: 87-88 (base64 de 64 bytes)
- `auth_length`: 24 (base64 de 16 bytes)

### 3. Enviar Notificación de Prueba
1. Inicia sesión como empleado
2. Ve a "Crear Producto"
3. Crea un nuevo producto
4. Al guardar, se enviará una notificación push
5. Verifica los logs del servidor:
   ```
   Enviando notificación a X suscripciones
   Notificaciones enviadas: X exitosas, 0 fallidas
   ```

## Logs Importantes

### Logs de Depuración del Frontend (Consola del Navegador)
- `Iniciando proceso de suscripción push...` - Proceso iniciado
- `Permiso de notificaciones: granted` - Permisos OK
- `Service Worker registrado` - SW OK
- `Datos de suscripción: {...}` - Datos a enviar
- `Respuesta del servidor: 200` - Guardado exitoso
- `Suscripción guardada exitosamente` - Proceso completo

### Logs de Depuración del Backend (Consola del Servidor)
- `Recibida petición de suscripción push` - Petición recibida
- `Endpoint: ...` - Endpoint recibido
- `Keys p256dh: presente` - Clave presente
- `Keys auth: presente` - Clave presente
- `Intentando guardar suscripción push...` - Guardando
- `Suscripción guardada exitosamente con ID: X` - Guardado OK

### Errores Comunes

#### Error: "Suscripción inválida: faltan endpoint/keys.p256dh/keys.auth"
- **Causa**: El formato del JSON no es correcto
- **Solución**: Verifica que MainView.java tenga el código actualizado con el objeto `keys`

#### Error: "Data too long for column 'endpoint'"
- **Causa**: La tabla aún tiene VARCHAR en lugar de TEXT
- **Solución**: Ejecuta el script de migración o elimina y recrea la tabla

#### Error: "ClassCastException: ECPublicKeyImpl cannot be cast to ECPublicKey"
- **Causa**: Las claves VAPID se generan con el proveedor incorrecto (SunEC vs BouncyCastle)
- **Solución**: Ya corregido en `WebPushService` - las claves ahora se generan con `BouncyCastleProvider.PROVIDER_NAME`

#### Error: "Cannot resolve table 'push_subscriptions'" (Warning)
- **Causa**: La tabla aún no existe en la BD
- **Solución**: Inicia la aplicación, Hibernate la creará automáticamente

## Arquitectura del Sistema

```
Frontend (MainView.java)
    ↓
[JavaScript registra Service Worker]
    ↓
[Solicita suscripción push del navegador]
    ↓
[Envía POST /api/push/subscribe con endpoint + keys]
    ↓
Backend (PushController)
    ↓
[Valida formato JSON]
    ↓
[Extrae endpoint, p256dh, auth]
    ↓
WebPushService
    ↓
[Busca suscripción existente por endpoint]
    ↓
[Guarda o actualiza en BD]
    ↓
PushSubscriptionRepository (JPA)
    ↓
Base de Datos (push_subscriptions)
```

## Envío de Notificaciones

```
CrearProductoView (o cualquier punto)
    ↓
[Llama webPushService.enviarNotificacionATodos()]
    ↓
WebPushService
    ↓
[Obtiene todas las suscripciones de BD]
    ↓
[Para cada suscripción:]
    ├─ Crea objeto Subscription
    ├─ Crea Notification con payload JSON
    ├─ Envía con pushService.send()
    └─ Si falla: elimina suscripción inválida
    ↓
[Log de estadísticas: X exitosas, Y fallidas]
```

## Configuración VAPID

Las claves VAPID están en `application.properties`:
```properties
webpush.vapid.public-key=BPBDhfJW56VVyf-MVZJfhfHvSnzaFBYN3HkOmj2zhu_YfJFH8ytnhBipLthBIhSrNoySd17msinm2GNXBuXiug8
webpush.vapid.private-key=H9Bht4PnhibiM4lWHoKEo8I0M7-pffSCgu1z-kmpyec
webpush.vapid.subject=mailto:enrique@localhost
```

**IMPORTANTE**: La clave pública en `MainView.java` debe coincidir con `webpush.vapid.public-key`

## Soporte de Navegadores

- ✅ Chrome/Edge 42+
- ✅ Firefox 44+
- ✅ Safari 16+
- ✅ Opera 39+
- ❌ Internet Explorer (no soporta Service Workers)

## Troubleshooting

### Problema: No aparece la solicitud de permisos
1. Verifica que estés en HTTPS (o localhost)
2. Verifica que no hayas bloqueado previamente las notificaciones
3. Resetea permisos: chrome://settings/content/notifications

### Problema: La suscripción no se guarda en BD
1. Activa logs de Hibernate: `spring.jpa.show-sql=true`
2. Verifica los logs del `PushController` y `WebPushService`
3. Verifica que la tabla existe: `SHOW TABLES LIKE 'push_%';`
4. Verifica la estructura: `DESCRIBE push_subscriptions;`

### Problema: Las notificaciones no se reciben
1. Verifica que el Service Worker esté activo: chrome://serviceworker-internals/
2. Verifica que la suscripción esté en BD
3. Verifica los logs al enviar notificación
4. Prueba con: `await registration.pushManager.getSubscription()` en la consola

## Próximas Mejoras (Opcionales)

- [ ] Asociar suscripciones a usuarios específicos
- [ ] Permitir desuscripción
- [ ] Notificaciones personalizadas por rol
- [ ] Panel de administración de suscripciones
- [ ] Estadísticas de notificaciones enviadas/leídas
- [ ] Soporte para notificaciones con imágenes y acciones

