import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api/v1';
const API_KEY = 'test-api-key';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'X-API-KEY': API_KEY
  }
});

// Account Lookup
export const accountLookup = (data) =>
  api.post('/account-lookup', data);

// Get Balance
export const getBalance = (accountNumber, bankCode) =>
  api.get('/accounts/balances', {
    params: { accountNumber, bankCode }
  });

// Get Transactions
export const getTransactions = (accountNumber, bankCode, from, to) =>
  api.get('/accounts/transactions', {
    params: { accountNumber, bankCode, from, to }
  });

// Payment (Account to Account)
export const payment = (data) =>
  api.post('/payments', data);

// Service Payment (Khách hàng nạp vào hệ thống)
export const servicePayment = (data) =>
  api.post('/payments/service', data);

// Refund by PaymentId
export const refundByPaymentId = (paymentId, data) =>
  api.post(`/payments/${paymentId}/refunds`, data);



// Refund Make (Gửi OTP hủy vé)
export const refundMake = (data) =>
  api.post('/refunds/make', data);

// Refund Confirm (Xác nhận OTP hủy vé)
export const refundConfirm = (data) =>
  api.post('/refunds/confirm', data);

// Service Payment Make (Gửi OTP)
export const servicePaymentMake = (data) =>
  api.post('/payments/service/make', data);

// Service Payment Confirm (Xác nhận OTP)
export const servicePaymentConfirm = (data) =>
  api.post('/payments/service/confirm', data); 