// src/iam/stores/userStore.js
import { defineStore } from 'pinia'
import createUserModel from '@/iam/models/userModel.js'

const model = createUserModel()

export const useUserStore = defineStore('userStore', {
    state: () => ({
        user: {},
        profile: {},
        roles: [],
        loading: false,
        error: null
    }),

    actions: {
        async fetchById(id) {
            this.loading = true
            this.error = null
            try {
                const data = await model.fetchById({ id })
                console.log('[userStore] fetchById:', data)
                this.user = data
                return data
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Fetch user failed'
                console.error('[userStore] fetchById error:', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        async fetchProfile(id) {
            this.loading = true
            this.error = null
            try {
                const data = await model.fetchProfile({ id })
                console.log('[userStore] fetchProfile:', data)
                this.profile = data
                return data
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Fetch profile failed'
                console.error('[userStore] fetchProfile error:', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        async updateUser(userId, updateData) {
            this.error = null
            try {
                console.log('[userStore] updateUser payload:', updateData)
                const updated = await model.updateUser(userId, updateData)
                console.log('[userStore] updateUser result:', updated)
                this.user = updated
                if (this.profile && typeof this.profile === 'object') {
                    this.profile = { ...this.profile, ...updated }
                }
                return updated
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Update user failed'
                console.error('[userStore] updateUser error:', this.error)
                throw e
            }
        },

        async updateUserDetails(userId, details) {
            this.error = null
            try {
                console.log('[userStore] updateUserDetails payload:', details)
                await model.updateUserDetails(userId, details)
                console.log('[userStore] updateUserDetails succeeded, applying to profile.details')
                if (this.profile && typeof this.profile === 'object') {
                    this.profile = { ...this.profile, details }
                }
                return details
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Update details failed'
                console.error('[userStore] updateUserDetails error:', this.error)
                throw e
            }
        },

        async fetchRoles() {
            this.loading = true
            this.error = null
            try {
                const roles = await model.fetchUserRoles()
                console.log('[userStore] fetchRoles:', roles)
                this.roles = Array.isArray(roles) ? roles : []
                return this.roles
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Fetch roles failed'
                console.error('[userStore] fetchRoles error:', this.error)
                throw e
            } finally {
                this.loading = false
            }
        }
    }
})
