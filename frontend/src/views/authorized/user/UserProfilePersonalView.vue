<template>
  <div class="p-8 max-w-4xl mx-auto bg-gray-50 rounded-xl shadow-lg">
    <!-- Loading / Not Found -->
    <div v-if="isLoading" class="text-center text-gray-400 py-16">
      ⏳ {{ t('common.loading') }}
    </div>
    <div v-else-if="notFound" class="text-center text-gray-400 py-16">
      {{ t('profile.notFound') }}
    </div>

    <!-- Profile -->
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
            <span><strong>{{ t('profile.email') }}:</strong> {{ userData.email }}</span>
            <span>
              <strong>{{ t('profile.active') }}:</strong>
              <span :class="userData.active ? 'text-green-600' : 'text-red-600'">
                {{ userData.active ? t('profile.yes') : t('profile.no') }}
              </span>
            </span>
          </div>
        </div>
      </div>

      <!-- Bio / Status / Skills -->
      <div v-if="userData.details" class="mb-8 px-6 py-4 bg-white rounded-lg shadow-sm">
        <p v-if="userData.details.status" class="text-lg text-gray-800">
          <strong>{{ t('profile.settings.fields.status') }}:</strong>
          {{ userData.details.status }}
        </p>
        <p v-if="userData.details.bio" class="mt-2 text-gray-600 italic">
          “{{ userData.details.bio }}”
        </p>
        <div v-if="userData.details.skills?.length" class="mt-4 flex flex-wrap gap-2">
          <span
              v-for="skill in userData.details.skills"
              :key="skill"
              class="px-3 py-1 bg-indigo-100 text-indigo-700 rounded-full text-sm font-medium"
          >
            {{ skill }}
          </span>
        </div>
      </div>

      <!-- Action Buttons -->
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

      <!-- Details Tabs -->
      <div v-if="hasContacts || hasPersonal" class="bg-white rounded-lg shadow-sm mb-8">
        <div class="border-b border-gray-200">
          <nav class="flex -mb-px">
            <button
                v-if="hasContacts"
                @click="activeTab = 'contacts'"
                :class="tabClass('contacts')"
            >
              {{ t('profile.tabs.contacts') }}
            </button>
            <button
                v-if="hasPersonal"
                @click="activeTab = 'personal'"
                :class="tabClass('personal')"
            >
              {{ t('profile.tabs.personal') }}
            </button>
          </nav>
        </div>

        <!-- Contacts Tab -->
        <div v-if="activeTab === 'contacts'" class="p-6 space-y-6">
          <h3 class="text-xl font-semibold text-gray-800">{{ t('profile.tabs.contacts') }}</h3>
          <dl class="grid grid-cols-1 sm:grid-cols-2 gap-6 text-gray-700">
            <!-- Основные соцсети -->
            <template v-for="(val, key) in userData.details.contacts" :key="key">
              <div v-if="val">
                <dt class="font-medium">{{ contactLabels[key] }}</dt>
                <dd>
                  <button
                      class="text-indigo-600 hover:underline"
                      @click="goToSocial(key, val)"
                  >
                    {{ val }}
                  </button>
                </dd>
              </div>
            </template>
            <!-- Другие контакты -->
            <template v-for="({ source, value }) in userData.details.other" :key="source">
              <div>
                <dt class="font-medium">{{ source }}</dt>
                <dd>
                  <button
                      class="text-indigo-600 hover:underline"
                      @click="openExternal(value)"
                  >
                    {{ value }}
                  </button>
                </dd>
              </div>
            </template>
          </dl>
        </div>

        <!-- Personal Tab -->
        <div v-if="activeTab === 'personal'" class="p-6 space-y-6">
          <h3 class="text-xl font-semibold text-gray-800">{{ t('profile.tabs.personal') }}</h3>
          <dl class="space-y-4 text-gray-700">
            <div v-if="userData.details.birthDate">
              <dt class="font-medium">{{ t('profile.personal.birthDate') }}</dt>
              <dd>{{ userData.details.birthDate }}</dd>
            </div>
            <div v-if="userData.details.languages?.length">
              <dt class="font-medium">{{ t('profile.personal.languages') }}</dt>
              <dd>{{ userData.details.languages.join(', ') }}</dd>
            </div>
            <div v-if="userData.details.location">
              <dt class="font-medium">{{ t('profile.personal.location') }}</dt>
              <dd>{{ userData.details.location }}</dd>
            </div>
            <div v-if="userData.details.website">
              <dt class="font-medium">{{ t('profile.personal.website') }}</dt>
              <dd>
                <button
                    class="text-indigo-600 hover:underline"
                    @click="openExternal(userData.details.website)"
                >
                  {{ userData.details.website }}
                </button>
              </dd>
            </div>
          </dl>
        </div>
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
              <button
                  class="px-3 py-1 text-sm rounded-full border border-red-500 text-red-500 hover:bg-red-50 transition"
                  @click.stop="removeFriend(f.userId)"
              >
                {{ t('profile.friends.remove') }}
              </button>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { fetchUserProfileIntent } from '@/iam/intents/userIntents'
