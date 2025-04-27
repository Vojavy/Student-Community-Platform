// === forums ===
export const fetchForumsIntent = ({
                                      page = 0,
                                      size = 25,
                                      isPublic = null,
                                      domain = null,
                                      status = null,
                                      name = '',
                                      topics = [],
                                      sort = 'newest'
                                  } = {}) => ({
    type: 'FETCH_FORUMS',
    payload: { page, size, isPublic, domain, status, name, topics, sort }
})
export const fetchForumIntent = (forumId) => ({
    type: 'FETCH_FORUM',
    payload: { forumId }
})

export const createForumIntent = (forumData) => ({
    type: 'CREATE_FORUM',
    payload: { forumData }
})

export const updateForumIntent = (forumId, forumData) => ({
    type: 'UPDATE_FORUM',
    payload: { forumId, forumData }
})

export const deleteForumIntent = (forumId) => ({
    type: 'DELETE_FORUM',
    payload: { forumId }
})

// === forum posts ===
export const fetchForumPostsIntent = (
    forumId,
    { page = 0, size = 20, parentPostId = null } = {}
) => ({
    type: 'FETCH_FORUM_POSTS',
    payload: { forumId, page, size, parentPostId }
})

export const createForumPostIntent = (forumId, postData) => ({
    type: 'CREATE_FORUM_POST',
    payload: { forumId, postData }
})

export const updateForumPostIntent = (forumId, postId, postData) => ({
    type: 'UPDATE_FORUM_POST',
    payload: { forumId, postId, postData }
})

export const deleteForumPostIntent = (forumId, postId) => ({
    type: 'DELETE_FORUM_POST',
    payload: { forumId, postId }
})
