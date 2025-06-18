<template>
  <main
    v-if="hotel"
    class="max-w-full mx-auto px-4 sm:px-6 lg:px-8 py-8"
    style="padding-top: 0"
  >
    <div
      class="sticky top-16 z-40 w-full rounded-lg border border-gray-200 bg-white shadow-lg p-3 mb-4 mt-4"
    >
      <div
        class="flex flex-col md:flex-row items-stretch h-auto md:h-auto border border-gray-300 rounded-lg"
      >
        <div
          ref="locationContainer"
          class="relative flex flex-grow cursor-pointer items-center p-3 bg-white hover:bg-gray-50 border-b md:border-b-0 md:border-r border-gray-200 min-w-[200px] rounded-t-md md:rounded-l-md md:rounded-tr-none"
        >
          <i class="fas fa-map-marker-alt text-blue-500 text-xl pr-3"></i>
          <div class="flex-1">
            <label class="text-xs text-gray-500">Địa điểm hoặc khách sạn</label>
            <input
              type="text"
              v-model="searchParams.location"
              @focus="handleLocationFocus"
              class="w-full bg-transparent font-semibold focus:outline-none text-gray-800"
              placeholder="Tìm kiếm..."
            />
          </div>
          <ul
            v-if="showLocationDropdown"
            class="absolute top-full mt-2 left-0 z-20 w-full bg-white border border-gray-300 rounded-lg shadow-lg max-h-56 overflow-y-auto"
          >
            <li
              v-for="loc in filteredLocations"
              :key="loc"
              @click.stop="selectLocation(loc)"
              class="px-4 py-2 hover:bg-blue-100 cursor-pointer truncate"
            >
              {{ loc }}
            </li>
          </ul>
        </div>

        <div
          class="flex flex-grow-[2] items-center p-3 bg-white hover:bg-gray-50 border-b md:border-b-0 md:border-r border-gray-200 min-w-[300px]"
        >
          <i class="fas fa-calendar-alt text-blue-500 text-xl pr-3"></i>
          <div class="flex flex-1 items-center">
            <div class="flex-1">
              <label class="text-xs text-gray-500">Ngày nhận</label>
              <input
                type="date"
                v-model="searchParams.checkin"
                :min="today"
                class="w-full bg-transparent font-semibold focus:outline-none"
              />
            </div>
            <div class="px-4 text-center">
              <div
                v-if="numberOfNights > 0"
                class="text-xs font-semibold text-blue-600 bg-blue-100 rounded-full px-2 py-0.5 whitespace-nowrap"
              >
                {{ numberOfNights }} đêm
              </div>
            </div>
            <div class="flex-1">
              <label class="text-xs text-gray-500">Ngày trả</label>
              <input
                type="date"
                v-model="searchParams.checkout"
                :min="minCheckOut"
                class="w-full bg-transparent font-semibold focus:outline-none"
              />
            </div>
          </div>
        </div>

        <div ref="guestsContainer" class="relative flex-grow">
          <div
            @click="showGuestsDropdown = !showGuestsDropdown"
            class="flex items-center p-3 bg-white hover:bg-gray-50 cursor-pointer h-full max-w-[300px]"
          >
            <i class="fas fa-users text-blue-500 text-xl pr-3"></i>
            <div class="flex-1">
              <label class="text-xs text-gray-500">Khách và Phòng</label>
              <p class="font-semibold truncate text-gray-800">
                {{ guestsDisplay }}
              </p>
            </div>
          </div>
          <div
            v-if="showGuestsDropdown"
            aria-label="Guest and room selection"
            class="absolute top-full mt-2 right-0 z-30 w-[320px] bg-white rounded-lg shadow-lg border border-gray-200 p-4"
            role="listbox"
          >
            <div class="flex items-center gap-3 mb-4">
              <i class="fas fa-user text-lg text-gray-600 w-5 text-center"></i>
              <span class="text-gray-900 font-semibold text-base"
                >Người lớn</span
              >
              <div class="ml-auto flex items-center gap-2">
                <button
                  @click.stop="updateGuests('adults', -1)"
                  :disabled="searchParams.adults <= 1"
                  class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:text-gray-300 disabled:border-gray-200 disabled:bg-gray-50 flex items-center justify-center hover:bg-gray-50"
                  type="button"
                >
                  −
                </button>
                <span
                  class="w-8 h-8 flex items-center justify-center text-gray-900 font-semibold text-base select-none"
                  >{{ searchParams.adults }}</span
                >
                <button
                  @click.stop="updateGuests('adults', 1)"
                  class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                  type="button"
                >
                  +
                </button>
              </div>
            </div>
            <div class="flex items-center gap-3 mb-4">
              <i class="fas fa-child text-lg text-gray-600 w-5 text-center"></i>
              <span class="text-gray-900 font-semibold text-base">Trẻ em</span>
              <div class="ml-auto flex items-center gap-2">
                <button
                  @click.stop="updateGuests('children', -1)"
                  :disabled="searchParams.children <= 0"
                  class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:text-gray-300 disabled:border-gray-200 disabled:bg-gray-50 flex items-center justify-center hover:bg-gray-50"
                  type="button"
                >
                  −
                </button>
                <span
                  class="w-8 h-8 flex items-center justify-center text-gray-900 font-semibold text-base select-none"
                  >{{ searchParams.children }}</span
                >
                <button
                  @click.stop="updateGuests('children', 1)"
                  class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                  type="button"
                >
                  +
                </button>
              </div>
            </div>
            <div class="flex items-center gap-3">
              <i class="fas fa-bed text-lg text-gray-600 w-5 text-center"></i>
              <span class="text-gray-900 font-semibold text-base">Phòng</span>
              <div class="ml-auto flex items-center gap-2">
                <button
                  @click.stop="updateGuests('rooms', -1)"
                  :disabled="searchParams.rooms <= 1"
                  class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 disabled:text-gray-300 disabled:border-gray-200 disabled:bg-gray-50 flex items-center justify-center hover:bg-gray-50"
                  type="button"
                >
                  −
                </button>
                <span
                  class="w-8 h-8 flex items-center justify-center text-gray-900 font-semibold text-base select-none"
                  >{{ searchParams.rooms }}</span
                >
                <button
                  @click.stop="updateGuests('rooms', 1)"
                  class="w-8 h-8 rounded-md bg-white text-blue-600 font-bold border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                  type="button"
                >
                  +
                </button>
              </div>
            </div>
            <p v-if="guestsError" class="text-red-500 text-xs mt-3 text-center">
              {{ guestsError }}
            </p>
          </div>
        </div>

        <button
          aria-label="Search"
          class="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-3 font-bold transition flex items-center justify-center rounded-b-md md:rounded-l-none md:rounded-r-md"
          @click="onSearch"
        >
          <i class="fas fa-search"></i>
        </button>
      </div>
    </div>
    <section class="bg-white rounded-xl shadow p-6 mb-8">
      <div class="flex space-x-3 mb-8 h-[500px]">
        <div
          class="flex-shrink-0 w-8/10 h-full overflow-hidden rounded-lg shadow-md"
        >
          <img
            v-if="hotel.image"
            :alt="hotel.alt"
            :src="hotel.image"
            class="w-full h-full object-cover transition-transform duration-300 hover:scale-105"
          />
        </div>
        <div class="flex flex-col w-2/10 h-full justify-between">
          <div
            v-for="(img, idx) in hotel.additionalImages"
            :key="idx"
            class="h-[160px] overflow-hidden rounded-lg shadow-md"
          >
            <img
              :src="img"
              :alt="`Additional image ${idx + 1}`"
              class="w-full h-full object-cover transition-transform duration-300 hover:scale-105"
            />
          </div>
        </div>
      </div>
      <div class="flex flex-col sm:flex-row items-center justify-between mb-4">
        <div class="flex-1 text-left">
          <h1 class="text-3xl font-extrabold text-gray-900 mb-1">
            {{ hotel.title }}
          </h1>
          <div class="flex items-center space-x-2 text-sm text-gray-600">
            <i class="fas fa-star text-blue-500"></i>
            <span class="text-blue-600 font-semibold">{{ hotel.rating }}</span>
            <span>({{ hotel.reviews }} reviews)</span>
            <span class="mx-2">•</span>
            <span>{{ hotel.location }}</span>
          </div>
        </div>
        <div class="flex items-center gap-4 mt-4 sm:mt-0">
          <div class="text-left">
            <span class="text-gray-500 text-sm block">Giá/phòng/đêm từ</span>
            <div class="text-2xl font-bold text-indigo-600 whitespace-nowrap">
              {{ minRoomPrice }} <span class="text-base font-normal">VND</span>
            </div>
          </div>
          <button
            class="bg-indigo-600 hover:bg-indigo-700 text-white rounded-full px-6 py-2 text-base font-semibold transition focus:outline-none focus:ring-2 focus:ring-indigo-500"
            @click="scrollToRooms"
          >
            Chọn phòng
          </button>
        </div>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-8 mb-10">
        <div>
          <h2 class="text-xl font-bold text-gray-800 mb-4">Tổng quan</h2>
          <ul class="text-sm text-gray-700 space-y-2 mb-4">
            <li class="flex items-center space-x-2">
              <i class="fas fa-home text-blue-500"></i>
              <span class="font-semibold">{{ hotel.details }}</span>
            </li>
            <li class="flex items-center space-x-2">
              <i class="far fa-calendar-alt text-pink-500"></i>
              <span class="font-semibold">Hủy miễn phí trong 48 giờ</span>
            </li>
          </ul>
          <p class="text-sm text-gray-700 leading-relaxed mb-4">
            Sunt ut elit cupidatat do quis incididunt sint mollit culpa
            consequat occaecat exercitati anim ad sint adipisicing nulla:
          </p>
          <ul
            class="text-sm text-gray-700 list-disc list-inside space-y-1 mb-4"
          >
            <li>Sit reprehenderit elit incididunt</li>
            <li>Aute aliqua anim et duis occaecat</li>
            <li v-if="showMoreOverview">
              Proident tempor deserunt laborum nostrud.
            </li>
            <li v-if="showMoreOverview">
              Dolore pariatur exercitation nisi commodo.
            </li>
          </ul>
          <button
            @click="showMoreOverview = !showMoreOverview"
            class="text-blue-600 font-semibold text-sm hover:underline focus:outline-none"
          >
            {{ showMoreOverview ? "Show less" : "Show more" }}
          </button>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-800 mb-4">
            Các tiện ích nổi bật
          </h3>
          <div
            class="grid grid-cols-2 sm:grid-cols-3 gap-y-3 text-sm text-gray-700 mb-4"
          >
            <div
              v-for="(amenity, index) in showAllAmenities
                ? hotel.amenitiesList
                : hotel.amenitiesList.slice(0, 4)"
              :key="index"
              class="flex items-center space-x-2 font-medium"
            >
              <i :class="amenity.icon" class="text-green-500"></i>
              <span>{{ amenity.name }}</span>
            </div>
          </div>
          <button
            @click="showAllAmenities = !showAllAmenities"
            class="mt-2 text-sm border border-gray-300 text-gray-700 rounded-full px-4 py-2 hover:bg-gray-100 focus:outline-none transition-colors duration-200"
          >
            {{ showAllAmenities ? "Show less" : "Show all amenities" }}
          </button>
        </div>
      </div>
    </section>
    <section ref="roomsSectionRef" class="bg-white rounded-xl p-0 pt-0 mb-8">
      <h2 class="text-2xl font-bold text-gray-800 mb-6">
        Những phòng trống tại {{ hotel.title }}
      </h2>
      <section class="bg-white rounded-xl shadow p-6 mb-8">
        <h3 class="text-lg font-bold text-gray-800 pb-4 mb-2">
          Tìm kiếm nhanh hơn bằng cách chọn những tiện nghi bạn cần
        </h3>
        <div class="flex flex-wrap gap-4">
          <div
            v-for="filter in amenityFilters"
            :key="filter.id"
            class="flex items-center"
          >
            <input
              :id="`filter-${filter.id}`"
              type="checkbox"
              :value="filter.id"
              v-model="selectedAmenities"
              class="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
            />
            <label
              :for="`filter-${filter.id}`"
              class="ml-2 block text-sm text-gray-900"
            >
              {{ filter.label }}
            </label>
          </div>
        </div>
      </section>
      <div
        v-for="(room, idx) in filteredRoomTypes"
        :key="idx"
        class="flex flex-col md:flex-row gap-6 border border-gray-200 rounded-lg p-5 mb-6"
      >
        <div class="relative flex flex-col gap-4 md:w-1/3">
          <span
            :class="[
              'inline-block font-bold rounded-full px-3 py-1 text-sm self-start',
              room.labelClass,
            ]"
          >
            {{ room.label }}
          </span>
          <div
            class="relative w-full h-48 overflow-hidden rounded-lg shadow-md"
          >
            <transition
              :name="slideDirection === 'next' ? 'slide-right' : 'slide-left'"
            >
              <img
                v-if="room.images && room.images.length"
                :key="roomImageIndex[room.id]"
                :src="room.images[roomImageIndex[room.id]]"
                :alt="`${room.label} Image ${roomImageIndex[room.id] + 1}`"
                class="w-full h-full object-cover absolute inset-0"
              />
            </transition>
            <button
              @click="prevRoomImage(room.id)"
              class="absolute left-2 top-1/2 transform -translate-y-1/2 bg-white/40 rounded-full p-2 hover:bg-white/45 z-10"
            >
              <i class="fas fa-chevron-left text-gray-700"></i>
            </button>
            <button
              @click="nextRoomImage(room.id)"
              class="absolute right-2 top-1/2 transform -translate-y-1/2 bg-white/40 rounded-full p-2 hover:bg-white/45 z-10"
            >
              <i class="fas fa-chevron-right text-gray-700"></i>
            </button>
          </div>
          <div
            class="flex flex-col gap-2 text-sm text-gray-700 font-semibold mt-2"
          >
            <div class="flex items-center gap-2">
              <i class="fas fa-ruler-combined" :class="room.iconColor"></i>
              <span>{{ room.size }}</span>
            </div>
            <div class="flex items-center gap-2">
              <i class="fas fa-smoking-ban text-gray-500"></i>
              <span>Không hút thuốc</span>
            </div>
            <div
              class="flex flex-wrap gap-x-6 gap-y-2 text-xs text-gray-500 font-normal mt-2"
            >
              <div
                v-for="(fac, i) in room.features"
                :key="i"
                class="flex items-center gap-1"
              >
                <i :class="fac.icon"></i>
                <span>{{ fac.name }}</span>
              </div>
            </div>
          </div>
          <button
            @click="openModal(room)"
            class="font-bold text-sm flex items-center gap-1 hover:underline mt-2 text-gray-700"
          >
            <i class="fas fa-info-circle"></i> Xem chi tiết phòng
          </button>
        </div>
        <div
          class="md:w-2/3 border border-gray-100 rounded-lg overflow-hidden shadow-sm"
        >
          <table class="w-full text-sm text-gray-700 border-collapse">
            <thead class="bg-gray-50 text-gray-800 font-bold">
              <tr>
                <th class="text-left py-3 px-4 border-b border-gray-200 w-2/5">
                  Lựa chọn phòng
                </th>
                <th
                  class="text-center py-3 px-4 border-b border-gray-200 w-1/5"
                >
                  Khách
                </th>
                <th
                  class="text-center py-3 px-4 border-b border-gray-200 w-2/5"
                >
                  Giá/phòng/đêm
                </th>
                <th class="w-16 border-b border-gray-200"></th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(variant, vIdx) in room.variants"
                :key="vIdx"
                class="border-b border-gray-100 last:border-b-0"
              >
                <td class="py-10 px-4 align-top">
                  <div class="text-xs text-gray-500 mb-1">
                    {{ room.type }} - {{ room.rate }}
                  </div>
                  <div class="font-bold text-sm mb-1">
                    {{ variant.breakfast }}
                  </div>
                  <div
                    class="flex items-center gap-1 text-gray-500 text-xs mb-1"
                  >
                    <i class="fas fa-bed"></i>
                    <span>{{ room.bed }}</span>
                  </div>
                  <div
                    class="flex items-center gap-1 text-green-600 text-xs mt-2"
                  >
                    <i class="fas fa-check-circle"></i>
                    <span>Áp dụng chính sách hủy phòng</span>
                    <i
                      class="fas fa-info-circle text-gray-400"
                      title="Thông tin chính sách hủy phòng"
                    ></i>
                  </div>
                </td>
                <td
                  class="text-center align-top py-10 px-4 text-xl text-gray-600"
                >
                  <i class="fas fa-user-friends"></i>
                </td>
                <td class="text-center align-top py-10 px-4">
                  <div class="mb-1">
                    <span
                      :class="[
                        'inline-block text-white text-xs rounded-full px-2.5 py-0.5',
                        room.tagClass,
                      ]"
                    >
                      {{ room.tag }}
                    </span>
                  </div>
                  <div class="line-through text-xs text-gray-500 mt-1">
                    {{ variant.originalPrice }} VND
                  </div>
                  <div class="text-red-600 font-bold text-base mt-1">
                    {{ variant.discountedPrice }} VND
                  </div>
                  <div class="text-xs text-gray-500 mt-0.5">
                    Chưa bao gồm thuế và phí
                  </div>
                </td>
                <td class="text-center align-middle py-10 px-4">
                  <button
                    @click="goToBooking(room, variant)"
                    :class="[
                      'rounded-lg px-5 py-2 text-sm font-semibold transition-colors duration-200 shadow-md',
                      room.buttonClass,
                    ]"
                  >
                    Chọn
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div v-if="!filteredRoomTypes.length" class="text-center py-10">
        <p class="text-xl text-gray-700">
          Không tìm thấy phòng nào phù hợp với lựa chọn của bạn.
        </p>
      </div>
    </section>
    <section class="w-full bg-[#d9f0ff] rounded-lg p-6 shadow-sm mb-8">
      <div
        class="flex flex-col md:flex-row md:justify-between md:items-start gap-4"
      >
        <div class="flex-1">
          <h2
            class="font-sans font-extrabold text-xl leading-6 mb-1 text-[#0a0a0a]"
          >
            Cơ sở lưu trú khác bạn có thể thích
          </h2>
          <p class="font-sans text-sm text-[#1a1a1a] opacity-90">
            Cơ sở lưu trú tương tự đã được khách chọn
          </p>
        </div>
        <div>
          <div
            class="bg-white rounded-lg p-3 w-64 text-xs font-sans text-[#1a1a1a] shadow-md cursor-pointer select-none"
          >
            <div class="mb-1">Hiển thị giá</div>
            <button
              aria-expanded="false"
              aria-haspopup="listbox"
              class="text-[#0071c2] font-semibold flex items-center gap-1"
            >
              Tổng giá (bao gồm thuế và phí)
              <svg
                class="w-3 h-3"
                fill="none"
                stroke="#0071c2"
                stroke-width="2"
                viewbox="0 0 24 24"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M19 9l-7 7-7-7"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                ></path>
              </svg>
            </button>
          </div>
        </div>
      </div>
      <div
        aria-label="Accommodation suggestions"
        class="mt-6 flex gap-4 overflow-x-auto no-scrollbar pb-2"
        role="list"
      >
        <article
          v-for="other in otherHotels"
          :key="other.id"
          class="bg-white rounded-lg shadow-sm min-w-[220px] max-w-[220px] flex-shrink-0 cursor-pointer hover:shadow-md transition"
          role="listitem"
          @click="
            router.push({ name: 'HotelDetail', params: { id: other.id } })
          "
        >
          <div class="relative">
            <img
              :alt="other.alt || other.title"
              class="w-full h-[160px] object-cover rounded-t-lg"
              height="160"
              loading="lazy"
              :src="other.image"
              width="220"
            />
            <div
              class="absolute top-2 left-2 bg-red-600 text-white text-[10px] font-semibold px-2 py-[2px] rounded-sm select-none"
              v-if="other.rating >= 4"
            >
              4-5 sao giá tốt
            </div>
          </div>
          <div class="p-3">
            <h3
              class="font-sans font-bold text-sm leading-5 mb-0.5 text-[#0a0a0a]"
            >
              {{ other.title }}
            </h3>
            <p class="font-sans text-xs text-[#1a1a1a] mb-2">
              {{ other.details }}
            </p>
            <div class="flex items-center gap-1 mb-1">
              <div class="text-yellow-400 text-xs leading-none">
                <i
                  class="fas fa-star"
                  v-for="n in Math.round(other.rating)"
                  :key="n"
                ></i>
              </div>
              <span
                class="text-[#0071c2] text-xs font-semibold leading-none flex items-center gap-0.5"
              >
                {{ other.rating }}
              </span>
              <span class="text-xs text-gray-500">/ 10</span>
              <span class="text-xs text-gray-500">({{ other.reviews }})</span>
            </div>
            <div class="flex items-center gap-1 text-xs text-gray-500 mb-3">
              <i class="fas fa-map-marker-alt text-xs"></i>
              <span>{{ other.location }}</span>
            </div>
            <div
              class="text-xs text-gray-400 line-through select-none"
              v-if="other.originalPrice"
            >
              {{ other.originalPrice }} VND
            </div>
            <div class="text-sm font-semibold text-[#d94a1a] select-none">
              {{ other.price }} VND
            </div>
            <div class="text-[9px] text-gray-600 mt-0.5 select-none">
              Bao gồm thuế và phí
            </div>
          </div>
        </article>
      </div>
    </section>
    <section class="bg-white-50 rounded-xl shadow-inner p-6 md:p-8">
      <div class="flex items-center gap-4 mb-8">
        <h2 class="text-2xl font-bold text-gray-800">
          {{ hotel.reviews }} đánh giá
        </h2>
      </div>
      <div class="space-y-6">
        <div
          v-for="(review, index) in paginatedReviews"
          :key="index"
          class="bg-white p-6 rounded-lg border border-gray-200 shadow-sm hover:shadow-lg hover:border-purple-300 transition-all duration-300 ease-in-out"
        >
          <div class="flex items-start gap-6">
            <div class="flex-shrink-0 w-40">
              <div class="flex items-center gap-4">
                <img
                  :alt="review.name"
                  class="w-12 h-12 rounded-full object-cover"
                  :src="review.avatar"
                />
                <div>
                  <h3 class="text-base font-bold text-gray-900">
                    {{ review.name }}
                  </h3>
                  <p class="text-sm text-gray-500 mt-0.5">
                    <span
                      class="hover:underline cursor-pointer"
                      title="Đã đánh giá vào ngày này"
                      >{{ review.date }}</span
                    >
                  </p>
                </div>
              </div>
            </div>
            <div class="flex-grow">
              <div class="flex items-center justify-between mb-2">
                <div
                  class="flex items-center gap-1.5 bg-blue-100 text-blue-800 font-bold text-sm px-2.5 py-1 rounded-full"
                >
                  <i class="fas fa-star text-xs"></i>
                  <span>{{ review.rating }}</span>
                </div>
              </div>
              <p class="text-gray-700 leading-relaxed">
                {{ review.comment }}
              </p>
              <div
                class="mt-5 pt-4 border-t border-gray-100 flex flex-wrap items-center gap-3"
              >
                <p class="text-sm font-medium text-gray-600">
                  Đánh giá này có hữu ích không?
                </p>
                <div class="flex items-center gap-2">
                  <button
                    class="flex items-center gap-1.5 rounded-full bg-gray-100 hover:bg-green-100 text-gray-700 hover:text-green-800 px-3 py-1 text-xs font-semibold transition-colors"
                  >
                    <i class="fas fa-thumbs-up"></i>
                    <span>Có</span>
                  </button>
                  <button
                    class="flex items-center gap-1.5 rounded-full bg-gray-100 hover:bg-red-100 text-gray-700 hover:text-red-800 px-3 py-1 text-xs font-semibold transition-colors"
                  >
                    <i class="fas fa-thumbs-down"></i>
                    <span>Không</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div
        class="mt-8 flex flex-col sm:flex-row justify-between items-center gap-4"
      >
        <div class="flex items-center gap-2">
          <label
            for="reviews-per-page"
            class="text-sm font-medium text-gray-700"
            >Hiển thị:</label
          >
          <select
            id="reviews-per-page"
            v-model="reviewsPerPage"
            class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block p-1.5"
          >
            <option
              v-for="option in reviewsPerPageOptions"
              :key="option.value"
              :value="option.value"
            >
              {{ option.text }}
            </option>
          </select>
        </div>
        <div class="flex items-center gap-4">
          <span class="text-sm font-medium text-gray-700">
            Trang {{ currentPage }} / {{ totalPages }}
          </span>
          <div class="flex items-center gap-2">
            <button
              @click="prevPage"
              :disabled="currentPage === 1"
              class="inline-flex items-center justify-center h-8 w-8 rounded-full border bg-white text-gray-600 hover:bg-gray-100 disabled:bg-gray-50 disabled:text-gray-300 disabled:cursor-not-allowed transition-colors"
            >
              <i class="fas fa-chevron-left text-xs"></i>
            </button>
            <button
              @click="nextPage"
              :disabled="currentPage === totalPages"
              class="inline-flex items-center justify-center h-8 w-8 rounded-full border bg-white text-gray-600 hover:bg-gray-100 disabled:bg-gray-50 disabled:text-gray-300 disabled:cursor-not-allowed transition-colors"
            >
              <i class="fas fa-chevron-right text-xs"></i>
            </button>
          </div>
        </div>
      </div>
    </section>
  </main>
  <div v-else class="text-center py-10">
    <p class="text-xl text-gray-700">Hotel not found.</p>
  </div>
  <div
    v-if="showModal"
    class="fixed inset-0 z-50 flex items-center justify-center pt-15"
    style="backdrop-filter: blur(6px); background-color: rgba(0, 0, 0, 0.15)"
    @click.self="closeModal"
  >
    <div
      class="bg-white rounded-xl shadow-xl max-w-3/5 w-full mx-4 lg:mx-0 overflow-hidden relative flex flex-col md:flex-row"
      style="max-height: 80vh"
    >
      <button
        @click="closeModal"
        class="absolute top-4 right-4 text-gray-500 hover:text-gray-800 z-10"
      >
        <i class="fas fa-times fa-lg"></i>
      </button>
      <div class="md:w-1/2 w-full flex flex-col bg-gray-50">
        <div class="flex-grow flex items-center justify-center p-4">
          <img
            v-if="selectedRoom.images && selectedRoom.images.length"
            :src="selectedRoom.images[modalImageIndex]"
            :alt="`${selectedRoom.label} Room`"
            class="object-contain max-h-full w-full rounded-lg transition-all duration-200"
          />
        </div>
        <div class="flex gap-2 justify-center p-2 bg-white">
          <img
            v-for="(img, idx) in selectedRoom.images"
            :key="idx"
            :src="img"
            :alt="`Ảnh ${idx + 1}`"
            class="w-16 h-16 object-cover rounded cursor-pointer border-2"
            :class="
              modalImageIndex === idx ? 'border-blue-500' : 'border-transparent'
            "
            @click="modalImageIndex = idx"
          />
        </div>
      </div>
      <div class="md:w-1/2 w-full p-6 overflow-y-auto" style="max-height: 80vh">
        <h2 class="text-2xl font-bold text-gray-800 mb-4">
          {{ selectedRoom.label }} – {{ selectedRoom.type }}
        </h2>
        <div class="mb-6 text-sm text-gray-700">
          <p class="flex items-center mb-2">
            <i class="fas fa-ruler-combined text-blue-500 mr-2"></i>
            Diện tích:
            <span class="font-semibold ml-1">{{ selectedRoom.size }}</span>
          </p>
          <p class="flex items-center">
            <i class="fas fa-user-friends text-blue-500 mr-2"></i>
            Sức chứa:
            <span class="font-semibold ml-1"
              >{{ selectedRoom.maxAdults }} người lớn</span
            >,
            <span class="font-semibold ml-1"
              >{{ selectedRoom.maxChildren }} trẻ em</span
            >
          </p>
        </div>
        <div class="mb-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-2">Tiện ích:</h3>
          <div class="flex flex-wrap gap-2">
            <div
              v-for="(fac, i) in selectedRoom.features"
              :key="i"
              class="flex items-center space-x-2 bg-gray-100 rounded-full px-3 py-1 text-xs text-gray-700 mb-2"
            >
              <i :class="fac.icon" class="text-green-600"></i>
              <span>{{ fac.name }}</span>
            </div>
          </div>
        </div>
        <div>
          <h3 class="text-lg font-semibold text-gray-800 mb-2">
            Các lựa chọn dịch vụ & giá:
          </h3>
          <table class="w-full text-sm text-gray-700 border-collapse">
            <thead class="bg-gray-50 text-gray-800 font-semibold">
              <tr>
                <th class="text-left py-2 px-4 border-b border-gray-200">
                  Phiên bản
                </th>
                <th class="text-center py-2 px-4 border-b border-gray-200">
                  Giá gốc
                </th>
                <th class="text-center py-2 px-4 border-b border-gray-200">
                  Giá KM
                </th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(variant, idx) in selectedRoom.variants"
                :key="idx"
                class="border-b border-gray-100 last:border-b-0"
              >
                <td class="py-3 px-4">
                  <div class="font-semibold">{{ variant.breakfast }}</div>
                  <div class="text-xs text-gray-500 mt-1">
                    <span v-if="variant.hasBreakfast">Có bữa sáng</span>
                    <span v-else>Không bữa sáng</span>
                    <span class="mx-1">•</span>
                    <span v-if="variant.payAtHotel"
                      >Thanh toán tại khách sạn</span
                    >
                    <span v-else>Thanh toán trước</span>
                    <span class="mx-1">•</span>
                    <span v-if="variant.cancellable">Hủy miễn phí</span>
                    <span v-else>Không hủy</span>
                  </div>
                </td>
                <td class="text-center py-3 px-4 line-through text-gray-500">
                  {{ variant.originalPrice }} VND
                </td>
                <td class="text-center py-3 px-4 text-red-600 font-bold">
                  {{ variant.discountedPrice }} VND
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, computed } from "vue";
import { hotels } from "@/data/hotelData.js";
import { useRouter, useRoute } from "vue-router";

