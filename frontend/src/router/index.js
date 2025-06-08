import { createRouter, createWebHistory } from "vue-router";

import Home from "../views/Home.vue";
import Tour from "../views/Tour.vue";
import Bus from "../views/Bus.vue";
import Plane from "../views/Plane.vue";
import Hotel from "../views/Hotel.vue";
import TourDetail from "../views/TourDetail.vue";
import BookingPage from "../views/BookingPage.vue";
import BusManagementLayout from "@/components/Bus/management_bus_component/BusManagementLayout.vue";
import MainLayout from "@/layouts/Main.vue";

const routes = [
  {
    path: "/",
    component: MainLayout,
    children: [
      { path: "", name: "Home", component: Home },
      { path: "tour", name: "Tour", component: Tour },
      { path: "bus", name: "Bus", component: Bus },
      { path: "plane", name: "Plane", component: Plane },
      { path: "hotel", name: "Hotel", component: Hotel },
      {
        path: "tours/:id",
        name: "TourDetail",
        component: TourDetail,
        props: true,
      },
      {
        path: "booking",
        name: "BookingPage",
        component: BookingPage
      },
    ]
  },
  { 
    path: "/bus-management", 
    name: "BusManagement", 
    component: BusManagementLayout 
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
