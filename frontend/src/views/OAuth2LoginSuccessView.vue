<template>

</template>

<script setup>
import {onMounted} from "vue";
import {useLoadingStore} from "@/store/GlobalStore.js";
import {useUserStore} from "@/store/UserStore.js";
import {useRouter} from "vue-router";
import {saveAccessToken, saveRefreshToken} from "@/services/TokenService.js";
import {AccountApi} from "@/api/AccountApi.js";
import {ErrorCodes} from "@/data/ErrorCode.js";
import {getRedirectPath} from "@/utils/redirectUtils.js";

const loadingStore = useLoadingStore();
const userStore = useUserStore();
const router = useRouter();

onMounted(async () => {
  loadingStore.startLoading();
  const urlParams = new URLSearchParams(window.location.search)
  const token = urlParams.get('t')
  const refreshToken = urlParams.get('rt')
  const email = urlParams.get("email")
  saveAccessToken(token)
  saveRefreshToken(refreshToken)

  const res = await AccountApi.getProfile()
  console.log("res", res)
  if (res["errorCode"] === ErrorCodes.userNotVerified) {
    await router.push("/verify-email?email=" + email)
    loadingStore.stopLoading();
    return;
  }
  if (res["errorCode"] === ErrorCodes.userDeactivated) {
    localStorage.setItem('oauth2Error', 'Tài khoản của bạn hiện tại bị khóa. Vui lòng liên hệ quản trị viên để được hỗ trợ.');
    await router.push("/login");
    loadingStore.stopLoading();
    return;
  }

  if (!res.errorCode) {
    userStore.login(res.data, token);
    const redirectPath = getRedirectPath(res.data);
    await router.push(redirectPath);
  } else {
    userStore.login(null, token);
    await router.push("/");
  }

  loadingStore.stopLoading();
})
</script>