const locationContainer = ref(null);
const guestsContainer = ref(null);
const showLocationDropdown = ref(false);
const showGuestsDropdown = ref(false);
const guestsError = ref("");
const errorTimeout = ref(null);

const today = new Date().toISOString().split("T")[0];
const locations = [
  "Hồ Chí Minh",
  "Hà Nội",
  "Đà Nẵng",
  "Nha Trang",
  "Cần Thơ",
  "Vũng Tàu",
  "Huế",
  "Hội An",
  "Phú Quốc",
  "Đà Lạt",
];

const searchParams = ref({
  location: "",
  checkin: "",
  checkout: "",
  adults: 2,
  children: 0,
  rooms: 1,
});

const lastLocation = ref("");

const guestsDisplay = computed(() => {
  return `${searchParams.value.adults} người lớn, ${searchParams.value.children} trẻ em, ${searchParams.value.rooms} phòng`;
});

const filteredLocations = computed(() => {
  if (!searchParams.value.location) return locations;
  return locations.filter((location) =>
    location.toLowerCase().includes(searchParams.value.location.toLowerCase())
  );
});

const minCheckOut = computed(() => {
  if (searchParams.value.checkin) {
    const d = new Date(searchParams.value.checkin);
    d.setDate(d.getDate() + 1);
    return d.toISOString().split("T")[0];
  }
  return today;
});

