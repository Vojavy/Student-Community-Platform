<template>
  <RouterView />
  <JwtExpiredModal />
  <LogoutSuccessModal />
</template>

<script setup>
import { RouterView } from 'vue-router'
import JwtExpiredModal from "@/components/global/JwtExpiredModal.vue";
import LogoutSuccessModal from "@/components/global/LogoutSuccessModal.vue";
</script>
