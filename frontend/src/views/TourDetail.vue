<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { StarIcon } from "@heroicons/vue/24/solid";

const route = useRoute();
const router = useRouter();
const tourId = Number(route.params.id);

const tour = ref(null);
const reviews = ref([]);
const itinerary = ref([]);
const availableDates = ref([]);
const isLoading = ref(true);
const selectedTab = ref("schedule");

// --- TRẠNG THÁI CHO LỊCH ---
const today = new Date();
today.setHours(0, 0, 0, 0); // Chuẩn hóa về đầu ngày
const currentDisplayDate = ref(
  new Date(today.getFullYear(), today.getMonth(), 1)
);

// --- LIFECYCLE HOOK: GỌI TẤT CẢ API KHI COMPONENT ĐƯỢỢC TẠO ---
onMounted(async () => {
  try {
    // Gọi đồng thời 4 API để tăng tốc độ tải trang
    const [
      tourRes,
      reviewsRes,
      itineraryRes, // Gọi API itinerary mới
      departuresRes,
    ] = await Promise.all([
      fetch(`http://localhost:8080/api/v1/tours/${tourId}`),
      fetch(`http://localhost:8080/api/v1/tours/${tourId}/reviews`),
      fetch(`http://localhost:8080/api/v1/tours/${tourId}/itinerary`), // API cho lịch trình chi tiết
      fetch(`http://localhost:8080/api/v1/tours/${tourId}/departures`),
    ]);

    // Xử lý và gán dữ liệu vào các biến state
    const tourData = await tourRes.json();
    if (tourData.statusCode === 200) {
      tour.value = tourData.data;
    }

    const reviewsData = await reviewsRes.json();
    if (reviewsData.statusCode === 200) {
      reviews.value = reviewsData.data;
    }

    const itineraryData = await itineraryRes.json();
    if (itineraryData.statusCode === 200) {
      itinerary.value = itineraryData.data;
    }

    const departuresData = await departuresRes.json();
    if (departuresData.statusCode === 200) {
      availableDates.value = departuresData.data.map((d) => ({
        departureId: d.id, // SỬA LỖI: Lưu lại ID của ngày khởi hành
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
      // Tự động chọn ngày đầu tiên và set calendar view
      if (availableDates.value.length > 0) {
        const firstAvailableDate = new Date(availableDates.value[0].date);
        selectDate(availableDates.value[0].date);
        // Đặt lịch về tháng của ngày khởi hành đầu tiên
        currentDisplayDate.value = new Date(
          firstAvailableDate.getFullYear(),
          firstAvailableDate.getMonth(),
          1
        );
      }
    }
  } catch (error) {
    console.error("Lỗi khi tải dữ liệu chi tiết tour:", error);
  } finally {
    isLoading.value = false;
  }
});

// --- LOGIC CHO LỊCH ---
const availableDatesMap = computed(() => {
  const map = new Map();
  availableDates.value.forEach((d) => {
    const dateObj = new Date(d.date);
    // Chuẩn hóa key của map về dạng YYYY-MM-DD để tra cứu
    const normalizedDateKey = new Date(
      dateObj.getFullYear(),
      dateObj.getMonth(),
      dateObj.getDate()
    )
      .toISOString()
      .split("T")[0];
    map.set(normalizedDateKey, d); // Map key YYYY-MM-DD với object ngày đầy đủ
  });
  return map;
});

const monthYearDisplay = computed(() => {
  return currentDisplayDate.value.toLocaleDateString("vi-VN", {
    month: "long",
    year: "numeric",
  });
});

const calendarGrid = computed(() => {
  const year = currentDisplayDate.value.getFullYear();
  const month = currentDisplayDate.value.getMonth();

  const firstDayOfMonth = new Date(year, month, 1);
  const lastDayOfMonth = new Date(year, month + 1, 0);
  const daysInMonth = lastDayOfMonth.getDate();
  const startDayOfWeek = firstDayOfMonth.getDay();

  const grid = [];

  const lastDayOfPrevMonth = new Date(year, month, 0).getDate();
  for (let i = startDayOfWeek - 1; i >= 0; i--) {
    grid.push({
      date: new Date(year, month - 1, lastDayOfPrevMonth - i),
      isCurrentMonth: false,
    });
  }

  for (let day = 1; day <= daysInMonth; day++) {
    grid.push({ date: new Date(year, month, day), isCurrentMonth: true });
  }

  const endDayOfWeek = lastDayOfMonth.getDay();
  for (let i = 1; i < 7 - endDayOfWeek; i++) {
    grid.push({ date: new Date(year, month + 1, i), isCurrentMonth: false });
  }

  return grid.map((dayObj) => {
    const normalizedDateKey = dayObj.date.toISOString().split("T")[0];
    const availableDateInfo = availableDatesMap.value.get(normalizedDateKey);
    const isAvailable = !!availableDateInfo;
    const isSelected =
      isAvailable && bookingForm.value.selectedDate === availableDateInfo.date;
    const isToday = dayObj.date.getTime() === today.getTime();

    return {
      ...dayObj,
      fullDateString: availableDateInfo ? availableDateInfo.date : null,
      dayOfMonth: dayObj.date.getDate(),
      isAvailable,
      isSelected,
      isToday,
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

// --- LOGIC CHO BOOKING FORM ---
const bookingForm = ref({
  selectedDate: null,
  travelers: { adults: 1, children: 0, infants: 0 },
  maxTravelers: 20,
  prices: { child_multiplier: 0.75, infant: 990000 },
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

const totalTravelers = computed(() => {
  const { adults, children, infants } = bookingForm.value.travelers;
  return adults + children + infants;
});

const canAddTraveler = computed(
  () => totalTravelers.value < bookingForm.value.maxTravelers
);

const subtotal = computed(() => {
  if (!currentPrice.value) return 0;
  const { adults, children, infants } = bookingForm.value.travelers;
  const { prices } = bookingForm.value;
  return (
    adults * currentPrice.value +
    children * (currentPrice.value * prices.child_multiplier) +
    infants * prices.infant
  );
});

const total = computed(() => {
  return subtotal.value;
});

const updateTravelers = (type, action) => {
  const current = bookingForm.value.travelers[type];
  if (action === "increase" && canAddTraveler.value) {
    bookingForm.value.travelers[type]++;
  } else if (action === "decrease" && current > 0) {
    if (type === "adults" && current <= 1) return;
    bookingForm.value.travelers[type]--;
  }
};

const selectDate = (date) => {
  if (date) {
    bookingForm.value.selectedDate = date;
  }
};

const formatPrice = (price) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    price
  );

const getAvailabilityClass = (seats) => {
  if (seats <= 5) return "text-red-500 font-semibold";
  if (seats <= 10) return "text-orange-500 font-semibold";
  return "text-green-600";
};

const handleBooking = async () => {
  if (!bookingForm.value.selectedDate) {
    alert("Vui lòng chọn ngày khởi hành");
    return;
  }

  // Tìm ngày khởi hành đã chọn
  const selectedDeparture = availableDates.value.find(
    (d) => d.date === bookingForm.value.selectedDate
  );
  if (!selectedDeparture) {
    alert("Lỗi: Không tìm thấy thông tin ngày khởi hành. Vui lòng thử lại.");
    return;
  }

  // Chuẩn bị dữ liệu giữ chỗ (mua ngay)
  const reservationRequest = {
    userId: 1, // TODO: Lấy userId thực tế
    tourId: tour.value.id,
    departureId: selectedDeparture.departureId,
    numberOfAdults: bookingForm.value.travelers.adults,
    numberOfChildren: bookingForm.value.travelers.children,
    customerName: '', // Sẽ nhập ở bước tiếp theo
    phone: '',
    email: '',
    notes: '',
    totalPrice: total.value,
  };

  // Gọi API giữ chỗ (tạo order tạm thời)
  try {
    const response = await fetch('http://localhost:8080/api/v1/orders/reserve-tour-direct', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(reservationRequest),
    });
    const result = await response.json();
    if (response.ok && result.statusCode === 201) {
      const order = result.data;
      // Chuyển sang trang checkout, truyền orderId và các thông tin cần thiết
      router.push({
        name: 'checkout',
        query: {
          orderId: order.id,
          tourId: tour.value.id,
          tourName: tour.value.name,
          selectedDate: bookingForm.value.selectedDate,
          departureId: selectedDeparture.departureId,
          travelers: JSON.stringify(bookingForm.value.travelers),
          totalPrice: total.value,
        },
      });
    } else {
      alert(result.message || 'Không thể giữ chỗ.');
    }
  } catch (error) {
    alert('Lỗi kết nối máy chủ.');
  }
};

const averageRating = computed(() => {
  if (!reviews.value || reviews.value.length === 0) return "Mới";
  const total = reviews.value.reduce((acc, review) => acc + review.rating, 0);
  return (total / reviews.value.length).toFixed(1);
});

// Logic cho gallery ảnh
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

// Logic cho collapse/expand lịch trình
const expandedDays = ref({});
const toggleDay = (dayIndex) => {
  expandedDays.value[dayIndex] = !expandedDays.value[dayIndex];
};
</script>

<template>
  <div v-if="isLoading" class="flex justify-center items-center h-screen">
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
    class="grid grid-cols-1 lg:grid-cols-12 gap-2 sm:gap-4 container mx-auto px-2 sm:px-4 py-4 sm:py-6"
  >
    <div class="col-span-12">
      <h1 class="text-2xl sm:text-3xl font-semibold mb-4">{{ tour.name }}</h1>
      <div
        class="flex flex-col sm:flex-row items-start sm:items-center gap-2 sm:gap-4 mb-6"
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
    </div>

    <div class="grid grid-cols-12 col-span-12 gap-6">
      <div class="col-span-12 lg:col-span-8">
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
                      expandedDays[dayIndex]
                        ? 'fa-chevron-up'
                        : 'fa-chevron-down'
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
                          i <= review.rating
                            ? 'text-yellow-400'
                            : 'text-gray-300'
                        "
                      />
                    </div>
                  </div>
                  <div class="text-sm text-gray-500">
                    {{ review.date }}
                  </div>
                </div>
                <p class="text-gray-600">{{ review.content }}</p>
              </div>
            </div>
          </div>
        </section>
      </div>

      <div class="col-span-12 lg:col-span-4">
        <div class="sticky top-4">
          <div class="bg-white rounded-xl shadow-lg overflow-hidden">
            <div class="p-4 bg-gray-50">
              <div class="flex items-baseline gap-2">
                <span class="text-2xl font-bold text-blue-600">{{
                  formatPrice(currentPrice)
                }}</span
                ><span
                  v-if="currentPriceData?.promoPrice"
                  class="text-sm text-gray-400 line-through"
                  >{{ formatPrice(currentPriceData?.price) }}</span
                >
              </div>
              <p class="text-sm text-gray-600">*Giá/người lớn</p>
            </div>
            <div class="p-4 space-y-4">
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
                      v-for="day in ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7']"
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
                          'w-9 h-9 sm:w-10 sm:h-10 rounded-full flex items-center justify-center text-sm transition-colors duration-200',
                          {
                            'cursor-not-allowed text-gray-300':
                              !day.isAvailable,
                            'cursor-pointer': day.isAvailable,
                            'bg-blue-100 text-blue-700 hover:bg-blue-200':
                              day.isAvailable && !day.isSelected,
                            'bg-blue-600 text-white font-bold shadow-md':
                              day.isSelected,
                            'ring-2 ring-offset-1 ring-red-500':
                              day.isToday && !day.isSelected,
                            'text-red-500 font-bold':
                              day.isToday && !day.isSelected,
                          },
                        ]"
                      >
                        {{ day.dayOfMonth }}
                      </button>
                      <span
                        v-else
                        class="w-9 h-9 sm:w-10 sm:h-10 flex items-center justify-center text-sm text-gray-300"
                      >
                        {{ day.dayOfMonth }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="space-y-2">
                <label class="block text-sm font-medium">Số lượng khách</label>
                <div
                  v-for="(type, key) in {
                    adults: { label: 'Người lớn', desc: '> 10 tuổi', min: 1 },
                    children: { label: 'Trẻ em', desc: '2 - 10 tuổi', min: 0 },
                    infants: { label: 'Em bé', desc: '< 2 tuổi', min: 0 },
                  }"
                  :key="key"
                  class="p-2.5 border rounded-lg"
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
                        :disabled="bookingForm.travelers[key] <= type.min"
                        class="w-7 h-7 rounded-lg border flex items-center justify-center disabled:opacity-50"
                      >
                        <i class="fas fa-minus text-xs"></i></button
                      ><span class="w-8 text-center">{{
                        bookingForm.travelers[key]
                      }}</span
                      ><button
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
              <div class="border-t pt-4 space-y-2">
                <div class="flex justify-between">
                  <span class="text-gray-600">Tạm tính</span
                  ><span class="font-medium">{{ formatPrice(subtotal) }}</span>
                </div>
                <div
                  class="flex justify-between items-center text-lg font-bold pt-2"
                >
                  <span>Tổng cộng</span
                  ><span class="text-blue-600">{{ formatPrice(total) }}</span>
                </div>
              </div>
              <button
                @click="handleBooking"
                :disabled="!bookingForm.selectedDate"
                class="w-full bg-blue-600 text-white py-3 rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50"
              >
                Đặt tour ngay
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
