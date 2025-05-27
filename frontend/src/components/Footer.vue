<script setup>
// chưa có logic gì nên để trống
</script>

<template>
  <footer class="bg-gray-50 text-gray-900">
    <div
      class="max-w-7xl mx-auto px-6 py-10 grid grid-cols-1 md:grid-cols-4 gap-10"
    >
      <div class="flex flex-col space-y-3">
        <h3 class="font-extrabold text-lg">Hỗ trợ</h3>
        <a href="#" class="text-base font-normal leading-6">Irure in molli</a>
        <a href="#" class="text-base font-normal leading-6"
          >Officia sit laborum</a
        >
        <a href="#" class="text-base font-normal leading-6"
          >Lorem ea quis labore</a
        >
      </div>

      <div class="flex flex-col space-y-3">
        <h3 class="font-extrabold text-lg">Cộng đồng</h3>
        <a href="#" class="text-base font-normal leading-6">Nisi velit ut</a>
        <a href="#" class="text-base font-normal leading-6"
          >Pariatur elit esse</a
        >
        <a href="#" class="text-base font-normal leading-6"
          >Laborum aliquip do</a
        >
      </div>

      <div class="flex flex-col space-y-3">
        <h3 class="font-extrabold text-lg">Giới thiệu</h3>
        <a href="#" class="text-base font-normal leading-6">Aute com</a>
        <a href="#" class="text-base font-normal leading-6">Volupta</a>
        <a href="#" class="text-base font-normal leading-6">Nulla min</a>
      </div>

      <div class="flex flex-col space-y-2 max-w-xs">
        <h3 class="font-extrabold text-lg">Đăng ký nhận bản tin</h3>
        <p class="text-sm leading-5 text-gray-700">
          Để nhận thông báo và ưu đãi độc quyền
        </p>
        <form class="flex mt-2 gap-x-2">
          <label for="email" class="sr-only">Email</label>
          <input
            id="email"
            type="email"
            placeholder="Nhập email của bạn"
            class="w-full sm:w-auto px-1.5 py-1.5 border border-gray-300 rounded text-xs placeholder-gray-400 outline-none"
          />
          <button
            type="submit"
            class="bg-indigo-500 text-white px-2 py-1.5 rounded text-xs font-medium hover:bg-indigo-600 transition"
          >
            Đăng ký
          </button>
        </form>
      </div>
    </div>

    <hr class="border-gray-300 mx-6" />

    <div
      class="max-w-7xl mx-auto px-6 py-4 grid grid-cols-1 md:grid-cols-2 gap-4 items-center text-sm font-normal"
    >
      <p>© 2022 Công ty, Inc. • Chính sách bảo mật • Điều khoản</p>

      <div class="flex items-center justify-start md:justify-end space-x-4">
        <span class="font-extrabold text-sm">VND</span>
        <button aria-label="Globe" class="hover:text-gray-700">
          <i class="fas fa-globe"></i>
        </button>
        <button aria-label="Facebook" class="hover:text-gray-700">
          <i class="fab fa-facebook-f"></i>
        </button>
        <button aria-label="Twitter" class="hover:text-gray-700">
          <i class="fab fa-twitter"></i>
        </button>
        <button aria-label="Instagram" class="hover:text-gray-700">
          <i class="fab fa-instagram"></i>
        </button>
      </div>
    </div>
  </footer>
</template>
