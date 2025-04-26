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
      <!-- Header -->
      <div class="flex items-center gap-6 mb-4">
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
            <span class="font-medium">{{ t('profile.email') }}:</span>
            {{ userData.email }}
          </p>
          <p class="text-sm">
            <span class="font-medium">{{ t('profile.active') }}:</span>
            {{ userData.active ? t('profile.yes') : t('profile.no') }}
          </p>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="flex flex-wrap gap-4 mb-8">
        <button
            @click="goToSettings"
            class="flex-1 sm:flex-none px-6 py-2 bg-accent-primary text-white rounded shadow hover:bg-accent-primary/90 transition"
        >
          {{ t('profile.buttons.settings') }}
        </button>
        <button
            @click="goToStag"
            class="flex-1 sm:flex-none px-6 py-2 border-2 border-accent-secondary bg-accent-secondary/80 text-white rounded shadow hover:bg-accent-secondary/70 transition"
        >
          {{ t('profile.buttons.connectStag') }}
        </button>
      </div>

      <!-- STAG Info -->
      <div v-if="userData.osCislo" class="bg-primary p-6 rounded-lg border border-gray-200 mb-8">
        <h2 class="text-xl font-semibold text-accent-secondary mb-4">
          {{ t('stag.studentInfo') }}
        </h2>
        <dl class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div><dt class="font-medium text-text">{{ t('stag.field.osCislo') }}</dt><dd class="text-text">{{ userData.osCislo }}</dd></div>
          <div><dt class="font-medium text-text">{{ t('stag.field.faculty') }}</dt><dd class="text-text">{{ userData.fakultaSp }}</dd></div>
          <div><dt class="font-medium text-text">{{ t('stag.field.program') }}</dt><dd class="text-text">{{ userData.nazevSp }}</dd></div>
          <div><dt class="font-medium text-text">{{ t('stag.field.year') }}</dt><dd class="text-text">{{ userData.rocnik }}</dd></div>
        </dl>
      </div>

      <!-- Details Toggle -->
      <div class="mt-6 flex items-center justify-between">
        <h2 class="text-lg font-semibold text-text">{{ t('profile.detailsTitle') }}</h2>
        <button @click="showDetails = !showDetails" class="p-1">
          <svg
              :class="{ 'rotate-180': showDetails }"
              class="w-5 h-5 text-text transition-transform"
              fill="none" stroke="currentColor" viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M19 9l-7 7-7-7"/>
          </svg>
        </button>
      </div>
      <transition name="slide-fade">
        <div v-if="showDetails" class="mt-2 p-4 bg-primary rounded border border-gray-200">
          <dl class="space-y-4">
            <template v-for="key in filledDetailsKeys" :key="key">
              <div class="flex flex-col sm:flex-row sm:items-center">
                <dt class="w-1/3 font-medium text-text">{{ detailLabels[key] || key }}</dt>
                <dd class="flex-1 text-text">
                  <template v-if="isLink(filledDetails[key])">
                    <a
                        :href="normalizeLink(filledDetails[key])"
                        target="_blank" rel="noopener"
                        class="text-accent-primary hover:underline"
                    >
                      {{ filledDetails[key] }}
                    </a>
                  </template>
                  <template v-else>
                    {{ filledDetails[key] }}
                  </template>
                </dd>
              </div>
            </template>
            <div v-if="filledDetailsKeys.length === 0" class="text-text/60 italic">
              {{ t('profile.noDetails') }}
            </div>
          </dl>
        </div>
      </transition>

      <!-- Friends List -->
      <div class="mt-8">
        <h2 class="text-xl font-semibold mb-4">{{ t('profile.friends.title') }}</h2>

        <div v-if="friendsLoading" class="text-text/60">⏳ {{ t('common.loading') }}</div>
        <div v-else-if="friends.length === 0" class="text-text/60">{{ t('profile.friends.empty') }}</div>

        <ul v-else class="divide-y divide-gray-200 bg-white rounded-lg shadow-sm">
          <li v-for="f in friends" :key="f.userId">
            <button
                type="button"
                class="w-full flex items-center justify-between px-4 py-3 hover:bg-accent-primary/10 transition"
                @click="coordinator.navigateToUser(f.userId)"
            >
              <span class="text-accent-primary font-medium">{{ f.name }}</span>
              <button
                  type="button"
                  class="px-3 py-1 rounded border border-red-600 text-red-600 hover:bg-red-600/10 transition"
                  @click.stop="removeFriend(f.userId)"
              >
                {{ t('profile.friends.remove') }}
              </button>
            </button>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import createUserModel from '@/iam/models/userModel'
import createFriendModel from '@/iam/models/friendModel'
import { fetchUserProfileIntent } from '@/iam/intents/userIntents'
import { fetchMyFriendsIntent, removeFriendIntent } from '@/iam/intents/friendIntents'
import { handleUserIntent } from '@/iam/actions/userActions'
import { handleFriendIntent } from '@/iam/actions/friendActions'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'

const { t } = useI18n()
const coordinator = inject('coordinator')
const userModel = createUserModel()
const friendModel = createFriendModel()

const isLoading     = ref(true)
const notFound      = ref(false)
const userData      = ref({})
const showDetails   = ref(false)
const friends       = ref([])
const friendsLoading= ref(true)
const defaultAvatar = '/default-avatar.png'

const formattedDate = computed(() => {
  if (!userData.value.createdAt) return ''
  return `${t('profile.registered')}: ${new Date(userData.value.createdAt).toLocaleDateString()}`
})

const detailLabels = {
  bio:       t('profile.settings.fields.bio'),
  status:    t('profile.settings.fields.status'),
  skills:    t('profile.settings.fields.skills'),
  languages: t('profile.personal.languages'),
  location:  t('profile.personal.location'),
  website:   t('profile.personal.website'),
  ...Object.fromEntries(Object.entries(t('profile.contacts')).map(([k,v]) => [k, v]))
}

const filledDetails = computed(() => {
  const d = userData.value.details || {}
  return Object.fromEntries(
      Object.entries(d)
          .filter(([_,v]) => v != null && !(Array.isArray(v) && v.length === 0) && !(typeof v === 'object' && !Array.isArray(v) && Object.keys(v).length === 0))
          .map(([k,v]) => [k, Array.isArray(v) ? v.join(', ') : v])
  )
})
const filledDetailsKeys = computed(() => Object.keys(filledDetails.value))

function isLink(str) {
  return /^https?:\/\//.test(str) || /^www\./.test(str)
}
function normalizeLink(str) {
  return str.startsWith('http') ? str : `https://${str}`
}

onMounted(async () => {
  try {
    const myId = getUserIdFromToken()
    userData.value = await handleUserIntent(fetchUserProfileIntent(myId), { model: userModel })

    friendsLoading.value = true
    friends.value = await handleFriendIntent(fetchMyFriendsIntent(), { model: friendModel })
    friendsLoading.value = false
  } catch (err) {
    if (err.response?.status === 404) notFound.value = true
    else console.error(err)
  } finally {
    isLoading.value = false
  }
})

async function removeFriend(id) {
  await handleFriendIntent(removeFriendIntent(id), { model: friendModel })
  friends.value = friends.value.filter(f => f.userId !== id)
}

const goToSettings = () => coordinator.navigateToUserSettings()
const goToStag     = () => coordinator.navigateToUserStag()
</script>

<style>
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
