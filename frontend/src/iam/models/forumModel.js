import apiClient from '@/utils/api/apiClient.js'

export default function createForumModel() {
    return {
        async fetchForums({
                              page, size, isPublic, domain, status, name, topics, sort
                          }) {
            const params = { page, size, sort: sort === 'oldest' ? 'oldest' : 'newest' }
            if (isPublic != null) params.isPublic = isPublic
            if (domain)    params.domain   = domain
            if (status)    params.status   = status
            if (name)      params.name     = name
            if (topics.length) params.topics = topics
            const resp = await apiClient.get('/forums', { params })
            return resp.data
        },
        async fetchForum(forumId) {
            const resp = await apiClient.get(`/forums/${forumId}`)
            return resp.data
        },

        async createForum(forumData) {
            const resp = await apiClient.post('/forums', forumData)
            return resp.data
        },

        async updateForum(forumId, forumData) {
            const resp = await apiClient.put(`/forums/${forumId}`, forumData)
            return resp.data
        },

        async deleteForum(forumId) {
            await apiClient.delete(`/forums/${forumId}`)
        },

        async fetchForumPosts(forumId, page = 0, size = 20, parentPostId = null) {
            const params = { page, size }
            if (parentPostId != null) params.parentPostId = parentPostId
            const resp = await apiClient.get(
                `/forums/${forumId}/posts`,
                { params }
            )
            return resp.data
        },

        async createForumPost(forumId, postData) {
            const resp = await apiClient.post(
                `/forums/${forumId}/posts`,
                postData
            )
            return resp.data
        },

        async updateForumPost(forumId, postId, postData) {
            const resp = await apiClient.put(
                `/forums/${forumId}/posts/${postId}`,
                postData
            )
            return resp.data
        },

        async deleteForumPost(forumId, postId) {
            await apiClient.delete(`/forums/${forumId}/posts/${postId}`)
        }
    }
}
