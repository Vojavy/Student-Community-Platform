<template>
  <div class="w-full flex justify-end p-4">
    <button @click="toggleMenu" class="text-3xl p-2">☰</button>

    <transition name="fade">
      <div v-if="menuOpen" class="fixed inset-0 z-50 flex">
        <div
            class="absolute inset-0 bg-black/50 backdrop-blur-sm"
            @click.self="closeMenu"
        />
        <div class="relative ml-auto w-3/4 max-w-xs bg-secondary text-text shadow-lg p-6 overflow-y-auto">
          <button
              @click="closeMenu"
              class="absolute top-2 right-2 text-2xl p-1"
              aria-label="Close menu"
          >×</button>

          <div class="flex flex-col space-y-4 mt-8">
            <button @click="onForum"    class="text-lg w-full px-4 py-2 rounded hover:bg-gray-100">
              {{ t('navBar.mobile.forum') }}
            </button>
            <button @click="onCalendar" class="text-lg w-full px-4 py-2 rounded hover:bg-gray-100">
              {{ t('navBar.mobile.burger.calendar') }}
            </button>
            <button @click="onGroups"   class="text-lg w-full px-4 py-2 rounded hover:bg-gray-100">
              {{ t('navBar.mobile.groups') }}
            </button>
            <button @click="onMessages" class="text-lg w-full px-4 py-2 rounded hover:bg-gray-100">
              {{ t('navBar.mobile.messages') }}
            </button>

            <template v-if="isAuthenticated">
              <button @click="onProfile" class="text-lg w-full px-4 py-2 rounded hover:bg-gray-100">
                {{ t('navBar.mobile.burger.profile') }}
              </button>
              <button @click="onActions" class="text-lg w-full px-4 py-2 rounded hover:bg-gray-100">
                {{ t('navBar.mobile.burger.actions') }}
              </button>
            </template>

            <hr />

            <template v-if="!isAuthenticated">
              <div class="flex flex-col items-stretch space-y-2">
                <button @click="onLogin"    class="w-full px-4 py-2 border-2 bg-accent-primary border-accent-primary">
                  {{ t('navBar.login') }}
                </button>
                <button @click="onRegister" class="w-full px-4 py-2 border-2 border-accent-secondary">
                  {{ t('navBar.registration') }}
                </button>
              </div>
            </template>

            <template v-else>
              <button
                  @click="onLogout"
                  class="text-lg w-full px-4 py-2 rounded border border-red-500 text-red-500"
              >
                {{ t('navBar.signout') }}
              </button>
            </template>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, inject } from 'vue'
import { useI18n }               from 'vue-i18n'
import { useAuthStore }          from '@/iam/stores/authStore.js'

const { t }      = useI18n()
const coordinator = inject('coordinator')
const authStore  = useAuthStore()

const menuOpen = ref(false)
const toggleMenu = () => { menuOpen.value = !menuOpen.value }
const closeMenu  = () => { menuOpen.value = false }

const isAuthenticated = computed(() => authStore.isAuthenticated)

function onLogin()    { closeMenu(); coordinator.navigateToLogin() }
function onRegister() { closeMenu(); coordinator.navigateToRegister() }

async function onLogout() {
  closeMenu()
  await authStore.logout(coordinator)
}

// other nav buttons
function onForum()    { closeMenu(); coordinator.navigateToForum() }
function onCalendar() { closeMenu(); coordinator.navigateToCalendar() }
function onGroups()   { closeMenu(); coordinator.navigateToGroups() }
function onMessages() { closeMenu(); coordinator.navigateToMessages() }
function onProfile()  { closeMenu(); coordinator.navigateToProfile() }
function onActions()  { closeMenu(); coordinator.navigateToActions() }
</script>

<style>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s }
.fade-enter-from,  .fade-leave-to     { opacity: 0 }
</style>
