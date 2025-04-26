import { createRouter, createWebHistory } from 'vue-router'

import PublicLayout    from '@/views/layouts/PublicLayout.vue'
import HomeLayout      from '@/views/layouts/HomeLayout.vue'

import LandingView     from '@/views/public/LandingView.vue'
import LoginView       from '@/views/auth/LoginView.vue'
import RegistrationView from '@/views/auth/RegistrationView.vue'
import VerificationView from '@/views/auth/VerificationView.vue'
import AccessDeniedView from '@/views/auth/AccessDeniedView.vue'
import GroupsBlockedView from '@/views/public/GroupsBlockedView.vue'

import HomeView             from '@/views/authorized/HomeView.vue'
import UserLayout           from '@/views/layouts/UserLayout.vue'
import UserProfileWrapper   from '@/views/authorized/User/UserProfileWrapper.vue'
import UserSettingsView     from '@/views/authorized/User/UserSettingsView.vue'
import UserStagView         from '@/views/authorized/User/UserStagView.vue'

import GroupsView           from '@/views/authorized/group/GroupsView.vue'
import CreateGroupView      from '@/views/authorized/group/CreateGroupView.vue'
import GroupWrapper         from '@/views/authorized/group/GroupWrapper.vue'
import GroupMembersView     from '@/views/authorized/group/GroupMembersView.vue'
import GroupCalendarView    from '@/views/authorized/group/GroupCalendarView.vue'
import GroupSettingsView    from '@/views/authorized/group/GroupSettingsView.vue'

import { checkTokenIntent } from '@/intents/authIntents'
import { handleAuthIntent } from '@/actions/authActions'
import createAuthModel      from '@/models/authModel'
import createCoordinator    from '@/coordinator/coordinator'
import GroupView from "@/views/authorized/group/GroupView.vue";

const routes = [
    {
        path: '/',
        component: PublicLayout,
        children: [
            { path: '',        name: 'landing',        component: LandingView },
            { path: 'login',   name: 'login',          component: LoginView,        meta: { requiresUnauth: true } },
            { path: 'register',name: 'register',       component: RegistrationView, meta: { requiresUnauth: true } },
            {
                path: 'verify',
                name: 'verify',
                component: VerificationView,
                meta: { requiresUnauth: true },
                beforeEnter(to, from, next) {
                    if (!to.query.email) return next({ name: 'login' })
                    next()
                }
            },
            { path: 'groups',         name: 'groups-blocked', component: GroupsBlockedView },
            { path: 'access-denied',  name: 'access-denied',  component: AccessDeniedView }
        ]
    },
    {
        path: '/',
        component: HomeLayout,
        children: [
            { path: 'home', name: 'home', component: HomeView, meta: { requiresAuth: true } },
            {
                path: 'user',
                component: UserLayout,
                children: [
                    { path: ':id',      name: 'user-profile',  component: UserProfileWrapper, meta: { requiresAuth: true } },
                    { path: 'settings', name: 'user-settings', component: UserSettingsView,   meta: { requiresAuth: true } },
                    { path: 'stag',     name: 'user-stag',     component: UserStagView,       meta: { requiresAuth: true } }
                ]
            },
            { path: 'app/groups',          name: 'groups',        component: GroupsView,      meta: { requiresAuth: true } },
            { path: 'app/groups/create',   name: 'create-group',  component: CreateGroupView, meta: { requiresAuth: true } },

            // Новый вложенный маршрут для конкретной группы
            {
                path: 'app/groups/:groupId',
                component: GroupWrapper,
                meta: { requiresAuth: true },
                children: [
                    {
                        path: '',
                        name: 'group-overview',
                        component: GroupView
                    },
                    {
                        path: 'members',
                        name: 'group-members',
                        component: GroupMembersView
                    },
                    {
                        path: 'calendar',
                        name: 'group-calendar',
                        component: GroupCalendarView
                    },
                    {
                        path: 'settings',
                        name: 'group-settings',
                        component: GroupSettingsView
                    }
                ]
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
    const requiresAuth   = to.matched.some(r => r.meta.requiresAuth)
    const requiresUnauth = to.matched.some(r => r.meta.requiresUnauth)
    const token          = localStorage.getItem('token')

    const model       = createAuthModel()
    const coordinator = createCoordinator(router)

    if (requiresAuth && !token) {
        if (to.name === 'groups' || to.name?.startsWith('group-')) {
            return next({ name: 'groups-blocked' })
        }
        return next({ name: 'login' })
    }

    if (requiresAuth && token) {
        try {
            await handleAuthIntent(checkTokenIntent(), { model, coordinator })
            return next()
        } catch (e) {
            if (e.response?.status === 401) {
                localStorage.removeItem('token')
                window.dispatchEvent(new CustomEvent('jwt-expired'))
                return next(false)
            }
            if (e.response?.status === 403) {
                return next({ name: 'access-denied' })
            }
            return next({ name: 'login' })
        }
    }

    if (requiresUnauth && token) {
        try {
            await handleAuthIntent(checkTokenIntent(), { model, coordinator })
            return next({ name: 'home' })
        } catch {
            localStorage.removeItem('token')
        }
    }

    return next()
})

export default router
