<template>

</template>

<script setup>
import {onMounted} from "vue";
import {useLoadingStore} from "@/store/GlobalStore.js";
import {useUserStore} from "@/store/UserStore.js";
import {useRouter} from "vue-router";
import {saveAccessToken, saveRefreshToken} from "@/services/TokenService.js";

const loadingStore = useLoadingStore();
const userStore = useUserStore();
const router = useRouter();

onMounted(() => {
  loadingStore.startLoading();
  const urlParams = new URLSearchParams(window.location.search)
  const token = urlParams.get('t')
  const refreshToken = urlParams.get('rt')
  console.log("token", token, refreshToken)
  saveAccessToken(token)
  saveRefreshToken(refreshToken)
  userStore.login();
  router.push("/")
  loadingStore.stopLoading();
})
</script>