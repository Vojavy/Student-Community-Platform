<template>
  <div>
    <nav class="tabs">
      <RouterLink to="/forum"      class="tab">Поиск</RouterLink>
      <RouterLink to="/forum/info" class="tab">Инфо-форумы</RouterLink>
    </nav>
    <RouterView />
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<style scoped>
.tabs { display: flex; gap: 1rem; margin-bottom: 1rem; }
.tab  { text-decoration: none; padding: .5rem 1rem; border-bottom: 2px solid transparent; }
.tab.router-link-active { border-color: var(--color-accent-primary); }
</style>
