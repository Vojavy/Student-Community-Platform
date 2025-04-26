import apiClient from '@/utils/api/apiClient'

export default function createGroupModel() {
    return {
        // === groups: listing & browsing ===
        async fetchUserGroups(page = 0, size = 25) {
            const resp = await apiClient.get('/groups/user', { params: { page, size } })
            return resp.data
        },
        async fetchBrowseGroups(filters) {
            const params = { ...filters }
            if (Array.isArray(params.topics)) {
                params.topics = JSON.stringify(params.topics)
            }
            const resp = await apiClient.get('/groups', { params })
            return resp.data
        },

        // === group: single‐group operations ===
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
        async fetchGroup(groupId) {
            const resp = await apiClient.get(`/groups/${groupId}`)
            return resp.data
        },
        async updateGroupSettings(groupId, settingsData) {
            const payload = {
                ...settingsData,
                topics:
                    typeof settingsData.topics === 'string'
                        ? settingsData.topics
                        : JSON.stringify(settingsData.topics)
            }
            const resp = await apiClient.put(`/groups/${groupId}/settings`, payload)
            return resp.data
        },
        async deleteGroup(groupId) {
            await apiClient.delete(`/groups/${groupId}`)
        },

        // === groupMembers: member management ===
        async joinGroup(groupId) {
            const resp = await apiClient.post(`/groups/${groupId}/join`)
            return resp.data
        },
        async leaveGroup(groupId) {
            await apiClient.post(`/groups/${groupId}/leave`)
        },
        async inviteUser(groupId, targetUserId, role) {
            const resp = await apiClient.post(
                `/groups/${groupId}/invite`,
                null,
                { params: { targetUserId, role } }
            )
            return resp.data
        },
        async fetchMembers(groupId, status = null) {
            const params = status ? { status } : {}
            const resp = await apiClient.get(`/groups/${groupId}/members`, { params })
            return resp.data
        },
        async fetchMemberStatus(groupId, targetUserId) {
            const path = targetUserId === null
                ? `/groups/${groupId}/members/status`
                : `/groups/${groupId}/members/${targetUserId}/status`
            const resp = await apiClient.get(path)
            return resp.data
        },
        async processJoinRequest(groupId, targetUserId, approve) {
            const resp = await apiClient.put(
                `/groups/${groupId}/members/${targetUserId}/status`,
                null,
                { params: { approve } }
            )
            return resp.data
        },
        async changeMemberRole(groupId, targetUserId, newRole) {
            const resp = await apiClient.put(
                `/groups/${groupId}/members/${targetUserId}/role`,
                null,
                { params: { newRole } }
            )
            return resp.data
        },
        async removeMember(groupId, targetUserId) {
            await apiClient.delete(`/groups/${groupId}/members/${targetUserId}`)
        },
        async banMember(groupId, targetUserId) {
            await apiClient.put(
                `/groups/${groupId}/members/${targetUserId}/ban`
            )
        }
    }
}