const numberOfNights = computed(() => {
  if (searchParams.value.checkin && searchParams.value.checkout) {
    const s = new Date(searchParams.value.checkin);
    const e = new Date(searchParams.value.checkout);
    if (e <= s) return 0;
    const diff = e - s;
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
  }
  return 0;
});

watch(
  () => searchParams.value.checkin,
  (nv) => {
    const s = new Date(nv);
    const e = new Date(searchParams.value.checkout);
    if (e <= s) {
      const ne = new Date(s);
      ne.setDate(ne.getDate() + 1);
      searchParams.value.checkout = ne.toISOString().split("T")[0];
    }
  }
);

function handleLocationFocus() {
  lastLocation.value = searchParams.value.location;
  searchParams.value.location = "";
  showLocationDropdown.value = true;
}

function selectLocation(loc) {
  searchParams.value.location = loc;
  lastLocation.value = loc;
  showLocationDropdown.value = false;
}

function updateGuests(type, amount) {
  const params = searchParams.value;
  if (errorTimeout.value) clearTimeout(errorTimeout.value);
  guestsError.value = "";

  if (type === "adults") {
    const newAdults = params.adults + amount;
    if (newAdults >= 1) {
      params.adults = newAdults;
      if (params.adults < params.rooms) {
        params.rooms = params.adults;
      }
    }
  } else if (type === "children") {
    params.children = Math.max(0, params.children + amount);
  } else if (type === "rooms") {
    const newRooms = params.rooms + amount;
    if (amount > 0) {
      if (newRooms > params.adults) {
        guestsError.value = "Số phòng không thể nhiều hơn số người lớn.";
        errorTimeout.value = setTimeout(() => {
          guestsError.value = "";
        }, 3000);
      } else {
        params.rooms = newRooms;
      }
    } else {
      params.rooms = Math.max(1, newRooms);
    }
  }
}

