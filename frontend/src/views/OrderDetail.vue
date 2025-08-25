<script setup>
import {
  ref,
  onMounted,
  computed,
  reactive,
  onUnmounted,
  nextTick,
  watch,
} from "vue";
import { useRoute, useRouter } from "vue-router";

import {
  updateHotelBooking,
  notifyPaymentSuccess,
  notifyHotelCancellation,
} from "@/api/hotelApi";
import {
  updateCustomer,
  cancelFlightBooking,
  getFlightDetail,
} from "@/api/flightApi";
import {
  servicePaymentMake,
  servicePaymentConfirm,
  accountLookup,
  refundMake,
  refundConfirm,
} from "@/api/coreBankingApi";
import SockJS from "sockjs-client/dist/sockjs.min.js";

import { markOrderSuccess } from "@/api/OrderApi";
import { createWebSocketConnection } from "@/utils/webSocketUtils";
import { WS_TOPICS, wsHelpers } from "@/config/webSocketConfig";
import { getBearerToken } from "@/services/TokenService"; // Import getBearerToken
import { BusBookingDetailAPI } from "@/api/busAPI_Client/busbookingApi/busBookingDetailApi"; // ✅ NEW: Import bus booking detail API

const route = useRoute();
const router = useRouter();

// --- STATE MANAGEMENT ---
const order = ref(null);
const flightBookingDetails = ref([]);
const isLoading = ref(true);
const error = ref(null);
const processingItemId = ref(null);

// ✅ NEW: State cho bus booking details
const busBookingDetails = ref(new Map()); // Map<bookingId, BusBookingDetailDto>
const isLoadingBusDetails = ref(new Set()); // Set<bookingId> để track loading state

// --- STATE CHO VOUCHER ---
const voucherCode = ref("");
const isApplyingVoucher = ref(false);
const suggestedVouchers = ref([]);
const isLoadingVouchers = ref(false);
const orderId = route.params.id;
const printInvoice = () => {
  if (order.value && order.value.id) {
    // Xây dựng URL đầy đủ đến API backend để lấy file PDF
    const invoiceUrl = `http://localhost:8080/api/v1/orders/${order.value.id}/invoice`;

    // Dùng window.open để MỞ file PDF từ API trong tab mới, thay vì window.print()
    window.open(invoiceUrl, "_blank");
  } else {
    window.$toast &&
      window.$toast("Không tìm thấy ID đơn hàng để in!", "error");
  }
};
// --- START: NEW STATE FOR VOUCHER PREVIEW ---
const previewedVoucher = reactive({
  code: "",
  discountAmount: 0,
  newTotal: 0,
  error: null,
});
const isAwaitingPaymentAfterVoucher = ref(false); // Flag to chain payment after voucher application
// --- END: NEW STATE FOR VOUCHER PREVIEW ---

// --- STATE CHO WEBSOCKET ---
const isSocketConnected = ref(false);

const timeLeft = ref(0);
const timerInterval = ref(null);
const hasExpired = ref(false);
const minutes = computed(() => Math.floor(timeLeft.value / 60));
const seconds = computed(() => timeLeft.value % 60);

const hotelImageIndices = reactive({});
const slideDirectionMap = reactive({});
const customer = ref({
  fullName: "",
  gender: null,
  dob: "",
  passport: "",
  email: "",
  phone: "",
  id: null,
});

const startCountdown = (expiresAt) => {
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
  {
    code: "VCB",
    name: "Vietcombank",
    logo: "https://hienlaptop.com/wp-content/uploads/2024/12/logo-vietcombank-vector-11.png",
  },
  {
    code: "VIB",
    name: "Vietinbank",
    logo: "https://cdn.haitrieu.com/wp-content/uploads/2022/01/Logo-VietinBank-CTG-Ori.png",
  },
  {
    code: "BIDV",
    name: "BIDV",
    logo: "https://bidv.com.vn/wps/wcm/connect/674b6448-d23b-484e-b4d3-1e86fa68bd0d/Logo+Nguyen+ban+nen+trang.png?MOD=AJPERES&CACHEID=ROOTWORKSPACE-674b6448-d23b-484e-b4d3-1e86fa68bd0d-pfdjkOq",
  },
  {
    code: "Techcombank",
    name: "Techcombank",
    logo: "https://plus.vtc.edu.vn/wp-content/uploads/2020/09/techcombank.png",
  },
  {
    code: "ACB",
    name: "ACB",
    logo: "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d9/Asia_Commercial_Bank_logo.svg/2560px-Asia_Commercial_Bank_logo.svg.png",
  },
  {
    code: "Sacombank",
    name: "Sacombank",
    logo: "https://upload.wikimedia.org/wikipedia/commons/2/2e/Logo-Sacombank-new.png",
  },
  {
    code: "VP Bank",
    name: "VPBank",
    logo: "https://cdn.haitrieu.com/wp-content/uploads/2022/01/Icon-VPBank.png",
  },
  {
    code: "MB Bank",
    name: "MB Bank",
    logo: "https://upload.wikimedia.org/wikipedia/commons/2/25/Logo_MB_new.png",
  },
  {
    code: "TCB",
    name: "TPBank",
    logo: "https://cdn.haitrieu.com/wp-content/uploads/2022/02/Icon-TPBank.png",
  },
  {
    code: "Agri Bank",
    name: "Agribank",
    logo: "https://cdn.haitrieu.com/wp-content/uploads/2022/01/Icon-Agribank.png",
  },
];

const bankTransfer = reactive({
  bankCode: "",
  accountNumber: "",
  accountName: "",
  availableBalance: 0,
  amount: 0,
  currency: "VND",
});

const showEditHotelModal = ref(false);
const editingHotel = ref(null);
const editHotelForm = ref({
  numAdults: 1,
  numChildren: 0,
  rooms: 1,
  totalPrice: 0,
});
const isUpdatingHotel = ref(false);

const showDeleteConfirmModal = ref(false);
const itemToDelete = ref(null);

// --- COMPUTED PROPERTIES ---
const isEditable = computed(() => order.value?.status === "PENDING_PAYMENT");

