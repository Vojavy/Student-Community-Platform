<template>
  <div class="space-y-6">
    <h2 class="text-xl font-semibold">{{ t('profile.settings.tabs.security') }}</h2>

    <!-- Email -->
    <div class="space-y-2">
      <label class="block font-medium">{{ t('profile.email') }}</label>
      <div v-if="!editing.email" class="flex items-center gap-4">
        <span class="flex-1 text-text/60">{{ profile.email }}</span>
        <button
            class="px-3 py-1 border rounded border-accent-primary text-accent-primary hover:bg-accent-primary/10 transition"
            @click="start('email')"
        >
          {{ t('common.change') }}
        </button>
      </div>
      <div v-else class="flex flex-col gap-2">
        <input
            v-model="form.email"
            type="email"
            class="w-full p-2 border rounded bg-primary text-text"
        />
        <input
            v-if="form.email !== profile.email"
            v-model="form.oldPassword"
            type="password"
            placeholder="••••••••"
            class="w-full p-2 border rounded bg-primary text-text"
        />
        <div class="flex gap-2">
          <button
              class="px-4 py-1 bg-accent-primary text-white rounded hover:bg-accent-primary/90"
              @click="saveEmail"
              :disabled="form.email === profile.email || !form.oldPassword"
          >
            {{ t('common.save') }}
          </button>
          <button
              class="px-4 py-1 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
              @click="cancel('email')"
          >
            {{ t('common.cancel') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Password -->
    <div class="space-y-2">
      <label class="block font-medium">{{ t('profile.settings.security.newPassword') }}</label>
      <div v-if="!editing.password" class="flex items-center gap-4">
        <span class="flex-1 text-text/60">••••••••</span>
        <button
            class="px-3 py-1 border rounded border-accent-primary text-accent-primary hover:bg-accent-primary/10 transition"
            @click="start('password')"
        >
          {{ t('common.change') }}
        </button>
      </div>
      <div v-else class="flex flex-col gap-2">
        <input
            v-model="form.newPassword"
            type="password"
            placeholder="••••••••"
            class="w-full p-2 border rounded bg-primary text-text"
        />
        <input
            v-model="form.confirmPassword"
            type="password"
            placeholder="••••••••"
            class="w-full p-2 border rounded bg-primary text-text"
        />
        <input
            v-if="form.newPassword && form.newPassword === form.confirmPassword"
            v-model="form.oldPassword"
            type="password"
            placeholder="••••••••"
            class="w-full p-2 border rounded bg-primary text-text"
        />
        <p v-if="form.newPassword && form.confirmPassword && form.newPassword !== form.confirmPassword"
           class="text-red-600 text-sm">
          {{ t('errors.passwordMismatch') }}
        </p>
        <div class="flex gap-2">
          <button
              class="px-4 py-1 bg-accent-primary text-white rounded hover:bg-accent-primary/90"
              @click="savePassword"
              :disabled="!canSavePassword"
          >
            {{ t('common.save') }}
          </button>
          <button
              class="px-4 py-1 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
              @click="cancel('password')"
          >
            {{ t('common.cancel') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Deactivate -->
    <div class="space-y-2">
      <button
          v-if="!confirmingDeactivate"
          @click="confirmingDeactivate = true"
          class="px-4 py-2 border border-red-600 text-red-600 rounded hover:bg-red-600/10 transition"
      >
        {{ t('profile.settings.security.deactivate') }}
      </button>
      <div
          v-else
          class="flex flex-col gap-2 bg-red-50 p-4 rounded border border-red-200"
      >
        <p class="text-red-600">{{ t('profile.settings.security.confirmDeactivate') }}</p>
        <input
            v-model="form.oldPassword"
            type="password"
            placeholder="••••••••"
            class="w-full p-2 border rounded bg-primary text-text"
        />
        <div class="flex gap-2">
          <button
              class="px-4 py-1 bg-red-600 text-white rounded hover:bg-red-700"
              @click="deactivate"
              :disabled="!form.oldPassword"
          >
            {{ t('profile.settings.security.deactivate') }}
          </button>
          <button
              class="px-4 py-1 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
              @click="cancel('deactivate')"
          >
            {{ t('common.cancel') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const props = defineProps({ profile: Object })
const emit  = defineEmits(['update-user'])

const editing = reactive({
  email: false,
  password: false
})

const confirmingDeactivate = ref(false)

const form = reactive({
  email: '',
  newPassword: '',
  confirmPassword: '',
  oldPassword: ''
})

watch(() => props.profile.email, email => {
  form.email = email || ''
}, { immediate: true })

function start(key) {
  editing[key] = true
  form.oldPassword = ''
}

function cancel(key) {
  if (key === 'email') form.email = props.profile.email
  if (key === 'password') {
    form.newPassword = ''
    form.confirmPassword = ''
  }
  form.oldPassword = ''
  editing[key] = false
  if (key === 'deactivate') confirmingDeactivate.value = false
}

async function saveEmail() {
  await emit('update-user', {
    email: form.email,
    oldPassword: form.oldPassword
  })
  cancel('email')
}

const canSavePassword = computed(() =>
    form.newPassword &&
    form.newPassword === form.confirmPassword &&
    form.oldPassword
)

async function savePassword() {
  await emit('update-user', {
    oldPassword: form.oldPassword,
    newPassword: form.newPassword
  })
  cancel('password')
}

async function deactivate() {
  await emit('update-user', {
    deactivate: true,
    oldPassword: form.oldPassword
  })
  cancel('deactivate')
}
</script>
