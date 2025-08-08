<script setup>
import { ref, onMounted, computed, reactive, onUnmounted, onBeforeUnmount, watch } from "vue"; // Import thêm onUnmounted
import { useRoute, useRouter } from "vue-router";
import { getBearerToken } from "@/services/TokenService";
import { updateCustomer, cancelFlightBooking, getFlightDetail } from '@/api/flightApi'
import { servicePaymentMake, servicePaymentConfirm, accountLookup, refundMake, refundConfirm } from '@/api/coreBankingApi';
import SockJS from "sockjs-client/dist/sockjs.min.js";
import { markOrderSuccess } from '@/api/OrderApi'
import Stomp from "stompjs";
// --- END: Thư viện WebSocket ---
const route = useRoute();
const router = useRouter();

// --- STATE MANAGEMENT ---
const order = ref(null);
const flightBookingDetails = ref([]);
const isLoading = ref(true);
const error = ref(null);
const processingItemId = ref(null);

// --- STATE CHO VOUCHER ---
const voucherCode = ref("");
const isApplyingVoucher = ref(false);
const suggestedVouchers = ref([]);
const isLoadingVouchers = ref(false);
const orderId = route.params.id;
// --- START: STATE CHO WEBSOCKET ---
const stompClient = ref(null);
const isSocketConnected = ref(false);
// --- END: STATE CHO WEBSOCKET ---
const timeLeft = ref(0);
const timerInterval = ref(null);
const hasExpired = ref(false);
const minutes = computed(() => Math.floor(timeLeft.value / 60));
const seconds = computed(() => timeLeft.value % 60);
// Reactive state for hotel image sliders
const hotelImageIndices = reactive({});
const slideDirectionMap = reactive({});
const customer = ref({
  fullName: '',
  gender: null,
  dob: '',
  passport: '',
  email: '',
  phone: '',
  id: null
})
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

// --- COMPUTED PROPERTIES ---
const isEditable = computed(() => order.value?.status === "PENDING_PAYMENT");
const isPaid = computed(() => order.value?.status === "PAID");
const canRefund = computed(() => isPaid.value && order.value?.transactionId);

// Computed property to check if any bookings are close to expiry
const bookingsCloseToExpiry = computed(() => {
  if (!order.value) return [];

  const now = new Date();
  const oneDayInMs = 24 * 60 * 60 * 1000;
  const closeBookings = [];

  // Check flight bookings
  if (order.value.flightBookings && order.value.flightBookings.length > 0) {
    for (const booking of order.value.flightBookings) {
      if (booking.flightSlot && booking.flightSlot.flight && booking.flightSlot.flight.departureTime) {
        const departureTime = new Date(booking.flightSlot.flight.departureTime);
        const timeUntilDeparture = departureTime.getTime() - now.getTime();

        if (timeUntilDeparture <= oneDayInMs && timeUntilDeparture > 0) {
          closeBookings.push({
            type: 'flight',
            name: `${booking.flightSlot.flight.name} (${booking.flightSlot.flight.flightNumber})`,
            remainingTime: formatRemainingTime(timeUntilDeparture),
            departureTime: booking.flightSlot.flight.departureTime
          });
        }
      }
    }
  }

  // // Check hotel bookings
  // if (order.value.hotelBookings && order.value.hotelBookings.length > 0) {
  //   for (const booking of order.value.hotelBookings) {
  //     if (booking.checkInDate) {
  //       const checkInDate = new Date(booking.checkInDate);
  //       const timeUntilCheckIn = checkInDate.getTime() - now.getTime();

  //       if (timeUntilCheckIn <= oneDayInMs && timeUntilCheckIn > 0) {
  //         closeBookings.push({
  //           type: 'hotel',
  //           name: booking.hotelName || 'N/A',
  //           remainingTime: formatRemainingTime(timeUntilCheckIn),
  //           checkInDate: booking.checkInDate
  //         });
  //       }
  //     }
  //   }
  // }

  // // Check tour bookings
  // if (order.value.tourBookings && order.value.tourBookings.length > 0) {
  //   for (const booking of order.value.tourBookings) {
  //     if (booking.departureDate) {
  //       const departureDate = new Date(booking.departureDate);
  //       const timeUntilDeparture = departureDate.getTime() - now.getTime();

  //       if (timeUntilDeparture <= oneDayInMs && timeUntilDeparture > 0) {
  //         closeBookings.push({
  //           type: 'tour',
  //           name: booking.tourName || 'N/A',
  //           remainingTime: formatRemainingTime(timeUntilDeparture),
  //           departureDate: booking.departureDate
  //         });
  //       }
  //     }
  //   }
  // }

  // // Check bus bookings
  // if (order.value.busBookings && order.value.busBookings.length > 0) {
  //   for (const booking of order.value.busBookings) {
  //     if (booking.busSlot && booking.busSlot.bus && booking.busSlot.bus.departureTime) {
  //       const departureTime = new Date(booking.busSlot.bus.departureTime);
  //       const timeUntilDeparture = departureTime.getTime() - now.getTime();

  //       if (timeUntilDeparture <= oneDayInMs && timeUntilDeparture > 0) {
  //         closeBookings.push({
  //           type: 'bus',
  //           name: booking.busSlot.bus.name || 'N/A',
  //           remainingTime: formatRemainingTime(timeUntilDeparture),
  //           departureTime: booking.busSlot.bus.departureTime
  //         });
  //       }
  //     }
  //   }
  // }

  return closeBookings;
});

// --- HELPER FUNCTIONS ---
const formatPrice = (price) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    price || 0
  );

const formatDate = (dateString) =>
  new Date(dateString).toLocaleDateString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });

function handleEdittem(data) {
  const div = document.getElementById('modeledit');
  customer.value = data;
  div.classList.remove('hidden');

}
const notFound = ref(false);
const found = ref(false);
const isPaying = ref(false);
const isLoadingLK = ref(false);
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

// --- STATE CHO HỦY VÉ ---
const showRefundDialog = ref(false);
const refundReason = ref('');
const isRefunding = ref(false);
const refundOtp = ref('');
const refundOtpError = ref('');
const refundOtpSuccess = ref(false);
const isRefundConfirming = ref(false);
const refundPaymentId = ref(null);
const refundOtpCountdown = ref(600);
const refundOtpCountdownDisplay = ref('10:00');
const refundOtpExpired = ref(false);
let refundOtpTimer = null;
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

function exit() {
  const div = document.getElementById('modeledit');
  customer.value = {
    fullName: '',
    gender: null,
    dob: '',
    passport: '',
    email: '',
    phone: '',
    id: null
  };
  div.classList.add('hidden')
}
let isloadingUpdateC = ref();
async function UpdateCustomer() {
  isloadingUpdateC.value = true
  const response = await updateCustomer(customer.value.id, customer.value);
  console.log(response);

  if (response.status === 200 && response.data) {
    window.$toast &&
      window.$toast(response.message || "Cập nhập thành công!", "success");
    isloadingUpdateC.value = false
  } else {
    window.$toast &&
      window.$toast(response.message || "Có lỗi xảy ra, vui lòng thử lại.", "error");
    isloadingUpdateC.value = false
  }

}

const formatDateTime = (dateString) =>
  new Date(dateString).toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });

// Helper function to format remaining time
const formatRemainingTime = (milliseconds) => {
  const days = Math.floor(milliseconds / (24 * 60 * 60 * 1000));
  const hours = Math.floor((milliseconds % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000));
  const minutes = Math.floor((milliseconds % (60 * 60 * 1000)) / (60 * 1000));

  if (days > 0) {
    return `${days} ngày ${hours} giờ ${minutes} phút`;
  } else if (hours > 0) {
    return `${hours} giờ ${minutes} phút`;
  } else {
    return `${minutes} phút`;
  }
};

