<template>
  <div class="p-6 max-w-3xl mx-auto bg-secondary rounded-lg shadow">
    <!-- Loading -->
    <div v-if="isLoading" class="text-center text-text/70 py-12">
      ⏳ {{ t('common.loading') }}
    </div>

    <!-- Not Found -->
    <div v-else-if="notFound" class="text-center text-text/70 py-12">
      {{ t('profile.notFound') }}
    </div>

    <!-- Profile Content -->
    <div v-else>
      <!-- Шапка -->
      <div class="flex items-center gap-6 mb-8">
        <img
            :src="userData.avatarUrl || defaultAvatar"
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
      <div
          v-if="userData.osCislo"
          class="bg-primary p-6 rounded-lg border border-gray-200 mb-8"
      >
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

      <!-- Friendship Actions -->
      <div v-if="!isOwner" class="flex flex-wrap gap-4 mb-8">
        <button
            v-if="isFriend"
            @click="removeFriend"
            class="px-6 py-2 rounded shadow bg-red-600 text-white hover:bg-red-700 transition"
        >
          {{ t('profile.friends.remove') }}
        </button>

        <template v-else-if="hasIncoming">
          <button
              @click="approveFriend"
              class="px-6 py-2 rounded shadow bg-accent-primary text-white hover:bg-accent-primary/90 transition"
          >
            {{ t('profile.friends.approve') }}
          </button>
          <button
              @click="declineFriend"
              class="px-6 py-2 rounded shadow border border-red-600 text-red-600 hover:bg-red-600/10 transition"
          >
            {{ t('profile.friends.decline') }}
          </button>
        </template>

        <button
            v-else-if="hasOutgoing"
            disabled
            class="px-6 py-2 rounded shadow text-text/60 border border-text/20"
        >
          {{ t('profile.friends.pending') }}
        </button>

        <button
            v-else
            @click="addFriend"
            class="px-6 py-2 rounded shadow bg-accent-primary text-white hover:bg-accent-primary/90 transition"
        >
          {{ t('profile.friends.add') }}
        </button>

        <button
            @click="onMessage"
            class="px-6 py-2 bg-accent-secondary text-white rounded shadow hover:bg-accent-secondary/90 transition"
        >
          {{ t('profile.buttons.sendMessage') }}
        </button>
      </div>

      <!--      TODO ПОФИКСИТЬ ВСЮ ХУЙНЮ ПОЖАЛУЙСТА-->
      <div class="mt-8">
        <h2 class="text-xl font-semibold mb-4">{{ t('profile.friends.title') }}</h2>

        <div v-if="friendsLoading" class="text-text/60">
          ⏳ {{ t('common.loading') }}
        </div>
        <!--      TODO ВЫВОДИТ ПЕНДИНГ ДРУЗЕЙ-->
        <div v-else-if="friends.length === 0" class="text-text/60">
          {{ t('profile.friends.empty') }}
        </div>
        <ul v-else class="divide-y divide-gray-200 bg-white rounded-lg shadow-sm">
          <li
              v-for="f in friends"
              :key="f.userId"
          >
            <button
                type="button"
                @click="coordinator.navigateToUser(f.userId)"
                class="w-full text-left px-4 py-3 hover:bg-accent-primary/10 transition"
            >
              <span class="text-left text-accent-primary font-medium">
                {{ f.name }}
              </span>
            </button>
          </li>
        </ul>
      </div>


    </div>
  </div>
</template>

<script setup>
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

// два отдельных `model`
const userModel   = createUserModel()
const friendModel= createFriendModel()

// состояние профиля
const isLoading = ref(true)
const notFound  = ref(false)
const userData  = ref({})

// состояние дружбы
const isFriend      = ref(false)
const hasIncoming   = ref(false)
const hasOutgoing   = ref(false)
const friends       = ref([])
const friendsLoading= ref(true)

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
  // 1) профиль
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

  // 2) друзья профиля
  friendsLoading.value = true
  friends.value = await handleFriendIntent(
      fetchFriendsIntent(viewedId),
      { model: friendModel }
  )
  friendsLoading.value = false

  // 3) мои заявки/друзья
  const myRelations = await handleFriendIntent(
      fetchMyFriendsIntent(),
      { model: friendModel }
  )
  isFriend.value    = myRelations.some(r => r.userId === viewedId && r.status === 'approved')
  hasOutgoing.value = myRelations.some(r => r.userId === viewedId && r.status === 'pending')

  // 4) входящие ко мне заявки
  const incoming = await handleFriendIntent(
      fetchIncomingRequestsIntent(),
      { model: friendModel }
  )
  hasIncoming.value = incoming.some(r => r.userId === viewedId)

  isLoading.value = false
})

// ————— методы дружбы —————————————

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
