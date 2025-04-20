<template>
  <div class="space-y-8">
    <h2 class="text-xl font-semibold">{{ t('profile.settings.tabs.contacts') }}</h2>

    <!-- Основные контакты -->
    <div
        v-for="field in contactFields"
        :key="field.key"
        class="space-y-2"
    >
      <label class="block font-medium">{{ t(field.label) }}</label>

      <!-- view mode -->
      <div class="flex flex-wrap items-center gap-2">
        <span class="text-text/60 flex-1 min-w-[100px]">
          {{ local.contacts[field.key] || t('profile.settings.empty') }}
        </span>
        <button
            class="px-3 py-1 text-sm rounded border border-accent-primary text-accent-primary hover:bg-accent-primary/10 transition"
            @click="startEditing(field.key)"
        >
          {{ local.contacts[field.key] ? t('common.change') : t('common.add') }}
        </button>
        <button
            v-if="local.contacts[field.key]"
            class="px-3 py-1 text-sm rounded border border-red-600 text-red-600 hover:bg-red-600/10 transition"
            @click="deleteField(field.key)"
        >
          {{ t('common.delete') }}
        </button>
      </div>

      <!-- edit mode -->
      <div v-if="editing[field.key]" class="flex flex-col gap-2">
        <input
            v-model="local.contacts[field.key]"
            :ref="el => inputRefs[field.key] = el"
            class="w-full p-2 border rounded bg-primary text-text"
        />
        <div class="flex flex-wrap gap-2">
          <button
              class="px-4 py-1 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition"
              @click="saveField(field.key)"
          >
            {{ t('common.save') }}
          </button>
          <button
              class="px-4 py-1 bg-gray-300 text-gray-700 rounded hover:bg-gray-400 transition"
              @click="cancelEditing(field.key)"
          >
            {{ t('common.cancel') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Другие контакты -->
    <div class="space-y-4">
      <h3 class="text-lg font-semibold">{{ t('profile.settings.contacts.otherTitle') }}</h3>

      <!-- существующие -->
      <div
          v-for="(value, key) in local.other"
          :key="key"
          class="flex flex-wrap items-center gap-2"
      >
        <span class="font-medium min-w-[80px]">{{ key }}:</span>
        <span class="text-text/60 flex-1 min-w-[100px]">{{ value }}</span>
        <button
            class="px-2 py-1 text-sm rounded border border-accent-primary text-accent-primary hover:bg-accent-primary/10 transition"
            @click="startOther(key)"
        >
          {{ t('common.change') }}
        </button>
        <button
            class="px-2 py-1 text-sm rounded border border-red-600 text-red-600 hover:bg-red-600/10 transition"
            @click="removeOther(key)"
        >
          {{ t('common.delete') }}
        </button>
      </div>

      <!-- форма добавления / редактирования -->
      <div class="flex flex-col gap-2">
        <template v-if="editingOther !== null">
          <div class="flex flex-wrap gap-2">
            <input
                v-model="otherForm.key"
                :placeholder="t('profile.settings.contacts.keyPlaceholder')"
                class="flex-1 p-2 border rounded bg-primary text-text min-w-[80px]"
                :disabled="editingOther !== '__new__'"
                :ref="el => otherRefs.key = el"
            />
            <input
                v-model="otherForm.value"
                :placeholder="t('profile.settings.contacts.valuePlaceholder')"
                class="flex-1 p-2 border rounded bg-primary text-text min-w-[100px]"
                :ref="el => otherRefs.value = el"
            />
          </div>
          <div class="flex flex-wrap gap-2">
            <button
                class="px-4 py-1 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition"
                @click="confirmOther"
            >
              {{ t('common.save') }}
            </button>
            <button
                class="px-4 py-1 bg-gray-300 text-gray-700 rounded hover:bg-gray-400 transition"
                @click="cancelOther"
            >
              {{ t('common.cancel') }}
            </button>
          </div>
        </template>
        <button
            v-else
            class="px-4 py-1 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition"
            @click="startOther('__new__')"
        >
          {{ t('profile.settings.contacts.addOther') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watch, nextTick } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const props = defineProps({ profile: Object })
const emit  = defineEmits(['update-details'])

// основные поля
const contactFields = [
  { key: 'fb',        label: 'profile.contacts.fb' },
  { key: 'ln',        label: 'profile.contacts.ln' },
  { key: 'tg',        label: 'profile.contacts.tg' },
  { key: 'inst',      label: 'profile.contacts.inst' },
  { key: 'steam',     label: 'profile.contacts.steam' },
  { key: 'telephone', label: 'profile.contacts.telephone' }
]

// локальная копия данных
const local = reactive({
  contacts: {},
  other: {}
})

// edit‑флаги и refs
const editing   = reactive(contactFields.reduce((o,f)=>(o[f.key]=false,o), {}))
const inputRefs = reactive(contactFields.reduce((o,f)=>(o[f.key]=null,o), {}))

// «другие» контакты
const editingOther = ref(null)
const otherForm    = reactive({ key:'', value:'' })
const otherRefs    = reactive({ key:null, value:null })

// синхронизация с profile.details
watch(() => props.profile.details, d => {
  local.contacts = { ...d.contacts }
  local.other    = { ...(d.other||{}) }
  Object.keys(editing).forEach(k => editing[k]=false)
  editingOther.value = null
}, { immediate: true })

// основные методы
function startEditing(key) {
  editing[key] = true
  nextTick(() => inputRefs[key]?.focus())
}
function cancelEditing(key) {
  local.contacts[key] = props.profile.details.contacts[key] || ''
  editing[key] = false
}
function deleteField(key) {
  const updated = { ...local.contacts, [key]: '' }
  emit('update-details', { contacts: updated })
  editing[key] = false
}
function saveField(key) {
  emit('update-details', { contacts: { ...local.contacts } })
  editing[key] = false
}

// методы для «других»
function startOther(key) {
  if (key === '__new__') {
    otherForm.key = ''
    otherForm.value = ''
  } else {
    otherForm.key = key
    otherForm.value = local.other[key]
  }
  editingOther.value = key
  nextTick(() => {
    (key === '__new__' ? otherRefs.key : otherRefs.value)?.focus()
  })
}
function cancelOther() {
  editingOther.value = null
}
function removeOther(key) {
  const o = { ...local.other }
  delete o[key]
  emit('update-details', { other: o })
}
function confirmOther() {
  const k = otherForm.key.trim()
  const v = otherForm.value.trim()
  if (!k) return
  const o = { ...local.other, [k]: v }
  emit('update-details', { other: o })
  editingOther.value = null
}
</script>
