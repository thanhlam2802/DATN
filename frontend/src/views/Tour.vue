<script setup>
import SearchBar from "../components/SearchBar.vue";
import SideBar from "../components/Sidebar.vue";
import TourGrid from "../components/TourGrid.vue";
import { ref } from "vue";

const isOpen = ref(false);
const getLocation = () => {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        alert(`Vị trí của bạn: 
Latitude: ${position.coords.latitude}
Longitude: ${position.coords.longitude}`);
      },
      (error) => {
        alert(
          "Không thể lấy vị trí của bạn. Vui lòng cho phép chia sẻ vị trí."
        );
      }
    );
  } else {
    alert("Trình duyệt của bạn không hỗ trợ định vị.");
  }
};
const destinations = [
  { name: "Tỉnh Lâm Đồng", image: "https://picsum.photos/id/1018/400/200" },
  { name: "Khánh Hòa", image: "https://picsum.photos/id/1025/400/200" },
  {
    name: "Tỉnh Thừa Thiên Huế",
    image: "https://picsum.photos/id/1011/400/200",
  },
  { name: "Bà Rịa - Vũng Tàu", image: "https://picsum.photos/id/1040/400/200" },
  { name: "Tỉnh Kiên Giang", image: "https://picsum.photos/id/1052/400/200" },
];

const southeastAsia = [
  { name: "Indonesia", image: "https://picsum.photos/id/1062/400/200" },
  { name: "Thái Lan", image: "https://picsum.photos/id/1065/400/200" },
  { name: "Malaysia", image: "https://picsum.photos/id/1066/400/200" },
  { name: "Singapore", image: "https://picsum.photos/id/1067/400/200" },
  { name: "Philippines", image: "https://picsum.photos/id/1068/400/200" },
];
</script>

<template>
  <div class="container mt-4">
    <section
      class="relative w-full h-48 sm:h-36 md:h-44 lg:h-52 xl:h-80 overflow-hidden"
    >
      <img
        src="https://ik.imagekit.io/tvlk/xpe-asset/AyJ40ZAo1DOyPyKLZ9c3RGQHTP2oT4ZXW+QmPVVkFQiXFSv42UaHGzSmaSzQ8DO5QIbWPZuF+VkYVRk6gh-Vg4ECbfuQRQ4pHjWJ5Rmbtkk=/5255025890550/Phong-Nha-Ke-Bang-National-Park-Tour-from-Hue-158e7222-35b7-4296-95bb-690972765d35.jpeg?_src=imagekit&tr=dpr-2,c-at_max,h-400,q-100,w-1280"
        alt=""
        class="absolute rounded-br-4xl inset-0 w-full h-full object-cover brightness-75"
      />

      <div
        class="relative z-10 grid grid-cols-12 gap-4 h-full items-center px-6"
      >
        <div class="col-span-12 md:col-span-6 flex flex-col gap-2">
          <h5 class="text-white text-3xl font-semibold">Du lịch</h5>
          <div>
            <button
              @click="isOpen = true"
              class="inline-flex hover:cursor-pointer items-center gap-2 bg-white bg-opacity-90 hover:bg-opacity-100 text-gray-900 text-sm font-medium rounded-full py-2 px-4 shadow-sm focus:outline-none w-fit"
            >
              <i class="fas fa-map-marker-alt text-blue-500"></i>
              <span>Chọn một điểm đến</span>
              <svg
                class="w-4 h-4 text-gray-600"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                viewBox="0 0 24 24"
              >
                <path
                  d="M19 9l-7 7-7-7"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
            </button>

            <transition name="slide-up" class="">
              <div
                v-if="isOpen"
                class="fixed inset-0 z-50 bg-black-500 bg-opacity-5 backdrop-blur-sm flex items-end md:items-center justify-center"
                @click.self="isOpen = false"
              >
                <div
                  class="relative bg-gray-100 rounded-t-2xl md:rounded-2xl shadow-lg w-full max-w-3xl max-h-[90vh] overflow-y-auto animate-slide-up"
                >
                  <div
                    class="grid grid-cols-2 gap-4 bg-white p-4 mb-2 items-center justify-center"
                  >
                    <div class="col-span-1 mt-4">
                      <p class="text-xs">Khám phá những điều xung quanh bạn:</p>
                      <h2
                        class="text-lg mb-4 text-blue-400 cursor-pointer"
                        @click="getLocation"
                      >
                        Thành phố Quy Nhơn, Vietnam
                      </h2>
                    </div>

                    <SearchBar
                      class="col-span-1"
                      :placeholder="'Tìm thành phố hoặc khu vưc'"
                    />
                  </div>

                  <div
                    class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4 p-4 bg-white"
                  >
                    <div
                      v-for="item in destinations"
                      :key="item.name"
                      class="rounded-lg overflow-hidden shadow cursor-pointer hover:scale-105 transition"
                    >
                      <img
                        :src="item.image"
                        alt=""
                        class="w-full h-32 object-cover"
                      />
                      <div class="p-2 text-center font-medium text-gray-800">
                        {{ item.name }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </transition>
          </div>
        </div>

        <SearchBar
          class="col-span-12 md:col-span-3"
          :placeholder="'Tìm kiếm địa điểm du lịch'"
        />
      </div>
    </section>

    <!-- Section giới thiệu tour -->
    <section class="pt-5">
      <div class="grid grid-cols-12 gap-4 items-center">
        <div class="col-span-1 flex">
          <img
            alt="Tour"
            draggable="false"
            src="https://ik.imagekit.io/tvlk/image/imageResource/2024/05/14/1715657640546-0e1140f44cf4a39fe69f33f236c6a564.png?tr=q-75"
            class="w-16 h-16 object-contain"
          />
        </div>
        <div class="col-span-11">
          <h2 class="font-extrabold text-3xl text-black leading-tight mb-1">
            Tour
          </h2>
          <p class="text-gray-600 text-base text-xl">
            Khám phá những địa điểm mới và đặc biệt bằng cách tham gia các
            chuyến tham quan trong ngày với hướng dẫn viên giàu kinh nghiệm.
          </p>
        </div>
      </div>
    </section>

    <!-- Section danh sách tour -->
    <section
      class="max-w-7xl mx-auto px-4 py-6 mt-10 flex flex-col md:flex-row gap-6"
    >
      <SideBar />
      <main class="flex-1">
        <div class="flex justify-between items-center mb-6">
          <h2 class="text-xl text-gray-600">Về 44 kết quả</h2>
          <div class="flex items-center gap-2">
            <span class="text-gray-600">Xếp theo:</span>
            <div class="relative">
              <button
                class="flex items-center gap-2 px-4 py-2 border rounded-lg"
              >
                <span>Phổ biến nhất</span>
                <svg
                  class="w-4 h-4 text-gray-500"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  viewBox="0 0 24 24"
                >
                  <path
                    d="M19 9l-7 7-7-7"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                </svg>
              </button>
            </div>
          </div>
        </div>
        <TourGrid />
      </main>
    </section>
  </div>
</template>

<style scoped>
.slide-up-enter-active {
  transition: all 0.3s ease;
}
.slide-up-leave-active {
  transition: all 0.2s ease;
}
.slide-up-enter-from {
  transform: translateY(100%);
  opacity: 0;
}
.slide-up-enter-to {
  transform: translateY(0);
  opacity: 1;
}
.slide-up-leave-from {
  transform: translateY(0);
  opacity: 1;
}
.slide-up-leave-to {
  transform: translateY(100%);
  opacity: 0;
}

.animate-slide-up {
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
