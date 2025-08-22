<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { StarIcon } from "@heroicons/vue/24/solid";
import {
  getAccessToken,
  clearToken,
  getBearerToken,
} from "@/services/TokenService";

// --- State của Component ---
const route = useRoute();
const router = useRouter();
const tourId = Number(route.params.id);

const tour = ref(null);
const reviews = ref([]);
const itinerary = ref([]);
const availableDates = ref([]);
const isLoading = ref(true);
const selectedTab = ref("schedule");
const activeCartId = ref(null);
const isLoggedIn = ref(false);
const user = ref(null);


const bookingForm = ref({
  selectedDate: null,
  travelers: { adults: 1, children: 0 },
  maxTravelers: 20,
  prices: { child_multiplier: 0.75 },
});

// --- STATE MỚI: DÀNH CHO FORM LIÊN HỆ ---
const contactInfo = ref({
  fullName: "",
  phone: "",
  email: "",
  notes: "",
});
const isProcessing = ref(false);

const today = new Date();
today.setHours(0, 0, 0, 0);
const currentDisplayDate = ref(
  new Date(today.getFullYear(), today.getMonth(), 1)
);

// --- Lifecycle Hook ---
onMounted(async () => {
  // Kiểm tra trạng thái đăng nhập
  const token = getAccessToken();
  if (token) {
    try {
      const payloadBase64 = token.split(".")[1];
      const decodedJson = atob(
        payloadBase64.replace(/-/g, "+").replace(/_/g, "/")
      );
      user.value = JSON.parse(decodedJson);
      isLoggedIn.value = true;

      // Tự động điền thông tin user vào form
      if (user.value) {
        contactInfo.value.fullName = user.value.name || "";
        contactInfo.value.email = user.value.email || "";
      }
    } catch (e) {
      console.error("Token không hợp lệ hoặc đã hết hạn:", e);
      clearToken();
      isLoggedIn.value = false;
    }
  }

  // Lấy dữ liệu tour
  activeCartId.value = localStorage.getItem("activeCartId");
  try {
    const [tourRes, reviewsRes, itineraryRes, departuresRes] =
      await Promise.all([
        fetch(`http://localhost:8080/api/v1/tours/${tourId}`),
        fetch(`http://localhost:8080/api/v1/tours/${tourId}/reviews`),
        fetch(`http://localhost:8080/api/v1/tours/${tourId}/itinerary`),
        fetch(`http://localhost:8080/api/v1/tours/${tourId}/departures`),
      ]);

    // Xử lý dữ liệu tour
    const tourData = await tourRes.json();
    if (tourData.statusCode === 200) tour.value = tourData.data;

    // Xử lý đánh giá
    const reviewsData = await reviewsRes.json();
    if (reviewsData.statusCode === 200) reviews.value = reviewsData.data;

    // Xử lý lịch trình
    const itineraryData = await itineraryRes.json();
    if (itineraryData.statusCode === 200) itinerary.value = itineraryData.data;

    // Xử lý ngày khởi hành
    const departuresData = await departuresRes.json();
    if (departuresData.statusCode === 200) {
      availableDates.value = departuresData.data.map((d) => ({
        departureId: d.id,
        date: d.departureDate,
        display: new Date(d.departureDate).toLocaleDateString("vi-VN", {
          day: "2-digit",
          month: "2-digit",
          year: "numeric",
        }),
        seats: d.seatCount - d.bookedSeats,
        price: d.adultPrice,
        promoPrice: d.discount > 0 ? d.adultPrice - d.discount : null,
      }));

      if (availableDates.value.length > 0) {
        const firstAvailableDate = new Date(availableDates.value[0].date);
        selectDate(availableDates.value[0].date);
        currentDisplayDate.value = new Date(
          firstAvailableDate.getFullYear(),
          firstAvailableDate.getMonth(),
          1
        );
      }
    }
  } catch (error) {
    console.error("Lỗi khi tải dữ liệu chi tiết tour:", error);
    if (window.$toast) window.$toast("Không thể tải dữ liệu tour.", "error");
  } finally {
    isLoading.value = false;
  }
});

