<template>
  <div>
    <h1 class="text-3xl font-bold text-gray-800 mb-6">
      Quản lý Khuyến mãi & Voucher
    </h1>

    <div class="mb-6 flex justify-between items-center">
      <div class="relative w-1/3">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Tìm theo tên hoặc mã voucher..."
          class="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
        <i
          class="fas fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
        ></i>
      </div>
      <button
        @click="openAddModal"
        class="bg-indigo-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-indigo-700 transition flex items-center gap-2"
      >
        <i class="fas fa-plus"></i>
        Thêm Voucher mới
      </button>
    </div>

    <div class="bg-white p-6 rounded-xl shadow-lg">
      <div v-if="isLoading" class="text-center py-10">
        <i class="fas fa-spinner fa-spin text-4xl text-indigo-500"></i>
        <p class="mt-2">Đang tải dữ liệu...</p>
      </div>
      <div v-else-if="error" class="text-center py-10 text-red-500">
        <p><strong>Lỗi:</strong> {{ error }}</p>
      </div>
      <div v-else class="overflow-x-auto">
        <table class="w-full text-sm text-left">
          <thead class="text-xs text-gray-500 uppercase bg-gray-50">
            <tr>
              <th class="px-4 py-3">Tên Voucher / Mã Code</th>
              <th class="px-4 py-3">Loại</th>
              <th class="px-4 py-3">Giá trị</th>
              <th class="px-4 py-3">Lượt sử dụng</th>
              <th class="px-4 py-3">Ngày hết hạn</th>
              <th class="px-4 py-3 text-center">Trạng thái</th>
              <th class="px-4 py-3 text-center">Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="voucher in filteredVouchers"
              :key="voucher.id"
              class="border-b hover:bg-gray-50"
            >
              <td class="px-4 py-3">
                <div class="font-bold text-gray-800">{{ voucher.name }}</div>
                <div class="font-mono text-xs text-indigo-600">
                  {{ voucher.code }}
                </div>
              </td>
              <td class="px-4 py-3">
                {{
                  voucher.type === "PERCENTAGE"
                    ? "Phần trăm (%)"
                    : "Số tiền cố định"
                }}
              </td>
              <td class="px-4 py-3 font-semibold">
                {{
                  voucher.type === "PERCENTAGE"
                    ? `${voucher.discountPercentage}%`
                    : formatCurrency(voucher.discountAmount)
                }}
              </td>
              <td class="px-4 py-3">
                <div>
                  <span class="font-semibold">{{ voucher.usageCount }}</span> /
                  <span class="text-gray-600">{{
                    voucher.usageLimit || "∞"
                  }}</span>
                  <span class="text-xs text-gray-500"> (tổng)</span>
                </div>
                <div class="text-xs text-gray-500">
                  Mỗi người: {{ voucher.userUsageLimit }} lần
                </div>
              </td>
              <td class="px-4 py-3">{{ formatDate(voucher.expiryDate) }}</td>
              <td class="px-4 py-3 text-center">
                <span
                  :class="getStatusClass(voucher.status)"
                  class="px-2 py-1 text-xs font-medium rounded-full"
                >
                  {{ voucher.status }}
                </span>
              </td>
              <td class="px-4 py-3 text-center">
                <button
                  @click="openEditModal(voucher)"
                  class="text-yellow-500 hover:text-yellow-700 mr-4"
                  title="Chỉnh sửa"
                >
                  <i class="fas fa-pencil-alt"></i>
                </button>
                <button
                  @click="handleDeleteVoucher(voucher.id)"
                  class="text-red-500 hover:text-red-700"
                  title="Xóa"
                >
                  <i class="fas fa-trash"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div
      v-if="isModalOpen"
      class="fixed inset-0 bg-black bg-opacity-50 z-50 flex justify-center items-center p-4"
    >
      <div
        class="bg-white rounded-lg shadow-2xl p-8 w-full max-w-3xl max-h-[90vh] flex flex-col"
      >
        <h2 class="text-2xl font-bold mb-6 flex-shrink-0">
          {{ isEditing ? "Chỉnh sửa Voucher" : "Tạo Voucher mới" }}
        </h2>
        <form @submit.prevent="handleSaveVoucher" class="overflow-y-auto pr-2">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="space-y-4">
              <div>
                <label class="block mb-2 font-semibold">Tên Voucher (*)</label>
                <input
                  v-model="currentVoucher.name"
                  type="text"
                  class="w-full border p-2 rounded-lg"
                  required
                />
              </div>
              <div>
                <label class="block mb-2 font-semibold">Mã Code (*)</label>
                <input
                  v-model="currentVoucher.code"
                  type="text"
                  class="w-full border p-2 rounded-lg"
                  required
                />
              </div>
              <div>
                <label class="block mb-2 font-semibold">Mô tả</label>
                <textarea
                  v-model="currentVoucher.description"
                  rows="3"
                  class="w-full border p-2 rounded-lg"
                ></textarea>
              </div>
              <div>
                <label class="block mb-2 font-semibold"
                  >Loại giảm giá (*)</label
                >
                <select
                  v-model="currentVoucher.type"
                  class="w-full border p-2 rounded-lg"
                >
                  <option value="FIXED_AMOUNT">Số tiền cố định</option>
                  <option value="PERCENTAGE">Phần trăm</option>
                </select>
              </div>
              <div v-if="currentVoucher.type === 'FIXED_AMOUNT'">
                <label class="block mb-2 font-semibold"
                  >Số tiền giảm (VNĐ) (*)</label
                >
                <input
                  v-model.number="currentVoucher.discountAmount"
                  type="number"
                  class="w-full border p-2 rounded-lg"
                  required
                />
              </div>
              <div v-if="currentVoucher.type === 'PERCENTAGE'">
                <label class="block mb-2 font-semibold"
                  >Phần trăm giảm (%) (*)</label
                >
                <input
                  v-model.number="currentVoucher.discountPercentage"
                  type="number"
                  class="w-full border p-2 rounded-lg"
                  required
                />
              </div>
            </div>
            <div class="space-y-4">
              <div>
                <label class="block mb-2 font-semibold">Trạng thái (*)</label>
                <select
                  v-model="currentVoucher.status"
                  class="w-full border p-2 rounded-lg"
                >
                  <option value="ACTIVE">Hoạt động</option>
                  <option value="INACTIVE">Không hoạt động</option>
                </select>
              </div>
              <div>
                <label class="block mb-2 font-semibold">Ngày bắt đầu (*)</label>

                <input
                  v-model="currentVoucher.startDate"
                  type="date"
                  class="w-full border p-2 rounded-lg"
                  :disabled="isEditing"
                  :class="{ 'bg-gray-100 cursor-not-allowed': isEditing }"
                  required
                />
                <p v-if="isEditing" class="text-xs text-gray-500 mt-1">
                  Không thể thay đổi ngày bắt đầu của voucher đã có.
                </p>
              </div>
              <div>
                <label class="block mb-2 font-semibold">Ngày hết hạn (*)</label>
                <input
                  v-model="currentVoucher.expiryDate"
                  type="date"
                  class="w-full border p-2 rounded-lg"
                  required
                />
              </div>
              <div>
                <label class="block mb-2 font-semibold"
                  >Đơn hàng tối thiểu (VNĐ)</label
                >
                <input
                  v-model.number="currentVoucher.conditionMinAmount"
                  type="number"
                  class="w-full border p-2 rounded-lg"
                />
              </div>
              <div v-if="currentVoucher.type === 'PERCENTAGE'">
                <label class="block mb-2 font-semibold"
                  >Giảm tối đa (VNĐ)</label
                >
                <input
                  v-model.number="currentVoucher.maxDiscountAmount"
                  type="number"
                  class="w-full border p-2 rounded-lg"
                />
              </div>
              <div>
                <label class="block mb-2 font-semibold"
                  >Tổng lượt sử dụng</label
                >
                <input
                  v-model.number="currentVoucher.usageLimit"
                  placeholder="Để trống nếu không giới hạn"
                  type="number"
                  class="w-full border p-2 rounded-lg"
                />
              </div>
              <div>
                <label class="block mb-2 font-semibold"
                  >Lượt dùng / người (*)</label
                >
                <input
                  v-model.number="currentVoucher.userUsageLimit"
                  type="number"
                  class="w-full border p-2 rounded-lg"
                  required
                />
              </div>
            </div>
          </div>
        </form>
        <div class="mt-8 flex justify-end gap-4 flex-shrink-0 pt-4 border-t">
          <button
            type="button"
            @click="closeModal"
            class="bg-gray-200 py-2 px-6 rounded-lg"
          >
            Hủy
          </button>
          <button
            @click="handleSaveVoucher"
            class="bg-indigo-600 text-white font-bold py-2 px-6 rounded-lg"
          >
            Lưu
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import voucherService from "@/api/voucherApi";

