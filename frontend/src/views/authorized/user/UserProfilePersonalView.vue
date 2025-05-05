<!-- src/views/authorized/UserProfilePersonalView.vue -->
<template>
  <div class="p-8 max-w-4xl mx-auto bg-gray-50 rounded-xl shadow-lg">
    <!-- Загрузка / 404 -->
    <div v-if="isLoading" class="text-center text-gray-400 py-16">
      ⏳ {{ t('common.loading') }}
    </div>
    <div v-else-if="notFound" class="text-center text-gray-400 py-16">
      {{ t('profile.notFound') }}
    </div>

    <!-- Контент профиля -->
    <div v-else>
      <!-- Header -->
      <div class="flex items-center gap-6 mb-8">
        <img
            :src="data.avatarUrl || defaultAvatar"
            alt="Аватар"
            class="w-24 h-24 rounded-full ring-4 ring-indigo-300 object-cover"
        />
        <div>
          <h1 v-if="data.jmeno" class="text-4xl font-extrabold text-gray-800">
            <span v-if="data.titulPred" class="mr-1">{{ data.titulPred }}</span>
            {{ data.jmeno }} {{ data.prijmeni }}
            <span v-if="data.titulZa" class="ml-1 text-lg text-gray-500">, {{ data.titulZa }}</span>
          </h1>
          <h1 v-else class="text-4xl font-extrabold text-gray-800">
            {{ data.email }}
          </h1>
          <p class="mt-1 text-sm text-gray-500">{{ regDate }}</p>
          <div class="mt-2 space-x-4 text-sm text-gray-700">
            <span>
              <strong>{{ t('profile.email') }}:</strong> {{ data.email }}
            </span>
            <span>
              <strong>{{ t('profile.active') }}:</strong>
              <span :class="data.active ? 'text-green-600' : 'text-red-600'">
                {{ data.active ? t('profile.yes') : t('profile.no') }}
              </span>
            </span>
          </div>
        </div>
      </div>

      <!-- О себе / статус / навыки -->
      <section
          v-if="data.details && (data.details.bio || data.details.status || skills.length)"
          class="mb-8 px-6 py-4 bg-white rounded-lg shadow-sm"
      >
        <p v-if="data.details.status" class="text-lg text-gray-800">
          <strong>{{ t('profile.settings.fields.status') }}:</strong>
          {{ data.details.status }}
        </p>
        <p v-if="data.details.bio" class="mt-2 text-gray-600 italic">
          “{{ data.details.bio }}”
        </p>
        <div v-if="skills.length" class="mt-4 flex flex-wrap gap-2">
          <span
              v-for="s in skills"
              :key="s"
              class="px-3 py-1 bg-indigo-100 text-indigo-700 rounded-full text-sm font-medium"
          >
            {{ s }}
          </span>
        </div>
      </section>

      <!-- Контакты -->
      <section v-if="hasContacts" class="mb-8">
        <button
            @click="contactsOpen = !contactsOpen"
            class="flex items-center gap-2 text-indigo-600 hover:text-indigo-800 font-medium"
        >
          <svg
              class="w-4 h-4 transition-transform"
              :class="{ 'rotate-180': contactsOpen }"
              fill="none" stroke="currentColor" viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M19 9l-7 7-7-7"/>
          </svg>
          <span>
            {{ contactsOpen ? t('profile.contacts.hide') : t('profile.contacts.show') }}
          </span>
        </button>
        <transition name="slide-fade">
          <div v-if="contactsOpen" class="mt-4 px-6 py-4 bg-white rounded-lg shadow-sm">
            <h2 class="text-xl font-semibold text-gray-800 mb-4">
              {{ t('profile.contacts.otherTitle') }}
            </h2>
            <dl class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-700">
              <!-- Стандартные контакты -->
              <template v-for="(val, key) in data.details.contacts" :key="key">
                <div v-if="val">
                  <dt class="font-medium">{{ contactLabels[key] }}</dt>
                  <dd>
                    <a
                        v-if="isUrlContact(key)"
                        :href="makeSocialUrl(key, val)"
                        target="_blank"
                        class="text-indigo-600 hover:underline break-all"
                    >
                      {{ val }}
                    </a>
                    <span v-else>{{ val }}</span>
                  </dd>
                </div>
              </template>
              <!-- Другие контакты -->
              <template v-for="(item, idx) in data.details.other || []" :key="idx">
                <div v-if="item.source && item.value">
                  <dt class="font-medium">{{ item.source }}</dt>
                  <dd>
                    <a
                        v-if="looksLikeUrl(item.value)"
                        :href="normalizeLink(item.value)"
                        target="_blank"
                        class="text-indigo-600 hover:underline break-all"
                    >
                      {{ item.value }}
                    </a>
                    <span v-else>{{ item.value }}</span>
                  </dd>
                </div>
              </template>
            </dl>
          </div>
        </transition>
      </section>

      <!-- Кнопки действий -->
      <div class="mb-8 flex flex-col sm:flex-row gap-4">
        <button
            @click="goToSettings"
            class="flex-1 px-6 py-3 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition"
        >
          {{ t('profile.buttons.settings') }}
        </button>
        <button
            @click="goToStag"
            class="flex-1 px-6 py-3 border-2 border-indigo-600 text-indigo-600 font-medium rounded-lg hover:bg-indigo-50 transition"
        >
          {{ t('profile.buttons.connectStag') }}
        </button>
      </div>

      <!-- STAG-инфа -->
      <section
          v-if="data.osCislo"
          class="mb-8 px-6 py-6 bg-white rounded-lg shadow-sm border-l-4 border-indigo-600"
      >
        <h2 class="text-2xl font-semibold text-indigo-600 mb-4">
          {{ t('stag.studentInfo') }}
        </h2>
        <dl class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-700">
          <div>
            <dt class="font-medium">{{ t('stag.field.osCislo') }}</dt>
            <dd>{{ data.osCislo }}</dd>
          </div>
          <div>
            <dt class="font-medium">{{ t('stag.field.faculty') }}</dt>
            <dd>{{ data.fakultaSp }}</dd>
          </div>
          <div>
            <dt class="font-medium">{{ t('stag.field.program') }}</dt>
            <dd>{{ data.nazevSp }}</dd>
          </div>
          <div>
            <dt class="font-medium">{{ t('stag.field.year') }}</dt>
            <dd>{{ data.rocnik }}</dd>
          </div>
        </dl>
      </section>

      <!-- Список друзей -->
      <section class="mb-8">
        <h2 class="text-2xl font-semibold mb-4 text-gray-800">{{ t('profile.friends.title') }}</h2>
        <div v-if="friendsLoading" class="text-gray-400">⏳ {{ t('common.loading') }}</div>
        <div v-else-if="friends.length === 0" class="text-gray-400">{{ t('profile.friends.empty') }}</div>
        <ul v-else class="bg-white rounded-lg shadow-sm divide-y divide-gray-200">
          <li
              v-for="f in friends"
              :key="f.userId"
              class="flex items-center justify-between px-6 py-4 hover:bg-indigo-50 transition cursor-pointer"
              @click="coord.navigateToUser(f.userId)"
          >
            <span class="text-indigo-600 font-medium">{{ f.name }}</span>
          </li>
        </ul>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/iam/stores/userStore.js'
