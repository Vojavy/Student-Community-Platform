import apiClient from '@/utils/api/apiClient.js'

export default function createGroupPostModel () {
    return {
        async fetchPosts (groupId, page = 0, size = 20, search = '') {
            const resp = await apiClient.get(`/groups/${groupId}/posts`, {
                params: { page, size, search }
            })
            return resp.data
        },

        async fetchPost (groupId, postId) {
            const resp = await apiClient.get(`/groups/${groupId}/posts/${postId}`)
            return resp.data
        },

        async createPost (groupId, { title, content, topics = [] }) {
            const resp = await apiClient.post(`/groups/${groupId}/posts`, {
                title,
                content,
                topics: JSON.stringify(topics)
            })
            return resp.data
        },

        async updatePost (groupId, postId, { title, content, topics = [] }) {
            const resp = await apiClient.put(`/groups/${groupId}/posts/${postId}`, {
                title,
                content,
                topics: JSON.stringify(topics)
            })
            return resp.data
        },

        async deletePost (groupId, postId) {
            await apiClient.delete(`/groups/${groupId}/posts/${postId}`)
        }
    }
}