// --- STATE MANAGEMENT ---
const vouchers = ref([]);
const searchQuery = ref("");
const isLoading = ref(false);
const error = ref(null);

const isModalOpen = ref(false);
const isEditing = ref(false);
const currentVoucher = ref({});

// --- LIFECYCLE HOOK ---
onMounted(() => {
  fetchVouchers();
});

// --- API METHODS ---
const fetchVouchers = async () => {
  isLoading.value = true;
  error.value = null;
  try {
    const response = await voucherService.getAllVouchers();
    vouchers.value = response.data;
  } catch (err) {
    error.value = "Không thể tải danh sách voucher.";
    console.error(err);
  } finally {
    isLoading.value = false;
  }
};

const handleSaveVoucher = async () => {
  try {
    if (isEditing.value) {
      await voucherService.updateVoucher(
        currentVoucher.value.id,
        currentVoucher.value
      );
    } else {
      await voucherService.createVoucher(currentVoucher.value);
    }
    closeModal();
    await fetchVouchers();
  } catch (err) {
    const errorMsg =
      err.response?.data?.message || "Đã xảy ra lỗi khi lưu voucher!";
    alert(errorMsg);
    console.error(err);
  }
};

const handleDeleteVoucher = async (id) => {
  if (confirm("Bạn có chắc chắn muốn xóa voucher này?")) {
    try {
      await voucherService.deleteVoucher(id);
      await fetchVouchers();
    } catch (err) {
      alert("Đã xảy ra lỗi khi xóa voucher!");
      console.error(err);
    }
  }
};

