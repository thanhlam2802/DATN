<template>
    <div class="container mx-auto px-4 py-12">
        <h2 class="text-2xl font-bold mb-8">A signature of excellence</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <div v-for="(destination, index) in destinations" :key="index"
                class="rounded-lg overflow-hidden shadow-lg hover:shadow-xl transition-shadow cursor-pointer">
                <div class="relative h-48 overflow-hidden">
                    <img :src="destination.image" :alt="destination.name"
                        class="w-full h-full object-cover object-top transition-transform duration-300 hover:scale-110" />
                </div>
                <div class="p-4">
                    <h3 class="font-bold text-lg mb-1">{{ destination.name }}</h3>
                    <p class="text-gray-600 text-sm">{{ destination.description }}</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { destinations } from "../../data/appData";

export default {
    name: "SignatureSection",
    data() {
        return {
            destinations: destinations,
        };
    },
};
</script>

<style scoped>
</style>