function onSearch() {
  if (searchParams.value.location) {
    lastLocation.value = searchParams.value.location;
  } else {
    searchParams.value.location = lastLocation.value;
  }
  router.push({
    name: "HotelListing",
    query: { ...searchParams.value, page: 1 },
  });
}

const handleClickOutside = (event) => {
  if (
    locationContainer.value &&
    !locationContainer.value.contains(event.target)
  ) {
    showLocationDropdown.value = false;
    if (searchParams.value.location === "") {
      searchParams.value.location = lastLocation.value;
    }
  }
  if (guestsContainer.value && !guestsContainer.value.contains(event.target)) {
    showGuestsDropdown.value = false;
  }
};

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});

const roomsSectionRef = ref(null);

const minRoomPrice = computed(() => {
  const roomsToPrice =
    filteredRoomTypes.value.length > 0
      ? filteredRoomTypes.value
      : roomTypes.value;
  if (!roomsToPrice.length) return "";
  let min = Number.POSITIVE_INFINITY;
  roomsToPrice.forEach((room) => {
    room.variants.forEach((variant) => {
      const price = parseFloat(
        (variant.discountedPrice + "").replace(/\./g, "").replace(/,/g, "")
      );
      if (!isNaN(price) && price < min) min = price;
    });
  });
  return min !== Number.POSITIVE_INFINITY ? min.toLocaleString("vi-VN") : "";
});

