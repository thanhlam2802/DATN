<template>
  <div v-if="loading" class="flex justify-center items-center min-h-screen bg-gray-50 w-full">
    <div class="text-center">
      <i class="fas fa-spinner fa-spin text-4xl text-blue-500"></i>
      <p class="text-xl text-gray-600 mt-4">Đang tải chi tiết khách sạn...</p>
    </div>
  </div>

  <div v-else-if="error" class="text-center py-20 bg-red-50 w-full">
    <p class="text-2xl text-red-600">Rất tiếc, đã có lỗi xảy ra!</p>
    <p class="text-gray-700 mt-2">{{ error }}</p>
    <router-link to="/" class="mt-4 inline-block bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700">
      Quay về trang chủ
    </router-link>
  </div>

  <main v-else-if="hotel" class="max-w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 lg:w-[1320px]" style="padding-top: 1rem;">
    <div ref="searchWidgetContainer"
      class="sticky top-18 z-40 w-full rounded-lg border border-gray-200 bg-white shadow-lg p-3 mb-4 mt-4">
      <div class="flex flex-col md:flex-row items-stretch h-auto md:h-auto border border-gray-300 rounded-lg">
        <div ref="locationContainer"
          class="relative flex flex-grow cursor-pointer items-center p-3 bg-white hover:bg-gray-50 border-b md:border-b-0 md:border-r border-gray-200 min-w-[200px] rounded-t-md md:rounded-l-md md:rounded-tr-none">
          <i class="fas fa-map-marker-alt text-blue-500 text-xl pr-3"></i>
          <div class="flex-1">
            <label class="text-xs text-gray-500">Địa điểm hoặc khách sạn</label>
            <input type="text" v-model="searchParams.location" @focus="handleLocationFocus"
              class="w-full bg-transparent font-semibold focus:outline-none text-gray-800 truncate"
              placeholder="Tìm kiếm..." autocomplete="off" />
          </div>
          <div v-if="showLocationDropdown && suggestions.length > 0"
            class="absolute top-full mt-2 left-0 z-20 w-full bg-white border border-gray-300 rounded-lg shadow-lg max-h-56 overflow-y-auto">
            <ul>
              <li v-for="loc in suggestions" :key="loc.type + '-' + loc.id" @click="selectLocation(loc.name)"
                class="px-4 py-2 hover:bg-gray-100 cursor-pointer flex items-center gap-3">
                <i
                  :class="loc.type === 'Province' ? 'fas fa-map-marked-alt text-gray-400' : 'fas fa-hotel text-gray-400'"></i>
                <div>
                  <p class="font-semibold">{{ loc.name }}</p>
                  <p class="text-xs text-gray-500">{{ loc.type === 'Province' ? 'Tỉnh/Thành phố' : 'Khách sạn' }}</p>
                </div>
              </li>
            </ul>
          </div>
        </div>

        <div
          class="flex flex-grow-[2] items-center p-3 bg-white border-b md:border-b-0 md:border-r border-gray-200 min-w-[300px]">
          <i class="fas fa-calendar-alt text-blue-500 text-xl pr-3"></i>
          <div class="flex flex-1 items-center">
            <div class="flex-1">
              <label class="text-xs text-gray-500">Ngày nhận</label>
              <input type="date" v-model="searchParams.checkin" :min="today"
                class="w-full bg-transparent font-semibold focus:outline-none" />
            </div>
            <div class="px-4 text-center">
              <div v-if="numberOfNights > 0"
                class="text-xs font-semibold text-blue-600 bg-blue-100 rounded-full px-2 py-0.5 whitespace-nowrap">{{
                  numberOfNights }} đêm</div>
            </div>
            <div class="flex-1">
              <label class="text-xs text-gray-500">Ngày trả</label>
              <input type="date" v-model="searchParams.checkout" :min="minCheckOut"
                class="w-full bg-transparent font-semibold focus:outline-none" />
            </div>
          </div>
        </div>

        <div ref="guestsContainer" class="relative flex-grow">
          <div @click="showGuestsDropdown = !showGuestsDropdown"
            class="flex items-center p-3 bg-white hover:bg-gray-50 cursor-pointer h-full max-w-[300px]">
            <i class="fas fa-users text-blue-500 text-xl pr-3"></i>
            <div class="flex-1">
              <label class="text-xs text-gray-500">Khách và Phòng</label>
              <p class="font-semibold truncate text-gray-800">{{ guestsDisplay }}</p>
            </div>
          </div>
          <div v-if="showGuestsDropdown" aria-label="Guest and room selection"
            class="absolute top-full mt-2 right-0 z-30 w-[320px] bg-white rounded-lg shadow-lg border border-gray-200 p-4"
            role="listbox">
            <div class="flex items-center gap-3 mb-4">
              <i class="fas fa-user text-lg text-gray-600 w-5 text-center"></i>
              <span class="text-gray-900 font-semibold text-base">Người lớn</span>
              <div class="ml-auto flex items-center gap-2">
                <button @click.stop="updateGuests('adults', -1)" :disabled="searchParams.adults <= 1"
                  class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:text-gray-300 disabled:border-gray-200 disabled:bg-gray-50 flex items-center justify-center hover:bg-gray-50"
                  type="button">−</button>
                <span
                  class="w-8 h-8 flex items-center justify-center text-gray-900 font-semibold text-base select-none">{{
                    searchParams.adults }}</span>
                <button @click.stop="updateGuests('adults', 1)"
                  class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                  type="button">+</button>
              </div>
            </div>
            <div class="flex items-center gap-3 mb-4">
              <i class="fas fa-child text-lg text-gray-600 w-5 text-center"></i>
              <span class="text-gray-900 font-semibold text-base">Trẻ em</span>
              <div class="ml-auto flex items-center gap-2">
                <button @click.stop="updateGuests('children', -1)" :disabled="searchParams.children <= 0"
                  class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:text-gray-300 disabled:border-gray-200 disabled:bg-gray-50 flex items-center justify-center hover:bg-gray-50"
                  type="button">−</button>
                <span
                  class="w-8 h-8 flex items-center justify-center text-gray-900 font-semibold text-base select-none">{{
                    searchParams.children }}</span>
                <button @click.stop="updateGuests('children', 1)"
                  class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                  type="button">+</button>
              </div>
            </div>
            <div class="flex items-center gap-3">
              <i class="fas fa-bed text-lg text-gray-600 w-5 text-center"></i>
              <span class="text-gray-900 font-semibold text-base">Phòng</span>
              <div class="ml-auto flex items-center gap-2">
                <button @click.stop="updateGuests('rooms', -1)" :disabled="searchParams.rooms <= 1"
                  class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:text-gray-300 disabled:border-gray-200 disabled:bg-gray-50 flex items-center justify-center hover:bg-gray-50"
                  type="button">−</button>
                <span
                  class="w-8 h-8 flex items-center justify-center text-gray-900 font-semibold text-base select-none">{{
                    searchParams.rooms }}</span>
                <button @click.stop="updateGuests('rooms', 1)"
                  class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                  type="button">+</button>
              </div>
            </div>
            <p v-if="guestsError" class="text-red-500 text-xs mt-3 text-center">{{ guestsError }}
            </p>
          </div>
        </div>

        <button aria-label="Search"
          class="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-3 font-bold transition flex items-center justify-center rounded-b-md md:rounded-l-none md:rounded-r-md gap-2"
          @click="onSearch">
          <i class="fas fa-search"></i><span>Tìm</span>
        </button>
      </div>
    </div>

    <section class="bg-white rounded-xl shadow p-6 mb-8">
      <div class="flex flex-col lg:flex-row lg:items-start space-y-3 lg:space-y-0 lg:space-x-3 mb-8">
        <div class="flex-shrink-0 w-full lg:w-[480px] h-96 lg:h-[336px] overflow-hidden rounded-lg shadow-md">
          <img v-if="hotel.imageUrls && hotel.imageUrls.length" :src="hotel.imageUrls[0]" :alt="hotel.name"
            class="w-full h-full object-cover transition-transform duration-300 hover:scale-105" />
        </div>
        <div class="grid grid-cols-3 grid-rows-2 gap-3 flex-1">
          <div v-for="(img, idx) in hotel.imageUrls.slice(1, 7)" :key="idx"
            class="w-full h-full overflow-hidden rounded-lg shadow-md lg:w-[234.67px] h-96 lg:h-[162px]">
            <img :src="img" :alt="`Additional image ${idx + 1}`"
              class="w-full h-full object-cover transition-transform duration-300 hover:scale-105" />
          </div>
        </div>
      </div>

      <div class="flex flex-col sm:flex-row items-center justify-between mb-4">
        <div class="flex-1 text-left">
          <h1 class="text-3xl font-extrabold text-gray-900 mb-1">{{ hotel.name }}</h1>
          <div class="flex items-center flex-wrap gap-x-2 text-sm text-gray-600">
            <span class="bg-blue-100 text-blue-800 text-xs font-semibold px-2.5 py-1 rounded-full">Khách sạn</span>
            <div class="flex items-center text-yellow-400 ml-1">
              <i v-for="n in hotel.starRating" :key="n" class="fas fa-star"></i>
            </div>
            <span class="mx-2">•</span>
            <i class="fas fa-thumbs-up text-blue-500"></i>
            <span class="text-blue-600 font-semibold">{{ hotel.rating.toFixed(1) }}</span>
            <span>({{ hotel.reviewCount }} đánh giá)</span>
          </div>
          <div class="flex items-center text-sm text-gray-600 mt-2">
            <i class="fas fa-map-marker-alt text-gray-500 mr-2"></i>
            <span>{{ hotel.provinceName }}</span>
          </div>
        </div>
        <div class="flex items-center gap-4 mt-4 sm:mt-0">
          <div class="text-left"><span class="text-gray-500 text-sm block">Giá/phòng/đêm từ</span>
            <div class="text-2xl font-bold text-orange-500 whitespace-nowrap">{{ minRoomPrice }}</div>
          </div>
          <button
            class="bg-orange-500 hover:bg-orange-600 text-white rounded-full px-6 py-2 text-base font-semibold transition focus:outline-none focus:ring-2 focus:ring-orange-500"
            @click="scrollToRooms">Chọn phòng</button>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-4 mb-10">
        <div>
          <h2 class="text-xl font-bold text-gray-800 mb-4">Tổng quan</h2>
          <div class="text-sm text-gray-700 leading-relaxed space-y-4">
            <div>
              <div class="prose prose-sm max-w-none text-justify text-gray-700 line-clamp-4" v-html="hotel.description">
              </div>
              <button @click="showDescriptionModal = true" class="btn-outline-blue mt-2">
                <span>Xem thêm</span>
                <i class="fas fa-chevron-down"></i>
              </button>
            </div>
            <div class="border-t border-gray-200 pt-4 mt-4">
              <ul class="space-y-4">
                <li class="flex items-start gap-4">
                  <div
                    class="flex-shrink-0 w-8 h-8 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center">
                    <i class="fas fa-map-marker-alt"></i>
                  </div>
                  <div>
                    <p class="text-xs text-gray-500">Địa chỉ</p>
                    <p class="font-medium text-gray-800">{{ hotel.address }}</p>
                  </div>
                </li>
                <li class="flex items-start gap-4">
                  <div
                    class="flex-shrink-0 w-8 h-8 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center">
                    <i class="fas fa-phone-alt text-lg"></i>
                  </div>
                  <div>
                    <p class="text-xs text-gray-500">Điện thoại</p>
                    <a :href="`tel:${hotel.phone}`"
                      class="font-medium text-gray-800 hover:text-blue-600 hover:underline">{{ hotel.phone }}</a>
                  </div>
                </li>
                <li class="flex items-start gap-4">
                  <div
                    class="flex-shrink-0 w-8 h-8 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center">
                    <i class="fas fa-envelope text-lg"></i>
                  </div>
                  <div>
                    <p class="text-xs text-gray-500">Email</p>
                    <a :href="`mailto:${hotel.email}`"
                      class="font-medium text-gray-800 hover:text-blue-600 hover:underline">{{ hotel.email }}</a>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-800 mb-4">Các tiện ích nổi bật</h3>
          <div class="grid grid-cols-2 gap-3">
            <div v-for="amenity in visibleAmenities" :key="amenity.name"
              class="flex items-center gap-2 rounded-lg bg-gray-50 p-2 transition-colors hover:bg-gray-100">
              <i :class="amenity.icon || 'fas fa-check-circle'" class="text-base text-blue-600 w-5 text-center"></i>
              <span class="font-medium text-gray-800 text-sm">{{ amenity.name }}</span>
            </div>
          </div>
          <button v-if="hotel.amenities && hotel.amenities.length > INITIAL_AMENITIES_COUNT"
            @click="showAllAmenities = !showAllAmenities" class="btn-outline-blue mt-4">
            <span>{{ showAllAmenities ? "Ẩn bớt" : "Xem tất cả tiện ích" }}</span>
            <i class="fas fa-chevron-down" :class="{ 'rotate-180': showAllAmenities }"></i>
          </button>
        </div>
      </div>
    </section>

    <section ref="roomsSectionRef" class="bg-white rounded-xl p-6 mb-8 scroll-mt-37">
      <h2 class="text-2xl font-bold text-gray-800 mb-6">Những phòng trống tại {{ hotel.name }}</h2>

      <section class="bg-gray-50 rounded-xl shadow-inner p-6 mb-8">
        <div class="flex flex-col lg:flex-row gap-8">
          <div class="lg:w-2/3">
            <h3 class="text-lg font-bold text-gray-800 pb-4 mb-2 border-b border-gray-200">Tìm kiếm nhanh hơn bằng cách
              chọn những tiện nghi bạn cần</h3>
            <div class="flex flex-wrap gap-x-6 gap-y-3 pt-2">
              <div v-for="filter in amenityFilters" :key="filter.id" class="flex items-center">
                <label class="custom-checkbox flex items-center cursor-pointer">
                  <input :id="`filter-${filter.id}`" type="checkbox" :value="filter.id" v-model="selectedAmenities"
                    class="hidden" />
                  <span class="checkmark"></span>
                  <span class="ml-2 block text-sm text-gray-900">{{ filter.label }}</span>
                </label>
              </div>
            </div>
          </div>
          <div class="lg:w-1/3 lg:border-l lg:pl-8 border-gray-200">
            <h3 class="text-lg font-bold text-gray-800 pb-4 mb-2 border-b border-gray-200">Lựa chọn hiển thị giá</h3>
            <div class="pt-2 relative" ref="priceDropdownRef">
              <button @click="showPriceDropdown = !showPriceDropdown"
                class="w-full bg-white border border-gray-300 text-blue-600 font-semibold text-sm rounded-lg focus:ring-indigo-500 focus:border-indigo-500 block p-2.5 cursor-pointer flex justify-between items-center">
                <span>{{ selectedPriceLabel }}</span>
                <i class="fas fa-chevron-down text-gray-500"></i>
              </button>
              <div v-if="showPriceDropdown"
                class="absolute z-20 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-lg overflow-hidden">
                <div v-for="option in priceOptions" :key="option.value" @click="selectPriceOption(option.value)"
                  class="flex items-center justify-between px-4 py-2 cursor-pointer hover:bg-blue-50 transition-colors duration-150"
                  :class="{ 'bg-blue-100 text-blue-600 font-semibold': priceDisplayMode === option.value }">
                  <span>{{ option.label }}</span>
                  <i v-if="priceDisplayMode === option.value" class="fas fa-check text-blue-600 ml-2"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <div v-if="filteredRoomTypes.length === 0" class="text-center py-10">
        <p class="text-xl text-gray-700">Không tìm thấy phòng nào phù hợp với lựa chọn của bạn.</p>
      </div>
      <div v-else v-for="room in filteredRoomTypes" :key="room.id"
        class="flex flex-col md:flex-row gap-6 border border-gray-200 rounded-lg p-5 mb-6">
        <div class="relative flex flex-col gap-4 md:w-1/3">
          <h3 class="text-lg font-bold text-black">{{ room.roomType }}</h3>
          <div class="relative w-full h-48 overflow-hidden rounded-lg shadow-md">
            <transition :name="slideDirection === 'next' ? 'slide-right' : 'slide-left'">
              <img v-if="room.imageUrls && room.imageUrls.length" :key="roomImageIndex[room.id]"
                :src="room.imageUrls[roomImageIndex[room.id]]" :alt="room.roomType"
                class="w-full h-full object-cover absolute inset-0" />
            </transition>
            <button @click="prevRoomImage(room.id)"
              class="absolute left-2 top-1/2 transform -translate-y-1/2 bg-white/60 rounded-full p-2 hover:bg-white/80 z-10"><i
                class="fas fa-chevron-left text-gray-700"></i></button>
            <button @click="nextRoomImage(room.id)"
              class="absolute right-2 top-1/2 transform -translate-y-1/2 bg-white/60 rounded-full p-2 hover:bg-white/80 z-10"><i
                class="fas fa-chevron-right text-gray-700"></i></button>
          </div>
          <div class="flex flex-col gap-2 text-sm text-gray-700 font-semibold mt-2">
            <div class="flex items-center gap-2"><i class="fas fa-ruler-combined text-blue-500"></i><span>{{
              room.roomArea || 'N/A' }} m²</span></div>
            <div class="flex items-center gap-2"><i class="fas fa-smoking-ban text-gray-500"></i><span>Không hút
                thuốc</span></div>
            <div class="flex flex-wrap gap-x-6 gap-y-2 text-xs text-gray-500 font-normal mt-2">
              <div v-for="amenity in room.amenities.slice(0, 7)" :key="amenity.name" class="flex items-center gap-1">
                <i :class="amenity.icon || 'fas fa-check'"></i><span>{{ amenity.name }}</span>
              </div>
            </div>
          </div>
          <button @click="openModal(room)"
            class="font-bold text-sm flex items-center gap-1 hover:underline mt-2 text-blue-600"><i
              class="fas fa-info-circle"></i> Xem chi tiết phòng</button>
        </div>
        <div class="md:w-2/3 border border-gray-100 rounded-lg shadow-sm">
          <table class="w-full text-sm text-gray-700 border-collapse">
            <thead class="bg-gray-50 text-gray-800 font-bold">
              <tr>
                <th class="text-left py-3 px-4 border-b border-gray-200 w-2/5">Lựa chọn phòng</th>
                <th class="text-center py-3 px-4 border-b border-gray-200 w-1/5">Khách</th>
                <th class="text-center py-3 px-4 border-b border-gray-200 w-2/5">Giá/phòng/đêm</th>
                <th class="w-16 border-b border-gray-200"></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="variant in room.availableVariants" :key="variant.id"
                class="border-b border-gray-100 last:border-b-0">
                <td class="py-4 px-4 align-top">
                  <p class="text-xs text-gray-500">{{ variant.variantName }}</p>
                  <p class="font-bold text-base text-black mb-1">{{ variant.hasBreakfast ? 'Bao gồm bữa sáng' :
                    'Không bao gồm bữa sáng' }}</p>
                  <div class="mt-2 space-y-1">
                    <div class="flex items-center gap-1 text-gray-500 text-xs"><i class="fas fa-bed w-4"></i><span>{{
                      room.bedType }}</span></div>
                    <div v-if="variant.cancellable" class="flex items-center gap-1 text-green-600 text-xs"><i
                        class="fas fa-check-circle w-4"></i><span>Miễn phí hủy phòng</span></div>
                    <div v-if="variant.payAtHotel" class="flex items-center gap-1 text-green-600 text-xs"><i
                        class="fas fa-check-circle w-4"></i><span>Thanh toán tại khách sạn</span></div>
                  </div>
                </td>
                <td class="text-center align-top py-4 px-4 text-xl text-gray-600 relative group">
                  <i class="fas fa-user-friends cursor-pointer pt-10"></i>
                  <div
                    class="absolute bottom-full left-1/2 -translate-x-1/2 translate-y-9 w-max bg-gray-800 text-white text-xs rounded py-1 px-2 opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap z-10">
                    {{ room.maxAdults }} người lớn<span v-if="room.maxChildren && room.maxChildren > 0">, {{
                      room.maxChildren }} trẻ em</span>
                    <div
                      class="absolute top-full left-1/2 -translate-x-1/2 border-4 border-transparent border-t-gray-800">
                    </div>
                  </div>
                </td>
                <td class="text-center align-middle py-4 px-4">
                  <div class="relative inline-block">
                    <p class="text-orange-500 font-bold text-base peer">
                      {{ formatPrice(priceDisplayMode === 'totalPrice' ? (variant.totalPrice ?? variant.price) :
                        variant.price) }}
                    </p>
                    <p class="text-xs text-gray-400 font-normal mt-1">
                      {{ priceDisplayMode === 'totalPrice' ? 'Đã bao gồm thuế và phí' : 'Chưa bao gồm thuế và phí' }}
                    </p>
                    <div
                      class="absolute bottom-full mb-2 left-1/2 -translate-x-1/2 translate-z-0 w-max max-w-[280px] bg-gray-800 text-white text-xs rounded-lg py-2 px-3 opacity-0 peer-hover:opacity-100 transition-opacity whitespace-nowrap z-9999 shadow-lg cursor-default">
                      <div class="grid grid-cols-[auto_1fr] gap-x-3 gap-y-1 text-left">
                        <span class="font-medium text-gray-300">Giá phòng:</span>
                        <span class="text-right font-semibold">{{ formatPrice(variant.price) }}</span>

                        <span class="font-medium text-gray-300">Thuế và phí:</span>
                        <span class="text-right font-semibold">{{ formatPrice(getTaxesAndFees(variant)) }}</span>

                        <div class="col-span-2 border-t border-gray-600 my-1"></div>

                        <span class="font-bold text-gray-100">Tổng cộng:</span>
                        <span class="text-right font-bold text-gray-100">{{ formatPrice(variant.totalPrice ??
                          variant.price) }}</span>
                      </div>
                      <div
                        class="absolute left-1/2 -translate-x-1/2 top-full border-4 border-transparent border-t-gray-800">
                      </div>
                    </div>
                  </div>
                </td>
                <td class="text-center align-middle py-4 px-4">
                  <button
                    class="rounded-lg px-5 py-2 text-sm font-semibold transition-colors duration-200 shadow-md bg-blue-600 text-white hover:bg-blue-700"
                    @click="goToBooking(room, variant)">
                    Chọn
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>

    <section v-if="otherHotels.length > 0" class="w-full bg-[#f0f8ff] rounded-lg p-6 shadow-sm mb-8">
      <div class="mb-6">
        <h2 class="font-sans font-extrabold text-xl leading-6 text-gray-800">Cơ sở lưu trú khác bạn có thể thích</h2>
        <p class="font-sans text-sm text-gray-600">Những khách sạn tương tự trong khu vực</p>
      </div>
      <div aria-label="Accommodation suggestions" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-70">
        <HotelCard v-for="other in otherHotels" :key="other.id" :hotel="other" />
      </div>
    </section>

    <section class="bg-white rounded-xl shadow p-6 mb-8">
      <div class="flex items-center gap-4 mb-8">
        <h2 class="text-2xl font-bold text-gray-800">{{ hotel.reviewCount }} đánh giá</h2>
      </div>
      <div v-if="reviewsList.length > 0" class="space-y-6">
        <div v-for="review in paginatedReviews" :key="review.id"
          class="bg-gradient-to-br from-blue-50 to-white p-6 rounded-2xl border border-gray-100 shadow-md mb-6 transition hover:shadow-lg">
          <div class="flex items-start gap-5">
            <img :alt="review.author" class="w-14 h-14 rounded-full object-cover border-2 border-blue-200 shadow"
              :src="review.avatar || 'https://i.pravatar.cc/150?u=' + review.author" />
            <div class="flex-1">
              <div class="flex items-center justify-between">
                <div>
                  <span class="font-semibold text-lg text-gray-900">{{ review.author }}</span>
                  <span class="ml-2 text-xs text-gray-400">{{ review.date }}</span>
                </div>
                <div class="flex items-center gap-1">
                  <i class="fas fa-star text-yellow-400 text-lg"></i>
                  <span class="font-bold text-yellow-500 text-base">{{ review.rating }}/5</span>
                </div>
              </div>
              <p class="mt-3 text-gray-800 text-base leading-relaxed whitespace-pre-line">
                {{ review.content }}
              </p>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="text-center text-gray-500 text-base py-8">
        Chưa có nhận xét nào cho khách sạn này. Hãy là người đầu tiên đánh giá!
      </div>
      <div class="mt-10 bg-slate-50 rounded-xl p-6 shadow flex flex-col gap-4 max-w-xl mx-auto">
        <h3 class="text-lg font-bold text-gray-800 mb-2 flex items-center gap-2">
          <i class="fas fa-pen-nib text-blue-500"></i> Viết đánh giá của bạn
        </h3>
        <div class="flex items-center gap-2 mb-2">
          <span class="text-gray-700 font-medium mr-2">Chọn số sao:</span>
          <template v-for="n in 5" :key="n">
            <i class="fas fa-star cursor-pointer text-2xl transition"
              :class="n <= newReview.rating ? 'text-yellow-400' : 'text-gray-300'" @click="newReview.rating = n"></i>
          </template>
        </div>
        <textarea v-model="newReview.content" rows="3" maxlength="500"
          class="w-full border border-gray-300 rounded-lg p-3 text-gray-800 focus:ring-2 focus:ring-blue-200 focus:outline-none resize-none"
          placeholder="Chia sẻ cảm nhận của bạn về khách sạn này..."></textarea>
        <button @click="submitReview" :disabled="submittingReview || !canSubmitReview"
          class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg text-lg transition disabled:opacity-60 disabled:cursor-not-allowed flex items-center justify-center gap-2">
          <i class="fas fa-paper-plane"></i>
          <span v-if="!submittingReview">Gửi đánh giá</span>
          <span v-else>Đang gửi...</span>
        </button>
        <p v-if="reviewError" class="text-red-500 text-sm text-center mt-2">{{ reviewError }}</p>
        <p v-if="reviewSuccess" class="text-green-600 text-sm text-center mt-2">{{ reviewSuccess }}</p>
      </div>
      <div v-if="reviewsList.length > 0" class="mt-8 flex flex-col sm:flex-row justify-between items-center gap-4">
        <div class="flex items-center gap-2">
          <label for="reviews-per-page" class="text-sm font-medium text-gray-700">Hiển thị:</label>
          <select id="reviews-per-page" v-model="reviewsPerPage"
            class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block p-1.5">
            <option v-for="option in reviewsPerPageOptions" :key="option.value" :value="option.value">{{ option.text }}
            </option>
          </select>
        </div>
        <div class="flex items-center gap-4">
          <span class="text-sm font-medium text-gray-700">Trang {{ currentPage }} / {{ totalPages }}</span>
          <div class="flex items-center gap-2">
            <button @click="prevPage" :disabled="currentPage === 1"
              class="inline-flex items-center justify-center h-8 w-8 rounded-full border bg-white text-gray-600 hover:bg-gray-100 disabled:bg-gray-50 disabled:text-gray-300 disabled:cursor-not-allowed transition-colors"><i
                class="fas fa-chevron-left text-xs"></i></button>
            <button @click="nextPage" :disabled="currentPage === totalPages"
              class="inline-flex items-center justify-center h-8 w-8 rounded-full border bg-white text-gray-600 hover:bg-gray-100 disabled:bg-gray-50 disabled:text-gray-300 disabled:cursor-not-allowed transition-colors"><i
                class="fas fa-chevron-right text-xs"></i></button>
          </div>
        </div>
      </div>
    </section>
  </main>

  <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center pt-15"
    style="background-color: rgba(0,0,0,0.5)" @click.self="closeModal">
    <div
      class="bg-white rounded-xl shadow-xl max-w-4xl w-full mx-4 lg:mx-0 overflow-hidden relative flex flex-col md:flex-row"
      style="max-height: 80vh">
      <div class="md:w-1/2 w-full flex flex-col bg-gray-50">
        <div class="flex-grow flex items-center justify-center p-4 relative">
          <div
            class="w-full h-80 bg-gray-200 rounded-lg flex items-center justify-center overflow-hidden shadow relative">
            <img v-if="selectedRoom.imageUrls && selectedRoom.imageUrls.length"
              :src="selectedRoom.imageUrls[modalImageIndex]" :alt="selectedRoom.roomType"
              class="w-full h-full object-cover transition-all duration-200" />
            <button v-if="selectedRoom.imageUrls && selectedRoom.imageUrls.length > 1" @click="prevModalImage"
              class="absolute left-2 top-1/2 transform -translate-y-1/2 bg-black/40 hover:bg-black/60 text-white rounded-full p-2 z-10">
              <i class="fas fa-chevron-left"></i>
            </button>
            <button v-if="selectedRoom.imageUrls && selectedRoom.imageUrls.length > 1" @click="nextModalImage"
              class="absolute right-2 top-1/2 transform -translate-y-1/2 bg-black/40 hover:bg-black/60 text-white rounded-full p-2 z-10">
              <i class="fas fa-chevron-right"></i>
            </button>
            <div v-if="selectedRoom.imageUrls && selectedRoom.imageUrls.length > 1"
              class="absolute top-3 right-4 bg-black/60 text-white text-xs font-semibold rounded-full px-3 py-1 z-20">
              {{ modalImageIndex + 1 }}/{{ selectedRoom.imageUrls.length }}
            </div>
          </div>
        </div>
        <div class="flex gap-3 justify-center p-3 bg-white relative items-center group">
          <button v-if="selectedRoom.imageUrls && selectedRoom.imageUrls.length > 1" @click="prevModalImage"
            class="absolute left-0 top-1/2 -translate-y-1/2 bg-black/60 hover:bg-blue-600 text-white rounded-full w-8 h-8 flex items-center justify-center z-10 ml-1 opacity-0 group-hover:opacity-100 transition-opacity">
            <i class="fas fa-chevron-left"></i>
          </button>
          <div ref="thumbsContainer" class="flex gap-3 overflow-x-auto scrollbar-hide w-full px-8">
            <img v-for="(img, idx) in selectedRoom.imageUrls" :key="idx" :src="img" :alt="`Ảnh ${idx + 1}`"
              class="w-24 h-20 object-cover rounded-lg cursor-pointer border-2 transition-all duration-200 mt-2 mb-2"
              :class="modalImageIndex === idx ? 'border-blue-500 shadow-lg scale-105' : 'border-gray-200 opacity-70 hover:opacity-100'"
              @click="modalImageIndex = idx" />
          </div>
          <button v-if="selectedRoom.imageUrls && selectedRoom.imageUrls.length > 1" @click="nextModalImage"
            class="absolute right-0 top-1/2 -translate-y-1/2 bg-black/60 hover:bg-blue-600 text-white rounded-full w-8 h-8 flex items-center justify-center z-10 mr-1 opacity-0 group-hover:opacity-100 transition-opacity">
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </div>
      <div class="md:w-1/2 w-full flex flex-col h-full">
        <div class="flex-grow overflow-y-auto p-6">
          <h2 class="text-2xl font-bold text-gray-800 mb-4">{{ selectedRoom.roomType }}</h2>
          <div class="mb-6 text-sm text-gray-700">
            <p class="flex items-center mb-2"><i class="fas fa-ruler-combined text-blue-500 mr-2"></i>Diện tích: <span
                class="font-semibold ml-1">{{ selectedRoom.roomArea }} m²</span></p>
            <p class="flex items-center"><i class="fas fa-user-friends text-blue-500 mr-2"></i>Sức chứa: <span
                class="font-semibold ml-1">{{ selectedRoom.maxAdults }} người lớn</span><span
                v-if="selectedRoom.maxChildren && selectedRoom.maxChildren > 0">,<span class="font-semibold ml-1">{{
                  selectedRoom.maxChildren }} trẻ em</span></span></p>
          </div>
          <div class="mb-6">
            <h3 class="text-lg font-semibold text-gray-800 mb-2">Toàn bộ tiện ích:</h3>
            <div class="grid grid-cols-2 gap-x-4 gap-y-2">
              <div v-for="amenity in selectedRoom.amenities" :key="amenity.name"
                class="flex items-center space-x-2 bg-gray-100 rounded-full px-3 py-1 text-xs text-gray-700 w-fit">
                <i :class="amenity.icon || 'fas fa-check'" class="text-green-600"></i><span>{{ amenity.name }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="sticky bottom-0 flex-shrink-0 p-6 border-t border-gray-200 bg-white">
          <div v-if="selectedRoom && selectedRoom.availableVariants && selectedRoom.availableVariants.length > 0">
            <h3 class="text-lg font-semibold text-gray-800 mb-2">Giá khởi điểm từ:</h3>
            <div class="flex items-baseline gap-2">
              <p class="text-2xl font-bold text-red-600">{{ formatPrice(minPriceOfSelectedRoom) }}</p>
              <span class="text-sm text-gray-600 font-medium">/ phòng / đêm</span>
            </div>
            <p class="text-xs text-gray-500">
              <span v-if="priceDisplayMode === 'totalPrice'">(đã bao gồm thuế phí)</span>
              <span v-else>(chưa bao gồm thuế phí)</span>
            </p>
            <button @click="closeModal"
              class="mt-4 w-full bg-blue-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors">
              Thêm sự lựa chọn
            </button>
          </div>
          <div v-else>
            <p class="text-gray-600 py-4">Hiện không có lựa chọn giá nào cho loại phòng này.</p>
          </div>
        </div>
      </div>
      <button @click="closeModal" class="absolute top-4 right-4 text-gray-500 hover:text-gray-800 z-50"><i
          class="fas fa-times fa-lg"></i></button>
    </div>
  </div>

  <div v-if="showDescriptionModal" class="fixed inset-0 z-50 flex items-center justify-center pt-15"
    style="background-color: rgba(0,0,0,0.5)" @click.self="showDescriptionModal = false">
    <div class="bg-white rounded-2xl shadow-xl max-w-2xl w-full mx-4 lg:mx-0 overflow-hidden relative flex flex-col"
      style="max-height: 80vh">
      <button @click="showDescriptionModal = false"
        class="absolute top-3 right-3 text-gray-500 hover:text-gray-800 z-50 text-2xl"><i
          class="fas fa-times"></i></button>
      <h2 class="text-xl font-bold text-gray-800 mb-4 px-8 pt-8">Giới thiệu {{ hotel.name }}</h2>
      <hr class="mb-4 mt-2" />
      <div class="flex-1 overflow-auto px-8">
        <HtmlContent :content="hotel.description" />
      </div>
      <button @click="showDescriptionModal = false"
        class="m-8 w-auto bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-4 rounded-lg text-lg transition-colors">
        Khám phá thêm về {{ hotel.name }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getHotelById, getHotelReviews, searchHotels, createHotelReview } from "@/api/hotelApi";
import { getAllProvinces } from "@/api/provinceApi.js";
import HotelCard from '@/components/Home/HotelCard.vue';
import HtmlContent from '@/components/HtmlContent.vue';

const hotel = ref(null);
const reviewsList = ref([]);
const otherHotels = ref([]);
const loading = ref(true);
const error = ref(null);
const route = useRoute();
const router = useRouter();
const searchParams = ref({
  location: '',
  checkin: '',
  checkout: '',
  adults: 2,
  children: 0,
  rooms: 1,
});

const showMoreOverview = ref(false);
const showAllAmenities = ref(false);
const showModal = ref(false);
const selectedRoom = ref(null);
const roomImageIndex = ref({});
const modalImageIndex = ref(0);
const slideDirection = ref("next");
const roomsSectionRef = ref(null);
const searchWidgetContainer = ref(null);
const locationContainer = ref(null);
const guestsContainer = ref(null);
const showLocationDropdown = ref(false);
const showGuestsDropdown = ref(false);
const guestsError = ref('');
const errorTimeout = ref(null);
const locationBeforeEdit = ref('');
const priceDropdownRef = ref(null);
const showPriceDropdown = ref(false);
const priceDisplayMode = ref('price');
const provinces = ref([]);
const hotelSuggestions = ref([]);
const debounceTimer = ref(null);
const showDescriptionModal = ref(false);
const thumbsContainer = ref(null);

const newReview = ref({ rating: 0, content: '' });
const submittingReview = ref(false);
const reviewError = ref('');
const reviewSuccess = ref('');
const canSubmitReview = computed(() => newReview.value.rating > 0 && newReview.value.content.trim().length > 0);

const goToBooking = (room, variant) => {
  router.push({
    name: 'HotelBooking',
    query: {
      hotelTitle: hotel.value.name,
      hotellocation: hotel.value.provinceName,
      hotelRating: hotel.value.rating,
      hotelReviews: hotel.value.reviewCount,
      hotelImages: JSON.stringify(hotel.value.imageUrls),
      starRating: hotel.value.starRating,
      variantName: variant.variantName,
      variantBreakfast: variant.hasBreakfast ? 'Gồm bữa sáng' : 'Không gồm bữa sáng',
      roomBed: room.bedType,
      variantOriginalPrice: variant.price,
      variantDiscountedPrice: variant.totalPrice ?? variant.price,
      nights: numberOfNights.value || 1,
      checkin: searchParams.value.checkin,
      checkout: searchParams.value.checkout,
      maxAdults: room.maxAdults,
      maxChildren: room.maxChildren,
      discount: 0,
      serviceFee: 0,
      rooms: searchParams.value.rooms,
    }
  });
};

const amenityFilters = ref([
  { id: "cancellable", label: "Miễn phí hủy phòng" },
  { id: "hasBreakfast", label: "Có bữa sáng" },
  { id: "payAtHotel", label: "Thanh toán tại khách sạn" },
]);
const selectedAmenities = ref([]);
const today = new Date().toISOString().split('T')[0];

const fetchHotelData = async (hotelId) => {
  loading.value = true;
  error.value = null;
  const apiParams = {
    checkInDate: searchParams.value.checkin,
    checkOutDate: searchParams.value.checkout,
  };
  try {
    const [hotelResponse, reviewsResponse] = await Promise.all([
      getHotelById(hotelId, apiParams),
      getHotelReviews(hotelId)
    ]);

    if (hotelResponse.data?.statusCode === 200) {
      hotel.value = hotelResponse.data.data;
      searchParams.value.location = hotel.value.name;
      locationBeforeEdit.value = hotel.value.name;
      initializeRoomImageIndices();
      if (hotel.value.provinceId) {
        fetchOtherHotels(hotel.value.provinceId, hotelId);
      }
    } else {
      throw new Error(hotelResponse.data?.message || "Không thể tải thông tin khách sạn.");
    }

    if (reviewsResponse.data?.statusCode === 200) {
      reviewsList.value = reviewsResponse.data.data;
    } else {
      reviewsList.value = [];
    }
  } catch (err) {
    error.value = "Không tìm thấy khách sạn hoặc đã có lỗi xảy ra. Vui lòng thử lại.";
    hotel.value = null;
  } finally {
    loading.value = false;
  }
};

const fetchOtherHotels = async (provinceId, currentHotelId) => {
  try {
    const response = await searchHotels({ provinceId: provinceId, size: 4 });
    if (response.data?.statusCode === 200) {
      otherHotels.value = response.data.data.content.filter(h => h.id != currentHotelId).slice(0, 3);
    }
  } catch (err) {
    otherHotels.value = [];
  }
}

const minCheckOut = computed(() => {
  if (!searchParams.value.checkin) return today;
  const d = new Date(searchParams.value.checkin);
  d.setDate(d.getDate() + 1);
  return d.toISOString().split("T")[0];
});

const numberOfNights = computed(() => {
  if (searchParams.value.checkin && searchParams.value.checkout) {
    const s = new Date(searchParams.value.checkin);
    const e = new Date(searchParams.value.checkout);
    if (e <= s) return 0;
    return Math.ceil((e - s) / (1000 * 60 * 60 * 24));
  }
  return 0;
});

const guestsDisplay = computed(() => `${searchParams.value.adults} người lớn, ${searchParams.value.children} trẻ em, ${searchParams.value.rooms} phòng`);

const suggestions = computed(() => {
  const keyword = searchParams.value.location.toLowerCase();
  const provinceResults = provinces.value
    .filter(p => p.name.toLowerCase().includes(keyword))
    .map(p => ({ id: `p-${p.id}`, name: p.name, type: 'Province' }));
  const hotelResults = hotelSuggestions.value.map(h => ({ id: `h-${h.id}`, name: h.name, type: 'Hotel' }));
  return [...provinceResults, ...hotelResults];
});

const INITIAL_AMENITIES_COUNT = 6;

const visibleAmenities = computed(() => {
  if (!hotel.value?.amenities) {
    return [];
  }
  if (showAllAmenities.value) {
    return hotel.value.amenities;
  }
  return hotel.value.amenities.slice(0, INITIAL_AMENITIES_COUNT);
});

const filteredRoomTypes = computed(() => {
  if (!hotel.value?.availableRooms) return [];

  const roomsCopy = JSON.parse(JSON.stringify(hotel.value.availableRooms));

  if (selectedAmenities.value.length === 0) {
    return roomsCopy;
  }

  const filteredRooms = roomsCopy.map(room => {
    const matchingVariants = room.availableVariants.filter(variant => {
      return selectedAmenities.value.every(amenityKey => variant[amenityKey] === true);
    });
    return { ...room, availableVariants: matchingVariants };
  }).filter(room => room.availableVariants.length > 0);

  return filteredRooms;
});

const minRoomPrice = computed(() => {
  if (!hotel.value?.availableRooms?.length) return "Đang cập nhật";
  let minPrice = Infinity;
  hotel.value.availableRooms.forEach(room => {
    room.availableVariants.forEach(variant => {
      const priceToCompare = priceDisplayMode.value === 'totalPrice' ? (variant.totalPrice ?? variant.price) : variant.price;
      if (typeof priceToCompare === 'number' && priceToCompare < minPrice) {
        minPrice = priceToCompare;
      }
    });
  });
  return minPrice === Infinity ? "Liên hệ" : formatPrice(minPrice);
});

const priceOptions = computed(() => [
  { value: 'price', label: 'Tổng giá (chưa bao gồm thuế và phí)' },
  { value: 'totalPrice', label: 'Tổng giá (bao gồm thuế và phí)' },
]);

const selectedPriceLabel = computed(() => {
  const selected = priceOptions.value.find(option => option.value === priceDisplayMode.value);
  return selected ? selected.label : 'Chọn hiển thị giá';
});


const minPriceOfSelectedRoom = computed(() => {
  if (!selectedRoom.value?.availableVariants?.length) {
    return null;
  }
  const variants = selectedRoom.value.availableVariants;
  if (priceDisplayMode.value === 'totalPrice') {
    const prices = variants.map(v => (typeof v.totalPrice === 'number' ? v.totalPrice : v.price));
    return Math.min(...prices);
  }
  return Math.min(...variants.map(v => v.price));
});

const getTaxesAndFees = (variant) => {
  if (typeof variant.totalPrice === 'number' &&
    typeof variant.price === 'number' &&
    variant.totalPrice >= variant.price) {
    return variant.totalPrice - variant.price;
  }
  return null;
};

const currentPage = ref(1);
const reviewsPerPage = ref(5);
const reviewsPerPageOptions = ref([
  { value: 5, text: "5 đánh giá" },
  { value: 10, text: "10 đánh giá" },
]);

const totalPages = computed(() => {
  if (reviewsList.value.length === 0) return 1;
  return Math.ceil(reviewsList.value.length / reviewsPerPage.value);
});

const paginatedReviews = computed(() => {
  const start = (currentPage.value - 1) * reviewsPerPage.value;
  const end = start + reviewsPerPage.value;
  return reviewsList.value.slice(start, end);
});

const nextPage = () => { if (currentPage.value < totalPages.value) currentPage.value++; };
const prevPage = () => { if (currentPage.value > 1) currentPage.value--; };

watch(reviewsPerPage, () => { currentPage.value = 1; });

const formatPrice = (price) => {
  if (price == null || isNaN(price)) return "N/A";
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    currencyDisplay: 'code'
  }).format(price).replace('VND', ' VND');
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
};

