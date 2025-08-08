import { createRouter, createWebHistory } from "vue-router";

import Home from "@/views/Home.vue";
import Tour from "@/views/Tour.vue";
import Bus from "@/views/Bus.vue";
import Hotel from "@/views/hotel/Hotel.vue";
import Plane from "@/components/Flight/FlightHome.vue";
import FlightDetail from "@/components/Flight/FlightDetail.vue";
import TourDetail from "@/views/TourDetail.vue";
import PostRegistrationChoice from "../views/PostRegistrationChoice.vue";
import SupplierApplication from "../views/SupplierApplication.vue";
import BusManagementLayout from "@/components/Bus/management_bus_component/BusManagementLayout.vue";
import MainLayout from "@/layouts/Main.vue";
import ApproveSuppliers from "@/views/Admin/ApproveSuppliers.vue";
import AccountView from "@/views/AccountView.vue";
import ServiceReviews from "@/components/User/Sidebar/ServiceReviews.vue";
import AccountDetails from "@/components/User/Sidebar/AccountDetails.vue";
import BookingHistory from "@/components/User/Sidebar/BookingHistory.vue";
import PaymentDetails from "@/components/User/Sidebar/PaymentDetails.vue";
import AccountSecurity from "@/components/User/Sidebar/AccountSecurity.vue";
import BookingPage from "@/views/BookingPage.vue";
import NotificationSetting from "@/components/User/Sidebar/NotificationSetting.vue";

import PayFlight from "@/components/Flight/PaymentPage.vue";
import AdminFight from "@/components/FlightAdmin/FlightAdminLayout.vue";
import GetTicket from "@/components/Flight/TicketReceipt.vue";
import DetailFlightAdmin from "@/components/FlightAdmin/DetailFlightAdmin.vue";
import RevenueManagement from "@/views/admin/RevenueManagement.vue";
import Reports from "@/views/admin/Reports.vue";
import HotelManagement from "@/views/admin/HotelManagement.vue";
import FlightManagement from "@/views/admin/FlightManagement.vue";
import BusManagement from "@/views/admin/BusManagement.vue";

import HotelListingPage from "@/views/hotel/HotelListingPage.vue";
import HotelDetail from "@/views/hotel/HotelDetail.vue";
import HotelBooking from "@/views/hotel/HotelBooking.vue";
import BookingSuccess from "@/views/hotel/BookingSuccess.vue";
import AdminLayout from "@/components/Hotel/HotelAdmin/AdminLayout.vue";
import HotelForm from "@/views/hotel/admin/HotelForm.vue";
import Dashboard from "@/views/hotel/admin/Dashboard.vue";
import Customer from "@/views/hotel/admin/Customer.vue";
import Booking from "@/views/hotel/admin/Booking.vue";
import Review from "@/views/hotel/admin/Review.vue";
import AdminDashboard from "@/views/admin/Dashboard.vue";

import AdminLayoutSuper from "@/layouts/AdminLayout.vue";
import PromotionManagement from "@/views/admin/PromotionManagement.vue";
import UserManagement from "@/views/admin/UserManagement.vue";
import BookingManagement from "@/views/admin/BookingManagement.vue";
import TourManagement from "@/views/admin/TourManagement.vue";

import TourManager from "../layouts/TourManager.vue";
import Register from "@/views/Register.vue";
import Login from "@/views/Login.vue";

import CheckoutView from "../views/CheckoutView.vue";
import PaymentView from "../views/PaymentView.vue";
import MyTripsView from "../views/MyTripsView.vue";
import SuccessHold from "@/components/Flight/SuccessHold.vue";
import BankTransferForm from "@/components/Flight/BankTransferForm.vue";
import ForgotPassword from "@/views/ForgotPassword.vue";
import ResetPassword from "@/views/ResetPassword.vue";
import ExpiredLink from "@/views/ExpiredLink.vue";
import VerifyEmail from "@/views/VerifyEmail.vue";
import OAuth2LoginSuccessView from "@/views/OAuth2LoginSuccessView.vue";
import Unauthorized from "@/views/Unauthorized.vue";

