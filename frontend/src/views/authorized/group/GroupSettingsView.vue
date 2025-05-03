<!-- src/views/authorized/Group/GroupSettingsView.vue -->
<template>
  <div class="space-y-6 max-w-xl mx-auto">
    <h1 class="text-2xl font-semibold">{{ t('groups.settings') }}</h1>
    <form @submit.prevent="onSave" class="space-y-4">
      <!-- name -->
      <div>
        <label class="block text-sm font-medium mb-1">{{ t('groups.name') }}</label>
        <input v-model="name" type="text" class="w-full border rounded px-3 py-2" />
      </div>

      <!-- description -->
      <div>
        <label class="block text-sm font-medium mb-1">{{ t('groups.description') }}</label>
        <textarea v-model="description" class="w-full border rounded px-3 py-2"></textarea>
      </div>

      <!-- public -->
      <div class="flex items-center space-x-2">
        <input id="public" type="checkbox" v-model="isPublic" />
        <label for="public">{{ t('groups.public') }}</label>
      </div>

      <!-- domain -->
      <div>
        <label class="block text-sm font-medium mb-1">{{ t('groups.domain') }}</label>
        <select v-model="selectedDomain" class="w-full border rounded px-3 py-2">
          <option :value="null">{{ t('groups.noDomain') }}</option>
          <option
              v-for="d in domains"
              :key="d.domain"
              :value="d.domain"
          >
            {{ d.domainName }}
          </option>
        </select>
      </div>

      <!-- minRoleForPosts -->
      <div>
        <label class="block text-sm font-medium mb-1">{{ t('groups.minRoleForPosts') }}</label>
        <select v-model="minRoleForPosts" class="w-full border rounded px-3 py-2">
          <option value="member">{{ t('groups.role.member') }}</option>
          <option value="helper">{{ t('groups.role.helper') }}</option>
          <option value="admin">{{ t('groups.role.admin') }}</option>
          <option value="owner">{{ t('groups.role.owner') }}</option>
        </select>
      </div>

      <!-- minRoleForEvents -->
      <div>
        <label class="block text-sm font-medium mb-1">{{ t('groups.minRoleForEvents') }}</label>
        <select v-model="minRoleForEvents" class="w-full border rounded px-3 py-2">
          <option value="member">{{ t('groups.role.member') }}</option>
          <option value="helper">{{ t('groups.role.helper') }}</option>
          <option value="admin">{{ t('groups.role.admin') }}</option>
          <option value="owner">{{ t('groups.role.owner') }}</option>
        </select>
      </div>

      <!-- topics -->
      <div class="space-y-2">
        <label class="block text-sm font-medium mb-1">{{ t('groups.topics') }}</label>

        <!-- existing tags -->
        <div v-for="(tag, idx) in topics" :key="idx" class="flex items-center gap-2">
          <template v-if="editingTopic === idx">
            <input
                v-model="topicValue"
                ref="topicInput"
                class="flex-1 border rounded px-2 py-1"
            />
            <button
                type="button"
                class="px-3 py-1 bg-accent-primary text-white rounded"
                @click="saveTopic(idx)"
            >{{ t('common.save') }}</button>
            <button
                type="button"
                class="px-3 py-1 bg-gray-300 text-gray-700 rounded"
                @click="cancelEdit()"
            >{{ t('common.cancel') }}</button>
          </template>
          <template v-else>
            <span class="px-2 py-1 bg-primary rounded-full text-sm">{{ tag }}</span>
            <button
                type="button"
                class="px-2 py-1 text-sm border rounded border-accent-primary text-accent-primary"
                @click="startEdit(idx)"
            >{{ t('common.change') }}</button>
            <button
                type="button"
                class="px-2 py-1 text-sm border rounded border-red-600 text-red-600"
                @click="removeTopic(idx)"
            >{{ t('common.delete') }}</button>
          </template>
        </div>

        <!-- add new tag -->
        <div class="flex gap-2">
          <input
              v-model="newTopic"
              placeholder="New topic…"
              class="flex-1 border rounded px-2 py-1"
              @keyup.enter="addTopic"
          />
          <button
              type="button"
              class="px-3 py-1 bg-accent-primary text-white rounded"
              :disabled="!newTopic.trim()"
              @click="addTopic"
          >{{ t('common.add') }}</button>
        </div>
      </div>

      <!-- save button -->
      <button
          type="submit"
          class="px-4 py-2 bg-accent-primary text-white rounded hover:bg-accent-secondary transition"
      >
        {{ t('common.save') }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useI18n }               from 'vue-i18n'
import { inject }                from 'vue'
import checkRights               from '@/utils/groups/checkRights'
import { useGroupStore }         from '@/iam/stores/groupStore.js'
import { useDomainStore }        from '@/iam/stores/domainStore.js'

const { t }         = useI18n()
const coordinator   = inject('coordinator')
const store         = useGroupStore()
const domainStore   = useDomainStore()

const group            = computed(() => store.currentGroup || {})
const role             = computed(() => store.memberStatus.role)
const name             = ref(group.value.name)
const description      = ref(group.value.description)
const isPublic         = ref(group.value.isPublic)
const selectedDomain   = ref(group.value.domain?.domain ?? null)
const minRoleForPosts  = ref(group.value.minRoleForPosts)
const minRoleForEvents = ref(group.value.minRoleForEvents)

const topics         = ref(Array.isArray(group.value.topics) ? [...group.value.topics] : [])
const editingTopic   = ref(null)
const topicValue     = ref('')
const newTopic       = ref('')

const domains = computed(() => domainStore.domains)

onMounted(async () => {
  if (!checkRights(role.value, group.value.minRoleForEvents)) {
    coordinator.navigateToGroup(group.value.id)
    return
  }
  await domainStore.fetchDomains()
})

function startEdit(idx) {
  editingTopic.value = idx
  topicValue.value = topics.value[idx]
  nextTick(() => {
    const inp = document.querySelector('input[ref="topicInput"]')
    inp?.focus()
  })
}
function saveTopic(idx) {
  const v = topicValue.value.trim()
  if (v) topics.value.splice(idx, 1, v)
  cancelEdit()
}
function cancelEdit() {
  editingTopic.value = null
  topicValue.value = ''
}
function removeTopic(idx) {
  topics.value.splice(idx, 1)
}
function addTopic() {
  const v = newTopic.value.trim()
  if (v && !topics.value.includes(v)) {
    topics.value.push(v)
  }
  newTopic.value = ''
}

async function onSave() {
  const settingsData = {
    name: name.value,
    description: description.value,
    isPublic: isPublic.value,
    domain: selectedDomain.value,
    minRoleForPosts: minRoleForPosts.value,
    minRoleForEvents: minRoleForEvents.value,
    topics: topics.value
  }
  await store.updateGroupSettings(group.value.id, settingsData)
  store.currentGroup = { ...store.currentGroup, ...settingsData }
}
</script>
