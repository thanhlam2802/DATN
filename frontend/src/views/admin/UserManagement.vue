<template>
  <div>
    <h1 class="text-3xl font-bold text-gray-800 mb-6">Quản lý Người dùng</h1>

    <div class="mb-6 flex justify-between items-center">
      <div class="relative w-1/3">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Tìm theo tên hoặc email..."
          class="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
        <i
          class="fas fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
        ></i>
      </div>
    </div>

    <div class="bg-white p-6 rounded-xl shadow-lg">
      <div class="overflow-x-auto">
        <table class="w-full text-sm text-left">
          <thead class="text-xs text-gray-500 uppercase bg-gray-50">
            <tr>
              <th class="px-4 py-3">Khách hàng</th>
              <th class="px-4 py-3">Số điện thoại</th>
              <th class="px-4 py-3">Ngày đăng ký</th>
              <th class="px-4 py-3">Tổng chi tiêu</th>
              <th class="px-4 py-3">Trạng thái</th>
              <th class="px-4 py-3 text-center">Hành động</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="user in filteredUsers"
              :key="user.id"
              class="border-b hover:bg-gray-50"
            >
              <td class="px-4 py-3">
                <div class="flex items-center gap-3">
                  <img
                    :src="user.avatar"
                    class="w-10 h-10 rounded-full object-cover"
                  />
                  <div>
                    <div class="font-bold">{{ user.name }}</div>
                    <div class="text-xs text-gray-500">{{ user.email }}</div>
                  </div>
                </div>
              </td>
              <td class="px-4 py-3">{{ user.phone }}</td>
              <td class="px-4 py-3">{{ formatDate(user.registrationDate) }}</td>
              <td class="px-4 py-3 font-semibold">
                {{ formatCurrency(user.totalSpent) }}
              </td>
              <td class="px-4 py-3">
                <span
                  :class="getStatusClass(user.status)"
                  class="px-2 py-1 text-xs font-medium rounded-full"
                >
                  {{ user.status }}
                </span>
              </td>
              <td class="px-4 py-3 text-center">
                <button
                  @click="viewUser(user)"
                  class="text-indigo-500 hover:text-indigo-700"
                >
                  <i class="fas fa-eye"></i> Xem chi tiết
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div
      v-if="isModalOpen"
      class="fixed inset-0 bg-black bg-opacity-50 z-50 flex justify-center items-center"
    >
      <div class="bg-white rounded-lg shadow-2xl p-8 w-full max-w-lg">
        <div class="flex items-start justify-between mb-6">
          <div class="flex items-center gap-4">
            <img
              :src="currentUser.avatar"
              class="w-20 h-20 rounded-full object-cover border-4 border-indigo-200"
            />
            <div>
              <h2 class="text-2xl font-bold">{{ currentUser.name }}</h2>
              <p class="text-gray-600">{{ currentUser.email }}</p>
            </div>
          </div>
          <button @click="isModalOpen = false" class="text-2xl">&times;</button>
        </div>

        <div class="space-y-4">
          <div>
            <h3 class="font-semibold text-gray-700">Thông tin liên hệ</h3>
            <p><strong>Số điện thoại:</strong> {{ currentUser.phone }}</p>
            <p>
              <strong>Ngày đăng ký:</strong>
              {{ formatDate(currentUser.registrationDate) }}
            </p>
          </div>
          <div>
            <h3 class="font-semibold text-gray-700">Thống kê</h3>
            <p>
              <strong>Tổng số đơn hàng:</strong> {{ currentUser.totalBookings }}
            </p>
            <p>
              <strong>Tổng chi tiêu:</strong>
              <span class="font-bold text-green-600">{{
                formatCurrency(currentUser.totalSpent)
              }}</span>
            </p>
          </div>
          <div>
            <h3 class="font-semibold text-gray-700">Trạng thái tài khoản</h3>
            <select
              v-model="currentUser.status"
              class="w-full md:w-1/2 border p-2 rounded-lg"
            >
              <option value="Active">Hoạt động</option>
              <option value="Inactive">Vô hiệu hóa</option>
              <option value="Banned">Bị cấm</option>
            </select>
          </div>
        </div>

        <div class="mt-8 flex justify-end gap-4">
          <button
            @click="isModalOpen = false"
            class="bg-gray-200 py-2 px-6 rounded-lg"
          >
            Đóng
          </button>
          <button
            @click="saveUser"
            class="bg-indigo-600 text-white font-bold py-2 px-6 rounded-lg"
          >
            Lưu thay đổi
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";

const searchQuery = ref("");
const users = ref([
  {
    id: 1,
    name: "Võ Lê Thành Lâm",
    email: "thanhlam@example.com",
    phone: "0905123456",
    avatar: "https://i.pravatar.cc/150?u=1",
    registrationDate: "2025-01-15",
    totalBookings: 5,
    totalSpent: 15200000,
    status: "Active",
  },
  {
    id: 2,
    name: "Nguyễn Văn An",
    email: "an.nguyen@example.com",
    phone: "0905654321",
    avatar: "https://i.pravatar.cc/150?u=2",
    registrationDate: "2025-03-20",
    totalBookings: 2,
    totalSpent: 4500000,
    status: "Active",
  },
  {
    id: 3,
    name: "Trần Thị Bình",
    email: "binh.tran@example.com",
    phone: "0988111222",
    avatar: "https://i.pravatar.cc/150?u=3",
    registrationDate: "2025-05-10",
    totalBookings: 0,
    totalSpent: 0,
    status: "Inactive",
  },
  {
    id: 4,
    name: "Lê Minh Cường",
    email: "cuong.le@example.com",
    phone: "0913987654",
    avatar: "https://i.pravatar.cc/150?u=4",
    registrationDate: "2024-11-02",
    totalBookings: 12,
    totalSpent: 45800000,
    status: "Banned",
  },
]);

const isModalOpen = ref(false);
const currentUser = ref({});

const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value;
  const query = searchQuery.value.toLowerCase();
  return users.value.filter(
    (u) =>
      u.name.toLowerCase().includes(query) ||
      u.email.toLowerCase().includes(query)
  );
});

const viewUser = (user) => {
  currentUser.value = { ...user };
  isModalOpen.value = true;
};

const saveUser = () => {
  const index = users.value.findIndex((u) => u.id === currentUser.value.id);
  if (index !== -1) {
    users.value[index] = currentUser.value;
  }
  isModalOpen.value = false;
};

const getStatusClass = (status) => {
  const map = {
    Active: "bg-green-100 text-green-800",
    Inactive: "bg-yellow-100 text-yellow-800",
    Banned: "bg-red-100 text-red-800",
  };
  return map[status] || "bg-gray-100 text-gray-800";
};

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    value || 0
  );
const formatDate = (dateString) =>
  new Date(dateString).toLocaleDateString("vi-VN");
</script>
