<template>
  <div class="max-w-2xl mx-auto p-6 space-y-6 bg-secondary rounded-lg shadow">
    <h1 class="text-2xl font-bold">{{ t('groups.createTitle') }}</h1>

    <form @submit.prevent="onSubmit" class="space-y-4">
      <!-- Domain -->
      <div>
        <label class="block font-medium mb-1">{{ t('groups.form.domain') }}</label>
        <input
            type="text"
            v-model="form.domain"
            class="w-full p-2 border rounded bg-primary text-text"
            readonly
        />
      </div>

      <!-- Name -->
      <div>
        <label class="block font-medium mb-1">{{ t('groups.form.name') }}</label>
        <input
            type="text"
            v-model="form.name"
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
      <div>
        <label class="block font-medium mb-1">{{ t('groups.form.topics') }}</label>
        <input
            type="text"
            v-model="form.topics"
            placeholder="tag1,tag2,…"
            class="w-full p-2 border rounded bg-primary text-text"
        />
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
import { reactive, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { inject } from 'vue'
import createGroupModel from '@/models/groupModel'
import createUserModel from '@/models/userModel'
import { fetchUserProfileIntent } from '@/intents/userIntents'
import { createGroupIntent } from '@/intents/groupIntents'
import { handleGroupIntent } from '@/actions/groupActions'
import { handleUserIntent } from '@/actions/userActions'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'

const { t } = useI18n()
const coordinator = inject('coordinator')
const groupModel = createGroupModel()
const userModel = createUserModel()

const userId = getUserIdFromToken()

const form = reactive({
  domain: '',
  name: '',
  description: '',
  topics: '',
  public: true
})

onMounted(async () => {
  try {
    const profile = await handleUserIntent(
        fetchUserProfileIntent(userId),
        { model: userModel }
    )
    form.domain = profile.domain || ''
  } catch {
    form.domain = ''
  }
})

async function onSubmit() {
  const topicsArr = form.topics
      .split(',')
      .map(s => s.trim())
      .filter(Boolean)

  const groupData = {
    name: form.name,
    description: form.description,
    topics: topicsArr,
    public: form.public,
    minRoleForPosts: 'member',
    minRoleForEvents: 'admin',
    domain: form.domain || undefined
  }

  const created = await handleGroupIntent(
      createGroupIntent(groupData),
      { model: groupModel }
  )

  coordinator.navigateToGroup(created.id)
}
</script>