const onSearch = async () => {
  const searchData = {
    location: searchParams.value.location,
    checkin: searchParams.value.checkin,
    checkout: searchParams.value.checkout,
    adults: searchParams.value.adults,
    children: searchParams.value.children,
    rooms: searchParams.value.rooms,
  };
  localStorage.setItem('lastSearchParams', JSON.stringify(searchData));

  if (searchParams.value.location.trim() !== hotel.value.name) {
    router.push({
      name: 'HotelListing',
      query: {
        keyword: searchParams.value.location,
        checkInDate: searchParams.value.checkin,
        checkOutDate: searchParams.value.checkout,
        numAdults: searchParams.value.adults,
        numChildren: searchParams.value.children,
        rooms: searchParams.value.rooms,
      }
    });
  } else {
    const newQuery = { ...route.query, checkInDate: searchParams.value.checkin, checkOutDate: searchParams.value.checkout, numAdults: searchParams.value.adults, numChildren: searchParams.value.children, rooms: searchParams.value.rooms };
    await router.replace({ query: newQuery });
    fetchHotelData(route.params.id);
  }
};
const scrollToRooms = () => roomsSectionRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' });

const initializeRoomImageIndices = () => {
  hotel.value?.availableRooms?.forEach(room => { roomImageIndex.value[room.id] = 0; });
};

