import { getUserIdFromToken } from "@/utils/jwt/getUserIdFromToken.js";

export default function createCoordinator(router) {
    return {
        navigateToLogin() {
            router.push('/login')
        },
        navigateToRegister() {
            router.push('/register')
        },
        navigateToVerification(email, code = null) {
            const query = { email }
            if (code) query.code = code
            router.push({ path: '/verify', query })
        },
        navigateToHome() {
            router.push('/home')
        },
        navigateToLanding() {
            router.push('/')
        },
        navigateToUserSettings() {
            router.push('/user/settings')
        },
        navigateToUserStag() {
            router.push('/user/stag')
        },
        navigateToForum() {
            router.push('/forum')
        },
        navigateToCalendar() {
            router.push('/calendar')
        },
        navigateToMessages() {
            router.push('/messages')
        },
        navigateToUser(id) {
            const userId = id ?? getUserIdFromToken()
            if (!userId) return console.warn('User ID is missing')
            router.push(`/user/${userId}`)
        },
        navigateToProfile() {
            this.navigateToUser()
        },
        navigateToActions() {
            router.push('/actions')
        },
        navigateToSearch() {
            router.push('/search')
        },
        navigateToGroups() {
            const userId = getUserIdFromToken();
            if (!userId) {
                router.push('/groups');            // заглушка для гостей
            } else {
                router.push('/app/groups');        // реальная страница для авторизованных
            }
        },
        navigateToCreateGroup() {
            router.push('/app/groups/create')
        },
        navigateToGroup(id) {
            router.push(`/app/groups/${id}`);    // детальная страница конкретной группы
        },
        navigateToGroupMembers(groupId) {
            router.push(`/app/groups/${groupId}/members`);
        },
        navigateToGroupCalendar(groupId) {
            router.push(`/app/groups/${groupId}/calendar`);
        },
        navigateToGroupSettings(groupId) {
            router.push(`/app/groups/${groupId}/settings`);
        },
        refreshPage() {
            router.go(0);
        }
    }
}
