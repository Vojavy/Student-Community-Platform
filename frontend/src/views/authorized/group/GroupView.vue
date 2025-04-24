<template>
  <div class="p-6 bg-secondary rounded-lg shadow space-y-6">
    <div class="flex justify-between items-center">
      <h2 class="text-2xl font-semibold">{{ group.name }}</h2>
      <button
          v-if="isMember"
          @click="$emit('leave')"
          class="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition"
      >
        {{ t('groups.leave') }}
      </button>
    </div>

    <div class="flex flex-wrap gap-4 text-sm text-text/70">
      <span>{{ t('groups.createdAt') }}: {{ formattedDate }}</span>
      <span>
        {{ t('groups.domain') }}:
        <span v-if="group.domain">{{ group.domain.domainName }}</span>
        <span v-else>—</span>
      </span>
      <span :class="group.public ? 'text-green-600' : 'text-red-600'">
        {{ group.public ? t('groups.public') : t('groups.private') }}
      </span>
    </div>

    <p class="text-text">{{ group.description }}</p>

    <div v-if="group.topics.length" class="flex flex-wrap gap-2">
      <span
          v-for="tag in group.topics"
          :key="tag"
          class="px-2 py-1 bg-primary rounded-full text-sm"
      >#{{ tag }}</span>
    </div>

    <div class="space-y-4 text-text">
      <div>
        <strong>{{ t('groups.owner') }}:</strong>
        {{ group.owner.firstName }} {{ group.owner.lastName }}
      </div>

      <div v-if="group.admins.length">
        <strong>{{ t('groups.admins') }}:</strong>
        <span v-for="u in group.admins" :key="u.userId">
          {{ u.firstName }} {{ u.lastName }}
          <span v-if="!isLast(group.admins, u)">, </span>
        </span>
      </div>

      <div v-if="group.helpers.length">
        <strong>{{ t('groups.helpers') }}:</strong>
        <span v-for="u in group.helpers" :key="u.userId">
          {{ u.firstName }} {{ u.lastName }}
          <span v-if="!isLast(group.helpers, u)">, </span>
        </span>
      </div>

      <div v-if="members.length">
        <strong>{{ t('groups.members') }}:</strong>
        <ul class="list-disc list-inside ml-4">
          <li v-for="u in members" :key="u.userId">
            {{ u.firstName }} {{ u.lastName }}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

defineProps({
  group:    Object,
  members:  Array,
  isMember: Boolean
})

const formattedDate = computed(() =>
    new Date(group.createdAt).toLocaleDateString()
)

function isLast(arr, item) {
  return arr.indexOf(item) === arr.length - 1
}
</script>
