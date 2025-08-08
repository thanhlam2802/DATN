<template>
  <!-- Main container with a gradient background -->
  <div
    class="flex flex-col items-center justify-center min-h-screen bg-gradient-to-br from-purple-600 to-blue-500 p-6 font-sans"
  >
    <!-- Glassmorphism Card -->
    <div
      class="w-full max-w-md bg-white/20 backdrop-blur-xl rounded-2xl border border-white/30 shadow-2xl p-8 text-center transition-all duration-500 transform hover:scale-105"
    >
      <!-- Animated Success Icon -->
      <div class="mx-auto mb-6 h-20 w-20">
        <svg
          class="checkmark"
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 52 52"
        >
          <circle
            class="checkmark__circle"
            cx="26"
            cy="26"
            r="25"
            fill="none"
          />
          <path
            class="checkmark__check"
            fill="none"
            d="M14.1 27.2l7.1 7.2 16.7-16.8"
          />
        </svg>
      </div>

      <!-- Title -->
      <h1 class="text-3xl font-bold text-white mb-3">
        Registration Successful!
      </h1>

      <!-- Subtitle -->
      <p class="text-gray-200 mb-8">
        Your account is ready. What would you like to do next?
      </p>

      <!-- Action Buttons -->
      <div class="space-y-4">
        <button
          @click="goToHome"
          class="group flex items-center justify-center w-full bg-white hover:bg-gray-200 text-purple-700 font-bold py-3 px-4 rounded-lg transition-all duration-300 transform hover:-translate-y-1 shadow-md"
        >
          <span class="mr-2">Continue as a Customer</span>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5 transition-transform duration-300 group-hover:translate-x-1"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M17 8l4 4m0 0l-4 4m4-4H3"
            />
          </svg>
        </button>

        <!-- Secondary Button: Provider -->
        <button
          @click="applyAsSupplier"
          class="group flex items-center justify-center w-full bg-transparent border-2 border-white/50 hover:bg-white/20 text-white font-semibold py-3 px-4 rounded-lg transition-all duration-300 transform hover:-translate-y-1"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5 mr-2"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
            />
          </svg>
          <span>Become a Service Provider</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "PostRegistrationChoice",
  methods: {
    goToHome() {
      this.$router.push("/");
    },
    applyAsSupplier() {
      this.$router.push("/supplier-application");
    },
  },
};
</script>

<style scoped>
/* Animated Checkmark Styles */
.checkmark__circle {
  stroke-dasharray: 166;
  stroke-dashoffset: 166;
  stroke-width: 2;
  stroke-miterlimit: 10;
  stroke: #4cbb17;
  fill: none;
  animation: stroke 0.6s cubic-bezier(0.65, 0, 0.45, 1) forwards;
}

.checkmark {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: block;
  stroke-width: 3;
  stroke: #fff;
  stroke-miterlimit: 10;
  margin: 10% auto;
  box-shadow: inset 0px 0px 0px #4cbb17;
  animation: fill 0.4s ease-in-out 0.4s forwards,
    scale 0.3s ease-in-out 0.9s both;
}

.checkmark__check {
  transform-origin: 50% 50%;
  stroke-dasharray: 48;
  stroke-dashoffset: 48;
  animation: stroke 0.3s cubic-bezier(0.65, 0, 0.45, 1) 0.8s forwards;
}

@keyframes stroke {
  100% {
    stroke-dashoffset: 0;
  }
}

@keyframes scale {
  0%,
  100% {
    transform: none;
  }
  50% {
    transform: scale3d(1.1, 1.1, 1);
  }
}

@keyframes fill {
  100% {
    box-shadow: inset 0px 0px 0px 40px #4cbb17;
  }
}
</style>