// --- Các hàm xử lý sự kiện ---
const selectDate = (date) => {
  if (date) bookingForm.value.selectedDate = date;
};

const handleAddToCart = async () => {
  if (!isLoggedIn.value) {
    if (window.$toast)
      window.$toast("Bạn cần đăng nhập để thực hiện chức năng này!", "error");
    return;
  }
  if (!bookingForm.value.selectedDate) {
    if (window.$toast) window.$toast("Vui lòng chọn ngày khởi hành!", "info");
    return;
  }
  const selectedDeparture = availableDates.value.find(
    (d) => d.date === bookingForm.value.selectedDate
  );
  if (!selectedDeparture) {
    if (window.$toast)
      window.$toast("Lỗi: Không tìm thấy thông tin ngày khởi hành.", "error");
    return;
  }

  try {
    const addToCartRequest = {
      itemId: tour.value.id,
      itemType: "TOUR",
      numberOfAdults: bookingForm.value.travelers.adults,
      numberOfChildren: bookingForm.value.travelers.children,
      departureId: selectedDeparture.departureId,
    };
    const response = await fetch(
      `http://localhost:8080/api/v1/cart/${activeCartId.value}/items`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: getBearerToken(),
        },
        body: JSON.stringify(addToCartRequest),
      }
    );
    if (response.ok) {
      // ✅ FIX: Lấy cart ID từ response
      const result = await response.json();
      
      if (window.$toast)
        window.$toast("Đã thêm tour vào chuyến đi thành công!", "success");
      
      // ✅ THÊM: Cập nhật localStorage với cart ID từ response
      if (result.data && result.data.id) {
        localStorage.setItem('activeCartId', result.data.id);
        console.log('✅ Updated cart ID from response:', result.data.id);
        router.push(`/orders/${result.data.id}`);
      } else {
        // Fallback: dùng cart ID cũ nếu response không có ID
        localStorage.removeItem("activeCartId");
        router.push(`/orders/${activeCartId.value}`);
      }
    } else {
      const result = await response.json();
      if (window.$toast)
        window.$toast(
          result.message || "Không thể thêm tour vào chuyến đi.",
          "error"
        );
    }
  } catch (error) {
    console.error("Lỗi khi thêm vào chuyến đi:", error);
    if (window.$toast) window.$toast("Lỗi kết nối máy chủ.", "error");
  }
};

const handleDirectBooking = async () => {
  // 1. Kiểm tra đăng nhập
  if (!isLoggedIn.value) {
    if (window.$toast) window.$toast("Bạn cần đăng nhập để đặt tour!", "error");
    return;
  }
  // 2. Kiểm tra đã chọn ngày
  if (!bookingForm.value.selectedDate) {
    if (window.$toast) window.$toast("Vui lòng chọn ngày khởi hành!", "info");
    return;
  }
  // 3. KIỂM TRA ĐÃ ĐIỀN ĐỦ THÔNG TIN LIÊN HỆ
  if (
    !contactInfo.value.fullName ||
    !contactInfo.value.phone ||
    !contactInfo.value.email
  ) {
    if (window.$toast)
      window.$toast(
        "Vui lòng điền đầy đủ Họ tên, SĐT và Email trong form liên hệ.",
        "error"
      );
    return;
  }

  isProcessing.value = true;
  const selectedDeparture = availableDates.value.find(
    (d) => d.date === bookingForm.value.selectedDate
  );
  if (!selectedDeparture) {
    if (window.$toast)
      window.$toast("Lỗi: Không tìm thấy thông tin ngày khởi hành.", "error");
    isProcessing.value = false;
    return;
  }

  try {
    const reservationRequest = {
      userId: user.value.userId,
      tourId: tour.value.id,
      departureId: selectedDeparture.departureId,
      numberOfAdults: bookingForm.value.travelers.adults,
      numberOfChildren: bookingForm.value.travelers.children,
      // Lấy thông tin từ form liên hệ
      customerName: contactInfo.value.fullName,
      phone: contactInfo.value.phone,
      email: contactInfo.value.email,
      notes: contactInfo.value.notes,
      totalPrice: total.value,
    };
    const response = await fetch(
      "http://localhost:8080/api/v1/orders/reserve-tour-direct",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: getBearerToken(),
        },
        body: JSON.stringify(reservationRequest),
      }
    );
    const result = await response.json();
    if (response.ok && result.statusCode === 201) {
      if (window.$toast)
        window.$toast("Giữ chỗ thành công! Chuẩn bị thanh toán.", "success");
      const order = result.data;
      // Chuyển hướng đến trang thanh toán
      router.push({
        name: "payment",
        params: { orderId: order.id },
      });
    } else {
      if (window.$toast)
        window.$toast(result.message || "Không thể giữ chỗ.", "error");
    }
  } catch (error) {
    console.error("Lỗi khi xử lý đặt tour:", error);
    if (window.$toast) window.$toast("Lỗi kết nối máy chủ.", "error");
  } finally {
    isProcessing.value = false;
  }
};

