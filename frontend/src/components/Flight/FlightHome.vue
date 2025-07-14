<template>
  <div
    class="container w-full min-h-screen py-10 overflow-hidden relative after:absolute after:w-96 after:h-96 after:bg-sky-300 after:-z-10 after:rounded-full after:-top-70 after:left-4 after:blur-xl after:[box-shadow:-100px_50px_30px_100px_#7dd3fc]"
  >
    <div class="max-w-7xl mx-auto px-4">
      <div class="mb-8 text-center relative">
        <h1 class="text-4xl font-bold text-gray-900">Booking</h1>
        <p class="mt-2 text-gray-500">
          Tìm và đặt vé máy bay theo nhu cầu của bạn
        </p>
        <i
          class="fa-solid fa-plane-departure absolute top-8 right-64 text-5xl text-[#4f39f6]"
        ></i>
      </div>
      <div class="mb-10 flex justify-center">
        <div class="inline-flex space-x-2 bg-white rounded-full shadow-md p-1">
          <button
            @click="currentTab = 'one-way'"
            :class="
              currentTab === 'one-way' ? tabActiveClass : tabInactiveClass
            "
            class="px-4 py-2 rounded-full text-sm font-medium transition-colors"
          >
            Một chiều
          </button>
          <button
            @click="currentTab = 'round-trip'"
            :class="
              currentTab === 'round-trip' ? tabActiveClass : tabInactiveClass
            "
            class="px-4 py-2 rounded-full text-sm font-medium transition-colors"
          >
            Khứ hồi
          </button>
          <button
            @click="currentTab = 'multi-city'"
            :class="
              currentTab === 'multi-city' ? tabActiveClass : tabInactiveClass
            "
            class="px-4 py-2 rounded-full text-sm font-medium transition-colors"
          >
            Nhiều thành phố
          </button>
        </div>
      </div>
      <form
        @submit.prevent="onSearch"
        class="rounded-xl shadow-lg p-8 mb-10 border-t border-l border-gray-200 overflow-hidden relative before:absolute before:w-96 before:h-96 before:bg-sky-300 before:-z-10 before:rounded-full before:-top-70 before:-right-72 before:blur-xl before:[box-shadow:-100px_50px_30px_100px_#7dd3fc] after:absolute after:w-96 after:h-96 after:bg-sky-300 after:-z-10 after:rounded-full after:top-70 after:-left-72 after:blur-xl after:[box-shadow:-100px_50px_30px_100px_#7dd3fc]"
      >
        <div class="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-8">
          <div>
            <label
              for="from"
              class="block text-sm font-medium text-gray-700 mb-1 flex items-center"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-400 mr-2"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M17 11l-5-5m0 0l-5 5m5-5v12"
                />
              </svg>
              From
            </label>
            <input
              id="from"
              v-model.number="filters.departureAirportId"
              type="number"
              placeholder="1"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
          <div>
            <label
              for="to"
              class="block text-sm font-medium text-gray-700 mb-1 flex items-center"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-400 mr-2"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 8c-4.418 0-8 1.79-8 4v4h16v-4c0-2.21-3.582-4-8-4z"
                />
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 8V4m0 0L8 8m4-4l4 4"
                />
              </svg>
              To
            </label>
            <input
              id="to"
              v-model.number="filters.arrivalAirportId"
              type="number"
              placeholder="2"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
          <div>
            <label
              for="departDate"
              class="block text-sm font-medium text-gray-700 mb-1 flex items-center"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-400 mr-2"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                />
              </svg>
              Ngày khởi hành
            </label>
            <div class="relative">
              <input
                id="departDate"
                v-model="filters.departureDate"
                type="date"
                class="w-full border border-gray-300 rounded-lg px-4 py-2 pr-10 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-400 absolute right-3 top-1/2 transform -translate-y-1/2 pointer-events-none"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                />
              </svg>
            </div>
          </div>
          <div v-if="currentTab === 'round-trip'">
            <label
              for="returnDate"
              class="block text-sm font-medium text-gray-700 mb-1 flex items-center"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-400 mr-2"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                />
              </svg>
              Ngày về
            </label>
            <div class="relative">
              <input
                id="returnDate"
                v-model="filters.returnDate"
                type="date"
                class="w-full border border-gray-300 rounded-lg px-4 py-2 pr-10 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-400 absolute right-3 top-1/2 transform -translate-y-1/2 pointer-events-none"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                />
              </svg>
            </div>
          </div>

          <div
            v-if="currentTab === 'multi-city'"
            class="lg:col-span-2 xl:col-span-3"
          >
            <label
              for="multiCities"
              class="block text-sm font-medium text-gray-700 mb-1 flex items-center"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-400 mr-2"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M13 10V3L4 14h7v7l9-11h-7z"
                />
              </svg>
              Hành trình (ví dụ: JFK-ORD, ORD-LAX)
            </label>
            <input
              id="multiCities"
              v-model="filters.multiCities"
              type="text"
              placeholder="JFK-ORD, ORD-LAX"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
            <p class="text-xs text-gray-500 mt-1">
              Nhập nhiều chặng, cách nhau bởi dấu phẩy.
            </p>
          </div>

          <div>
            <label
              for="passengers"
              class="block text-sm font-medium text-gray-700 mb-1 flex items-center"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-400 mr-2"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M5 13l4 4L19 7"
                />
              </svg>
              Số hành khách
            </label>
            <select
              id="passengers"
              v-model="filters.passengers"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option v-for="n in 10" :key="n" :value="n">{{ n }} người</option>
            </select>
          </div>

          <div>
            <label
              for="cabinClass"
              class="block text-sm font-medium text-gray-700 mb-1 flex items-center"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-400 mr-2"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M16 11V7a4 4 0 10-8 0v4M5 20h14v-5H5v5z"
                />
              </svg>
              Cabin Class
            </label>
            <select
              id="cabinClass"
              v-model="filters.cabinClass"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option value="economy">Economy</option>
              <option value="premium">Premium Economy</option>
              <option value="business">Business</option>
              <option value="first">First Class</option>
            </select>
          </div>
          <div>
            <label
              class="block text-sm font-medium text-gray-700 mb-2 flex items-center"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-400 mr-2"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V7M7 3h10v4H7V3z"
                />
              </svg>
              Hãng hàng không
            </label>
            <div class="flex flex-wrap gap-2">
              <template v-for="(label, key) in airlineOptions" :key="key">
                <button
                  type="button"
                  @click="toggleAirline(key)"
                  :class="
                    filters.airlines.includes(key)
                      ? 'bg-indigo-600 text-white'
                      : 'bg-white text-gray-600 border border-gray-300 hover:bg-gray-100'
                  "
                  class="px-3 py-1 rounded-full text-sm flex items-center space-x-1 transition-colors"
                >
                  <span>{{ label }}</span>
                </button>
              </template>
            </div>
          </div>

          <div>
            <label
              class="block text-sm font-medium text-gray-700 mb-2 flex items-center"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-400 mr-2"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M9 17v-6a3 3 0 116 0v6m-6 0H5a2 2 0 01-2-2v-3a2 2 0 012-2h2m10 7h2a2 2 0 002-2v-3a2 2 0 00-2-2h-2m0 0V7a3 3 0 00-6 0v3"
                />
              </svg>
              Số điểm dừng
            </label>
            <div class="flex flex-wrap gap-2">
              <template v-for="(label, key) in stopsOptions" :key="key">
                <button
                  type="button"
                  @click="toggleStop(key)"
                  :class="
                    filters.stops.includes(key)
                      ? 'bg-indigo-600 text-white'
                      : 'bg-white text-gray-600 border border-gray-300 hover:bg-gray-100'
                  "
                  class="px-3 py-1 rounded-full text-sm transition-colors"
                >
                  {{ label }}
                </button>
              </template>
            </div>
          </div>

          <div>
            <label
              for="timeWindow"
              class="block text-sm font-medium text-gray-700 mb-1 flex items-center"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-400 mr-2"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 8v4l3 3m6-12a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
              Thời gian khởi hành
            </label>
            <select
              id="timeWindow"
              v-model="filters.timeWindow"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option value="all">Tất cả</option>
              <option value="early">Sáng sớm (0:00–6:00)</option>
              <option value="morning">Sáng (6:00–12:00)</option>
              <option value="afternoon">Chiều (12:00–18:00)</option>
              <option value="evening">Tối (18:00–24:00)</option>
            </select>
          </div>

          <div>
            <label
              class="block text-sm font-medium text-gray-700 mb-2 flex items-center"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-400 mr-2"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 8c-3.866 0-7 1.343-7 3v2c0 1.657 3.134 3 7 3s7-1.343 7-3v-2c0-1.657-3.134-3-7-3z"
                />
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 12c-3.866 0-7 1.343-7 3v2c0 1.657 3.134 3 7 3s7-1.343 7-3v-2c0-1.657-3.134-3-7-3z"
                />
              </svg>
              Giá (VND)
            </label>
            <div class="flex space-x-4">
              <div class="w-1/2">
                <input
                  v-model.number="filters.priceMin"
                  type="number"
                  min="0"
                  placeholder="Tối thiểu"
                  class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>
              <div class="w-1/2">
                <input
                  v-model.number="filters.priceMax"
                  type="number"
                  :min="filters.priceMin"
                  placeholder="Tối đa"
                  class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>
            </div>
            <p class="text-xs text-gray-500 mt-1">Nhập khoảng giá mong muốn.</p>
          </div>
        </div>

        <div class="mt-8 flex justify-end">
          <button
            type="submit"
            class="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold px-8 py-3 rounded-lg transition-colors shadow-md"
          >
            Search
          </button>
        </div>
      </form>

      <h2 class="text-2xl font-semibold text-gray-800 mb-4">Flight options</h2>
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
        <div
          v-for="flight in paginatedFlights"
          :key="flight.id"
          class="relative group overflow-hidden rounded-xl shadow-lg hover:shadow-2xl transition-shadow"
        >
          <img
            :src="flight.images && flight.images.length > 0 ? flight.images[0].imageUrl : 'https://ix-marketing.imgix.net/autotagging.png?auto=format,compress&w=1946'"
            :alt="flight.name"
            class="w-full h-56 object-cover group-hover:scale-105 transition-transform duration-300"
          />
          <div
            class="absolute inset-0 bg-gradient-to-t from-black via-transparent opacity-60"
          ></div>

          <div class="absolute bottom-4 left-4 text-white">
            <h3 class="text-xl font-semibold">{{ flight.name }}</h3>
            <p class="text-sm mt-1">
              {{ formatTime(flight.departureTime) }} - {{ formatTime(flight.arrivalTime) }}
            </p>
            <p class="text-lg font-bold mt-2">
              {{ priceDisplay(flight) }}
            </p>
          </div>

          <button
            @click="openBooking(flight)"
            class="absolute top-4 right-4 bg-indigo-600 hover:bg-indigo-700 text-white px-3 py-1 rounded-md text-sm transition-colors shadow-md"
          >
            Book
          </button>
        </div>
      </div>

      <div class="mt-10 flex justify-center items-center space-x-2">
        <button
          @click="prevPage"
          :disabled="currentPage === 1"
          class="p-2 rounded-full bg-white hover:bg-gray-100 shadow-sm disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <svg
            class="h-5 w-5 text-gray-600"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M15 19l-7-7 7-7"
            />
          </svg>
        </button>

        <button
          v-for="page in totalPages"
          :key="page"
          @click="goToPage(page)"
          :class="page === currentPage ? pageActiveClass : pageInactiveClass"
          class="w-8 h-8 flex items-center justify-center rounded-full text-sm font-medium transition-colors"
        >
          {{ page }}
        </button>

        <button
          @click="nextPage"
          :disabled="currentPage === totalPages"
          class="p-2 rounded-full bg-white hover:bg-gray-100 shadow-sm disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <svg
            class="h-5 w-5 text-gray-600"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M9 5l7 7-7 7"
            />
          </svg>
        </button>
      </div>
    </div>
  </div>
  <div class="fixed inset-0 z-50 flex w-full" v-show="showBookingModal">
    <div
      class="absolute inset-0 bg-black/30 backdrop-blur-sm"
      @click="closeBooking"
    ></div>

    <div
      class="relative bg-white overflow-scroll h-full w-7/12 shadow-xl transform transition-transform duration-300 ease-in-out pr-4"
      :class="showBookingModal ? 'translate-x-3/4' : 'translate-x-full'"
    >
      <div class="border-b border-gray-200 px-6 mt-14 py-4">
        <h2 class="text-xl font-semibold text-gray-800">Chuyến đi của bạn</h2>
      </div>

      <div class="p-6 overflow-y-auto h-full">
        <div v-if="selectedFlight" class="mb-6">
          <div class="group relative w-full h-56 [perspective:1000px]">
            <div
              class="absolute inset-0 w-full h-full duration-1000 [transform-style:preserve-3d] group-hover:[transform:rotateX(180deg)]"
            >
              <div
                class="absolute inset-0 bg-white rounded-xl shadow-lg flex flex-col [backface-visibility:hidden] bg-gradient-to-br from-violet-400 to-indigo-600"
              >
                <div class="flex items-center space-x-14 overflow-hidden">
                  <img
                    :src="selectedFlight.image"
                    alt="flight image"
                    class="h-full object-cover rounded-lg overflow-hidden"
                  />
                  <div class="text-white">
                    <h3 class="text-3xl font-semibold">
                      {{ selectedFlight.name }}
                    </h3>
                    <p class="text-sm">
                      {{ formatTime(selectedFlight.departureTime) }} - {{ formatTime(selectedFlight.arrivalTime) }}
                    </p>
                  </div>
                  <div>
                    <p class="text-2xl text-white font-bold">
                      {{ priceDisplay(selectedFlight) }}
                    </p>
                  </div>
                </div>
              </div>

              <div
                class="absolute inset-0 bg-white rounded-xl shadow-lg p-4 [transform:rotateX(180deg)] [backface-visibility:hidden]"
              >
                <div class="mb-3">
                  <h4 class="text-lg font-bold text-gray-800">
                    Thông tin chuyến bay
                  </h4>
                </div>
                <div class="text-sm text-gray-600 space-y-1">
                  <p>
                    <strong>Chặng:</strong>
                    {{ selectedFlight.name }}
                  </p>
                  <p>
                    <strong>Khởi hành:</strong>
                    Sân bay: {{ selectedFlight.departureAirport.name }}
                    Thời gian: {{ formatTime(selectedFlight.departureTime) }}
                  </p>
                  <p>
                    <strong>Đến:</strong>
                    Sân bay: {{ selectedFlight.arrivalAirport.name }}
                    Thời gian: {{ formatTime(selectedFlight.arrivalTime) }}
                  </p>
                  <p>
                    <strong>Hãng:</strong>
                    {{ selectedFlight.airline ? (typeof selectedFlight.airline === 'object' ? selectedFlight.airline.name : selectedFlight.airline) : 'N/A' }}
                  </p>
                  <p class="text-gray-500 italic">
                    {{ selectedFlight.flightNumber }} • {{ formatTime(selectedFlight.departureTime) }} – {{ formatTime(selectedFlight.arrivalTime) }}
                  </p>
                  <p>
                    <strong>Số ghế còn:</strong>
                    {{ availableSeats !== null ? availableSeats.total : '...' }}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="mb-4">
          <h4 class="text-lg font-semibold text-gray-800 mb-4">
            Chọn loại vé của bạn
          </h4>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div
              class="group cursor-pointer transform transition-all duration-500 hover:scale-105 hover:-rotate-1"
            >
              <div
                class="relative w-full rounded-3xl border border-indigo-500/20 bg-gradient-to-tr from-[#0F0F0F] to-[#1B1B1B] shadow-2xl overflow-hidden backdrop-blur-xl hover:border-indigo-500/40 hover:shadow-indigo-500/10 hover:shadow-3xl"
              > 
                <div class="absolute inset-0 z-0 overflow-hidden">
                  <div
                    class="absolute inset-0 bg-gradient-to-tr from-indigo-500/10 to-indigo-400/20 opacity-40 group-hover:opacity-60 transition-opacity duration-500"
                  ></div>
                  <div
                    class="absolute -bottom-20 -left-20 w-48 h-48 rounded-full bg-indigo-500/20 blur-3xl opacity-30 group-hover:opacity-50 transition-all duration-700 animate-bounce delay-500"
                  ></div>
                  <div
                    class="absolute top-10 left-10 w-16 h-16 rounded-full bg-indigo-500/10 blur-xl animate-ping"
                  ></div>
                  <div
                    class="absolute bottom-16 right-16 w-12 h-12 rounded-full bg-indigo-500/10 blur-lg animate-ping delay-1000"
                  ></div>
                  <div
                    class="absolute inset-0 bg-gradient-to-r from-transparent via-indigo-500/10 to-transparent -skew-x-12 translate-x-full group-hover:translate-x-[-200%] transition-transform duration-1000"
                  ></div>
                </div>

                <div class="p-6 relative z-10 flex flex-col space-y-3">
                  <div class="flex items-center space-x-3">
                    <div class="relative flex h-[50px] w-[50px] items-center justify-center p-3 rounded-full border border-indigo-500/30">
                      <input type="radio" id="radio" name="gender" value="male" class="peer z-10 h-full w-full cursor-pointer opacity-0" />
                      <div
                        class="absolute h-full w-full rounded-full  bg-black/40 backdrop-blur-md p-4 shadow-sm shadow-[#00000050] ring-blue-400 duration-300 peer-checked:scale-110 peer-checked:ring-2">
                      </div>
                      <div class="absolute -z-10 h-full w-full scale-0 rounded-full bg-indigo-200/30 duration-500 filter
                      peer-checked:scale-[300%] peer-checked:blur-lg"></div>
                      <div class="absolute stroke-indigo-400">
                        ✈️
                      </div>
                    </div>
                    <h5 class="text-xl font-bold text-white tracking-wide">
                      Phổ thông
                    </h5>
                  </div>

                  <p class="text-gray-300">
                    Giá:<span class="text-sm text-gray-400"> / 1 khách</span>
                  </p>

                  <ul
                    class="text-sm text-gray-300 list-disc list-inside space-y-1"
                  >
                    <li>Giá từ: <span class="font-bold text-indigo-400">{{ economySummary?.price ? formatCurrency(economySummary.price) : 'N/A' }}</span></li>
                    <li>Hành lý xách tay: <span class="font-bold text-indigo-400">{{ economySummary?.carryOnLuggage ? economySummary.carryOnLuggage + ' kg' : 'N/A' }}</span></li>
                    <li>Số lượng vé: <span class="font-bold text-indigo-400">{{ economySummary?.total ?? 0 }}</span></li>
                  </ul>

                  <div class="mt-2 flex flex-col gap-1 text-sm text-indigo-300 hover:underline">
                    <label class="inline-flex items-center">
                      <input type="radio" v-model="economySeatType" value="window" class="form-radio text-indigo-600" />
                      <span class="ml-2 font-bold">Ngồi cửa sổ <span class="text-xs text-gray-400">(+200,000 VND)</span> <span class="ml-1 text-xs text-gray-500">(Còn {{ economySummary.countWindow ?? 0 }})</span></span>
                    </label>
                    <label class="inline-flex items-center">
                      <input type="radio" v-model="economySeatType" value="aisle" class="form-radio text-indigo-600" />
                      <span class="ml-2 font-bold">Ngồi lối đi <span class="ml-1 text-xs text-gray-500">(Còn {{ economySummary.countAisle ?? 0 }})</span></span>
                    </label>
                  </div>
                </div>
              </div>
            </div>

            <div
              class="group cursor-pointer transform transition-all duration-500 hover:scale-105 hover:-rotate-1"
            >
              <div
                class="relative w-full rounded-3xl border border-yellow-400/20 bg-gradient-to-tr from-[#1A1400] to-[#2B1F00] shadow-2xl overflow-hidden backdrop-blur-xl hover:border-yellow-400/40 hover:shadow-yellow-400/10 hover:shadow-3xl"
              >
                <div class="absolute inset-0 z-0 overflow-hidden">
                  <div
                    class="absolute inset-0 bg-gradient-to-tr from-yellow-400/10 to-yellow-300/20 opacity-40 group-hover:opacity-60 transition-opacity duration-500"
                  ></div>
                  <div
                    class="absolute -bottom-20 -left-20 w-48 h-48 rounded-full bg-yellow-400/20 blur-3xl opacity-30 group-hover:opacity-50 transition-all duration-700 animate-bounce delay-500"
                  ></div>
                  <div
                    class="absolute top-10 left-10 w-16 h-16 rounded-full bg-yellow-400/10 blur-xl animate-ping"
                  ></div>
                  <div
                    class="absolute bottom-16 right-16 w-12 h-12 rounded-full bg-yellow-400/10 blur-lg animate-ping delay-1000"
                  ></div>
                  <div
                    class="absolute inset-0 bg-gradient-to-r from-transparent via-yellow-400/10 to-transparent -skew-x-12 translate-x-full group-hover:translate-x-[-200%] transition-transform duration-1000"
                  ></div>
                </div>

                <div class="p-6 relative z-10 flex flex-col space-y-3 overflow-hidden">
                  <div class="flex items-center space-x-3 ">
                    <div class="relative flex h-[50px] w-[50px] items-center justify-center p-3 rounded-full border  border-yellow-400/30">
                      <input type="radio" id="radio" name="gender" value="male" class="peer z-10 h-full w-full cursor-pointer opacity-0" />
                      <div
                        class="absolute h-full w-full rounded-full  bg-black/40 backdrop-blur-md p-4 shadow-sm shadow-[#00000050] ring-yellow-400 duration-300 peer-checked:scale-110 peer-checked:ring-2">
                      </div>
                      <div class="absolute -z-10 h-full w-full scale-0 rounded-full bg-yellow-400/30 duration-500 filter
                                  peer-checked:scale-[300%] peer-checked:blur-lg"></div>
                      <div class="absolute stroke-indigo-400">
                        👑
                      </div>
                    </div>
                    <h5 class="text-xl font-bold text-yellow-200 tracking-wide">
                      Thương gia
                    </h5>
                  </div>

                  <p class="text-gray-300">
                    Giá:
                    <span class="font-bold text-yellow-300">{{
                      selectedFlight?.price
                    }}</span
                    ><span class="text-sm text-gray-400"> / khách</span>
                  </p>

                  <ul
                    class="text-sm text-gray-300 list-disc list-inside space-y-1"
                  >
                    <li>Giá từ: <span class="font-bold text-indigo-400">{{ businessSummary?.price ? formatCurrency(businessSummary.price) : 'N/A' }}</span></li>
                    <li>Hành lý xách tay: <span class="font-bold text-indigo-400">{{ businessSummary?.carryOnLuggage ? businessSummary.carryOnLuggage + ' kg' : 'N/A' }}</span></li>
                    <li>Số lượng vé: <span class="font-bold text-indigo-400">{{ businessSummary?.total ?? 0 }}</span></li>
                  </ul>

                  <div class="mt-2 flex flex-col gap-1 text-sm text-yellow-300 hover:underline">
                    <label class="inline-flex items-center">
                      <input type="radio" v-model="businessSeatType" value="window" class="form-radio text-yellow-500" />
                      <span class="ml-2 font-bold">Ngồi cửa sổ <span class="text-xs text-gray-400">(+200,000 VND)</span> <span class="ml-1 text-xs text-gray-500">(Còn {{ businessSummary.countWindow ?? 0 }})</span></span>
                    </label>
                    <label class="inline-flex items-center">
                      <input type="radio" v-model="businessSeatType" value="aisle" class="form-radio text-yellow-500" />
                      <span class="ml-2 font-bold">Ngồi lối đi <span class="ml-1 text-xs text-gray-500">(Còn {{ businessSummary.countAisle ?? 0 }})</span></span>
                    </label>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="mt-8 flex">
          <router-link
            to="/plane/pay"
            class="w-2/6 block text-center mx-auto bg-green-600 hover:bg-green-700 text-white font-semibold py-3 rounded-md transition-colors"
          >
            Tiếp tục thanh toán
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { searchFlights, getAvailableSeats } from '@/api/flightApi'
import Flight from '@/entity/Flight'

