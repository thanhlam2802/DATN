<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount, onUnmounted, watch } from "vue";
import { useRoute } from "vue-router";
import { servicePaymentMake, servicePaymentConfirm, accountLookup } from '@/api/coreBankingApi';
import { markOrderSuccess } from '@/api/OrderApi'

/// — Route & Order details + Countdown —
const route = useRoute();
const orderId = route.params.orderId;
const orderDetails = ref(null);
const timeLeft = ref(0);
const timerInterval = ref(null);
const hasExpired = ref(false);

const minutes = computed(() => Math.floor(timeLeft.value / 60));
const seconds = computed(() => timeLeft.value % 60);

const fetchOrderDetails = async () => {
  try {
    const res = await fetch(`http://localhost:8080/api/v1/orders/${orderId}`);
    const json = await res.json();
    if (res.ok) {
      orderDetails.value = json.data;
      startCountdown(json.data.expiresAt);
    } else {
      console.error("Lỗi khi lấy đơn hàng:", json.message);
    }
  } catch (e) {
    console.error("Lỗi mạng:", e);
  }
};

const startCountdown = expiresAt => {
  if (!expiresAt) return;
  const expireMs = new Date(expiresAt).getTime();
  timerInterval.value = setInterval(() => {
    const rem = Math.round((expireMs - Date.now()) / 1000);
    if (rem > 0) timeLeft.value = rem;
    else {
      timeLeft.value = 0;
      hasExpired.value = true;
      clearInterval(timerInterval.value);
    }
  }, 1000);
};

onMounted(fetchOrderDetails);
onUnmounted(() => clearInterval(timerInterval.value));

const formatPrice = price =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(price || 0);

/// — Bank transfer + OTP —
const emit = defineEmits(['submit']);

const showDropdown = ref(false);
const selectedBank = ref(null);
const banks = [
  { code: 'VCB', name: 'Vietcombank', logo: 'https://hienlaptop.com/wp-content/uploads/2024/12/logo-vietcombank-vector-11.png' },
  { code: 'VIB', name: 'Vietinbank', logo: 'https://cdn.haitrieu.com/wp-content/uploads/2022/01/Logo-VietinBank-CTG-Ori.png' },
  { code: 'BIDV', name: 'BIDV', logo: 'https://bidv.com.vn/wps/wcm/connect/674b6448-d23b-484e-b4d3-1e86fa68bd0d/Logo+Nguyen+ban+nen+trang.png?MOD=AJPERES&CACHEID=ROOTWORKSPACE-674b6448-d23b-484e-b4d3-1e86fa68bd0d-pfdjkOq' },
  { code: 'Techcombank', name: 'Techcombank', logo: 'https://plus.vtc.edu.vn/wp-content/uploads/2020/09/techcombank.png' },
  { code: 'ACB', name: 'ACB', logo: 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/d9/Asia_Commercial_Bank_logo.svg/2560px-Asia_Commercial_Bank_logo.svg.png' },
  { code: 'Sacombank', name: 'Sacombank', logo: 'https://upload.wikimedia.org/wikipedia/commons/2/2e/Logo-Sacombank-new.png' },
  { code: 'VP Bank', name: 'VPBank', logo: 'https://cdn.haitrieu.com/wp-content/uploads/2022/01/Icon-VPBank.png' },
  { code: 'MB Bank', name: 'MB Bank', logo: 'https://upload.wikimedia.org/wikipedia/commons/2/25/Logo_MB_new.png' },
  { code: 'TCB', name: 'TPBank', logo: 'https://cdn.haitrieu.com/wp-content/uploads/2022/02/Icon-TPBank.png' },
  { code: 'Agri Bank', name: 'Agribank', logo: 'https://cdn.haitrieu.com/wp-content/uploads/2022/01/Icon-Agribank.png' },
]

const bankTransfer = reactive({
  bankCode: '',
  accountNumber: '',
  accountName: '',
  availableBalance: 0,
  amount: 0,
  currency: 'VND',
});

const isLoading = ref(false);
const notFound = ref(false);
const found = ref(false);
const isPaying = ref(false);

const showOtpDialog = ref(false);
const paymentId = ref(null);
const otp = ref('');
const isConfirming = ref(false);
const otpError = ref('');
const otpSuccess = ref(false);

const amountError = ref(false);
const amountValidationMessage = ref('');

const otpCountdown = ref(600);
const otpCountdownDisplay = ref('10:00');
const otpExpired = ref(false);
let otpTimer = null;

function resetAccountInfo() {
  bankTransfer.accountName = '';
  bankTransfer.availableBalance = 0;
  bankTransfer.amount = 0;
  notFound.value = false;
  found.value = false;
}

function selectBank(bank) {
  selectedBank.value = bank;
  bankTransfer.bankCode = bank.code;
  showDropdown.value = false;
}

function handleClickOutside(e) {
  const dd = document.querySelector('.bank-dropdown');
  if (dd && !dd.contains(e.target)) showDropdown.value = false;
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});
onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});

