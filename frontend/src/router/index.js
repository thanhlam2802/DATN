import { createRouter, createWebHistory } from "vue-router";

import Home from "../views/Home.vue";
import Tour from "../views/Tour.vue";
import Car from "../views/Car.vue";
import Plane from "../views/Plane.vue";
import Hotel from "../views/Hotel.vue";
import TourDetail from "../views/TourDetail.vue";
const routes = [
  { path: "/", name: "Home", component: Home },
  { path: "/tour", name: "Tour", component: Tour },
  { path: "/car", name: "Car", component: Car },
  { path: "/plane", name: "Plane", component: Plane },
  { path: "/hotel", name: "Hotel", component: Hotel },
  {
    path: "/tours/:id",
    name: "TourDetail",
    component: TourDetail,
    props: true,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
