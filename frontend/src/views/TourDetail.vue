<script setup>
import { ref, computed } from "vue";

import { useRoute } from "vue-router";

const route = useRoute();
const id = Number(route.params.id);
const tour = computed(() => {
  return tours.value.find((t) => t.id === id) || {};
});
const images = [
  "https://images.unsplash.com/photo-1587502537684-6fa7fa8f451f?auto=format&fit=crop&w=800&q=80",
  "https://images.unsplash.com/photo-1595433562696-a2641c44aa67?auto=format&fit=crop&w=800&q=80",
  "https://images.unsplash.com/photo-1602524818307-f38c62aef1d4?auto=format&fit=crop&w=800&q=80",
  "https://images.unsplash.com/photo-1621605812260-d2fbd9cb26a2?auto=format&fit=crop&w=800&q=80",
];

const currentIndex = ref(0);

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
</script>

<template>
  <div class="grid grid-cols-12 gap-4 container">
    <div class="col-span-10">
      <h1 class="text-2xl font-semibold mt-2">{{ tour.title }}</h1>
    </div>

    <div class="grid grid-cols-12 col-span-12 gap-4">
      <div class="col-span-7">
        <section class="slider">
          <!-- Ảnh chính -->
          <div class="relative">
            <img
              :src="images[currentIndex]"
              alt="Ảnh chính"
              class="w-full h-120 rounded-xl shadow-lg"
            />

            <button
              @click="prevImage"
              type="button"
              class="absolute left-10 top-1/2 -translate-y-1/2 carousel-prev start-5 max-sm:start-3 carousel-disabled:opacity-50 size-9.5 bg-base-100 flex items-center justify-center rounded-full shadow-base-300/20 shadow-sm"
            >
              <span
                class="icon-[tabler--chevron-left] size-5 cursor-pointer"
              ></span>
              <span class="sr-only">Previous</span>
            </button>

            <button
              @click="nextImage"
              type="button"
              class="absolute right-10 top-1/2 -translate-y-1/2 carousel-next end-5 max-sm:end-3 carousel-disabled:opacity-50 size-9.5 bg-base-100 flex items-center justify-center rounded-full shadow-base-300/20 shadow-sm"
            >
              <span class="icon-[tabler--chevron-right] size-5"></span>
              <span class="sr-only">Next</span>
            </button>
          </div>

          <!-- Ảnh phụ -->
          <div class="flex space-x-3 overflow-x-auto p-4">
            <img
              v-for="(img, index) in images"
              :key="index"
              :src="img"
              @click="selectImage(index)"
              :class="[
                'w-24 h-16 object-cover cursor-pointer rounded-md border-2',
                currentIndex === index
                  ? 'border-blue-500'
                  : 'border-transparent hover:border-blue-500',
              ]"
            />
          </div>
        </section>
        <section></section>
      </div>

      <!-- Sidebar -->
      <div class="col-span-5">
        <div class="max-w-sm p-4 bg-white-100 rounded-sm shadow-sm">
          <h5 class="text-blue-800 text-lg font-bold mb-2">
            Lịch Trình và Giá Tour
          </h5>
          <p class="mb-3 text-sm text-gray-700">Chọn Lịch Trình và Xem Giá:</p>

          <!-- Ngày chọn -->
          <div class="grid grid-cols-5 gap-2 mb-4">
            <button
              class="border rounded-lg py-2 text-center hover:bg-blue-100"
            >
              17/07
            </button>
            <button
              class="border rounded-lg py-2 text-center hover:bg-blue-100"
            >
              31/07
            </button>
            <button
              class="border rounded-lg py-2 text-center hover:bg-blue-100"
            >
              07/08
            </button>
            <button
              class="border rounded-lg py-2 text-center hover:bg-blue-100"
            >
              <i class="fas fa-calendar-alt"></i>
            </button>
          </div>

          <!-- Người lớn -->
          <div
            class="flex justify-between items-center mb-3 border rounded-lg px-3 py-2"
          >
            <div>
              <p class="text-sm font-medium">Người lớn</p>
              <p class="text-xs text-gray-500">&gt; 10 tuổi</p>
            </div>
            <div class="text-right text-orange-500 font-bold">x 24.990.000</div>
            <div class="flex items-center space-x-2">
              <button class="px-2 text-lg font-bold">−</button>
              <span>1</span>
              <button class="px-2 text-lg font-bold">+</button>
            </div>
          </div>

          <!-- Trẻ em -->
          <div
            class="flex justify-between items-center mb-3 border rounded-lg px-3 py-2"
          >
            <div>
              <p class="text-sm font-medium">Trẻ em</p>
              <p class="text-xs text-gray-500">2 - 10 tuổi</p>
            </div>
            <div class="flex items-center space-x-2">
              <button class="px-2 text-lg font-bold">−</button>
              <span>0</span>
              <button class="px-2 text-lg font-bold">+</button>
            </div>
          </div>

          <!-- Trẻ nhỏ -->
          <div
            class="flex justify-between items-center mb-3 border rounded-lg px-3 py-2"
          >
            <div>
              <p class="text-sm font-medium">Trẻ nhỏ</p>
              <p class="text-xs text-gray-500">&lt; 2 tuổi</p>
            </div>
            <div class="flex items-center space-x-2">
              <button class="px-2 text-lg font-bold">−</button>
              <span>0</span>
              <button class="px-2 text-lg font-bold">+</button>
            </div>
          </div>

          <!-- Ghi chú -->
          <div class="text-xs text-cyan-600 mb-2">
            <i class="fas fa-info-circle mr-1"></i> Liên hệ để xác nhận chỗ
          </div>

          <!-- Phụ thu -->
          <div class="flex justify-between text-sm mb-1">
            <span>Phụ Thu Phòng Đơn</span>
            <span class="text-orange-500 font-medium">+ 6.000.000 đ</span>
          </div>

          <!-- Tổng giá -->
          <div
            class="flex justify-between items-center text-lg font-semibold text-orange-600 mb-4"
          >
            <span>Tổng Giá Tour</span>
            <span>30.990.000 đ</span>
          </div>

          <!-- Nút đặt -->
          <button
            class="bg-[#646ae7] text-white text-center py-2 w-full rounded-md font-semibold hover:bg-orange-600"
          >
            Yêu cầu đặt
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