const getStatusInfo = (status) => {
  switch (status) {
    case "PAID":
      return {
        text: "Đã thanh toán",
        color: "green",
        icon: "fa-solid fa-circle-check",
      };
    case "PENDING_PAYMENT":
      return {
        text: "Chờ thanh toán",
        color: "orange",
        icon: "fa-solid fa-clock",
      };
    case "CANCELLED":
      return { text: "Đã hủy", color: "red", icon: "fa-solid fa-circle-xmark" };
    default:
      return {
        text: status,
        color: "gray",
        icon: "fa-solid fa-circle-question",
      };
  }
};

const getVoucherDiscountText = (voucher) => {
  if (voucher.type === "FIXED_AMOUNT") {
    return `Giảm ${formatPrice(voucher.discountAmount)}`;
  }
  if (voucher.type === "PERCENTAGE") {
    let text = `Giảm ${voucher.discountPercentage}%`;
    if (voucher.maxDiscountAmount) {
      text += ` (tối đa ${formatPrice(voucher.maxDiscountAmount)})`;
    }
    return text;
  }
  return "";
};

const connectWebSocket = () => {

  if (!orderId || (stompClient.value && isSocketConnected.value)) return;

  const socket = new SockJS("http://localhost:8080/ws");
  stompClient.value = Stomp.over(socket);
  stompClient.value.debug = null; // Tắt log không cần thiết

  stompClient.value.connect({}, (frame) => {
    console.log("WebSocket Connected:", frame);
    isSocketConnected.value = true;

    // Kênh riêng cho đơn hàng này (giữ nguyên)
    stompClient.value.subscribe(`/topic/orders/${orderId}`, (response) => {
      handleVoucherResponse(JSON.parse(response.body));
    });

    // MỚI: Lắng nghe trên kênh cập nhật voucher công khai
    stompClient.value.subscribe(`/topic/vouchers/updates`, (response) => {
      const update = JSON.parse(response.body);
      if (update.status === "UNAVAILABLE") {
        console.log(`Voucher ${update.voucherCode} đã hết! Cập nhật UI.`);
        // Tìm voucher trong danh sách gợi ý và vô hiệu hóa nó
        const usedVoucher = suggestedVouchers.value.find(
          (v) => v.code === update.voucherCode
        );
        if (usedVoucher) {
          // Bạn có thể thêm một thuộc tính 'disabled' hoặc xóa nó khỏi danh sách
          usedVoucher.disabled = true;
        }
      }
    });
  });
};

/**
 * Xử lý phản hồi voucher từ server qua WebSocket.
 */
const handleVoucherResponse = (response) => {
  isApplyingVoucher.value = false; // Tắt loading
  if (response.statusCode === 200 && response.data) {
    // Thành công
    order.value = response.data;
    window.$toast &&
      window.$toast(
        response.message || "Áp dụng voucher thành công!",
        "success"
      );
    voucherCode.value = "";
  } else {
    // Thất bại
    window.$toast &&
      window.$toast(
        response.message || "Có lỗi xảy ra, vui lòng thử lại.",
        "error"
      );
  }
};
async function CancelFlightBooking(id) {

  const response = await cancelFlightBooking(id);
  if (response.status === 200) {
    await fetchOrderDetails();
    window.$toast &&
      window.$toast(response.message || "Xóa thành công!", "success");

  } else {
    window.$toast &&
      window.$toast(response.message || "Có lỗi xảy ra, vui lòng thử lại.", "error");
  }

}

onUnmounted(() => {
  if (stompClient.value && isSocketConnected.value) {
    stompClient.value.disconnect();
    console.log("WebSocket Disconnected.");

  }
  clearInterval(timerInterval.value);
  clearInterval(refundOtpTimer);
});

// --- END: Logic WebSocket ---

// --- API CALLS ---
const fetchOrderDetails = async () => {
  isLoading.value = true;
  error.value = null;
  const orderId = route.params.id;
  try {
    const orderResponse = await fetch(
      `http://localhost:8080/api/v1/orders/${orderId}`,
      { headers: { Authorization: getBearerToken() } }
    );
    if (!orderResponse.ok) throw new Error("Không thể tải chi tiết đơn hàng.");

    const data = await orderResponse.json();
    order.value = data.data;
    startCountdown(data.data.expiresAt);
    // THAY ĐỔI: Nếu đơn hàng có thể sửa, kết nối WebSocket và tải voucher
    if (order.value && isEditable.value) {
      connectWebSocket();
      await fetchSuggestedVouchers();
    }

    flightBookingDetails.value = [];
    if (order.value.flightBookings && order.value.flightBookings.length > 0) {
      for (const booking of order.value.flightBookings) {
        try {
          const detailRes = await fetch(
            `http://localhost:8080/api/bookings/flights/reservation-summary/${booking.id}`,
            { headers: { Authorization: getBearerToken() } }
          );
          if (detailRes.ok) {
            const detail = await detailRes.json();
            flightBookingDetails.value.push({ ...detail, showDetail: false });
          }
        } catch (e) {
          console.error(`Lỗi tải chi tiết chuyến bay ${booking.id}:`, e);
        }
      }
    }
  } catch (e) {
    error.value = e.message;
  } finally {
    isLoading.value = false;
  }
};

const fetchSuggestedVouchers = async () => {
  isLoadingVouchers.value = true;
  try {
    const response = await fetch(
      `http://localhost:8080/api/vouchers/suggested?orderAmount=${order.value.amount}`,
      {
        headers: { Authorization: getBearerToken() },
      }
    );
    if (response.ok) {
      suggestedVouchers.value = await response.json();
    }
  } catch (e) {
    console.error("Lỗi tải voucher gợi ý:", e);
  } finally {
    isLoadingVouchers.value = false;
  }
};

onMounted(() => {
  fetchOrderDetails();
  document.addEventListener('click', handleClickOutside);
});