const nextRoomImage = (roomId) => {
  slideDirection.value = 'next';
  const room = hotel.value.availableRooms.find(r => r.id === roomId);
  if (!room?.imageUrls?.length) return;
  roomImageIndex.value[roomId] = (roomImageIndex.value[roomId] + 1) % room.imageUrls.length;
};

const prevRoomImage = (roomId) => {
  slideDirection.value = 'prev';
  const room = hotel.value.availableRooms.find(r => r.id === roomId);
  if (!room?.imageUrls?.length) return;
  roomImageIndex.value[roomId] = (roomImageIndex.value[roomId] - 1 + room.imageUrls.length) % room.imageUrls.length;
};

const openModal = (room) => {
  selectedRoom.value = room;
  modalImageIndex.value = 0;
  showModal.value = true;
};
const closeModal = () => {
  showModal.value = false;
  selectedRoom.value = null;
};

const handleLocationFocus = () => {
  locationBeforeEdit.value = searchParams.value.location;
  showLocationDropdown.value = true;
};

const fetchHotelSuggestions = async (keyword) => {
  try {
    const response = await searchHotels({ keyword: keyword, size: 5 });
    if (response.data?.statusCode === 200) {
      hotelSuggestions.value = response.data.data.content;
    }
  } catch (error) { }
};

