<script setup>
import { ref, computed } from "vue";
import { useRoute } from "vue-router";
import { StarIcon } from "@heroicons/vue/24/solid";

const route = useRoute();
const id = Number(route.params.id);

// Thêm data cho reviews
const reviews = ref([
  {
    id: 1,
    author: "Nguyễn Văn A",
    rating: 5,
    date: "15/03/2024",
    content:
      "Tour rất tuyệt vời, hướng dẫn viên nhiệt tình, cảnh đẹp, đồ ăn ngon!",
    images: [
      "https://images.unsplash.com/photo-1583579262521-0fe89b9d013c?w=300",
      "https://images.unsplash.com/photo-1583579262521-0fe89b9d013c?w=300",
    ],
  },
  {
    id: 2,
    author: "Trần Thị B",
    rating: 4,
    date: "10/03/2024",
    content: "Chuyến đi rất thú vị, tuy nhiên thời gian hơi gấp rút.",
    images: [],
  },
]);

// Thêm data cho tour details với lịch trình nhiều ngày
const tourDetails = ref({
  duration: {
    days: 3,
    nights: 2,
  },
  schedule: [
    {
      day: 1,
      title: "KHÁM PHÁ HANG ĐỘNG - ẨM THỰC ĐỊA PHƯƠNG",
      activities: [
        {
          time: "07:00",
          activity: "Đón khách",
          description: "Xe và HDV đón khách tại các điểm hẹn trong thành phố",
          icon: "fas fa-bus",
        },
        {
          time: "09:30",
          activity: "Tham quan hang động",
          description:
            "Khám phá hệ thống hang động với hướng dẫn viên chuyên nghiệp",
          icon: "fas fa-mountain",
        },
        {
          time: "12:00",
          activity: "Ăn trưa",
          description: "Thưởng thức ẩm thực địa phương tại nhà hàng",
          icon: "fas fa-utensils",
        },
        {
          time: "14:00",
          activity: "Check-in khách sạn",
          description: "Nhận phòng và nghỉ ngơi tại khách sạn 4 sao",
          icon: "fas fa-hotel",
        },
        {
          time: "19:00",
          activity: "Ăn tối",
          description: "Buffet tối tại khách sạn",
          icon: "fas fa-utensils",
        },
      ],
    },
    {
      day: 2,
      title: "KHÁM PHÁ LÀNG NGHỀ - TRẢI NGHIỆM KAYAK",
      activities: [
        {
          time: "07:00",
          activity: "Ăn sáng",
          description: "Buffet sáng tại khách sạn",
          icon: "fas fa-coffee",
        },
        {
          time: "09:00",
          activity: "Thăm làng nghề",
          description: "Tham quan và tìm hiểu về làng nghề truyền thống",
          icon: "fas fa-hands",
        },
        {
          time: "12:00",
          activity: "Ăn trưa",
          description: "Thưởng thức đặc sản địa phương",
          icon: "fas fa-utensils",
        },
        {
          time: "14:00",
          activity: "Chèo thuyền kayak",
          description: "Trải nghiệm chèo thuyền kayak trên sông",
          icon: "fas fa-ship",
        },
        {
          time: "19:00",
          activity: "Ăn tối",
          description: "BBQ tối tại bãi biển",
          icon: "fas fa-fire",
        },
      ],
    },
    {
      day: 3,
      title: "KHÁM PHÁ BIỂN ĐẢO - TẠM BIỆT",
      activities: [
        {
          time: "07:00",
          activity: "Ăn sáng",
          description: "Buffet sáng tại khách sạn",
          icon: "fas fa-coffee",
        },
        {
          time: "09:00",
          activity: "Tham quan đảo",
          description: "Khám phá các đảo nhỏ bằng cano",
          icon: "fas fa-island-tropical",
        },
        {
          time: "12:00",
          activity: "Ăn trưa",
          description: "Ăn trưa với hải sản tươi sống",
          icon: "fas fa-fish",
        },
        {
          time: "14:00",
          activity: "Tự do mua sắm",
          description: "Tham quan và mua sắm tại chợ địa phương",
          icon: "fas fa-shopping-bag",
        },
        {
          time: "16:00",
          activity: "Trở về",
          description: "Khởi hành về điểm đón ban đầu",
          icon: "fas fa-bus-alt",
        },
      ],
    },
  ],
  highlights: [
    "Khám phá hang động kỳ vĩ với hệ thống thạch nhũ độc đáo",
    "Trải nghiệm ẩm thực địa phương đặc sắc",
    "Chèo thuyền kayak trên dòng sông trong xanh",
    "Thăm làng nghề truyền thống",
    "BBQ tối tại bãi biển",
    "Khám phá các đảo nhỏ bằng cano",
  ],
  included: [
    "Xe đưa đón theo chương trình",
    "Hướng dẫn viên chuyên nghiệp",
    "Khách sạn 4 sao (2 đêm)",
    "Các bữa ăn theo chương trình",
    "Vé tham quan các điểm trong lịch trình",
    "Bảo hiểm du lịch",
    "Nước uống trên xe",
  ],
});