async function onAccountNumberBlur() {
  if (!selectedBank.value || !bankTransfer.accountNumber) {
    resetAccountInfo();
    return;
  }
  isLoading.value = true;
  notFound.value = false;
  found.value = false;
  try {
    const res = await accountLookup({
      bankCode: selectedBank.value.code,
      accountNumber: bankTransfer.accountNumber
    });
    bankTransfer.accountName = res.data.accountHolderName || '';
    bankTransfer.availableBalance = res.data.availableBalance || 0;
    bankTransfer.currency = res.data.currency || 'VND';
    if (bankTransfer.accountName) {
      found.value = true;
      // Tự động điền số tiền thanh toán và validate
      if (orderDetails.value?.amount) {
        bankTransfer.amount = orderDetails.value.amount;
        validateAmount();
      }
    }
    else notFound.value = true;
  } catch {
    resetAccountInfo();
    notFound.value = true;
  } finally {
    isLoading.value = false;
  }
}

function validateAmount() {
  amountValidationMessage.value = '';
  const requiredAmount = orderDetails.value?.amount;

  if (!requiredAmount) {
    amountError.value = true;
    amountValidationMessage.value = 'Chưa có thông tin số tiền cần thanh toán.';
    return;
  }

  if (bankTransfer.amount !== requiredAmount) {
    amountError.value = true;
    amountValidationMessage.value = `Vui lòng nhập đúng số tiền thanh toán: ${formatPrice(requiredAmount)}`;
  } else if (bankTransfer.amount > bankTransfer.availableBalance) {
    amountError.value = true;
    amountValidationMessage.value = `Số dư không đủ. Cần ${formatPrice(requiredAmount)}.`;
  } else {
    amountError.value = false;
  }
}

async function submitPayment() {
  validateAmount();
  if (amountError.value || !bankTransfer.amount) return;
  isPaying.value = true;
  try {
    const res = await servicePaymentMake({
      customerAccountNumber: bankTransfer.accountNumber,
      customerBankCode: selectedBank.value.code,
      amount: bankTransfer.amount,
      currency: bankTransfer.currency,
      remittanceInfo: 'Thanh toán dịch vụ',
      idempotencyKey: Date.now().toString(),
    });
    if (res.data?.paymentId) {
      paymentId.value = res.data.paymentId;
      showOtpDialog.value = true;
      otp.value = '';
      otpError.value = '';
      otpSuccess.value = false;
      window.$toast('Đã gửi OTP qua email.', 'success');

    } else {
      window.$toast('Không nhận được paymentId.', 'error');
    }
  } catch {
    window.$toast('Gửi thanh toán thất bại!', 'error');
  } finally {
    isPaying.value = false;
  }
}