// --- COMPUTED & HELPERS ---
const filteredVouchers = computed(() => {
  if (!searchQuery.value) return vouchers.value;
  const query = searchQuery.value.toLowerCase();
  return vouchers.value.filter(
    (v) =>
      v.code.toLowerCase().includes(query) ||
      v.name.toLowerCase().includes(query)
  );
});

const openAddModal = () => {
  isEditing.value = false;
  currentVoucher.value = {
    name: "",
    code: "",
    description: "",
    status: "ACTIVE",
    type: "FIXED_AMOUNT",
    discountAmount: null,
    discountPercentage: null,
    maxDiscountAmount: null,
    conditionMinAmount: null,
    startDate: "",
    expiryDate: "",
    usageLimit: null,
    userUsageLimit: 1,
  };
  isModalOpen.value = true;
};

const openEditModal = (voucher) => {
  isEditing.value = true;
  currentVoucher.value = {
    ...voucher,
    startDate: formatDateForInput(voucher.startDate),
    expiryDate: formatDateForInput(voucher.expiryDate),
  };
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
};

const getStatusClass = (status) =>
  ({
    ACTIVE: "bg-green-100 text-green-800",
    INACTIVE: "bg-yellow-100 text-yellow-800",
    EXPIRED: "bg-gray-100 text-gray-800",
  }[status] || "bg-gray-100");

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    value || 0
  );
const formatDateForInput = (dateString) =>
  dateString ? new Date(dateString).toISOString().split("T")[0] : "";
const formatDate = (dateString) =>
  new Date(dateString).toLocaleDateString("vi-VN");
</script>
