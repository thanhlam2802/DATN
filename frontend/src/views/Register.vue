<template>
  <div class="w-full px-4 sm:px-6 lg:px-2 py-5">
    <div class="relative w-full rounded-md overflow-hidden shadow-md">
      <img
        alt="Scenic lake"
        class="w-full lg:h-[850px] object-cover brightness-75"
        src="https://storage.googleapis.com/a1aa/image/f092f5e2-89b2-445b-38d1-ef49b53e6262.jpg"
      />

      <div class="absolute inset-0 flex items-center justify-center px-6 py-5">
        <form
          @submit.prevent="submitForm"
          class="bg-white rounded-md shadow-lg max-w-md w-full p-8"
        >
          <h2 class="text-center font-extrabold text-xl mb-6">Sign up</h2>

          <div class="mb-4">
            <label
              class="block text-xs font-semibold text-gray-900 mb-1"
              for="name"
            >
              <span class="text-red-500">*</span>
              Full Name</label
            >
            <div class="relative">
              <span
                class="absolute inset-y-0 left-3 flex items-center text-gray-400"
              >
                <i class="fas fa-user"></i>
              </span>
              <input
                v-model="name"
                type="text"
                id="name"
                placeholder="Enter your full name"
                class="w-full pl-9 pr-3 py-2 text-sm rounded-md border border-gray-200 bg-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600"
                required
              />
            </div>
            <p v-if="nameError" class="text-red-500 text-xs mt-1">
              {{ nameError }}
            </p>
          </div>

          <div class="mb-4">
            <label
              class="block text-xs font-semibold text-gray-900 mb-1"
              for="email"
            >
              <span class="text-red-500">*</span>
              Email</label
            >
            <div class="relative">
              <span
                class="absolute inset-y-0 left-3 flex items-center text-gray-400"
              >
                <i class="fas fa-envelope"></i>
              </span>
              <input
                v-model="email"
                type="email"
                id="email"
                placeholder="Enter your email"
                class="w-full pl-9 pr-3 py-2 text-sm rounded-md border border-gray-200 bg-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600"
                required
              />

              <p v-if="emailError" class="text-red-500 text-xs mt-1">
                {{ emailError }}
              </p>
            </div>
          </div>

          <div class="mb-4">
            <label
              class="block text-xs font-semibold text-gray-900 mb-1"
              for="password"
            >
              <span class="text-red-500">*</span>
              Password</label
            >
            <div class="relative">
              <span
                class="absolute inset-y-0 left-3 flex items-center text-gray-400"
              >
                <i class="fas fa-lock"></i>
              </span>
              <input
                @keydown.space.prevent
                :type="showPassword ? 'text' : 'password'"
                v-model="password"
                id="password"
                placeholder="Enter your password"
                class="w-full pl-9 pr-9 py-2 text-sm rounded-md border border-gray-200 bg-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600"
                required
              />
              <button
                type="button"
                @click="togglePassword"
                class="absolute inset-y-0 right-3 flex items-center text-gray-600 hover:text-gray-900"
                aria-label="Toggle password visibility"
              >
                <i
                  :class="showPassword ? 'fas fa-eye' : 'fas fa-eye-slash'"
                ></i>
              </button>
            </div>
            <p v-if="passwordError" class="text-red-500 text-xs mt-1">
              {{ passwordError }}
            </p>
          </div>

          <div class="mb-6">
            <label
              class="block text-xs font-semibold text-gray-900 mb-1"
              for="confirmPassword"
            >
              <span class="text-red-500">*</span>
              Confirm Password</label
            >
            <div class="relative">
              <span
                class="absolute inset-y-0 left-3 flex items-center text-gray-400"
              >
                <i class="fas fa-lock"></i>
              </span>
              <input
                :type="showConfirmPassword ? 'text' : 'password'"
                v-model="confirmPassword"
                id="confirmPassword"
                placeholder="Confirm your password"
                class="w-full pl-9 pr-9 py-2 text-sm rounded-md border border-gray-200 bg-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600"
                required
              />
              <button
                type="button"
                @click="toggleConfirmPassword"
                class="absolute inset-y-0 right-3 flex items-center text-gray-600 hover:text-gray-900"
                aria-label="Toggle confirm password visibility"
              >
                <i
                  :class="
                    showConfirmPassword ? 'fas fa-eye' : 'fas fa-eye-slash'
                  "
                ></i>
              </button>
            </div>
            <p v-if="passwordNotMatch" class="text-red-500 text-xs mt-1">
              {{ passwordNotMatch }}
            </p>
          </div>

          <button
            type="submit"
            class="w-full bg-purple-700 hover:bg-purple-600 text-white font-semibold py-2 rounded-md"
          >
            Sign up
          </button>

          <p class="text-center text-xs mt-4 text-gray-900">
            Already have an account?
            <router-link to="/login" class="text-purple-700 hover:underline"
              >Sign in</router-link
            >
          </p>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { AuthApi } from "@/api/AuthApi.js";
import { saveAccessToken } from "@/services/TokenService.js";
import { useUserStore } from "@/store/UserStore.js";
import { useLoadingStore } from "@/store/GlobalStore.js";

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
      const userStore = useUserStore();
      useLoadingStore().startLoading();
      if (
        !this.validateName() ||
        !this.validateEmail() ||
        !this.validatePassword()
      ) {
        useLoadingStore().stopLoading();
        return;
      }

      if (this.password !== this.confirmPassword) {
        this.passwordNotMatch = "Passwords do not match.";
        useLoadingStore().stopLoading();
        return;
      } else {
        this.passwordNotMatch = "";
      }

      try {
        const registerRequest = {
          email: this.email,
          password: this.password,
          name: this.name,
          role: "USER",
        };

        const responseData = await AuthApi.register(registerRequest);
        if (responseData && responseData.accessToken) {
          // useUserStore().login();
          await userStore.handleRegistration(responseData);
          this.$router.push("/post-registration-choice");
        } else {
          throw new Error(
            "Đăng ký thành công nhưng không nhận được token xác thực."
          );
        }
      } catch (error) {
        console.error("Registration failed:", error);
        alert(`Đăng ký thất bại: ${error.message || "Vui lòng thử lại."}`);
      } finally {
        useLoadingStore().stopLoading();
      }
    },
  },
};
</script>