async function confirmOtp() {
  if (!otp.value || !paymentId.value) return;
  isConfirming.value = true;
  otpError.value = '';
  otpSuccess.value = false;
  try {
    const res = await servicePaymentConfirm({ paymentId: paymentId.value, otp: otp.value });
    if (res.data?.transactionId) {
      otpSuccess.value = true;
      window.$toast('Thanh toán hoàn tất!', 'success');
      showOtpDialog.value = false;

      try {
        await markOrderSuccess(orderId, res.data.transactionId);
        console.log(orderId,res.data.transactionId);
      } catch (e) {
        window.$toast(e.message || 'Cập nhật trạng thái đơn hàng thất bại!', 'error');
      }
    } else {
      otpError.value = 'OTP không đúng hoặc đã hết hạn.';
    }
  } catch {
    otpError.value = 'Xác nhận OTP thất bại!';
  } finally {
    isConfirming.value = false;
  }
}

function startOtpCountdown() {
  otpCountdown.value = 600;
  otpExpired.value = false;
  updateOtpCountdownDisplay();
  if (otpTimer) clearInterval(otpTimer);
  otpTimer = setInterval(() => {
    if (otpCountdown.value > 0) {
      otpCountdown.value--;
      updateOtpCountdownDisplay();
    } else {
      otpExpired.value = true;
      updateOtpCountdownDisplay();
      clearInterval(otpTimer);
    }
  }, 1000);
}

