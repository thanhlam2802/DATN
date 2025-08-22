<template>
  <div>
    <h1 class="text-3xl font-bold text-gray-800 mb-4">
      Quản lý hệ thống Chuyến bay
    </h1>

    <div class="border-b border-gray-200 mb-6">
      <nav class="-mb-px flex space-x-6">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          @click="activeTab = tab.id"
          :class="[
            'whitespace-nowrap py-3 px-1 border-b-2 font-medium text-sm flex items-center gap-2',
            activeTab === tab.id
              ? 'border-indigo-500 text-indigo-600'
              : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
          ]"
        >
          <i :class="tab.icon"></i>
          <span>{{ tab.name }}</span>
        </button>
      </nav>
    </div>

    <div>
      <div v-if="activeTab === 'airlines'">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold text-gray-700">Quản lý Hãng hàng không</h2>
            <div class="flex gap-2">
              <input v-model="newAirlineName" placeholder="Tên hãng bay" class="border rounded-lg px-3 py-2" />
              <button @click="addAirline" class="bg-indigo-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-indigo-700">
                <i class="fas fa-plus mr-2"></i>Thêm
              </button>
            </div>
          </div>
          <div>
            <table class="min-w-full divide-y divide-gray-200">
              <thead>
                <tr>
                  <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                  <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tên</th>
                  <th class="px-4 py-2"></th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-200">
                <tr v-for="airline in airlines" :key="airline.id">
                  <td class="px-4 py-2 text-gray-700">{{ airline.id }}</td>
                  <td class="px-4 py-2">
                    <input v-if="editingAirlineId === airline.id" v-model="editingAirlineName" class="border rounded px-2 py-1 w-full" />
                    <span v-else>{{ airline.name }}</span>
                  </td>
                  <td class="px-4 py-2 text-right">
                    <button v-if="editingAirlineId === airline.id" @click="saveAirline(airline)" class="text-green-600 hover:underline mr-3">Lưu</button>
                    <button v-if="editingAirlineId === airline.id" @click="cancelEditAirline" class="text-gray-600 hover:underline mr-3">Hủy</button>
                    <button v-else @click="startEditAirline(airline)" class="text-indigo-600 hover:underline mr-3">Sửa</button>
                    <button @click="removeAirline(airline)" class="text-red-600 hover:underline">Xóa</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'airports'">
        <div class="bg-white p-6 rounded-xl shadow-lg">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold text-gray-700">Quản lý Sân bay</h2>
            <div class="flex gap-2">
              <input v-model="newAirportName" placeholder="Tên sân bay" class="border rounded-lg px-3 py-2" />
              <button @click="addAirport" class="bg-indigo-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-indigo-700">
                <i class="fas fa-plus mr-2"></i>Thêm
              </button>
            </div>
          </div>
          <div>
            <table class="min-w-full divide-y divide-gray-200">
              <thead>
                <tr>
                  <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                  <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tên</th>
                  <th class="px-4 py-2"></th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-200">
                <tr v-for="airport in airports" :key="airport.id">
                  <td class="px-4 py-2 text-gray-700">{{ airport.id }}</td>
                  <td class="px-4 py-2">
                    <input v-if="editingAirportId === airport.id" v-model="editingAirportName" class="border rounded px-2 py-1 w-full" />
                    <span v-else>{{ airport.name }}</span>
                  </td>
                  <td class="px-4 py-2 text-right">
                    <button v-if="editingAirportId === airport.id" @click="saveAirport(airport)" class="text-green-600 hover:underline mr-3">Lưu</button>
                    <button v-if="editingAirportId === airport.id" @click="cancelEditAirport" class="text-gray-600 hover:underline mr-3">Hủy</button>
                    <button v-else @click="startEditAirport(airport)" class="text-indigo-600 hover:underline mr-3">Sửa</button>
                    <button @click="removeAirport(airport)" class="text-red-600 hover:underline">Xóa</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'settings'">
        <div class="space-y-6">
          <!-- Header -->
          <div class="flex justify-between items-center">
            <h2 class="text-2xl font-bold text-gray-800">Quản lý Admin Chuyến bay</h2>
          </div>

          <!-- Loading -->
          <div v-if="loadingFlightAdmins" class="flex justify-center items-center py-12">
            <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          </div>

          <!-- Error -->
          <div v-else-if="flightAdminError" class="text-center py-8">
            <div class="text-red-600 mb-4">
              <i class="fas fa-exclamation-triangle text-2xl"></i>
            </div>
            <p class="text-gray-600">{{ flightAdminError }}</p>
            <button
              @click="loadFlightAdmins"
              class="mt-4 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
            >
              Thử lại
            </button>
          </div>

          <!-- Content -->
          <div v-else class="space-y-6">
            <!-- Summary Cards -->
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              <div class="bg-white rounded-xl shadow p-6">
                <div class="flex items-center">
                  <div class="p-3 rounded-full bg-blue-100 text-blue-600">
                    <i class="fas fa-users text-xl"></i>
                  </div>
                  <div class="ml-4">
                    <p class="text-sm font-medium text-gray-600">Tổng Admin</p>
                    <p class="text-2xl font-bold text-gray-900">{{ flightAdmins.length }}</p>
                  </div>
                </div>
              </div>
              
              <div class="bg-white rounded-xl shadow p-6">
                <div class="flex items-center">
                  <div class="p-3 rounded-full bg-green-100 text-green-600">
                    <i class="fas fa-plane text-xl"></i>
                  </div>
                  <div class="ml-4">
                    <p class="text-sm font-medium text-gray-600">Tổng Chuyến bay</p>
                    <p class="text-2xl font-bold text-gray-900">{{ totalFlightAdminsFlights }}</p>
                  </div>
                </div>
              </div>
              
              <div class="bg-white rounded-xl shadow p-6">
                <div class="flex items-center">
                  <div class="p-3 rounded-full bg-yellow-100 text-yellow-600">
                    <i class="fas fa-ticket-alt text-xl"></i>
                  </div>
                  <div class="ml-4">
                    <p class="text-sm font-medium text-gray-600">Tổng Booking</p>
                    <p class="text-2xl font-bold text-gray-900">{{ totalFlightAdminsBookings }}</p>
                  </div>
                </div>
              </div>
              
              <div class="bg-white rounded-xl shadow p-6">
                <div class="flex items-center">
                  <div class="p-3 rounded-full bg-purple-100 text-purple-600">
                    <i class="fas fa-coins text-xl"></i>
                  </div>
                  <div class="ml-4">
                    <p class="text-sm font-medium text-gray-600">Tổng Doanh thu</p>
                    <p class="text-2xl font-bold text-gray-900">{{ formatCurrency(totalFlightAdminsRevenue) }}</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Admin List -->
            <div class="bg-white rounded-xl shadow">
              <div class="px-6 py-4 border-b border-gray-200">
                <h3 class="text-lg font-semibold text-gray-800">Danh sách Admin Chuyến bay</h3>
              </div>
              
              <div class="overflow-x-auto">
                <table class="w-full">
                  <thead class="bg-gray-50">
                    <tr>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Admin
                      </th>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Chuyến bay
                      </th>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Booking
                      </th>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Doanh thu
                      </th>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Tỷ lệ lấp đầy
                      </th>
                      <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Thao tác
                      </th>
                    </tr>
                  </thead>
                  <tbody class="bg-white divide-y divide-gray-200">
                    <tr v-for="admin in flightAdmins" :key="admin.adminId" class="hover:bg-gray-50">
                      <td class="px-6 py-4 whitespace-nowrap">
                        <div>
                          <div class="text-sm font-medium text-gray-900">{{ admin.adminName }}</div>
                          <div class="text-sm text-gray-500">{{ admin.adminEmail }}</div>
                        </div>
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {{ admin.totalFlights }}
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {{ admin.totalBookings }}
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {{ formatCurrency(admin.totalRevenue) }}
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {{ admin.averageOccupancyRate.toFixed(1) }}%
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        <div class="relative">
                          <button
                            @click="toggleFlightDropdown(admin.adminId)"
                            class="text-blue-600 hover:text-blue-900 flex items-center"
                          >
                            <i class="fas fa-chevron-down mr-1 transition-transform" :class="{ 'rotate-180': openFlightDropdown === admin.adminId }"></i>
                            Chuyến bay
                          </button>
                          
                          <!-- Flight Dropdown -->
                          <div v-if="openFlightDropdown === admin.adminId" class="absolute right-0 mt-2 w-96 bg-white border border-gray-200 rounded-lg shadow-lg z-10">
                            <div class="p-4">
                              <div class="flex justify-between items-center mb-3">
                                <h4 class="font-semibold text-gray-800">Chuyến bay của {{ admin.adminName }}</h4>
                                <button @click="openFlightDropdown = null" class="text-gray-400 hover:text-gray-600">
                                  <i class="fas fa-times"></i>
                                </button>
                              </div>
                              
                              <!-- Loading -->
                              <div v-if="loadingFlights[admin.adminId]" class="flex justify-center py-4">
                                <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
                              </div>
                              
                              <!-- Flights List -->
                              <div v-else class="space-y-2 max-h-64 overflow-y-auto">
                                <div v-for="flight in adminFlights[admin.adminId]" :key="flight.id" 
                                     class="flex justify-between items-center p-2 hover:bg-gray-50 rounded">
                                  <div class="flex-1">
                                    <div class="font-medium text-sm">{{ flight.flightNumber }}</div>
                                    <div class="text-xs text-gray-500">
                                      {{ flight.departureAirport?.name }} → {{ flight.arrivalAirport?.name }}
                                    </div>
                                  </div>
                                  <button
                                    @click="viewFlight(flight.id)"
                                    class="text-blue-600 hover:text-blue-900 text-xs px-2 py-1 rounded border border-blue-300 hover:bg-blue-50"
                                  >
                                    Xem
                                  </button>
                                </div>
                                
                                <!-- Pagination -->
                                <div v-if="flightPagination[admin.adminId]" class="flex justify-between items-center pt-3 border-t">
                                  <div class="text-xs text-gray-500">
                                    Trang {{ flightPagination[admin.adminId].currentPage + 1 }} / {{ flightPagination[admin.adminId].totalPages }}
                                  </div>
                                  <div class="flex space-x-1">
                                    <button
                                      @click="loadAdminFlights(admin.adminId, flightPagination[admin.adminId].currentPage - 1)"
                                      :disabled="flightPagination[admin.adminId].currentPage === 0"
                                      class="px-2 py-1 text-xs border rounded disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                                    >
                                      <i class="fas fa-chevron-left"></i>
                                    </button>
                                    <button
                                      @click="loadAdminFlights(admin.adminId, flightPagination[admin.adminId].currentPage + 1)"
                                      :disabled="flightPagination[admin.adminId].currentPage >= flightPagination[admin.adminId].totalPages - 1"
                                      class="px-2 py-1 text-xs border rounded disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                                    >
                                      <i class="fas fa-chevron-right"></i>
                                    </button>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>

        <!-- Admin Detail Modal -->
        <div v-if="showDetailModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div class="relative top-20 mx-auto p-5 border w-11/12 max-w-4xl shadow-lg rounded-md bg-white">
            <div class="mt-3">
              <!-- Header -->
              <div class="flex justify-between items-center mb-6">
                <h3 class="text-lg font-semibold text-gray-900">
                  Chi tiết Admin: {{ selectedAdmin?.adminName }}
                </h3>
                <button
                  @click="showDetailModal = false"
                  class="text-gray-400 hover:text-gray-600"
                >
                  <i class="fas fa-times text-xl"></i>
                </button>
              </div>

              <!-- Loading Detail -->
              <div v-if="loadingDetail" class="flex justify-center items-center py-8">
                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
              </div>

              <!-- Detail Content -->
              <div v-else-if="adminDetail" class="space-y-6">
                <!-- Admin Info -->
                <div class="bg-gray-50 rounded-lg p-4">
                  <h4 class="font-semibold text-gray-800 mb-3">Thông tin Admin</h4>
                  <div class="grid grid-cols-2 gap-4">
                    <div>
                      <p class="text-sm text-gray-600">Tên:</p>
                      <p class="font-medium">{{ adminDetail.adminName }}</p>
                    </div>
                    <div>
                      <p class="text-sm text-gray-600">Email:</p>
                      <p class="font-medium">{{ adminDetail.adminEmail }}</p>
                    </div>
                  </div>
                </div>

                <!-- Statistics -->
                <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
                  <div class="bg-blue-50 rounded-lg p-4 text-center">
                    <p class="text-2xl font-bold text-blue-600">{{ adminDetail.totalFlights }}</p>
                    <p class="text-sm text-gray-600">Chuyến bay</p>
                  </div>
                  <div class="bg-green-50 rounded-lg p-4 text-center">
                    <p class="text-2xl font-bold text-green-600">{{ adminDetail.totalBookings }}</p>
                    <p class="text-sm text-gray-600">Booking</p>
                  </div>
                  <div class="bg-yellow-50 rounded-lg p-4 text-center">
                    <p class="text-2xl font-bold text-yellow-600">{{ formatCurrency(adminDetail.totalRevenue) }}</p>
                    <p class="text-sm text-gray-600">Doanh thu</p>
                  </div>
                  <div class="bg-purple-50 rounded-lg p-4 text-center">
                    <p class="text-2xl font-bold text-purple-600">{{ adminDetail.averageOccupancyRate.toFixed(1) }}%</p>
                    <p class="text-sm text-gray-600">Tỷ lệ lấp đầy</p>
                  </div>
                </div>

                <!-- Recent Flights -->
                <div>
                  <h4 class="font-semibold text-gray-800 mb-3">Chuyến bay gần đây</h4>
                  <div class="overflow-x-auto">
                    <table class="w-full text-sm">
                      <thead class="bg-gray-50">
                        <tr>
                          <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Mã chuyến bay</th>
                          <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Tuyến bay</th>
                          <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Thời gian</th>
                          <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Ghế</th>
                          <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Doanh thu</th>
                          <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Thao tác</th>
                        </tr>
                      </thead>
                      <tbody class="divide-y divide-gray-200">
                        <tr v-for="flight in adminDetail.recentFlights" :key="flight.flightId" class="hover:bg-gray-50">
                          <td class="px-4 py-2 font-medium">{{ flight.flightNumber }}</td>
                          <td class="px-4 py-2">{{ flight.departureAirport }} → {{ flight.arrivalAirport }}</td>
                          <td class="px-4 py-2">{{ formatDateTime(flight.departureTime) }}</td>
                          <td class="px-4 py-2">{{ flight.bookedSeats }}/{{ flight.totalSeats }}</td>
                          <td class="px-4 py-2 font-medium">{{ formatCurrency(flight.revenue) }}</td>
                          <td class="px-4 py-2">
                            <button
                              @click="viewFlight(flight.flightId)"
                              class="text-blue-600 hover:text-blue-900 text-sm"
                            >
                              <i class="fas fa-external-link-alt mr-1"></i>
                              Xem
                            </button>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div> 