async function onAccountNumberBlur() {
  if (!selectedBank.value || !bankTransfer.accountNumber) {
    resetAccountInfo();
    return;
  }
  isLoadingLK.value = true;
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
      if (order.value?.amount) {
        bankTransfer.amount = order.value.amount;
        validateAmount();
      }
    }
    else notFound.value = true;
  } catch (e) {
    console.error('Lỗi tra cứu tài khoản:', {
      message: e.message,
      name: e.name,
      response: e.response?.data,
      status: e.response?.status
    });

    resetAccountInfo();
    notFound.value = true;
  } finally {
    isLoadingLK.value = false;
  }
}
function validateAmount() {
  amountValidationMessage.value = '';
  const requiredAmount = order.value?.amount;

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
      onAccountNumberBlur();
      order.value.status = "PAID";
      order.value.transactionId = res.data.transactionId;
      window.$toast('Thanh toán hoàn tất!', 'success');
      showOtpDialog.value = false;

      try {
        await markOrderSuccess(orderId, res.data.transactionId);
        console.log(orderId, res.data.transactionId);
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

function startRefundOtpCountdown() {
  refundOtpCountdown.value = 600;
  refundOtpExpired.value = false;
  updateRefundOtpCountdownDisplay();
  if (refundOtpTimer) clearInterval(refundOtpTimer);
  refundOtpTimer = setInterval(() => {
    if (refundOtpCountdown.value > 0) {
      refundOtpCountdown.value--;
      updateRefundOtpCountdownDisplay();
    } else {
      refundOtpExpired.value = true;
      updateRefundOtpCountdownDisplay();
      clearInterval(refundOtpTimer);
    }
  }, 1000);
}

function updateRefundOtpCountdownDisplay() {
  const m = Math.floor(refundOtpCountdown.value / 60);
  const s = refundOtpCountdown.value % 60;
  refundOtpCountdownDisplay.value = `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
}

async function openRefundDialog() {
  // Validate if tickets can be cancelled based on their expiry times
  const validationErrors = [];
  const now = new Date();
  const oneDayInMs = 24 * 60 * 60 * 1000; // 1 day in milliseconds

  
  // Check flight bookings
  if (order.value.flightBookings && order.value.flightBookings.length > 0) {
      console.log("trong if");
    for (const booking of order.value.flightBookings) {
      
      const flightdetail = await getFlightDetail(booking.flightId);
      if (flightdetail.data.departureTime) {
        const departureTime = new Date(flightdetail.data.departureTime);
        const timeUntilDeparture = departureTime.getTime() - now.getTime();
        console.log('Time until departure:', timeUntilDeparture, 'ms');
        console.log(departureTime);
        if (timeUntilDeparture <= oneDayInMs) {
          const remainingTime = formatRemainingTime(timeUntilDeparture);
          validationErrors.push(`Chuyến bay ${flightdetail.data.name} (${flightdetail.data.flightNumber}) khởi hành trong ${remainingTime}. Không thể hủy vé.`);
        }
      }
    }
  }

  // Check hotel bookings
  // if (order.value.hotelBookings && order.value.hotelBookings.length > 0) {
  //   for (const booking of order.value.hotelBookings) {
  //     if (booking.checkInDate) {
  //       const checkInDate = new Date(booking.checkInDate);
  //       const timeUntilCheckIn = checkInDate.getTime() - now.getTime();

  //       if (timeUntilCheckIn <= oneDayInMs) {
  //         const remainingTime = formatRemainingTime(timeUntilCheckIn);
  //         validationErrors.push(`Khách sạn ${booking.hotelName || 'N/A'} nhận phòng trong ${remainingTime}. Không thể hủy đặt phòng.`);
  //       }
  //     }
  //   }
  // }

  // Check tour bookings
  // if (order.value.tourBookings && order.value.tourBookings.length > 0) {
  //   for (const booking of order.value.tourBookings) {
  //     if (booking.departureDate) {
  //       const departureDate = new Date(booking.departureDate);
  //       const timeUntilDeparture = departureDate.getTime() - now.getTime();

  //       if (timeUntilDeparture <= oneDayInMs) {
  //         const remainingTime = formatRemainingTime(timeUntilDeparture);
  //         validationErrors.push(`Tour ${booking.tourName || 'N/A'} khởi hành trong ${remainingTime}. Không thể hủy tour.`);
  //       }
  //     }
  //   }
  // }

  // Check bus bookings (if they exist in the order structure)
  // if (order.value.busBookings && order.value.busBookings.length > 0) {
  //   for (const booking of order.value.busBookings) {
  //     if (booking.busSlot && booking.busSlot.bus && booking.busSlot.bus.departureTime) {
  //       const departureTime = new Date(booking.busSlot.bus.departureTime);
  //       const timeUntilDeparture = departureTime.getTime() - now.getTime();

  //       if (timeUntilDeparture <= oneDayInMs) {
  //         const remainingTime = formatRemainingTime(timeUntilDeparture);
  //         validationErrors.push(`Chuyến xe ${booking.busSlot.bus.name || 'N/A'} khởi hành trong ${remainingTime}. Không thể hủy vé.`);
  //       }
  //     }
  //   }
  // }

  // If there are validation errors, show them and don't open the dialog
  if (validationErrors.length > 0) {
    const errorMessage = validationErrors.join('\n');
    window.$toast && window.$toast(errorMessage, 'error');
    return;
  }

  // If validation passes, open the dialog
  showRefundDialog.value = true;
  refundReason.value = '';
  refundOtp.value = '';
  refundOtpError.value = '';
  refundOtpSuccess.value = false;
  refundPaymentId.value = null;
}

function closeRefundDialog() {
  showRefundDialog.value = false;
  refundReason.value = '';
  refundOtp.value = '';
  refundOtpError.value = '';
  refundOtpSuccess.value = false;
  refundPaymentId.value = null;
  clearInterval(refundOtpTimer);
}

async function submitRefund() {
  if (!refundReason.value.trim()) {
    window.$toast && window.$toast('Vui lòng nhập lý do hủy vé.', 'error');
    return;
  }

  isRefunding.value = true;
  try {
    console.log(order.value);
    
    const res = await refundMake(order.value.transactionId, refundReason.value.trim());

    if (res.data?.paymentId) {
      refundPaymentId.value = res.data.paymentId;
      refundOtp.value = '';
      refundOtpError.value = '';
      refundOtpSuccess.value = false;
      window.$toast('Đã gửi OTP xác nhận hủy vé qua email.', 'success');
    } else {
      window.$toast('Không nhận được paymentId cho giao dịch hủy vé.', 'error');
    }
  } catch (error) {
    console.error('Lỗi khi gửi yêu cầu hủy vé:', error);
    window.$toast('Gửi yêu cầu hủy vé thất bại!', 'error');
  } finally {
    isRefunding.value = false;
  }
}

async function confirmRefundOtp() {
  if (!refundOtp.value || !refundPaymentId.value) return;

  isRefundConfirming.value = true;
  refundOtpError.value = '';
  refundOtpSuccess.value = false;

  try {
    const res = await refundConfirm({
      paymentId: refundPaymentId.value,
      otp: refundOtp.value
    });

    if (res.data?.refundId) {
      refundOtpSuccess.value = true;
      window.$toast('Hủy vé thành công!', 'success');

      // Gọi API backend để cập nhật trạng thái đơn hàng và booking
      try {
        const orderResponse = await fetch(
          `http://localhost:8080/api/v1/orders/${order.value.id}/cancel-after-refund`,
          {
            method: 'PUT',
            headers: {
              'Authorization': getBearerToken(),
              'Content-Type': 'application/json'
            }
          }
        );

        if (orderResponse.ok) {
          console.log('Đã cập nhật trạng thái đơn hàng thành công');
        } else {
          console.error('Lỗi khi cập nhật trạng thái đơn hàng');
        }
      } catch (e) {
        console.error('Lỗi khi gọi API cập nhật trạng thái đơn hàng:', e);
      }

      // Cập nhật thông tin đơn hàng
      try {
        await fetchOrderDetails();
      } catch (e) {
        console.error('Lỗi khi cập nhật thông tin đơn hàng:', e);
      }

      closeRefundDialog();
    } else {
      refundOtpError.value = 'OTP không đúng hoặc đã hết hạn.';
    }
  } catch (error) {
    console.error('Lỗi khi xác nhận OTP hủy vé:', error);
    refundOtpError.value = 'Xác nhận OTP thất bại!';
  } finally {
    isRefundConfirming.value = false;
  }
}

watch(showOtpDialog, val => {
  if (val) startOtpCountdown();
  else clearInterval(otpTimer);
});

watch(showRefundDialog, val => {
  if (val) startRefundOtpCountdown();
  else clearInterval(refundOtpTimer);
});
// --- EVENT HANDLERS ---

/**
 * THAY ĐỔI: Gửi yêu cầu áp dụng voucher qua WebSocket thay vì HTTP fetch.
 */
