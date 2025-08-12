<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">
      Danh sách đơn đăng ký làm nhà cung cấp
    </h1>

    <div v-if="loading" class="text-center text-gray-500 py-10">
      Đang tải dữ liệu...
    </div>

    <div v-else>
      <div
        v-if="applications.length === 0"
        class="text-center text-gray-500 py-10"
      >
        Không có đơn đăng ký nào đang chờ duyệt.
      </div>

      <div
        v-else
        class="min-w-full bg-white shadow-md rounded-lg overflow-hidden border border-gray-200"
      >
        <table class="min-w-full">
          <thead class="bg-gray-50 text-left">
            <tr>
              <th class="p-4 font-semibold text-sm text-gray-600">#</th>
              <th class="p-4 font-semibold text-sm text-gray-600">
                Người gửi đơn
              </th>
              <th class="p-4 font-semibold text-sm text-gray-600">
                Tên doanh nghiệp
              </th>
              <th class="p-4 font-semibold text-sm text-gray-600">
                Loại dịch vụ
              </th>
              <th class="p-4 font-semibold text-sm text-gray-600">
                Mã số thuế
              </th>
              <th class="p-4 font-semibold text-sm text-gray-600 text-right">
                Hành động
              </th>
            </tr>
          </thead>

          <template v-for="(item, index) in applications" :key="item.id">
            <tbody class="border-t border-gray-200">
              <tr class="hover:bg-gray-50">
                <td class="p-4 text-sm text-gray-700">{{ index + 1 }}</td>
                <td class="p-4 text-sm text-gray-700">
                  <div class="font-medium">{{ item.userName }}</div>
                  <div class="text-xs text-gray-500">{{ item.userEmail }}</div>
                </td>
                <td class="p-4 text-sm text-gray-700">
                  {{ item.businessName }}
                </td>
                <td class="p-4 text-sm text-gray-700">
                  {{ item.serviceType }}
                </td>
                <td class="p-4 text-sm text-gray-700">{{ item.taxId }}</td>
                <td class="p-4 space-x-2 whitespace-nowrap text-right">
                  <button
                    class="bg-gray-100 text-gray-700 px-3 py-1 rounded-md hover:bg-gray-200 text-sm font-medium"
                    @click="toggleDetails(item.id)"
                  >
                    {{ expandedRowId === item.id ? "Ẩn" : "Xem" }}
                  </button>
                  <button
                    class="bg-green-500 text-white px-3 py-1 rounded-md hover:bg-green-600 text-sm font-medium"
                    @click="approve(item)"
                  >
                    Duyệt
                  </button>
                  <button
                    class="bg-red-500 text-white px-3 py-1 rounded-md hover:bg-red-600 text-sm font-medium"
                    @click="reject(item.id)"
                  >
                    Từ chối
                  </button>
                </td>
              </tr>

              <tr v-if="expandedRowId === item.id">
                <td colspan="6" class="p-4 bg-gray-50">
                  <div class="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
                    <div>
                      <strong class="block text-gray-500"
                        >Người liên hệ:</strong
                      >
                      <span>{{ item.contactPerson }}</span>
                    </div>
                    <div>
                      <strong class="block text-gray-500"
                        >Số điện thoại:</strong
                      >
                      <span>{{ item.businessPhone }}</span>
                    </div>
                    <div>
                      <strong class="block text-gray-500">Địa chỉ:</strong>
                      <span>{{ item.address }}</span>
                    </div>
                  </div>
                </td>
              </tr>
            </tbody>
          </template>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { SupplierApi } from "@/api/SupplierApi";

const loading = ref(true);
const applications = ref([]);
const expandedRowId = ref(null);

const fetchApplications = async () => {
  loading.value = true;
  try {
    const res = await SupplierApi.getPendingApplications();
    applications.value = res.data || [];
  } catch (err) {
    window.toast.error("Không thể tải danh sách đơn đăng ký.");
  } finally {
    loading.value = false;
  }
};

const approve = async (applicationItem) => {
  try {
    const id = applicationItem.id;
    const roleToAssign = applicationItem.serviceType;

    if (!roleToAssign) {
      window.toast.error("Lỗi: Loại dịch vụ (vai trò) không được để trống!");
      return;
    }
    await SupplierApi.approveApplication(id, roleToAssign);

    applications.value = applications.value.filter((a) => a.id !== id);
    window.toast.success("Đã duyệt đơn đăng ký thành công!");
  } catch (err) {
    window.toast.error("Duyệt đơn thất bại!");
  }
};

const reject = async (id) => {
  try {
    await SupplierApi.rejectApplication(id);
    applications.value = applications.value.filter((a) => a.id !== id);
    window.toast.success("Đã từ chối đơn đăng ký thành công!");
  } catch (err) {
    window.toast.error("Từ chối đơn thất bại!");
  }
};

const toggleDetails = (id) => {
  expandedRowId.value = expandedRowId.value === id ? null : id;
};

onMounted(() => {
  fetchApplications();
});
</script>