import { fetchMyFriendsIntent, removeFriendIntent } from '@/iam/intents/friendIntents'
import createUserModel from '@/iam/models/userModel'
import createFriendModel from '@/iam/models/friendModel'
import { handleUserIntent } from '@/iam/actions/userActions'
import { handleFriendIntent } from '@/iam/actions/friendActions'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'

const { t } = useI18n()
const coordinator = inject('coordinator')
const userModel   = createUserModel()
const friendModel = createFriendModel()

const isLoading      = ref(true)
const notFound       = ref(false)
const userData       = ref({})
const friends        = ref([])
const friendsLoading = ref(true)
const defaultAvatar  = '/default-avatar.png'
const activeTab      = ref('')

const formattedDate = computed(() => {
  if (!userData.value.createdAt) return ''
  return `${t('profile.registered')}: ${new Date(userData.value.createdAt).toLocaleDateString()}`
})

// наличе контактов / личных данных
const hasContacts = computed(() => {
  const c = userData.value.details?.contacts || {}
  const o = userData.value.details?.other || []
  return Object.values(c).some(v => v) || o.length > 0
})
const hasPersonal = computed(() => {
  const d = userData.value.details || {}
  return Boolean(
      d.birthDate ||
      (d.languages?.length || 0) > 0 ||
      d.location ||
      d.website
  )
})
// при инициализации выбираем первую доступную вкладку
onMounted(() => {
  if (hasContacts.value) activeTab.value = 'contacts'
  else if (hasPersonal.value) activeTab.value = 'personal'
})

// подписи соцсетей
const contactLabels = {
  fb:        t('profile.contacts.fb'),
  ln:        t('profile.contacts.ln'),
  tg:        t('profile.contacts.tg'),
  inst:      t('profile.contacts.inst'),
  steam:     t('profile.contacts.steam'),
  telephone: t('profile.contacts.telephone')
}

// класс для табов
function tabClass(tab) {
  return [
    'px-6 py-3 text-sm font-medium -mb-px',
    activeTab.value === tab
        ? 'border-b-2 border-indigo-600 text-indigo-600'
        : 'border-b-2 border-transparent text-gray-500 hover:text-gray-700'
  ]
}

// открывает внешнюю ссылку
function normalizeLink(str) {
  return str.startsWith('http') ? str : `https://${str}`
}
function openExternal(url) {
  if (!url) return
  window.open(normalizeLink(url), '_blank')
}

// переходит на соцсеть по шаблону URL
function goToSocial(key, handle) {
  const urls = {
    fb:   `https://facebook.com/${handle}`,
    ln:   `https://linkedin.com/in/${handle}`,
    tg:   `https://t.me/${handle}`,
    inst: `https://instagram.com/${handle}`,
    steam:`https://steamcommunity.com/id/${handle}`
  }
  openExternal(urls[key] || handle)
}

// загрузка данных
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

<style scoped>
/* плавное появление/исчезновение */
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
