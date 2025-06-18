<template>
  <div class="w-full bg-slate-50">
    <SearchBar ref="searchBarRef" />

    <div class="font-sans py-12">
      <div class="max-w-[1200px] mx-auto px-4">
        <div class="flex flex-col items-center text-center mb-8">
          <h2
            class="text-3xl font-bold text-slate-800 mb-2 flex items-center gap-3"
          >
            <span>🌴</span>
            <span>Chơi cuối tuần gần nhà</span>
          </h2>
          <p class="text-slate-500 max-w-xl">
            Khám phá những điểm đến hấp dẫn và tận hưởng kỳ nghỉ cuối tuần tuyệt
            vời cùng gia đình và bạn bè.
          </p>
        </div>

        <nav class="flex justify-center flex-wrap gap-2 mb-8">
          <button
            v-for="city in citiesFilter"
            :key="city"
            @click="selectCity(city)"
            :class="
              activeCity === city
                ? 'bg-blue-600 text-white border-transparent shadow-md'
                : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-100'
            "
            class="border text-sm font-semibold rounded-full px-5 py-2 whitespace-nowrap transition-all duration-200"
            type="button"
          >
            {{ city }}
          </button>
        </nav>

        <div class="relative">
          <button
            @click="scrollLeft"
            aria-label="Cuộn sang trái"
            class="absolute top-1/2 left-0 -translate-x-1/2 -translate-y-1/2 z-10 bg-white rounded-full w-10 h-10 flex items-center justify-center shadow-md hover:bg-gray-100 transition"
          >
            <i class="fas fa-chevron-left text-gray-600"></i>
          </button>

          <div
            ref="scrollContainer"
            class="flex items-stretch gap-6 overflow-x-auto scroll-smooth snap-x snap-mandatory scrollbar-hide py-2"
          >
            <div
              v-for="hotel in filteredHotels"
              :key="hotel.id"
              @click="goToHotelDetail(hotel)"
              class="snap-start shrink-0 w-[270px] cursor-pointer"
            >
              <div
                class="group bg-white rounded-xl shadow-md overflow-hidden flex flex-col hover:shadow-xl transition-shadow duration-300 h-full"
              >
                <div class="relative">
                  <div
                    class="absolute top-0 left-0 bg-black bg-opacity-70 text-white text-xs font-semibold px-3 py-1 flex items-center gap-1.5 rounded-br-xl z-10"
                  >
                    <i class="fas fa-map-marker-alt"></i>
                    <span>{{ hotel.location }}</span>
                  </div>
                  <img
                    :alt="hotel.alt"
                    class="w-full h-48 object-cover group-hover:scale-105 transition-transform duration-300"
                    :src="hotel.image"
                  />
                </div>
                <div class="p-4 flex flex-col flex-grow">
                  <span
                    class="bg-blue-100 text-blue-800 text-xs font-semibold mb-2 px-2.5 py-0.5 rounded-full self-start"
                  >
                    {{ hotel.amenities }}
                  </span>
                  <h3
                    class="font-bold text-gray-800 text-base leading-tight mb-2 flex-grow group-hover:text-blue-600 transition-colors"
                  >
                    {{ hotel.title }}
                  </h3>
                  <div class="flex justify-between items-center mb-3">
                    <div class="flex items-center gap-0.5 text-yellow-400">
                      <i
                        v-for="n in Math.floor(hotel.rating)"
                        :key="n"
                        class="fas fa-star"
                      ></i>
                      <i
                        v-if="hotel.rating % 1 !== 0"
                        class="fas fa-star-half-alt"
                      ></i>
                    </div>
                    <div class="text-blue-600 font-semibold text-sm">
                      {{ hotel.rating }}
                      <span class="text-gray-500 font-normal text-xs"
                        >({{ hotel.reviews }})</span
                      >
                    </div>
                  </div>
                  <div class="mt-auto pt-3 border-t border-gray-100 text-right">
                    <p class="text-gray-400 line-through text-xs">
                      {{
                        Number(hotel.originalPrice).toLocaleString("vi-VN")
                      }}
                      VND
                    </p>
                    <p class="text-orange-600 font-bold text-lg">
                      {{ Number(hotel.price).toLocaleString("vi-VN") }}
                      <span class="text-sm font-normal">VND</span>
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <button
            @click="scrollRight"
            aria-label="Cuộn sang phải"
            class="absolute top-1/2 right-0 translate-x-1/2 -translate-y-1/2 z-10 bg-white rounded-full w-10 h-10 flex items-center justify-center shadow-md hover:bg-gray-100 transition"
          >
            <i class="fas fa-chevron-right text-gray-600"></i>
          </button>
        </div>
      </div>

      <div class="bg-white text-black font-sans mt-12">
        <div class="max-w-7xl mx-auto px-4 py-8">
          <hr class="border-t border-gray-300 mb-8" />
          <h2 class="text-center font-semibold text-lg mb-8">
            Tại sao nên đặt chỗ của chúng tôi?
          </h2>
          <div
            class="flex flex-wrap justify-center gap-x-20 gap-y-8 text-center max-w-5xl mx-auto"
          >
            <div class="max-w-[180px]">
              <img
                alt="Blue price tag with a yellow heart"
                class="mx-auto mb-4"
                height="100"
                src="https://ik.imagekit.io/tvlk/image/imageResource/2017/05/10/1494407528373-a0e2c450b5cfac244d687d6fa8f5dd98.png?tr=dpr-2,h-150,q-75,w-150"
                width="100"
              />
              <h3 class="font-semibold text-base mb-2">
                Giá rẻ mỗi ngày với ưu đãi đặc biệt dành riêng cho ứng dụng
              </h3>
              <p class="text-sm font-normal leading-relaxed">
                Đặt phòng qua ứng dụng để nhận giá tốt nhất với các khuyến mãi
                tuyệt vời!
              </p>
            </div>
            <div class="max-w-[180px]">
              <img
                alt="Stack of credit and ATM cards"
                class="mx-auto mb-4"
                height="100"
                src="https://ik.imagekit.io/tvlk/image/imageResource/2017/05/10/1494407536280-ddcb70cab4907fa78468540ba722d25b.png?tr=dpr-2,h-150,q-75,w-150"
                width="100"
              />
              <h3 class="font-semibold text-base mb-2">
                Phương thức thanh toán an toàn và linh hoạt
              </h3>
              <p class="text-sm font-normal leading-relaxed">
                Giao dịch trực tuyến an toàn với nhiều lựa chọn như thanh toán
                tại cửa hàng tiện lợi, chuyển khoản ngân hàng, thẻ tín dụng đến
                Internet Banking. Không tính phí giao dịch.
              </p>
            </div>
            <div class="max-w-[180px]">
              <img
                alt="24/7 support icons"
                class="mx-auto mb-4"
                height="100"
                src="https://ik.imagekit.io/tvlk/image/imageResource/2017/05/10/1494407541562-61b4438f5439c253d872e70dd7633791.png?tr=dpr-2,h-150,q-75,w-150"
                width="100"
              />
              <h3 class="font-semibold text-base mb-2">
                Hỗ trợ khách hàng 24/7
              </h3>
              <p class="text-xs font-normal leading-relaxed">
                Đội ngũ nhân viên hỗ trợ khách hàng luôn sẵn sàng giúp đỡ bạn
                trong từng bước của quá trình đặt vé
              </p>
            </div>
            <div class="max-w-[180px]">
              <img
                alt="Verified guest reviews icon"
                class="mx-auto mb-4"
                height="100"
                src="https://ik.imagekit.io/tvlk/image/imageResource/2017/05/10/1494407562736-ea624be44aec195feffac615d37ab492.png?tr=dpr-2,h-150,q-75,w-150"
                width="100"
              />
              <h3 class="font-semibold text-base mb-2">
                Khách thực, đánh giá thực
              </h3>
              <p class="text-xs font-normal leading-relaxed">
                Hơn 10.000.000 đánh giá, bình chọn đã được xác thực từ du khách
                sẽ giúp bạn đưa ra lựa chọn đúng đắn.
              </p>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white">
        <div class="max-w-6xl mx-auto px-4 py-8">
          <h2 class="text-center font-semibold text-lg mb-6">
            Điểm đến hot nhất được đề xuất
          </h2>
          <div
            class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4"
          >
            <div
              v-for="city in hotDestinations"
              :key="city.name"
              class="relative rounded-md overflow-hidden shadow-md group cursor-pointer"
              @click="searchForHotDestination(city.name)"
            >
              <img
                :alt="city.alt"
                class="w-full h-48 object-cover transition-transform duration-300 group-hover:scale-105"
                height="250"
                :src="city.src"
                width="400"
              />
              <div
                class="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent"
              ></div>

              <div
                class="absolute top-3 left-3 text-white font-semibold text-base leading-tight drop-shadow-md z-10"
              >
                {{ city.name }}
                <br />
                <span class="font-normal text-sm">
                  Có {{ city.hotels }} khách sạn
                </span>
              </div>

              <div
                class="absolute inset-0 bg-black/70 opacity-0 group-hover:opacity-100 transition-opacity duration-300 z-20 flex items-center justify-center p-4"
              >
                <button
                  class="bg-orange-500 hover:bg-orange-600 text-white font-semibold py-2 px-4 rounded-md shadow-md transition-colors duration-200 text-center pointer-events-none"
                >
                  Xem khách sạn trống phòng
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="max-w-4xl mx-auto px-4 py-10">
        <h2 class="text-center font-semibold text-lg text-slate-900 mb-4">
          Đặt phòng khách sạn tại Việt Nam trên trang của chúng tôi
        </h2>
        <p
          class="text-center text-slate-700 text-base leading-relaxed max-w-3xl mx-auto"
        >
          Trang web của chúng tôi là một trong những nền tảng du lịch trực tuyến
          hàng đầu Đông Nam Á, cung cấp đầy đủ tiện ích cho một chuyến đi trọn
          vẹn với các sản phẩm chính gồm:
          <a href="#" class="text-blue-600 underline">Vé máy bay</a>,
          <a href="#" class="text-blue-600 underline">Khách sạn</a>,
          <a href="#" class="text-blue-600 underline">Vé xe khách</a> cùng
          <a href="#" class="text-blue-600 underline">Hoạt động du lịch</a> như
          vé vui chơi, tour. Với mạng lưới hơn 1 triệu khách sạn toàn cầu,
          Traveloka mang đến vô vàn lựa chọn phù hợp mọi nhu cầu và sở thích. Từ
          khách sạn giá rẻ gần bạn đến những nơi lưu trú sang trọng với view
          đẳng cấp, tất cả đều dễ dàng tìm kiếm nhờ bộ lọc thông minh. Đặc biệt,
          việc đặt phòng trực tuyến trên Traveloka còn đi kèm các tiện ích như
          thanh toán linh hoạt, hỗ trợ 24/7, giúp bạn an tâm tận hưởng chuyến
          đi.
        </p>
        <p
          class="text-center text-gray-500 font-semibold mt-6 cursor-pointer select-none"
        >
          Xem thêm
        </p>
        <hr class="mt-6 border-t border-gray-300" />
      </div>
    </div>
  </div>
