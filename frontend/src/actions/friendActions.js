export const handleFriendIntent = async (intent, { model }) => {
    switch (intent.type) {
        case 'FETCH_FRIENDS': {
            const { userId } = intent.payload
            return await model.fetchFriends(userId)
        }

        case 'FETCH_MY_FRIENDS': {
            return await model.fetchMyFriends()
        }

        case 'FETCH_INCOMING_FRIEND_REQUESTS': {
            return await model.fetchIncomingRequests()
        }

        case 'SEND_FRIEND_REQUEST': {
            const { targetId } = intent.payload
            return await model.sendFriendRequest(targetId)
        }

        case 'APPROVE_FRIEND_REQUEST': {
            const { fromId } = intent.payload
            return await model.approveFriendRequest(fromId)
        }

        case 'DECLINE_FRIEND_REQUEST': {
            const { fromId } = intent.payload
            return await model.declineFriendRequest(fromId)
        }

        case 'REMOVE_FRIEND': {
            const { friendId } = intent.payload
            return await model.removeFriend(friendId)
        }

        default:
            throw new Error(`Unknown FriendIntent type: ${intent.type}`)
    }
}