const scrollToRooms = () => {
  if (roomsSectionRef.value) {
    roomsSectionRef.value.scrollIntoView({ behavior: "smooth" });
  }
};

const showMoreOverview = ref(false);
const showAllAmenities = ref(false);
const showModal = ref(false);
const selectedRoom = ref(null);
const roomImageIndex = ref({});

const amenityFilters = ref([
  { id: "freeCancellation", label: "Miễn phí hủy phòng" },
  { id: "freeBreakfast", label: "Miễn phí bữa sáng" },
  { id: "payAtHotel", label: "Thanh toán tại khách sạn" },
  { id: "noBreakfast", label: "Không bao gồm bữa sáng" },
]);
const selectedAmenities = ref([]);

const filteredRoomTypes = computed(() => {
  if (selectedAmenities.value.length === 0) {
    return roomTypes.value;
  }
  return roomTypes.value.filter((room) => {
    return selectedAmenities.value.every((amenity) => {
      return room.variants.some((variant) => {
        if (amenity === "freeCancellation") return variant.cancellable;
        if (amenity === "freeBreakfast") return variant.hasBreakfast;
        if (amenity === "payAtHotel") return variant.payAtHotel;
        if (amenity === "noBreakfast") return !variant.hasBreakfast;
        return false;
      });
    });
  });
});

