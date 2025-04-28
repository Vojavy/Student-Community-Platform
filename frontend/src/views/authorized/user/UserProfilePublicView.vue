<template>
  <div class="p-8 max-w-4xl mx-auto bg-gray-50 rounded-xl shadow-lg">
    <!-- Loading / Not Found -->
    <div v-if="isLoading" class="text-center text-gray-400 py-16">
      ⏳ {{ t('common.loading') }}
    </div>
    <div v-else-if="notFound" class="text-center text-gray-400 py-16">
      {{ t('profile.notFound') }}
    </div>

    <!-- Profile Content -->
    <div v-else>
      <!-- Header -->
      <div class="flex items-center gap-6 mb-8">
        <img
            :src="userData.avatarUrl || defaultAvatar"
            alt="Аватар"
            class="w-24 h-24 rounded-full ring-4 ring-indigo-300 object-cover"
        />
        <div>
          <h1 class="text-4xl font-extrabold text-gray-800">
            <span v-if="userData.titulPred" class="mr-1">{{ userData.titulPred }}</span>
            {{ userData.jmeno }} {{ userData.prijmeni }}
            <span v-if="userData.titulZa" class="ml-1 text-lg text-gray-500">, {{ userData.titulZa }}</span>
          </h1>
          <p class="mt-1 text-sm text-gray-500">{{ formattedDate }}</p>
          <div class="mt-2 space-x-4 text-sm text-gray-700">
            <span><strong>{{ t('profile.username') }}:</strong> {{ userData.userName }}</span>
            <span>
              <strong>{{ t('profile.active') }}:</strong>
              <span :class="userData.active ? 'text-green-600' : 'text-red-600'">
                {{ userData.active ? t('profile.yes') : t('profile.no') }}
              </span>
            </span>
          </div>
        </div>
      </div>

      <!-- STAG Info -->
      <div
          v-if="userData.osCislo"
          class="mb-8 px-6 py-6 bg-white rounded-lg shadow-sm border-l-4 border-indigo-600"
      >
        <h2 class="text-2xl font-semibold text-indigo-600 mb-4">
          {{ t('stag.studentInfo') }}
        </h2>
        <dl class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-700">
          <div>
            <dt class="font-medium">{{ t('stag.field.osCislo') }}</dt>
            <dd>{{ userData.osCislo }}</dd>
          </div>
          <div>
            <dt class="font-medium">{{ t('stag.field.faculty') }}</dt>
            <dd>{{ userData.fakultaSp }}</dd>
          </div>
          <div>
            <dt class="font-medium">{{ t('stag.field.program') }}</dt>
            <dd>{{ userData.nazevSp }}</dd>
          </div>
          <div>
            <dt class="font-medium">{{ t('stag.field.year') }}</dt>
            <dd>{{ userData.rocnik }}</dd>
          </div>
        </dl>
      </div>

      <!-- Friendship Actions -->
      <div v-if="!isOwner" class="flex flex-col sm:flex-row gap-4 mb-8">
        <button
            v-if="isFriend"
            @click="removeFriend"
            class="flex-1 px-6 py-3 bg-red-600 text-white font-medium rounded-lg hover:bg-red-700 transition"
        >
          {{ t('profile.friends.remove') }}
        </button>

        <template v-else-if="hasIncoming">
          <button
              @click="approveFriend"
              class="flex-1 px-6 py-3 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition"
          >
            {{ t('profile.friends.approve') }}
          </button>
          <button
              @click="declineFriend"
              class="flex-1 px-6 py-3 border border-red-600 text-red-600 font-medium rounded-lg hover:bg-red-50 transition"
          >
            {{ t('profile.friends.decline') }}
          </button>
        </template>

        <button
            v-else-if="hasOutgoing"
            disabled
            class="flex-1 px-6 py-3 border border-gray-200 text-gray-400 font-medium rounded-lg"
        >
          {{ t('profile.friends.pending') }}
        </button>

        <button
            v-else
            @click="addFriend"
            class="flex-1 px-6 py-3 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition"
        >
          {{ t('profile.friends.add') }}
        </button>

        <button
            @click="onMessage"
            class="flex-1 px-6 py-3 bg-indigo-500 text-white font-medium rounded-lg hover:bg-indigo-600 transition"
        >
          {{ t('profile.buttons.sendMessage') }}
        </button>
      </div>

      <!-- Friends List -->
      <div>
        <h2 class="text-2xl font-semibold mb-4 text-gray-800">{{ t('profile.friends.title') }}</h2>
        <div v-if="friendsLoading" class="text-gray-400">⏳ {{ t('common.loading') }}</div>
        <div v-else-if="friends.length === 0" class="text-gray-400">{{ t('profile.friends.empty') }}</div>
        <ul v-else class="bg-white rounded-lg shadow-sm divide-y divide-gray-200">
          <li v-for="f in friends" :key="f.userId">
            <div
                class="flex items-center justify-between px-6 py-4 hover:bg-indigo-50 transition cursor-pointer"
                @click="coordinator.navigateToUser(f.userId)"
            >
              <span class="text-indigo-600 font-medium">{{ f.name }}</span>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
