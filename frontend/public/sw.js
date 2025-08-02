const CACHE_NAME = 'hotel-admin-v1';
const urlsToCache = [
  '/',
  '/static/js/bundle.js',
  '/static/css/main.css',
  '/favicon.ico'
];

self.addEventListener('install', (event) => {
  console.log('Service Worker installing...');
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then((cache) => {
        console.log('Opened cache');
        return cache.addAll(urlsToCache);
      })
  );
});

self.addEventListener('activate', (event) => {
  console.log('Service Worker activating...');
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((cacheName) => {
          if (cacheName !== CACHE_NAME) {
            console.log('Deleting old cache:', cacheName);
            return caches.delete(cacheName);
          }
        })
      );
    })
  );
});

self.addEventListener('push', (event) => {
  console.log('Push event received:', event);

  let notificationData = {
    title: 'Thông báo mới',
    body: 'Có thông báo mới từ hệ thống',
    icon: '/favicon.ico',
    badge: '/favicon.ico',
    tag: 'hotel-admin-notification',
    requireInteraction: false,
    silent: false,
    data: {
      url: '/admin/hotel/dashboard'
    }
  };

  if (event.data) {
    try {
      const data = event.data.json();
      notificationData = {
        title: data.title || notificationData.title,
        body: data.body || data.message || notificationData.body,
        icon: data.icon || getNotificationIcon(data.type) || notificationData.icon,
        badge: data.badge || notificationData.badge,
        tag: data.tag || notificationData.tag,
        requireInteraction: data.requireInteraction || false,
        silent: data.silent || false,
        data: {
          url: data.url || '/admin/hotel/dashboard',
          type: data.type,
          timestamp: data.timestamp || new Date().toISOString()
        },
        actions: [
          {
            action: 'view',
            title: 'Xem chi tiết',
            icon: '/icons/view.png'
          },
          {
            action: 'dismiss',
            title: 'Đóng',
            icon: '/icons/close.png'
          }
        ]
      };
    } catch (error) {
      console.error('Error parsing push data:', error);
    }
  }

  event.waitUntil(
    self.registration.showNotification(notificationData.title, notificationData)
  );
});

self.addEventListener('notificationclick', (event) => {
  console.log('Notification clicked:', event);

  event.notification.close();

  if (event.action === 'dismiss') {
    return;
  }

  event.waitUntil(
    clients.matchAll({ type: 'window', includeUncontrolled: true }).then((clientList) => {
      for (let i = 0; i < clientList.length; i++) {
        const client = clientList[i];
        if (client.url.includes('/admin/hotel/dashboard') && 'focus' in client) {
          return client.focus();
        }
      }

      if (clients.openWindow) {
        const url = event.notification.data?.url || '/admin/hotel/dashboard';
        return clients.openWindow(url);
      }
    })
  );
});

self.addEventListener('sync', (event) => {
  console.log('Background sync event:', event);

  if (event.tag === 'background-sync') {
    event.waitUntil(doBackgroundSync());
  }
});

async function doBackgroundSync() {
  try {
    console.log('Performing background sync...');
  } catch (error) {
    console.error('Background sync failed:', error);
  }
}

function getNotificationIcon(type) {
  switch (type) {
    case 'booking':
      return '/icons/booking.png';
    case 'payment':
      return '/icons/payment.png';
    case 'review':
      return '/icons/review.png';
    case 'cancellation':
      return '/icons/cancellation.png';
    case 'system':
      return '/icons/system.png';
    default:
      return '/favicon.ico';
  }
}

self.addEventListener('fetch', (event) => {
  event.respondWith(
    caches.match(event.request)
      .then((response) => {
        return response || fetch(event.request);
      })
  );
}); 