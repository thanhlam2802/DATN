<template>
  <div class="w-full px-4 sm:px-6 lg:px-2 py-5">
    <div class="relative w-full rounded-md overflow-hidden shadow-md">
      <img
          alt="Scenic background"
          class="w-full h-[600px] object-cover brightness-75"
          src="https://storage.googleapis.com/a1aa/image/f092f5e2-89b2-445b-38d1-ef49b53e6262.jpg"
      />

      <div class="absolute inset-0 flex items-center justify-center px-6 py-8">
        <form @submit.prevent="submitCode" class="bg-white rounded-md shadow-lg max-w-md w-full p-8">
          <div class="flex items-center justify-center mb-6 relative">
            <button @click="goBack" class="absolute left-0 text-purple-700 text-xl">
              <i class="fas fa-arrow-left"></i>
            </button>
            <h2 class="text-center font-extrabold text-xl">Enter confirmation code</h2>
          </div>

          <p class="block text-sm font-medium text-gray-500 mb-8 text-center">
            Verification code has been sent to email
            <strong>{{ email }}</strong>
          </p>

          <div class="flex justify-between gap-3 mb-6">
            <input
                v-for="(digit, index) in code"
                :key="index"
                type="text"
                class="w-12 h-12 border border-gray-300 rounded text-center text-xl focus:outline-purple-700"
                maxlength="1"
                v-model="code[index]"
                @input="onInput(index)"
                @keydown.backspace="onBackspace(index, $event)"
                ref="inputRefs"
            />
          </div>

          <button
              type="submit"
              class="w-full bg-purple-700 hover:bg-purple-600 text-white font-semibold py-2 rounded-md"
          >
            Confirm
          </button>

          <p class="text-sm text-center text-gray-500 mt-4">
            Didn’t receive the code?
            <span v-if="timer > 0" class="text-gray-700 font-medium">
              Resend in {{ timer }}s
            </span>
            <button
                v-else
                class="text-purple-700 hover:underline ml-1"
                @click="resendCode"
                type="button"
            >
              Resend
            </button>
          </p>
        </form>
      </div>
    </div>

    <div class="mt-6 flex justify-center space-x-1 text-gray-400 text-xs select-none">
      <span class="w-3 h-1 rounded-full bg-gray-900"></span>
      <span class="w-3 h-1 rounded-full bg-gray-300"></span>
      <span class="w-3 h-1 rounded-full bg-gray-300"></span>
    </div>
  </div>
</template>

<script>
export default {
  name: "VerifyCode",
  data() {
    return {
      email: "you@example.com", // Replace this with your actual email value
      code: ["", "", "", "", "", ""],
      timer: 60,
    };
  },
  mounted() {
    this.startTimer();
    this.$nextTick(() => {
      this.$refs.inputRefs[0].focus();
    });
  },
  methods: {
    goBack() {
      this.$router.back();
    },
    onInput(index) {
      const value = this.code[index];
      if (/^\d$/.test(value) && index < this.code.length - 1) {
        this.$refs.inputRefs[index + 1].focus();
      }
    },
    onBackspace(index, event) {
      if (!this.code[index] && index > 0) {
        this.$refs.inputRefs[index - 1].focus();
      }
    },
    submitCode() {
      const fullCode = this.code.join("");
      console.log("Submitted Code:", fullCode);
      // Call API to verify code here
    },
    resendCode() {
      // Trigger resend logic
      this.timer = 60;
      this.startTimer();
    },
    startTimer() {
      const countdown = setInterval(() => {
        if (this.timer > 0) {
          this.timer--;
        } else {
          clearInterval(countdown);
        }
      }, 1000);
    },
  },
};
</script>

<style scoped>
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

input[type="text"] {
  appearance: textfield;
}
</style>
