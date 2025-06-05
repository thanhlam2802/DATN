<template>
    <section class="space-y-4">
        <HotelCard v-for="hotel in hotels" :key="hotel.id" v-bind="hotel" @click="goToDetail(hotel.id)" />
    </section>
</template>

<script setup>
import HotelCard from './HotelCard.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const props = defineProps({
    hotels: {
        type: Array,
        required: true
    }
})

const goToDetail = (id) => {
    router.push({ name: 'HotelDetail', params: { id } })
}
</script>