const reviewsList = ref([
  {
    name: "John Smith",
    avatar: "https://randomuser.me/api/portraits/men/32.jpg",
    rating: 4.5,
    date: "Tháng 6, 2025",
    comment:
      "Nơi tuyệt vời, dịch vụ xuất sắc và rất sạch sẽ. Rất khuyến khích!",
  },
  {
    name: "Emily Johnson",
    avatar: "https://randomuser.me/api/portraits/women/45.jpg",
    rating: 5,
    date: "Tháng 5, 2025",
    comment: "Vị trí hoàn hảo và chủ nhà siêu nhiệt tình!",
  },
  {
    name: "Michael Brown",
    avatar: "https://randomuser.me/api/portraits/men/28.jpg",
    rating: 4,
    date: "Tháng 5, 2025",
    comment: "Phòng đẹp, hơi ồn ào một chút nhưng có thể chấp nhận được.",
  },
  {
    name: "Sarah Davis",
    avatar: "https://randomuser.me/api/portraits/women/55.jpg",
    rating: 4.5,
    date: "Tháng 4, 2025",
    comment: "Giường rất thoải mái và tiện nghi tuyệt vời.",
  },
  {
    name: "Tom Wilson",
    avatar: "https://randomuser.me/api/portraits/men/52.jpg",
    rating: 3.5,
    date: "Tháng 4, 2025",
    comment: "Có thể cải thiện về độ sạch sẽ, nhưng nhìn chung là ổn.",
  },
  {
    name: "Alex Martin",
    avatar: "https://randomuser.me/api/portraits/men/45.jpg",
    rating: 4,
    date: "Tháng 3, 2025",
    comment: "Giá trị tốt so với số tiền bỏ ra và nhân viên thân thiện.",
  },
  {
    name: "Jessica Lee",
    avatar: "https://randomuser.me/api/portraits/women/65.jpg",
    rating: 5,
    date: "Tháng 3, 2025",
    comment: "Chắc chắn sẽ quay lại! Một trong những nơi tốt nhất tôi từng ở.",
  },
  {
    name: "David Chen",
    avatar: "https://randomuser.me/api/portraits/men/75.jpg",
    rating: 4,
    date: "Tháng 2, 2025",
    comment: "Mọi thứ đều tốt. Wifi hơi chậm vào buổi tối.",
  },
]);

