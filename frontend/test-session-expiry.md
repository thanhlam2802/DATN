# Test Session Expiry Flow

## 🧪 CÁCH TEST:

### **1. Test Manual trong Browser Console:**

```javascript
// 1. Trigger session expired modal trực tiếp
if (window.globalSessionExpiredHandler) {
  window.globalSessionExpiredHandler()
} else {
  console.log('❌ globalSessionExpiredHandler not found')
}

// 2. Test bằng cách xóa token và gọi API
localStorage.removeItem('accessToken')
// Sau đó thử gọi bất kỳ API nào (VD: tạo BusCategory)

// 3. Test bằng cách set token invalid
localStorage.setItem('accessToken', 'invalid-token-here')
// Sau đó thử gọi API
```

### **2. Test với Backend:**

1. **Đăng nhập bình thường**
2. **Đợi token hết hạn** hoặc **thay đổi JWT secret** ở backend
3. **Thực hiện bất kỳ action nào** (VD: tạo bus category, route, etc.)
4. **Kiểm tra:**
   - ✅ Có hiện SessionExpiredModal không?
   - ✅ Click "Đăng nhập lại" có redirect về `/login` không?
   - ✅ Token có bị clear khỏi localStorage không?

### **3. Test Cases:**

- ❌ **Token hết hạn** → Modal hiện
- ❌ **Token invalid** → Modal hiện  
- ❌ **Token bị xóa** → Modal hiện khi call API
- ❌ **Backend trả 401** → Modal hiện
- ✅ **Click "Đăng nhập lại"** → Redirect `/login`
- ✅ **Modal không thể đóng** bằng backdrop click

### **4. Verify Global Handler:**

```javascript
// Check if global handler exists
console.log('Global handler:', typeof window.globalSessionExpiredHandler)

// Check if session modal instance exists
console.log('DOM elements:', document.querySelectorAll('[class*="SessionExpired"]'))
```

## 🎯 EXPECTED BEHAVIOR:

1. **Token hết hạn** → GraphQL request fail với 401
2. **401 detected** → Trigger `window.globalSessionExpiredHandler()`
3. **Modal xuất hiện** toàn màn hình với thông báo rõ ràng
4. **User click "Đăng nhập lại"** → Clear tokens + redirect `/login`
5. **App state reset** hoàn toàn

## 🔧 DEBUG:

Nếu có vấn đề, check:
- Console có error không?
- `window.globalSessionExpiredHandler` có exist không?
- SessionExpiredModal có được mount không?
- GraphQL interceptor có hoạt động không?
