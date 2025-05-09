import { createRouter, createWebHistory } from 'vue-router'

import PublicLayout         from '@/views/layouts/PublicLayout.vue'
import HomeLayout           from '@/views/layouts/HomeLayout.vue'

import LandingView          from '@/views/public/LandingView.vue'
import LoginView            from '@/views/auth/LoginView.vue'
import RegistrationView     from '@/views/auth/RegistrationView.vue'
import VerificationView     from '@/views/auth/VerificationView.vue'
import AccessDeniedView     from '@/views/auth/AccessDeniedView.vue'
import GroupsBlockedView    from '@/views/public/GroupsBlockedView.vue'

import HomeView             from '@/views/authorized/HomeView.vue'
import UserLayout           from '@/views/layouts/UserLayout.vue'
import UserProfileWrapper   from '@/views/authorized/User/UserProfileWrapper.vue'
import UserSettingsView     from '@/views/authorized/User/UserSettingsView.vue'
import UserStagView         from '@/views/authorized/User/UserStagView.vue'
import UserChatView from "@/views/authorized/user/UserChatView.vue";
import UserSearchView       from '@/views/authorized/user/UserSearchView.vue'

import GroupsView           from '@/views/authorized/group/GroupsView.vue'
import CreateGroupView      from '@/views/authorized/group/CreateGroupView.vue'
import GroupWrapper         from '@/views/authorized/group/GroupWrapper.vue'
import GroupMembersView     from '@/views/authorized/group/GroupMembersView.vue'
import GroupCalendarView    from '@/views/authorized/group/GroupCalendarView.vue'
import GroupSettingsView    from '@/views/authorized/group/GroupSettingsView.vue'
import GroupView            from '@/views/authorized/group/GroupView.vue'
import GroupPostsView       from '@/views/authorized/group/GroupPostsView.vue'
import GroupNewPostView     from '@/views/authorized/group/GroupNewPostView.vue'

import PublicForumsWrapper  from '@/views/forum/public/PublicForumsWrapper.vue'
import ForumSearchView      from '@/views/forum/ForumSearchView.vue'
import ForumInfoView        from '@/views/forum/ForumInfoView.vue'

import ForumsWrapper        from '@/views/forum/authorized/ForumsWrapper.vue'
import ForumFollowingView   from '@/views/forum/authorized/ForumFollowingView.vue'
import ForumArchivedView    from '@/views/forum/authorized/ForumArchivedView.vue'
import ForumBannedView      from '@/views/forum/authorized/ForumBannedView.vue'
import ForumCreateView      from '@/views/forum/authorized/ForumCreateView.vue'

import ForumDetailPublicView from '@/views/forum/public/ForumDetailPublicView.vue'
import ForumDetailView      from '@/views/forum/authorized/ForumDetailView.vue'

import { useAuthStore }     from '@/iam/stores/authStore.js'
import createCoordinator    from '@/coordinator/coordinator'


const routes = [
    // --- Public part ---
    {
        path: '/',
        component: PublicLayout,
        children: [
            { path: '',        name: 'landing',           component: LandingView },
            { path: 'login',   name: 'login',             component: LoginView,        meta: { requiresUnauth: true } },
            { path: 'register',name: 'register',          component: RegistrationView, meta: { requiresUnauth: true } },
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
            { path: 'groups',        name: 'groups-blocked', component: GroupsBlockedView, meta: { requiresUnauth: true } },
            { path: 'access-denied', name: 'access-denied',  component: AccessDeniedView, meta: { requiresUnauth: true } },

            {
                path: 'forum',
                component: PublicForumsWrapper,
                children: [
                    { path: '',     name: 'forum-search-public', component: ForumSearchView, meta: { requiresUnauth: true } },
                    { path: 'info', name: 'forum-info-public',   component: ForumInfoView,   meta: { requiresUnauth: true } }
                ]
            },
            { path: 'forum/:id', name: 'forum-public', component: ForumDetailPublicView, meta: { requiresUnauth: true } },

            { path: 'users/search', name: 'user-search-public', component: UserSearchView }
        ]
    },

    // --- Authorized part ---
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
                    { path: 'stag',     name: 'user-stag',     component: UserStagView,       meta: { requiresAuth: true } },
                    { path: 'search',   name: 'user-search',   component: UserSearchView,     meta: { requiresAuth: true } }
                ]
            },
            { path: 'chat/:id', name: 'chat', component: UserChatView, meta: { requiresAuth: true } },
            { path: 'app/groups',        name: 'groups',        component: GroupsView,      meta: { requiresAuth: true } },
            { path: 'app/groups/create', name: 'create-group',  component: CreateGroupView, meta: { requiresAuth: true } },
            {
                path: 'app/groups/:groupId',
                component: GroupWrapper,
                meta: { requiresAuth: true },
                children: [
                    { path: '',          name: 'group-overview',   component: GroupView },
                    { path: 'members',   name: 'group-members',    component: GroupMembersView },
                    { path: 'calendar',  name: 'group-calendar',   component: GroupCalendarView },
                    { path: 'settings',  name: 'group-settings',   component: GroupSettingsView },
                    { path: 'posts',     name: 'group-posts',      component: GroupPostsView },
                    { path: 'post-edit', name: 'group-new-post',   component: GroupNewPostView }
                ]
            },

            {
                path: 'app/forum',
                component: ForumsWrapper,
                meta: { requiresAuth: true },
                children: [
                    { path: '',          name: 'forum-search',    component: ForumSearchView },
                    { path: 'following', name: 'forum-following', component: ForumFollowingView },
                    { path: 'info',      name: 'forum-info',      component: ForumInfoView },
                    { path: 'archived',  name: 'forum-archived',  component: ForumArchivedView },
                    { path: 'banned',    name: 'forum-banned',    component: ForumBannedView }
                ]
            },
            { path: 'app/forum/create', name: 'forum-create', component: ForumCreateView, meta: { requiresAuth: true } },
            { path: 'app/forum/:id',     name: 'forum',        component: ForumDetailView, meta: { requiresAuth: true } },

            { path: '/:pathMatch(.*)*', redirect: '/' }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach(async (to, from, next) => {
    const authStore     = useAuthStore()
    const coordinator   = createCoordinator(router)
    const token         = authStore.token
    const requiresAuth   = to.matched.some(r => r.meta.requiresAuth)
    const requiresUnauth = to.matched.some(r => r.meta.requiresUnauth)

    // redirect guests away from private
    if (requiresAuth && !token) {
        return next({ name: 'groups-blocked' })
    }

    // validate token on private
    if (requiresAuth && token) {
        try {
            await authStore.checkToken(coordinator)
            return next()
        } catch (e) {
            if (e.response?.status === 401) {
                authStore.setToken(null)
                window.dispatchEvent(new CustomEvent('jwt-expired'))
            }
            if (e.response?.status === 403) {
                return next({ name: 'access-denied' })
            }
            return next({ name: 'login' })
        }
    }

    // redirect logged-in away from login/register
    if (requiresUnauth && token) {
        try {
            await authStore.checkToken(coordinator)
            return next({ name: 'home' })
        } catch {
            authStore.setToken(null)
        }
    }

    return next()
})

export default router