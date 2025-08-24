<script setup>
import { computed } from "vue";

const props = defineProps({
  order: {
    type: Object,
    required: true,
  },
});

// Helper function để định dạng giá tiền
const formatPrice = (price) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    price || 0
  );

// Helper function để định dạng ngày
const formatDate = (dateString) => {
  if (!dateString) return "";
  return new Date(dateString).toLocaleDateString("en-GB", {
    day: "2-digit",
    month: "long",
    year: "numeric",
  });
};

const lineItems = computed(() => {
  if (!props.order) return [];
  const items = [];

  props.order.tourBookings?.forEach((tour) => {
    items.push({
      name: `Tour: ${tour.tourName}`,
      quantity: 1,
      unitPrice: tour.totalPrice,
      total: tour.totalPrice,
    });
  });

  props.order.flightBookings?.forEach((flight) => {
    items.push({
      name: `Chuyến bay: ${flight.flight.name} (${flight.flight.flightNumber})`,
      quantity: 1,
      unitPrice: flight.totalPrice,
      total: flight.totalPrice,
    });
  });

  // Thêm hotel bookings vào danh sách
  props.order.hotelBookings?.forEach((hotel) => {
    items.push({
      name: `Khách sạn: ${hotel.hotelName} (${hotel.roomType})`,
      quantity: hotel.numberOfRooms || hotel.rooms || 1,
      unitPrice: hotel.totalPrice / (hotel.numberOfRooms || hotel.rooms || 1),
      total: hotel.totalPrice,
    });
  });

  return items;
});
</script>

<template>
  <div id="invoice-printable-area" class="font-sans bg-white p-8 text-gray-800">
    <div class="grid grid-cols-2 items-start mb-12">
      <div class="flex items-center gap-4">
        <div class="w-16 h-16 bg-black rounded-full"></div>
        <div>
          <p class="font-bold text-lg">BILLED TO:</p>
          <p>{{ order.user?.fullName || "Customer Name" }}</p>
          <p>{{ order.user?.phone || "+123-456-7890" }}</p>
          <p>{{ order.user?.address || "Customer Address Here" }}</p>
        </div>
      </div>

      <div class="text-right">
        <h1 class="text-5xl font-light tracking-widest">INVOICE</h1>
        <p class="mt-2">Invoice No. {{ order.transactionId || order.id }}</p>
        <p>{{ formatDate(order.payDate || order.createdAt) }}</p>
      </div>
    </div>

    <table class="w-full text-left border-collapse">
      <thead>
        <tr class="border-b-2 border-black">
          <th class="py-2 font-normal">Item</th>
          <th class="py-2 font-normal text-center">Quantity</th>
          <th class="py-2 font-normal text-right">Unit Price</th>
          <th class="py-2 font-normal text-right">Total</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(item, index) in lineItems"
          :key="index"
          class="border-b border-gray-300"
        >
          <td class="py-4">{{ item.name }}</td>
          <td class="py-4 text-center">{{ item.quantity }}</td>
          <td class="py-4 text-right">{{ formatPrice(item.unitPrice) }}</td>
          <td class="py-4 text-right">{{ formatPrice(item.total) }}</td>
        </tr>
      </tbody>
    </table>

    <div class="w-full flex justify-end mt-8">
      <div class="w-1/2">
        <div class="flex justify-between py-2">
          <span>Subtotal</span>
          <span>{{ formatPrice(order.originalAmount || order.amount) }}</span>
        </div>
        <div class="flex justify-between py-2">
          <span>Tax (0%)</span>
          <span>{{ formatPrice(0) }}</span>
        </div>
        <div
          class="flex justify-between py-4 border-t-2 border-black font-bold text-xl"
        >
          <span>Total Due</span>
          <span>{{ formatPrice(order.amount) }}</span>
        </div>
      </div>
    </div>

    <div class="mt-16 text-center">
      <p class="font-semibold text-lg">Thank you for your Business!</p>
    </div>

    <div class="mt-8 border-t pt-8 grid grid-cols-2">
      <div>
        <p class="font-bold">PAYMENT INFORMATION</p>
        <p>Bank Name: Your Bank Name</p>
        <p>Account Name: Your Business Name</p>
        <p>Account No.: 987-654-321</p>
      </div>
      <div class="text-right">
        <p class="font-bold">Your Business Name</p>
        <p>987 Anywhere St., Any City, Any State 987655</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Font family có thể cần được import vào dự án của bạn nếu chưa có */
.font-sans {
  font-family: "Inter", sans-serif;
}
</style>
