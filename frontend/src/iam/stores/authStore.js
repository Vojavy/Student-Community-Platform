// src/iam/stores/authStore.js
import { defineStore } from 'pinia'
import createAuthModel from '@/iam/models/authModel'

const model = createAuthModel()

export const useAuthStore = defineStore('authStore', {
    state: () => ({
        // JWT токен
        token: localStorage.getItem('token') || null,
        // флаг загрузки / ошибка
        loading: false,
        error:   null,
        // состояние верификации (если нужно)
        verification: {
            loading:      false,
            success:      false,
            error:        false,
            resending:    false,
            resendSuccess:false
        }
    }),

    getters: {
        isAuthenticated: (state) => !!state.token
    },

    actions: {
        // === LOGIN ===
        async login({ email, password }, coordinator) {
            this.loading = true
            this.error   = null
            try {
                const res = await model.login({ email, password })
                this.token = res.token
                localStorage.setItem('token', res.token)
                coordinator.navigateToHome()
            } catch (e) {
                console.error('Login failed', e)
                this.error = e.response?.data?.message || e.message
                throw e
            } finally {
                this.loading = false
            }
        },

        // === REGISTER ===
        async register({ email, password }, coordinator) {
            this.loading = true
            this.error   = null
            try {
                await model.register({ email, password })
                coordinator.navigateToVerification(email)
            } catch (e) {
                console.error('Register failed', e)
                this.error = e.response?.data?.message || e.message
                throw e
            } finally {
                this.loading = false
            }
        },

        // === VERIFY ===
        async verify(email, verificationCode, coordinator) {
            this.verification.loading = true
            this.verification.error   = false
            try {
                await model.verify(email, verificationCode)
                this.verification.success = true
                coordinator.navigateToHome()
            } catch (e) {
                console.error('Verification failed', e)
                this.verification.error = true
                throw e
            } finally {
                this.verification.loading = false
            }
        },

        // === RESEND_CODE ===
        async resendCode(email) {
            this.verification.resending     = true
            this.verification.resendSuccess = false
            try {
                await model.resend(email)
                this.verification.resendSuccess = true
            } catch (e) {
                console.error('Resend failed', e)
                throw e
            } finally {
                this.verification.resending = false
            }
        },

        // === CHECK_TOKEN ===
        async checkToken() {
            if (!this.token) {
                throw new Error('No token to check')
            }
            this.loading = true
            this.error   = null
            try {
                await model.checkToken()
            } catch (e) {
                console.error('Token check failed', e)
                localStorage.removeItem('token')
                window.dispatchEvent(new CustomEvent('jwt-expired'))
                this.token = null
                throw e
            } finally {
                this.loading = false
            }
        },

        // === LOGOUT ===
        async logout() {
            this.loading = true
            this.error   = null
            try {
                await model.logout()
                window.dispatchEvent(new CustomEvent('logout-success'))
            } catch (e) {
                console.error('Logout failed', e)
            } finally {
                localStorage.removeItem('token')
                this.token   = null
                this.loading = false
            }
        }
    }
})
