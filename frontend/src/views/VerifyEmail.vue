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
            <button @click="goBack" class="absolute left-0 text-purple-700 text-xl" type="button">
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
                :ref="el => inputRefs[index] = el"
            />
          </div>
          <div class="mb-5 flex justify-center">
            <p v-if="otpError" class="text-red-500 text-xs mt-1">{{ otpError }}</p>

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

<script setup>
import {ref, onMounted, nextTick} from 'vue';
import {useRouter} from 'vue-router';
import {AuthApi} from "@/api/AuthApi.js";
import {useLoadingStore} from "@/store/GlobalStore.js";
import {useUserStore} from "@/store/UserStore.js";
import {ErrorCodes} from "@/data/ErrorCode.js";
import {saveAccessToken} from "@/services/TokenService.js";

const router = useRouter();
const email = ref("");
const code = ref(["", "", "", "", "", ""]);
const timer = ref(60);
const otpError = ref("");
const inputRefs = ref([]);
const loadingStore = useLoadingStore();
const userStore = useUserStore();

const goBack = () => {
  router.back();
};

const onInput = (index) => {
  let value = code.value[index];

  // Only allow a single digit (0-9)
  if (!/^\d$/.test(value)) {
    code.value[index] = "";
    return;
  }

  // Move to next input if valid and not last
  if (index < code.value.length - 1) {
    inputRefs.value[index + 1]?.focus();
  }
};

const onBackspace = (index, event) => {
  if (!code.value[index] && index > 0) {
    inputRefs.value[index - 1]?.focus();
  }
};

const submitCode = async () => {
  const fullCode = code.value.join("");

  // Check if all digits are filled
  if (fullCode.length !== 6 || code.value.some(d => !/^\d$/.test(d))) {
    otpError.value = "Please enter all 6 digits of the verification code.";
    return;
  }

  loadingStore.startLoading();
  console.log("Submitted Code:", fullCode);
  const request = {
    code: fullCode,
    email: email.value
  }
  const res = await AuthApi.verifyAccount(request);
  if (res["errorCode"] === ErrorCodes.otpNotMatch) {
    otpError.value = "Invalid OTP!!!"
    loadingStore.stopLoading();
    return;
  }
  if (res["errorCode"] === ErrorCodes.otpExpired) {
    otpError.value = "OTP expired!!!"
    loadingStore.stopLoading();
    return;
  }
  if (res["errorCode"]) {
    otpError.value = "An error occurred!!!"
    loadingStore.stopLoading();
    return;
  }
  otpError.value = "";
  saveAccessToken(res.accessToken);
  userStore.login();
  await router.push("/");
  loadingStore.stopLoading();
};

const resendCode = async () => {
  timer.value = 60;
  startTimer();
  loadingStore.startLoading();
  await AuthApi.verifyAccountResend(email.value);
  loadingStore.stopLoading();
  // Add logic to resend code via API
};

const startTimer = () => {
  const countdown = setInterval(() => {
    if (timer.value > 0) {
      timer.value--;
    } else {
      clearInterval(countdown);
    }
  }, 1000);
};

onMounted(() => {
  startTimer();
  nextTick(() => {
    inputRefs.value[0]?.focus();
  });

  const urlParams = new URLSearchParams(window.location.search);
  email.value = urlParams.get("email");
});
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
