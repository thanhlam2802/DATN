<template>
  <div
    class="container w-full min-h-screen py-10 overflow-hidden relative after:absolute after:w-96 after:h-96 after:bg-sky-300 after:-z-10 after:rounded-full after:-top-70 after:left-4 after:blur-xl after:[box-shadow:-100px_50px_30px_100px_#7dd3fc]">
    <div class="max-w-7xl mx-auto px-4">
      <div class="mb-8 text-center relative">
        <h1 class="text-4xl font-bold text-gray-900">Booking</h1>
        <p class="mt-2 text-gray-500">
          Tìm và đặt vé máy bay theo nhu cầu của bạn
        </p>
        <i class="fa-solid fa-plane-departure absolute top-8 right-64 text-5xl text-[#4f39f6]"></i>
      </div>
      <form @submit.prevent="onSearch"
        class="rounded-xl shadow-lg p-8 mb-10 border-t border-l border-gray-200 overflow-hidden relative before:absolute before:w-96 before:h-96 before:bg-sky-300 before:-z-10 before:rounded-full before:-top-70 before:-right-72 before:blur-xl before:[box-shadow:-100px_50px_30px_100px_#7dd3fc] after:absolute after:w-96 after:h-96 after:bg-sky-300 after:-z-10 after:rounded-full after:top-70 after:-left-72 after:blur-xl after:[box-shadow:-100px_50px_30px_100px_#7dd3fc]">
        <div class="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-8">
          <div>
            <label for="from" class="block text-sm font-medium text-gray-700 mb-1 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                stroke="currentColor" class="size-6">
                <path stroke-linecap="round" stroke-linejoin="round"
                  d="M2.25 18 9 11.25l4.306 4.306a11.95 11.95 0 0 1 5.814-5.518l2.74-1.22m0 0-5.94-2.281m5.94 2.28-2.28 5.941" />
              </svg>

              Sân bay khởi hành
            </label>
            <select id="departureAirport" v-model="filters.departureAirportId"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500">
              <option value="">Chọn sân bay khởi hành</option>
              <option v-for="airport in airports" :key="airport.id" :value="airport.id">
                {{ airport.name }}
              </option>
            </select>
            <!-- Xóa validation error display -->
          </div>
          <div>
            <label for="to" class="block text-sm font-medium text-gray-700 mb-1 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                stroke="currentColor" class="size-6">
                <path stroke-linecap="round" stroke-linejoin="round"
                  d="M2.25 6 9 12.75l4.286-4.286a11.948 11.948 0 0 1 4.306 6.43l.776 2.898m0 0 3.182-5.511m-3.182 5.51-5.511-3.181" />
              </svg>

              Sân bay hạ cánh
            </label>
            <select id="arrivalAirport" v-model="filters.arrivalAirportId"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500">
              <option value="">Chọn sân bay hạ cánh</option>
              <option v-for="airport in airports" :key="airport.id" :value="airport.id">
                {{ airport.name }}
              </option>
            </select>
            <!-- Xóa validation error display -->
          </div>
          <div>
            <label for="departDate" class="block text-sm font-medium text-gray-700 mb-1 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400 mr-2" fill="none" viewBox="0 0 24 24"
                stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              Ngày khởi hành
            </label>
            <div class="relative">
              <input id="departDate" v-model="filters.departureDate" type="date"
                class="w-full border border-gray-300 rounded-lg px-4 py-2 pr-10 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
              <svg xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-400 absolute right-3 top-1/2 transform -translate-y-1/2 pointer-events-none"
                fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
            <!-- Xóa validation error display -->
          </div>


          <div>
            <label for="passengers" class="block text-sm font-medium text-gray-700 mb-1 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                stroke="currentColor" class="size-6">
                <path stroke-linecap="round" stroke-linejoin="round"
                  d="M3.75 5.25h16.5m-16.5 4.5h16.5m-16.5 4.5h16.5m-16.5 4.5h16.5" />
              </svg>

              Loại chuyến bay
            </label>
            <select v-model="filters.categoryId"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500">
              <option value="one-way">Một chiều</option>
              <option value="round-trip">Khứ hồi</option>
              <option v-for="cate in categories" :key="cate.id" :value="cate.id">
                {{ cate.name }}
              </option>
            </select>
          </div>

          <div>
            <label for="cabinClass" class="block text-sm font-medium text-gray-700 mb-1 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                stroke="currentColor" class="size-6">
                <path stroke-linecap="round" stroke-linejoin="round"
                  d="M6 12 3.269 3.125A59.769 59.769 0 0 1 21.485 12 59.768 59.768 0 0 1 3.27 20.875L5.999 12Zm0 0h7.5" />
              </svg>

              Hãng hàng không
            </label>
            <select v-model="filters.airlineId"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500">
              <option v-for="airline in airlines" :key="airline.id" :value="airline.id">
                {{ airline.name }}
              </option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400 mr-2" fill="none" viewBox="0 0 24 24"
                stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M12 8c-3.866 0-7 1.343-7 3v2c0 1.657 3.134 3 7 3s7-1.343 7-3v-2c0-1.657-3.134-3-7-3z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M12 12c-3.866 0-7 1.343-7 3v2c0 1.657 3.134 3 7 3s7-1.343 7-3v-2c0-1.657-3.134-3-7-3z" />
              </svg>
              Giá (VND)
            </label>
            <div class="flex space-x-4">
              <div class="w-1/2">
                <input type="range" v-model="filters.priceMin" min="0" max="10000000" step="50000" class="w-full" />
              </div>
              <div class="w-1/2">
                <input type="range" v-model="filters.priceMax" min="0" max="10000000" step="50000" class="w-full" />
              </div>
            </div>
            <div class="text-xs text-gray-500 flex">
              <span class="text-center block w-1/2">{{ filters.priceMin <= 0 ? "0 ₫" :formatCurrency(filters.priceMin)
                  }}</span>
                  <span class="text-center block w-1/2 ">{{ formatCurrency(filters.priceMax )}}</span>
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                stroke="currentColor" class="size-6">
                <path stroke-linecap="round" stroke-linejoin="round"
                  d="M12 6v6h4.5m4.5 0a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" />
              </svg>
              Thời điểm khởi hành
            </label>
            <div class="flex flex-col space-y-4 p-4 bg-transparent rounded-lg shadow">
              <!-- Sáng -->
              <label class="relative flex items-center cursor-pointer">
                <input type="radio" name="timeWindow" value="morning" v-model="filters.timeWindow"
                  class="sr-only peer" />
                <div class="w-6 h-6 flex items-center justify-center bg-transparent border-2 border-yellow-500 rounded-full
             peer-checked:bg-yellow-500 peer-checked:border-yellow-500
             peer-hover:shadow-lg peer-hover:shadow-yellow-500/50
             transition duration-300 ease-in-out">
                  <!-- Icon mặt trời buổi sáng -->
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 text-white" fill="none" viewBox="0 0 24 24"
                    stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M12 3v2m0 14v2m9-9h-2M5 12H3m15.364-6.364l-1.414 1.414M7.05 16.95l-1.414 1.414m0-11.314l1.414 1.414M16.95 16.95l1.414 1.414M12 8a4 4 0 100 8 4 4 0 000-8z" />
                  </svg>
                </div>
                <span class="ml-2 text-gray-700">Sáng (05:00–12:00)</span>
              </label>

              <!-- Trưa -->
              <label class="relative flex items-center cursor-pointer">
                <input type="radio" name="timeWindow" value="afternoon" v-model="filters.timeWindow"
                  class="sr-only peer" />
                <div class="w-6 h-6 flex items-center justify-center bg-transparent border-2 border-red-500 rounded-full
             peer-checked:bg-red-500 peer-checked:border-red-500
             peer-hover:shadow-lg peer-hover:shadow-red-500/50
             transition duration-300 ease-in-out">
                  <!-- Icon mặt trời cao (trưa) -->
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 text-white" fill="none" viewBox="0 0 24 24"
                    stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M12 8a4 4 0 100 8 4 4 0 000-8zM12 2v2m0 16v2m8-10h-2M6 12H4m12.364-6.364l-1.414 1.414M7.05 16.95l-1.414 1.414" />
                  </svg>
                </div>
                <span class="ml-2 text-gray-700">Trưa (12:00–20:00)</span>
              </label>

              <!-- Tối -->
              <label class="relative flex items-center cursor-pointer">
                <input type="radio" name="timeWindow" value="night" v-model="filters.timeWindow"
                  class="sr-only peer" />
                <div class="w-6 h-6 flex items-center justify-center bg-transparent border-2 border-indigo-500 rounded-full
             peer-checked:bg-indigo-500 peer-checked:border-indigo-500
             peer-hover:shadow-lg peer-hover:shadow-indigo-500/50
             transition duration-300 ease-in-out">
                  <!-- Icon mặt trăng (tối) -->
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 text-white" fill="none" viewBox="0 0 24 24"
                    stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M20.354 15.354A9 9 0 118.646 3.646 7 7 0 0020.354 15.354z" />
                  </svg>
                </div>
                <span class="ml-2 text-gray-700">Tối (20:00–05:00)</span>
              </label>
            </div>


          </div>
          <div class="my-auto flex justify-center items-center gap-4 self-start">
            <button type="submit"
              class=" bg-indigo-600 hover:bg-transparent hover:text-indigo-600 horver:border hover:border-4 horver:border-indigo-600 text-white font-semibold px-8 py-3 rounded-lg transition-colors shadow-md">
              Search
            </button>
            <button @click="reset"
              class="border bg-transparent border-4 border-indigo-600 hover:bg-indigo-600 hover:text-white text-indigo-600 font-semibold px-8 py-3 rounded-lg transition-colors shadow-md">
              Reset
            </button>
          </div>

        </div>


      </form>

      <h2 class="text-2xl font-semibold text-gray-800 mb-4">Flight options</h2>
      
      <!-- Loading state -->
      <div v-if="loading" class="flex justify-center items-center py-20">
        <div class="flex flex-col items-center">
          <div class="animate-spin rounded-full h-16 w-16 border-b-2 border-indigo-600 mb-4"></div>
          <p class="text-lg text-gray-600 font-medium">Đang tìm kiếm chuyến bay...</p>
        </div>
      </div>
      
      <!-- Error state -->
      <div v-else-if="error" class="text-center py-20">
        <div class="text-red-500 text-lg mb-4">
          <i class="fas fa-exclamation-triangle text-3xl mb-4"></i>
          <p>{{ error }}</p>
        </div>
        <button @click="onSearch" class="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-3 rounded-lg">
          Thử lại
        </button>
      </div>
      
      <!-- Flight list -->
      <div v-else-if="flights.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        <div v-for="flight in paginatedFlights" :key="flight.id"
          class="relative group overflow-hidden rounded-xl shadow-lg hover:shadow-2xl transition-shadow">
          <img
            :src="flight.images && flight.images.length > 0 ? flight.images[0].imageUrl : 'https://ix-marketing.imgix.net/autotagging.png?auto=format,compress&w=1946'"
            :alt="flight.name"
            class="w-full h-56 object-cover group-hover:scale-105 transition-transform duration-300" />
          <div class="absolute inset-0 bg-gradient-to-t from-black via-transparent opacity-60"></div>

          <div class="absolute bottom-4 left-4 text-white">
            <h3 class="text-xl font-semibold">{{ flight.name }}</h3>
            <p class="text-sm mt-1">
              {{ formatDate(flight.departureTime)+ ' ' +formatTime(flight.departureTime) }}
            </p>
            <p class="text-lg font-bold mt-2">
              {{ priceDisplay(flight) }}
            </p>
          </div>

          <button @click="openBooking(flight)"
            class="absolute top-4 right-4 bg-indigo-600 hover:bg-indigo-700 text-white px-3 py-1 rounded-md text-sm transition-colors shadow-md">
            Book
          </button>
        </div>
      </div>

      <div class="mt-10 flex justify-center items-center space-x-2">
        <button @click="prevPage" :disabled="currentPage === 1"
          class="p-2 rounded-full bg-white hover:bg-gray-100 shadow-sm disabled:opacity-50 disabled:cursor-not-allowed">
          <svg class="h-5 w-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
        </button>

        <button v-for="page in totalPages" :key="page" @click="goToPage(page)"
          :class="page === currentPage ? pageActiveClass : pageInactiveClass"
          class="w-8 h-8 flex items-center justify-center rounded-full text-sm font-medium transition-colors">
          {{ page }}
        </button>

        <button @click="nextPage" :disabled="currentPage === totalPages"
          class="p-2 rounded-full bg-white hover:bg-gray-100 shadow-sm disabled:opacity-50 disabled:cursor-not-allowed">
          <svg class="h-5 w-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
        </button>
      </div>
    </div>
  </div>
  <div class="fixed inset-0 z-50 flex w-full" v-show="showBookingModal">
    <div class="absolute inset-0 bg-black/30 backdrop-blur-sm" @click="closeBooking"></div>

    <div
      class="relative bg-white overflow-scroll h-full w-7/12 shadow-xl transform transition-transform duration-300 ease-in-out pr-4"
      :class="showBookingModal ? 'translate-x-3/4' : 'translate-x-full'">
      <div class="border-b border-gray-200 px-6 mt-14 py-4">
        <h2 class="text-xl font-semibold text-gray-800">Chuyến đi của bạn</h2>
      </div>

      <div class="p-6 overflow-y-auto h-full">
        <div v-if="selectedFlight" class="mb-6">
          <div class="group relative w-full h-56 [perspective:1000px]">
            <div
              class="absolute inset-0 w-full h-full duration-1000 [transform-style:preserve-3d] group-hover:[transform:rotateX(180deg)]">
              <div
                class="absolute inset-0 bg-white rounded-xl shadow-lg flex flex-col [backface-visibility:hidden] bg-gradient-to-br from-violet-400 to-indigo-600">
                <div class="flex items-center space-x-14 overflow-hidden">
                  <img :src="selectedFlight.images[0].imageUrl" alt="flight image"
                    class="h-full w-full object-cover rounded-lg overflow-hidden" />
                  <div class="text-white">
                    <h3 class="text-2xl font-semibold">
                      {{ selectedFlight.name }}
                    </h3>
                    <p class="text-sm">
                      {{formatDate(selectedFlight.departureTime)+ ' ' +formatTime(selectedFlight.departureTime)}}
                    </p>
                    <p class="text-2xl text-white font-bold">
                      {{ priceDisplay(selectedFlight) }}
                    </p>
                  </div>
                </div>
              </div>

              <div
                class="absolute inset-0 bg-white rounded-xl shadow-lg p-4 [transform:rotateX(180deg)] [backface-visibility:hidden]">
                <div class="mb-3">
                  <h4 class="text-lg font-bold text-gray-800 flex gap-3">
                    Thông tin chuyến bay  <p class="text-gray-500 italic">
                    {{ selectedFlight.flightNumber }} 
                  </p>
                  </h4>
                </div>
                <div class="text-sm text-gray-600 space-y-1">
                  <p>
                    <strong>Chặng:</strong>
                    {{ selectedFlight.name }}
                  </p>
                  <p>
                    <strong>Khởi hành:</strong>
                    {{ selectedFlight.departureAirport.name }} - 
                    Thời gian: {{formatDate(selectedFlight.departureTime)+ ' ' +formatTime(selectedFlight.departureTime) }}
                  </p>
                  <p>
                    <strong>Đến:</strong>
                    {{ selectedFlight.arrivalAirport.name }} - 
                    Thời gian: {{formatDate(selectedFlight.arrivalTime)+ ' ' +formatTime(selectedFlight.arrivalTime) }}
                  </p>
                  <p>
                    <strong>Hãng:</strong>
                    {{ selectedFlight.airline ? (typeof selectedFlight.airline === 'object' ?
                    selectedFlight.airline.name
                    : selectedFlight.airline) : 'N/A' }}
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
            <div v-if="economySummary.total > 0" class="group rounded-3xl cursor-pointer transform transition-all duration-500 hover:scale-105 hover:-rotate-1"
                 :class="{'scale-105 -rotate-1 ring-2 ring-indigo-500/60 shadow-indigo-500/20': selectedCabinClass==='economy'}"
                 @click="() => { selectedCabinClass = 'economy'; if (!economySeatType) economySeatType = 'window'; selectedSeatType = economySeatType; }">
              <div
                class="relative w-full rounded-3xl border border-indigo-500/20 bg-gradient-to-tr from-[#0F0F0F] to-[#1B1B1B] shadow-2xl overflow-hidden backdrop-blur-xl hover:border-indigo-500/40 hover:shadow-indigo-500/10 hover:shadow-3xl">
                <div class="absolute inset-0 z-0 overflow-hidden">
                  <div
                    class="absolute inset-0 bg-gradient-to-tr from-indigo-500/10 to-indigo-400/20 opacity-40 group-hover:opacity-60 transition-opacity duration-500">
                  </div>
                  <div
                    class="absolute -bottom-20 -left-20 w-48 h-48 rounded-full bg-indigo-500/20 blur-3xl opacity-30 group-hover:opacity-50 transition-all duration-700 animate-bounce delay-500">
                  </div>
                  <div class="absolute top-10 left-10 w-16 h-16 rounded-full bg-indigo-500/10 blur-xl animate-ping">
                  </div>
                  <div
                    class="absolute bottom-16 right-16 w-12 h-12 rounded-full bg-indigo-500/10 blur-lg animate-ping delay-1000">
                  </div>
                  <div
                    class="absolute inset-0 bg-gradient-to-r from-transparent via-indigo-500/10 to-transparent -skew-x-12 translate-x-full group-hover:translate-x-[-200%] transition-transform duration-1000">
                  </div>
                </div>

                <div class="p-6 relative z-10 flex flex-col space-y-3">
                  <div class="flex items-center space-x-3">
                    <div
                      class="relative flex h-[50px] w-[50px] items-center justify-center p-3 rounded-full border border-indigo-500/30">
                      <input type="radio" id="economy" name="cabinClass" value="economy" v-model="selectedCabinClass"
                        @change="selectedSeatType = economySeatType"
                        class="peer z-10 h-full w-full cursor-pointer opacity-0" />
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

                  <ul class="text-sm text-gray-300 list-disc list-inside space-y-1">
                    <li>Hành lý xách tay: <span class="font-bold text-indigo-400">{{ economySummary?.carryOnLuggage ?
                        economySummary.carryOnLuggage + ' kg' : 'N/A' }}</span></li>
                    <li>Số lượng vé: <span class="font-bold text-indigo-400">{{ economySummary?.total ?? 0 }}</span>
                    </li>
                  </ul>

                  <div class="mt-2 flex flex-col gap-1 text-sm text-indigo-300 hover:underline">
                    <label   class="inline-flex items-center">
                      <input  type="radio" v-model="economySeatType" value="window" @change="selectedSeatType = 'window'"
                        class="form-radio text-indigo-600" :disabled="economySummary.countWindow <= 0"/>
                        <span class="ml-2 font-bold">Ngồi cửa sổ <span class="text-xs text-gray-400">(+{{ economySummary.priceWindow }} VND)</span>
                        <span class="ml-1 text-xs text-gray-500">(Còn {{ economySummary.countWindow ?? 0
                          }})</span></span>
                    </label>
                    <label  class="inline-flex items-center">
                      <input type="radio" v-model="economySeatType" value="aisle" @change="selectedSeatType = 'aisle'"
                        class="form-radio text-indigo-600"  :disabled="economySummary.countAisle <= 0"/>
                      <span class="ml-2 font-bold">Ngồi lối đi <span class="text-xs text-gray-400">(+{{ economySummary.priceAisle }} VND)</span>
                        <span class="ml-1 text-xs text-gray-500">(Còn {{
                          economySummary.countAisle ?? 0 }})</span></span>
                    </label>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="businessSummary.total > 0" class="group rounded-3xl cursor-pointer transform transition-all duration-500 hover:scale-105 hover:-rotate-1"
                 :class="{'scale-105 -rotate-1 ring-2 ring-yellow-400/60 shadow-yellow-400/20': selectedCabinClass==='business'}"
                 @click="() => { selectedCabinClass = 'business'; if (!businessSeatType) businessSeatType = 'window'; selectedSeatType = businessSeatType; }">
              <div
                class="relative w-full rounded-3xl border border-yellow-400/20 bg-gradient-to-tr from-[#1A1400] to-[#2B1F00] shadow-2xl overflow-hidden backdrop-blur-xl hover:border-yellow-400/40 hover:shadow-yellow-400/10 hover:shadow-3xl">
                <div class="absolute inset-0 z-0 overflow-hidden">
                  <div
                    class="absolute inset-0 bg-gradient-to-tr from-yellow-400/10 to-yellow-300/20 opacity-40 group-hover:opacity-60 transition-opacity duration-500">
                  </div>
                  <div
                    class="absolute -bottom-20 -left-20 w-48 h-48 rounded-full bg-yellow-400/20 blur-3xl opacity-30 group-hover:opacity-50 transition-all duration-700 animate-bounce delay-500">
                  </div>
                  <div class="absolute top-10 left-10 w-16 h-16 rounded-full bg-yellow-400/10 blur-xl animate-ping">
                  </div>
                  <div
                    class="absolute bottom-16 right-16 w-12 h-12 rounded-full bg-yellow-400/10 blur-lg animate-ping delay-1000">
                  </div>
                  <div
                    class="absolute inset-0 bg-gradient-to-r from-transparent via-yellow-400/10 to-transparent -skew-x-12 translate-x-full group-hover:translate-x-[-200%] transition-transform duration-1000">
                  </div>
                </div>

                <div  class="p-6 relative z-10 flex flex-col space-y-3 overflow-hidden">
                  <div class="flex items-center space-x-3 ">
                    <div
                      class="relative flex h-[50px] w-[50px] items-center justify-center p-3 rounded-full border  border-yellow-400/30">
                      <input type="radio" id="business" name="cabinClass" value="business" v-model="selectedCabinClass"
                        @change="selectedSeatType = businessSeatType"
                        class="peer z-10 h-full w-full cursor-pointer opacity-0" />
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
                      }}</span><span class="text-sm text-gray-400"> / khách</span>
                  </p>

                  <ul class="text-sm text-gray-300 list-disc list-inside space-y-1">
                    <li>Hành lý xách tay: <span class="font-bold text-indigo-400">{{ businessSummary?.carryOnLuggage ?
                        businessSummary.carryOnLuggage + ' kg' : 'N/A' }}</span></li>
                    <li>Số lượng vé: <span class="font-bold text-indigo-400">{{ businessSummary?.total ?? 0 }}</span>
                    </li>
                  </ul>

                  <div class="mt-2 flex flex-col gap-1 text-sm text-yellow-300 hover:underline">
                    <label   class="inline-flex items-center">
                      <input type="radio" v-model="businessSeatType" value="window" :disabled="businessSummary.countWindow <= 0"
                        @change="selectedSeatType = 'window'" class="form-radio text-yellow-500" />
                      <span class="ml-2 font-bold">Ngồi cửa sổ <span class="text-xs text-gray-400">(+{{ businessSummary.priceWindow }} VND)</span>
                        <span class="ml-1 text-xs text-gray-500">(Còn {{ businessSummary.countWindow ?? 0
                          }})</span></span>
                    </label>
                    <label  class="inline-flex items-center">
                      <input type="radio" v-model="businessSeatType" value="aisle" @change="selectedSeatType = 'aisle'"
                        class="form-radio text-yellow-500" :disabled="businessSummary.countAisle <= 0" />
                      <span class="ml-2 font-bold">Ngồi lối đi <span class="text-xs text-gray-400">(+{{ businessSummary.priceAisle }} VND)</span>
                        <span class="ml-1 text-xs text-gray-500">(Còn {{
                          businessSummary.countAisle ?? 0 }})</span></span>
                    </label>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="mt-8 flex">
          <button @click="createAndLogDto"
            class="w-2/6 block text-center mx-auto bg-green-600 hover:bg-green-700 text-white font-semibold py-3 rounded-md transition-colors">
            Đặt chỗ
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from 'vue-router';
import { searchFlights, getAvailableSeats, getAllAirlines,getAllAirports ,getAllFlightCategories} from '@/api/flightApi'
import Flight from '@/entity/Flight'
import FindAvailableSlotRequestDto from '@/dto/FindAvailableSlotRequestDto'

