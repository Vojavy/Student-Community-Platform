import apiClient from '@/utils/api/apiClient.js'

export default function createGroupPostModel () {
    return {
        async fetchPosts (groupId, page = 0, size = 20, search = '') {
            const response = await apiClient.get(`/groups/${groupId}/posts`, {
                params: { page, size, search }
            })
            return response.data
        },

        async fetchPost (groupId, postId) {
            const response = await apiClient.get(`/groups/${groupId}/posts/${postId}`)
            return response.data
        },

        async createPost (groupId, { title, content, topics = [] }) {
            const response = await apiClient.post(`/groups/${groupId}/posts`, {
                title,
                content,
                topics: JSON.stringify(topics)
            })
            return response.data
        },

        async updatePost (groupId, postId, { title, content, topics = [] }) {
            const response = await apiClient.put(`/groups/${groupId}/posts/${postId}`, {
                title,
                content,
                topics: JSON.stringify(topics)
            })
            return response.data
        },

        async deletePost (groupId, postId) {
            await apiClient.delete(`/groups/${groupId}/posts/${postId}`)
        }
    }
}
