<template>
  <div class="p-6 max-w-3xl mx-auto bg-secondary rounded-lg shadow">
    <div v-if="isLoading" class="text-center text-text/70 py-12">
      ⏳ {{ t('loading') }}
    </div>

    <div v-else>
      <!-- Шапка -->
      <div class="flex items-center gap-6 mb-8">
        <img
            alt="Аватар"
            class="w-20 h-20 rounded-full object-cover border-2 border-accent-primary"
        />
        <div>
          <h1 class="text-3xl font-bold text-accent-primary">
            {{ userData.titulPred ? userData.titulPred + ' ' : '' }}
            {{ userData.jmeno }} {{ userData.prijmeni }}
            {{ userData.titulZa ? ', ' + userData.titulZa : '' }}
          </h1>
          <p class="text-sm text-text/70">{{ formattedDate }}</p>
          <p class="text-sm">
            <span class="font-medium">{{ t('profile.username') }}:</span>
            {{ userData.userName }}
          </p>
          <p class="text-sm">
            <span class="font-medium">{{ t('profile.active') }}:</span>
            {{ userData.active ? t('profile.yes') : t('profile.no') }}
          </p>
        </div>
      </div>

      <!-- STAG данные -->
      <div v-if="userData.osCislo" class="bg-primary p-6 rounded-lg border border-gray-200 mb-8">
        <h2 class="text-xl font-semibold text-accent-secondary mb-4">
          {{ t('stag.studentInfo') }}
        </h2>
        <dl class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <dt class="font-medium text-text">{{ t('stag.field.osCislo') }}</dt>
            <dd class="text-text">{{ userData.osCislo }}</dd>
          </div>
          <div>
            <dt class="font-medium text-text">{{ t('stag.field.faculty') }}</dt>
            <dd class="text-text">{{ userData.fakultaSp }}</dd>
          </div>
          <div>
            <dt class="font-medium text-text">{{ t('stag.field.program') }}</dt>
            <dd class="text-text">{{ userData.nazevSp }}</dd>
          </div>
          <div>
            <dt class="font-medium text-text">{{ t('stag.field.year') }}</dt>
            <dd class="text-text">{{ userData.rocnik }}</dd>
          </div>
        </dl>
      </div>

      <!-- Кнопки действий -->
      <div v-if="!isOwner" class="flex flex-wrap gap-4">
        <button
            @click="toggleFriend"
            :class="[
              'px-6 py-2 rounded shadow transition',
              isFriend
                ? 'bg-red-600 text-white hover:bg-red-700'
                : 'bg-accent-primary text-white hover:bg-accent-primary/90'
            ]"
        >
          {{ isFriend ? t('profile.buttons.removeFriend') : t('profile.buttons.addFriend') }}
        </button>
        <button
            @click="onMessage"
            class="px-6 py-2 bg-accent-secondary text-white rounded shadow transition hover:bg-accent-secondary/90"
        >
          {{ t('profile.buttons.sendMessage') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, inject } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import createUserModel from '@/models/userModel'
import { fetchUserProfileIntent } from '@/intents/userIntents'
import { handleUserIntent } from '@/actions/userActions'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'

const route = useRoute()
const coordinator = inject('coordinator')
const model = createUserModel()
const { t } = useI18n()

const isLoading = ref(true)
const userData = ref({})
const isFriend = ref(false)

const selfId = getUserIdFromToken()
const viewedId = Number(route.params.id)
const isOwner = computed(() => selfId === viewedId)

const formattedDate = computed(() => {
  if (!userData.value.createdAt) return ''
  const d = new Date(userData.value.createdAt)
  return `${t('profile.registered')}: ${d.toLocaleDateString()}`
})

onMounted(async () => {
  const intent = fetchUserProfileIntent(viewedId)
  userData.value = await handleUserIntent(intent, { model })

  // TODO: Реализовать проверку isFriend с бэка
  // isFriend.value = userData.value.isFriend || false

  isLoading.value = false
})

const toggleFriend = async () => {
  // TODO: Реализовать добавление / удаление из друзей
  isFriend.value = !isFriend.value
}

const onMessage = () => {
  coordinator.navigateToChat(viewedId)
}
</script>
