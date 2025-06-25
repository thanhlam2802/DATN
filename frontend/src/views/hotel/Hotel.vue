<template>
  <div class="w-full">
    <SearchBar ref="searchBarRef" />

    <div class="font-sans py-12">
      <div class="max-w-[1200px] mx-auto px-4">
        <section>
          <div class="flex flex-col items-center text-center mb-8">
            <h2 class="text-3xl font-bold text-slate-800 mb-2 flex items-center gap-3">
              <span>🌴</span>
              <span>Chơi cuối tuần gần nhà</span>
            </h2>
            <p class="text-slate-500 max-w-xl">
              Khám phá những điểm đến hấp dẫn và tận hưởng kỳ nghỉ cuối tuần tuyệt vời cùng gia đình và bạn bè.
            </p>
          </div>

          <div class="mb-8">
            <div ref="provinceScrollContainer"
              class="flex items-center gap-2 overflow-x-auto scroll-smooth scrollbar-hide">
              <button v-for="province in provinces" :key="province.id" @click="selectProvince(province.id)"
                :class="activeProvinceId === province.id ? 'bg-blue-600 text-white border-transparent shadow-md' : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-100'"
                class="border text-sm font-semibold rounded-full px-5 py-2 whitespace-nowrap transition-all duration-200"
                type="button">
                {{ province.name }}
              </button>
            </div>
          </div>

          <div v-if="loading.hotels" class="text-center p-10">
            <p>Đang tải khách sạn...</p>
          </div>
          <div v-else-if="error.hotels" class="text-center p-10 bg-red-50 text-red-600 rounded-lg">
            <p>{{ error.hotels }}</p>
          </div>
          <div v-else-if="hotels.length === 0" class="text-center p-10">
            <p>Không có khách sạn nào trong khu vực này.</p>
          </div>
          <div v-else class="relative">
            <div ref="scrollContainer"
              class="flex items-stretch gap-6 overflow-x-auto scroll-smooth snap-x snap-mandatory scrollbar-hide py-2">
              <HotelCard v-for="hotel in hotels" :key="hotel.id" :hotel="hotel" class="snap-start shrink-0" />
            </div>
          </div>
        </section>

        <div class="bg-white text-black font-sans mt-12">
          <div class="max-w-7xl mx-auto px-4 py-8">
            <hr class="border-t border-gray-300 mb-8" />
            <h2 class="text-center font-semibold text-lg mb-8">
              Tại sao nên đặt chỗ của chúng tôi?
            </h2>
            <div class="flex flex-wrap justify-center gap-x-20 gap-y-8 text-center max-w-5xl mx-auto">
              <div class="max-w-[180px]"><img alt="Blue price tag" class="mx-auto mb-4" height="100"
                  src="https://ik.imagekit.io/tvlk/image/imageResource/2017/05/10/1494407528373-a0e2c450b5cfac244d687d6fa8f5dd98.png?tr=dpr-2,h-150,q-75,w-150"
                  width="100" />
                <h3 class="font-semibold text-base mb-2">Giá rẻ mỗi ngày với ưu đãi đặc biệt</h3>
                <p class="text-sm font-normal leading-relaxed">Đặt phòng qua ứng dụng để nhận giá tốt nhất!</p>
              </div>
              <div class="max-w-[180px]"><img alt="Stack of cards" class="mx-auto mb-4" height="100"
                  src="https://ik.imagekit.io/tvlk/image/imageResource/2017/05/10/1494407536280-ddcb70cab4907fa78468540ba722d25b.png?tr=dpr-2,h-150,q-75,w-150"
                  width="100" />
                <h3 class="font-semibold text-base mb-2">Phương thức thanh toán an toàn</h3>
                <p class="text-sm font-normal leading-relaxed">Giao dịch trực tuyến an toàn với nhiều lựa chọn linh
                  hoạt.</p>
              </div>
              <div class="max-w-[180px]"><img alt="24/7 support" class="mx-auto mb-4" height="100"
                  src="https://ik.imagekit.io/tvlk/image/imageResource/2017/05/10/1494407541562-61b4438f5439c253d872e70dd7633791.png?tr=dpr-2,h-150,q-75,w-150"
                  width="100" />
                <h3 class="font-semibold text-base mb-2">Hỗ trợ khách hàng 24/7</h3>
                <p class="text-xs font-normal leading-relaxed">Đội ngũ nhân viên luôn sẵn sàng giúp đỡ bạn.</p>
              </div>
              <div class="max-w-[180px]"><img alt="Verified reviews" class="mx-auto mb-4" height="100"
                  src="https://ik.imagekit.io/tvlk/image/imageResource/2017/05/10/1494407562736-ea624be44aec195feffac615d37ab492.png?tr=dpr-2,h-150,q-75,w-150"
                  width="100" />
                <h3 class="font-semibold text-base mb-2">Khách thực, đánh giá thực</h3>
                <p class="text-xs font-normal leading-relaxed">Hơn 10.000.000 đánh giá đã được xác thực từ du khách.</p>
              </div>
            </div>
          </div>
        </div>
        <hr class="border-t border-gray-300 mb-8" />
        <div class="bg-white">
          <div class="max-w-6xl mx-auto px-4 py-8">
            <h2 class="text-center font-semibold text-lg mb-6">
              Điểm đến hot nhất được đề xuất
            </h2>
            <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
              <div v-for="city in hotDestinations" :key="city.id"
                class="relative rounded-md overflow-hidden shadow-md group cursor-pointer" @click="searchForHotDestination(city.name)">
                <img :alt="city.name"
                  class="w-full h-48 object-cover transition-transform duration-300 group-hover:scale-105" height="250"
                  :src="city.imageUrl || 'https://placehold.co/400x250'" width="400" />
                <div
                  class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent transition-all duration-300 group-hover:bg-black/50">
                </div>
                <div
                  class="absolute bottom-4 left-4 text-white font-semibold text-base leading-tight drop-shadow-md z-10">
                  {{ city.name }}<br />
                  <span class="font-normal text-sm">Có {{ city.hotelCount }} khách sạn</span>
                </div>
                <div
                  class="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300 z-20">
                  <div class="bg-white text-blue-600 font-semibold px-4 py-2 rounded-md shadow-lg text-sm">Xem khách sạn
                    trống phòng</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="bg-white text-black font-sans mt-12">
        <div class="max-w-7xl mx-auto px-4 py-8">
          <hr class="border-t border-gray-300 mb-8" />
          <h2 class="text-center font-bold text-2xl text-slate-800 mb-4">
            Đặt phòng khách sạn tại Việt Nam trên trang của chúng tôi
          </h2>
          <div
            class="relative text-slate-600 text-base leading-relaxed max-w-3xl mx-auto transition-all duration-500 ease-in-out overflow-hidden"
            :class="isDescriptionExpanded ? 'max-h-[1000px]' : 'max-h-[120px]'">
            <p class="mb-4 text-justify">
              Trang web của chúng tôi tự hào là một trong những nền tảng du lịch trực tuyến hàng đầu Đông Nam Á, mang
              đến
              một giải pháp toàn diện cho mọi nhu cầu du lịch của bạn. Với chúng tôi, việc lên kế hoạch cho một chuyến
              đi
              trọn vẹn trở nên dễ dàng hơn bao giờ hết, từ việc đặt <a href="#"
                class="text-blue-600 font-semibold hover:underline">Vé máy bay</a>, <a href="#"
                class="text-blue-600 font-semibold hover:underline">Khách sạn</a>, <a href="#"
                class="text-blue-600 font-semibold hover:underline">Vé xe khách</a>, cho đến các <a href="#"
                class="text-blue-600 font-semibold hover:underline">Hoạt động du lịch</a> hấp dẫn như vé vui chơi, tour
              tham quan và nhiều hơn nữa.
            </p>
            <p class="mb-4 text-justify">
              Sở hữu mạng lưới hơn 1 triệu khách sạn trên toàn cầu, chúng tôi mang đến vô vàn lựa chọn lưu trú phù hợp
              với
              mọi nhu cầu và ngân sách. Dù bạn đang tìm kiếm một khách sạn giá rẻ gần trung tâm, một resort sang trọng
              với
              tầm nhìn hướng biển, hay một căn hộ dịch vụ tiện nghi cho chuyến công tác dài ngày, bộ lọc thông minh của
              chúng tôi sẽ giúp bạn tìm thấy lựa chọn hoàn hảo chỉ trong vài cú nhấp chuột.
            </p>
            <p class="text-justify">
              Chúng tôi hiểu rằng sự tin cậy và tiện lợi là yếu tố quan trọng nhất. Vì vậy, chúng tôi cam kết mang đến
              trải nghiệm đặt phòng trực tuyến an toàn với các phương thức thanh toán linh hoạt, được bảo mật tuyệt đối.
              Đội ngũ hỗ trợ khách hàng chuyên nghiệp của chúng tôi luôn sẵn sàng phục vụ 24/7, giải đáp mọi thắc mắc và
              hỗ trợ bạn trong suốt hành trình. Hãy để chúng tôi đồng hành cùng bạn trên mọi nẻo đường, biến mỗi chuyến
              đi
              thành một kỷ niệm khó quên.
            </p>
            <div v-if="!isDescriptionExpanded"
              class="absolute bottom-0 left-0 w-full h-16 bg-gradient-to-t from-white to-transparent"></div>
          </div>
          <div class="text-center mt-6">
            <button @click="isDescriptionExpanded = !isDescriptionExpanded"
              class="text-blue-600 font-semibold hover:underline px-4 py-2 rounded-md">
              {{ isDescriptionExpanded ? 'Thu gọn' : 'Xem thêm' }}
            </button>
          </div>
        </div>
      </div>
      <div class="bg-white text-black font-sans mt-12">
        <div class="max-w-7xl mx-auto px-4 py-8">
          <hr class="border-t border-gray-300 mb-8" />
          <div class="flex flex-col md:flex-row items-center justify-between px-15 gap-8">
            <div>
              <h2 class="font-extrabold text-xl md:text-2xl text-black mb-3">
                Đăng ký nơi nghỉ của bạn
              </h2>
              <p class="text-base md:text-lg text-black font-normal leading-relaxed max-w-xl">
                Tiếp cận hàng triệu khách hàng tiềm năng và nâng tầm doanh nghiệp của bạn với chúng tôi.
              </p>
            </div>
            <div class="flex-shrink-0">
              <img
                alt="Bedroom with white bedspread, orange pillows, two bedside tables with lamps, and an orange painting on the wall"
                class="rounded-md w-full md:w-[400px] h-[180px] object-cover"
                src="https://storage.googleapis.com/a1aa/image/c8f59ed9-e281-431f-4240-7c964dd3c72e.jpg" width="400"
                height="180" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import SearchBar from "@/components/Hotel/SearchBar.vue";
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { searchHotels } from "@/api/hotelApi";
import { getAllProvinces } from "@/api/provinceApi";
import HotelCard from "@/components/Home/HotelCard.vue";