// State cho booking form
const bookingForm = ref({
  selectedDate: null,
  travelers: {
    adults: 1,
    children: 0,
    infants: 0,
  },
  maxTravelers: 20,
  prices: {
    base: 24990000,
    child: 0.75, // 75% giá người lớn
    infant: 990000,
    singleRoomExtra: 6000000,
  },
  availableDates: [
    {
      date: "2024-07-17",
      display: "17/07",
      seats: 15,
      price: 24990000,
    },
    {
      date: "2024-07-31",
      display: "31/07",
      seats: 8,
      price: 22990000,
      promoPrice: 19990000,
    },
    {
      date: "2024-08-07",
      display: "07/08",
      seats: 20,
      price: 24990000,
    },
  ],
});

// Computed properties cho booking
const currentPrice = computed(() => {
  const selectedDateInfo = bookingForm.value.availableDates.find(
    (d) => d.date === bookingForm.value.selectedDate
  );
  return (
    selectedDateInfo?.promoPrice ||
    selectedDateInfo?.price ||
    bookingForm.value.prices.base
  );
});

const totalTravelers = computed(() => {
  const { adults, children, infants } = bookingForm.value.travelers;
  return adults + children + infants;
});

const canAddTraveler = computed(() => {
  return totalTravelers.value < bookingForm.value.maxTravelers;
});

const subtotal = computed(() => {
  const { adults, children, infants } = bookingForm.value.travelers;
  const { prices } = bookingForm.value;

  return (
    adults * currentPrice.value +
    children * (currentPrice.value * prices.child) +
    infants * prices.infant
  );
});

const total = computed(() => {
  const { adults } = bookingForm.value.travelers;
  return (
    subtotal.value +
    (adults === 1 ? bookingForm.value.prices.singleRoomExtra : 0)
  );
});

// Methods cho booking
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

const formatPrice = (price) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(price);
};

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
  const total = reviews.value.reduce((acc, review) => acc + review.rating, 0);
  return (total / reviews.value.length).toFixed(1);
});

const tour = computed(() => {
  return tours.value.find((t) => t.id === id) || {};
});

const images = [
  "https://cdn2.ivivu.com/2019/12/10/16/thap-doi.jpg",
  "https://cdn2.ivivu.com/2022/05/26/14/ivivu-ghenh-rang-tien-sa-1.jpg",
  "https://cdn2.ivivu.com/2022/05/27/15/ivivu-quy-nhon-hinh-bia-750x460.jpg",
  "https://cdn2.ivivu.com/2022/02/25/15/ivivu-thap-nghinh-phong-750x460.gif",
];

const currentIndex = ref(0);
const selectedTab = ref("schedule");

const nextImage = () => {
  currentIndex.value = (currentIndex.value + 1) % images.length;
};

const prevImage = () => {
  currentIndex.value = (currentIndex.value - 1 + images.length) % images.length;
};

const selectImage = (index) => {
  currentIndex.value = index;
};

