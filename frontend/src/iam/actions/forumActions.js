export const handleForumIntent = async (intent, { model, coordinator }) => {
    switch (intent.type) {
        case 'FETCH_FORUMS':
            return await model.fetchForums(intent.payload)
        case 'FETCH_FORUM':
            return await model.fetchForum(intent.payload.forumId)

        case 'CREATE_FORUM':
            return await model.createForum(intent.payload.forumData)

        case 'UPDATE_FORUM':
            return await model.updateForum(
                intent.payload.forumId,
                intent.payload.forumData
            )

        case 'DELETE_FORUM':
            await model.deleteForum(intent.payload.forumId)
            return

        // forum posts
        case 'FETCH_FORUM_POSTS':
            return await model.fetchForumPosts(
                intent.payload.forumId,
                intent.payload.page,
                intent.payload.size,
                intent.payload.parentPostId
            )

        case 'CREATE_FORUM_POST':
            return await model.createForumPost(
                intent.payload.forumId,
                intent.payload.postData
            )

        case 'UPDATE_FORUM_POST':
            return await model.updateForumPost(
                intent.payload.forumId,
                intent.payload.postId,
                intent.payload.postData
            )

        case 'DELETE_FORUM_POST':
            await model.deleteForumPost(
                intent.payload.forumId,
                intent.payload.postId
            )
            return

        default:
            throw new Error(`Unknown forum intent: ${intent.type}`)
    }
}
