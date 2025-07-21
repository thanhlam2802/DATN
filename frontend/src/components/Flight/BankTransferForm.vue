<template>
  <div class="max-w-lg mx-auto mt-8">
    <div class="bg-white rounded-2xl shadow-xl p-8 relative border border-gray-100">
      <div class="absolute w-40 h-40 bg-sky-200 rounded-full -top-24 -right-32 blur-2xl opacity-60"></div>
      <h3 class="text-2xl font-bold text-indigo-700 mb-6 flex items-center gap-2">
        <i class="fas fa-money-check-alt text-indigo-400"></i> Thanh toán dịch vụ
      </h3>
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
            <span class="ml-auto font-bold text-green-600">{{ formatCurrency(bankTransfer.availableBalance) }} {{ bankTransfer.currency }}</span>
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
          <div v-if="amountError" class="text-red-500 text-sm mt-1">Số tiền không hợp lệ hoặc vượt quá số dư khả dụng</div>
        </div>
        <div class="flex justify-end" v-if="found">
          <button @click="submit" :disabled="isPaying || amountError || !bankTransfer.amount" class="bg-gradient-to-r from-indigo-500 to-blue-500 hover:from-indigo-600 hover:to-blue-600 text-white px-8 py-2 rounded-lg font-semibold flex items-center shadow-lg transition disabled:opacity-60">
            <span v-if="isPaying" class="animate-spin rounded-full h-5 w-5 border-t-2 border-b-2 border-white mr-2"></span>
            <i class="fas fa-paper-plane mr-2"></i> Thanh toán
          </button>
        </div>
      </div>
    </div>
    <!-- Modal OTP đẹp -->
    <transition name="modal" appear>
      <div v-if="showOtpDialog" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
        <div class="relative w-full max-w-md bg-white rounded-2xl shadow-2xl overflow-hidden flex flex-col animate-fade-in">
          <div class="bg-gradient-to-r from-indigo-500 to-blue-400 px-6 py-4 flex items-center justify-between">
            <h3 class="text-lg font-bold text-white flex items-center gap-2"><i class="fas fa-shield-alt"></i> Xác thực OTP</h3>
            <button @click="showOtpDialog = false" class="text-white hover:text-gray-200 focus:outline-none">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>
          <div class="p-6 flex flex-col gap-4">
            <div class="text-gray-700 text-base mb-2">Mã OTP đã được gửi qua email. Vui lòng nhập để xác nhận thanh toán.</div>
            <input v-model="otp" maxlength="6" class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400 text-center tracking-widest text-xl placeholder-gray-400" placeholder="Nhập OTP" />
            <div v-if="otpError" class="text-red-500 text-sm mt-1">{{ otpError }}</div>
            <div v-if="otpSuccess" class="text-green-600 text-sm mt-1">Xác nhận OTP thành công!</div>
            <div class="flex justify-end gap-2 mt-2">
              <button @click="showOtpDialog = false" class="px-4 py-2 rounded bg-gray-200 hover:bg-gray-300">Hủy</button>
              <button @click="confirmOtp" :disabled="isConfirming || !otp" class="px-4 py-2 rounded bg-gradient-to-r from-indigo-500 to-blue-500 text-white hover:from-indigo-600 hover:to-blue-600 flex items-center transition disabled:opacity-60">
                <span v-if="isConfirming" class="animate-spin rounded-full h-5 w-5 border-t-2 border-b-2 border-white mr-2"></span>
                <i class="fas fa-check mr-2"></i> Xác nhận
              </button>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { servicePaymentMake, servicePaymentConfirm } from '@/api/coreBankingApi'
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { defineEmits } from 'vue'
import { accountLookup } from '@/api/coreBankingApi'

const emit = defineEmits(['submit'])

const showDropdown = ref(false)
const selectedBank = ref(null)

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
  currency: 'VND', // Thêm currency
})

