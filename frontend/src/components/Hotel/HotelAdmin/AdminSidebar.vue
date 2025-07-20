<template>
    <aside class="w-64 bg-white flex flex-col min-h-screen fixed left-0 top-0 bottom-0 z-20 border-r border-slate-200">
        <div class="p-6 border-b border-slate-200">
            <div class="flex items-center">
                <i class="fas fa-hotel text-blue-600 text-2xl mr-3"></i>
                <span class="text-xl font-bold text-slate-800">Hotel Admin</span>
            </div>
        </div>
        <nav class="p-6 flex-grow space-y-4">
            <router-link to="/hotel/admin/dashboard"
                class="flex items-center space-x-3 p-3 rounded-lg transition-colors duration-200" :class="isActive('/hotel/admin/dashboard')
                    ? 'text-blue-600 bg-slate-100 font-semibold'
                    : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900 font-medium'">
                <i class="fas fa-tachometer-alt w-5 text-center"></i>
                <span>Dashboard</span>
            </router-link>
            <router-link to="/hotel/admin/hotelform"
                class="flex items-center space-x-3 p-3 rounded-lg transition-colors duration-200" :class="isActive('/hotel/admin/hotelform')
                    ? 'text-blue-600 bg-slate-100 font-semibold'
                    : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900 font-medium'">
                <i class="fas fa-building w-5 text-center"></i>
                <span>Hotels</span>
            </router-link>
            <router-link to="/hotel/admin/bookings"
                class="flex items-center space-x-3 p-3 rounded-lg transition-colors duration-200" :class="isActive('/hotel/admin/bookings')
                    ? 'text-blue-600 bg-slate-100 font-semibold'
                    : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900 font-medium'">
                <i class="fas fa-calendar-alt w-5 text-center"></i>
                <span>Bookings</span>
            </router-link>
            <router-link to="/hotel/admin/customers"
                class="flex items-center space-x-3 p-3 rounded-lg transition-colors duration-200" :class="isActive('/hotel/admin/customers')
                    ? 'text-blue-600 bg-slate-100 font-semibold'
                    : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900 font-medium'">
                <i class="fas fa-users w-5 text-center"></i>
                <span>Customers</span>
            </router-link>
            <router-link to="/hotel/admin/reviews"
                class="flex items-center space-x-3 p-3 rounded-lg transition-colors duration-200" :class="isActive('/hotel/admin/reviews')
                    ? 'text-blue-600 bg-slate-100 font-semibold'
                    : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900 font-medium'">
                <i class="fas fa-star w-5 text-center"></i>
                <span>Reviews</span>
            </router-link>
            <router-link to="/hotel/admin/settings"
                class="flex items-center space-x-3 p-3 rounded-lg transition-colors duration-200" :class="isActive('/hotel/admin/settings')
                    ? 'text-blue-600 bg-slate-100 font-semibold'
                    : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900 font-medium'">
                <i class="fas fa-cog w-5 text-center"></i>
                <span>Settings</span>
            </router-link>
        </nav>
    </aside>
</template>

<script setup>
import { useRoute } from 'vue-router'
const route = useRoute()
const isActive = (path) => route.path === path
</script>
