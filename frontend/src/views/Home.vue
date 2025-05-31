<template>
  <div class="w-full min-h-screen text-lg md:text-xl relative" style="padding-top: 27px;">
    <HeroSection />
    <div class="absolute top-[0.2%] left-1/2 transform -translate-x-1/2 z-30 w-full px-4 max-w-7xl">
      <SearchBarHome />
    </div>
    <SignatureSection />
    <CouponSection />
    <CategoryTabs />
  </div>
</template>

<script>
import SearchBarHome from "../components/Home/SearchBarHome.vue";
import HeroSection from "../components/Home/HeroSection.vue";
import SignatureSection from "../components/Home/SignatureSection.vue";
import CouponSection from "../components/Home/CouponSection.vue";
import CategoryTabs from "../components/Home/CategoryTabs.vue";

export default {
  name: "Home",
  components: {
    SearchBarHome,
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
  -webkit-appearance: none;
  -moz-appearance: textfield;
  appearance: textfield;
}
</style>
