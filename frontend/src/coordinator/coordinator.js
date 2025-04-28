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
        navigateToGroupPosts(groupId) {
            router.push(`/app/groups/${groupId}/posts`);
        },
        navigateToGroupNewPost(groupId) {
            router.push(`/app/groups/${groupId}/post-edit`)
        },
        navigateToGroupEditPost(groupId, postId) {
            router.push(`/app/groups/${groupId}/post-edit/${postId}`)
        },
        navigateToForum(id = null) {
            const userId = getUserIdFromToken();
            if (userId) {
                id !== null ? router.push(`/app/forum/${id}`) : router.push('/app/forum');
            } else {
                id !== null ? router.push(`/forum/${id}`) : router.push('/forum');
            }
        },
        navigateToForumPublic() {
            router.push('/forum');
        },
        navigateToForumAuth() {
            router.push('/app/forum');
        },
        navigateToForumSearch() {
            this.navigateToForum();
        },
        navigateToForumFollowing() {
            router.push('/app/forum/following');
        },
        navigateToForumInfo() {
            router.push('/app/forum/info');
        },
        navigateToForumArchived() {
            router.push('/app/forum/archived');
        },
        navigateToForumBanned() {
            router.push('/app/forum/banned');
        },
        navigateToForumCreate() {
            router.push('/app/forum/create');
        },
        navigateToForumDetails(id) {
            router.push({ name: 'forum-info', params: { forumId: id } })
        },
        refreshPage() {
            router.go(0);
        }
    }
}