const router = useRouter();

const provinces = ref([]);
const hotDestinations = ref([]);
const hotels = ref([]);
const activeProvinceId = ref(null);

const isDescriptionExpanded = ref(false);
const loading = ref({ provinces: true, hotels: true });
const error = ref({ provinces: null, hotels: null });

const fetchProvinces = async () => {
  try {
    loading.value.provinces = true;
    const response = await getAllProvinces();
    if (response.data?.statusCode === 200) {
      provinces.value = [{ id: null, name: 'Tất cả' }, ...response.data.data];
      hotDestinations.value = response.data.data
        .sort((a, b) => b.hotelCount - a.hotelCount)
        .slice(0, 12);
    }
  } catch (e) {
    error.value.provinces = "Không thể tải danh sách tỉnh.";
  } finally {
    loading.value.provinces = false;
  }
};

const fetchHotels = async (provinceId = null) => {
  try {
    loading.value.hotels = true;
    error.value.hotels = null;
    const params = { size: 10, sortBy: 'popular' };
    if (provinceId) {
      params.provinceId = provinceId;
    }
    const response = await searchHotels(params);
    if (response.data?.statusCode === 200) {
      hotels.value = response.data.data.content;
    }
  } catch (e) {
    error.value.hotels = "Không thể tải danh sách khách sạn.";
  } finally {
    loading.value.hotels = false;
  }
};

const selectProvince = (provinceId) => {
  activeProvinceId.value = provinceId;
  fetchHotels(provinceId);
};

const searchForHotDestination = (provinceName) => {
  router.push({
    name: "HotelListing",
    query: { keyword: provinceName },
  });
};

const scrollContainer = ref(null);
const provinceScrollContainer = ref(null);

onMounted(() => {
  fetchProvinces();
  fetchHotels();
});
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