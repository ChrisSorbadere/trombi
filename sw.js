const CACHE_VERSION = 'trombi-v1.0.0';
const STATIC = ['./', './index.html', './manifest.json', './favicon.ico', './icons/icon-192.png', './icons/icon-512.png', './icons/favicon-32.png', './icons/apple-touch-icon.png'];
self.addEventListener('install', e => {
  e.waitUntil(caches.open(CACHE_VERSION).then(c => c.addAll(STATIC)).then(() => self.skipWaiting()));
});
self.addEventListener('activate', e => {
  e.waitUntil(caches.keys().then(ks => Promise.all(ks.filter(k => k !== CACHE_VERSION).map(k => caches.delete(k)))).then(() => self.clients.claim()));
});
self.addEventListener('fetch', e => {
  const req = e.request;
  if (req.method !== 'GET' || new URL(req.url).origin !== location.origin) return;
  const isPage = req.mode === 'navigate' || req.url.endsWith('/index.html');
  if (isPage) {
    e.respondWith(fetch(req).then(r => { const c = r.clone(); caches.open(CACHE_VERSION).then(ca => ca.put('./index.html', c)); return r; })
      .catch(() => caches.match('./index.html')));
  } else {
    e.respondWith(caches.match(req).then(r => r || fetch(req).then(n => { const c = n.clone(); caches.open(CACHE_VERSION).then(ca => ca.put(req, c)); return n; })));
  }
});
