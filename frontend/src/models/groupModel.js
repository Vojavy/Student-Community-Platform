import apiClient from '@/utils/api/apiClient'

export default function createGroupModel() {
    return {
        // 1. Создать группу
        async createGroup(groupData) {
            const resp = await apiClient.post('/groups', {
                name: groupData.name,
                description: groupData.description,
                topics: JSON.stringify(groupData.topics),
                public: groupData.public,
                minRoleForPosts: groupData.minRoleForPosts,
                minRoleForEvents: groupData.minRoleForEvents,
                domain: groupData.domain
            })
            return resp.data
        },

        // 2. Заявка на вступление
        async joinGroup(groupId) {
            const resp = await apiClient.post(`/groups/${groupId}/join`)
            return resp.data
        },

        // 3. Приглашение
        async inviteUser(groupId, targetUserId, role) {
            const resp = await apiClient.post(
                `/groups/${groupId}/invite`,
                null,
                { params: { targetUserId, role } }
            )
            return resp.data
        },

        // 4. Смена роли
        async changeMemberRole(groupId, targetUserId, newRole) {
            const resp = await apiClient.put(
                `/groups/${groupId}/members/${targetUserId}/role`,
                null,
                { params: { newRole } }
            )
            return resp.data
        },

        // 5. Обработка заявки
        async processJoinRequest(groupId, targetUserId, approve) {
            const resp = await apiClient.put(
                `/groups/${groupId}/members/${targetUserId}/status`,
                null,
                { params: { approve } }
            )
            return resp.data
        },

        // 6. Удалить участника
        async removeMember(groupId, targetUserId) {
            await apiClient.delete(`/groups/${groupId}/members/${targetUserId}`)
        },

        // 7. Участники
        async fetchMembers(groupId, status = null) {
            const params = status ? { status } : {}
            const resp = await apiClient.get(`/groups/${groupId}/members`, { params })
            return resp.data
        },

        // 8. Мои группы (пейджинг)
        async fetchUserGroups(page = 0, size = 25) {
            const resp = await apiClient.get('/groups/user', {
                params: { page, size }
            })
            return resp.data
        },

        // 9. Статус участника
        async fetchMemberStatus(groupId, targetUserId) {
            const resp = await apiClient.get(
                `/groups/${groupId}/members/${targetUserId}/status`
            )
            return resp.data
        },

        // 10. Browse groups (пейджинг + фильтры)
        async fetchBrowseGroups(filters) {
            const params = { ...filters }
            if (Array.isArray(params.topics)) {
                params.topics = JSON.stringify(params.topics)
            }
            const resp = await apiClient.get('/groups', { params })
            return resp.data
        },

        async fetchGroup(groupId) {
            const resp = await apiClient.get(`/groups/${groupId}`)
            return resp.data
        }
    }
}
