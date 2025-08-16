<template>
  <div class="invoice-container" ref="invoiceRef">
    <div class="invoice-header">
      <div class="company-info">
        <div class="company-details">
          <h1 class="company-name">TRAVELA</h1>
          <div class="company-contact">
            <p>Hệ thống đặt phòng khách sạn trực tuyến</p>
            <p>info@travela.com</p>
            <p>www.travela.com</p>
          </div>
        </div>
      </div>

      <div class="invoice-title">
        <h2>HÓA ĐƠN</h2>
      </div>
    </div>

    <div class="invoice-details">
      <div class="details-grid">
        <div class="detail-item">
          <span class="label">Số hóa đơn:</span>
          <span class="value">{{ props.invoiceData.orderId || `INV${props.invoiceData.id}` }}</span>
        </div>
        <div class="detail-item">
          <span class="label">Ngày:</span>
          <span class="value">{{ formatDateTime(new Date()) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">Mã đặt phòng:</span>
          <span class="value">{{ props.invoiceData.id }}</span>
        </div>
                 <div class="detail-item">
           <span class="label">Khách hàng:</span>
           <span class="value">{{ props.invoiceData.customerName }}</span>
         </div>
         <div class="detail-item">
           <span class="label">Email:</span>
           <span class="value">{{ props.invoiceData.customerEmail || props.invoiceData.customer?.email || '--' }}</span>
         </div>
         <div class="detail-item">
           <span class="label">Số điện thoại:</span>
           <span class="value">{{ props.invoiceData.customerPhone || props.invoiceData.customer?.phone || props.invoiceData.customer?.phoneNumber || '--' }}</span>
         </div>
        <div class="detail-item">
          <span class="label">Khách sạn:</span>
          <span class="value">{{ props.invoiceData.hotelName || '--' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">Số đêm lưu trú:</span>
          <span class="value">{{ calculateNights(props.invoiceData.checkInDate, props.invoiceData.checkOutDate)
          }}</span>
        </div>
        <div class="detail-item">
          <span class="label">Ngày nhận phòng:</span>
          <span class="value">{{ formatDate(props.invoiceData.checkInDate) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">Ngày trả phòng:</span>
          <span class="value">{{ formatDate(props.invoiceData.checkOutDate) }}</span>
        </div>
      </div>
    </div>

    <div class="line-items">
      <table class="items-table">
        <thead>
          <tr>
            <th>#</th>
            <th>Ngày</th>
            <th>Phòng</th>
            <th>Số lượng</th>
            <th>Đơn giá</th>
            <th>Thành tiền</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(night, index) in getNightsArray(props.invoiceData.checkInDate, props.invoiceData.checkOutDate)"
            :key="index">
            <td>{{ index + 1 }}</td>
            <td>{{ formatDate(night) }}</td>
            <td>
              <div>{{ props.invoiceData.variantName || props.invoiceData.roomType || 'Tiền phòng' }}</div>
            </td>
            <td>{{ props.invoiceData.rooms || props.invoiceData.roomQuantity || 1 }}</td>
            <td>{{ formatCurrency(getUnitPrice()) }} VND</td>
            <td>{{ formatCurrency(getUnitPrice() * (props.invoiceData.rooms || props.invoiceData.roomQuantity || 1)) }}
              VND</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="payment-summary">
      <div class="summary-item">
        <span class="label">Tổng tiền</span>
        <span class="value">{{ formatCurrency(props.invoiceData.totalPrice) }} VND</span>
      </div>
      <div class="summary-item">
        <span class="label">Thanh toán #1 (Bank Transfer)</span>
        <span class="value">{{ formatCurrency(props.invoiceData.totalPrice) }} VND</span>
      </div>
      <div class="summary-item">
        <span class="label">Số tiền còn lại</span>
        <span class="value">0 VND</span>
      </div>
    </div>

    <div class="invoice-footer">
      <div class="signature-section">
        <div class="signature-box">
          <p class="signature-title">Đại diện</p>
          <p class="signature-name">TRAVELA</p>
        </div>
        <div class="signature-box">
          <p class="signature-title">Khách hàng</p>
          <p class="signature-name">{{ props.invoiceData.customerName }}</p>
        </div>
      </div>
    </div>

    <div class="print-actions" v-if="showPrintButton">
      <button @click="printInvoice" class="print-btn">
        <i class="fas fa-print mr-2"></i>In hóa đơn
      </button>
      <button @click="$emit('close')" class="close-btn">
        <i class="fas fa-times mr-2"></i>Đóng
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const props = defineProps({
  invoiceData: {
    type: Object,
    required: true
  },
  showPrintButton: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['close']);
const invoiceRef = ref(null);

function formatDate(date) {
  if (!date) return '--';
  const d = new Date(date);
  return d.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
}

function formatDateTime(date) {
  if (!date) return '--';
  const d = new Date(date);
  return d.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  }) + ' ' + d.toLocaleTimeString('vi-VN', {
    hour: '2-digit',
    minute: '2-digit'
  });
}

function formatCurrency(value) {
  if (value == null) return '0';
  return new Intl.NumberFormat('vi-VN').format(value);
}

function calculateNights(checkIn, checkOut) {
  if (!checkIn || !checkOut) return '--';
  const checkInDate = new Date(checkIn);
  const checkOutDate = new Date(checkOut);
  const diffTime = checkOutDate - checkInDate;
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  return diffDays;
}

function getNightsArray(checkIn, checkOut) {
  if (!checkIn || !checkOut) return [];
  const nights = [];
  const checkInDate = new Date(checkIn);
  const checkOutDate = new Date(checkOut);
  const currentDate = new Date(checkInDate);

  while (currentDate < checkOutDate) {
    nights.push(new Date(currentDate));
    currentDate.setDate(currentDate.getDate() + 1);
  }

  return nights;
}

function getUnitPrice() {
  const rooms = props.invoiceData.rooms || props.invoiceData.roomQuantity || 1;
  const nights = calculateNights(props.invoiceData.checkInDate, props.invoiceData.checkOutDate);
  if (nights <= 0 || rooms <= 0) return props.invoiceData.totalPrice || 0;
  return Math.round((props.invoiceData.totalPrice || 0) / (rooms * nights));
}

function printInvoice() {
  window.print();
}
</script>

<style scoped>
.invoice-container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  font-family: 'Arial', sans-serif;
  color: #000;
  line-height: 1.4;
  padding: 40px;
}

.invoice-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 40px;
  border-bottom: 1px solid #ccc;
  padding-bottom: 20px;
}

.company-info {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  flex: 1;
}

.logo-section {
  flex-shrink: 0;
}

.logo {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  color: #000;
}

.company-details {
  flex: 1;
}

.company-name {
  font-size: 16px;
  font-weight: bold;
  color: #000;
  margin: 0 0 10px 0;
}

.company-contact {
  font-size: 12px;
  color: #000;
}

.company-contact p {
  margin: 2px 0;
}

.invoice-title {
  flex: 1;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}

.invoice-title h2 {
  font-size: 24px;
  font-weight: bold;
  color: #000;
  margin: 0;
  text-transform: uppercase;
}

.invoice-details {
  margin-bottom: 30px;
}

.details-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  font-size: 12px;
  align-items: center;
}

.detail-item .label {
  color: #000;
  font-weight: normal;
}

.detail-item .value {
  color: #000;
  font-weight: normal;
}

.line-items {
  margin-bottom: 30px;
}

.items-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}

.items-table th,
.items-table td {
  border: 1px solid #ccc;
  padding: 8px;
  text-align: left;
  color: #000;
}

.items-table th {
  font-weight: bold;
  background: #f9f9f9;
}

.items-table td {
  font-weight: normal;
}

.payment-summary {
  margin-bottom: 30px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 12px;
  border-bottom: 1px solid #eee;
}

.summary-item:last-child {
  border-bottom: none;
  font-weight: bold;
}

.summary-item .label {
  color: #000;
  font-weight: normal;
}

.summary-item .value {
  color: #000;
  font-weight: normal;
}

.summary-item:last-child .value {
  font-weight: bold;
}

.invoice-footer {
  margin-top: 40px;
}

.signature-section {
  display: flex;
  justify-content: space-between;
  gap: 40px;
}

.signature-box {
  flex: 1;
  text-align: center;
}

.signature-title {
  font-size: 12px;
  font-weight: bold;
  color: #000;
  margin: 0 0 20px 0;
}

.signature-name {
  font-size: 12px;
  color: #000;
  margin: 0;
}

.print-actions {
  position: fixed;
  top: 20px;
  right: 20px;
  display: flex;
  gap: 10px;
  z-index: 1000;
}

.print-btn,
.close-btn {
  padding: 10px 20px;
  border: 1px solid #ccc;
  background: white;
  color: #000;
  border-radius: 4px;
  font-weight: normal;
  cursor: pointer;
  transition: all 0.2s;
}

.print-btn:hover,
.close-btn:hover {
  background: #f5f5f5;
}

@media print {
  .print-actions {
    display: none;
  }

  .invoice-container {
    max-width: none;
    margin: 0;
    padding: 20px;
  }

  body {
    margin: 0;
    padding: 0;
    background: white;
  }

  * {
    -webkit-print-color-adjust: exact;
    color-adjust: exact;
  }
}
</style>