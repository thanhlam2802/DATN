<template>
    <main class="flex-grow bg-white rounded-xl shadow-xl overflow-hidden">
        <header class="bg-transparent border-b border-slate-200 py-4 px-8 flex justify-between items-center z-10">
            <div>
                <h1 class="text-2xl font-bold text-slate-800">Dashboard</h1>
                <p class="text-sm text-slate-500 mt-1">Welcome back, Admin. Here's what's happening.</p>
            </div>
            <div class="flex items-center space-x-6">
                <button
                    class="text-slate-500 hover:text-blue-600 hover:bg-slate-100 p-2 rounded-full transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                    <i class="fas fa-bell text-lg"></i>
                </button>
                <div class="flex items-center space-x-3">
                    <img src="https://readdy.ai/api/search-image?query=professional%20headshot%20of%20a%20hotel%20manager%2C%20business%20attire%2C%20confident%20smile%2C%20neutral%20background%2C%20high%20quality%20portrait%2C%20corporate%20style&width=40&height=40&seq=9&orientation=squarish"
                        alt="Admin Avatar"
                        class="w-10 h-10 rounded-full object-cover border-2 border-white ring-2 ring-slate-200" />
                    <div>
                        <span class="text-slate-800 font-semibold">John Doe</span>
                        <p class="text-xs text-slate-500">Administrator</p>
                    </div>
                </div>
            </div>
        </header>

        <div class="p-8">
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                <div
                    class="bg-white rounded-xl shadow-md p-6 border border-slate-200 transition-all duration-300 hover:shadow-lg hover:-translate-y-1">
                    <div class="flex items-start justify-between mb-4">
                        <div class="text-slate-600 font-semibold">Total Bookings</div>
                        <div class="bg-blue-50 p-3 rounded-full">
                            <i class="fas fa-calendar-check text-blue-500 text-xl"></i>
                        </div>
                    </div>
                    <div class="text-3xl font-bold text-slate-900">1,257</div>
                    <div class="text-green-600 text-sm mt-2 flex items-center">
                        <i class="fas fa-arrow-up mr-1 text-xs"></i>
                        <span>+12.5% from last month</span>
                    </div>
                </div>
                <div
                    class="bg-white rounded-xl shadow-md p-6 border border-slate-200 transition-all duration-300 hover:shadow-lg hover:-translate-y-1">
                    <div class="flex items-start justify-between mb-4">
                        <div class="text-slate-600 font-semibold">Revenue</div>
                        <div class="bg-green-50 p-3 rounded-full">
                            <i class="fas fa-dollar-sign text-green-500 text-xl"></i>
                        </div>
                    </div>
                    <div class="text-3xl font-bold text-slate-900">$45,257</div>
                    <div class="text-green-600 text-sm mt-2 flex items-center">
                        <i class="fas fa-arrow-up mr-1 text-xs"></i>
                        <span>+8.3% from last month</span>
                    </div>
                </div>
                <div
                    class="bg-white rounded-xl shadow-md p-6 border border-slate-200 transition-all duration-300 hover:shadow-lg hover:-translate-y-1">
                    <div class="flex items-start justify-between mb-4">
                        <div class="text-slate-600 font-semibold">Active Hotels</div>
                        <div class="bg-purple-50 p-3 rounded-full">
                            <i class="fas fa-hotel text-purple-500 text-xl"></i>
                        </div>
                    </div>
                    <div class="text-3xl font-bold text-slate-900">36</div>
                    <div class="text-blue-600 text-sm mt-2 flex items-center">
                        <i class="fas fa-plus mr-1 text-xs"></i>
                        <span>2 new this month</span>
                    </div>
                </div>
                <div
                    class="bg-white rounded-xl shadow-md p-6 border border-slate-200 transition-all duration-300 hover:shadow-lg hover:-translate-y-1">
                    <div class="flex items-start justify-between mb-4">
                        <div class="text-slate-600 font-semibold">Reviews</div>
                        <div class="bg-yellow-50 p-3 rounded-full">
                            <i class="fas fa-star text-yellow-500 text-xl"></i>
                        </div>
                    </div>
                    <div class="text-3xl font-bold text-slate-900">4.8</div>
                    <div class="text-green-600 text-sm mt-2 flex items-center">
                        <i class="fas fa-arrow-up mr-1 text-xs"></i>
                        <span>+0.2 from last month</span>
                    </div>
                </div>
            </div>

            <div class="bg-white rounded-xl shadow-md border border-slate-200">
                <div class="p-6 border-b border-slate-200">
                    <h2 class="text-xl font-bold text-slate-800">Recent Bookings</h2>
                </div>
                <div class="overflow-x-auto">
                    <table class="min-w-full divide-y divide-slate-200">
                        <thead class="bg-slate-50">
                            <tr>
                                <th
                                    class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">
                                    Guest</th>
                                <th
                                    class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">
                                    Hotel</th>
                                <th
                                    class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">
                                    Dates</th>
                                <th
                                    class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">
                                    Status</th>
                                <th
                                    class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">
                                    Amount</th>
                                <th
                                    class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">
                                    Actions</th>
                            </tr>
                        </thead>
                        <tbody class="bg-white divide-y divide-slate-100">
                            <tr v-for="booking in recentBookings" :key="booking.id"
                                class="hover:bg-slate-50 transition-colors duration-150 align-middle">
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <div class="flex items-center">
                                        <img :src="booking.guestImage" :alt="booking.guestName"
                                            class="w-10 h-10 rounded-full object-cover mr-4 border border-slate-200" />
                                        <span class="text-slate-800 font-medium">{{ booking.guestName }}</span>
                                    </div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-slate-600">{{ booking.hotel }}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-slate-600">
                                    <div>{{ booking.checkIn }}</div>
                                    <div class="text-xs text-slate-400">to {{ booking.checkOut }}</div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <span :class="getStatusClass(booking.status)"
                                        class="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold leading-tight">
                                        {{ booking.status }}
                                    </span>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-slate-800 font-semibold">{{
                                    booking.amount }}</td>
                                <td class="px-6 py-4 whitespace-nowrap text-center">
                                    <button
                                        class="text-slate-400 hover:text-blue-600 p-2 rounded-full transition-colors duration-200">
                                        <i class="fas fa-ellipsis-v"></i>
                                    </button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