/** ========== Tab State ========== **/
const currentTab = ref("one-way");
const tabActiveClass = "bg-indigo-600 text-white";
const tabInactiveClass = "bg-white text-gray-600 hover:bg-gray-100";
const showBookingModal = ref(false);
const selectedFlight = ref(null);
const availableSeats = ref(null);

function openBooking(flight) {
  selectedFlight.value = flight;
  availableSeats.value = null;
  getAvailableSeats(flight.id).then(res => {
    availableSeats.value = res.data;
    economySummary.value.total = availableSeats.value.economy;
    businessSummary.value.total = availableSeats.value.business;
    economySummary.value.countWindow = availableSeats.value.economyWindow;
    economySummary.value.countAisle = availableSeats.value.economyAisle;
    businessSummary.value.countWindow = availableSeats.value.businessWindow;
    businessSummary.value.countAisle = availableSeats.value.businessAisle;
    getTicketSummary(selectedFlight.value.flightSlots, false);
    getTicketSummary(selectedFlight.value.flightSlots, true);
  });
  showBookingModal.value = true;
  // Debug chi tiết
  setTimeout(() => {
    console.log('Available seats:', availableSeats.value);
    console.log('Economy summary:', economySummary.value);
    console.log('Business summary:', businessSummary.value);
  }, 2000);

  
}
const economySummary = ref({
  price: 0,
  carryOnLuggage: 0,
  total: 0,
  countWindow: 0,
  countAisle: 0
});
const businessSummary = ref({
  price: 0,
  carryOnLuggage: 0,
  total: 0,
  countWindow: 0,
  countAisle: 0
});
function closeBooking() {
  showBookingModal.value = false;
  selectedFlight.value = null;
}