const finalTotal = computed(() => {
  if (!order.value) return 0;
  // If a voucher is previewed and not yet permanently applied, show the previewed total
  if (!order.value.voucher && previewedVoucher.newTotal > 0) {
    return previewedVoucher.newTotal;
  }
  // Otherwise, show the actual amount from the order object
  return order.value.amount;
});
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
      if (
        booking.flightSlot &&
        booking.flightSlot.flight &&
        booking.flightSlot.flight.departureTime
      ) {
        const departureTime = new Date(booking.flightSlot.flight.departureTime);
        const timeUntilDeparture = departureTime.getTime() - now.getTime();

        if (timeUntilDeparture <= oneDayInMs && timeUntilDeparture > 0) {
          closeBookings.push({
            type: "flight",
            name: `${booking.flightSlot.flight.name} (${booking.flightSlot.flight.flightNumber})`,
            remainingTime: formatRemainingTime(timeUntilDeparture),
            departureTime: booking.flightSlot.flight.departureTime,
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

  // Check tour bookings

  if (order.value.tourBookings && order.value.tourBookings.length > 0) {
    for (const booking of order.value.tourBookings) {
      // 1. Kiểm tra sự tồn tại của đối tượng lồng nhau trước khi truy cập
      if (booking.departure && booking.departure.departureDate) {
        const departureDate = new Date(booking.departure.departureDate);
        const timeUntilDeparture = departureDate.getTime() - now.getTime();

        if (timeUntilDeparture <= oneDayInMs && timeUntilDeparture > 0) {
          // 2. Lấy tên tour từ cấu trúc lồng nhau
          const tourName = booking.departure.tour
            ? booking.departure.tour.name
            : "N/A";

          closeBookings.push({
            type: "tour",
            name: tourName,
            remainingTime: formatRemainingTime(timeUntilDeparture),
            // 3. Lấy đúng đường dẫn của ngày khởi hành
            departureDate: booking.departure.departureDate,
          });
        }
      }
    }
  }

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
  const div = document.getElementById("modeledit");
  customer.value = data;
  div.classList.remove("hidden");
}

const notFound = ref(false);
const found = ref(false);
const isPaying = ref(false);
const isLoadingLK = ref(false);
const showOtpDialog = ref(false);
const paymentId = ref(null);
const otp = ref("");
const isConfirming = ref(false);
const otpError = ref("");
const otpSuccess = ref(false);
const amountError = ref(false);
const amountValidationMessage = ref("");
const otpCountdown = ref(600);
const otpCountdownDisplay = ref("10:00");
const otpExpired = ref(false);
let otpTimer = null;

// --- STATE CHO HỦY VÉ ---
const showRefundDialog = ref(false);
const refundReason = ref("");
const isRefunding = ref(false);
const refundOtp = ref("");
const refundOtpError = ref("");
const refundOtpSuccess = ref(false);
const isRefundConfirming = ref(false);
const refundPaymentId = ref(null);
const refundOtpCountdown = ref(600);
const refundOtpCountdownDisplay = ref("10:00");
const refundOtpExpired = ref(false);
let refundOtpTimer = null;
function resetAccountInfo() {
  bankTransfer.accountName = "";
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
  const dd = document.querySelector(".bank-dropdown");
  if (dd && !dd.contains(e.target)) showDropdown.value = false;
}

function exit() {
  const div = document.getElementById("modeledit");
  customer.value = {
    fullName: "",
    gender: null,
    dob: "",
    passport: "",
    email: "",
    phone: "",
    id: null,
  };
  div.classList.add("hidden");
}

let isloadingUpdateC = ref();
async function UpdateCustomer() {
  isloadingUpdateC.value = true;
  const response = await updateCustomer(customer.value.id, customer.value);
  if (response.status === 200 && response.data) {
    window.$toast &&
      window.$toast(response.message || "Cập nhập thành công!", "success");
    isloadingUpdateC.value = false;
  } else {
    window.$toast &&
      window.$toast(
        response.message || "Có lỗi xảy ra, vui lòng thử lại.",
        "error"
      );
    isloadingUpdateC.value = false;
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
  const hours = Math.floor(
    (milliseconds % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
  );
  const minutes = Math.floor((milliseconds % (60 * 60 * 1000)) / (60 * 1000));

  if (days > 0) {
    return `${days} ngày ${hours} giờ ${minutes} phút`;
  } else if (hours > 0) {
    return `${hours} giờ ${minutes} phút`;
  } else {
    return `${minutes} phút`;
  }
};

// ✅ NEW: Format time for bus slots
const formatTime = (timeString) => {
  if (!timeString) return "--:--";
  return timeString.substring(0, 5); // Extract HH:MM from time string
};

// ✅ NEW: Calculate duration between departure and arrival
const calculateDuration = (departureTime, arrivalTime) => {
  if (!departureTime || !arrivalTime) return "N/A";

  try {
    const departure = new Date(`2000-01-01T${departureTime}`);
    const arrival = new Date(`2000-01-01T${arrivalTime}`);

    // Handle overnight trips
    if (arrival < departure) {
      arrival.setDate(arrival.getDate() + 1);
    }

    const diffMs = arrival - departure;
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));

    if (diffHours > 0) {
      return `${diffHours}h ${diffMinutes}m`;
    } else {
      return `${diffMinutes}m`;
    }
  } catch (error) {
    return "N/A";
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

// ✅ Sử dụng WebSocket utility chung
const wsConnection = ref(null);

const connectWebSocket = () => {
  if (!orderId || (wsConnection.value && isSocketConnected.value)) return;

  wsConnection.value = createWebSocketConnection({
    componentName: "OrderDetail",
    onConnect: (frame) => {
      wsHelpers.log("Connected to Order system", frame);
      isSocketConnected.value = true;

      // ✅ Subscribe using config topics
      wsConnection.value.subscribe(
        WS_TOPICS.ORDER.ORDER_UPDATES(orderId),
        handleVoucherResponse
      );

      // Subscribe to voucher updates
      wsConnection.value.subscribe(
        WS_TOPICS.ORDER.VOUCHER_UPDATES,
        (update) => {
          if (update.status === "UNAVAILABLE") {
            console.log(`Voucher ${update.voucherCode} đã hết! Cập nhật UI.`);
            const usedVoucher = suggestedVouchers.value.find(
              (v) => v.code === update.voucherCode
            );
            if (usedVoucher) {
              usedVoucher.disabled = true;
            }
          }
        }
      );
    },
    onError: (error) => {
      wsHelpers.logError("Order WebSocket connection error", error);
      isSocketConnected.value = false;
    },
  });

  wsConnection.value.connect();
};

/**
 * Handles voucher responses from the server.
 * This is now also responsible for triggering the payment flow after a successful voucher application.
 */
const handleVoucherResponse = (response) => {
  isApplyingVoucher.value = false;
  if (response.statusCode === 200 && response.data) {
    order.value = response.data;
    bankTransfer.amount = order.value.amount;
    validateAmount();
    window.$toast && window.$toast("Áp dụng voucher thành công!", "success");

    // If payment was waiting for this, proceed now.
    if (isAwaitingPaymentAfterVoucher.value) {
      isAwaitingPaymentAfterVoucher.value = false;
      window.$toast &&
        window.$toast("Đang tiếp tục đến thanh toán...", "success");
      nextTick(() => {
        proceedToPaymentGateway();
      });
    }
  } else {
    window.$toast &&
      window.$toast(
        response.message || "Có lỗi xảy ra, vui lòng thử lại.",
        "error"
      );
    // If the process fails, stop the payment flow.
    if (isAwaitingPaymentAfterVoucher.value) {
      isAwaitingPaymentAfterVoucher.value = false;
      isPaying.value = false;
    }
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
      window.$toast(
        response.message || "Có lỗi xảy ra, vui lòng thử lại.",
        "error"
      );
  }
}

onUnmounted(() => {
  if (wsConnection.value && isSocketConnected.value) {
    wsConnection.value.disconnect();
  }
  clearInterval(timerInterval.value);
  clearInterval(refundOtpTimer);
});

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

    if (order.value && order.value.status !== "PENDING_PAYMENT") {
      const activeCartId = localStorage.getItem("activeCartId");
      if (activeCartId && parseInt(activeCartId) === order.value.id) {
        localStorage.removeItem("activeCartId");
      }
    }
    startCountdown(data.data.expiresAt);
    if (order.value && isEditable.value) {
      connectWebSocket();
      await fetchSuggestedVouchers();
    }

    flightBookingDetails.value = [];
    if (order.value.flightBookings && order.value.flightBookings.length > 0) {
      for (const booking of order.value.flightBookings) {
        const detailRes = await fetch(
          `http://localhost:8080/api/bookings/flights/reservation-summary/${booking.id}`,
          { headers: { Authorization: getBearerToken() } }
        );
        if (detailRes.ok) {
          const detail = await detailRes.json();
          flightBookingDetails.value.push({ ...detail, showDetail: false });
        }
      }
    }

    // ✅ NEW: Fetch bus booking details
    if (order.value.busBookings && order.value.busBookings.length > 0) {
      await fetchBusBookingDetails();
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
      { headers: { Authorization: getBearerToken() } }
    );
    if (response.ok) {
      suggestedVouchers.value = await response.json();
    }
  } catch (e) {
    // Silent error handling
  } finally {
    isLoadingVouchers.value = false;
  }
};

onMounted(() => {
  fetchOrderDetails();
  document.addEventListener("click", handleClickOutside);
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
      accountNumber: bankTransfer.accountNumber,
    });
    bankTransfer.accountName = res.data.accountHolderName || "";
    bankTransfer.availableBalance = res.data.availableBalance || 0;
    bankTransfer.currency = res.data.currency || "VND";
    if (bankTransfer.accountName) {
      found.value = true;
      if (order.value?.amount) {
        // Use the finalTotal computed property which accounts for previews
        bankTransfer.amount = finalTotal.value;
        validateAmount();
      }
    } else notFound.value = true;
  } catch (e) {
    resetAccountInfo();
    notFound.value = true;
  } finally {
    isLoadingLK.value = false;
  }
}

function validateAmount() {
  amountValidationMessage.value = "";
  // Use finalTotal computed property for validation
  const requiredAmount = finalTotal.value;

  if (!requiredAmount) {
    amountError.value = true;
    amountValidationMessage.value = "Chưa có thông tin số tiền cần thanh toán.";
    return;
  }

  if (bankTransfer.amount !== requiredAmount) {
    amountError.value = true;
    amountValidationMessage.value = `Vui lòng nhập đúng số tiền thanh toán: ${formatPrice(
      requiredAmount
    )}`;
  } else if (bankTransfer.amount > bankTransfer.availableBalance) {
    amountError.value = true;
    amountValidationMessage.value = `Số dư không đủ. Cần ${formatPrice(
      requiredAmount
    )}.`;
  } else {
    amountError.value = false;
  }
}
// ✅ NEW: Fetch bus booking details
const fetchBusBookingDetails = async () => {
  if (!order.value?.busBookings) return;

  for (const busBooking of order.value.busBookings) {
    if (!busBookingDetails.value.has(busBooking.id)) {
      await fetchBusBookingDetail(busBooking.id);
    }
  }
};

// ✅ NEW: Fetch single bus booking detail
const fetchBusBookingDetail = async (bookingId) => {
  if (isLoadingBusDetails.value.has(bookingId)) return; // Prevent duplicate requests

  isLoadingBusDetails.value.add(bookingId);
  try {
    const response = await BusBookingDetailAPI.getBookingDetailForDisplay(
      bookingId
    );

    if (response.data) {
      busBookingDetails.value.set(bookingId, response.data);
    }
  } catch (error) {
    // Don't throw error to prevent breaking the page
  } finally {
    isLoadingBusDetails.value.delete(bookingId);
  }
};

onMounted(fetchOrderDetails);

/**
 * Contains the core logic for making the payment after all checks and voucher applications are done.
 */
async function proceedToPaymentGateway() {
  try {
    const res = await servicePaymentMake({
      customerAccountNumber: bankTransfer.accountNumber,
      customerBankCode: selectedBank.value.code,
      amount: bankTransfer.amount,
      currency: bankTransfer.currency,
      remittanceInfo: "Thanh toán dịch vụ",
      idempotencyKey: Date.now().toString(),
    });
    if (res.data?.paymentId) {
      paymentId.value = res.data.paymentId;
      showOtpDialog.value = true;
      otp.value = "";
      otpError.value = "";
      otpSuccess.value = false;
      window.$toast("Đã gửi OTP qua email.", "success");
    } else {
      window.$toast("Không nhận được paymentId.", "error");
      isPaying.value = false; // Stop loading on failure
    }
  } catch {
    window.$toast("Gửi thanh toán thất bại!", "error");
    isPaying.value = false; // Stop loading on failure
  }
}

/**
 * The main payment handler. It orchestrates applying the voucher first, then paying.
 */
async function submitPayment() {
  validateAmount();
  if (amountError.value || !bankTransfer.amount) return;

  isPaying.value = true;

  // ✅ DEBUG: Log current state
  console.log("💰 submitPayment called with current state:", {
    voucherCode: voucherCode.value,
    previewedVoucher: { ...previewedVoucher },
    orderVoucher: order.value?.voucher
  });

  // If a valid voucher is being previewed, apply it for real first.
  if (
    previewedVoucher.code &&
    previewedVoucher.code.trim() !== "" &&
    previewedVoucher.discountAmount > 0 &&
    !order.value.voucher
  ) {
    console.log("📋 Attempting to apply voucher:", previewedVoucher.code);

    if (!wsConnection.value || !isSocketConnected.value) {
      window.$toast("Lỗi kết nối real-time. Vui lòng thử lại.", "error");
      isPaying.value = false;
      return;
    }

    // ✅ Double check voucher code before sending
    if (!previewedVoucher.code || previewedVoucher.code.trim() === "") {
      window.$toast("Mã voucher không hợp lệ. Vui lòng thử lại.", "error");
      isPaying.value = false;
      return;
    }

    isAwaitingPaymentAfterVoucher.value = true;
    isApplyingVoucher.value = true; // Show loading state

    console.log("🚀 Sending voucher application via WebSocket:", {
      orderId: order.value.id,
      voucherCode: previewedVoucher.code
    });

    wsConnection.value.send(
      `/app/orders/${order.value.id}/apply-voucher`,
      { voucherCode: previewedVoucher.code.trim() }
    );
    // The flow will now wait for handleVoucherResponse to continue.
  } else {
    // No voucher to apply, go straight to payment.
    await proceedToPaymentGateway();
  }
}
async function confirmOtp() {
  console.log("=== BẮT ĐẦU XÁC NHẬN OTP ===");
  console.log("Giá trị OTP:", otp.value);
  console.log("Giá trị Payment ID:", paymentId.value);

  if (!otp.value || !paymentId.value) {
    console.warn("OTP hoặc Payment ID không tồn tại => Dừng xử lý");
    return;
  }

  isConfirming.value = true;
  otpError.value = "";
  otpSuccess.value = false;
  console.log("isConfirming:", isConfirming.value);

  try {
    console.log("Gọi API servicePaymentConfirm với:", {
      paymentId: paymentId.value,
      otp: otp.value,
    });

    const res = await servicePaymentConfirm({
      paymentId: paymentId.value,
      otp: otp.value,
    });

    console.log("Kết quả trả về từ servicePaymentConfirm:", res);

    if (res.data?.transactionId) {
      console.log("Có transactionId:", res.data.transactionId);

      otpSuccess.value = true;
      console.log("otpSuccess:", otpSuccess.value);

      console.log("Gọi hàm onAccountNumberBlur()");
      onAccountNumberBlur();

      order.value.status = "PAID";
      console.log("Trạng thái order:", order.value.status);

      window.$toast("Thanh toán hoàn tất!", "success");

      order.value.transactionId = res.data.transactionId;
      console.log("Gán transactionId vào order:", order.value.transactionId);

      showOtpDialog.value = false;
      console.log("Ẩn dialog OTP:", showOtpDialog.value);

      try {
        console.log(
          "Cập nhật trạng thái đơn hàng:",
          orderId,
          res.data.transactionId
        );
        await markOrderSuccess(orderId, res.data.transactionId);
        console.log("Đã cập nhật trạng thái đơn hàng thành công");

        console.log("Thông báo thành công tới người dùng");
        await notifyPaymentSuccess(orderId, order.value.amount);
        console.log("Đã gửi thông báo thành công");
      } catch (e) {
        console.error("Lỗi khi cập nhật trạng thái đơn hàng:", e);
        window.$toast(
          e.message || "Cập nhật trạng thái đơn hàng thất bại!",
          "error"
        );
      }
    } else {
      console.warn("OTP không đúng hoặc đã hết hạn");
      otpError.value = "OTP không đúng hoặc đã hết hạn.";
    }
  } catch (err) {
    console.error("Lỗi khi xác nhận OTP:", err);
    otpError.value = "Xác nhận OTP thất bại!";
  } finally {
    isConfirming.value = false;
    isPaying.value = false;
    console.log(
      "Hoàn tất confirmOtp | isConfirming:",
      isConfirming.value,
      " | isPaying:",
      isPaying.value
    );
    console.log("=== KẾT THÚC XÁC NHẬN OTP ===");
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
      clearInterval(otpTimer);
    }
  }, 1000);
}

