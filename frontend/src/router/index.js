import { createRouter, createWebHistory } from "vue-router";

import Home from "@/views/Home.vue";
import Tour from "@/views/Tour.vue";
import Bus from "@/views/Bus.vue";
import Hotel from "@/views/hotel/Hotel.vue";
import Plane from "@/components/Flight/FlightHome.vue";
import TourDetail from "@/views/TourDetail.vue";

import BusManagementLayout from "@/components/Bus/management_bus_component/BusManagementLayout.vue";
import MainLayout from "@/layouts/Main.vue";

import Login from "@/views/Login.vue";
import Register from "@/views/Register.vue";
import AccountView from "@/views/AccountView.vue";
import AccountDetails from "@/components/User/Sidebar/AccountDetails.vue";
import PaymentDetails from "@/components/User/Sidebar/PaymentDetails.vue";
import AccountSecurity from "@/components/User/Sidebar/AccountSecurity.vue";
import BookingPage from "@/views/BookingPage.vue";
import NotificationSetting from "@/components/User/Sidebar/NotificationSetting.vue";

import PayFlight from "@/components/Flight/PaymentPage.vue";
import AdminFight from "@/components/FlightAdmin/formAdminFlight.vue";
import GetTicket from "@/components/Flight/TicketReceipt.vue";

import HotelListingPage from "@/views/hotel/HotelListingPage.vue";
import HotelDetail from "@/views/hotel/HotelDetail.vue";
import HotelBooking from "@/views/hotel/HotelBooking.vue";
import BookingSuccess from "@/views/hotel/BookingSuccess.vue";
import AdminLayout from "@/components/Hotel/HotelAdmin/AdminLayout.vue";
import Dashboard from "@/views/hotel/admin/Dashboard.vue";
import HotelForm from "@/views/hotel/admin/HotelForm.vue";
import TourManager from "@/layouts/TourManager.vue";

const routes = [
  {
    path: "/",
    component: MainLayout,
    children: [
      { path: "", name: "Home", component: Home },
      { path: "tour", name: "Tour", component: Tour },
      { path: "bus", name: "Bus", component: Bus },
      { path: "plane", name: "Plane", component: Plane },
      {
        path: "hotel",
        name: "Hotel",
        component: Hotel,
      },
      {
        path: "hotel/listing",
        name: "HotelListing",
        component: HotelListingPage,
      },
      {
        path: "hotel/:id",
        name: "HotelDetail",
        component: HotelDetail,
        props: true,
      },
      {
        path: "hotel/:id/booking",
        name: "HotelBooking",
        component: HotelBooking,
        props: true,
      },
      {
        path: "hotel/:id/booking/success",
        name: "BookingSuccess",
        component: BookingSuccess,
      },
      {
        path: "tours/:id",
        name: "TourDetail",
        component: TourDetail,
        props: true,
      },
      {
        path: "booking",
        name: "BookingPage",
        component: BookingPage,
      },
      { path: "/login", name: "Login", component: Login },
      { path: "/register", name: "Register", component: Register },
      {
        path: "/account",
        component: AccountView,
        children: [
          { path: "", redirect: "personal" },
          { path: "personal", component: AccountDetails },
          { path: "payment", component: PaymentDetails },
          { path: "security", component: AccountSecurity },
          { path: "notifications", component: NotificationSetting },
        ],
      },
    ],
  },
  {
    path: "/bus-management",
    name: "BusManagement",
    component: BusManagementLayout,
  },

  {
    path: "/plane/pay",
    name: "PayFlight",
    component: PayFlight,
  },
  {
    path: "/plane/getticket",
    name: "GetTicket",
    component: GetTicket,
  },
  {
    path: "/plane/admin",
    name: "AdminFight",
    component: AdminFight,
  },

  {
    path: "/tour/admin",
    name: "TourManager",
    component: TourManager,
  },

  {
    path: "/hotel/admin",
    component: AdminLayout,
    children: [
      { path: "dashboard", component: Dashboard },
      { path: "hotelform", component: HotelForm },
      { path: "", redirect: "dashboard" },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