import { useFriendStore } from '@/iam/stores/friendStore.js'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken.js'

const coord        = inject('coordinator')
const { t }        = useI18n()
const userStore    = useUserStore()
const friendStore  = useFriendStore()

const defaultAvatar = '/default-avatar.png'
const notFound      = ref(false)
const contactsOpen  = ref(false)

const data           = computed(() => userStore.profile)
const isLoading      = computed(() => userStore.loading)
const friends        = computed(() => friendStore.friends)
const friendsLoading= computed(() => friendStore.loadingFriends)

// регистрационная дата
const regDate = computed(() => {
  if (!data.value.createdAt) return ''
  return `${t('profile.registered')}: ${new Date(data.value.createdAt).toLocaleDateString()}`
})

// навыки
const skills = computed(() => data.value.details?.skills || [])

// есть ли хоть один контакт
const hasContacts = computed(() => {
  const c = data.value.details?.contacts || {}
  const o = data.value.details?.other    || []
  return Object.values(c).some(v => !!v) || o.length > 0
})

// маппинг ключ→название контакта
const contactLabels = {
  inst:      t('profile.contacts.inst'),
  tg:        t('profile.contacts.tg'),
  fb:        t('profile.contacts.fb'),
  ln:        t('profile.contacts.ln'),
  steam:     t('profile.contacts.steam'),
  telephone: t('profile.contacts.telephone')
}

function looksLikeUrl(str) {
  return /^https?:\/\//i.test(str) || /\.[a-z]{2,}$/i.test(str)
}
function normalizeLink(str) {
  return looksLikeUrl(str)
      ? (str.startsWith('http') ? str : `https://${str}`)
      : str
}
function isUrlContact(key) {
  return key !== 'telephone'
}
function makeSocialUrl(key, handle) {
  const map = {
    fb:    `https://facebook.com/${handle}`,
    ln:    `https://linkedin.com/in/${handle}`,
    tg:    `https://t.me/${handle}`,
    inst:  `https://instagram.com/${handle}`,
    steam: `https://steamcommunity.com/id/${handle}`
  }
  return map[key] || normalizeLink(handle)
}

function goToSettings() { coord.navigateToUserSettings() }
function goToStag()     { coord.navigateToUserStag()      }

onMounted(async () => {
  try {
    const id = getUserIdFromToken()
    await userStore.fetchProfile(id)
  } catch (err) {
    if (err.response?.status === 404) notFound.value = true
    console.error(err)
  }
  // загрузим друзей текущего пользователя
  await friendStore.fetchFriends(getUserIdFromToken())
})
</script>

<style scoped>
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