const handleApplyVoucher = () => {
  const code = voucherCode.value.trim();
  if (!code) {
    window.$toast && window.$toast("Vui lòng nhập mã giảm giá.", "info");
    return;
  }

  if (!stompClient.value || !isSocketConnected.value) {
    window.$toast &&
      window.$toast("Lỗi kết nối real-time. Đang thử kết nối lại...", "error");
    connectWebSocket(); // Thử kết nối lại
    return;
  }

  isApplyingVoucher.value = true;
  const payload = { voucherCode: code };

  stompClient.value.send(
    `/app/orders/${order.value.id}/apply-voucher`,
    {},
    JSON.stringify(payload)
  );
};

const selectVoucher = (code) => {
  voucherCode.value = code;
};

const addMoreServices = () => {
  localStorage.setItem("activeCartId", order.value.id);
  router.push("/");
};

const handleDeleteItem = async (itemId, itemType) => {
  if (!confirm(`Bạn có chắc chắn muốn xóa dịch vụ này khỏi đơn hàng?`)) return;

  processingItemId.value = `${itemType}-${itemId}`;
  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/cart/items?orderId=${order.value.id}&itemId=${itemId}&itemType=${itemType}`,
      { method: "DELETE", headers: { Authorization: getBearerToken() } }
    );
    if (!response.ok) throw new Error("Xóa dịch vụ thất bại.");

    window.$toast && window.$toast("Đã xóa dịch vụ thành công.", "success");
    await fetchOrderDetails();
  } catch (e) {
    window.$toast && window.$toast(e.message, "error");
  } finally {
    processingItemId.value = null;
  }
};

const getPay = () => {
  router.push(`/payment/${order.value.id}`);
};

const handleEditItem = async (item, itemType) => {
  if (
    !confirm(
      `Chức năng này sẽ xóa dịch vụ hiện tại và đưa bạn về trang sản phẩm để chọn lại. Bạn có muốn tiếp tục?`
    )
  )
    return;

  processingItemId.value = `${itemType}-${item.id}`;
  try {
    const deleteResponse = await fetch(
      `http://localhost:8080/api/v1/cart/items?orderId=${order.value.id}&itemId=${item.id}&itemType=${itemType}`,
      { method: "DELETE", headers: { Authorization: getBearerToken() } }
    );
    if (!deleteResponse.ok) throw new Error("Lỗi khi xóa dịch vụ cũ.");

    localStorage.setItem("activeCartId", order.value.id);
    const paths = {
      TOUR: `/tours/${item.tourId}`,
      FLIGHT: `/flights/${item.flightId}`,
      HOTEL: `/hotels/${item.hotelId}`,
    };
    if (paths[itemType]) router.push(paths[itemType]);
  } catch (e) {
    window.$toast && window.$toast(e.message, "error");
  } finally {
    processingItemId.value = null;
  }
};

// --- Hotel Image Slider Functions ---
function nextHotelImage(hotel) {
  if (!hotel.imageUrls || hotel.imageUrls.length <= 1) return;
  if (!(hotel.id in hotelImageIndices)) hotelImageIndices[hotel.id] = 0;
  slideDirectionMap[hotel.id] = "next";
  hotelImageIndices[hotel.id] =
    (hotelImageIndices[hotel.id] + 1) % hotel.imageUrls.length;
}

function prevHotelImage(hotel) {
  if (!hotel.imageUrls || hotel.imageUrls.length <= 1) return;
  if (!(hotel.id in hotelImageIndices)) hotelImageIndices[hotel.id] = 0;
  slideDirectionMap[hotel.id] = "prev";
  hotelImageIndices[hotel.id] =
    (hotelImageIndices[hotel.id] - 1 + hotel.imageUrls.length) %
    hotel.imageUrls.length;
}
</script>

