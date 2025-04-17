import { createRouter, createWebHistory } from 'vue-router'

// Layouts
import HomeLayout from '@/views/layouts/HomeLayout.vue'
import PublicLayout from '@/views/layouts/PublicLayout.vue'

// Auth views
import LoginView from '@/views/auth/LoginView.vue'
import RegistrationView from '@/views/auth/RegistrationView.vue'
import VerificationView from '@/views/auth/VerificationView.vue'

import { checkTokenIntent } from '@/intents/authIntents'
import { handleAuthIntent } from '@/actions/authActions'
import createAuthModel from '@/models/authModel'
import apiClient from '@/utils/api/apiClient'
import createCoordinator from '@/coordinator/coordinator'

import HomeView from '@/views/protected/HomeView.vue'
import LandingView from "@/views/public/LandingView.vue";
import AccessDeniedView from "@/views/auth/AccessDeniedView.vue";

const routes = [
    {
        path: '/',
        component: PublicLayout,
        children: [
            { path: '', name: 'landing', component: LandingView },
            { path: 'login', name: 'login', component: LoginView, meta: { requiresUnauth: true } },
            { path: 'register', name: 'register', component: RegistrationView, meta: { requiresUnauth: true } },
            {
                path: 'verify',
                name: 'verify',
                component: VerificationView,
                meta: { requiresUnauth: true },
                beforeEnter: (to, from, next) => {
                    if (!to.query.email) return next({ name: 'login' })
                    next()
                }
            },
            { path: 'access-denied', name: 'access-denied', component: AccessDeniedView }
        ]
    },
    {
        path: '/',
        component: HomeLayout,
        children: [
            {
                path: 'home',
                name: 'home',
                component: HomeView,
                meta: { requiresAuth: true }
            }
        ]
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/'
    }
]



const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach(async (to, from, next) => {
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
    const requiresUnauth = to.matched.some(record => record.meta.requiresUnauth)
    const token = localStorage.getItem('token')

    const model = createAuthModel(apiClient)
    const coordinator = createCoordinator(router)

    if (requiresAuth) {
        if (!token) return next({ name: 'login' })

        try {
            await handleAuthIntent(checkTokenIntent(), { model, coordinator })
            return next()
        } catch (e) {
            if (e.response?.status === 401) {
                localStorage.removeItem('token')
                window.dispatchEvent(new CustomEvent('jwt-expired'))
                return next(false) // остановим переход
            } else if (e.response?.status === 403) {
                return next({ name: 'access-denied' })
            }

            return next({ name: 'login' })
        }
    }

    if (requiresUnauth && token) {
        try {
            await handleAuthIntent(checkTokenIntent(), { model, coordinator })
            return next({ name: 'home' })
        } catch (e) {
            localStorage.removeItem('token')
            return next()
        }
    }

    return next()
})

export default router