</template>

<script setup>
import SearchBar from "@/components/Hotel/SearchBar.vue";
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { hotels } from "@/data/hotelData.js";

const router = useRouter();
const searchBarRef = ref(null);

const citiesFilter = [
  "Tất cả",
  "Vũng Tàu",
  "Cần Thơ",
  "Hội An",
  "Thành Phố Hồ Chí Minh",
  "Bình Định",
];
const activeCity = ref("Tất cả");

function selectCity(city) {
  activeCity.value = city;
}

const filteredHotels = computed(() => {
  if (activeCity.value === "Tất cả") {
    return hotels;
  }
  return hotels.filter((hotel) => hotel.location === activeCity.value);
});

const scrollContainer = ref(null);

const scrollLeft = () => {
  if (scrollContainer.value) {
    scrollContainer.value.scrollBy({ left: -300, behavior: "smooth" });
  }
};

const scrollRight = () => {
  if (scrollContainer.value) {
    scrollContainer.value.scrollBy({ left: 300, behavior: "smooth" });
  }
};

function goToHotelDetail(hotel) {
  if (!searchBarRef.value) {
    console.error("Lỗi: Không thể truy cập SearchBar.");
    return;
  }

  const searchData = searchBarRef.value.getSearchData();

  if (!searchData || !searchData.checkin || !searchData.checkout) {
    console.error("Lỗi: Dữ liệu từ SearchBar không hợp lệ.");
    return;
  }

  const checkoutString = searchData.checkout.toISOString().split("T")[0];

  router.push({
    name: "HotelDetail",
    params: { id: hotel.id },
    query: {
      location: hotel.title,

      checkin: searchData.checkin,
      checkout: checkoutString,
      adults: searchData.adults,
      children: searchData.children,
      rooms: searchData.rooms,
    },
  });
}

