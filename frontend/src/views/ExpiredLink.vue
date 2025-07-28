<template>
  <div class="w-full px-4 sm:px-6 lg:px-2 py-5">
    <div class="relative w-full rounded-md overflow-hidden shadow-md">
      <img
          alt="Scenic lake"
          class="w-full h-[600px] object-cover brightness-75"
          src="https://storage.googleapis.com/a1aa/image/f092f5e2-89b2-445b-38d1-ef49b53e6262.jpg"
      />

      <div class="absolute inset-0 flex items-center justify-center px-6 py-8">
        <form @submit.prevent="" class="bg-white rounded-md shadow-lg max-w-md w-full p-8">
          <h2 class="text-center font-extrabold text-xl mb-6">Whoops, that's an expired link</h2>

          <div class="mb-4">
            <p class="block text-xs font-medium text-gray-500 mb-10 text-center">
              For security reasons, password reset links expire after a little while.
              If you still need to reset your password, you can request a new reset email.
            </p>
          </div>

          <button
              type="submit"
              class="w-full bg-purple-700 hover:bg-purple-600 text-white font-semibold py-2 rounded-md"
          >
            Request a new reset email
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
  name: "ExpiredLink",

};
</script>
