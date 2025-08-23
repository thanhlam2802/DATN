<template>
    <div 
        @click="handleRouteClick"
        class="w-[350px] h-[350px] flex-shrink-0 rounded-2xl bg-white overflow-hidden shadow-2xl transition-all duration-300 scale-[0.95] hover:scale-100 cursor-pointer hover:shadow-3xl"
    >
        <div class="relative h-48 overflow-hidden">
            <img 
                :src="routeImage" 
                :alt="`${bus.origin} - ${bus.destination}`"
                class="w-full h-full object-cover object-center transition-transform duration-300 hover:scale-110" 
            />
            <div class="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent"></div>
        </div>
        <div class="p-4">
            <h3 class="font-bold text-lg mb-2 text-gray-800">
                {{ bus.origin }} → {{ bus.destination }}
            </h3>
            <div class="flex justify-between items-center mb-3">
                <div class="flex items-center gap-2 text-sm text-gray-600">
                    <i class="fas fa-route text-blue-500"></i>
                    <span>Tuyến xe phổ biến</span>
                </div>
                <div v-if="bus.price" class="text-blue-600 font-bold text-lg whitespace-nowrap">
                    {{ formatPrice(bus.price) }}
                </div>
            </div>
            <div class="flex items-center justify-between">
                <div class="flex items-center text-gray-600 text-sm">
                    <i class="fas fa-map-marker-alt mr-1 text-red-500"></i>
                    <span>{{ bus.origin }}</span>
                </div>
                <div class="flex items-center text-gray-600 text-sm">
                    <i class="fas fa-map-marker-alt mr-1 text-green-500"></i>
                    <span>{{ bus.destination }}</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useRouteStore } from '@/stores/routeStore';

const props = defineProps({
    bus: {
        type: Object,
        required: true,
    },
});



const router = useRouter();
const { setRouteData } = useRouteStore();

const routeImage = computed(() => {
    // Sử dụng ảnh từ API hoặc ảnh mặc định dựa trên origin
    if (props.bus.imageUrl) {
        return props.bus.imageUrl;
    }
    
    // Tạo ảnh dựa trên tuyến đường
    const origin = props.bus.origin?.toLowerCase();
    const routeImages = {
        'vũng tàu': 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400&h=300&fit=crop',
        'hà nội': 'https://images.unsplash.com/photo-1509023464722-18d996393ca8?w=400&h=300&fit=crop',
        'đà nẵng': 'https://images.unsplash.com/photo-1555841900-31bfc04ca0a9?w=400&h=300&fit=crop',
        'cần thơ': 'https://images.unsplash.com/photo-1557804506-669a67965ba0?w=400&h=300&fit=crop',
        'nha trang': 'https://images.unsplash.com/photo-1539650116574-75c0c6d05be9?w=400&h=300&fit=crop',
        'bình dương': 'https://images.unsplash.com/photo-1544966503-7cc5ac882d5f?w=400&h=300&fit=crop',
        'hồ chí minh': 'https://images.unsplash.com/photo-1583417267826-aebc4d1542e1?w=400&h=300&fit=crop'
    };
    
    return routeImages[origin] || 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=400&h=300&fit=crop';
});

const formatPrice = (price) => {
    if (!price) return 'Liên hệ';
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(price);
};

const handleRouteClick = () => {
    // Lưu dữ liệu route vào store
    const routeData = {
        departureProvince: props.bus.origin,
        arrivalProvince: props.bus.destination
    };
    
    setRouteData(routeData);
    
    // Navigate đến trang Bus
    router.push({ name: 'Bus' });
};
</script>
<style scoped></style>