const selectLocation = (loc) => {
  searchParams.value.location = loc
  showLocationDropdown.value = false
};

const updateGuests = (type, amount) => {
  const params = searchParams.value;
  if (errorTimeout.value) clearTimeout(errorTimeout.value);
  guestsError.value = '';

  if (type === 'adults') {
    const newAdults = params.adults + amount;
    if (newAdults >= 1) {
      params.adults = newAdults;
      if (params.adults < params.rooms) {
        params.rooms = params.adults;
      }
    }
  } else if (type === 'children') {
    params.children = Math.max(0, params.children + amount);
  } else if (type === 'rooms') {
    const newRooms = params.rooms + amount;
    if (amount > 0) {
      if (newRooms > params.adults) {
        guestsError.value = 'Số phòng không thể nhiều hơn số người lớn.';
        errorTimeout.value = setTimeout(() => { guestsError.value = '' }, 3000);
      } else {
        params.rooms = newRooms;
      }
    } else {
      params.rooms = Math.max(1, newRooms);
    }
  }
};

const handleClickOutside = (event) => {
  if (priceDropdownRef.value && !priceDropdownRef.value.contains(event.target)) {
    showPriceDropdown.value = false;
  }
  if (searchWidgetContainer.value && !searchWidgetContainer.value.contains(event.target)) {
    showLocationDropdown.value = false;
    showGuestsDropdown.value = false;
    if (searchParams.value.location.trim() === '' && locationBeforeEdit.value) {
      searchParams.value.location = locationBeforeEdit.value;
    }
  } else {
    if (locationContainer.value && !locationContainer.value.contains(event.target)) {
      showLocationDropdown.value = false;
    }
    if (guestsContainer.value && !guestsContainer.value.contains(event.target)) {
      showGuestsDropdown.value = false;
    }
  }
};