function updateOtpCountdownDisplay() {
  const m = Math.floor(otpCountdown.value / 60);
  const s = otpCountdown.value % 60;
  otpCountdownDisplay.value = `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
}

watch(showOtpDialog, val => {
  if (val) startOtpCountdown();
  else clearInterval(otpTimer);
});

onUnmounted(() => {
  clearInterval(otpTimer);
});
</script>

<template>
  <div class="bg-gray-50 min-h-screen flex items-center justify-center py-12 px-4">
    <div class="max-w-lg w-full bg-white p-8 rounded-2xl shadow-lg">

      <!-- Phần 1: Đơn hàng + Countdown -->
      <div class="text-center mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Thanh toán Đơn hàng #{{ orderDetails?.id }}</h1>
        <p class="text-gray-500 mt-1">Vui lòng thanh toán trước khi hết thời gian giữ chỗ.</p>
      </div>
      <div class="p-4 border border-red-200 bg-red-50 rounded-lg text-center mb-6">
        <p v-if="!hasExpired" class="text-sm text-red-700">Thời gian giữ chỗ còn lại:</p>
        <p v-else class="text-sm font-semibold text-red-700">Đã hết thời gian giữ chỗ!</p>
        <p class="text-3xl font-mono font-bold text-red-600 tracking-wider mt-1">
          {{ String(minutes).padStart(2,'0') }}<span class="animate-pulse">:</span>{{ String(seconds).padStart(2,'0') }}
        </p>
      </div>
      <div v-if="orderDetails" class="mb-8 border-t pt-6 text-sm space-y-2">
        <div class="flex justify-between">
          <span>Tổng tiền:</span>
          <span class="font-medium text-blue-600">{{ formatPrice(orderDetails.amount) }}</span>
        </div>
        <div class="flex justify-between">
          <span>Trạng thái:</span>
          <span class="font-medium text-yellow-600 bg-yellow-100 px-2 rounded-full">{{ orderDetails.status }}</span>
        </div>
        <div class="flex justify-between">
          <span>Ngày tạo:</span>
          <span>{{ new Date(orderDetails.createdAt).toLocaleString("vi-VN") }}</span>
        </div>
      </div>

      <!-- ✅ Bus Booking Summary -->
      <div v-if="orderDetails && orderDetails.busBookings && orderDetails.busBookings.length > 0" 
           class="mb-8 bg-blue-50 p-4 rounded-lg border border-blue-200">
        <h3 class="font-semibold text-blue-800 mb-3 flex items-center">
          <i class="fas fa-bus mr-2"></i>
          Thông tin vé xe
        </h3>
        <div v-for="busBooking in orderDetails.busBookings" :key="busBooking.id" class="space-y-2 text-sm">
          <div class="grid grid-cols-2 gap-2">
            <div>
              <span class="text-gray-600">Tuyến:</span>
              <span class="font-medium ml-1">{{ busBooking.busName }}</span>
            </div>
            <div>
              <span class="text-gray-600">Ghế:</span>
              <span class="font-medium ml-1">{{ busBooking.seatNumbers?.join(', ') || 'N/A' }}</span>
            </div>
          </div>
          <div class="grid grid-cols-2 gap-2">
            <div>
              <span class="text-gray-600">Ngày đi:</span>
              <span class="font-medium ml-1">{{ new Date(busBooking.departureDate).toLocaleDateString('vi-VN') }}</span>
            </div>
            <div>
              <span class="text-gray-600">Giờ đi:</span>
              <span class="font-medium ml-1">{{ busBooking.departureTime?.substring(0, 5) || 'N/A' }}</span>
            </div>
          </div>
          <div class="grid grid-cols-1 gap-2">
            <div>
              <span class="text-gray-600">Hành khách:</span>
              <span class="font-medium ml-1">{{ busBooking.customerName }} - {{ busBooking.customerPhone }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Phần 3: Thanh toán qua chuyển khoản ngân hàng -->
      <div>
        <h3 class="text-lg font-semibold mb-4">Thanh toán chuyển khoản</h3>
        <div class="space-y-6">
          <!-- Chọn ngân hàng -->
          <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2">
            <i class="fas fa-university text-blue-400"></i> Ngân hàng
          </label>
          <div class="relative">
            <button type="button" @click="showDropdown = !showDropdown" class="w-full border border-gray-300 rounded-lg px-4 py-2 flex items-center focus:outline-none focus:ring-2 focus:ring-indigo-400 bg-white hover:border-indigo-400 transition">
              <img v-if="selectedBank && selectedBank.logo" :src="selectedBank.logo" class="w-6 h-6 mr-2 object-contain" />
              <span class="font-medium">{{ selectedBank ? selectedBank.name : 'Chọn ngân hàng' }}</span>
              <svg class="ml-auto w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/></svg>
            </button>
            <ul v-if="showDropdown" class="absolute z-20 bg-white border w-full mt-1 rounded shadow max-h-60 overflow-auto animate-fade-in">
              <li v-for="bank in banks" :key="bank.code" @click="selectBank(bank)" class="flex items-center px-4 py-2 hover:bg-indigo-50 cursor-pointer transition">
                <img v-if="bank.logo" :src="bank.logo" class="w-6 h-6 mr-2 object-contain" />
                <span>{{ bank.name }}</span>
              </li>
            </ul>
          </div>
        </div>
          <!-- Số tài khoản -->
          <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2">
            <i class="fas fa-id-card text-green-400"></i> Số tài khoản
          </label>
          <input v-model="bankTransfer.accountNumber" type="text" placeholder="Nhập số tài khoản" maxlength="20"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-green-400 transition placeholder-gray-400"
            @blur="onAccountNumberBlur" @input="resetAccountInfo" />
          <div v-if="isLoading" class="flex items-center mt-2 text-blue-500"><span class="animate-spin rounded-full h-5 w-5 border-t-2 border-b-2 border-blue-400 mr-2"></span>Đang kiểm tra...</div>
          <div v-if="notFound && !isLoading" class="text-red-500 text-sm mt-2">Không tìm thấy thông tin số tài khoản</div>
        </div>
        <!-- Thông tin tài khoản -->
        <div v-if="found" class="bg-indigo-50 rounded-lg p-4 flex flex-col gap-2 border border-indigo-100">
          <div class="flex items-center gap-2">
            <i class="fas fa-user-circle text-indigo-400"></i>
            <span class="font-semibold text-gray-700">Tên tài khoản:</span>
            <span class="ml-auto font-bold text-indigo-700">{{ bankTransfer.accountName }}</span>
          </div>
          <div class="flex items-center gap-2">
            <i class="fas fa-wallet text-green-400"></i>
            <span class="font-semibold text-gray-700">Số dư khả dụng:</span>
            <span class="ml-auto font-bold text-green-600">{{ bankTransfer.availableBalance}} {{ bankTransfer.currency }}</span>
          </div>
        </div>
        <!-- Số tiền muốn thanh toán -->
        <div v-if="found">
          <label class="block text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2">
            <i class="fas fa-coins text-yellow-400"></i> Số tiền muốn thanh toán
          </label>
          <input v-model.number="bankTransfer.amount" type="number" min="0" :max="bankTransfer.availableBalance" placeholder="Nhập số tiền"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-yellow-400 transition placeholder-gray-400"
            @input="validateAmount" />
          <div v-if="amountError" class="text-red-500 text-sm mt-1">{{ amountValidationMessage }}</div>
        </div>
        <div class="flex justify-end" v-if="found">
          <button @click="submitPayment" :disabled="isPaying || amountError || !bankTransfer.amount" class="bg-gradient-to-r from-indigo-500 to-blue-500 hover:from-indigo-600 hover:to-blue-600 text-white px-8 py-2 rounded-lg font-semibold flex items-center shadow-lg transition disabled:opacity-60">
            <span v-if="isPaying" class="animate-spin rounded-full h-5 w-5 border-t-2 border-b-2 border-white mr-2"></span>
            <i class="fas fa-paper-plane mr-2"></i> Thanh toán
          </button>
        </div>
        </div>
      </div>

      <!-- OTP Modal -->
      <transition name="modal" appear>
        <div v-if="showOtpDialog" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40">
          <div class="bg-white rounded-2xl shadow-2xl max-w-md w-full overflow-hidden">
            <div class="bg-gradient-to-r from-indigo-500 to-blue-400 px-6 py-4 flex justify-between items-center">
              <h3 class="text-white flex items-center gap-2"><i class="fas fa-shield-alt"></i> Xác thực OTP</h3>
              <button @click="showOtpDialog=false"><i class="fas fa-times text-white"></i></button>
            </div>
            <div class="p-6 space-y-4">
              <p>OTP đã gửi qua email. Nhập để xác nhận thanh toán.</p>
              <div class="flex items-center gap-2">
                <i class="fas fa-clock"></i>
                <span>Thời gian còn lại:</span>
                <span class="font-mono">{{ otpCountdownDisplay }}</span>
              </div>
              <input v-model="otp" maxlength="6"
                     :disabled="otpExpired" placeholder="Nhập OTP"
                     class="w-full border rounded-lg px-4 py-2 text-center"/>
              <div v-if="otpError" class="text-red-500">{{ otpError }}</div>
              <div v-if="otpSuccess" class="text-green-600">Xác thực thành công!</div>
              <div v-if="otpExpired" class="text-red-500">OTP đã hết hạn.</div>
              <div class="flex justify-end gap-2">
                <button @click="showOtpDialog=false" class="px-4 py-2 rounded bg-gray-200">Hủy</button>
                <button @click="confirmOtp" :disabled="isConfirming||!otp||otpExpired"
                        class="px-4 py-2 rounded text-white bg-gradient-to-r from-indigo-500 to-blue-500 disabled:opacity-50 flex items-center">
                  <span v-if="isConfirming" class="animate-spin inline-block h-5 w-5 border-t-2 border-white mr-2 rounded-full"></span>
                  Xác nhận
                </button>
              </div>
            </div>
          </div>
        </div>
      </transition>

    </div>
  </div>
</template>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.4s;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px) scale(0.98); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
.modal-enter-active, .modal-leave-active {
  transition: all 0.3s;
}
.modal-enter-from, .modal-leave-to {
  opacity: 0;
  transform: scale(0.95);
}
.modal-enter-to, .modal-leave-from {
  opacity: 1;
  transform: scale(1);
}
</style> 