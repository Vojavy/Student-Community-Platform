export const fetchFriendsIntent = (userId) => ({
    type: 'FETCH_FRIENDS',
    payload: { userId }
})

export const fetchMyFriendsIntent = () => ({
    type: 'FETCH_MY_FRIENDS'
})

export const fetchIncomingRequestsIntent = () => ({
    type: 'FETCH_INCOMING_FRIEND_REQUESTS'
})

export const sendFriendRequestIntent = (targetId) => ({
    type: 'SEND_FRIEND_REQUEST',
    payload: { targetId }
})

export const approveFriendRequestIntent = (fromId) => ({
    type: 'APPROVE_FRIEND_REQUEST',
    payload: { fromId }
})

export const declineFriendRequestIntent = (fromId) => ({
    type: 'DECLINE_FRIEND_REQUEST',
    payload: { fromId }
})

export const removeFriendIntent = (friendId) => ({
    type: 'REMOVE_FRIEND',
    payload: { friendId }
})