const tours = ref([
  {
    id: 1,
    imageUrl: "",
    title: "Chuyến tham quan trong ngày | Khám phá Phú Yên - Chính phủ",
    location: "Xã Hòa Tâm",
    locationDetail: "Đông Hòa",
    price: "742.383",
    originalPrice: "",
  },
  {
    id: 2,
    imageUrl:
      "https://images.unsplash.com/photo-1583579262521-0fe89b9d013c?q=80&w=1000",
    title: "Sagrada Familia Skip-the-Line Guided Tour",
    location: "Eixample",
    locationDetail: "Barcelona",
    price: "993.264",
    originalPrice: "",
    rating: {
      score: "9.0",
      reviews: "6",
    },
  },
  {
    id: 3,
    imageUrl:
      "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?q=80&w=1000",
    title: "Tour 1 Ngày Ghép Xe Hồ Nhật Nguyệt & Nông Trại Qingjing",
    location: "Hồ Nhật Nguyệt",
    locationDetail: "Nam Đầu",
    price: "949.203",
    originalPrice: "949.204",
  },
  {
    id: 4,
    imageUrl:
      "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=1000",
    title: "Resort với hồ bơi vô cực",
    location: "Đà Nẵng",
    locationDetail: "Việt Nam",
    price: "850.000",
    originalPrice: "",
  },
  {
    id: 5,
    imageUrl:
      "https://images.unsplash.com/photo-1544551763-46a013bb70d5?q=80&w=1000",
    title: "Khám phá thế giới đại dương",
    location: "Nha Trang",
    locationDetail: "Khánh Hòa",
    price: "620.000",
    originalPrice: "680.000",
  },
  {
    id: 6,
    imageUrl:
      "https://images.unsplash.com/photo-1559592413-7cec4d0cae2b?q=80&w=1000",
    title: "Tour khám phá rùa biển",
    location: "Côn Đảo",
    locationDetail: "Bà Rịa - Vũng Tàu",
    price: "1.250.000",
    originalPrice: "",
  },
]);

// Thêm state để quản lý collapse/expand
const expandedDays = ref({});

// Thêm method để toggle
const toggleDay = (dayIndex) => {
  expandedDays.value[dayIndex] = !expandedDays.value[dayIndex];
};
</script>

