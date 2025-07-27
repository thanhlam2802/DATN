<template>
  <div class="w-full px-4 sm:px-6 lg:px-2 py-5">
    <div class="relative w-full rounded-md overflow-hidden shadow-md">
      <img
          alt="Scenic lake"
          class="w-full h-[600px] object-cover brightness-75"
          src="https://storage.googleapis.com/a1aa/image/f092f5e2-89b2-445b-38d1-ef49b53e6262.jpg"
      />

      <div class="absolute inset-0 flex items-center justify-center px-6 py-8">
        <form @submit.prevent="submitForm" class="bg-white rounded-md shadow-lg max-w-md w-full p-8">
          <h2 class="text-center font-extrabold text-xl mb-6">Change Your Password</h2>

          <div class="mb-4">
            <p class="block text-xs font-medium text-gray-500 mb-5">
              Enter a new password below to change your password
            </p>

            <label class="block text-xs font-semibold text-gray-900 mb-1">
              <span class="text-red-500">*</span>
              New Password
            </label>
            <input
                v-model="newPassword"
                type="password"
                placeholder="Enter your new password"
                class="w-full pl-3 pr-3 py-2 text-sm rounded-md border border-gray-200 bg-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600"
            />
            <p v-if="newPasswordError" class="text-red-500 text-xs mt-1">{{ newPasswordError }}</p>

            <label class="block text-xs font-semibold text-gray-900 mb-1 mt-4">
              <span class="text-red-500">*</span>
              Confirm New Password
            </label>
            <input
                v-model="confirmPassword"
                type="password"
                placeholder="Confirm password"
                class="w-full pl-3 pr-3 py-2 text-sm rounded-md border border-gray-200 bg-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600"
            />
            <p v-if="confirmPasswordError" class="text-red-500 text-xs mt-1">{{ confirmPasswordError }}</p>
          </div>

          <button
              type="submit"
              class="w-full bg-purple-700 hover:bg-purple-600 text-white font-semibold py-2 rounded-md"
          >
            Change Password
          </button>
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
import {ref, onMounted} from 'vue'
import {useLoadingStore} from "@/store/GlobalStore.js";
import {AuthApi} from "@/api/AuthApi.js";
import {useRouter} from 'vue-router'
import {ErrorCodes} from "@/data/ErrorCode.js";

const router = useRouter()

const newPassword = ref('')
const confirmPassword = ref('')
const newPasswordError = ref('')
const confirmPasswordError = ref('')
const tokenRef = ref('')
const otpCodeRef = ref('')
const loadingStore = useLoadingStore();

onMounted(async () => {
  loadingStore.startLoading();
  const urlParams = new URLSearchParams(window.location.search)
  const token = urlParams.get('rt')
  const otp = urlParams.get('otp')
  const verifyLinkRequest = {
    resetToken: token,
    otpCode: otp
  }
  const res = await AuthApi.verifyResetPassLink(verifyLinkRequest);
  if (res && res['errorCode'] === ErrorCodes.otpExpired) {
    await router.push("/expired-link")
    loadingStore.stopLoading();
  }
  loadingStore.stopLoading();
  tokenRef.value = token;
  otpCodeRef.value = otp;
})

const validatePasswords = () => {
  let isValid = true

  if (newPassword.value.length < 4) {
    newPasswordError.value = 'Password must be at least 4 characters.'
    isValid = false
  } else {
    newPasswordError.value = ''
  }

  if (newPassword.value !== confirmPassword.value) {
    confirmPasswordError.value = 'Passwords do not match.'
    isValid = false
  } else {
    confirmPasswordError.value = ''
  }

  return isValid
}

const submitForm = async () => {
  if (!validatePasswords()) return

  try {
    const request = {
      newPassword: newPassword.value,
      otpCode: otpCodeRef.value,
      resetToken: tokenRef.value
    }
    loadingStore.startLoading();
    const res = await AuthApi.resetPassWord(request);
    if (res && res['errorCode'] === ErrorCodes.otpExpired) {
      await router.push("/expired-link")
      loadingStore.stopLoading();
    }
    await router.push("/login")
    loadingStore.stopLoading();
  } catch (error) {
    console.error(error)
    newPasswordError.value = 'Something went wrong. Please try again.'
  }
}
</script>