</template>
<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import {
  getAdminAirlines,
  createAdminAirline,
  updateAdminAirline,
  deleteAdminAirline,
  getAdminAirports,
  createAdminAirport,
  updateAdminAirport,
  deleteAdminAirport,
} from "@/api/flightApi";
import { getFlightAdminSummaries, getFlightAdminDetail, getFlightsByAdminId } from "@/api/adminApi";

const router = useRouter();

const activeTab = ref("airlines");
const tabs = ref([
  { id: "airlines", name: "Quản lý Hãng bay", icon: "fas fa-building" },
  { id: "airports", name: "Quản lý Sân bay", icon: "fas fa-plane-departure" },
  { id: "settings", name: "Quản lý Admin Chuyến bay", icon: "fas fa-users" },
]);

// State
const airlines = ref([]);
const airports = ref([]);

const newAirlineName = ref("");
const newAirportName = ref("");

const editingAirlineId = ref(null);
const editingAirlineName = ref("");
const editingAirportId = ref(null);
const editingAirportName = ref("");

// Flight Admin Management State
const flightAdmins = ref([]);
const loadingFlightAdmins = ref(false);
const flightAdminError = ref("");
const showDetailModal = ref(false);
const selectedAdmin = ref(null);
const adminDetail = ref(null);
const loadingDetail = ref(false);