watch(() => searchParams.value.checkin, (newCheckin) => {
  if (newCheckin && searchParams.value.checkout) {
    const checkinDate = new Date(newCheckin);
    const checkoutDate = new Date(searchParams.value.checkout);
    if (checkoutDate <= checkinDate) {
      const nextDay = new Date(checkinDate);
      nextDay.setDate(nextDay.getDate() + 1);
      searchParams.value.checkout = nextDay.toISOString().split('T')[0];
    }
  }
});

watch(() => route.query, (q) => {
  searchParams.value.location = q.keyword || hotel.value?.name || '';
  searchParams.value.checkin = q.checkInDate || today;
  searchParams.value.checkout = q.checkOutDate || minCheckOut.value;
  searchParams.value.adults = Number(q.numAdults) || 2;
  searchParams.value.children = Number(q.numChildren) || 0;
  searchParams.value.rooms = Number(q.rooms) || 1;
}, { deep: true, immediate: true });

watch(() => route.params.id, (newId, oldId) => {
  if (newId && newId !== oldId) {
    fetchHotelData(newId);
    window.scrollTo(0, 0);
  }
}, { immediate: true });

onMounted(async () => {
  document.addEventListener('click', handleClickOutside);
  try {
    const response = await getAllProvinces();
    provinces.value = response.data?.data || [];
  } catch (error) { }
});

