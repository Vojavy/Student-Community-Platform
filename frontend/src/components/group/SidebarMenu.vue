<template>
  <nav class="space-y-2">
    <button
        @click="goOverview"
        class="w-full text-left px-3 py-2 rounded hover:bg-primary transition"
    >
      {{ $t('groups.overview') }}
    </button>

    <button
        @click="goMembers"
        class="w-full text-left px-3 py-2 rounded hover:bg-primary transition flex justify-between"
    >
      <span>{{ $t('groups.members') }}</span>
      <span class="text-sm text-text/60">{{ members.length }}</span>
    </button>

    <button
        @click="goCalendar"
        class="w-full text-left px-3 py-2 rounded hover:bg-primary transition"
    >
      {{ $t('groups.calendar') }}
    </button>

    <button
        v-if="['admin','owner'].includes(role)"
        @click="goSettings"
        class="w-full text-left px-3 py-2 rounded hover:bg-primary transition"
    >
      {{ $t('groups.settings') }}
    </button>
  </nav>
</template>

<script setup>
import { inject } from 'vue'
import { useI18n } from 'vue-i18n'

const { t }     = useI18n()
const coordinator = inject('coordinator')
const group    = inject('group')
const members  = inject('members')
const role     = inject('role')

const groupId = group.value.id

function goOverview() {
  coordinator.navigateToGroup(groupId)
}
function goMembers() {
  coordinator.navigateToGroupMembers(groupId)
}
function goCalendar() {
  coordinator.navigateToGroupCalendar(groupId)
}
function goSettings() {
  coordinator.navigateToGroupSettings(groupId)
}
</script>
