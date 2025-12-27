// src/main/resources/META-INF/resources/push-push-sw.js
const CACHE_NAME = 'merca-esi-v1';

self.addEventListener('install', (event) => {
    console.log('Push SW instalado');
    self.skipWaiting();
});

self.addEventListener('activate', (event) => {
    console.log('Push SW activado');
    event.waitUntil(
        caches.keys()
            .then((cacheNames) =>
                Promise.all(
                    cacheNames
                        .filter((name) => name !== CACHE_NAME)
                        .map((name) => caches.delete(name))
                )
            )
            .then(() => self.clients.claim())
    );
});

self.addEventListener('push', (event) => {
    console.log('Push recibido');

    let data = {};
    if (event.data) {
        try {
            data = event.data.json();
        } catch (e) {
            data = { title: 'Notificación', body: event.data.text() };
        }
    }

    const title = data.title || 'Notificación';
    const options = {
        body: data.body || 'Nueva notificación',
        icon: '/images/icon.png',
        badge: '/images/badge.png',
        tag: 'merca-esi-notification',
        renotify: true
    };

    event.waitUntil(self.registration.showNotification(title, options));
});

self.addEventListener('notificationclick', (event) => {
    event.notification.close();

    event.waitUntil(
        clients.matchAll({ type: 'window', includeUncontrolled: true }).then((clientList) => {
            for (const client of clientList) {
                if ('focus' in client) return client.focus();
            }
            return clients.openWindow('/');
        })
    );
});