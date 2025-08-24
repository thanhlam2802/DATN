<template>
  <div class="w-full px-4 sm:px-6 lg:px-2 py-5">
    <div class="relative w-full rounded-md overflow-hidden shadow-md">
      <img
          alt="Scenic lake"
          class="w-full lg:h-[700px] object-cover brightness-75"
          src="https://storage.googleapis.com/a1aa/image/f092f5e2-89b2-445b-38d1-ef49b53e6262.jpg"
      />

      <div class="absolute inset-0 flex items-center justify-center px-6">
        <form @submit.prevent="submitForm" class="bg-white rounded-md shadow-lg max-w-md w-full p-8">
          <h2 class="text-center font-extrabold text-xl mb-6">Sign in</h2>

          <div class="mb-4">
            <label class="block text-xs font-semibold text-gray-900 mb-1" for="email">
              <span class="text-red-500">*</span>
              Email
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
            <p v-if="emailError" class="text-red-500 text-xs mt-1">{{ emailError }}</p>
          </div>

          <div class="mb-4">
            <label class="block text-xs font-semibold text-gray-900 mb-1" for="password">
              <span class="text-red-500">*</span>
              Password</label>
            <div class="relative">
              <span class="absolute inset-y-0 left-3 flex items-center text-gray-400">
                <i class="fas fa-lock"></i>
              </span>
              <input
                  :type="showPassword ? 'text' : 'password'"
                  id="password"
                  v-model="password"
                  @keydown.space.prevent
                  placeholder="Enter your password"
                  class="w-full pl-9 pr-9 py-2 text-sm rounded-md border border-gray-200 bg-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600"
              />
              <button
                  type="button"
                  @click="togglePassword"
                  class="absolute inset-y-0 right-3 flex items-center text-gray-600 hover:text-gray-900"
                  aria-label="Toggle password visibility"
              >
                <i :class="showPassword ? 'fas fa-eye' : 'fas fa-eye-slash'"></i>
              </button>
            </div>
            <p v-if="passwordError" class="text-red-500 text-xs mt-1">{{ passwordError }}</p>
            <p v-if="wrongCredentialError" class="text-red-500 text-xs mt-1">{{ wrongCredentialError }}</p>
          </div>

          <div class="flex items-center justify-between mb-6 text-sm">
            <label class="inline-flex items-center space-x-2 text-gray-900">
              <input
                  type="checkbox"
                  class="form-checkbox h-4 w-4 text-purple-700 rounded border-gray-300"
                  checked
              />
              <span>Remember me</span>
            </label>
            <a href="/forgot-password" class="text-purple-700 hover:underline">Forgot password?</a>
          </div>

          <button
              type="submit"
              class="w-full bg-purple-700 hover:bg-purple-600 text-white font-semibold py-2 rounded-md"
          >
            Sign in
          </button>

          <p class="text-center text-xs mt-4 text-gray-900">
            Don't have an account?
            <router-link to="/register" class="text-purple-700 hover:underline">Sign up</router-link>
          </p>

          <div class="mt-6 flex items-center justify-center space-x-4 text-gray-400 text-xs">
            <hr class="flex-grow border-gray-300"/>
            <span class="px-2">OR</span>
            <hr class="flex-grow border-gray-300"/>
          </div>

          <div class="mt-6 flex justify-center space-x-4">
            <button @click="googleLogin()" type="button"
                    class="w-10 h-10 rounded-full bg-red-700 text-white hover:bg-red-600">G
            </button>
            <button
                type="button"
                class="w-10 h-10 rounded-full bg-blue-700 text-white hover:bg-blue-600"
            >
              <i class="fab fa-facebook-f"></i>
            </button>
          </div>
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
import {ref} from "vue";
import {useRouter} from "vue-router";
import {AuthApi} from "@/api/AuthApi.js";
import {AccountApi} from "@/api/AccountApi.js";
import {saveAccessToken} from "@/services/TokenService.js";
import {useUserStore} from "@/store/UserStore.js";
import {ErrorCodes} from "@/data/ErrorCode.js";
import {useLoadingStore} from "@/store/GlobalStore.js";
import {getRedirectPath} from "@/utils/redirectUtils.js";

const BASE_URL = import.meta.env.VITE_API_URL;


const loadingStore = useLoadingStore();

const router = useRouter();
const userStore = useUserStore();

const email = ref("");
const password = ref("");
const showPassword = ref(false);

const emailError = ref("");
const passwordError = ref("");
const wrongCredentialError = ref("");

const togglePassword = () => {
  showPassword.value = !showPassword.value;
};

const validateEmail = () => {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!regex.test(email.value)) {
    emailError.value = "Invalid email, try again!";
    return false;
  }
  emailError.value = "";
  return true;
};

const validatePassword = () => {
  if (password.value.trim().length < 4) {
    passwordError.value = "Password must be at least 4 characters.";
    return false;
  }
  passwordError.value = "";
  return true;
};

const googleLogin = () => {
  window.location.href = `${BASE_URL}/oauth2/authorization/google`;
}

const submitForm = async () => {
  const isEmailValid = validateEmail();
  const isPasswordValid = validatePassword();

  if (!isEmailValid || !isPasswordValid) return;

  const loginRequest = {
    identifier: email.value,
    password: password.value
  };

  try {
    loadingStore.startLoading();
    const res = await AuthApi.login(loginRequest);
    console.log(res);

    if (res["errorCode"] === ErrorCodes.userNotVerified) {
      console.log("Not verified");
      await router.push("/verify-email?email=" + email.value);
      loadingStore.stopLoading();
      return;
    }
    if (res["errorCode"] === ErrorCodes.invalidPassword) {
      wrongCredentialError.value = "Wrong credential !!!"
      loadingStore.stopLoading();
      return;
    }
    if (res["errorCode"]) {
      wrongCredentialError.value = "An error occur !!!"
      return;
    }


    saveAccessToken(res.accessToken);
    
    try {
      const profileRes = await AccountApi.getProfile();
      if (!profileRes.errorCode) {
        userStore.login(profileRes.data, res.accessToken);
        
        const intendedRoute = localStorage.getItem('intendedRoute');
        console.log('Login success - intended route:', intendedRoute);
        
        if (intendedRoute) {
          console.log('Redirecting to intended route:', intendedRoute);
          localStorage.removeItem('intendedRoute');
          await router.push(intendedRoute);
        } else {
          const redirectPath = getRedirectPath(profileRes.data);
          console.log('Redirecting to default path:', redirectPath);
          await router.push(redirectPath);
        }
      } else {
        userStore.login(null, res.accessToken);
        await router.push("/");
      }
    } catch (error) {
      console.error("Failed to get user profile:", error);
      userStore.login(null, res.accessToken);
      await router.push("/");
    }

    emailError.value = "";
    passwordError.value = "";
    wrongCredentialError.value = "";
    loadingStore.stopLoading();
  } catch (error) {
    console.error("Login failed:", error);
    passwordError.value = "Login failed. Please try again.";
    loadingStore.stopLoading();
  }
};
</script>
