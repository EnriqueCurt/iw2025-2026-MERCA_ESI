const CACHE_NAME = 'merca-esi-v1';
// Al inicio de sw.js
self.addEventListener('install', (event) => {
    self.skipWaiting(); // Ya lo tienes
});

self.addEventListener('activate', (event) => {
    event.waitUntil(
        clients.claim() // Toma control inmediato
    );
});
self.addEventListener('install', (event) => {
  console.log('Service Worker instalado');
  self.skipWaiting();
});

self.addEventListener('activate', (event) => {
  console.log('Service Worker activado');
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames
          .filter(name => name !== CACHE_NAME)
          .map(name => caches.delete(name))
      );
    }).then(() => self.clients.claim())
  );
});

self.addEventListener('push', (event) => {
  console.log('Push recibido:', event);

  const data = event.data ? event.data.json() : {};
  const options = {
    body: data.body || 'Nueva notificación',
    icon: '/images/icon.png',
    badge: '/images/badge.png',
    tag: 'merca-esi-notification'
  };

  event.waitUntil(
    self.registration.showNotification(data.title || 'Notificación', options)
  );
});

self.addEventListener('notificationclick', (event) => {
  console.log('Notificación clickeada');
  event.notification.close();
  event.waitUntil(
    clients.openWindow('/')
  );
});