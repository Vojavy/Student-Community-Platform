import apiClient from '@/utils/api/apiClient'

export default function createGroupModel() {
    return {
        // 1. Создать группу
        async createGroup(groupData) {
            const response = await apiClient.post(
                '/groups',
                {
                    name: groupData.name,
                    description: groupData.description,
                    topics: JSON.stringify(groupData.topics),
                    public: groupData.public,
                    minRoleForPosts: groupData.minRoleForPosts,
                    minRoleForEvents: groupData.minRoleForEvents,
                    domain: groupData.domain
                }
            )
            return response.data
        },

        // 2. Отправить заявку на вступление
        async joinGroup(groupId) {
            const response = await apiClient.post(`/groups/${groupId}/join`)
            return response.data
        },

        // 3. Пригласить пользователя
        async inviteUser(groupId, targetUserId, role) {
            const response = await apiClient.post(
                `/groups/${groupId}/invite`,
                null,
                { params: { targetUserId, role } }
            )
            return response.data
        },

        // 4. Изменить роль участника
        async changeMemberRole(groupId, targetUserId, newRole) {
            const response = await apiClient.put(
                `/groups/${groupId}/members/${targetUserId}/role`,
                null,
                { params: { newRole } }
            )
            return response.data
        },

        // 5. Обработать заявку (approve=true или false)
        async processJoinRequest(groupId, targetUserId, approve) {
            const response = await apiClient.put(
                `/groups/${groupId}/members/${targetUserId}/status`,
                null,
                { params: { approve } }
            )
            return response.data
        },

        // 6. Удалить участника
        async removeMember(groupId, targetUserId) {
            await apiClient.delete(`/groups/${groupId}/members/${targetUserId}`)
        },

        // 7. Получить участников (опц. фильтрация по статусу)
        async fetchMembers(groupId, status = null) {
            const params = status ? { status } : {}
            const response = await apiClient.get(`/groups/${groupId}/members`, { params })
            return response.data
        },

        // 8. Получить все группы текущего пользователя
        async fetchUserGroups() {
            const response = await apiClient.get('/groups/user')
            return response.data
        },

        // 9. Получить статус участника
        async fetchMemberStatus(groupId, targetUserId) {
            const response = await apiClient.get(`/groups/${groupId}/members/${targetUserId}/status`)
            return response.data
        },

        // 10. Фильстрация груп
        async fetchBrowseGroups(filters) {
            const params = { ...filters }
            if (Array.isArray(params.topics)) {
                params.topics = JSON.stringify(params.topics)
            }
            const resp = await apiClient.get('/groups', { params })
            return resp.data
        }
    }
}
