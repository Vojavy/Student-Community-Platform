<template>
  <div class="space-y-8">
    <h2 class="text-xl font-semibold">{{ t('profile.settings.tabs.contacts') }}</h2>
    <div v-for="field in contactFields" :key="field.key" class="space-y-2">

      <label class="block font-medium">{{ t(field.label) }}</label>
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

    <div class="space-y-4">
      <h3 class="text-lg font-semibold">{{ t('profile.settings.contacts.otherTitle') }}</h3>

      <div v-for="item in localOtherArray" :key="item.source" class="flex flex-wrap items-center gap-2">
        <span class="font-medium min-w-[80px]">{{ item.source }}:</span>
        <span class="text-text/60 flex-1 min-w-[100px]">{{ item.value }}</span>
        <button
            class="px-2 py-1 text-sm rounded border border-accent-primary text-accent-primary hover:bg-accent-primary/10 transition"
            @click="startOther(item.source)"
        >
          {{ t('common.change') }}
        </button>
        <button
            class="px-2 py-1 text-sm rounded border border-red-600 text-red-600 hover:bg-red-600/10 transition"
            @click="removeOther(item.source)"
        >
          {{ t('common.delete') }}
        </button>
      </div>

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
import { reactive, ref, watch, nextTick, computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const props = defineProps({ profile: Object })
const emit  = defineEmits(['update-details'])

const contactFields = [
  { key: 'fb',        label: 'profile.contacts.fb' },
  { key: 'ln',        label: 'profile.contacts.ln' },
  { key: 'tg',        label: 'profile.contacts.tg' },
  { key: 'inst',      label: 'profile.contacts.inst' },
  { key: 'steam',     label: 'profile.contacts.steam' },
  { key: 'telephone', label: 'profile.contacts.telephone' }
]

const local = reactive({
  contacts: {},
  otherMap: {}
})

const localOtherArray = computed(() =>
    Object.entries(local.otherMap).map(([source, value]) => ({ source, value }))
)

const editing   = reactive(contactFields.reduce((o,f)=>(o[f.key]=false,o), {}))
const inputRefs = reactive(contactFields.reduce((o,f)=>(o[f.key]=null,o), {}))

const editingOther = ref(null)
const otherForm    = reactive({ key:'', value:'' })
const otherRefs    = reactive({ key:null, value:null })

watch(
    () => props.profile.details,
    details => {
      if (!details) return
      local.contacts = { ...(details.contacts || {}) }
      local.otherMap = {}
      ;(details.other || []).forEach(({ source, value }) => {
        local.otherMap[source] = value
      })
      Object.keys(editing).forEach(k => editing[k] = false)
      editingOther.value = null
    },
    { immediate: true }
)

function startEditing(key) {
  editing[key] = true
  nextTick(() => inputRefs[key]?.focus())
}
function cancelEditing(key) {
  local.contacts[key] = props.profile.details?.contacts?.[key] || ''
  editing[key] = false
}
function deleteField(key) {
  const updated = { ...local.contacts, [key]: '' }
  emit('update-details', {
    ...props.profile.details,
    contacts: { ...local.contacts },
    other:    localOtherArray.value
  })
  editing[key] = false
}
function saveField(key) {
  emit('update-details', {
    ...props.profile.details,
    contacts: { ...local.contacts },
    other:    localOtherArray.value
  })
  editing[key] = false
}

function startOther(key) {
  if (key === '__new__') {
    otherForm.key = ''
    otherForm.value = ''
  } else {
    otherForm.key = key
    otherForm.value = local.otherMap[key]
  }
  editingOther.value = key
  nextTick(() => {
    (key === '__new__' ? otherRefs.key : otherRefs.value)?.focus()
  })
}
function cancelOther() {
  editingOther.value = null
}
function removeOther(source) {
  const o = { ...local.otherMap }
  delete o[source]
  emit('update-details', {
    ...props.profile.details,
    contacts: { ...local.contacts },
    other:    localOtherArray.value
  })
}
function confirmOther() {
  const source = otherForm.key.trim()
  const val    = otherForm.value.trim()
  if (!source) return
  const o = { ...local.otherMap, [source]: val }
  emit('update-details', {
    ...props.profile.details,
    contacts: { ...local.contacts },
    other:    localOtherArray.value
  })
  editingOther.value = null
}
</script>