/** ========== Filter State ========== **/
const filters = ref({
  departureAirportId: null,
  arrivalAirportId: null,
  departureDate: '',
  returnDate: '',
  passengers: 1,
  cabinClass: "economy",
  airlines: [], // array of selected airline keys
  stops: [], // array of selected stop types
  timeWindow: "all",
  priceMin: 0,
  priceMax: 1000,
  multiCities: "",
});





// Tùy chọn stop types
const stopsOptions = {
  direct: "Bay thẳng",
  "1stop": "1 điểm dừng",
  "2plus": "2+ điểm dừng",
};

// Hàm toggle chọn airlines (đa lựa)
function toggleAirline(key) {
  const idx = filters.value.airlines.indexOf(key);
  if (idx === -1) filters.value.airlines.push(key);
  else filters.value.airlines.splice(idx, 1);
}

// Hàm toggle chọn stops
function toggleStop(key) {
  const idx = filters.value.stops.indexOf(key);
  if (idx === -1) filters.value.stops.push(key);
  else filters.value.stops.splice(idx, 1);
}

const flights = ref([])
const loading = ref(false)
const error = ref('')

function onSearch() {
  loading.value = true
  searchFlights({
    departureAirportId: filters.value.departureAirportId,
    arrivalAirportId: filters.value.arrivalAirportId,
    departureDate: filters.value.departureDate,
    airlineId: filters.value.airlines.length === 1 ? filters.value.airlines[0] : undefined
  })
    .then(res => {
      flights.value = res.data
    })
    .catch(() => {
      error.value = 'Không thể tìm chuyến bay.'
    })
    .finally(() => {
      loading.value = false
    })
}

