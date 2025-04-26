<template>
  <div>
    <h1 class="text-2xl font-semibold mb-4">{{ t('groups.calendar') }}</h1>
    <!-- TODO: здесь календарь событий -->
  </div>
</template>

<script setup>
import { inject } from 'vue'
const group = inject('group')
// можно подгружать через интент fetchEventsIntent
</script>