</template>

<script setup>
import { ref } from 'vue';
const recentBookings = ref([
    {
        id: 1,
        guestName: 'Sarah Johnson',
        guestImage: 'https://randomuser.me/api/portraits/men/46.jpg',
        hotel: 'Grand Hotel Luxury',
        checkIn: 'Jun 1, 2025',
        checkOut: 'Jun 5, 2025',
        status: 'Confirmed',
        amount: '$1,200'
    },
    {
        id: 2,
        guestName: 'Michael Chen',
        guestImage: 'https://randomuser.me/api/portraits/men/21.jpg',
        hotel: 'Seaside Resort',
        checkIn: 'Jun 3, 2025',
        checkOut: 'Jun 7, 2025',
        status: 'Pending',
        amount: '$850'
    },
    {
        id: 3,
        guestName: 'Emma Davis',
        guestImage: 'https://randomuser.me/api/portraits/women/12.jpg',
        hotel: 'Mountain View Lodge',
        checkIn: 'Jun 5, 2025',
        checkOut: 'Jun 8, 2025',
        status: 'Completed',
        amount: '$650'
    },
    {
        id: 4,
        guestName: 'James Wilson',
        guestImage: 'https://randomuser.me/api/portraits/men/13.jpg',
        hotel: 'City Center Hotel',
        checkIn: 'Jun 6, 2025',
        checkOut: 'Jun 9, 2025',
        status: 'Cancelled',
        amount: '$920'
    }
]);

const getStatusClass = (status) => {
    const classes = {
        'Confirmed': 'bg-green-100 text-green-700',
        'Pending': 'bg-yellow-100 text-yellow-700',
        'Completed': 'bg-blue-100 text-blue-700',
        'Cancelled': 'bg-red-100 text-red-700'
    };
    return classes[status] || 'bg-slate-100 text-slate-700';
};
</script>