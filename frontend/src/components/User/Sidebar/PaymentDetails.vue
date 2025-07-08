<template>
  <div>
    <h2 class="text-2xl font-semibold text-gray-800 mb-4">Phương thức thanh toán chính</h2>
    <div v-if="primaryCard"
         class="flex items-center justify-between p-5 rounded-xl bg-blue-100 border border-blue-300 shadow-sm">
      <div class="flex items-center gap-4">
        <div class="w-12 h-8 flex items-center justify-center bg-white rounded shadow-inner">
          <i class="fab fa-cc-visa text-2xl text-blue-600" v-if="primaryCard.brand === 'Visa'"></i>
          <i class="fab fa-cc-mastercard text-2xl text-red-600"
             v-else-if="primaryCard.brand === 'MasterCard'"></i>
          <i class="fas fa-credit-card text-2xl text-gray-600" v-else></i>
        </div>
        <div>
          <p class="text-base font-medium text-gray-900">
            {{ primaryCard.brand }} **** {{ primaryCard.last4 }}
          </p>
          <p class="text-sm text-gray-600">Hết hạn {{ primaryCard.expiry }}</p>
        </div>
      </div>
      <span
          class="text-sm font-semibold text-blue-700 bg-white px-3 py-1 rounded-lg border border-blue-300">Mặc
                    định</span>
    </div>
    <p v-else class="text-sm text-gray-500 mt-2">Chưa có phương thức thanh toán chính.</p>
  </div>

  <div class="mt-5">
    <h3 class="text-2xl font-semibold text-gray-800 mb-4">Danh sách thẻ đã lưu</h3>
    <div v-if="cards.length" class="space-y-4">
      <div v-for="(card, index) in cards" :key="card.id"
           class="flex items-center justify-between p-5 rounded-xl bg-white border border-gray-200 shadow-sm hover:shadow-md transition">
        <div class="flex items-center gap-4">
          <div class="w-10 h-8 flex items-center justify-center bg-gray-100 rounded">
            <i class="fab fa-cc-visa text-xl text-blue-600" v-if="card.brand === 'Visa'"></i>
            <i class="fab fa-cc-mastercard text-xl text-red-600"
               v-else-if="card.brand === 'MasterCard'"></i>
            <i class="fas fa-credit-card text-xl text-gray-600" v-else></i>
          </div>
          <div>
            <p class="text-base font-medium text-gray-900">{{ card.brand }} **** {{ card.last4 }}</p>
            <p class="text-sm text-gray-600">Hết hạn {{ card.expiry }}</p>
          </div>
        </div>
        <div class="flex gap-3">
          <button v-if="!card.primary" @click="setAsPrimary(index)"
                  class="text-sm text-blue-600 font-medium hover:underline">Đặt làm mặc định
          </button>
          <button @click="removeCard(index)"
                  class="text-sm text-red-600 font-medium hover:underline">Xóa
          </button>
        </div>
      </div>
    </div>
    <p v-else class="text-sm text-gray-500 mt-2">Không có thẻ nào được lưu.</p>
  </div>

  <div v-if="showAddForm"
       class="p-6 bg-white border border-gray-200 rounded-xl shadow-md space-y-6 transition duration-300">
    <h3 class="text-xl font-semibold text-gray-800">Thêm phương thức thanh toán</h3>
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Loại thẻ</label>
        <select v-model="newCard.brand"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none">
          <option value="Visa">Visa</option>
          <option value="MasterCard">MasterCard</option>
        </select>
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">4 số cuối</label>
        <input type="text" v-model="newCard.last4" maxlength="4"
               class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"/>
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Ngày hết hạn</label>
        <input type="text" v-model="newCard.expiry" placeholder="MM/YY"
               class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"/>
      </div>
    </div>
    <div class="flex justify-end gap-3 pt-2">
      <button @click="confirmAddCard"
              class="px-5 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg shadow-sm transition">
        Lưu
      </button>
      <button @click="cancelAddCard"
              class="px-5 py-2 bg-gray-200 hover:bg-gray-300 text-gray-800 rounded-lg transition">
        Hủy
      </button>
    </div>
  </div>

  <div v-else class="pt-4">
    <button @click="showAddForm = true"
            class="px-5 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-xl shadow transition duration-200">
      + Thêm phương thức thanh toán
    </button>
  </div>
</template>

<script>
export default {
  name: "PaymentDetails",
  data() {
    return {
      cards: [
        {id: 1, brand: "Visa", last4: "1234", expiry: "12/26", primary: true},
        {id: 2, brand: "MasterCard", last4: "5678", expiry: "05/25", primary: false}
      ],
      showAddForm: false,
      newCard: {
        brand: "Visa",
        last4: "",
        expiry: ""
      }
    };
  },
  computed: {
    primaryCard() {
      return this.cards.find(card => card.primary);
    }
  },
  methods: {
    setAsPrimary(index) {
      this.cards.forEach((c, i) => {
        c.primary = i === index;
      });
    },
    removeCard(index) {
      if (this.cards[index].primary) {
        this.cards.splice(index, 1);
        if (this.cards.length) this.cards[0].primary = true;
      } else {
        this.cards.splice(index, 1);
      }
    },
    confirmAddCard() {
      if (this.newCard.last4.length !== 4 || !this.newCard.expiry) {
        alert("Vui lòng nhập đủ thông tin thẻ.");
        return;
      }
      const newId = this.cards.length + 1;
      this.cards.push({
        id: newId,
        brand: this.newCard.brand,
        last4: this.newCard.last4,
        expiry: this.newCard.expiry,
        primary: this.cards.length === 0
      });
      this.resetForm();
    },
    cancelAddCard() {
      this.resetForm();
    },
    resetForm() {
      this.newCard = {brand: "Visa", last4: "", expiry: ""};
      this.showAddForm = false;
    }
  }
};
</script>

<style scoped></style>
