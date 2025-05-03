<template>
  <div class="max-w-2xl mx-auto p-6 space-y-6 bg-secondary rounded-lg shadow">
    <h1 class="text-2xl font-bold">{{ t('groups.createTitle') }}</h1>
    <form @submit.prevent="onSubmit" class="space-y-4">
      <!-- Domain -->
      <div>
        <label class="block font-medium mb-1">{{ t('groups.form.domain') }}</label>
        <select v-model="form.domain" class="w-full p-2 border rounded bg-primary text-text">
          <option value="">{{ t('groups.form.domainNone') }}</option>
          <option v-for="d in domains" :key="d.id" :value="d.domain">
            {{ d.domainName }}
          </option>
        </select>
      </div>

      <!-- Name -->
      <div>
        <label class="block font-medium mb-1">{{ t('groups.form.name') }}</label>
        <input
            v-model="form.name"
            type="text"
            class="w-full p-2 border rounded bg-primary text-text"
            required
        />
      </div>

      <!-- Description -->
      <div>
        <label class="block font-medium mb-1">{{ t('groups.form.description') }}</label>
        <textarea
            v-model="form.description"
            rows="4"
            class="w-full p-2 border rounded bg-primary text-text"
        ></textarea>
      </div>

      <!-- Topics -->
      <div class="space-y-2">
        <label class="block font-medium mb-1">{{ t('groups.form.topics') }}</label>
        <!-- Existing topics -->
        <ul class="list-disc list-inside space-y-1 mb-2">
          <li v-for="(topic, idx) in topics" :key="idx" class="flex items-center gap-2">
            <span class="flex-1">{{ topic }}</span>
            <button
                type="button"
                class="px-2 py-1 text-sm border rounded border-accent-primary text-accent-primary hover:bg-accent-primary/10 transition"
                @click="startEdit(idx)"
            >
              {{ t('common.change') }}
            </button>
            <button
                type="button"
                class="px-2 py-1 text-sm border rounded border-red-600 text-red-600 hover:bg-red-600/10 transition"
                @click="removeTopic(idx)"
            >
              {{ t('common.delete') }}
            </button>
          </li>
        </ul>

        <!-- Add / Edit topic form -->
        <div class="flex gap-2">
          <input
              v-model="topicValue"
              type="text"
              :placeholder="editingTopicIndex === null ? t('groups.form.newTopicPlaceholder') : t('common.change')"
              class="flex-1 p-2 border rounded bg-primary text-text"
          />
          <button
              type="button"
              class="px-4 py-2 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition"
              @click="confirmTopic"
          >
            {{ editingTopicIndex === null ? t('common.add') : t('common.save') }}
          </button>
          <button
              v-if="editingTopicIndex !== null"
              type="button"
              class="px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400 transition"
              @click="cancelEdit"
          >
            {{ t('common.cancel') }}
          </button>
        </div>
      </div>

      <!-- Public -->
      <div class="flex items-center gap-4">
        <label class="flex items-center gap-2">
          <input type="checkbox" v-model="form.public" class="accent-accent-primary" />
          {{ t('groups.form.public') }}
        </label>
      </div>

      <!-- Submit -->
      <div class="text-right">
        <button
            type="submit"
            class="px-6 py-2 bg-accent-primary text-white rounded shadow hover:bg-accent-primary/90 transition"
        >
          {{ t('groups.form.createButton') }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { inject } from 'vue'
import { useGroupStore } from '@/iam/stores/groupStore.js'
import { useDomainStore } from '@/iam/stores/domainStore.js'
import { useUserStore } from '@/iam/stores/userStore.js'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'

const { t } = useI18n()
const coordinator = inject('coordinator')
const groupStore = useGroupStore()
const domainStore = useDomainStore()
const userStore   = useUserStore()

const userId = getUserIdFromToken()
const domains = ref([])

const form = reactive({
  domain:      '',
  name:        '',
  description: '',
  public:      true
})

const topics = ref([])
const editingTopicIndex = ref(null)
const topicValue = ref('')

onMounted(async () => {
  await domainStore.fetchDomains()
  domains.value = domainStore.domains

  try {
    await userStore.fetchProfile(userId)
    if (userStore.profile.domain) {
      form.domain = userStore.profile.domain
    }
  } catch {  }
})

function confirmTopic() {
  const val = topicValue.value.trim()
  if (!val) return
  if (editingTopicIndex.value === null) {
    topics.value.push(val)
  } else {
    topics.value[editingTopicIndex.value] = val
  }
  cancelEdit()
}

function startEdit(idx) {
  editingTopicIndex.value = idx
  topicValue.value = topics.value[idx]
}

function removeTopic(idx) {
  topics.value.splice(idx, 1)
  if (editingTopicIndex.value === idx) cancelEdit()
}

function cancelEdit() {
  editingTopicIndex.value = null
  topicValue.value = ''
}

async function onSubmit() {
  const groupData = {
    name:             form.name,
    description:      form.description,
    topics:           topics.value,
    public:           form.public,
    minRoleForPosts:  'member',
    minRoleForEvents: 'admin',
    domain:           form.domain || undefined
  }

  const created = await groupStore.createGroup(groupData)
  coordinator.navigateToGroup(created.id)
}
</script>