const searchForHotDestination = (destination) => {
  if (!searchBarRef.value) {
    console.error("Lỗi: Không thể truy cập SearchBar.");
    return;
  }

  const searchData = searchBarRef.value.getSearchData();

  if (!searchData || !searchData.checkout) {
    console.error("Lỗi: Dữ liệu từ SearchBar không hợp lệ.");
    return;
  }

  const checkoutString = searchData.checkout.toISOString().split("T")[0];

  router.push({
    name: "HotelListing",
    query: {
      location: destination,
      checkin: searchData.checkin,
      checkout: checkoutString,
      adults: searchData.adults,
      children: searchData.children,
      rooms: searchData.rooms,
    },
  });
};

const hotDestinations = ref([
  {
    name: "Đà Nẵng",
    hotels: 763,
    src: "https://storage.googleapis.com/a1aa/image/ef6b3cfd-021b-4f43-0680-8ab3f1decaf6.jpg",
    alt: "Da Nang Bridge with dragon sculpture lit up at night over river",
  },
  {
    name: "Nha Trang",
    hotels: 569,
    src: "https://storage.googleapis.com/a1aa/image/9293e117-a19c-42e1-9b85-6b2c3750869a.jpg",
    alt: "Nha Trang pier extending into sea at sunset with colorful sky",
  },
  {
    name: "Phú Quốc",
    hotels: 381,
    src: "https://storage.googleapis.com/a1aa/image/370aa369-81fa-4d50-fcfa-8faeb8af7806.jpg",
    alt: "Phu Quoc beach with rocks and clear blue water",
  },
  {
    name: "Vũng Tàu",
    hotels: 339,
    src: "https://storage.googleapis.com/a1aa/image/3086ff8d-e363-4df9-c9a4-ae055f059b66.jpg",
    alt: "Vung Tau city beach with buildings and ocean",
  },
  {
    name: "Hà Nội",
    hotels: 1049,
    src: "https://storage.googleapis.com/a1aa/image/e6c318d0-0790-4f18-94e2-cb5bfa449de7.jpg",
    alt: "Ha Noi red bridge over pond in park with trees",
  },
  {
    name: "Đà Lạt",
    hotels: 591,
    src: "https://storage.googleapis.com/a1aa/image/d8d4b298-03d9-4370-68c7-768a213e3929.jpg",
    alt: "Da Lat building with tower and trees under cloudy sky",
  },
  {
    name: "Hội An",
    hotels: 553,
    src: "https://storage.googleapis.com/a1aa/image/dd748cfa-4dde-4dcd-a298-1ff2d30a7fee.jpg",
    alt: "Hoi An yellow buildings along river with boats",
  },
  {
    name: "Phan Thiết",
    hotels: 243,
    src: "https://storage.googleapis.com/a1aa/image/51568591-43e6-4bda-d43c-6c9cd73001d5.jpg",
    alt: "Phan Thiet sand dunes with people walking on top",
  },
  {
    name: "Quy Nhơn",
    hotels: 80,
    src: "https://storage.googleapis.com/a1aa/image/5dfc9eec-1804-4b17-70d6-6f9371204b28.jpg",
    alt: "Quy Nhon rocky beach at sunset with orange sky",
  },
  {
    name: "Huế",
    hotels: 243,
    src: "https://storage.googleapis.com/a1aa/image/fa56211f-2933-4546-4e13-b981c950182d.jpg",
    alt: "Hue traditional boat with people on river",
  },
  {
    name: "Hồ Chí Minh",
    hotels: 1527,
    src: "https://storage.googleapis.com/a1aa/image/283c1baa-234f-491b-6fca-9166bca9e35a.jpg",
    alt: "Ho Chi Minh cathedral with statue and trees",
  },
  {
    name: "Hạ Long",
    hotels: 230,
    src: "https://storage.googleapis.com/a1aa/image/6759c201-298b-4881-f22d-daa7932f194f.jpg",
    alt: "Ha Long Bay limestone islands with water and sky",
  },
]);
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}

.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