watch(() => searchParams.value.location, (newKeyword) => {
  if (newKeyword && newKeyword !== hotel.value?.name) {
    clearTimeout(debounceTimer.value);
    debounceTimer.value = setTimeout(() => {
      fetchHotelSuggestions(newKeyword);
    }, 300);
  } else {
    hotelSuggestions.value = [];
  }
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});

const selectPriceOption = (value) => {
  priceDisplayMode.value = value;
  showPriceDropdown.value = false;
};

function prevModalImage() {
  if (!selectedRoom.value?.imageUrls?.length) return;
  modalImageIndex.value = (modalImageIndex.value - 1 + selectedRoom.value.imageUrls.length) % selectedRoom.value.imageUrls.length;
  scrollThumbToActive();
}
function nextModalImage() {
  if (!selectedRoom.value?.imageUrls?.length) return;
  modalImageIndex.value = (modalImageIndex.value + 1) % selectedRoom.value.imageUrls.length;
  scrollThumbToActive();
}
function scrollThumbs(direction) {
  const el = thumbsContainer.value;
  if (!el) return;
  const scrollAmount = 120;
  if (direction === 'left') el.scrollLeft -= scrollAmount;
  else el.scrollLeft += scrollAmount;
}
function scrollThumbToActive() {
  const el = thumbsContainer.value;
  if (!el) return;
  const active = el.querySelectorAll('img')[modalImageIndex.value];
  if (active) {
    const offsetLeft = active.offsetLeft;
    const offsetWidth = active.offsetWidth;
    const containerWidth = el.offsetWidth;
    if (offsetLeft < el.scrollLeft || offsetLeft + offsetWidth > el.scrollLeft + containerWidth) {
      el.scrollLeft = offsetLeft - containerWidth / 2 + offsetWidth / 2;
    }
  }
}
watch(modalImageIndex, () => scrollThumbToActive());