function updateOtpCountdownDisplay() {
  const m = Math.floor(otpCountdown.value / 60);
  const s = otpCountdown.value % 60;
  otpCountdownDisplay.value = `${m.toString().padStart(2, "0")}:${s
    .toString()
    .padStart(2, "0")}`;
}

watch(showOtpDialog, (val) => {
  if (val) startOtpCountdown();
  else clearInterval(otpTimer);
});

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
  refundOtpCountdownDisplay.value = `${m.toString().padStart(2, "0")}:${s
    .toString()
    .padStart(2, "0")}`;
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
        console.log("Time until departure:", timeUntilDeparture, "ms");
        console.log(departureTime);
        if (timeUntilDeparture <= oneDayInMs) {
          const remainingTime = formatRemainingTime(timeUntilDeparture);
          validationErrors.push(
            `Chuyến bay ${flightdetail.data.name} (${flightdetail.data.flightNumber}) khởi hành trong ${remainingTime}. Không thể hủy vé.`
          );
        }
      }
    }
  }

  if (order.value.hotelBookings && order.value.hotelBookings.length > 0) {
    for (const booking of order.value.hotelBookings) {
      if (booking.checkInDate) {
        const checkInDate = new Date(booking.checkInDate);
        const timeUntilCheckIn = checkInDate.getTime() - now.getTime();
        if (timeUntilCheckIn <= oneDayInMs) {
          const remainingTime = formatRemainingTime(timeUntilCheckIn);
          validationErrors.push(`Khách sạn ${booking.hotelName || 'N/A'} nhận phòng trong ${remainingTime}. Không thể hủy đặt phòng.`);
        }
      }     }
      }

  if (order.value.tourBookings && order.value.tourBookings.length > 0) {
    for (const booking of order.value.tourBookings) {
      if (booking.departureDate) {
        const departureDate = new Date(booking.departureDate);
        const timeUntilDeparture = departureDate.getTime() - now.getTime();

        if (timeUntilDeparture <= oneDayInMs) {
          const remainingTime = formatRemainingTime(timeUntilDeparture);


          const tourName = booking.tourName || "N/A";

          validationErrors.push(
            `Tour ${tourName} khởi hành trong ${remainingTime}. Không thể hủy tour.`
          );
        }

      }
    }
  }

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
    const errorMessage = validationErrors.join("\n");
    window.$toast && window.$toast(errorMessage, "error");
    return;
  }
  console.log("Không có lỗi, mở dialog hủy vé");
  // If validation passes, open the dialog
  showRefundDialog.value = true;
  refundReason.value = "";
  refundOtp.value = "";
  refundOtpError.value = "";
  refundOtpSuccess.value = false;
  refundPaymentId.value = null;
}

function closeRefundDialog() {
  showRefundDialog.value = false;
  refundReason.value = "";
  refundOtp.value = "";
  refundOtpError.value = "";
  refundOtpSuccess.value = false;
  refundPaymentId.value = null;
  clearInterval(refundOtpTimer);
}

async function submitRefund() {
  if (!refundReason.value.trim()) {
    window.$toast && window.$toast("Vui lòng nhập lý do hủy vé.", "error");
    return;
  }

  isRefunding.value = true;
  try {
    console.log(order.value);

    const res = await refundMake(
      order.value.transactionId,
      refundReason.value.trim()
    );

    if (res.data?.paymentId) {
      refundPaymentId.value = res.data.paymentId;
      refundOtp.value = "";
      refundOtpError.value = "";
      refundOtpSuccess.value = false;
      window.$toast("Đã gửi OTP xác nhận hủy vé qua email.", "success");
    } else {
      window.$toast("Không nhận được paymentId cho giao dịch hủy vé.", "error");
    }
  } catch (error) {
    console.error("Lỗi khi gửi yêu cầu hủy vé:", error);
    window.$toast("Gửi yêu cầu hủy vé thất bại!", "error");
  } finally {
    isRefunding.value = false;
  }
}

async function confirmRefundOtp() {
  if (!refundOtp.value || !refundPaymentId.value) return;

  isRefundConfirming.value = true;
  refundOtpError.value = "";
  refundOtpSuccess.value = false;

  try {
    const res = await refundConfirm({
      paymentId: refundPaymentId.value,
      otp: refundOtp.value,
    });

    if (res.data?.refundId) {
      refundOtpSuccess.value = true;
      window.$toast("Hủy vé thành công!", "success");

      // Gọi API backend để cập nhật trạng thái đơn hàng và booking
      try {
        const orderResponse = await fetch(
          `http://localhost:8080/api/v1/orders/${order.value.id}/cancel-after-refund`,
          {
            method: "PUT",
            headers: {
              Authorization: getBearerToken(),
              "Content-Type": "application/json",
            },
          }
        );

        if (orderResponse.ok) {
          console.log("Đã cập nhật trạng thái đơn hàng thành công");
        } else {
          console.error("Lỗi khi cập nhật trạng thái đơn hàng");
        }
      } catch (e) {
        console.error("Lỗi khi gọi API cập nhật trạng thái đơn hàng:", e);
      }

      // Cập nhật thông tin đơn hàng
      try {
        await fetchOrderDetails();
      } catch (e) {
        console.error("Lỗi khi cập nhật thông tin đơn hàng:", e);
      }

      closeRefundDialog();
    } else {
      refundOtpError.value = "OTP không đúng hoặc đã hết hạn.";
    }
  } catch (error) {
    console.error("Lỗi khi xác nhận OTP hủy vé:", error);
    refundOtpError.value = "Xác nhận OTP thất bại!";
  } finally {
    isRefundConfirming.value = false;
  }
}

watch(showOtpDialog, (val) => {
  if (val) startOtpCountdown();
  else clearInterval(otpTimer);
});

watch(showRefundDialog, (val) => {
  if (val) startRefundOtpCountdown();
  else clearInterval(refundOtpTimer);
});
// --- EVENT HANDLERS ---

/**
 * Calculates the discount for preview purposes without calling the server.
 */
const previewVoucherDiscount = () => {
  const code = voucherCode.value.trim().toUpperCase();
  console.log("🎯 Preview voucher - original input:", voucherCode.value);
  console.log("🎯 Preview voucher - processed code:", code);

  if (!code) {
    previewedVoucher.code = "";
    previewedVoucher.discountAmount = 0;
    previewedVoucher.newTotal = 0;
    previewedVoucher.error = null;
    return;
  }

  const voucherData = suggestedVouchers.value.find(
    (v) => v.code.toUpperCase() === code && !v.disabled
  );
  if (!voucherData) {
    previewedVoucher.error =
      "Mã voucher không hợp lệ hoặc đã hết lượt sử dụng.";
    previewedVoucher.code = code;
    previewedVoucher.discountAmount = 0;
    previewedVoucher.newTotal = 0;
    window.$toast(previewedVoucher.error, "error");
    return;
  }

  let discount = 0;
  const originalAmount = order.value.originalAmount || order.value.amount;

  if (voucherData.type === "FIXED_AMOUNT") {
    discount = voucherData.discountAmount;
  } else if (voucherData.type === "PERCENTAGE") {
    discount = originalAmount * (voucherData.discountPercentage / 100);
    if (
      voucherData.maxDiscountAmount &&
      discount > voucherData.maxDiscountAmount
    ) {
      discount = voucherData.maxDiscountAmount;
    }
  }

  discount = Math.min(discount, originalAmount);
  previewedVoucher.code = code;
  previewedVoucher.discountAmount = discount;
  previewedVoucher.newTotal = originalAmount - discount;
  previewedVoucher.error = null;

  console.log("✅ Voucher preview set successfully:", {
    code: previewedVoucher.code,
    discountAmount: previewedVoucher.discountAmount,
    newTotal: previewedVoucher.newTotal
  });

  // Update the payment amount field automatically for better UX
  bankTransfer.amount = previewedVoucher.newTotal;
  validateAmount();

  window.$toast(
    "Đã tính thử giảm giá. Tổng mới: " + formatPrice(previewedVoucher.newTotal),
    "info"
  );
};

const selectVoucher = (code) => {
  voucherCode.value = code;
  previewVoucherDiscount(); // Preview automatically when selected
};

const addMoreServices = () => {
  localStorage.setItem("activeCartId", order.value.id);
  router.push("/");
};

const openDeleteConfirmModal = (itemId, itemType) => {
  itemToDelete.value = { id: itemId, type: itemType };
  showDeleteConfirmModal.value = true;
};

const closeDeleteConfirmModal = () => {
  showDeleteConfirmModal.value = false;
  itemToDelete.value = null;
};
// ✅ NEW: Cancel bus booking before removing from cart
const cancelBusBooking = async (busBookingId) => {
  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/bus-booking/${busBookingId}/cancel`,
      {
        method: "POST",
        headers: {
          Authorization: getBearerToken(),
          "Content-Type": "application/json",
        },
      }
    );

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "Không thể hủy vé xe.");
    }

    const result = await response.json();
    return result;
  } catch (error) {
    throw error;
  }
};

const handleDeleteItem = async () => {
  if (!itemToDelete.value) return;

  const { id: itemId, type: itemType } = itemToDelete.value;
  processingItemId.value = `${itemType}-${itemId}`;

  try {
    // ✅ STEP 1: Nếu là BUS, cancel booking trước để release seats
    if (itemType === "BUS") {
      try {
        await cancelBusBooking(itemId);
      } catch (error) {
        // ✅ Handle case where booking is already cancelled/expired
        if (
          error.message &&
          (error.message.includes("already cancelled") ||
            error.message.includes("already expired"))
        ) {
        } else {
          throw error; // Re-throw other errors
        }
      }
    }

    // ✅ STEP 2: Xóa item khỏi cart
    const response = await fetch(
      `http://localhost:8080/api/v1/cart/items?orderId=${order.value.id}&itemId=${itemId}&itemType=${itemType}`,
      { method: "DELETE", headers: { Authorization: getBearerToken() } }
    );
    if (!response.ok) throw new Error("Xóa dịch vụ thất bại.");

    if (itemType === "HOTEL") {
      try {
        await notifyHotelCancellation(order.value.id, itemId);
      } catch (e) {
        console.error("Failed to send cancellation notification:", e);
      }
    }

    window.$toast && window.$toast("Đã xóa dịch vụ thành công.", "success");
    // ✅ Success messages theo loại service
    const successMessages = {

      'BUS': '🚌 Đã hủy vé xe và xóa khỏi đơn hàng thành công! Ghế đã được giải phóng.',
      'TOUR': '🏔️ Đã xóa tour khỏi đơn hàng thành công!',
      'FLIGHT': '✈️ Đã xóa chuyến bay khỏi đơn hàng thành công!',
      'HOTEL': '🏨 Đã hủy đặt phòng khách sạn và xóa khỏi đơn hàng thành công! Phòng đã được hoàn trả.'

    };

    const message = successMessages[itemType] || "Đã xóa dịch vụ thành công.";
    window.$toast && window.$toast(message, "success");

    // ✅ Clear activeCartId từ localStorage nếu đơn hàng này là cart hiện tại
    const activeCartId = localStorage.getItem("activeCartId");
    if (activeCartId && parseInt(activeCartId) === order.value.id) {
      localStorage.removeItem("activeCartId");
    }

    await fetchOrderDetails();
  } catch (e) {
    window.$toast && window.$toast(e.message, "error");
  } finally {
    processingItemId.value = null;
    closeDeleteConfirmModal();
  }
};

