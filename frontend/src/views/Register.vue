<template>
  <div class="w-full px-4 sm:px-6 lg:px-2 py-5">
    <div class="relative w-full rounded-md overflow-hidden shadow-md">
      <img alt="Scenic lake" class="w-full lg:h-[850px] object-cover brightness-75"
           src="https://storage.googleapis.com/a1aa/image/f092f5e2-89b2-445b-38d1-ef49b53e6262.jpg"/>

      <div class="absolute inset-0 flex items-center justify-center px-6 py-5">
        <form @submit.prevent="submitForm" class="bg-white rounded-md shadow-lg max-w-md w-full p-8">
          <h2 class="text-center font-extrabold text-xl mb-6">Sign up</h2>

          <div class="mb-4">
            <label class="block text-xs font-semibold text-gray-900 mb-1" for="name">
              <span class="text-red-500">*</span>
              Full Name</label>
            <div class="relative">
                            <span class="absolute inset-y-0 left-3 flex items-center text-gray-400">
                                <i class="fas fa-user"></i>
                            </span>
              <input v-model="name" type="text" id="name" placeholder="Enter your full name"
                     class="w-full pl-9 pr-3 py-2 text-sm rounded-md border border-gray-200 bg-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600"
                     required/>
            </div>
            <p v-if="nameError" class="text-red-500 text-xs mt-1">{{ nameError }}</p>
          </div>

          <div class="mb-4">
            <label class="block text-xs font-semibold text-gray-900 mb-1" for="email">
              <span class="text-red-500">*</span>
              Email</label>
            <div class="relative">
                            <span class="absolute inset-y-0 left-3 flex items-center text-gray-400">
                                <i class="fas fa-envelope"></i>
                            </span>
              <input v-model="email" type="email" id="email" placeholder="Enter your email"
                     class="w-full pl-9 pr-3 py-2 text-sm rounded-md border border-gray-200 bg-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600"
                     required/>

              <p v-if="emailError" class="text-red-500 text-xs mt-1">{{ emailError }}</p>
            </div>
          </div>

          <div class="mb-4">
            <label class="block text-xs font-semibold text-gray-900 mb-1" for="password">
              <span class="text-red-500">*</span>
              Password</label>
            <div class="relative">
                            <span class="absolute inset-y-0 left-3 flex items-center text-gray-400">
                                <i class="fas fa-lock"></i>
                            </span>
              <input @keydown.space.prevent :type="showPassword ? 'text' : 'password'" v-model="password" id="password"
                     placeholder="Enter your password"
                     class="w-full pl-9 pr-9 py-2 text-sm rounded-md border border-gray-200 bg-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600"
                     required/>
              <button type="button" @click="togglePassword"
                      class="absolute inset-y-0 right-3 flex items-center text-gray-600 hover:text-gray-900"
                      aria-label="Toggle password visibility">
                <i :class="showPassword ? 'fas fa-eye' : 'fas fa-eye-slash'"></i>
              </button>
            </div>
            <p v-if="passwordError" class="text-red-500 text-xs mt-1">{{ passwordError }}</p>
          </div>

          <div class="mb-6">
            <label class="block text-xs font-semibold text-gray-900 mb-1" for="confirmPassword">
              <span class="text-red-500">*</span>
              Confirm
              Password</label>
            <div class="relative">
                            <span class="absolute inset-y-0 left-3 flex items-center text-gray-400">
                                <i class="fas fa-lock"></i>
                            </span>
              <input :type="showConfirmPassword ? 'text' : 'password'" v-model="confirmPassword"
                     id="confirmPassword" placeholder="Confirm your password"
                     class="w-full pl-9 pr-9 py-2 text-sm rounded-md border border-gray-200 bg-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600"
                     required/>
              <button type="button" @click="toggleConfirmPassword"
                      class="absolute inset-y-0 right-3 flex items-center text-gray-600 hover:text-gray-900"
                      aria-label="Toggle confirm password visibility">
                <i :class="showConfirmPassword ? 'fas fa-eye' : 'fas fa-eye-slash'"></i>
              </button>
            </div>
            <p v-if="passwordNotMatch" class="text-red-500 text-xs mt-1">{{ passwordNotMatch }}</p>

            <div class="mb-6">
              <label class="block text-xs font-semibold text-gray-900 mb-1 mt-4" for="role">
                <span class="text-red-500">*</span>
                Select Role</label>

              <div class="relative">
                <span class="absolute inset-y-0 left-3 flex items-center text-gray-400">
                <i class="fas fa-user"></i>
                </span>
                <select
                    id="role"
                    v-model="selectedRole"
                    class="w-full pl-9 pr-2 py-2 text-sm rounded-md border border-gray-200 bg-gray-100 text-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600">
                  <option v-for="role in roles" :key="role.value" :value="role.value">{{ role.label }}</option>
                </select>
              </div>
            </div>

          </div>

          <button type="submit"
                  class="w-full bg-purple-700 hover:bg-purple-600 text-white font-semibold py-2 rounded-md">
            Sign up
          </button>

          <p class="text-center text-xs mt-4 text-gray-900">
            Already have an account?
            <router-link to="/login" class="text-purple-700 hover:underline">Sign in</router-link>
          </p>

          <div class="mt-6 flex items-center justify-center space-x-4 text-gray-400 text-xs">
            <hr class="flex-grow border-gray-300"/>
            <span class="px-2">OR</span>
            <hr class="flex-grow border-gray-300"/>
          </div>

          <div class="mt-6 flex justify-center space-x-4">
            <button type="button" class="w-10 h-10 rounded-full bg-red-700 text-white hover:bg-red-600">
              G
            </button>
            <button type="button" class="w-10 h-10 rounded-full bg-blue-700 text-white hover:bg-blue-600">
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