const isLoading = ref(false)
const notFound = ref(false)
const found = ref(false)
const isPaying = ref(false)
const showOtpDialog = ref(false)
const paymentId = ref(null)
const otp = ref('')
const isConfirming = ref(false)
const otpError = ref('')
const otpSuccess = ref(false)
const amountError = ref(false)

function resetAccountInfo() {
  bankTransfer.accountName = ''
  bankTransfer.availableBalance = 0
  bankTransfer.amount = 0
  notFound.value = false
  found.value = false
}

function selectBank(bank) {
  selectedBank.value = bank
  bankTransfer.bankCode = bank.code
  showDropdown.value = false
}

function handleClickOutside(event) {
  const dropdown = document.querySelector('.relative')
  if (dropdown && !dropdown.contains(event.target)) {
    showDropdown.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})
onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})

async function onAccountNumberBlur() {
  if (!selectedBank.value || !bankTransfer.accountNumber) {
    resetAccountInfo()
    return
  }
  isLoading.value = true
  notFound.value = false
  found.value = false
  try {
    const res = await accountLookup({
      bankCode: selectedBank.value.code,
      accountNumber: bankTransfer.accountNumber
    })
    bankTransfer.accountName = res.data.accountHolderName || ''
    bankTransfer.availableBalance = res.data.availableBalance || 0
    bankTransfer.currency = res.data.currency || 'VND'
    if (bankTransfer.accountName) {
      found.value = true
      notFound.value = false
    } else {
      found.value = false
      notFound.value = true
    }
  } catch (e) {
    resetAccountInfo()
    notFound.value = true
  } finally {
    isLoading.value = false
  }
}

function validateAmount() {
  if (!bankTransfer.amount || bankTransfer.amount <= 0 || bankTransfer.amount > bankTransfer.availableBalance) {
    amountError.value = true
  } else {
    amountError.value = false
  }
}

function formatCurrency(val) {
  if (!val) return ''
  return Number(val).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })
}

async function submit() {
  validateAmount()
  if (amountError.value || !bankTransfer.amount) return
  isPaying.value = true
  try {
    const res = await servicePaymentMake({
      customerAccountNumber: bankTransfer.accountNumber,
      customerBankCode: selectedBank.value.code,
      amount: bankTransfer.amount,
      currency: bankTransfer.currency || 'VND',
      remittanceInfo: 'Thanh toán dịch vụ',
      idempotencyKey: Date.now().toString()
    })
    if (res.data && res.data.paymentId) {
      paymentId.value = res.data.paymentId
      showOtpDialog.value = true
      otp.value = ''
      otpError.value = ''
      otpSuccess.value = false
      window.$toast('Đã gửi OTP qua email. Vui lòng kiểm tra và nhập mã OTP để xác nhận thanh toán.', 'success')
    } else {
      window.$toast('Không nhận được paymentId từ hệ thống core.', 'error')
    }
  } catch (e) {
    window.$toast('Gửi yêu cầu thanh toán thất bại!', 'error')
  } finally {
    isPaying.value = false
  }
}

async function confirmOtp() {
  if (!otp.value || !paymentId.value) return
  isConfirming.value = true
  otpError.value = ''
  otpSuccess.value = false
  try {
    const res = await servicePaymentConfirm({
      paymentId: paymentId.value,
      otp: otp.value
    })
    if (res.data && res.data.transactionId) {
      otpSuccess.value = true
      otpError.value = ''
      window.$toast('Xác nhận OTP thành công! Thanh toán đã hoàn tất.', 'success')
      showOtpDialog.value = false
    } else {
      otpError.value = 'OTP không đúng hoặc đã hết hạn.'
      window.$toast('OTP không đúng hoặc đã hết hạn.', 'error')
    }
  } catch (e) {
    otpError.value = 'Xác nhận OTP thất bại!'
    window.$toast('Xác nhận OTP thất bại!', 'error')
  } finally {
    isConfirming.value = false
  }
}
</script>

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