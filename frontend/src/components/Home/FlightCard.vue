<template>
    <router-link :to="{ name: 'FlightDetail', params: { id: flight.id } }" class="w-[350px] h-[350px] flex-shrink-0 rounded-2xl bg-white overflow-hidden shadow-2xl transition-all duration-300 scale-[0.95]">
        <div class="relative h-48 overflow-hidden">
            <img :src="flight.images[0].imageUrl" :alt="flight.name"
                class="w-full h-full object-cover object-center transition-transform duration-300" />
        </div>
        <div class="p-4 space-y-2">
            <h3 class="font-bold text-xl text-gray-800 truncate">{{ flight.name }}</h3>
            <div class=" text-sm">
                <span class="text-gray-600">Mã chuyến: <strong>{{ flight.flightNumber }}</strong></span>
                <br>
                <span class="text-blue-600 font-bold text-base whitespace-nowrap">
                  {{ priceDisplay }}
                </span>
            </div>
            <div class="text-gray-600 text-sm flex items-center space-x-4">
                <div class="flex items-center space-x-1">
                    <i class="fas fa-plane-departure text-blue-500"></i>
                    <span>{{ formatDate(flight.departureTime)+ ' ' +formatTime(flight.departureTime) }}</span>
                </div>
                <div class="flex items-center space-x-1">
                <span class="block">Số ghế còn: {{ flight.totalAvailableSeats }}</span>
            </div>
            </div>
            
        </div>
        <div class="px-4 pb-4 flex justify-end">
        </div>
    </router-link>
</template>

<script>
export default {
    name: "FlightCard",
    props: {
        flight: {
            type: Object,
            required: true,
        },
    },
    computed: {
        imageUrl() {
            return this.flight.images[0].url;
        },
        priceDisplay() {
            if (this.flight.minPrice && this.flight.maxPrice && this.flight.minPrice !== this.flight.maxPrice) {
                return this.formatCurrency(this.flight.minPrice) + ' - ' + this.formatCurrency(this.flight.maxPrice);
            } else if (this.flight.minPrice) {
                return this.formatCurrency(this.flight.minPrice);
            } else {
                return 'Liên hệ';
            }
        },
    },
    methods: {
        formatCurrency(val) {
            if (!val) return '';
            return Number(val).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
        },
        formatTime(val) {
            if (!val) return '';
            // Giả sử val là ISO string
            const d = new Date(val);
            return d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
        },
        formatDate(val) {
            if (!val) return '';
            const d = new Date(val);
            return d.toLocaleDateString('vi-VN');
        }
    },
};
</script>
<style scoped></style>