<template>
  <div class="grid grid-cols-12 gap-4 container mx-auto px-4 py-6">
    <div class="col-span-12">
      <h1 class="text-3xl font-semibold mb-4">{{ tour.title }}</h1>
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
          <span class="text-gray-600"
            >{{ tour.location }}, {{ tour.locationDetail }}</span
          >
        </div>
      </div>
    </div>

    <div class="grid grid-cols-12 col-span-12 gap-6">
      <!-- Cột trái -->
      <div class="col-span-12 lg:col-span-8">
        <!-- Ảnh slider -->
        <section class="mb-8">
          <!-- Ảnh chính -->
          <div class="relative h-[500px] rounded-xl overflow-hidden group">
            <img
              :src="images[currentIndex]"
              :alt="'Tour image ' + (currentIndex + 1)"
              class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
            />

            <!-- Overlay gradient -->
            <div
              class="absolute inset-0 bg-gradient-to-b from-black/10 to-black/40 opacity-0 group-hover:opacity-100 transition-opacity duration-300"
            ></div>

            <!-- Navigation buttons -->
            <button
              @click="prevImage"
              class="absolute left-4 top-1/2 -translate-y-1/2 w-10 h-10 rounded-full bg-white shadow-lg flex items-center justify-center hover:bg-gray-100"
            >
              <i class="fas fa-chevron-left text-gray-800"></i>
            </button>

            <button
              @click="nextImage"
              class="absolute right-4 top-1/2 -translate-y-1/2 w-10 h-10 rounded-full bg-white shadow-lg flex items-center justify-center hover:bg-gray-100"
            >
              <i class="fas fa-chevron-right text-gray-800"></i>
            </button>

            <!-- Image counter -->
            <div
              class="absolute bottom-4 right-4 px-3 py-1.5 rounded-full bg-black/50 backdrop-blur-sm text-white text-sm"
            >
              {{ currentIndex + 1 }}/{{ images.length }}
            </div>
          </div>

          <!-- Ảnh phụ -->
          <div class="mt-4 px-1">
            <div class="flex gap-3 overflow-x-auto py-2 px-1">
              <button
                v-for="(img, index) in images"
                :key="index"
                @click="selectImage(index)"
                class="relative flex-none w-32 h-20 rounded-lg overflow-hidden transition-all duration-300 ease-in-out"
                :class="[
                  currentIndex === index
                    ? 'ring-2 ring-blue-500 ring-offset-2'
                    : 'opacity-60 hover:opacity-100',
                ]"
              >
                <img
                  :src="img"
                  :alt="'Thumbnail ' + (index + 1)"
                  class="w-full h-full object-cover"
                />
                <div
                  v-if="currentIndex === index"
                  class="absolute inset-0 bg-blue-500/10"
                ></div>
              </button>
            </div>
          </div>
        </section>

        <!-- Tabs thông tin -->
        <section class="mb-8">
          <div class="border-b border-gray-200">
            <nav class="flex space-x-8" aria-label="Tabs">
              <button
                v-for="tab in ['schedule', 'details', 'reviews']"
                :key="tab"
                @click="selectedTab = tab"
                :class="[
                  selectedTab === tab
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
                  'whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm',
                ]"
              >
                {{
                  tab === "schedule"
                    ? "Lịch trình"
                    : tab === "details"
                    ? "Chi tiết"
                    : "Đánh giá"
                }}
              </button>
            </nav>
          </div>

          <!-- Nội dung tab -->
          <div class="py-6">
            <!-- Tab Lịch trình -->
            <div v-if="selectedTab === 'schedule'" class="space-y-8">
              <!-- Thông tin thời gian tour -->
              <div class="bg-blue-50 p-4 rounded-lg">
                <div class="flex items-center gap-4">
                  <div class="flex items-center gap-2">
                    <i class="fas fa-clock text-blue-500"></i>
                    <span class="font-medium">Thời gian:</span>
                  </div>
                  <div class="text-gray-700">
                    {{ tourDetails.duration.days }} ngày
                    {{ tourDetails.duration.nights }} đêm
                  </div>
                </div>
              </div>

              <!-- Timeline cho từng ngày -->
              <div
                v-for="(day, dayIndex) in tourDetails.schedule"
                :key="dayIndex"
                class="border border-[#646ae7] rounded-lg overflow-hidden"
              >
                <!-- Tiêu đề ngày có thể click -->
                <div
                  @click="toggleDay(dayIndex)"
                  class="p-4 cursor-pointer hover:bg-gray-100 transition-colors flex justify-between items-center"
                >
                  <h3 class="font-semibold text-lg text-gray-900">
                    NGÀY {{ day.day }}: {{ day.title }}
                  </h3>
                  <i
                    class="fas transition-transform duration-200"
                    :class="
                      expandedDays[dayIndex]
                        ? 'fa-chevron-up'
                        : 'fa-chevron-down'
                    "
                  ></i>
                </div>

                <!-- Các hoạt động trong ngày - có animation -->
                <div
                  v-show="expandedDays[dayIndex]"
                  class="p-4 transition-all duration-200"
                >
                  <div class="relative">
                    <!-- Line kết nối các điểm -->
                    <div
                      class="absolute top-0 bottom-0 left-[2.45rem] w-0.5 bg-gray-200"
                    ></div>

                    <!-- Các hoạt động -->
                    <div class="space-y-6">
                      <div
                        v-for="(activity, index) in day.activities"
                        :key="index"
                        class="relative flex gap-6 items-start group"
                      >
                        <!-- Icon và thời gian -->
                        <div class="flex-none">
                          <div class="flex items-center">
                            <div
                              class="relative z-10 w-12 h-12 flex items-center justify-center rounded-full bg-[#646ae7]-100 text-[#646ae7] group-hover:bg-[#646ae7] group-hover:text-white transition-colors"
                            >
                              <i :class="activity.icon"></i>
                            </div>
                          </div>
                          <div
                            class="mt-2 text-sm font-medium text-gray-500 text-center"
                          >
                            {{ activity.time }}
                          </div>
                        </div>

                        <!-- Nội dung hoạt động -->
                        <div
                          class="flex-1 bg-white p-4 rounded-lg border group-hover:border-[#646ae7] group-hover:shadow-md transition-all"
                        >
                          <h4
                            class="font-medium text-gray-900 group-hover:text-[#646ae7] transition-colors"
                          >
                            {{ activity.activity }}
                          </h4>
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

            <!-- Tab Chi tiết -->
            <div v-if="selectedTab === 'details'" class="space-y-8">
              <!-- Điểm nổi bật -->
              <div>
                <h3 class="text-lg font-semibold mb-4">
                  Điểm nổi bật của tour
                </h3>
                <ul class="space-y-2">
                  <li
                    v-for="(highlight, index) in tourDetails.highlights"
                    :key="index"
                    class="flex items-start"
                  >
                    <span class="text-blue-500 mr-2">✓</span>
                    {{ highlight }}
                  </li>
                </ul>
              </div>

              <!-- Dịch vụ bao gồm -->
              <div>
                <h3 class="text-lg font-semibold mb-4">Dịch vụ bao gồm</h3>
                <ul class="space-y-2">
                  <li
                    v-for="(item, index) in tourDetails.included"
                    :key="index"
                    class="flex items-start"
                  >
                    <span class="text-green-500 mr-2">✓</span>
                    {{ item }}
                  </li>
                </ul>
              </div>
            </div>

            <!-- Tab Đánh giá -->
            <div v-if="selectedTab === 'reviews'" class="space-y-6">
              <!-- Tổng quan đánh giá -->
              <div class="bg-gray-50 p-4 rounded-lg mb-6">
                <div class="flex items-center gap-4">
                  <div class="text-center">
                    <div class="text-3xl font-bold text-blue-600">
                      {{ averageRating }}
                    </div>
                    <div class="text-sm text-gray-500">trên 5</div>
                  </div>
                  <div class="flex-1">
                    <div class="flex items-center">
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
                    <div class="text-sm text-gray-500">
                      {{ reviews.length }} đánh giá
                    </div>
                  </div>
                </div>
              </div>

              <!-- Danh sách đánh giá -->
              <div class="space-y-6">
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
                    <div class="text-sm text-gray-500">{{ review.date }}</div>
                  </div>
                  <p class="text-gray-600 mb-4">{{ review.content }}</p>
                  <!-- Ảnh đính kèm -->
                  <div v-if="review.images.length" class="flex gap-2">
                    <img
                      v-for="(image, index) in review.images"
                      :key="index"
                      :src="image"
                      class="w-20 h-20 object-cover rounded-lg cursor-pointer hover:opacity-80"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>

      <!-- Cột phải - Booking form -->
      <div class="col-span-12 lg:col-span-4">
        <div class="sticky top-4">
          <div class="bg-white rounded-xl shadow-lg overflow-hidden">
            <!-- Header với giá -->
            <div class="p-4 bg-gradient-to-r from-blue-500/10 to-blue-500/5">
              <div class="flex items-baseline gap-2">
                <span class="text-2xl font-bold text-blue-600">{{
                  formatPrice(currentPrice)
                }}</span>
                <span
                  v-if="
                    bookingForm.selectedDate &&
                    bookingForm.availableDates.find(
                      (d) => d.date === bookingForm.selectedDate
                    )?.promoPrice
                  "
                  class="text-sm text-gray-400 line-through"
                >
                  {{
                    formatPrice(
                      bookingForm.availableDates.find(
                        (d) => d.date === bookingForm.selectedDate
                      )?.price
                    )
                  }}
                </span>
              </div>
              <p class="text-sm text-gray-600">*Giá/người lớn</p>
            </div>

            <!-- Form đặt tour -->
            <div class="p-4 space-y-4">
              <!-- Chọn ngày -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2"
                  >Chọn ngày khởi hành</label
                >
                <div class="grid grid-cols-2 gap-2">
                  <button
                    v-for="date in bookingForm.availableDates"
                    :key="date.date"
                    @click="selectDate(date.date)"
                    :class="[
                      'p-3 border rounded-lg text-left transition-colors',
                      bookingForm.selectedDate === date.date
                        ? 'border-blue-500 bg-blue-50 ring-1 ring-blue-500/20'
                        : 'hover:border-blue-200 hover:bg-gray-50',
                    ]"
                  >
                    <div class="flex items-center justify-between mb-1">
                      <span class="font-medium">{{ date.display }}</span>
                      <span
                        v-if="date.promoPrice"
                        class="text-xs font-medium text-red-500 bg-red-50 px-2 py-0.5 rounded-full"
                      >
                        Giảm giá
                      </span>
                    </div>
                    <div class="text-sm space-y-1">
                      <div class="text-gray-700">
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

              <!-- Số lượng khách -->
              <div class="space-y-2">
                <label class="block text-sm font-medium text-gray-700"
                  >Số lượng khách</label
                >
                <div
                  v-for="(type, key) in {
                    adults: { label: 'Người lớn', desc: '> 10 tuổi', min: 1 },
                    children: { label: 'Trẻ em', desc: '2 - 10 tuổi', min: 0 },
                    infants: { label: 'Em bé', desc: '< 2 tuổi', min: 0 },
                  }"
                  :key="key"
                  class="p-2.5 border rounded-lg hover:border-blue-200 transition-colors"
                >
                  <div class="flex justify-between items-center">
                    <div>
                      <p class="font-medium text-gray-800">{{ type.label }}</p>
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
                        class="w-7 h-7 rounded-lg border flex items-center justify-center transition-colors disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                      >
                        <i class="fas fa-minus text-xs"></i>
                      </button>
                      <span class="w-8 text-center font-medium">{{
                        bookingForm.travelers[key]
                      }}</span>
                      <button
                        @click="updateTravelers(key, 'increase')"
                        :disabled="!canAddTraveler"
                        class="w-7 h-7 rounded-lg border flex items-center justify-center transition-colors disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                      >
                        <i class="fas fa-plus text-xs"></i>
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Tổng tiền -->
              <div class="border-t pt-4 space-y-2">
                <div class="flex justify-between text-sm">
                  <span class="text-gray-600">Tạm tính</span>
                  <span class="font-medium">{{ formatPrice(subtotal) }}</span>
                </div>
                <div
                  v-if="bookingForm.travelers.adults === 1"
                  class="flex justify-between text-sm"
                >
                  <span class="text-gray-600">Phụ thu phòng đơn</span>
                  <span class="font-medium text-blue-600">
                    +{{ formatPrice(bookingForm.prices.singleRoomExtra) }}
                  </span>
                </div>
                <div
                  class="flex justify-between items-center text-lg font-bold pt-2"
                >
                  <span>Tổng cộng</span>
                  <span class="text-blue-600">{{ formatPrice(total) }}</span>
                </div>
              </div>

              <!-- Nút đặt tour -->
              <button
                @click="handleBooking"
                :disabled="!bookingForm.selectedDate"
                class="w-full bg-blue-600 text-white py-3 rounded-lg font-medium transition-colors hover:bg-blue-[#646ae7] disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
              >
                <i class="fas fa-shopping-cart"></i>
                {{
                  bookingForm.selectedDate
                    ? "Đặt tour ngay"
                    : "Vui lòng chọn ngày"
                }}
              </button>

              <!-- Thông tin bổ sung -->
              <div class="text-sm text-gray-500 space-y-2 pt-2">
                <div
                  v-for="(info, index) in [
                    { icon: 'clock', text: 'Xác nhận trong vòng 24h' },
                    { icon: 'shield-alt', text: 'Đảm bảo hoàn tiền' },
                    { icon: 'headset', text: 'Hỗ trợ 24/7' },
                  ]"
                  :key="index"
                  class="flex items-center gap-2"
                >
                  <i :class="['fas', 'fa-' + info.icon, 'text-blue-500']"></i>
                  <span>{{ info.text }}</span>
                </div>

                <div
                  v-if="totalTravelers >= bookingForm.maxTravelers"
                  class="flex items-center gap-2 text-red-500 bg-red-50 p-2 rounded-lg mt-2"
                >
                  <i class="fas fa-exclamation-circle"></i>
                  <span>Đã đạt số lượng khách tối đa</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Giữ nguyên styles cũ */
