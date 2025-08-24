<template>
  <div class="bg-white p-6 rounded-xl shadow-lg relative">
    <div v-if="globalLoading" class="absolute inset-0 bg-white/60 backdrop-blur-sm z-10 flex items-center justify-center">
      <span class="animate-spin w-8 h-8 border-2 border-indigo-500 border-t-transparent rounded-full"></span>
      <span class="ml-3 text-gray-700 font-medium">Đang xử lý...</span>
    </div>
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-xl font-bold text-gray-700">Quản lý Sân bay</h2>
      <div class="flex gap-2">
        <input v-model="newAirportName" :disabled="globalLoading" placeholder="Tên sân bay" class="border rounded-lg px-3 py-2" />
        <button @click="addAirport" :disabled="globalLoading" class="bg-indigo-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-indigo-700 disabled:opacity-50">
          <i class="fas fa-plus mr-2"></i>Thêm
        </button>
      </div>
    </div>

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
            <input v-if="editingAirportId === airport.id" v-model="editingAirportName" :disabled="globalLoading" class="border rounded px-2 py-1 w-full" />
            <span v-else>{{ airport.name }}</span>
          </td>
          <td class="px-4 py-2 text-right">
            <button v-if="editingAirportId === airport.id" @click="saveAirport(airport)" :disabled="globalLoading" class="text-green-600 hover:underline mr-3 disabled:opacity-50">Lưu</button>
            <button v-if="editingAirportId === airport.id" @click="cancelEditAirport" :disabled="globalLoading" class="text-gray-600 hover:underline mr-3 disabled:opacity-50">Hủy</button>
            <button v-else @click="startEditAirport(airport)" :disabled="globalLoading" class="text-indigo-600 hover:underline mr-3 disabled:opacity-50">Sửa</button>
            <button @click="removeAirport(airport)" :disabled="globalLoading" class="text-red-600 hover:underline disabled:opacity-50">Xóa</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminAirports, createAdminAirport, updateAdminAirport, deleteAdminAirport } from '@/api/flightApi'

const airports = ref([])
const newAirportName = ref('')
const editingAirportId = ref(null)
const editingAirportName = ref('')
const globalLoading = ref(false)

async function loadAirports() {
  try {
    globalLoading.value = true
    const res = await getAdminAirports()
    airports.value = res.data
  } catch (error) {
    window.$toast?.('Không thể tải danh sách sân bay', 'error')
  } finally {
    globalLoading.value = false
  }
}

onMounted(async () => {
  await loadAirports()
})

async function addAirport() {
  if (!newAirportName.value.trim()) return
  try {
    globalLoading.value = true
    await createAdminAirport({ name: newAirportName.value.trim() })
    newAirportName.value = ''
    window.$toast?.('Thêm sân bay thành công', 'success')
    await loadAirports()
  } catch (error) {
    window.$toast?.(extractError(error, 'Thêm sân bay thất bại'), 'error')
  } finally {
    globalLoading.value = false
  }
}

function startEditAirport(airport) {
  editingAirportId.value = airport.id
  editingAirportName.value = airport.name
}

async function saveAirport(airport) {
  if (!editingAirportName.value.trim()) return
  try {
    globalLoading.value = true
    await updateAdminAirport(airport.id, { name: editingAirportName.value.trim() })
    editingAirportId.value = null
    editingAirportName.value = ''
    window.$toast?.('Cập nhật thành công', 'success')
    await loadAirports()
  } catch (error) {
    window.$toast?.(extractError(error, 'Cập nhật thất bại'), 'error')
  } finally {
    globalLoading.value = false
  }
}

function cancelEditAirport() {
  editingAirportId.value = null
  editingAirportName.value = ''
}

async function removeAirport(airport) {
  try {
    globalLoading.value = true
    await deleteAdminAirport(airport.id)
    window.$toast?.('Xóa thành công', 'success')
    await loadAirports()
  } catch (error) {
    const msg = isConstraintError(error)
      ? 'Không thể xóa vì đang được sử dụng'
      : extractError(error, 'Xóa thất bại')
    window.$toast?.(msg, 'error')
  } finally {
    globalLoading.value = false
  }
}

function isConstraintError(error) {
  const status = error?.response?.status
  const body = (error?.response?.data && JSON.stringify(error.response.data)) || ''
  const message = String(error?.message || '').toLowerCase()
  return status === 409 || /constraint|violat|foreign key|integrity/i.test(body) || /constraint|violat/i.test(message)
}

function extractError(error, fallback) {
  return error?.response?.data?.message || error?.message || fallback
}
</script>

<style scoped>
</style>


