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
              New Password</label>
            <input
                v-model="newPassword"
                type="password"
                placeholder="Enter your new password"
                class="w-full pl-3 pr-3 py-2 text-sm rounded-md border border-gray-200 bg-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-600"
            />
            <p v-if="newPasswordError" class="text-red-500 text-xs mt-1">{{ newPasswordError }}</p>

            <label class="block text-xs font-semibold text-gray-900 mb-1 mt-4">
              <span class="text-red-500">*</span>
              Confirm New Password</label>
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

<script>
export default {
  name: "ResetPasswordView",
  data() {
    return {
      newPassword: "",
      confirmPassword: "",
      newPasswordError: "",
      confirmPasswordError: "",
    };
  },
  methods: {
    validatePasswords() {
      let isValid = true;

      if (this.newPassword.length < 4) {
        this.newPasswordError = "Password must be at least 4 characters.";
        isValid = false;
      } else {
        this.newPasswordError = "";
      }

      if (this.newPassword !== this.confirmPassword) {
        this.confirmPasswordError = "Passwords do not match.";
        isValid = false;
      } else {
        this.confirmPasswordError = "";
      }

      return isValid;
    },

    async submitForm() {
      if (!this.validatePasswords()) return;

      try {

        alert("Password reset successfully!");
      } catch (error) {
        console.error(error);
        this.newPasswordError = "Something went wrong. Please try again.";
      }
    },
  },
};
</script>