<script>
import {AuthApi} from "@/api/AuthApi.js";
import {saveAccessToken} from "@/services/TokenService.js";
import {useUserStore} from "@/store/UserStore.js";
import {useRouter} from "vue-router";
import {useLoadingStore} from "@/store/GlobalStore.js"

const router = useRouter()

export default {
  name: "Register",
  data() {
    return {
      name: "",
      email: "",
      password: "",
      confirmPassword: "",
      showPassword: false,
      showConfirmPassword: false,
      passwordNotMatch: "",
      emailError: "",
      passwordError: "",
      nameError: "",
      roles: [
        {label: "Customer", value: "USER"},
        {label: "Hotel Supplier", value: "HOTEL_SUPPLIER"},
        {label: "Flight Supplier", value: "FLIGHT_SUPPLIER"},
        {label: "Tour Supplier", value: "TOUR_SUPPLIER"},
        {label: "Bus Supplier", value: "BUS_SUPPLIER"}
      ],
      selectedRole: "USER"
    };
  },
  methods: {
    togglePassword() {
      this.showPassword = !this.showPassword;
    },
    toggleConfirmPassword() {
      this.showConfirmPassword = !this.showConfirmPassword;
    },
    validateName() {
      if (!this.name || this.name.trim() === "") {
        this.nameError = "Please enter your username.";
        return false;
      }
      this.nameError = "";
      return true;
    },
    validateEmail() {
      const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!regex.test(this.email)) {
        this.emailError = "Invalid email, try again!";
        return false;
      }
      this.emailError = "";
      return true;
    },
    validatePassword() {
      if (this.password.trim().length < 4) {
        this.passwordError = "Password must contain at least 4 characters!";
        return false;
      }
      this.passwordError = "";
      return true;
    },

    async submitForm() {

      useLoadingStore().startLoading();
      const isEmailValid = this.validateEmail();
      const isPasswordValid = this.validatePassword();
      const isNameValid = this.validateName();

      if (!isEmailValid || !isPasswordValid || !isNameValid) {
        useLoadingStore().stopLoading();
        return;
      }

      if (this.password !== this.confirmPassword) {
        this.passwordNotMatch = "Passwords do not match.";
        return;
      } else {
        this.passwordNotMatch = "";
      }

      const registerRequest = {
        email: this.email,
        password: this.password,
        name: this.name,
        role: this.selectedRole
      }
      const res = await AuthApi.register(registerRequest);

      // saveAccessToken(res.accessToken);
      //
      // useUserStore().login()
      //
      // this.$router.push("/");

      if (res.success) {
        this.$router.push(`/verify-email?email=${this.email}`);
      } else {
        this.emailError = "Registration failed!";
      }


      this.name = "";
      this.email = "";
      this.password = "";
      this.confirmPassword = "";
      this.nameError = "";
      this.emailError = "";
      this.passwordError = "";
      this.passwordNotMatch = "";
      this.showPassword = false;
      this.showConfirmPassword = false;
      useLoadingStore().stopLoading();
    },
  },
};
</script>
