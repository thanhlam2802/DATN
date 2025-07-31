export const VAPID_PUBLIC_KEY = 'BFWXg7NO8zh2w_W3YZnRXZsRdLJ5-zcpeph5JLhZ0ufN6CkaQizQI7gqZMl5chN2b3QZDIL0MHskCZy0mhEORpU';
export const VAPID_PRIVATE_KEY = 'nNR58nxC_FnLkA9wIstp1xRbxHZ-8Mo8TUsqMoIptdI';

export const urlBase64ToUint8Array = (base64String) => {
  const padding = '='.repeat((4 - base64String.length % 4) % 4);
  const base64 = (base64String + padding)
    .replace(/-/g, '+')
    .replace(/_/g, '/');

  const rawData = window.atob(base64);
  const outputArray = new Uint8Array(rawData.length);

  for (let i = 0; i < rawData.length; ++i) {
    outputArray[i] = rawData.charCodeAt(i);
  }
  return outputArray;
};

export const isValidVapidKey = (key) => {
  return key && key !== 'YOUR_VAPID_PUBLIC_KEY_HERE' && key.length > 0;
}; 