<template>
  <div class="p-6 w-full h-full overflow-y-auto">
    <RouterView />
  </div>
</template>

<script setup>
import { RouterView } from 'vue-router'
import { useI18n } from "vue-i18n";

const { t } = useI18n()
</script>
