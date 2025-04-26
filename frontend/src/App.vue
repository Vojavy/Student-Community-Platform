<template>
  <!-- Контейнер приложения -->
  <main
      class="app-container"
      :style="{ paddingBottom: isMobile ? '50px' : '0' }"
  >
    <RouterView />
  </main>

  <!-- Модалки остаются полноэкранными -->
  <JwtExpiredModal />
  <LogoutSuccessModal />
</template>

<script setup>
import { RouterView } from 'vue-router'
import JwtExpiredModal    from '@/components/global/JwtExpiredModal.vue'
import LogoutSuccessModal from '@/components/global/LogoutSuccessModal.vue'

/* детектируем мобильный экран (по умолчанию брейк-поинт 768 px) */
import { useIsMobile } from '@/utils/device/useIsMobile'
const isMobile = useIsMobile()        // ref<boolean>
</script>

<style scoped>
/* ширина и горизонтальное центрирование одинаковы для всех устройств */
.app-container {
  margin: 0 auto;
  /* padding-bottom задаётся динамически через :style */
}
</style>
