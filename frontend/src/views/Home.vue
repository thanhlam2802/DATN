<template>
  <div class="w-full min-h-screen text-lg md:text-xl">
    <HeroSection />
    <SignatureSection />
    <CouponSection />
    <CategoryTabs />
  </div>
</template>

<script>
import HeroSection from "../components/Home/HeroSection.vue";
import SignatureSection from "../components/Home/SignatureSection.vue";
import CouponSection from "../components/Home/CouponSection.vue";
import CategoryTabs from "../components/Home/CategoryTabs.vue";

export default {
  name: "Home",
  components: {
    HeroSection,
    SignatureSection,
    CouponSection,
    CategoryTabs,
  },
};
</script>

<style scoped>
input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

input[type="number"] {
  -moz-appearance: textfield;
}
</style>