const submitReview = async () => {
  reviewError.value = '';
  reviewSuccess.value = '';
  if (!canSubmitReview.value) return;
  submittingReview.value = true;
  try {
    await createHotelReview(hotel.value.id, {
      rating: newReview.value.rating,
      content: newReview.value.content.trim()
    });
    reviewSuccess.value = 'Đánh giá của bạn đã được gửi!';
    newReview.value.rating = 0;
    newReview.value.content = '';
    await fetchHotelData(hotel.value.id);
  } catch (e) {
    if (e.response && e.response.status === 401) {
      if (window.$toast) window.$toast('Bạn cần đăng nhập để gửi đánh giá!', 'error');
      return;
    } else {
      reviewError.value = 'Gửi đánh giá thất bại. Vui lòng thử lại.';
    }
  } finally {
    submittingReview.value = false;
  }
};
</script>

<style scoped>
.line-clamp-4 {
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-clamp: 4;
}

.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
  transition: transform 0.3s ease;
}

.slide-left-enter-from {
  transform: translateX(100%);
}

.slide-left-leave-to {
  transform: translateX(-100%);
}

.slide-right-enter-from {
  transform: translateX(-100%);
}

.slide-right-leave-to {
  transform: translateX(100%);
}

.custom-checkbox {
  position: relative;
  display: flex;
  align-items: center;
}

