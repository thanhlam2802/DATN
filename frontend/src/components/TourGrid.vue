<template>
  <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
    <TourCard
      v-for="tour in tours"
      :key="tour.id"
      :imageUrl="tour.imageUrl"
      :title="tour.title"
      :location="tour.location"
      :location-detail="tour.locationDetail"
      :price="tour.price"
      :original-price="tour.originalPrice"
      :rating="tour.rating"
      @click="goToDetail(tour.id)"
    />
  </div>
</template>

<script setup>
import { ref } from "vue";
import TourCard from "./TourCard.vue";
import { useRouter } from "vue-router";
const router = useRouter();

const goToDetail = (id) => {
  router.push(`/tours/${id}`);
};
const tours = ref([
  {
    id: 1,
    imageUrl: "",
    title: "Chuyến tham quan trong ngày | Khám phá Phú Yên - Chính phủ",
    location: "Xã Hòa Tâm",
    locationDetail: "Đông Hòa",
    price: "742.383",
    originalPrice: "",
  },
  {
    id: 2,
    imageUrl:
      "https://images.unsplash.com/photo-1583579262521-0fe89b9d013c?q=80&w=1000",
    title: "Sagrada Familia Skip-the-Line Guided Tour",
    location: "Eixample",
    locationDetail: "Barcelona",
    price: "993.264",
    originalPrice: "",
    rating: {
      score: "9.0",
      reviews: "6",
    },
  },
  {
    id: 3,
    imageUrl:
      "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?q=80&w=1000",
    title: "Tour 1 Ngày Ghép Xe Hồ Nhật Nguyệt & Nông Trại Qingjing",
    location: "Hồ Nhật Nguyệt",
    locationDetail: "Nam Đầu",
    price: "949.203",
    originalPrice: "949.204",
  },
  {
    id: 4,
    imageUrl:
      "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=1000",
    title: "Resort với hồ bơi vô cực",
    location: "Đà Nẵng",
    locationDetail: "Việt Nam",
    price: "850.000",
    originalPrice: "",
  },
  {
    id: 5,
    imageUrl:
      "https://images.unsplash.com/photo-1544551763-46a013bb70d5?q=80&w=1000",
    title: "Khám phá thế giới đại dương",
    location: "Nha Trang",
    locationDetail: "Khánh Hòa",
    price: "620.000",
    originalPrice: "680.000",
  },
  {
    id: 6,
    imageUrl:
      "https://images.unsplash.com/photo-1559592413-7cec4d0cae2b?q=80&w=1000",
    title: "Tour khám phá rùa biển",
    location: "Côn Đảo",
    locationDetail: "Bà Rịa - Vũng Tàu",
    price: "1.250.000",
    originalPrice: "",
  },
]);
</script>