<template class="relative">
  <div class="bg-gray-50 w-full min-h-screen ">
    <div class="container mx-auto px-4 py-10">
      <div v-if="isLoading" class="text-center py-20">
        <i class="fas fa-spinner fa-spin text-4xl text-blue-500"></i>
        <p class="mt-4 text-gray-600">Đang tải chi tiết đơn hàng...</p>
      </div>

      <div v-else-if="error" class="text-center py-20 bg-red-50 p-6 rounded-lg">
        <i class="fas fa-exclamation-triangle text-4xl text-red-500"></i>
        <p class="mt-4 font-semibold text-red-700">Đã xảy ra lỗi</p>
        <p class="text-red-600">{{ error }}</p>
      </div>

      <div v-else-if="order">
        <div class="mb-8 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          <div>
            <div class="flex items-center gap-4">
              <h1 class="text-4xl font-bold text-gray-800">
                Chi tiết Đơn hàng
              </h1>
              <span class="font-semibold text-3xl text-blue-600">#{{ order.id }}</span>
            </div>
            <p class="text-gray-500 mt-1">
              Xem lại thông tin chi tiết cho các dịch vụ bạn đã đặt.
            </p>
          </div>
          <button v-if="isEditable" @click="addMoreServices"
            class="bg-green-600 text-white font-medium px-5 py-2.5 rounded-lg text-sm text-center hover:bg-green-700 transition-colors w-full sm:w-auto">
            <i class="fa-solid fa-plus mr-2"></i> Thêm dịch vụ khác
          </button>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div class="lg:col-span-2 space-y-6">
            <div v-if="order.tourBookings && order.tourBookings.length > 0">
              <h2 class="text-2xl font-bold mb-4 text-gray-700 flex items-center gap-3">
                <i class="fa-solid fa-mountain-sun text-blue-500"></i> Các tour
                đã đặt
              </h2>
              <div v-for="tour in order.tourBookings" :key="'tour-' + tour.id"
                class="bg-white p-6 rounded-xl shadow-lg border border-gray-200 mb-4">
                <div class="flex justify-between items-start">
                  <h3 class="text-xl font-bold text-blue-700 mb-4">
                    {{ tour.tourName }}
                  </h3>
                  <div v-if="isEditable" class="flex items-center gap-2 flex-shrink-0">
                    <button :disabled="processingItemId === `TOUR-${tour.id}`" @click="handleEditItem(tour, 'TOUR')"
                      class="text-sm text-yellow-600 hover:text-yellow-800 disabled:opacity-50">
                      <i :class="processingItemId === `TOUR-${tour.id}`
                        ? 'fas fa-spinner fa-spin'
                        : 'fa-solid fa-pencil'
                        "></i>
                      Sửa
                    </button>
                    <button :disabled="processingItemId === `TOUR-${tour.id}`"
                      @click="handleDeleteItem(tour.id, 'TOUR')"
                      class="text-sm text-red-600 hover:text-red-800 disabled:opacity-50">
                      <i :class="processingItemId === `TOUR-${tour.id}`
                        ? 'fas fa-spinner fa-spin'
                        : 'fa-solid fa-trash'
                        "></i>
                      Xóa
                    </button>
                  </div>
                </div>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-600">
                  <div class="flex items-center gap-3">
                    <i class="fa-solid fa-calendar-days w-5 text-center text-blue-500"></i><span>Ngày khởi hành:
                      <strong>{{
                        formatDate(tour.departureDate)
                      }}</strong></span>
                  </div>
                  <div class="flex items-center gap-3">
                    <i class="fa-solid fa-users w-5 text-center text-blue-500"></i><span>Hành khách:
                      <strong>{{ tour.numberOfAdults }} người lớn,
                        {{ tour.numberOfChildren }} trẻ em</strong></span>
                  </div>
                </div>
                <div class="border-t mt-4 pt-4">
                  <p class="text-lg text-right">
                    Tổng giá tour:
                    <span class="font-bold text-xl text-blue-700">{{
                      formatPrice(tour.totalPrice)
                    }}</span>
                  </p>
                </div>
              </div>
            </div>

            <div v-if="flightBookingDetails.length > 0">
              <h2 class="text-2xl font-bold mb-4 text-gray-700 flex items-center gap-3">
                <i class="fa-solid fa-plane-up text-green-500"></i> Các chuyến
                bay đã đặt
              </h2>
              <div v-for="(flightDetail, idx) in flightBookingDetails" :key="flightDetail.booking.bookingId"
                class="bg-white p-6 rounded-xl shadow-lg border border-gray-200 mb-4">
                <div class="flex justify-between items-start">
                  <h3 class="text-xl font-bold text-green-700 mb-4">
                    {{ flightDetail.booking.flight.name }} -
                    {{ flightDetail.booking.flight.airline?.name }}
                  </h3>
                  <div class="flex items-center gap-2 flex-shrink-0">
                    <button @click="
                      CancelFlightBooking(flightDetail.booking.bookingId)
                      " class="text-sm text-red-600 hover:text-red-800">
                      <i class="fa-solid fa-trash"></i> Xóa
                    </button>
                    <button @click="
                      handleEdittem(flightDetail.customer)
                      " class="text-sm text-blue-600 hover:text-blue-800">
                      <i class="fas fa-pencil-alt"></i> Sửa
                    </button>
                  </div>
                </div>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-600">
                  <div class="flex items-center gap-3">
                    <i class="fa-solid fa-chair w-5 text-center text-indigo-500"></i>
                    <span>Số ghế:
                      <strong>{{
                        flightDetail.flightSlot?.seatNumber
                      }}</strong></span>
                  </div>
                  <div class="flex items-center gap-3">
                    <i class="fa-solid fa-couch w-5 text-center text-yellow-500"></i>
                    <span>Hạng vé:
                      <strong>{{
                        flightDetail.flightSlot?.isBusiness
                          ? "Thương gia"
                          : "Phổ thông"
                      }}</strong></span>
                  </div>
                  <div class="flex items-center gap-3">
                    <i class="fa-solid fa-user w-5 text-center text-blue-500"></i>
                    <span>Khách:
                      <strong>{{ flightDetail.customer?.fullName }}</strong> -
                      <strong>{{ flightDetail.customer?.phone }}</strong></span>
                  </div>
                </div>
                <div class="border-t mt-4 pt-4 flex justify-between items-center">
                  <p class="text-lg">
                    Tổng giá vé:
                    <span class="font-bold text-xl text-green-700">{{
                      formatPrice(flightDetail.booking.totalPrice)
                    }}</span>
                  </p>
                  <button @click="flightDetail.showDetail = !flightDetail.showDetail"
                    class="text-blue-600 hover:underline flex items-center">
                    <i :class="flightDetail.showDetail
                      ? 'fa-solid fa-chevron-up'
                      : 'fa-solid fa-chevron-down'
                      "></i>
                    <span class="ml-1">Chi tiết</span>
                  </button>
                </div>
                <transition name="fade">
                  <div v-if="flightDetail.showDetail" class="mt-4 bg-gray-50 p-4 rounded-lg border border-gray-200">
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                      <div>
                        <span class="font-semibold">Khởi hành:</span>
                        <span>{{
                          formatDateTime(
                            flightDetail.booking.flight.departureTime
                          )
                        }}
                          tại
                          {{
                            flightDetail.booking.flight.departureAirport?.name
                          }}</span>
                      </div>
                      <div>
                        <span class="font-semibold">Hạ cánh:</span>
                        <span>{{
                          formatDateTime(
                            flightDetail.booking.flight.arrivalTime
                          )
                        }}
                          tại
                          {{
                            flightDetail.booking.flight.arrivalAirport?.name
                          }}</span>
                      </div>
                      <div>
                        <span class="font-semibold">Email:</span>
                        {{ flightDetail.customer?.email }}
                      </div>
                      <div>
                        <span class="font-semibold">Passport:</span>
                        {{ flightDetail.customer?.passport }}
                      </div>
                      <div>
                        <span class="font-semibold">Giới tính:</span>
                        {{
                          flightDetail.customer?.gender === true
                            ? "Nam"
                            : flightDetail.customer?.gender === false
                              ? "Nữ"
                              : "Khác"
                        }}
                      </div>
                      <div>
                        <span class="font-semibold">Ngày sinh:</span>
                        {{
                          flightDetail.customer?.dob
                            ? formatDate(flightDetail.customer.dob)
                            : ""
                        }}
                      </div>
                      <div>
                        <span class="font-semibold">Hành lý xách tay:</span>
                        {{ flightDetail.flightSlot?.carryOnLuggage }} kg
                      </div>
                      <div>
                        <span class="font-semibold">Vị trí ghế:</span>
                        {{
                          flightDetail.flightSlot?.isWindow
                            ? "Cửa sổ"
                            : flightDetail.flightSlot?.isAisle
                              ? "Lối đi"
                              : "Khác"
                        }}
                      </div>
                    </div>
                  </div>
                </transition>
              </div>
            </div>

            <div v-if="order.hotelBookings && order.hotelBookings.length > 0">
              <h2 class="text-2xl font-bold mb-4 text-gray-700 flex items-center gap-3">
                <i class="fa-solid fa-hotel text-indigo-500"></i> Các phòng
                khách sạn đã đặt
              </h2>
              <div v-for="hotel in order.hotelBookings" :key="'hotel-' + hotel.id"
                class="bg-gradient-to-br from-indigo-50 to-white p-6 rounded-2xl shadow-xl border border-indigo-100 mb-6 flex flex-col md:flex-row gap-6 items-center md:items-stretch hover:shadow-2xl transition-shadow duration-200 relative">
                <div class="relative flex-shrink-0 flex flex-col items-center">
                  <template v-if="
                    (hotel.imageUrls && hotel.imageUrls.length) ||
                    hotel.imageUrl
                  ">
                    <div
                      class="relative w-40 h-32 md:w-56 md:h-40 flex items-center justify-center overflow-hidden rounded-xl">
                      <transition :name="slideDirectionMap[hotel.id] === 'next'
                        ? 'slide-right'
                        : 'slide-left'
                        ">
                        <img :key="hotel.imageUrls && hotel.imageUrls.length
                          ? hotel.imageUrls[
                          hotelImageIndices[hotel.id] || 0
                          ]
                          : hotel.imageUrl
                          " :src="hotel.imageUrls && hotel.imageUrls.length
                            ? hotel.imageUrls[
                            hotelImageIndices[hotel.id] || 0
                            ]
                            : hotel.imageUrl
                            "
                          class="w-40 h-32 md:w-56 md:h-40 object-cover border-2 border-indigo-200 shadow-md mb-2 absolute left-0 top-0" />
                      </transition>
                      <div v-if="hotel.imageUrls && hotel.imageUrls.length > 1"
                        class="flex gap-2 absolute top-1/2 left-0 right-0 justify-between px-2 -translate-y-1/2 z-10">
                        <button @click="prevHotelImage(hotel)"
                          class="bg-white/80 hover:bg-indigo-100 rounded-full p-1 shadow border border-indigo-200">
                          <i class="fa-solid fa-chevron-left text-indigo-600"></i>
                        </button>
                        <button @click="nextHotelImage(hotel)"
                          class="bg-white/80 hover:bg-indigo-100 rounded-full p-1 shadow border border-indigo-200">
                          <i class="fa-solid fa-chevron-right text-indigo-600"></i>
                        </button>
                      </div>
                    </div>
                  </template>
                </div>
                <div class="flex justify-between items-start">
                  <div class="flex-1 flex flex-col justify-between">
                    <h3 class="text-2xl font-extrabold text-indigo-700 mb-1 flex items-center gap-2">
                      <i class="fa-solid fa-bed text-indigo-400"></i>
                      {{ hotel.hotelName }}
                    </h3>
                    <div class="text-base text-gray-700 font-semibold mb-1 flex items-center gap-2">
                      <i class="fa-solid fa-door-closed text-gray-400"></i>
                      {{ hotel.roomType }} <span class="mx-1">-</span>
                      <span class="text-indigo-600 font-bold">{{
                        hotel.variantName
                      }}</span>
                    </div>
                    <div class="flex flex-wrap gap-4 text-sm text-gray-500 mb-1 mt-2">
                      <div class="flex items-center gap-1">
                        <i class="fa-solid fa-calendar-days text-blue-400"></i>
                        Nhận phòng: <b>{{ formatDate(hotel.checkInDate) }}</b>
                      </div>
                      <div class="flex items-center gap-1">
                        <i class="fa-solid fa-calendar-check text-green-400"></i>
                        Trả phòng: <b>{{ formatDate(hotel.checkOutDate) }}</b>
                      </div>
                    </div>
                    <div class="flex items-center gap-2 text-sm text-gray-600 mt-1">
                      <i class="fa-solid fa-users text-pink-400"></i> Khách:
                      <b>{{ hotel.numAdults }}</b> người lớn,
                      <b>{{ hotel.numChildren }}</b> trẻ em
                    </div>
                    <div class="flex items-center gap-1 mt-1 text-sm text-indigo-700 pt-1">
                      <i class="fa-solid fa-door-open text-indigo-400"></i> Số
                      lượng phòng đã đặt:
                      <b>{{ hotel.numberOfRooms ?? hotel.rooms ?? 1 }}</b>
                    </div>
                    <div class="text-right font-bold text-2xl text-indigo-600 absolute right-6 bottom-3">
                      {{ formatPrice(hotel.totalPrice) }}
                    </div>
                  </div>
                  <div v-if="isEditable" class="flex gap-2 absolute right-6 top-6 z-10">
                    <button @click="handleEditItem(hotel, 'HOTEL')"
                      class="text-sm text-yellow-600 hover:text-yellow-800 flex items-center">
                      <i class="fa-solid fa-pencil mr-1"></i> Sửa
                    </button>
                    <button @click="handleDeleteItem(hotel.id, 'HOTEL')"
                      class="text-sm text-red-600 hover:text-red-800 flex items-center">
                      <i class="fa-solid fa-trash mr-1"></i> Xóa
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="lg:col-span-1">
            <div class="sticky top-8">
              <div class="bg-white p-6 rounded-xl shadow-lg border border-gray-200">
                <div class=" mb-4 border-b flex  justify-between ">
                  <h2 class="text-xl font-semibold">Tóm tắt đơn hàng</h2>
                  <div
                    class="px-2 border border-red-200 bg-red-50 rounded-lg text-center mb-6 flex items-center justify-center gap-2">
                    <p v-if="!hasExpired" class="text-sm text-red-700">Thời gian giữ chỗ còn lại:</p>
                    <p v-else class="text-sm font-semibold text-red-700">Đã hết thời gian giữ chỗ!</p>
                    <p class="text-sm font-mono font-bold text-red-600 tracking-wider mt-1">
                      {{ String(minutes).padStart(2, '0') }}<span class="animate-pulse">:</span>{{
                        String(seconds).padStart(2,
                          '0') }}
                    </p>
                  </div>

                </div>

                <div class="space-y-3 text-gray-600">
                  <div class="flex justify-between items-center">
                    <span>Trạng thái:</span>
                    <span :class="`bg-${getStatusInfo(order.status).color
                      }-100 text-${getStatusInfo(order.status).color}-700`"
                      class="font-medium px-3 py-1 rounded-full text-sm flex items-center gap-2">
                      <i :class="getStatusInfo(order.status).icon"></i>
                      {{ getStatusInfo(order.status).text }}
                    </span>
                  </div>
                  <div class="flex justify-between items-center">
                    <span>Ngày đặt:</span>
                    <strong class="text-gray-800">{{
                      formatDateTime(order.createdAt)
                    }}</strong>
                  </div>
                  <div v-if="order.payDate" class="flex justify-between items-center">
                    <span>Ngày thanh toán:</span>
                    <strong class="text-gray-800">{{
                      formatDateTime(order.payDate)
                    }}</strong>
                  </div>

                  <div v-if="order.voucher" class="flex justify-between items-center">
                    <span>Tạm tính:</span>
                    <strong class="text-gray-800 line-through">{{
                      formatPrice(order.originalAmount)
                    }}</strong>
                  </div>
                  <div v-if="order.voucher" class="flex justify-between items-center text-green-600">
                    <span>Giảm giá ({{ order.voucher.code }}):</span>
                    <strong class="font-semibold">-
                      {{
                        formatPrice(order.originalAmount - order.amount)
                      }}</strong>
                  </div>
                </div>

                <div class="border-t pt-4 mt-4">
                  <div class="flex justify-between items-center text-lg">
                    <span class="font-bold text-gray-800">Tổng cộng</span>
                    <span class="text-2xl font-bold text-blue-600">{{
                      formatPrice(order.amount)
                    }}</span>
                  </div>
                </div>

                <div v-if="isEditable" class="mt-4 pt-4 border-t">
                  <div v-if="isLoadingVouchers" class="text-center text-sm text-gray-500 py-2">
                    <i class="fas fa-spinner fa-spin mr-1"></i> Đang tìm mã giảm
                    giá...
                  </div>
                  <div v-else-if="suggestedVouchers.length > 0 && !order.voucher">
                    <p class="block text-sm font-medium text-gray-700 mb-2">
                      Mã giảm giá cho bạn:
                    </p>
                    <div class="space-y-2 max-h-32 overflow-y-auto pr-2">
                      <button v-for="voucher in suggestedVouchers" :key="voucher.id"
                        @click="selectVoucher(voucher.code)" :disabled="voucher.disabled"
                        class="w-full text-left p-2 border-l-4 rounded-md transition" :class="{
                          'border-green-500 bg-green-50 hover:bg-green-100':
                            !voucher.disabled,
                          'border-gray-300 bg-gray-100 text-gray-400 cursor-not-allowed':
                            voucher.disabled,
                        }">
                        <strong :class="{ 'text-green-700': !voucher.disabled }">{{ voucher.code }}</strong>
                        <p class="text-xs" :class="{ 'text-gray-600': !voucher.disabled }">
                          {{ getVoucherDiscountText(voucher) }}
                        </p>
                        <p v-if="voucher.disabled" class="text-xs font-bold text-red-500">
                          Đã hết lượt sử dụng
                        </p>
                      </button>
                    </div>
                  </div>

                  <div class="flex gap-2 mt-3" v-if="!order.voucher">
                    <input v-model="voucherCode" @keyup.enter="handleApplyVoucher" type="text"
                      class="block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                      placeholder="Hoặc nhập mã khác" />
                    <button @click="handleApplyVoucher" :disabled="isApplyingVoucher"
                      class="flex-shrink-0 bg-gray-800 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-black disabled:opacity-50">
                      <i v-if="isApplyingVoucher" class="fas fa-spinner fa-spin"></i>
                      <span v-else>Áp dụng</span>
                    </button>
                  </div>
                </div>
                <div v-if="isEditable" class="max-w-lg w-full mt-4">


                  <div>
                    <h3 class="text-lg font-semibold mb-4">Thanh toán chuyển khoản</h3>
                    <div class="space-y-6">
                      <div>
                        <label class="block text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2">
                          <i class="fas fa-university text-blue-400"></i> Ngân hàng
                        </label>
                        <div class="relative">
                          <button type="button" @click="showDropdown = !showDropdown"
                            class="w-full border border-gray-300 rounded-lg px-4 py-2 flex items-center focus:outline-none focus:ring-2 focus:ring-indigo-400 bg-white hover:border-indigo-400 transition">
                            <img v-if="selectedBank && selectedBank.logo" :src="selectedBank.logo"
                              class="w-6 h-6 mr-2 object-contain" />
                            <span class="font-medium">{{ selectedBank ? selectedBank.name : 'Chọn ngân hàng'
                            }}</span>
                            <svg class="ml-auto w-4 h-4 text-gray-400" fill="none" stroke="currentColor"
                              viewBox="0 0 24 24">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                d="M19 9l-7 7-7-7" />
                            </svg>
                          </button>
                          <ul v-if="showDropdown"
                            class="absolute z-20 bg-white border w-full mt-1 rounded shadow max-h-60 overflow-auto animate-fade-in">
                            <li v-for="bank in banks" :key="bank.code" @click="selectBank(bank)"
                              class="flex items-center px-4 py-2 hover:bg-indigo-50 cursor-pointer transition">
                              <img v-if="bank.logo" :src="bank.logo" class="w-6 h-6 mr-2 object-contain" />
                              <span>{{ bank.name }}</span>
                            </li>
                          </ul>
                        </div>
                      </div>
                      <div>
                        <label class="block text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2">
                          <i class="fas fa-id-card text-green-400"></i> Số tài khoản
                        </label>
                        <input v-model="bankTransfer.accountNumber" type="text" placeholder="Nhập số tài khoản"
                          maxlength="20"
                          class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-green-400 transition placeholder-gray-400"
                          @blur="onAccountNumberBlur" @input="resetAccountInfo" />
                        <div v-if="isLoadingLK" class="flex items-center mt-2 text-blue-500"><span
                            class="animate-spin rounded-full h-5 w-5 border-t-2 border-b-2 border-blue-400 mr-2"></span>Đang
                          kiểm
                          tra...</div>
                        <div v-if="notFound && !isLoadingLK" class="text-red-500 text-sm mt-2">Không tìm thấy thông
                          tin số tài
                          khoản
                        </div>
                      </div>
                      <div v-if="found"
                        class="bg-indigo-50 rounded-lg p-4 flex flex-col gap-2 border border-indigo-100">
                        <div class="flex items-center gap-2">
                          <i class="fas fa-user-circle text-indigo-400"></i>
                          <span class="font-semibold text-gray-700">Tên tài khoản:</span>
                          <span class="ml-auto font-bold text-indigo-700">{{ bankTransfer.accountName }}</span>
                        </div>
                        <div class="flex items-center gap-2">
                          <i class="fas fa-wallet text-green-400"></i>
                          <span class="font-semibold text-gray-700">Số dư khả dụng:</span>
                          <span class="ml-auto font-bold text-green-600">{{ bankTransfer.availableBalance }} {{
                            bankTransfer.currency
                          }}</span>
                        </div>
                      </div>
                      <div v-if="found">
                        <label class="block text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2">
                          <i class="fas fa-coins text-yellow-400"></i> Số tiền muốn thanh toán
                        </label>
                        <input v-model.number="bankTransfer.amount" type="number" min="0"
                          :max="bankTransfer.availableBalance" placeholder="Nhập số tiền"
                          class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-yellow-400 transition placeholder-gray-400"
                          @input="validateAmount" />
                        <div v-if="amountError" class="text-red-500 text-sm mt-1">{{ amountValidationMessage }}
                        </div>
                      </div>
                      <div class="flex justify-end" v-if="found">
                        <button @click="submitPayment" :disabled="isPaying || amountError || !bankTransfer.amount"
                          class="bg-gradient-to-r from-indigo-500 to-blue-500 hover:from-indigo-600 hover:to-blue-600 text-white px-8 py-2 rounded-lg font-semibold flex items-center shadow-lg transition disabled:opacity-60">
                          <span v-if="isPaying"
                            class="animate-spin rounded-full h-5 w-5 border-t-2 border-b-2 border-white mr-2"></span>
                          <i class="fas fa-paper-plane mr-2"></i> Thanh toán
                        </button>
                      </div>
                    </div>
                  </div>

                  <transition name="modal" appear>
                    <div v-if="showOtpDialog"
                      class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40">
                      <div class="bg-white rounded-2xl shadow-2xl max-w-md w-full overflow-hidden">
                        <div
                          class="bg-gradient-to-r from-indigo-500 to-blue-400 px-6 py-4 flex justify-between items-center">
                          <h3 class="text-white flex items-center gap-2"><i class="fas fa-shield-alt"></i> Xác thực
                            OTP</h3>
                          <button @click="showOtpDialog = false"><i class="fas fa-times text-white"></i></button>
                        </div>
                        <div class="p-6 space-y-4">
                          <p>OTP đã gửi qua email. Nhập để xác nhận thanh toán.</p>
                          <div class="flex items-center gap-2">
                            <i class="fas fa-clock"></i>
                            <span>Thời gian còn lại:</span>
                            <span class="font-mono">{{ otpCountdownDisplay }}</span>
                          </div>
                          <input v-model="otp" maxlength="6" :disabled="otpExpired" placeholder="Nhập OTP"
                            class="w-full border rounded-lg px-4 py-2 text-center" />
                          <div v-if="otpError" class="text-red-500">{{ otpError }}</div>
                          <div v-if="otpSuccess" class="text-green-600">Xác thực thành công!</div>
                          <div v-if="otpExpired" class="text-red-500">OTP đã hết hạn.</div>
                          <div class="flex justify-end gap-2">
                            <button @click="showOtpDialog = false" class="px-4 py-2 rounded bg-gray-200">Hủy</button>
                            <button @click="confirmOtp" :disabled="isConfirming || !otp || otpExpired"
                              class="px-4 py-2 rounded text-white bg-gradient-to-r from-indigo-500 to-blue-500 disabled:opacity-50 flex items-center">
                              <span v-if="isConfirming"
                                class="animate-spin inline-block h-5 w-5 border-t-2 border-white mr-2 rounded-full"></span>
                              Xác nhận
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </transition>

                </div>
                <div v-if="!isEditable && order.status === 'PAID'" class="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
                  <button
                    class="mt-6 w-full bg-gray-800 text-white font-medium py-3 rounded-lg hover:bg-black transition">
                    <i class="fa-solid fa-print mr-2"></i> In hóa đơn
                  </button>
                  <div v-if="canRefund && bookingsCloseToExpiry.length > 0"
                    class="mt-4 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
                    <div class="flex items-start gap-3">
                      <i class="fa-solid fa-exclamation-triangle text-yellow-600 mt-1"></i>
                      <div class="flex-1">
                        <h4 class="text-yellow-800 font-semibold mb-2">
                          ⚠️ Cảnh báo: Một số vé sắp hết hạn hủy
                        </h4>
                        <div class="space-y-2">
                          <div v-for="booking in bookingsCloseToExpiry" :key="`${booking.type}-${booking.name}`"
                            class="text-sm text-yellow-700">
                            <span class="font-medium">
                              {{ booking.type === 'flight' ? '✈️ Chuyến bay:' :
                                booking.type === 'hotel' ? '🏨 Khách sạn:' :
                                  booking.type === 'tour' ? '🏔️ Tour:' : '🚌 Chuyến xe:' }}
                            </span>
                            <span class="font-semibold">{{ booking.name }}</span>
                            <span class="text-yellow-600">(còn {{ booking.remainingTime }})</span>
                          </div>
                        </div>
                        <p class="text-xs text-yellow-600 mt-2">
                          * Vé sẽ không thể hủy khi còn dưới 1 ngày trước khi khởi hành/nhận phòng
                        </p>
                      </div>
                    </div>
                  </div>
                  <button  @click="openRefundDialog"
                    class="mt-6 w-full bg-red-600 text-white font-medium py-3 rounded-lg hover:bg-red-700 transition flex items-center justify-center">
                    <i class="fa-solid fa-times-circle mr-2"></i> Hủy vé
                  </button>
                </div>





              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div id="modeledit" class="w-full h-full absolute inset-0 hidden z-50">
    <div class="absolute inset-0 bg-black opacity-50 z-40"></div>
    <section class="relative w-6/12 m-auto mt-20 bg-white rounded-lg shadow-md p-6 overflow-hidden z-50">
      <div
        class="absolute bottom-4 -right-40 w-34 h-34 bg-sky-300 rounded-full blur-xl [box-shadow:-100px_50px_30px_100px_#7dd3fc] z-0">
      </div>
      <h2 class="relative text-lg font-medium text-gray-700 mb-4 z-60">Thông tin khách hàng</h2>
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-6 relative z-50">
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-1">Họ và tên</label>
          <input v-model="customer.fullName" type="text" placeholder="Nhập họ tên đầy đủ"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-1">Giới tính</label>
          <select v-model="customer.gender"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500">
            <option :value="true">Nam</option>
            <option :value="false">Nữ</option>
            <option :value="null">Khác</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-1">Ngày sinh</label>
          <input v-model="customer.dob" type="date"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-1">Số hộ chiếu</label>
          <input v-model="customer.passport" type="text" placeholder="Nhập số hộ chiếu"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-1">Email</label>
          <input v-model="customer.email" type="email" placeholder="Nhập email"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-1">Số điện thoại</label>
          <input v-model="customer.phone" type="text" placeholder="Nhập số điện thoại"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
        </div>
      </div>
      <div class="mt-6 flex justify-start space-x-4 relative z-60">
        <button @click="UpdateCustomer" :disabled="isloadingUpdateC"
          class="mt-3 px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700 flex items-center justify-center min-w-[100px]">
          <span v-if="isloadingUpdateC"
            class="animate-spin mr-2 w-4 h-4 border-2 border-white border-t-indigo-500 rounded-full"></span>
          <span>{{ isloadingUpdateC ? 'Đang lưu...' : 'Cập nhập' }}</span>
        </button>
        <button @click="exit"
          class="px-6 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition">Hủy</button>
      </div>
    </section>
  </div>

  <!-- Modal hủy vé -->
  <transition name="modal" appear>
    <div v-if="showRefundDialog" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
      <div class="bg-white rounded-2xl shadow-2xl max-w-lg w-full mx-4 overflow-hidden">
        <div class="bg-gradient-to-r from-red-500 to-red-600 px-6 py-4 flex justify-between items-center">
          <h3 class="text-white flex items-center gap-2">
            <i class="fas fa-times-circle"></i> Hủy vé
          </h3>
          <button @click="closeRefundDialog" class="text-white hover:text-red-200">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div class="p-6 space-y-4">
          <!-- Bước 1: Nhập lý do -->
          <div v-if="!refundPaymentId">
            <p class="text-gray-700 mb-4">Vui lòng nhập lý do hủy vé để tiếp tục.</p>
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">
                Lý do hủy vé <span class="text-red-500">*</span>
              </label>
              <textarea v-model="refundReason" placeholder="Nhập lý do hủy vé..." rows="4"
                class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-red-400 transition resize-none"></textarea>
            </div>
            <div class="flex justify-end gap-3 mt-4">
              <button @click="closeRefundDialog"
                class="px-4 py-2 rounded-lg bg-gray-200 text-gray-700 hover:bg-gray-300 transition">
                Hủy
              </button>
              <button @click="submitRefund" :disabled="isRefunding || !refundReason.trim()"
                class="px-6 py-2 rounded-lg bg-red-600 text-white hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center">
                <span v-if="isRefunding"
                  class="animate-spin inline-block h-5 w-5 border-t-2 border-white mr-2 rounded-full"></span>
                <i class="fas fa-paper-plane mr-2"></i>
                Gửi yêu cầu
              </button>
            </div>
          </div>

          <!-- Bước 2: Nhập OTP -->
          <div v-else>
            <p class="text-gray-700 mb-4">OTP đã được gửi qua email. Nhập để xác nhận hủy vé.</p>

            <div class="flex items-center gap-2 mb-4 text-sm text-gray-600">
              <i class="fas fa-clock"></i>
              <span>Thời gian còn lại:</span>
              <span class="font-mono font-bold" :class="refundOtpExpired ? 'text-red-500' : 'text-blue-600'">
                {{ refundOtpCountdownDisplay }}
              </span>
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">
                Mã OTP <span class="text-red-500">*</span>
              </label>
              <input v-model="refundOtp" type="text" maxlength="6" :disabled="refundOtpExpired"
                placeholder="Nhập mã OTP"
                class="w-full border border-gray-300 rounded-lg px-4 py-2 text-center text-lg font-mono focus:outline-none focus:ring-2 focus:ring-red-400 transition disabled:opacity-50" />
            </div>

            <div v-if="refundOtpError" class="text-red-500 text-sm mt-2">
              {{ refundOtpError }}
            </div>

            <div v-if="refundOtpSuccess" class="text-green-600 text-sm mt-2">
              Xác thực thành công!
            </div>

            <div v-if="refundOtpExpired" class="text-red-500 text-sm mt-2">
              OTP đã hết hạn.
            </div>

            <div class="flex justify-end gap-3 mt-4">
              <button @click="closeRefundDialog"
                class="px-4 py-2 rounded-lg bg-gray-200 text-gray-700 hover:bg-gray-300 transition">
                Hủy
              </button>
              <button @click="confirmRefundOtp" :disabled="isRefundConfirming || !refundOtp || refundOtpExpired"
                class="px-6 py-2 rounded-lg bg-red-600 text-white hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center">
                <span v-if="isRefundConfirming"
                  class="animate-spin inline-block h-5 w-5 border-t-2 border-white mr-2 rounded-full"></span>
                <i class="fas fa-check mr-2"></i>
                Xác nhận
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </transition>

  <!--  -->


</template>

<style scoped>
/* Transition for flight detail dropdown */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Transitions for hotel image slider */
.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: absolute;
  width: 100%;
  height: 100%;
}

.slide-left-enter-from {
  transform: translateX(100%);
  opacity: 0.7;
}


.slide-left-leave-to {
  transform: translateX(-100%);
  opacity: 0.7;
}

.slide-right-enter-from {
  transform: translateX(-100%);
  opacity: 0.7;
}

.slide-right-leave-to {
  transform: translateX(100%);
  opacity: 0.7;
}

.animate-fade-in {
  animation: fadeIn 0.4s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.98);
  }

  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

.modal-enter-to,
.modal-leave-from {
  opacity: 1;
  transform: scale(1);
}
</style>