.custom-checkbox .checkmark {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  border: 2px solid #d1d5db;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color 0.2s, background 0.2s;
}

.custom-checkbox input:checked+.checkmark {
  background: #1890ff;
  border-color: #1890ff;
}

.custom-checkbox .checkmark::after {
  content: '';
  display: block;
  width: 14px;
  height: 14px;
  mask: url('data:image/svg+xml;utf8,<svg fill="none" viewBox="0 0 24 24" stroke="gray" stroke-width="3" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"/></svg>') center/contain no-repeat;
  background: #d1d5db;
  opacity: 0.7;
  transition: background 0.2s, opacity 0.2s;
}

.custom-checkbox input:checked+.checkmark::after {
  background: #fff;
  opacity: 1;
}

.scrollbar-hide {
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.scrollbar-hide::-webkit-scrollbar {
  display: none;
}

.btn-outline-blue {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  background: #e6f0fa;
  color: #1976d2;
  font-weight: 600;
  border-radius: 9999px;
  padding: 0.5rem 1.25rem;
  font-size: 0.95rem;
  border: none;
  outline: none;
  box-shadow: none;
  transition: none;
  cursor: pointer;
  text-decoration: none;
}

.btn-outline-blue:active,
.btn-outline-blue:focus,
.btn-outline-blue:hover {
  background: #e6f0fa;
  color: #1976d2;
  text-decoration: none;
  box-shadow: none;
}
</style>