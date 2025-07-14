<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import { StarIcon } from "@heroicons/vue/24/solid";

const route = useRoute();
const tourId = Number(route.params.id);

const tour = ref(null);
const reviews = ref([]);
const itinerary = ref([]);
const availableDates = ref([]);
const isLoading = ref(true);
const selectedTab = ref("schedule");

// --- LIFECYCLE HOOK: GỌI TẤT CẢ API KHI COMPONENT ĐƯỢC TẠO ---
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
        date: d.departureDate,
        display: new Date(d.departureDate).toLocaleDateString("vi-VN", {
          day: "2-digit",
          month: "2-digit",
        }),
        seats: d.seatCount - d.bookedSeats,
        price: d.adultPrice,
        promoPrice: d.discount > 0 ? d.adultPrice - d.discount : null,
      }));
      // Tự động chọn ngày đầu tiên nếu có
      if (availableDates.value.length > 0) {
        selectDate(availableDates.value[0].date);
      }
    }
  } catch (error) {
    console.error("Lỗi khi tải dữ liệu chi tiết tour:", error);
  } finally {
    isLoading.value = false;
  }
});

// --- LOGIC CHO BOOKING FORM ---
const bookingForm = ref({
  selectedDate: null,
  travelers: { adults: 1, children: 0, infants: 0 },
  maxTravelers: 20,
  prices: { child_multiplier: 0.75, infant: 990000, singleRoomExtra: 6000000 },
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
  const singleRoomFee =
    bookingForm.value.travelers.adults === 1
      ? bookingForm.value.prices.singleRoomExtra
      : 0;
  return subtotal.value + singleRoomFee;
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
  bookingForm.value.selectedDate = date;
};

const formatPrice = (price) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    price
  );

const getAvailabilityClass = (seats) => {
  if (seats <= 5) return "text-red-500";
  if (seats <= 10) return "text-orange-500";
  return "text-green-500";
};

const handleBooking = () => {
  if (!bookingForm.value.selectedDate) {
    alert("Vui lòng chọn ngày khởi hành");
    return;
  }
  console.log("Booking details:", {
    date: bookingForm.value.selectedDate,
    travelers: bookingForm.value.travelers,
    total: total.value,
  });
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
    class="grid grid-cols-12 gap-4 container mx-auto px-4 py-6"
  >
    <div class="col-span-12">
      <h1 class="text-3xl font-semibold mb-4">{{ tour.name }}</h1>
      <div class="flex items-center gap-4 mb-6">
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
                <div class="grid grid-cols-2 gap-2">
                  <button
                    v-for="date in availableDates"
                    :key="date.date"
                    @click="selectDate(date.date)"
                    :class="[
                      'p-3 border rounded-lg text-left',
                      bookingForm.selectedDate === date.date
                        ? 'border-blue-500 bg-blue-50'
                        : 'hover:border-blue-300',
                    ]"
                  >
                    <div class="flex items-center justify-between mb-1">
                      <span class="font-medium">{{ date.display }}</span
                      ><span
                        v-if="date.promoPrice"
                        class="text-xs font-medium text-red-500 bg-red-50 px-2 rounded-full"
                        >Giảm giá</span
                      >
                    </div>
                    <div class="text-sm space-y-1">
                      <div>
                        {{ formatPrice(date.promoPrice || date.price) }}
                      </div>
                      <div
                        :class="['text-xs', getAvailabilityClass(date.seats)]"
                      >
                        Còn {{ date.seats }} chỗ
                      </div>
                    </div>
                  </button>
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
                  v-if="bookingForm.travelers.adults === 1"
                  class="flex justify-between"
                >
                  <span class="text-gray-600">Phụ thu phòng đơn</span
                  ><span class="font-medium text-blue-600"
                    >+{{
                      formatPrice(bookingForm.prices.singleRoomExtra)
                    }}</span
                  >
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
