// src/iam/stores/authStore.js
import { defineStore } from 'pinia'
import createAuthModel from '@/iam/models/authModel'

const model = createAuthModel()

export const useAuthStore = defineStore('authStore', {
    state: () => ({
        token: localStorage.getItem('token') || null,
        loading: false,
        error: null,
        verification: {
            loading: false,
            success: false,
            error: false,
            resending: false,
            resendSuccess: false
        }
    }),
    actions: {
        async login({ email, password }, coordinator) {
            this.loading = true
            this.error = null
            try {
                const res = await model.login({ email, password })
                this.token = res.token
                coordinator.navigateToHome()
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Login failed'
            } finally {
                this.loading = false
            }
        },
        async register({ email, password }, coordinator) {
            this.loading = true
            this.error = null
            try {
                await model.register({ email, password })
                coordinator.navigateToVerification(email)
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Registration failed'
            } finally {
                this.loading = false
            }
        },
        async verify({ email, verificationCode }, coordinator, auto = false) {
            this.verification.loading = true
            this.verification.error = false
            try {
                await model.verify(email, verificationCode)
                this.verification.success = true
                if (auto) {
                    setTimeout(() => coordinator.navigateToLogin(), 3000)
                } else {
                    coordinator.navigateToHome()
                }
            } catch {
                this.verification.error = true
            } finally {
                this.verification.loading = false
            }
        },
        async resend(email) {
            this.verification.resending = true
            this.verification.resendSuccess = false
            try {
                await model.resend(email)
                this.verification.resendSuccess = true
            } catch {
                console.error('Resend failed')
            } finally {
                this.verification.resending = false
            }
        }
    }
})
