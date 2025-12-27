-- Script de migración para corregir la tabla push_subscriptions
-- Ejecutar este script si ya hay datos en la base de datos

-- 1. Crear tabla temporal con la estructura correcta
CREATE TABLE IF NOT EXISTS push_subscriptions_new (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    endpoint TEXT NOT NULL,
    p256dh TEXT NOT NULL,
    auth TEXT NOT NULL,
    user_id VARCHAR(255),
    CONSTRAINT uk_push_endpoint_new UNIQUE (endpoint(255))
);

-- 2. Copiar datos válidos de la tabla antigua (si existe)
INSERT IGNORE INTO push_subscriptions_new (id, endpoint, p256dh, auth, user_id)
SELECT id, endpoint, p256dh, auth, user_id
FROM push_subscriptions
WHERE endpoint IS NOT NULL
  AND p256dh IS NOT NULL
  AND auth IS NOT NULL
  AND LENGTH(endpoint) > 10
  AND LENGTH(p256dh) > 10
  AND LENGTH(auth) > 10;

-- 3. Eliminar tabla antigua y renombrar la nueva
DROP TABLE IF EXISTS push_subscriptions;
RENAME TABLE push_subscriptions_new TO push_subscriptions;

-- 4. Verificar la migración
SELECT COUNT(*) AS total_suscripciones FROM push_subscriptions;

-- Nota: Si la tabla no existía, simplemente se creará con la estructura correcta
-- En ese caso, Hibernate la creará automáticamente al iniciar la aplicación