const currentPage = ref(1);
const reviewsPerPage = ref(5);
const reviewsPerPageOptions = ref([
  { value: 5, text: "5" },
  { value: 10, text: "10" },
  { value: reviewsList.value.length, text: "Tất cả" },
]);

const totalPages = computed(() => {
  return Math.ceil(reviewsList.value.length / reviewsPerPage.value);
});

const paginatedReviews = computed(() => {
  const start = (currentPage.value - 1) * reviewsPerPage.value;
  const end = start + reviewsPerPage.value;
  return reviewsList.value.slice(start, end);
});

const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++;
};
const prevPage = () => {
  if (currentPage.value > 1) currentPage.value--;
};
watch(reviewsPerPage, () => {
  currentPage.value = 1;
});

const props = defineProps({
  id: { type: [String, Number], required: true },
});

const otherHotels = computed(() => {
  return hotels.filter((h) => h.id !== hotel.value?.id).slice(0, 6);
});

const hotel = ref(null);
const roomTypes = ref([]);
const initializeRoomIndices = () => {
  roomTypes.value.forEach((room) => {
    roomImageIndex.value[room.id] = 0;
  });
};

const modalImageIndex = ref(0);
watch(selectedRoom, (room) => {
  if (room && room.images && room.images.length) modalImageIndex.value = 0;
});