// --- Các computed properties và hàm tiện ích ---
const formatPrice = (price) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    price
  );

const averageRating = computed(() => {
  if (!reviews.value || reviews.value.length === 0) return "Mới";
  const total = reviews.value.reduce((acc, review) => acc + review.rating, 0);
  return (total / reviews.value.length).toFixed(1);
});

const currentPriceData = computed(() => {
  if (!bookingForm.value.selectedDate) return null;
  return availableDates.value.find(
    (d) => d.date === bookingForm.value.selectedDate
  );
});

const currentPrice = computed(
  () => currentPriceData.value?.promoPrice || currentPriceData.value?.price || 0
);

const totalTravelers = computed(
  () =>
    bookingForm.value.travelers.adults + bookingForm.value.travelers.children
);

const canAddTraveler = computed(
  () => totalTravelers.value < bookingForm.value.maxTravelers
);

const subtotal = computed(() => {
  if (!currentPrice.value) return 0;
  const { adults, children } = bookingForm.value.travelers;
  return (
    adults * currentPrice.value +
    children * (currentPrice.value * bookingForm.value.prices.child_multiplier)
  );
});

const total = computed(() => subtotal.value);

const monthYearDisplay = computed(() =>
  currentDisplayDate.value.toLocaleDateString("vi-VN", {
    month: "long",
    year: "numeric",
  })
);

const calendarGrid = computed(() => {
  const year = currentDisplayDate.value.getFullYear();
  const month = currentDisplayDate.value.getMonth();
  const firstDayOfMonth = new Date(year, month, 1);
  const lastDayOfMonth = new Date(year, month + 1, 0);
  const daysInMonth = lastDayOfMonth.getDate();
  const startDayOfWeek = firstDayOfMonth.getDay();
  const grid = [];
  const lastDayOfPrevMonth = new Date(year, month, 0).getDate();
  for (let i = startDayOfWeek === 0 ? 6 : startDayOfWeek - 1; i > 0; i--) {
    grid.push({
      date: new Date(year, month - 1, lastDayOfPrevMonth - i + 1),
      isCurrentMonth: false,
    });
  }
  for (let day = 1; day <= daysInMonth; day++) {
    grid.push({ date: new Date(year, month, day), isCurrentMonth: true });
  }
  const endDayOfWeek = lastDayOfMonth.getDay();
  for (let i = 1; i <= (endDayOfWeek === 0 ? 0 : 7 - endDayOfWeek); i++) {
    grid.push({ date: new Date(year, month + 1, i), isCurrentMonth: false });
  }
  return grid.map((dayObj) => {
    const normalizedDateKey = dayObj.date.toISOString().split("T")[0];
    const availableDateInfo = availableDates.value.find(
      (d) => new Date(d.date).toISOString().split("T")[0] === normalizedDateKey
    );
    const isAvailable =
      !!availableDateInfo && dayObj.date.getTime() >= today.getTime();
    return {
      ...dayObj,
      fullDateString: availableDateInfo ? availableDateInfo.date : null,
      dayOfMonth: dayObj.date.getDate(),
      isAvailable,
      isSelected:
        isAvailable &&
        bookingForm.value.selectedDate === availableDateInfo.date,
      isToday: dayObj.date.getTime() === today.getTime(),
      seats: availableDateInfo ? availableDateInfo.seats : null,
    };
  });
});