// Flight Dropdown State
const openFlightDropdown = ref(null);
const adminFlights = ref({});
const loadingFlights = ref({});
const flightPagination = ref({});

// Computed for Flight Admin Summary
const totalFlightAdminsFlights = computed(() => 
  flightAdmins.value.reduce((sum, admin) => sum + admin.totalFlights, 0)
);

const totalFlightAdminsBookings = computed(() => 
  flightAdmins.value.reduce((sum, admin) => sum + admin.totalBookings, 0)
);

const totalFlightAdminsRevenue = computed(() => 
  flightAdmins.value.reduce((sum, admin) => sum + admin.totalRevenue, 0)
);

// Load data
async function loadAirlines() {
  const res = await getAdminAirlines();
  airlines.value = res.data;
}

async function loadAirports() {
  const res = await getAdminAirports();
  airports.value = res.data;
}

async function loadFlightAdmins() {
  loadingFlightAdmins.value = true;
  flightAdminError.value = "";
  try {
    const res = await getFlightAdminSummaries();
    flightAdmins.value = res.data;
  } catch (e) {
    flightAdminError.value = "Không thể tải danh sách admin chuyến bay.";
    console.error(e);
  } finally {
    loadingFlightAdmins.value = false;
  }
}

onMounted(async () => {
  await Promise.all([loadAirlines(), loadAirports(), loadFlightAdmins()]);
});

