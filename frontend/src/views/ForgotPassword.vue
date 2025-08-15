<template>
  <div class="w-full px-4 sm:px-6 lg:px-2 py-5">
    <div class="relative w-full rounded-md overflow-hidden shadow-md">
      <img
          alt="Scenic lake"
          class="w-full h-[600px] object-cover brightness-75"
          src="https://storage.googleapis.com/a1aa/image/f092f5e2-89b2-445b-38d1-ef49b53e6262.jpg"
      />

      <div class="absolute inset-0 flex items-center justify-center px-6 py-8">
        <div class="bg-white rounded-md shadow-lg max-w-md w-full p-8">
          <template v-if="!success">
            <form @submit.prevent="submitForm">
              <h2 class="text-center font-extrabold text-xl mb-6">Forgot Password</h2>

              <div class="mb-4">
                <label class="block text-xs font-medium text-gray-500 mb-5" for="email">
                  Please fill in your Email address below and we'll send an email with instructions on how to reset your
                  password
                </label>

                <div class="relative">
                  <span class="absolute inset-y-0 left-3 flex items-center text-gray-400">
                    <i class="fas fa-envelope"></i>
                  </span>
                  <input
                      v-model="email"
                      type="email"
                      id="email"
                      placeholder="Enter your email"
                      class="w-full pl-9 pr-3 py-2 text-sm rounded-md border border-gray-200 bg-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600"
                  />
                </div>

                <p v-if="emailError" class="text-red-500 text-xs mt-1">
                  {{ emailError }}
                </p>
              </div>

              <button
                  type="submit"
                  class="w-full bg-purple-700 hover:bg-purple-600 text-white font-semibold py-2 rounded-md"
              >
                Reset Password
              </button>
            </form>
          </template>

          <template v-else>
            <div class="text-center space-y-4">
              <i class="fas fa-check-circle text-green-500 text-4xl"></i>
              <h3 class="text-lg font-semibold">Check your email</h3>
              <p class="text-sm text-gray-500">
                We've sent a link to reset your password to <span class="font-medium">{{ email }}</span><br />
                Please check your inbox and follow the instructions.
              </p>
            </div>
          </template>
        </div>
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
import { ref } from 'vue';
import { useLoadingStore } from "@/store/GlobalStore.js";
import { AuthApi } from '@/api/AuthApi.js';

const email = ref('');
const emailError = ref('');
const success = ref(false);
const loadingStore = useLoadingStore();

const validateEmail = () => {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!regex.test(email.value)) {
    emailError.value = "Invalid email, try again!";
    return false;
  }
  emailError.value = "";
  return true;
};

const submitForm = async () => {
  if (!validateEmail()) return;

  try {
    loadingStore.startLoading();
    const res = await AuthApi.requestResetPassWord({ email: email.value });
    if (res["errorCode"]) {
      emailError.value = "Something went wrong!!";
    } else {
      success.value = true;
    }
  } catch (e) {
    emailError.value = "Failed to send reset request.";
  } finally {
    loadingStore.stopLoading();
  }
};
</script>