const prevMonth = () => {
  currentDisplayDate.value = new Date(
    currentDisplayDate.value.getFullYear(),
    currentDisplayDate.value.getMonth() - 1,
    1
  );
};

const nextMonth = () => {
  currentDisplayDate.value = new Date(
    currentDisplayDate.value.getFullYear(),
    currentDisplayDate.value.getMonth() + 1,
    1
  );
};

const updateTravelers = (type, action) => {
  const current = bookingForm.value.travelers[type];
  if (action === "increase" && canAddTraveler.value) {
    bookingForm.value.travelers[type]++;
  } else if (action === "decrease" && current > (type === "adults" ? 1 : 0)) {
    bookingForm.value.travelers[type]--;
  }
};

const expandedDays = ref({});
const toggleDay = (dayIndex) => {
  expandedDays.value[dayIndex] = !expandedDays.value[dayIndex];
};

const currentIndex = ref(0);
const nextImage = () => {
  currentIndex.value =
    (currentIndex.value + 1) % (tour.value?.imageUrls.length || 1);
};
const prevImage = () => {
  currentIndex.value =
    (currentIndex.value - 1 + (tour.value?.imageUrls.length || 1)) %
    (tour.value?.imageUrls.length || 1);
};
const selectImage = (index) => {
  currentIndex.value = index;
};
</script>