// Airline CRUD
async function addAirline() {
  if (!newAirlineName.value.trim()) return;
  await createAdminAirline({ name: newAirlineName.value.trim() });
  newAirlineName.value = "";
  await loadAirlines();
}

function startEditAirline(airline) {
  editingAirlineId.value = airline.id;
  editingAirlineName.value = airline.name;
}

async function saveAirline(airline) {
  if (!editingAirlineName.value.trim()) return;
  await updateAdminAirline(airline.id, { name: editingAirlineName.value.trim() });
  editingAirlineId.value = null;
  editingAirlineName.value = "";
  await loadAirlines();
}

function cancelEditAirline() {
  editingAirlineId.value = null;
  editingAirlineName.value = "";
}

async function removeAirline(airline) {
  await deleteAdminAirline(airline.id);
  await loadAirlines();
}

// Airport CRUD
async function addAirport() {
  if (!newAirportName.value.trim()) return;
  await createAdminAirport({ name: newAirportName.value.trim() });
  newAirportName.value = "";
  await loadAirports();
}

function startEditAirport(airport) {
  editingAirportId.value = airport.id;
  editingAirportName.value = airport.name;
}

async function saveAirport(airport) {
  if (!editingAirportName.value.trim()) return;
  await updateAdminAirport(airport.id, { name: editingAirportName.value.trim() });
  editingAirportId.value = null;
  editingAirportName.value = "";
  await loadAirports();
}

