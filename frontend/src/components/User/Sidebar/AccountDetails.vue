<template>
  <h2 class="text-2xl font-semibold text-gray-800 mb-5">Thông tin cá nhân</h2>

  <div class="space-y-6">
    <div v-for="(item, index) in infoItems" :key="item.label"
         class="flex items-center justify-between pb-4 border-b border-gray-100 last:border-b-0">
      <div class="flex items-start space-x-3">
        <i :class="item.icon + ' text-gray-500 text-lg mt-0.5 min-w-[20px] text-center'"></i>
        <div>
          <p class="text-sm text-gray-500 font-medium">{{ item.label }}</p>

          <!-- View Mode -->
          <p v-if="!item.editing" class="text-base text-gray-800 font-semibold mt-1">
            {{ item.label === 'Date of birth' ? formatDate(item.value) : item.value }}
          </p>

          <!-- Edit Mode -->
          <div v-else class="mt-1">
            <!-- Gender select -->
            <select v-if="item.label === 'Gender'" v-model="item.editValue"
                    class="border border-gray-300 rounded px-2 py-1 text-sm">
              <option value="Male">Male</option>
              <option value="Female">Female</option>
            </select>

            <!-- Date picker -->
            <input v-else-if="item.label === 'Date of birth'" type="date" v-model="item.editValue"
                   class="border border-gray-300 rounded px-2 py-1 text-sm" />

            <!-- Text input -->
            <input v-else type="text" v-model="item.editValue"
                   class="border border-gray-300 rounded px-2 py-1 text-sm w-64" />
          </div>
        </div>
      </div>

      <!-- Action buttons -->
      <div class="flex items-center space-x-2">
        <!-- View mode -->
        <button v-if="!item.editing" class="text-sm font-medium text-blue-600 hover:underline"
                @click="startEdit(index)">
          Edit
        </button>

        <!-- Edit mode -->
        <div v-else class="flex space-x-2">
          <button class="text-sm font-medium text-blue-800 hover:underline" @click="saveEdit(index)">
            Save
          </button>
          <button class="text-sm font-medium text-gray-600 hover:underline" @click="cancelEdit(index)">
            Cancel
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { AccountApi } from '@/api/AccountApi.js'

const infoItems = ref([])

onMounted(async () => {
  try {
    const response = await AccountApi.getProfile()
    const data = response.data
    infoItems.value = [
      {
        label: 'Full name',
        value: data.name,
        editValue: data.name,
        editing: false,
        icon: 'fas fa-user'
      },
      {
        label: 'Gender',
        value: data.gender || 'Not specified',
        editValue: data.gender || 'Not specified',
        editing: false,
        icon: 'fas fa-venus-mars'
      },
      {
        label: 'Date of birth',
        value: data.birthday,
        editValue: data.birthday,
        editing: false,
        icon: 'fas fa-birthday-cake'
      }
    ]
  } catch (err) {
    console.error('Lỗi khi tải profile:', err)
  }
})

function startEdit(index) {
  infoItems.value[index].editing = true
}

function cancelEdit(index) {
  const item = infoItems.value[index]
  item.editValue = item.value
  item.editing = false
}

function saveEdit(index) {
  const item = infoItems.value[index]
  item.value = item.editValue
  item.editing = false

  // Optionally send update to API here
  console.log('Updated item:', item.label, item.value)
}

function formatDate(dateStr) {
  if (!dateStr) return 'Not provided'
  const date = new Date(dateStr)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script>