<template>
  <div
    v-if="isLoading"
    class="flex justify-center items-center h-screen w-full"
  >
    <div class="text-center">
      <i class="fas fa-spinner fa-spin text-4xl text-blue-500 mb-4"></i>
      <h2 class="text-xl font-semibold text-gray-700">
        Đang tải dữ liệu tour...
      </h2>
      <p class="text-gray-500 mt-1">Vui lòng chờ trong giây lát.</p>
    </div>
  </div>

  <div
    v-else-if="tour"
    class="grid grid-cols-1 lg:grid-cols-12 gap-8 container mx-auto px-4 py-6"
  >
    <div class="col-span-12 lg:col-span-8">
      <h1 class="text-3xl font-semibold mb-4">{{ tour.name }}</h1>
      <div
        class="flex flex-col sm:flex-row items-start sm:items-center gap-4 mb-6"
      >
        <div class="flex items-center">
          <span class="text-yellow-400 text-xl font-bold">{{
            averageRating
          }}</span>
          <div class="flex ml-2">
            <StarIcon
              v-for="i in 5"
              :key="i"
              class="h-5 w-5"
              :class="
                i <= Math.round(averageRating)
                  ? 'text-yellow-400'
                  : 'text-gray-300'
              "
            />
          </div>
          <span class="ml-2 text-gray-600"
            >({{ reviews.length }} đánh giá)</span
          >
        </div>
        <div class="flex items-center gap-2">
          <i class="fas fa-map-marker-alt text-blue-500"></i>
          <span class="text-gray-600">{{ tour.destination }}</span>
        </div>
      </div>

      <section class="mb-8" v-if="tour.imageUrls && tour.imageUrls.length">
        <div class="relative h-[500px] rounded-xl overflow-hidden group">
          <img
            :src="tour.imageUrls[currentIndex]"
            :alt="tour.name"
            class="w-full h-full object-cover transition-transform duration-500"
          />
          <button
            @click="prevImage"
            class="absolute left-4 top-1/2 -translate-y-1/2 w-10 h-10 rounded-full bg-white shadow-lg flex items-center justify-center hover:bg-gray-100"
          >
            <i class="fas fa-chevron-left"></i>
          </button>
          <button
            @click="nextImage"
            class="absolute right-4 top-1/2 -translate-y-1/2 w-10 h-10 rounded-full bg-white shadow-lg flex items-center justify-center hover:bg-gray-100"
          >
            <i class="fas fa-chevron-right"></i>
          </button>
          <div
            class="absolute bottom-4 right-4 px-3 py-1.5 rounded-full bg-black/50 text-white text-sm"
          >
            {{ currentIndex + 1 }}/{{ tour.imageUrls.length }}
          </div>
        </div>
        <div class="mt-4 px-1">
          <div class="flex gap-3 overflow-x-auto py-2 px-1 scrollbar-thin">
            <button
              v-for="(img, index) in tour.imageUrls"
              :key="index"
              @click="selectImage(index)"
              class="relative flex-none w-32 h-20 rounded-lg overflow-hidden"
              :class="[
                currentIndex === index
                  ? 'ring-2 ring-blue-500 ring-offset-2'
                  : 'opacity-60 hover:opacity-100',
              ]"
            >
              <img :src="img" class="w-full h-full object-cover" />
            </button>
          </div>
        </div>
      </section>

      <section class="mb-8">
        <div class="border-b border-gray-200">
          <nav class="flex space-x-8" aria-label="Tabs">
            <button
              @click="selectedTab = 'schedule'"
              :class="[
                selectedTab === 'schedule'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500',
                'py-4 px-1 border-b-2 font-medium text-sm',
              ]"
            >
              Lịch trình
            </button>
            <button
              @click="selectedTab = 'details'"
              :class="[
                selectedTab === 'details'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500',
                'py-4 px-1 border-b-2 font-medium text-sm',
              ]"
            >
              Chi tiết
            </button>
            <button
              @click="selectedTab = 'reviews'"
              :class="[
                selectedTab === 'reviews'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500',
                'py-4 px-1 border-b-2 font-medium text-sm',
              ]"
            >
              Đánh giá
            </button>
          </nav>
        </div>
        <div class="py-6">
          <div v-if="selectedTab === 'schedule'" class="space-y-8">
            <div class="bg-blue-50 p-4 rounded-lg flex items-center gap-2">
              <i class="fas fa-clock text-blue-500"></i
              ><span class="font-medium">Thời gian:</span
              ><span>{{ tour.durationDays }} ngày</span>
            </div>
            <div
              v-for="(day, dayIndex) in itinerary"
              :key="dayIndex"
              class="border rounded-lg"
            >
              <div
                @click="toggleDay(dayIndex)"
                class="p-4 cursor-pointer flex justify-between items-center"
              >
                <h3 class="font-semibold text-lg">
                  NGÀY {{ day.day }}: {{ day.title }}
                </h3>
                <i
                  class="fas"
                  :class="
                    expandedDays[dayIndex] ? 'fa-chevron-up' : 'fa-chevron-down'
                  "
                ></i>
              </div>
              <div v-show="expandedDays[dayIndex]" class="p-4 border-t">
                <div class="relative">
                  <div
                    class="absolute top-0 bottom-0 left-[2.45rem] w-0.5 bg-gray-200"
                  ></div>
                  <div class="space-y-6">
                    <div
                      v-for="(activity, index) in day.activities"
                      :key="index"
                      class="relative flex gap-6 items-start"
                    >
                      <div class="flex-none">
                        <div
                          class="relative z-10 w-12 h-12 flex items-center justify-center rounded-full bg-blue-100 text-blue-600"
                        >
                          <i :class="activity.icon"></i>
                        </div>
                        <div class="mt-2 text-sm text-center">
                          {{ activity.time }}
                        </div>
                      </div>
                      <div class="flex-1 bg-white p-4 rounded-lg border">
                        <h4 class="font-medium">{{ activity.activity }}</h4>
                        <p class="mt-1 text-gray-600">
                          {{ activity.description }}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div
            v-if="selectedTab === 'details'"
            class="space-y-4 prose max-w-none"
          >
            <div v-html="tour.description"></div>
          </div>
          <div v-if="selectedTab === 'reviews'" class="space-y-6">
            <div
              v-for="review in reviews"
              :key="review.id"
              class="border-b pb-6"
            >
              <div class="flex justify-between mb-2">
                <div>
                  <h4 class="font-medium">{{ review.author }}</h4>
                  <div class="flex items-center">
                    <StarIcon
                      v-for="i in 5"
                      :key="i"
                      class="h-4 w-4"
                      :class="
                        i <= review.rating ? 'text-yellow-400' : 'text-gray-300'
                      "
                    />
                  </div>
                </div>
                <div class="text-sm text-gray-500">{{ review.date }}</div>
              </div>
              <p class="text-gray-600">{{ review.content }}</p>
            </div>
          </div>
        </div>
      </section>

      <section class="mb-8">
        <h2 class="text-2xl font-semibold mb-4">Thông tin liên hệ</h2>
        <div class="bg-white p-6 rounded-lg shadow-md">
          <p class="text-sm text-gray-600 mb-4">
            Vui lòng cung cấp thông tin người đại diện đặt tour để chúng tôi
            liên hệ khi cần thiết.
          </p>
          <form @submit.prevent="handleDirectBooking" class="space-y-4">
            <div>
              <label
                for="fullName"
                class="block text-sm font-medium text-gray-700"
                >Họ và tên</label
              >
              <input
                v-model="contactInfo.fullName"
                type="text"
                id="fullName"
                required
                class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <div>
              <label for="phone" class="block text-sm font-medium text-gray-700"
                >Số điện thoại</label
              >
              <input
                v-model="contactInfo.phone"
                type="tel"
                id="phone"
                required
                class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <div>
              <label for="email" class="block text-sm font-medium text-gray-700"
                >Email</label
              >
              <input
                v-model="contactInfo.email"
                type="email"
                id="email"
                required
                class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <div>
              <label for="notes" class="block text-sm font-medium text-gray-700"
                >Ghi chú thêm (tùy chọn)</label
              >
              <textarea
                v-model="contactInfo.notes"
                id="notes"
                rows="3"
                class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
              ></textarea>
            </div>
          </form>
        </div>
      </section>
    </div>

    <div class="col-span-12 lg:col-span-4">
      <div class="sticky top-4">
        <div class="bg-white rounded-xl shadow-lg overflow-hidden">
          <div class="p-6 bg-gray-50 border-b">
            <div class="flex items-baseline gap-2">
              <span class="text-3xl font-bold text-blue-600">{{
                formatPrice(currentPrice)
              }}</span>
              <span
                v-if="currentPriceData?.promoPrice"
                class="text-base text-gray-400 line-through"
                >{{ formatPrice(currentPriceData?.price) }}</span
              >
            </div>
            <p class="text-sm text-gray-600 mt-1">*Giá/người lớn</p>
          </div>
          <div class="p-6 space-y-5">
            <div>
              <label class="block text-sm font-medium mb-2"
                >Chọn ngày khởi hành</label
              >
              <div class="border rounded-lg p-3 sm:p-4">
                <div class="flex justify-between items-center mb-4">
                  <button
                    @click="prevMonth"
                    class="w-8 h-8 flex items-center justify-center rounded-full hover:bg-gray-100 transition-colors"
                  >
                    <i class="fas fa-chevron-left text-sm"></i>
                  </button>
                  <span class="font-semibold text-center capitalize w-32">{{
                    monthYearDisplay
                  }}</span>
                  <button
                    @click="nextMonth"
                    class="w-8 h-8 flex items-center justify-center rounded-full hover:bg-gray-100 transition-colors"
                  >
                    <i class="fas fa-chevron-right text-sm"></i>
                  </button>
                </div>
                <div
                  class="grid grid-cols-7 gap-1 text-center text-xs text-gray-500 font-medium"
                >
                  <div
                    v-for="day in ['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN']"
                    :key="day"
                    class="py-2"
                  >
                    {{ day }}
                  </div>
                </div>
                <div class="grid grid-cols-7 gap-1 text-center">
                  <div
                    v-for="(day, index) in calendarGrid"
                    :key="index"
                    class="py-1 flex justify-center items-center"
                  >
                    <button
                      v-if="day.isCurrentMonth"
                      @click="selectDate(day.fullDateString)"
                      :disabled="!day.isAvailable"
                      :class="[
                        'w-9 h-9 sm:w-10 sm:h-10 rounded-full flex flex-col items-center justify-center text-sm transition-colors duration-200',
                        {
                          'cursor-not-allowed text-gray-300': !day.isAvailable,
                        },
                        { 'cursor-pointer': day.isAvailable },
                        {
                          'bg-blue-100 text-blue-700 hover:bg-blue-200':
                            day.isAvailable && !day.isSelected,
                        },
                        {
                          'bg-blue-600 text-white font-bold shadow-md':
                            day.isSelected,
                        },
                        {
                          'ring-2 ring-offset-1 ring-red-500':
                            day.isToday && !day.isSelected,
                        },
                        {
                          'text-red-500 font-bold':
                            day.isToday && !day.isSelected,
                        },
                      ]"
                    >
                      <span>{{ day.dayOfMonth }}</span>
                      <span
                        v-if="day.isAvailable && day.seats !== null"
                        class="text-[10px] -mt-0.5"
                        :class="
                          day.isSelected ? 'text-blue-200' : 'text-gray-500'
                        "
                      >
                        {{ day.seats }} chỗ
                      </span>
                    </button>
                    <span
                      v-else
                      class="w-9 h-9 sm:w-10 sm:h-10 flex items-center justify-center text-sm text-gray-300"
                      >{{ day.dayOfMonth }}</span
                    >
                  </div>
                </div>
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium">Số lượng khách</label>
              <div
                v-for="(type, key) in {
                  adults: { label: 'Người lớn', desc: '> 10 tuổi' },
                  children: { label: 'Trẻ em', desc: '2 - 10 tuổi' },
                }"
                :key="key"
                class="p-2.5 border rounded-lg mt-2"
              >
                <div class="flex justify-between items-center">
                  <div>
                    <p class="font-medium">{{ type.label }}</p>
                    <div class="flex items-center gap-2">
                      <p class="text-xs text-gray-500">{{ type.desc }}</p>
                      <p
                        v-if="key === 'children'"
                        class="text-xs text-green-600"
                      >
                        -25%
                      </p>
                    </div>
                  </div>
                  <div class="flex items-center gap-1.5">
                    <button
                      @click="updateTravelers(key, 'decrease')"
                      class="w-7 h-7 rounded-lg border flex items-center justify-center disabled:opacity-50"
                    >
                      <i class="fas fa-minus text-xs"></i>
                    </button>
                    <span class="w-8 text-center">{{
                      bookingForm.travelers[key]
                    }}</span>
                    <button
                      @click="updateTravelers(key, 'increase')"
                      :disabled="!canAddTraveler"
                      class="w-7 h-7 rounded-lg border flex items-center justify-center disabled:opacity-50"
                    >
                      <i class="fas fa-plus text-xs"></i>
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <div class="border-t pt-4 space-y-3">
              <div class="flex justify-between items-center text-lg font-bold">
                <span>Tổng cộng</span>
                <span class="text-2xl text-blue-600">{{
                  formatPrice(total)
                }}</span>
              </div>
              <button
                v-if="activeCartId"
                @click="handleAddToCart"
                :disabled="!bookingForm.selectedDate || !isLoggedIn"
                class="w-full bg-green-600 text-white py-2.5 rounded-lg font-medium hover:bg-green-700 disabled:opacity-50"
                :title="!isLoggedIn ? 'Vui lòng đăng nhập' : ''"
              >
                Thêm vào chuyến đi
              </button>
              <button
                @click="handleDirectBooking"
                :disabled="isProcessing"
                class="w-full bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 disabled:opacity-50 flex items-center justify-center text-lg"
              >
                <i v-if="isProcessing" class="fas fa-spinner fa-spin mr-2"></i>
                <span>{{
                  isProcessing ? "Đang xử lý..." : "Xác nhận & Đặt chỗ"
                }}</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div v-else class="text-center py-20">
    <p class="text-2xl font-semibold text-red-500">
      Lỗi: Không thể tải dữ liệu tour.
    </p>
    <p class="text-gray-600">Vui lòng thử lại sau.</p>
  </div>
</template>

<style scoped>
.sticky {
  position: -webkit-sticky;
  position: sticky;
  top: 1rem;
}
.scrollbar-thin {
  scrollbar-width: thin;
  scrollbar-color: #cbd5e1 transparent;
}
.scrollbar-thin::-webkit-scrollbar {
  height: 4px;
}
.scrollbar-thin::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 4px;
}
</style>