const router = useRouter();
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
const categories = ref([]);
const airports =ref([]);
const airlines =ref([]);
const economySummary = ref({
  price: 0,
  carryOnLuggage: 0,
  total: 0,
  countWindow: 0,
  countAisle: 0,
  priceWindow: 0,
  priceAisle: 0
});
const businessSummary = ref({
  price: 0,
  carryOnLuggage: 0,
  total: 0,
  countWindow: 0,
  countAisle: 0,
  priceWindow: 0,
  priceAisle: 0
});
function closeBooking() {
  showBookingModal.value = false;
  selectedFlight.value = null;
}

function reset(){
  filters.value ={
  departureAirportId: null,
  arrivalAirportId: null,
  departureDate: '',
  categoryId: null,
  airlineId: null,
  timeWindow:null,  
  priceMin: 0,
  priceMax: 10000000,
}
}
const filters = ref({
  departureAirportId: null,
  arrivalAirportId: null,
  departureDate: '',
  categoryId: null,
  airlineId: null,
  timeWindow:null,  
  priceMin: 0,
  priceMax: 10000000,
});

// Xóa validation errors - không còn sử dụng
// const validationErrors = ref({})

const flights = ref([])
const loading = ref(false)
const error = ref('')

function onSearch() {
  // Xóa validation - chỉ search trực tiếp
  loading.value = true
  console.log(filters.value);
  
  searchFlights(filters.value)
    .then(res => {
      flights.value = res.data
      window.$toast('Thành công!', 'success');
    })
    .catch(() => {
      error.value = 'Không thể tìm chuyến bay.'
      window.$toast('Không tìm thấy chuyến bay!', 'error');
    })
    .finally(() => {
      loading.value = false
    })
}
function fillAirPorts(){
  getAllAirports().then(res =>{
    airports.value = res.data;
    console.log(airports.value);
    
  })
  getAllAirlines().then(res =>{
    airlines.value = res.data;
  })
  getAllFlightCategories().then(res =>{
    categories.value = res.data;
  })
  
}
onMounted(() => {
  fillAirPorts();
  searchFlights(filters.value)
    .then(res => {
      flights.value = res.data
      window.$toast('Tìm kiếm thành công!', 'success');
    })
    .catch((e) => {
      error.value = 'Không thể tìm chuyến bay.'
      console.log(e);
      
      window.$toast('Không tìm thấy chuyến bay!', 'error');
    })
    .finally(() => {
      loading.value = false
    })
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
    return formatCurrency(flight.minPrice) + ' ' + formatCurrency(flight.maxPrice);
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
function formatDate(val) {
  if (!val) return '';
  const d = new Date(val);
  return d.toLocaleDateString('vi-VN');
}
function getTicketSummary(slots, isBusiness) {
  console.log('Slots:', slots);
  for (const slot of slots) {
    if (slot.isBusiness === isBusiness) {
      businessSummary.value.price = slot.price;
      businessSummary.value.carryOnLuggage = slot.carryOnLuggage;
      if(slot.isWindow){
        businessSummary.value.priceWindow = slot.price;
      }
      if(slot.isAisle){
        businessSummary.value.priceAisle = slot.price;
      }
    } else {
      economySummary.value.price = slot.price;
      economySummary.value.carryOnLuggage = slot.carryOnLuggage;
      if(slot.isWindow){
        economySummary.value.priceWindow = slot.price;
      }
      if(slot.isAisle){
        economySummary.value.priceAisle = slot.price;
      }
    }
  }
}

const economySeatType = ref('window');
const businessSeatType = ref('window');

// Thêm biến để theo dõi lựa chọn cabin class
const selectedCabinClass = ref('economy');
const selectedSeatType = ref('window');

// Function để tạo DTO và router sang PaymentPage
function createAndLogDto() {
  if (!selectedFlight.value) {
    console.error('Không có chuyến bay được chọn');
    return;
  }

  // Xác định cabin class và seat type dựa trên lựa chọn
  let cabinClass = selectedCabinClass.value;
  let seatType = selectedSeatType.value;

  // Tạo DTO
  const requestDto = FindAvailableSlotRequestDto.fromFormData(
    selectedFlight.value.id,
    seatType,
    cabinClass
  );

  // Log DTO
  requestDto.log();

  // Router sang PaymentPage với DTO
  router.push({
    name: 'PayFlight',
    params: {
      flightId: selectedFlight.value.id
    },
    query: {
      dto: JSON.stringify(requestDto.toObject())
    }
  });

  return requestDto;
}


</script>

<style scoped></style>