function cancelEditAirport() {
  editingAirportId.value = null;
  editingAirportName.value = "";
}

async function removeAirport(airport) {
  await deleteAdminAirport(airport.id);
  await loadAirports();
}

// Flight Admin Management Methods
async function viewAdminDetail(admin) {
  selectedAdmin.value = admin;
  showDetailModal.value = true;
  loadingDetail.value = true;
  adminDetail.value = null;
  
  try {
    const res = await getFlightAdminDetail(admin.adminId);
    adminDetail.value = res.data;
  } catch (e) {
    console.error("Không thể tải chi tiết admin:", e);
  } finally {
    loadingDetail.value = false;
  }
}

function toggleFlightDropdown(adminId) {
  if (openFlightDropdown.value === adminId) {
    openFlightDropdown.value = null;
  } else {
    openFlightDropdown.value = adminId;
    if (!adminFlights.value[adminId]) {
      loadAdminFlights(adminId, 0);
    }
  }
}

async function loadAdminFlights(adminId, page = 0) {
  loadingFlights.value[adminId] = true;
  try {
    const res = await getFlightsByAdminId(adminId, page, 10);
    adminFlights.value[adminId] = res.data.content;
    flightPagination.value[adminId] = {
      currentPage: res.data.number,
      totalPages: res.data.totalPages,
      totalElements: res.data.totalElements
    };
  } catch (e) {
    console.error("Không thể tải chuyến bay của admin:", e);
  } finally {
    loadingFlights.value[adminId] = false;
  }
}

function viewFlight(flightId) {
  router.push(`/plane/${flightId}`);
}

function formatCurrency(value) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
}

function formatDateTime(dateTimeString) {
  if (!dateTimeString) return "";
  return new Date(dateTimeString).toLocaleString("vi-VN");
}
</script>

<style scoped>
.rotate-180 {
  transform: rotate(180deg);
}

.transition-transform {
  transition: transform 0.2s ease-in-out;
}
</style>