const handleDeleteTour = async (tourId) => {
  if (!confirm("Bạn có chắc chắn muốn xóa tour này khỏi đơn hàng?")) {
    return;
  }

  processingItemId.value = `TOUR-${tourId}`;

  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/cart/items?orderId=${order.value.id}&itemId=${tourId}&itemType=TOUR`,
      { method: "DELETE", headers: { Authorization: getBearerToken() } }
    );
    if (!response.ok) throw new Error("Xóa tour thất bại.");

    window.$toast && window.$toast("Đã xóa tour thành công.", "success");
    await fetchOrderDetails();
  } catch (e) {
    window.$toast && window.$toast(e.message, "error");
  } finally {
    processingItemId.value = null;
  }
};

const showEditTourModal = ref(false);
const editingTourItem = ref(null);
const openEditTourModal = (item) => {
  console.log("Dữ liệu tour được truyền vào modal:", item);
  editingTourItem.value = { ...item };
  showEditTourModal.value = true;
};
const closeEditTourModal = () => {
  showEditTourModal.value = false;
  editingTourItem.value = null;
};

const handleUpdateTour = async () => {
  if (!editingTourItem.value) return;

  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/bookings/tours/${editingTourItem.value.id}`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: getBearerToken(),
        },
        body: JSON.stringify({
          customerName: editingTourItem.value.customerName,
          phone: editingTourItem.value.phone,
          email: editingTourItem.value.email,
          numberOfAdults: editingTourItem.value.numberOfAdults,
          numberOfChildren: editingTourItem.value.numberOfChildren,
          notes: editingTourItem.value.notes,
        }),
      }
    );

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "Cập nhật thông tin tour thất bại.");
    }

    window.$toast && window.$toast("Cập nhật thành công!", "success");
    closeEditTourModal();

    await fetchOrderDetails();
  } catch (e) {
    window.$toast && window.$toast(e.message, "error");
  }
};

const handleEditItem = async (item, itemType) => {
  if (itemType === "HOTEL") {
    openEditHotelModal(item);
    return;
  }
  if (itemType === "TOUR") {
    openEditTourModal(item);
    return;
  }

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
      BUS: `/bus`, // ✅ Route cho bus booking
    };
    if (paths[itemType]) router.push(paths[itemType]);
  } catch (e) {
    window.$toast && window.$toast(e.message, "error");
  } finally {
    processingItemId.value = null;
  }
};

const openEditHotelModal = (hotel) => {
  editingHotel.value = hotel;
  editHotelForm.value = {
    numAdults: hotel.numAdults || 1,
    numChildren: hotel.numChildren || 0,
    rooms: hotel.rooms || hotel.numberOfRooms || 1,
    totalPrice: hotel.totalPrice || 0,
  };
  showEditHotelModal.value = true;
};

const closeEditHotelModal = () => {
  showEditHotelModal.value = false;
  editingHotel.value = null;
  editHotelForm.value = {
    numAdults: 1,
    numChildren: 0,
    rooms: 1,
    totalPrice: 0,
  };
};

const updateHotelBookingData = async () => {
  if (!editingHotel.value) return;
  if (
    !editHotelForm.value.rooms ||
    editHotelForm.value.rooms === "" ||
    editHotelForm.value.rooms === null ||
    editHotelForm.value.rooms === undefined
  ) {
    window.$toast && window.$toast("Vui lòng nhập số phòng!", "error");
    return;
  }
  const rooms = parseInt(editHotelForm.value.rooms);

  if (isNaN(rooms) || rooms < 1) {
    window.$toast &&
      window.$toast("Số phòng phải là số và ít nhất là 1!", "error");
    return;
  }

  if (editHotelForm.value.numAdults < 1) {
    window.$toast && window.$toast("Số người lớn phải ít nhất là 1!", "error");
    return;
  }

  if (rooms > editHotelForm.value.numAdults) {
    window.$toast &&
      window.$toast("Số phòng không thể nhiều hơn số người lớn!", "error");
    return;
  }

  if (
    editHotelForm.value.numAdults + editHotelForm.value.numChildren >
    4 * rooms
  ) {
    window.$toast &&
      window.$toast(
        "Tổng số người không được vượt quá 4 người/phòng!",
        "error"
      );
    return;
  }

  isUpdatingHotel.value = true;
  try {
    const response = await updateHotelBooking({
      bookingId: editingHotel.value.id,
      numAdults: editHotelForm.value.numAdults,
      numChildren: editHotelForm.value.numChildren,
      rooms: rooms,
      totalPrice: editHotelForm.value.totalPrice,
    });

    if (response.data && response.data.statusCode === 200) {
      window.$toast && window.$toast("Cập nhật booking thành công!", "success");
      closeEditHotelModal();
      await fetchOrderDetails();
    } else {
      window.$toast && window.$toast("Cập nhật booking thất bại!", "error");
    }
  } catch (error) {
    console.error("Lỗi khi cập nhật booking:", error);
    window.$toast &&
      window.$toast("Có lỗi xảy ra khi cập nhật booking!", "error");
  } finally {
    isUpdatingHotel.value = false;
  }
};

const recalculateHotelTotalPrice = () => {
  if (!editingHotel.value) return;

  if (
    editHotelForm.value.rooms &&
    !isNaN(parseInt(editHotelForm.value.rooms))
  ) {
    const rooms = parseInt(editHotelForm.value.rooms);
    const originalPricePerRoom =
      editingHotel.value.totalPrice /
      (editingHotel.value.rooms || editingHotel.value.numberOfRooms || 1);
    editHotelForm.value.totalPrice = originalPricePerRoom * rooms;
  } else {
    editHotelForm.value.totalPrice = 0;
  }
};

const adjustRoomsBasedOnAdults = () => {
  if (
    editHotelForm.value.rooms &&
    editHotelForm.value.numAdults < editHotelForm.value.rooms
  ) {
    editHotelForm.value.rooms = editHotelForm.value.numAdults;
    recalculateHotelTotalPrice();
  }
};