.sticky {
  position: sticky;
  top: 1rem;
}

/* Animation cho tabs */
.tab-enter-active,
.tab-leave-active {
  transition: opacity 0.3s ease;
}

.tab-enter-from,
.tab-leave-to {
  opacity: 0;
}

/* Custom scrollbar */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #555;
}

/* Animation cho timeline */
.group:hover .group-hover\:bg-blue-600 {
  transform: scale(1.1);
  transition: transform 0.2s ease;
}

/* Smooth hover effect cho các items */
.group {
  transition: all 0.3s ease;
}

.group:hover {
  transform: translateX(4px);
}

/* Custom styles cho timeline line */
.timeline-line {
  position: absolute;
  left: 2.45rem;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(
    to bottom,
    transparent,
    #e5e7eb 10%,
    #e5e7eb 90%,
    transparent
  );
}

/* Custom scrollbar for thumbnails */
.scrollbar-thin {
  scrollbar-width: thin;
}

.scrollbar-thin::-webkit-scrollbar {
  height: 4px;
}

.scrollbar-thin::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 4px;
}

.scrollbar-thin::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

.scrollbar-thin::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* Smooth transitions */
.transition-all {
  transition: all 0.3s ease-in-out;
}

/* Image hover effect */
.group:hover .group-hover\:scale-105 {
  transform: scale(1.05);
  transition: transform 0.3s ease-in-out;
}

/* Thumbnail hover effect */
.opacity-60 {
  opacity: 0.6;
  transition: opacity 0.3s ease-in-out;
}

.hover\:opacity-100:hover {
  opacity: 1;
}

/* Ring animation */
.ring-2 {
  transition: all 0.3s ease-in-out;
}

.ring-offset-2 {
  transition: all 0.3s ease-in-out;
}
</style>
