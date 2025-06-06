import {createRouter, createWebHistory} from "vue-router";

import Home from "@/views/Home.vue";
import Tour from "@/views/Tour.vue";
import Bus from "@/views/Bus.vue";

import Plane from "@/components/Flight/FlightHome.vue";
import Hotel from "@/views/hotel/Hotel.vue";

import TourDetail from "@/views/TourDetail.vue";
import Login from "@/views/Login.vue";
import Register from "@/views/Register.vue";
import AccountView from "@/views/AccountView.vue";
import AccountDetails from "@/components/User/AccountDetails.vue";
import PaymentDetails from "@/components/User/PaymentDetails.vue";
import AccountSecurity from "@/components/User/AccountSecurity.vue";
import BookingPage from "@/views/BookingPage.vue";
import NotificationSetting from "@/components/User/NotificationSetting.vue";

import PayFlight from "@/components/Flight/PaymentPage.vue"
import AdminFight from "@/components/FlightAdmin/formAdminFlight.vue"
import GetTicket from "@/components/Flight/TicketReceipt.vue"

import HotelListingPage from "@/views/hotel/HotelListingPage.vue";
import HotelDetail from "@/views/hotel/HotelDetail.vue";
import HotelBooking from "@/views/hotel/HotelBooking.vue";
import BookingSuccess from "@/views/hotel/BookingSuccess.vue";



const routes = [
    {path: "/", name: "Home", component: Home},
    {path: "/tour", name: "Tour", component: Tour},
    {path: "/bus", name: "Bus", component: Bus},
    {path: "/plane", name: "Plane", component: Plane},

    {path: "/plane/pay", name: "PayFlight", component: PayFlight},
    {path: "/plane/getticket", name: "Get ticket", component: GetTicket},
<<<<<<< HEAD
    {path: "/plane/admin", name: "Get ticket", component: AdminFight},
=======

    {
    path: "/hotel",
    name: "Hotel",
    component: Hotel,
    children: [
      {
        path: '',
        name: 'HotelListing',
        component: HotelListingPage,
      },
      {
        path: ':id',
        name: 'HotelDetail',
        component: HotelDetail,
        props: true,
      },
      {
        path: ':id/booking',
        name: 'HotelBooking',
        component: HotelBooking,
        props: true,
      },
      {
        path: ':id/booking/success',
        name: 'BookingSuccess',
        component: BookingSuccess,
      }
    ],
  },

>>>>>>> 3abe751fdf1f1188774e80d14f8973404d96db28
    {
        path: "/tours/:id",
        name: "TourDetail",
        component: TourDetail,
        props: true,
    },
    {path: '/login', name: 'Login', component: Login},
    {path: '/register', name: 'Register', component: Register},
    {path: '/account', name: 'Account', component: AccountView},
    {
        path: '/account',
        component: AccountView,
        children: [
            {path: '', redirect: 'personal'},
            {path: 'personal', component: AccountDetails},
            {path: 'payment', component: PaymentDetails},
            {path: 'security', component: AccountSecurity},
            {path: 'notifications', component: NotificationSetting},
        ]
    },
    {
        path: "/booking",
        name: "BookingPage",
        component: BookingPage
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