const routes = [
  {
    path: "/unauthorized",
    name: "Unauthorized",
    component: Unauthorized,
  },
  {
    path: "/",
    component: MainLayout,
    children: [
      { path: "", name: "Home", component: Home },
      {
        path: "/post-registration-choice",
        name: "PostRegistrationChoice",
        component: PostRegistrationChoice,
      },
      {
        path: "/supplier-application",
        name: "SupplierApplication",
        component: SupplierApplication,
      },
      { path: "register", name: "Register", component: Register },
      { path: "login", name: "Login", component: Login },
      {
        path: "forgot-password",
        name: "ForgotPassword",
        component: ForgotPassword,
      },
      {
        path: "reset-password",
        name: "ResetPassword",
        component: ResetPassword,
      },
      { path: "expired-link", name: "ExpiredLink", component: ExpiredLink },
      { path: "verify-email", name: "VerifyEmail", component: VerifyEmail },
      { path: "tour", name: "Tour", component: Tour },
      { path: "bus", name: "Bus", component: Bus },
      { path: "plane", name: "Plane", component: Plane },
      {
        path: "plane/:id",
        name: "FlightDetail",
        component: FlightDetail,
        props: true,
      },

      {
        path: "/orders/:id",
        name: "order-detail",
        component: () => import("../views/OrderDetail.vue"),
      },
      {
        path: "/checkout/:id",
        name: "checkout",
        component: CheckoutView,
      },
      {
        path: "/flight/successhold/:id",
        name: "SuccessHold",
        component: SuccessHold,
      },
      {
        path: "/payment/:orderId",
        name: "payment",
        component: PaymentView,
      },
      {
        path: "/my-trips",
        name: "my-trips",
        component: MyTripsView,
      },
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
        path: "/account",
        component: AccountView,
        children: [
          { path: "", redirect: "personal" },
          { path: "personal", component: AccountDetails },
          { path: "bookings", component: BookingHistory },
          { path: "payment", component: PaymentDetails },
          { path: "security", component: AccountSecurity },
          { path: "notifications", component: NotificationSetting },
          {
            path: "reviews",
            name: "AccountReviews",
            component: ServiceReviews,
          },
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
    path: "/oauth2/login/success",
    name: "OAuth2LoginSuccessView",
    component: OAuth2LoginSuccessView,
  },
  {
    path: "/plane/admin",
    name: "AdminFight",
    component: AdminFight,
    children: [
      {
        path: "flights/:id",
        name: "DetailFlightAdmin",
        component: DetailFlightAdmin,
      },
    ],
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
      { path: "customer", component: Customer },
      { path: "booking", component: Booking },
      { path: "review", component: Review },
      { path: "", redirect: "dashboard" },
    ],
  },

  {
    path: "/admin",
    component: AdminLayoutSuper,
    redirect: "/admin/dashboard",

    children: [
      // Route cho dashboard chung
      {
        path: "dashboard",
        name: "AdminDashboard",
        component: AdminDashboard,
      },

      {
        path: "hotels",
        name: "AdminHotels",
        component: HotelManagement,
      },
      {
        path: "flights",
        name: "AdminFlights",
        component: FlightManagement,
      },
      {
        path: "buses",
        name: "AdminBuses",
        component: BusManagement,
      },
      {
        path: "promotions",
        name: "AdminPromotions",
        component: PromotionManagement,
      },
      {
        path: "users",
        name: "AdminUsers",
        component: UserManagement,
      },
      {
        path: "bookings",
        name: "AdminBookings",
        component: BookingManagement,
      },
      {
        path: "revenue",
        name: "AdminRevenue",
        component: RevenueManagement,
      },
      {
        path: "reports",
        name: "AdminReports",
        component: Reports,
      },
      {
        path: "tours",
        name: "AdminTours",
        component: TourManagement,
      },
      {
        path: "/admin/approve-suppliers",
        name: "ApproveSuppliers",
        component: ApproveSuppliers,
      },
    ],
  },

  {
    path: "/bank-transfer-form",
    name: "BankTransferForm",
    component: BankTransferForm,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