const validateAndRecalculateRooms = () => {
  if (
    editHotelForm.value.rooms !== null &&
    editHotelForm.value.rooms !== undefined &&
    editHotelForm.value.rooms !== ""
  ) {
    if (editHotelForm.value.rooms > editHotelForm.value.numAdults) {
      editHotelForm.value.rooms = editHotelForm.value.numAdults;
    }

    if (editHotelForm.value.rooms < 1) {
      editHotelForm.value.rooms = 1;
    }
  }

  recalculateHotelTotalPrice();
};

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
  <div class="bg-gray-50 w-full min-h-screen">
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
        <div
          class="mb-8 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4"
        >
          <div>
            <div class="flex items-center gap-4">
              <h1 class="text-4xl font-bold text-gray-800">
                Chi tiết Đơn hàng
              </h1>
              <span class="font-semibold text-3xl text-blue-600"
                >#{{ order.id }}</span
              >
            </div>
            <p class="text-gray-500 mt-1">
              Xem lại thông tin chi tiết cho các dịch vụ bạn đã đặt.
            </p>
          </div>
          <button
            v-if="isEditable"
            @click="addMoreServices"
            class="bg-green-600 text-white font-medium px-5 py-2.5 rounded-lg text-sm text-center hover:bg-green-700 transition-colors w-full sm:w-auto"
          >
            <i class="fa-solid fa-plus mr-2"></i> Thêm dịch vụ khác
          </button>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div class="lg:col-span-2 space-y-6">
            <div v-if="order.tourBookings && order.tourBookings.length > 0">
              <h2
                class="text-2xl font-bold mb-4 text-gray-700 flex items-center gap-3"
              >
                <i class="fa-solid fa-mountain-sun text-blue-500"></i> Các tour
                đã đặt
              </h2>
              <div
                v-for="tour in order.tourBookings"
                :key="'tour-' + tour.id"
                class="bg-white p-6 rounded-xl shadow-lg border border-gray-200 mb-4"
              >
                <div class="flex justify-between items-start">
                  <h3 class="text-xl font-bold text-blue-700 mb-4">
                    {{ tour.tourName }}
                  </h3>
                  <div
                    v-if="isEditable"
                    class="flex items-center gap-2 flex-shrink-0"
                  >
                    <button
                      :disabled="processingItemId === `TOUR-${tour.id}`"
                      @click="handleEditItem(tour, 'TOUR')"
                      class="text-sm text-yellow-600 hover:text-yellow-800 disabled:opacity-50"
                    >
                      <i
                        :class="
                          processingItemId === `TOUR-${tour.id}`
                            ? 'fas fa-spinner fa-spin'
                            : 'fa-solid fa-pencil'
                        "
                      ></i>
                      Sửa
                    </button>
                    <button
                      :disabled="processingItemId === `TOUR-${tour.id}`"
                      @click="handleDeleteTour(tour.id)"
                      class="text-sm text-red-600 hover:text-red-800 disabled:opacity-50"
                    >
                      <i
                        :class="
                          processingItemId === `TOUR-${tour.id}`
                            ? 'fas fa-spinner fa-spin'
                            : 'fa-solid fa-trash'
                        "
                      ></i>
                      Xóa
                    </button>
                  </div>
                </div>
                <div
                  class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-600"
                >
                  <div class="flex items-center gap-3">
                    <i
                      class="fa-solid fa-calendar-days w-5 text-center text-blue-500"
                    ></i
                    ><span
                      >Ngày khởi hành:
                      <strong>{{
                        formatDate(tour.departureDate)
                      }}</strong></span
                    >
                  </div>
                  <div class="flex items-center gap-3">
                    <i
                      class="fa-solid fa-users w-5 text-center text-blue-500"
                    ></i
                    ><span
                      >Hành khách:
                      <strong
                        >{{ tour.numberOfAdults }} người lớn,
                        {{ tour.numberOfChildren }} trẻ em</strong
                      ></span
                    >
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
              <h2
                class="text-2xl font-bold mb-4 text-gray-700 flex items-center gap-3"
              >
                <i class="fa-solid fa-plane-up text-green-500"></i> Các chuyến
                bay đã đặt
              </h2>
              <div
                v-for="(flightDetail, idx) in flightBookingDetails"
                :key="flightDetail.booking.bookingId"
                class="bg-white p-6 rounded-xl shadow-lg border border-gray-200 mb-4"
              >
                <div class="flex justify-between items-start">
                  <h3 class="text-xl font-bold text-green-700 mb-4">
                    {{ flightDetail.booking.flight.name }} -
                    {{ flightDetail.booking.flight.airline?.name }}
                  </h3>
                  <div class="flex items-center gap-2 flex-shrink-0">
                    <button
                      @click="
                        CancelFlightBooking(flightDetail.booking.bookingId)
                      "
                      class="text-sm text-red-600 hover:text-red-800"
                    >
                      <i class="fa-solid fa-trash"></i> Xóa
                    </button>
                    <button
                      @click="handleEdittem(flightDetail.customer)"
                      class="text-sm text-blue-600 hover:text-blue-800"
                    >
                      <i class="fas fa-pencil-alt"></i> Sửa
                    </button>
                  </div>
                </div>
                <div
                  class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-600"
                >
                  <div class="flex items-center gap-3">
                    <i
                      class="fa-solid fa-chair w-5 text-center text-indigo-500"
                    ></i>
                    <span
                      >Số ghế:
                      <strong>{{
                        flightDetail.flightSlot?.seatNumber
                      }}</strong></span
                    >
                  </div>
                  <div class="flex items-center gap-3">
                    <i
                      class="fa-solid fa-couch w-5 text-center text-yellow-500"
                    ></i>
                    <span
                      >Hạng vé:
                      <strong>{{
                        flightDetail.flightSlot?.isBusiness
                          ? "Thương gia"
                          : "Phổ thông"
                      }}</strong></span
                    >
                  </div>
                  <div class="flex items-center gap-3">
                    <i
                      class="fa-solid fa-user w-5 text-center text-blue-500"
                    ></i>
                    <span
                      >Khách:
                      <strong>{{ flightDetail.customer?.fullName }}</strong> -
                      <strong>{{ flightDetail.customer?.phone }}</strong></span
                    >
                  </div>
                </div>
                <div
                  class="border-t mt-4 pt-4 flex justify-between items-center"
                >
                  <p class="text-lg">
                    Tổng giá vé:
                    <span class="font-bold text-xl text-green-700">{{
                      formatPrice(flightDetail.booking.totalPrice)
                    }}</span>
                  </p>
                  <button
                    @click="flightDetail.showDetail = !flightDetail.showDetail"
                    class="text-blue-600 hover:underline flex items-center"
                  >
                    <i
                      :class="
                        flightDetail.showDetail
                          ? 'fa-solid fa-chevron-up'
                          : 'fa-solid fa-chevron-down'
                      "
                    ></i>
                    <span class="ml-1">Chi tiết</span>
                  </button>
                </div>
                <transition name="fade">
                  <div
                    v-if="flightDetail.showDetail"
                    class="mt-4 bg-gray-50 p-4 rounded-lg border border-gray-200"
                  >
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                      <div>
                        <span class="font-semibold">Khởi hành:</span>
                        <span
                          >{{
                            formatDateTime(
                              flightDetail.booking.flight.departureTime
                            )
                          }}
                          tại
                          {{
                            flightDetail.booking.flight.departureAirport?.name
                          }}</span
                        >
                      </div>
                      <div>
                        <span class="font-semibold">Hạ cánh:</span>
                        <span
                          >{{
                            formatDateTime(
                              flightDetail.booking.flight.arrivalTime
                            )
                          }}
                          tại
                          {{
                            flightDetail.booking.flight.arrivalAirport?.name
                          }}</span
                        >
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
              <h2
                class="text-2xl font-bold mb-4 text-gray-700 flex items-center gap-3"
              >
                <i class="fa-solid fa-hotel text-indigo-500"></i> Các phòng
                khách sạn đã đặt
              </h2>
              <div
                v-for="hotel in order.hotelBookings"
                :key="'hotel-' + hotel.id"
                class="bg-gradient-to-br from-indigo-50 to-white p-6 rounded-2xl shadow-xl border border-indigo-100 mb-6 flex flex-col md:flex-row gap-6 items-center md:items-stretch hover:shadow-2xl transition-shadow duration-200 relative"
              >
                <div class="relative flex-shrink-0 flex flex-col items-center">
                  <template
                    v-if="
                      (hotel.imageUrls && hotel.imageUrls.length) ||
                      hotel.imageUrl
                    "
                  >
                    <div
                      class="relative w-40 h-32 md:w-56 md:h-40 flex items-center justify-center overflow-hidden rounded-xl"
                    >
                      <transition
                        :name="
                          slideDirectionMap[hotel.id] === 'next'
                            ? 'slide-right'
                            : 'slide-left'
                        "
                      >
                        <img
                          :key="
                            hotel.imageUrls && hotel.imageUrls.length
                              ? hotel.imageUrls[
                                  hotelImageIndices[hotel.id] || 0
                                ]
                              : hotel.imageUrl
                          "
                          :src="
                            hotel.imageUrls && hotel.imageUrls.length
                              ? hotel.imageUrls[
                                  hotelImageIndices[hotel.id] || 0
                                ]
                              : hotel.imageUrl
                          "
                          class="w-40 h-32 md:w-56 md:h-40 object-cover border-2 border-indigo-200 shadow-md mb-2 absolute left-0 top-0"
                        />
                      </transition>
                      <div
                        v-if="hotel.imageUrls && hotel.imageUrls.length > 1"
                        class="flex gap-2 absolute top-1/2 left-0 right-0 justify-between px-2 -translate-y-1/2 z-10"
                      >
                        <button
                          @click="prevHotelImage(hotel)"
                          class="bg-white/80 hover:bg-indigo-100 rounded-full p-1 shadow border border-indigo-200"
                        >
                          <i
                            class="fa-solid fa-chevron-left text-indigo-600"
                          ></i>
                        </button>
                        <button
                          @click="nextHotelImage(hotel)"
                          class="bg-white/80 hover:bg-indigo-100 rounded-full p-1 shadow border border-indigo-200"
                        >
                          <i
                            class="fa-solid fa-chevron-right text-indigo-600"
                          ></i>
                        </button>
                      </div>
                    </div>
                  </template>
                </div>
                <div class="flex justify-between items-start">
                  <div class="flex-1 flex flex-col justify-between">
                    <h3
                      class="text-2xl font-extrabold text-indigo-700 mb-1 flex items-center gap-2"
                    >
                      <i class="fa-solid fa-bed text-indigo-400"></i>
                      {{ hotel.hotelName }}
                    </h3>
                    <div
                      class="text-base text-gray-700 font-semibold mb-1 flex items-center gap-2"
                    >
                      <i class="fa-solid fa-door-closed text-gray-400"></i>
                      {{ hotel.roomType }} <span class="mx-1">-</span>
                      <span class="text-indigo-600 font-bold">{{
                        hotel.variantName
                      }}</span>
                    </div>
                    <div
                      class="flex flex-wrap gap-4 text-sm text-gray-500 mb-1 mt-2"
                    >
                      <div class="flex items-center gap-1">
                        <i class="fa-solid fa-calendar-days text-blue-400"></i>
                        Nhận phòng: <b>{{ formatDate(hotel.checkInDate) }}</b>
                      </div>
                      <div class="flex items-center gap-1">
                        <i
                          class="fa-solid fa-calendar-check text-green-400"
                        ></i>
                        Trả phòng: <b>{{ formatDate(hotel.checkOutDate) }}</b>
                      </div>
                    </div>
                    <div
                      class="flex items-center gap-2 text-sm text-gray-600 mt-1"
                    >
                      <i class="fa-solid fa-users text-pink-400"></i> Khách:
                      <b>{{ hotel.numAdults }}</b> người lớn,
                      <b>{{ hotel.numChildren }}</b> trẻ em
                    </div>
                    <div
                      class="flex items-center gap-1 mt-1 text-sm text-indigo-700 pt-1"
                    >
                      <i class="fa-solid fa-door-open text-indigo-400"></i> Số
                      lượng phòng đã đặt:
                      <b>{{ hotel.numberOfRooms ?? hotel.rooms ?? 1 }}</b>
                    </div>
                    <div
                      class="text-right font-bold text-2xl text-indigo-600 absolute right-6 bottom-3"
                    >
                      {{ formatPrice(hotel.totalPrice) }}
                    </div>
                  </div>
                  <div
                    v-if="isEditable"
                    class="flex gap-2 absolute right-6 top-6 z-10"
                  >
                    <button
                      @click="handleEditItem(hotel, 'HOTEL')"
                      class="text-sm text-yellow-600 hover:text-yellow-800 flex items-center"
                    >
                      <i class="fa-solid fa-pencil mr-1"></i> Sửa
                    </button>
                    <button
                      @click="openDeleteConfirmModal(hotel.id, 'HOTEL')"
                      class="text-sm text-red-600 hover:text-red-800 flex items-center"
                    >
                      <i class="fa-solid fa-trash mr-1"></i> Xóa
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- ✅ Bus Bookings Section - Compact Design -->
            <div v-if="order.busBookings && order.busBookings.length > 0">
              <h2
                class="text-xl font-bold mb-3 text-gray-700 flex items-center gap-2"
              >
                <i class="fa-solid fa-bus text-orange-500"></i> Vé xe đã đặt
              </h2>

              <div
                v-for="bus in order.busBookings"
                :key="'bus-' + bus.id"
                class="bg-white p-4 rounded-lg shadow-sm border border-gray-200 mb-3"
              >
                <!-- ✅ Compact Header -->
                <div class="flex justify-between items-center mb-3">
                  <div class="flex-1">
                    <h3 class="text-lg font-bold text-orange-700">
                      🚌 #{{ bus.bookingReference || bus.id }}
                    </h3>

                    <!-- ✅ Compact Route Info -->
                    <div v-if="isLoadingBusDetails.has(bus.id)" class="mt-1">
                      <div
                        class="flex items-center gap-2 text-gray-500 text-sm"
                      >
                        <i class="fa-solid fa-spinner fa-spin"></i>
                        <span>Đang tải...</span>
                      </div>
                    </div>

                    <div v-else-if="busBookingDetails.has(bus.id)" class="mt-1">
                      <div class="flex items-center gap-2 text-sm">
                        <i
                          class="fa-solid fa-map-marker-alt text-red-500 text-xs"
                        ></i>
                        <span class="text-red-600 font-medium">{{
                          busBookingDetails.get(bus.id).departureLocation ||
                          "Điểm đi"
                        }}</span>
                        <i
                          class="fa-solid fa-arrow-right text-gray-400 text-xs"
                        ></i>
                        <i
                          class="fa-solid fa-map-marker-alt text-green-500 text-xs"
                        ></i>
                        <span class="text-green-600 font-medium">{{
                          busBookingDetails.get(bus.id).arrivalLocation ||
                          "Điểm đến"
                        }}</span>
                      </div>
                    </div>

                    <div v-else class="mt-1">
                      <div class="flex items-center gap-2 text-sm">
                        <i
                          class="fa-solid fa-map-marker-alt text-red-500 text-xs"
                        ></i>
                        <span class="text-red-600 font-medium">{{
                          bus.busSlot?.route?.startLocation || "Điểm đi"
                        }}</span>
                        <i
                          class="fa-solid fa-arrow-right text-gray-400 text-xs"
                        ></i>
                        <i
                          class="fa-solid fa-map-marker-alt text-green-500 text-xs"
                        ></i>
                        <span class="text-green-600 font-medium">{{
                          bus.busSlot?.route?.endLocation || "Điểm đến"
                        }}</span>
                      </div>
                    </div>
                  </div>

                  <!-- ✅ Compact Action buttons -->
                  <div v-if="isEditable" class="flex items-center gap-1">
                    <button
                      :disabled="processingItemId === `BUS-${bus.id}`"
                      @click="handleEditItem(bus, 'BUS')"
                      class="text-xs text-yellow-600 hover:text-yellow-800 disabled:opacity-50 px-2 py-1 rounded"
                    >
                      <i
                        :class="
                          processingItemId === `BUS-${bus.id}`
                            ? 'fas fa-spinner fa-spin'
                            : 'fa-solid fa-pencil'
                        "
                      ></i>
                    </button>
                    <button
                      :disabled="processingItemId === `BUS-${bus.id}`"
                      @click="handleDeleteItem(bus.id, 'BUS')"
                      class="text-xs text-red-600 hover:text-red-800 disabled:opacity-50 px-2 py-1 rounded"
                    >
                      <i
                        :class="
                          processingItemId === `BUS-${bus.id}`
                            ? 'fas fa-spinner fa-spin'
                            : 'fa-solid fa-trash'
                        "
                      ></i>
                    </button>
                  </div>
                </div>

                <!-- ✅ Compact Trip Info với dữ liệu từ API mới -->
                <div
                  v-if="busBookingDetails.has(bus.id)"
                  class="bg-gray-50 rounded-lg p-3 mb-3"
                >
                  <div class="grid grid-cols-4 gap-3 text-xs">
                    <div class="flex items-center gap-1">
                      <i class="fa-solid fa-calendar text-blue-500"></i>
                      <span class="font-medium">{{
                        formatDate(busBookingDetails.get(bus.id).departureDate)
                      }}</span>
                    </div>
                    <div class="flex items-center gap-1">
                      <i class="fa-solid fa-clock text-green-500"></i>
                      <span class="font-medium">{{
                        formatTime(busBookingDetails.get(bus.id).departureTime)
                      }}</span>
                    </div>
                    <div class="flex items-center gap-1">
                      <i class="fa-solid fa-chair text-purple-500"></i>
                      <span class="font-medium"
                        >{{
                          busBookingDetails.get(bus.id).totalSeats || 0
                        }}
                        ghế</span
                      >
                    </div>
                    <div class="flex items-center gap-1">
                      <i class="fa-solid fa-route text-orange-500"></i>
                      <span class="font-medium">{{
                        busBookingDetails.get(bus.id).tripDuration
                      }}</span>
                    </div>
                  </div>
                </div>

                <!-- ✅ Compact Trip Info với dữ liệu cũ -->
                <div v-else class="bg-gray-50 rounded-lg p-3 mb-3">
                  <div class="grid grid-cols-4 gap-3 text-xs">
                    <div class="flex items-center gap-1">
                      <i class="fa-solid fa-calendar text-blue-500"></i>
                      <span class="font-medium">{{
                        formatDate(bus.busSlot?.slotDate)
                      }}</span>
                    </div>
                    <div class="flex items-center gap-1">
                      <i class="fa-solid fa-clock text-green-500"></i>
                      <span class="font-medium">{{
                        formatTime(bus.busSlot?.departureTime)
                      }}</span>
                    </div>
                    <div class="flex items-center gap-1">
                      <i class="fa-solid fa-chair text-purple-500"></i>
                      <span class="font-medium"
                        >{{ bus.selectedSeats?.length || 0 }} ghế</span
                      >
                    </div>
                    <div class="flex items-center gap-1">
                      <i class="fa-solid fa-route text-orange-500"></i>
                      <span class="font-medium">{{
                        calculateDuration(
                          bus.busSlot?.departureTime,
                          bus.busSlot?.arrivalTime
                        )
                      }}</span>
                    </div>
                  </div>
                </div>

                <!-- ✅ Compact Seat Info (chỉ hiển thị khi hover hoặc click) -->
                <div
                  v-if="
                    busBookingDetails.has(bus.id) &&
                    busBookingDetails.get(bus.id).selectedSeats?.length > 0
                  "
                  class="text-xs text-gray-600 mb-2"
                >
                  <i class="fa-solid fa-chair text-purple-500 mr-1"></i>
                  Ghế:
                  {{
                    busBookingDetails
                      .get(bus.id)
                      .selectedSeats.map((seat) => seat.seatNumber)
                      .join(", ")
                  }}
                </div>

                <!-- ✅ Compact Additional Info -->
                <div class="text-xs text-gray-600 mb-2">
                  <i class="fa-solid fa-users text-blue-500 mr-1"></i>
                  {{
                    busBookingDetails.has(bus.id)
                      ? busBookingDetails.get(bus.id).numPassengers
                      : bus.numPassengers
                  }}
                  hành khách
                </div>

                <!-- ✅ Compact Customer Info -->
                <div
                  v-if="
                    busBookingDetails.has(bus.id) &&
                    busBookingDetails.get(bus.id).customerName
                  "
                  class="text-xs text-gray-600 mb-2"
                >
                  <i class="fa-solid fa-user text-blue-500 mr-1"></i>
                  {{ busBookingDetails.get(bus.id).customerName }}
                  <span
                    v-if="busBookingDetails.get(bus.id).customerPhone"
                    class="ml-2"
                  >
                    <i class="fa-solid fa-phone text-green-500 mr-1"></i>
                    {{ busBookingDetails.get(bus.id).customerPhone }}
                  </span>
                </div>

                <div
                  v-else-if="bus.customer"
                  class="text-xs text-gray-600 mb-2"
                >
                  <i class="fa-solid fa-user text-blue-500 mr-1"></i>
                  {{ bus.customer.fullName }}
                  <span v-if="bus.customer.phone" class="ml-2">
                    <i class="fa-solid fa-phone text-green-500 mr-1"></i>
                    {{ bus.customer.phone }}
                  </span>
                </div>

                <!-- ✅ Compact Footer: Status & Price -->
                <div class="border-t pt-2 flex justify-between items-center">
                  <div class="flex items-center gap-2">
                    <!-- Status Badge -->
                    <div
                      v-if="
                        busBookingDetails.has(bus.id)
                          ? busBookingDetails.get(bus.id).status
                          : bus.status
                      "
                      class="flex items-center gap-1"
                    >
                      <span
                        :class="
                          (busBookingDetails.has(bus.id)
                            ? busBookingDetails.get(bus.id).status
                            : bus.status) === 'RESERVED'
                            ? 'bg-orange-100 text-orange-700'
                            : (busBookingDetails.has(bus.id)
                                ? busBookingDetails.get(bus.id).status
                                : bus.status) === 'CONFIRMED'
                            ? 'bg-green-100 text-green-700'
                            : (busBookingDetails.has(bus.id)
                                ? busBookingDetails.get(bus.id).status
                                : bus.status) === 'CANCELLED'
                            ? 'bg-red-100 text-red-700'
                            : 'bg-gray-100 text-gray-700'
                        "
                        class="px-2 py-0.5 rounded-full font-medium text-xs"
                      >
                        {{
                          (busBookingDetails.has(bus.id)
                            ? busBookingDetails.get(bus.id).status
                            : bus.status) === "RESERVED"
                            ? "🟡 Đã đặt"
                            : (busBookingDetails.has(bus.id)
                                ? busBookingDetails.get(bus.id).status
                                : bus.status) === "CONFIRMED"
                            ? "✅ Đã xác nhận"
                            : (busBookingDetails.has(bus.id)
                                ? busBookingDetails.get(bus.id).status
                                : bus.status) === "CANCELLED"
                            ? "❌ Đã hủy"
                            : busBookingDetails.has(bus.id)
                            ? busBookingDetails.get(bus.id).status
                            : bus.status
                        }}
                      </span>
                    </div>

                    <!-- Booking Date -->
                    <div class="text-xs text-gray-500">
                      <i class="fa-solid fa-calendar text-gray-400 mr-1"></i>
                      {{
                        formatDateTime(
                          busBookingDetails.has(bus.id)
                            ? busBookingDetails.get(bus.id).bookingDate
                            : bus.bookingDate
                        )
                      }}
                    </div>
                  </div>

                  <div class="text-right">
                    <div class="text-base font-bold text-orange-700">
                      {{
                        formatPrice(
                          busBookingDetails.has(bus.id)
                            ? busBookingDetails.get(bus.id).totalPrice
                            : bus.totalPrice
                        )
                      }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="lg:col-span-1">
            <div class="sticky top-8">
              <div
                class="bg-white p-6 rounded-xl shadow-lg border border-gray-200"
              >
                <div class="mb-4 border-b flex justify-between">
                  <h2 class="text-xl font-semibold">Tóm tắt đơn hàng</h2>
                  <div
                    class="px-2 border border-red-200 bg-red-50 rounded-lg text-center mb-6 flex items-center justify-center gap-2"
                  >
                    <p v-if="!hasExpired" class="text-sm text-red-700">
                      Thời gian giữ chỗ còn lại:
                    </p>
                    <p v-else class="text-sm font-semibold text-red-700">
                      Đã hết thời gian giữ chỗ!
                    </p>
                    <p
                      class="text-sm font-mono font-bold text-red-600 tracking-wider mt-1"
                    >
                      {{ String(minutes).padStart(2, "0")
                      }}<span class="animate-pulse">:</span
                      >{{ String(seconds).padStart(2, "0") }}
                    </p>
                  </div>
                </div>
                <div class="space-y-3 text-gray-600">
                  <div class="flex justify-between items-center">
                    <span>Trạng thái:</span>
                    <span
                      :class="`bg-${
                        getStatusInfo(order.status).color
                      }-100 text-${getStatusInfo(order.status).color}-700`"
                      class="font-medium px-3 py-1 rounded-full text-sm flex items-center gap-2"
                    >
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
                  <div
                    v-if="order.payDate"
                    class="flex justify-between items-center"
                  >
                    <span>Ngày thanh toán:</span>
                    <strong class="text-gray-800">{{
                      formatDateTime(order.payDate)
                    }}</strong>
                  </div>

                  <div v-if="order.voucher">
                    <div class="flex justify-between items-center">
                      <span>Tạm tính:</span>
                      <strong class="text-gray-800 line-through">{{
                        formatPrice(order.originalAmount)
                      }}</strong>
                    </div>
                    <div
                      class="flex justify-between items-center text-green-600"
                    >
                      <span>Giảm giá ({{ order.voucher.code }}):</span>
                      <strong class="font-semibold"
                        >-
                        {{
                          formatPrice(order.originalAmount - order.amount)
                        }}</strong
                      >
                    </div>
                  </div>

                  <div
                    v-if="!order.voucher && previewedVoucher.discountAmount > 0"
                  >
                    <div class="flex justify-between items-center">
                      <span>Tạm tính:</span>
                      <strong class="text-gray-800">{{
                        formatPrice(order.amount)
                      }}</strong>
                    </div>
                    <div
                      class="flex justify-between items-center text-green-600"
                    >
                      <span>Giảm giá ({{ previewedVoucher.code }}):</span>
                      <strong class="font-semibold"
                        >-
                        {{
                          formatPrice(previewedVoucher.discountAmount)
                        }}</strong
                      >
                    </div>
                  </div>
                </div>

                <div class="border-t pt-4 mt-4">
                  <div class="flex justify-between items-center text-lg">
                    <span class="font-bold text-gray-800">Tổng cộng</span>
                    <span class="text-2xl font-bold text-blue-600">{{
                      formatPrice(finalTotal)
                    }}</span>
                  </div>
                </div>

                <div v-if="isEditable" class="mt-4 pt-4 border-t">
                  <div
                    v-if="isLoadingVouchers"
                    class="text-center text-sm text-gray-500 py-2"
                  >
                    <i class="fas fa-spinner fa-spin mr-1"></i> Đang tìm mã giảm
                    giá...
                  </div>
                  <div
                    v-else-if="suggestedVouchers.length > 0 && !order.voucher"
                  >
                    <p class="block text-sm font-medium text-gray-700 mb-2">
                      Mã giảm giá cho bạn:
                    </p>
                    <div class="space-y-2 max-h-32 overflow-y-auto pr-2">
                      <button
                        v-for="voucher in suggestedVouchers"
                        :key="voucher.id"
                        @click="selectVoucher(voucher.code)"
                        :disabled="voucher.disabled"
                        class="w-full text-left p-2 border-l-4 rounded-md transition"
                        :class="{
                          'border-green-500 bg-green-50 hover:bg-green-100':
                            !voucher.disabled,
                          'border-gray-300 bg-gray-100 text-gray-400 cursor-not-allowed':
                            voucher.disabled,
                        }"
                      >
                        <strong
                          :class="{ 'text-green-700': !voucher.disabled }"
                          >{{ voucher.code }}</strong
                        >
                        <p
                          class="text-xs"
                          :class="{ 'text-gray-600': !voucher.disabled }"
                        >
                          {{ getVoucherDiscountText(voucher) }}
                        </p>
                        <p
                          v-if="voucher.disabled"
                          class="text-xs font-bold text-red-500"
                        >
                          Đã hết lượt sử dụng
                        </p>
                      </button>
                    </div>
                  </div>

                  <div class="flex gap-2 mt-3" v-if="!order.voucher">
                    <input
                      v-model="voucherCode"
                      @keyup.enter="previewVoucherDiscount"
                      type="text"
                      class="block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                      placeholder="Hoặc nhập mã khác"
                    />
                    <button
                      @click="previewVoucherDiscount"
                      class="flex-shrink-0 bg-gray-800 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-black"
                    >
                      Áp dụng
                    </button>
                  </div>
                  <p
                    v-if="previewedVoucher.error"
                    class="text-red-500 text-sm mt-1"
                  >
                    {{ previewedVoucher.error }}
                  </p>
                </div>

                <div v-if="isEditable" class="max-w-lg w-full mt-4">
                  <div>
                    <h3 class="text-lg font-semibold mb-4">
                      Thanh toán chuyển khoản
                    </h3>
                    <div class="space-y-6">
                      <div>
                        <label
                          class="block text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2"
                        >
                          <i class="fas fa-university text-blue-400"></i> Ngân
                          hàng
                        </label>
                        <div class="relative">
                          <button
                            type="button"
                            @click="showDropdown = !showDropdown"
                            class="w-full border border-gray-300 rounded-lg px-4 py-2 flex items-center focus:outline-none focus:ring-2 focus:ring-indigo-400 bg-white hover:border-indigo-400 transition"
                          >
                            <img
                              v-if="selectedBank && selectedBank.logo"
                              :src="selectedBank.logo"
                              class="w-6 h-6 mr-2 object-contain"
                            />
                            <span class="font-medium">{{
                              selectedBank
                                ? selectedBank.name
                                : "Chọn ngân hàng"
                            }}</span>
                            <svg
                              class="ml-auto w-4 h-4 text-gray-400"
                              fill="none"
                              stroke="currentColor"
                              viewBox="0 0 24 24"
                            >
                              <path
                                stroke-linecap="round"
                                stroke-linejoin="round"
                                stroke-width="2"
                                d="M19 9l-7 7-7-7"
                              />
                            </svg>
                          </button>
                          <ul
                            v-if="showDropdown"
                            class="absolute z-20 bg-white border w-full mt-1 rounded shadow max-h-60 overflow-auto animate-fade-in"
                          >
                            <li
                              v-for="bank in banks"
                              :key="bank.code"
                              @click="selectBank(bank)"
                              class="flex items-center px-4 py-2 hover:bg-indigo-50 cursor-pointer transition"
                            >
                              <img
                                v-if="bank.logo"
                                :src="bank.logo"
                                class="w-6 h-6 mr-2 object-contain"
                              />
                              <span>{{ bank.name }}</span>
                            </li>
                          </ul>
                        </div>
                      </div>
                      <div>
                        <label
                          class="block text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2"
                        >
                          <i class="fas fa-id-card text-green-400"></i> Số tài
                          khoản
                        </label>
                        <input
                          v-model="bankTransfer.accountNumber"
                          type="text"
                          placeholder="Nhập số tài khoản"
                          maxlength="20"
                          class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-green-400 transition placeholder-gray-400"
                          @blur="onAccountNumberBlur"
                          @input="resetAccountInfo"
                        />
                        <div
                          v-if="isLoadingLK"
                          class="flex items-center mt-2 text-blue-500"
                        >
                          <span
                            class="animate-spin rounded-full h-5 w-5 border-t-2 border-b-2 border-blue-400 mr-2"
                          ></span
                          >Đang kiểm tra...
                        </div>
                        <div
                          v-if="notFound && !isLoadingLK"
                          class="text-red-500 text-sm mt-2"
                        >
                          Không tìm thấy thông tin số tài khoản
                        </div>
                      </div>
                      <div
                        v-if="found"
                        class="bg-indigo-50 rounded-lg p-4 flex flex-col gap-2 border border-indigo-100"
                      >
                        <div class="flex items-center gap-2">
                          <i class="fas fa-user-circle text-indigo-400"></i>
                          <span class="font-semibold text-gray-700"
                            >Tên tài khoản:</span
                          >
                          <span class="ml-auto font-bold text-indigo-700">{{
                            bankTransfer.accountName
                          }}</span>
                        </div>
                        <div class="flex items-center gap-2">
                          <i class="fas fa-wallet text-green-400"></i>
                          <span class="font-semibold text-gray-700"
                            >Số dư khả dụng:</span
                          >
                          <span class="ml-auto font-bold text-green-600"
                            >{{ bankTransfer.availableBalance }}
                            {{ bankTransfer.currency }}</span
                          >
                        </div>
                      </div>
                      <div v-if="found">
                        <label
                          class="block text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2"
                        >
                          <i class="fas fa-coins text-yellow-400"></i> Số tiền
                          muốn thanh toán
                        </label>
                        <input
                          v-model.number="bankTransfer.amount"
                          type="number"
                          min="0"
                          :max="bankTransfer.availableBalance"
                          placeholder="Nhập số tiền"
                          class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-yellow-400 transition placeholder-gray-400"
                          @input="validateAmount"
                        />
                        <div
                          v-if="amountError"
                          class="text-red-500 text-sm mt-1"
                        >
                          {{ amountValidationMessage }}
                        </div>
                      </div>
                      <div class="flex justify-end" v-if="found">
                        <button
                          @click="submitPayment"
                          :disabled="
                            isPaying || amountError || !bankTransfer.amount
                          "
                          class="bg-gradient-to-r from-indigo-500 to-blue-500 hover:from-indigo-600 hover:to-blue-600 text-white px-8 py-2 rounded-lg font-semibold flex items-center shadow-lg transition disabled:opacity-60"
                        >
                          <span
                            v-if="isPaying"
                            class="animate-spin rounded-full h-5 w-5 border-t-2 border-b-2 border-white mr-2"
                          ></span>
                          <i class="fas fa-paper-plane mr-2"></i> Thanh toán
                        </button>
                      </div>
                    </div>
                  </div>

                  <transition name="modal" appear>
                    <div
                      v-if="showOtpDialog"
                      class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40"
                    >
                      <div
                        class="bg-white rounded-2xl shadow-2xl max-w-md w-full overflow-hidden"
                      >
                        <div
                          class="bg-gradient-to-r from-indigo-500 to-blue-400 px-6 py-4 flex justify-between items-center"
                        >
                          <h3 class="text-white flex items-center gap-2">
                            <i class="fas fa-shield-alt"></i> Xác thực OTP
                          </h3>
                          <button @click="showOtpDialog = false">
                            <i class="fas fa-times text-white"></i>
                          </button>
                        </div>
                        <div class="p-6 space-y-4">
                          <p>
                            OTP đã gửi qua email. Nhập để xác nhận thanh toán.
                          </p>
                          <div class="flex items-center gap-2">
                            <i class="fas fa-clock"></i>
                            <span>Thời gian còn lại:</span>
                            <span class="font-mono">{{
                              otpCountdownDisplay
                            }}</span>
                          </div>
                          <input
                            v-model="otp"
                            maxlength="6"
                            :disabled="otpExpired"
                            placeholder="Nhập OTP"
                            class="w-full border rounded-lg px-4 py-2 text-center"
                          />
                          <div v-if="otpError" class="text-red-500">
                            {{ otpError }}
                          </div>
                          <div v-if="otpSuccess" class="text-green-600">
                            Xác thực thành công!
                          </div>
                          <div v-if="otpExpired" class="text-red-500">
                            OTP đã hết hạn.
                          </div>
                          <div class="flex justify-end gap-2">
                            <button
                              @click="showOtpDialog = false"
                              class="px-4 py-2 rounded bg-gray-200"
                            >
                              Hủy
                            </button>
                            <button
                              @click="confirmOtp"
                              :disabled="isConfirming || !otp || otpExpired"
                              class="px-4 py-2 rounded text-white bg-gradient-to-r from-indigo-500 to-blue-500 disabled:opacity-50 flex items-center"
                            >
                              <span
                                v-if="isConfirming"
                                class="animate-spin inline-block h-5 w-5 border-t-2 border-white mr-2 rounded-full"
                              ></span>
                              Xác nhận
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </transition>
                </div>
                <div
                  v-if="!isEditable && order.status === 'PAID'"
                  class="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg"
                >
                  <button
                    @click="printInvoice"
                    class="w-full bg-gray-800 text-white font-medium py-3 rounded-lg hover:bg-black transition"
                  >
                    <i class="fa-solid fa-print mr-2"></i> In hóa đơn
                  </button>
                  <button
                    @click="openRefundDialog"
                    class="mt-6 w-full bg-red-600 text-white font-medium py-3 rounded-lg hover:bg-red-700 transition flex items-center justify-center"
                  >
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

  <div
    v-if="showEditHotelModal"
    class="fixed inset-0 z-[9999] flex items-center justify-center"
    style="background-color: rgba(0, 0, 0, 0.6)"
    @click="closeEditHotelModal"
  >
    <div
      class="bg-white rounded-lg p-6 w-full max-w-4xl mx-4 mt-20"
      @click.stop
    >
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-lg font-semibold">Sửa thông tin booking khách sạn</h3>
        <button
          @click="closeEditHotelModal"
          class="text-gray-400 hover:text-gray-600"
        >
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div v-if="editingHotel" class="mb-4 p-3 bg-gray-50 rounded-md">
        <p class="text-sm text-gray-600">
          <strong>Khách sạn:</strong> {{ editingHotel.hotelName }}
        </p>
        <p class="text-sm text-gray-600">
          <strong>Loại phòng:</strong> {{ editingHotel.roomType }} -
          {{ editingHotel.variantName }}
        </p>
        <p class="text-sm text-gray-600">
          <strong>Ngày nhận/trả:</strong>
          {{ formatDate(editingHotel.checkInDate) }} -
          {{ formatDate(editingHotel.checkOutDate) }}
        </p>
      </div>

      <div class="grid grid-cols-2 gap-6">
        <div class="space-y-2">
          <label
            class="flex items-center gap-2 text-sm font-semibold text-gray-700"
          >
            <i class="fas fa-user text-blue-500"></i>
            Số người lớn
          </label>
          <input
            v-model.number="editHotelForm.numAdults"
            @input="adjustRoomsBasedOnAdults"
            type="number"
            min="1"
            class="w-full px-4 py-3 border-2 border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
            placeholder="Nhập số người lớn"
          />
        </div>

        <div class="space-y-2">
          <label
            class="flex items-center gap-2 text-sm font-semibold text-gray-700"
          >
            <i class="fas fa-child text-blue-500"></i>
            Số trẻ em
          </label>
          <input
            v-model.number="editHotelForm.numChildren"
            type="number"
            min="0"
            class="w-full px-4 py-3 border-2 border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
            placeholder="Nhập số trẻ em"
          />
        </div>

        <div class="space-y-2">
          <label
            class="flex items-center gap-2 text-sm font-semibold text-gray-700"
          >
            <i class="fas fa-bed text-blue-500"></i>
            Số phòng
          </label>
          <input
            v-model.number="editHotelForm.rooms"
            @input="validateAndRecalculateRooms"
            type="number"
            :min="1"
            :max="editHotelForm.numAdults"
            class="w-full px-4 py-3 border-2 border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
            placeholder="Nhập số phòng"
          />
          <p
            v-if="editHotelForm.rooms > editHotelForm.numAdults"
            class="text-sm text-red-500 flex items-center gap-1"
          >
            <i class="fas fa-exclamation-triangle"></i>
            Số phòng không thể nhiều hơn số người lớn!
          </p>
        </div>

        <div class="space-y-2">
          <label
            class="flex items-center gap-2 text-sm font-semibold text-gray-700"
          >
            <i class="fas fa-coins text-blue-500"></i>
            Tổng tiền
          </label>
          <div
            class="bg-gradient-to-r from-blue-50 to-indigo-50 border-2 border-blue-200 rounded-lg p-4"
          >
            <div class="flex items-center justify-between">
              <span class="text-lg font-bold text-blue-700">
                {{ formatPrice(editHotelForm.totalPrice) }}
              </span>
            </div>
            <p class="text-xs text-blue-600 mt-1">
              Tự động tính dựa trên số phòng
            </p>
          </div>
        </div>
      </div>

      <div class="flex justify-end space-x-3 mt-6">
        <button
          @click="closeEditHotelModal"
          class="px-4 py-2 text-gray-600 border border-gray-300 rounded-md hover:bg-gray-50"
        >
          Hủy
        </button>
        <button
          @click="updateHotelBookingData"
          :disabled="isUpdatingHotel"
          class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50"
        >
          <span v-if="isUpdatingHotel">
            <i class="fas fa-spinner fa-spin mr-2"></i>Đang cập nhật...
          </span>
          <span v-else>Cập nhật</span>
        </button>
      </div>
    </div>
  </div>
  <div id="modeledit" class="w-full h-full absolute inset-0 hidden z-50">
    <div class="absolute inset-0 bg-black opacity-50 z-40"></div>
    <section
      class="relative w-6/12 m-auto mt-20 bg-white rounded-lg shadow-md p-6 overflow-hidden z-50"
    >
      <div
        class="absolute bottom-4 -right-40 w-34 h-34 bg-sky-300 rounded-full blur-xl [box-shadow:-100px_50px_30px_100px_#7dd3fc] z-0"
      ></div>
      <h2 class="relative text-lg font-medium text-gray-700 mb-4 z-60">
        Thông tin khách hàng
      </h2>
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-6 relative z-50">
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-1"
            >Họ và tên</label
          >
          <input
            v-model="customer.fullName"
            type="text"
            placeholder="Nhập họ tên đầy đủ"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-1"
            >Giới tính</label
          >
          <select
            v-model="customer.gender"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option :value="true">Nam</option>
            <option :value="false">Nữ</option>
            <option :value="null">Khác</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-1"
            >Ngày sinh</label
          >
          <input
            v-model="customer.dob"
            type="date"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-1"
            >Số hộ chiếu</label
          >
          <input
            v-model="customer.passport"
            type="text"
            placeholder="Nhập số hộ chiếu"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-1"
            >Email</label
          >
          <input
            v-model="customer.email"
            type="email"
            placeholder="Nhập email"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-1"
            >Số điện thoại</label
          >
          <input
            v-model="customer.phone"
            type="text"
            placeholder="Nhập số điện thoại"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
      </div>
      <div class="mt-6 flex justify-start space-x-4 relative z-60">
        <button
          @click="UpdateCustomer"
          :disabled="isloadingUpdateC"
          class="mt-3 px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700 flex items-center justify-center min-w-[100px]"
        >
          <span
            v-if="isloadingUpdateC"
            class="animate-spin mr-2 w-4 h-4 border-2 border-white border-t-indigo-500 rounded-full"
          ></span>
          <span>{{ isloadingUpdateC ? "Đang lưu..." : "Cập nhập" }}</span>
        </button>
        <button
          @click="exit"
          class="px-6 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition"
        >
          Hủy
        </button>
      </div>
    </section>
  </div>

  <div
    v-if="showDeleteConfirmModal"
    class="fixed inset-0 z-[9999] flex items-center justify-center"
    style="background-color: rgba(0, 0, 0, 0.6)"
    @click="closeDeleteConfirmModal"
  >
    <div class="bg-white rounded-lg p-6 w-full max-w-md mx-4" @click.stop>
      <div class="flex items-center gap-3 mb-4">
        <div class="flex-shrink-0">
          <div
            class="w-12 h-12 bg-red-100 rounded-full flex items-center justify-center"
          >
            <i class="fas fa-exclamation-triangle text-red-600 text-xl"></i>
          </div>
        </div>
        <div>
          <h3 class="text-lg font-semibold text-gray-900">Xác nhận xóa</h3>
          <p class="text-sm text-gray-500">Hành động này không thể hoàn tác</p>
        </div>
      </div>

      <div class="mb-6">
        <p class="text-gray-700">
          Bạn có chắc chắn muốn xóa dịch vụ này khỏi đơn hàng?
        </p>
      </div>
      <!-- Modal hủy vé -->

      <!--  -->

      <div class="flex justify-end gap-3">
        <button
          @click="closeDeleteConfirmModal"
          class="px-4 py-2 text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-lg font-medium transition-colors"
        >
          Hủy
        </button>
        <button
          @click="handleDeleteItem"
          :disabled="processingItemId"
          class="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg font-medium transition-colors disabled:opacity-50 flex items-center gap-2"
        >
          <span
            v-if="processingItemId"
            class="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"
          ></span>
          <i v-else class="fas fa-trash"></i>
          {{ processingItemId ? "Đang xóa..." : "Xóa" }}
        </button>
      </div>
    </div>
  </div>
  <div
    v-if="showEditTourModal"
    class="fixed inset-0 z-[9999] flex items-center justify-center"
    style="background-color: rgba(0, 0, 0, 0.6)"
    @click="closeEditTourModal"
  >
    <div class="bg-white rounded-lg p-6 w-full max-w-2xl mx-4" @click.stop>
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-lg font-semibold text-gray-800">
          Chỉnh sửa thông tin Tour
        </h3>
        <button
          @click="closeEditTourModal"
          class="text-gray-400 hover:text-gray-600"
        >
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div v-if="editingTourItem" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1"
            >Tên khách hàng</label
          >
          <input
            type="text"
            v-model="editingTourItem.customerName"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1"
              >Số điện thoại</label
            >
            <input
              type="text"
              v-model="editingTourItem.phone"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1"
              >Email</label
            >
            <input
              type="email"
              v-model="editingTourItem.email"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1"
              >Số người lớn</label
            >
            <input
              type="number"
              min="1"
              v-model.number="editingTourItem.numberOfAdults"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1"
              >Số trẻ em</label
            >
            <input
              type="number"
              min="0"
              v-model.number="editingTourItem.numberOfChildren"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1"
            >Ghi chú</label
          >
          <textarea
            v-model="editingTourItem.notes"
            rows="3"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          ></textarea>
        </div>
      </div>

      <div class="flex justify-end space-x-3 mt-6">
        <button
          @click="closeEditTourModal"
          class="px-4 py-2 text-gray-600 border border-gray-300 rounded-md hover:bg-gray-50"
        >
          Hủy
        </button>
        <button
          @click="handleUpdateTour"
          class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
        >
          Lưu thay đổi
        </button>
      </div>
    </div>
  </div>
  <transition name="modal" appear>
    <div
      v-if="showRefundDialog"
      class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50"
    >
      <div
        class="bg-white rounded-2xl shadow-2xl max-w-lg w-full mx-4 overflow-hidden"
      >
        <div
          class="bg-gradient-to-r from-red-500 to-red-600 px-6 py-4 flex justify-between items-center"
        >
          <h3 class="text-white flex items-center gap-2">
            <i class="fas fa-times-circle"></i> Hủy vé
          </h3>
          <button
            @click="closeRefundDialog"
            class="text-white hover:text-red-200"
          >
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div class="p-6 space-y-4">
          <!-- Bước 1: Nhập lý do -->
          <div v-if="!refundPaymentId">
            <p class="text-gray-700 mb-4">
              Vui lòng nhập lý do hủy vé để tiếp tục.
            </p>
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">
                Lý do hủy vé <span class="text-red-500">*</span>
              </label>
              <textarea
                v-model="refundReason"
                placeholder="Nhập lý do hủy vé..."
                rows="4"
                class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-red-400 transition resize-none"
              ></textarea>
            </div>
            <div class="flex justify-end gap-3 mt-4">
              <button
                @click="closeRefundDialog"
                class="px-4 py-2 rounded-lg bg-gray-200 text-gray-700 hover:bg-gray-300 transition"
              >
                Hủy
              </button>
              <button
                @click="submitRefund"
                :disabled="isRefunding || !refundReason.trim()"
                class="px-6 py-2 rounded-lg bg-red-600 text-white hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
              >
                <span
                  v-if="isRefunding"
                  class="animate-spin inline-block h-5 w-5 border-t-2 border-white mr-2 rounded-full"
                ></span>
                <i class="fas fa-paper-plane mr-2"></i>
                Gửi yêu cầu
              </button>
            </div>
          </div>

          <!-- Bước 2: Nhập OTP -->
          <div v-else>
            <p class="text-gray-700 mb-4">
              OTP đã được gửi qua email. Nhập để xác nhận hủy vé.
            </p>

            <div class="flex items-center gap-2 mb-4 text-sm text-gray-600">
              <i class="fas fa-clock"></i>
              <span>Thời gian còn lại:</span>
              <span
                class="font-mono font-bold"
                :class="refundOtpExpired ? 'text-red-500' : 'text-blue-600'"
              >
                {{ refundOtpCountdownDisplay }}
              </span>
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">
                Mã OTP <span class="text-red-500">*</span>
              </label>
              <input
                v-model="refundOtp"
                type="text"
                maxlength="6"
                :disabled="refundOtpExpired"
                placeholder="Nhập mã OTP"
                class="w-full border border-gray-300 rounded-lg px-4 py-2 text-center text-lg font-mono focus:outline-none focus:ring-2 focus:ring-red-400 transition disabled:opacity-50"
              />
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
              <button
                @click="closeRefundDialog"
                class="px-4 py-2 rounded-lg bg-gray-200 text-gray-700 hover:bg-gray-300 transition"
              >
                Hủy
              </button>
              <button
                @click="confirmRefundOtp"
                :disabled="isRefundConfirming || !refundOtp || refundOtpExpired"
                class="px-6 py-2 rounded-lg bg-red-600 text-white hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
              >
                <span
                  v-if="isRefundConfirming"
                  class="animate-spin inline-block h-5 w-5 border-t-2 border-white mr-2 rounded-full"
                ></span>
                <i class="fas fa-check mr-2"></i>
                Xác nhận
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<style scoped>
/* Styles remain the same */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

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