// (Скрипт оставляем без изменений)
import { ref, onMounted, computed, inject } from 'vue'
import { useRoute }     from 'vue-router'
import { useI18n }      from 'vue-i18n'

import createUserModel     from '@/iam/models/userModel'
import { fetchUserProfileIntent } from '@/iam/intents/userIntents'
import { handleUserIntent }      from '@/iam/actions/userActions'

import createFriendModel from '@/iam/models/friendModel'
import {
  fetchFriendsIntent,
  fetchMyFriendsIntent,
  fetchIncomingRequestsIntent,
  sendFriendRequestIntent,
  approveFriendRequestIntent,
  declineFriendRequestIntent,
  removeFriendIntent
} from '@/iam/intents/friendIntents'
import { handleFriendIntent } from '@/iam/actions/friendActions'

import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'

const route       = useRoute()
const coordinator = inject('coordinator')
const { t }       = useI18n()

const userModel    = createUserModel()
const friendModel  = createFriendModel()

const isLoading      = ref(true)
const notFound       = ref(false)
const userData       = ref({})
const isFriend       = ref(false)
const hasIncoming    = ref(false)
const hasOutgoing    = ref(false)
const friends        = ref([])
const friendsLoading = ref(true)

const selfId   = getUserIdFromToken()
const viewedId = Number(route.params.id)
const isOwner  = computed(() => selfId === viewedId)

const defaultAvatar = '/default-avatar.png'
const formattedDate = computed(() => {
  if (!userData.value.createdAt) return ''
  const d = new Date(userData.value.createdAt)
  return `${t('profile.registered')}: ${d.toLocaleDateString()}`
})

onMounted(async () => {
  try {
    userData.value = await handleUserIntent(
        fetchUserProfileIntent(viewedId),
        { model: userModel }
    )
  } catch (err) {
    if (err.response?.status === 404) {
      notFound.value = true
      isLoading.value = false
      return
    }
    console.error(err)
  }

  friendsLoading.value = true
  friends.value = await handleFriendIntent(
      fetchFriendsIntent(viewedId),
      { model: friendModel }
  )
  friendsLoading.value = false

  const myRelations = await handleFriendIntent(
      fetchMyFriendsIntent(),
      { model: friendModel }
  )
  isFriend.value    = myRelations.some(r => r.userId === viewedId && r.status === 'approved')
  hasOutgoing.value = myRelations.some(r => r.userId === viewedId && r.status === 'pending')

  const incoming = await handleFriendIntent(
      fetchIncomingRequestsIntent(),
      { model: friendModel }
  )
  hasIncoming.value = incoming.some(r => r.userId === viewedId)

  isLoading.value = false
})

async function addFriend() {
  await handleFriendIntent(
      sendFriendRequestIntent(viewedId),
      { model: friendModel }
  )
  hasOutgoing.value = true
}

async function approveFriend() {
  await handleFriendIntent(
      approveFriendRequestIntent(viewedId),
      { model: friendModel }
  )
  isFriend.value    = true
  hasIncoming.value = false
}

async function declineFriend() {
  await handleFriendIntent(
      declineFriendRequestIntent(viewedId),
      { model: friendModel }
  )
  hasIncoming.value = false
}

async function removeFriend() {
  await handleFriendIntent(
      removeFriendIntent(viewedId),
      { model: friendModel }
  )
  isFriend.value = false
}

function onMessage() {
  coordinator.navigateToChat(viewedId)
}
</script>

<style scoped>
/* Плавные анимации (если нужны дальше) */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all .2s ease;
}
.slide-fade-enter-from,
.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
