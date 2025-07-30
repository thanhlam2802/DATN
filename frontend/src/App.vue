<script setup>
import ToastContainer from '@/components/ToastContainer.vue';
import {onMounted} from "vue";
import {AccountApi} from "@/api/AccountApi.js";
import {useUserStore} from "@/store/UserStore.js";
import {useRouter} from "vue-router";
import {ErrorCodes} from "@/data/ErrorCode.js";

const userStore = useUserStore();
const router = useRouter();

onMounted(async () => {
  const res = await AccountApi.getProfile()
  console.log("res", res)
  if (res["errorCode"] === ErrorCodes.userNotVerified) {
    await router.push("/verify-email")
  }
})
</script>

<template>
  <div>
    <router-view/>
    <ToastContainer/>
  </div>
</template>