onMounted(() => {
  onSearch()
})

/** ========== Pagination ========== **/
const itemsPerPage = 6;
const currentPage = ref(1);
const totalPages = computed(() =>
  Math.ceil(flights.value.length / itemsPerPage)
);
const paginatedFlights = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return flights.value.slice(start, start + itemsPerPage);
});

function goToPage(page) {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
}

function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
}

function nextPage() {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
}

// CSS classes cho pagination
const pageActiveClass = "bg-indigo-600 text-white shadow-md";
const pageInactiveClass = "bg-white text-gray-600 hover:bg-gray-100";

function priceDisplay(flight) {
  if (flight.minPrice && flight.maxPrice && flight.minPrice !== flight.maxPrice) {
    return formatCurrency(flight.minPrice) + ' - ' + formatCurrency(flight.maxPrice);
  } else if (flight.minPrice) {
    return formatCurrency(flight.minPrice);
  } else {
    return 'Liên hệ';
  }
}
function formatCurrency(val) {
  if (!val) return '';
  return Number(val).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
}
function formatTime(val) {
  if (!val) return '';
  const d = new Date(val);
  return d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
}
function getTicketSummary(slots, isBusiness) {
  console.log('Slots:', slots);
  for (const slot of slots) {
    if (slot.isBusiness === isBusiness) {
      businessSummary.value.price = slot.price;
      businessSummary.value.carryOnLuggage = slot.carryOnLuggage;
    }else{
      economySummary.value.price = slot.price;
      economySummary.value.carryOnLuggage = slot.carryOnLuggage;
    }
  }
}

const economySeatType = ref('window');
const businessSeatType = ref('window');


</script>

<style scoped>

</style>
