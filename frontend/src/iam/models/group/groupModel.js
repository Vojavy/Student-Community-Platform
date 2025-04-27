// src/iam/models/groupModel.js

import apiClient from '@/utils/api/apiClient.js'

export default function createGroupModel() {
    return {
        // === groups: listing & browsing ===
        async fetchUserGroups(page = 0, size = 25) {
            const response = await apiClient.get('/groups/user', {
                params: { page, size }
            })
            return response.data
        },

        async fetchBrowseGroups(filters) {
            const params = { ...filters }
            if (Array.isArray(params.topics)) {
                params.topics = JSON.stringify(params.topics)
            }
            const response = await apiClient.get('/groups', { params })
            return response.data
        },

        // === group: single‐group operations ===
        async createGroup(groupData) {
            const response = await apiClient.post('/groups', {
                name: groupData.name,
                description: groupData.description,
                topics: JSON.stringify(groupData.topics),
                public: groupData.public,
                minRoleForPosts: groupData.minRoleForPosts,
                minRoleForEvents: groupData.minRoleForEvents,
                domain: groupData.domain
            })
            return response.data
        },

        async fetchGroup(groupId) {
            const response = await apiClient.get(`/groups/${groupId}`)
            return response.data
        },

        async updateGroupSettings(groupId, settingsData) {
            const payload = {
                ...settingsData,
                topics:
                    typeof settingsData.topics === 'string'
                        ? settingsData.topics
                        : JSON.stringify(settingsData.topics)
            }
            const response = await apiClient.put(
                `/groups/${groupId}/settings`,
                payload
            )
            return response.data
        },

        async deleteGroup(groupId) {
            await apiClient.delete(`/groups/${groupId}`)
        },

        // === groupMembers: member management ===
        async joinGroup(groupId) {
            const response = await apiClient.post(`/groups/${groupId}/join`)
            return response.data
        },

        async leaveGroup(groupId) {
            await apiClient.delete(`/groups/${groupId}/leave`)
        },

        async inviteUser(groupId, targetUserId, role) {
            const response = await apiClient.post(
                `/groups/${groupId}/invite`,
                null,
                { params: { targetUserId, role } }
            )
            return response.data
        },

        async fetchMembers(groupId, status = null) {
            const params = status ? { status } : {}
            const response = await apiClient.get(
                `/groups/${groupId}/members`,
                { params }
            )
            return response.data
        },

        async fetchMemberStatus(groupId, targetUserId) {
            const path =
                targetUserId == null
                    ? `/groups/${groupId}/members/status`
                    : `/groups/${groupId}/members/${targetUserId}/status`
            const response = await apiClient.get(path)
            return response.data
        },

        async processJoinRequest(groupId, targetUserId, approve) {
            const response = await apiClient.put(
                `/groups/${groupId}/members/${targetUserId}/status`,
                null,
                { params: { approve } }
            )
            return response.data
        },

        async changeMemberRole(groupId, targetUserId, newRole) {
            const response = await apiClient.put(
                `/groups/${groupId}/members/${targetUserId}/role`,
                null,
                { params: { newRole } }
            )
            return response.data
        },

        async removeMember(groupId, targetUserId) {
            await apiClient.delete(
                `/groups/${groupId}/members/${targetUserId}`
            )
        },

        async banMember(groupId, targetUserId) {
            await apiClient.put(
                `/groups/${groupId}/members/${targetUserId}/ban`
            )
        },

        async unbanMember(groupId, targetUserId) {
            await apiClient.put(
                `/groups/${groupId}/members/${targetUserId}/unban`
            )
        }
    }
}