const findHotel = () => {
  const foundHotel = hotels.find((h) => h.id === parseInt(props.id));
  if (foundHotel) {
    hotel.value = {
      ...foundHotel,
      details: "6 khách • 4 giường • 1 phòng tắm riêng",
      amenitiesList: [
        { name: "Nhà bếp", icon: "fas fa-utensils" },
        { name: "Bộ sơ cứu", icon: "fas fa-first-aid" },
        { name: "TV", icon: "fas fa-tv" },
        { name: "Đồ uống miễn phí", icon: "fas fa-wine-glass-alt" },
        { name: "Điều hòa", icon: "fas fa-snowflake" },
        { name: "Thuê xe đạp", icon: "fas fa-bicycle" },
        { name: "Wifi", icon: "fas fa-wifi" },
        { name: "Hệ thống sưởi", icon: "fas fa-thermometer-half" },
      ],
      additionalImages: [
        "https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/10027533-8936a739daf46bb17c623254e65eadfb.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666",
        "https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027533-73d20c6f7dc45a59568b54c2931f4bcb.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666",
        "https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027533-a1b9e5ddc7b8cc55108aee73bbfee7c3.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666",
      ],
      price: "273000",
    };
    roomTypes.value = [
      {
        id: 1,
        label: "Tiêu chuẩn",
        labelClass: "bg-blue-100 text-blue-800",
        iconColor: "text-blue-500",
        linkColor: "text-blue-600",
        images: [
          "https://storage.googleapis.com/a1aa/image/344db20c-d038-4d9b-a473-fefd7c443548.jpg",
          "https://storage.googleapis.com/a1aa/image/68c6fc18-96ef-43c2-3bae-b96409260526.jpg",
        ],
        size: "17.0 m²",
        maxAdults: 2,
        maxChildren: 1,
        features: [
          { name: "Vòi tắm đứng", icon: "fas fa-shower" },
          { name: "Tủ lạnh", icon: "fas fa-snowflake" },
          { name: "Khu vực chờ", icon: "fas fa-chair" },
          { name: "Máy lạnh", icon: "fas fa-fan" },
        ],
        type: "Standard Room",
        rate: "Standard Rate",
        bed: "1 giường cỡ king",
        tag: "Vị trí tốt",
        tagClass: "bg-blue-500",
        buttonClass: "bg-blue-600 text-white hover:bg-blue-700",
        variants: [
          {
            breakfast: "Không bao gồm bữa sáng",
            hasBreakfast: false,
            cancellable: false,
            payAtHotel: false,
            originalPrice: "933.217",
            discountedPrice: "699.913",
          },
          {
            breakfast: "Bao gồm bữa sáng",
            hasBreakfast: true,
            cancellable: true,
            payAtHotel: true,
            originalPrice: "1.033.217",
            discountedPrice: "799.913",
          },
        ],
      },
      {
        id: 2,
        label: "Cao cấp",
        labelClass: "bg-green-100 text-green-800",
        iconColor: "text-green-500",
        linkColor: "text-green-600",
        images: [
          "https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/67778274-8c40a3ba78c6007e2e18af54a57143db.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320",
          "https://storage.googleapis.com/a1aa/image/0216c56f-c55e-4854-998f-8a6098451dd3.jpg",
        ],
        size: "20.0 m²",
        maxAdults: 2,
        maxChildren: 2,
        features: [
          { name: "Bồn tắm", icon: "fas fa-bath" },
          { name: "Wifi miễn phí", icon: "fas fa-wifi" },
          { name: "TV màn hình phẳng", icon: "fas fa-tv" },
          { name: "Máy lạnh", icon: "fas fa-fan" },
        ],
        type: "Deluxe Room",
        rate: "Deluxe Rate",
        bed: "1 giường cỡ queen",
        tag: "Phổ biến",
        tagClass: "bg-green-500",
        buttonClass: "bg-green-600 text-white hover:bg-green-700",
        variants: [
          {
            breakfast: "Không bao gồm bữa sáng",
            hasBreakfast: false,
            cancellable: false,
            payAtHotel: false,
            originalPrice: "1.200.000",
            discountedPrice: "950.000",
          },
          {
            breakfast: "Bao gồm bữa sáng",
            hasBreakfast: true,
            cancellable: true,
            payAtHotel: false,
            originalPrice: "1.400.000",
            discountedPrice: "1.100.000",
          },
        ],
      },
      {
        id: 3,
        label: "Thượng hạng",
        labelClass: "bg-red-100 text-red-800",
        iconColor: "text-red-500",
        linkColor: "text-red-600",
        images: [
          "https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/67778274-d22561c06b9bcec727af7a5fb1378627.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320",
          "https://storage.googleapis.com/a1aa/image/27a56a3d-1d5e-4fa7-720b-0fe8b04c4867.jpg",
        ],
        size: "22.0 m²",
        maxAdults: 3,
        maxChildren: 1,
        features: [
          { name: "Bar mini", icon: "fas fa-wine-glass-alt" },
          { name: "Sofa", icon: "fas fa-sofa" },
          { name: "TV màn hình phẳng", icon: "fas fa-tv" },
          { name: "Máy lạnh", icon: "fas fa-fan" },
        ],
        type: "Superior Room",
        rate: "Superior Rate",
        bed: "1 giường cỡ king",
        tag: "Ưu đãi đặc biệt",
        tagClass: "bg-red-500",
        buttonClass: "bg-red-600 text-white hover:bg-red-700",
        variants: [
          {
            breakfast: "Không bao gồm bữa sáng",
            hasBreakfast: false,
            cancellable: false,
            payAtHotel: false,
            originalPrice: "1.500.000",
            discountedPrice: "1.100.000",
          },
          {
            breakfast: "Bao gồm bữa sáng + minibar",
            hasBreakfast: true,
            cancellable: true,
            payAtHotel: true,
            originalPrice: "1.700.000",
            discountedPrice: "1.300.000",
          },
        ],
      },
    ];

    const q = route.query;
    searchParams.value.location = foundHotel.title;
    searchParams.value.adults = Number(q.adults) || 2;
    searchParams.value.children = Number(q.children) || 0;
    searchParams.value.rooms = Number(q.rooms) || 1;
    searchParams.value.checkin = q.checkin || today;

    const checkinDate = new Date(searchParams.value.checkin);
    const checkoutDate = q.checkout ? new Date(q.checkout) : null;

    if (checkoutDate && checkoutDate > checkinDate) {
      searchParams.value.checkout = q.checkout;
    } else {
      searchParams.value.checkout = minCheckOut.value;
    }
    lastLocation.value = foundHotel.title;

    initializeRoomIndices();
  } else {
    hotel.value = null;
  }
};

const router = useRouter();
const route = useRoute();

watch(() => props.id, findHotel, { immediate: true });

const goToBooking = (room, variant) => {
  const hotelId = props.id;
  const query = {
    hotelTitle: hotel.value.title,
    hotellocation: hotel.value.location,
    hotelRating: hotel.value.rating,
    hotelReviews: hotel.value.reviews,
    hotelImage: hotel.value.image,
    hotelDetails: hotel.value.details,
    roomLabel: room.label,
    roomType: room.type,
    roomRate: room.rate,
    roomBed: room.bed,
    variantBreakfast: variant.breakfast,
    variantOriginalPrice: variant.originalPrice,
    variantDiscountedPrice: variant.discountedPrice,
    checkin: searchParams.value.checkin,
    checkout: searchParams.value.checkout,
    adults: searchParams.value.adults,
    children: searchParams.value.children,
    rooms: searchParams.value.rooms,
  };
  router.push({
    name: "HotelBooking",
    params: { id: hotelId },
    query,
  });
};

const openModal = (room) => {
  selectedRoom.value = JSON.parse(JSON.stringify(room));
  showModal.value = true;
};
const closeModal = () => {
  showModal.value = false;
  selectedRoom.value = null;
};

const slideDirection = ref("next");

const nextRoomImage = (roomId) => {
  slideDirection.value = "next";
  const room = roomTypes.value.find((r) => r.id === roomId);
  if (!room || !room.images) return;
  const count = room.images.length;
  roomImageIndex.value[roomId] = (roomImageIndex.value[roomId] + 1) % count;
};

const prevRoomImage = (roomId) => {
  slideDirection.value = "prev";
  const room = roomTypes.value.find((r) => r.id === roomId);
  if (!room || !room.images) return;
  const count = room.images.length;
  roomImageIndex.value[roomId] =
    (roomImageIndex.value[roomId] - 1 + count) % count;
};
</script>

<style scoped>
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
</style>
