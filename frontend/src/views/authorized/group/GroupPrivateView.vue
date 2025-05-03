<!-- src/components/group/GroupPrivateView.vue -->
<template>
  <div v-if="group" class="p-6 bg-secondary rounded-lg shadow text-center space-y-4">
    <h2 class="text-2xl font-semibold">{{ group.name }}</h2>
    <p class="text-text mb-4">{{ group.description }}</p>

    <button
        v-if="status === 'none'"
        @click="$emit('join')"
        class="px-6 py-2 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition"
    >
      {{ t('groups.join') }}
    </button>
    <span v-else-if="status === 'pending'" class="italic text-text/60">
      {{ t('groups.pending') }}
    </span>
    <span v-else-if="status === 'banned'" class="text-red-600">
      {{ t('groups.banned') }}
    </span>
  </div>

  <!-- fallback while `group` is still loading -->
  <div v-else class="p-6 text-center text-gray-500">
    ⏳ {{ t('common.loading') }}
  </div>
</template>

<script setup>
import { defineProps } from 'vue'
import { useI18n }     from 'vue-i18n'

const { t } = useI18n()

defineProps({
  group: {
    type: Object,
    required: false,
    default: null
  },
  status: {
    type: String,
    required: true
  }
})
</script>
