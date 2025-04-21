export const handleGroupIntent = async (intent, { model, coordinator }) => {
    switch (intent.type) {
        case 'CREATE_GROUP': {
            const { groupData } = intent.payload
            return await model.createGroup(groupData)
        }
        case 'JOIN_GROUP': {
            const { groupId } = intent.payload
            return await model.joinGroup(groupId)
        }
        case 'INVITE_USER': {
            const { groupId, targetUserId, role } = intent.payload
            return await model.inviteUser(groupId, targetUserId, role)
        }
        case 'CHANGE_MEMBER_ROLE': {
            const { groupId, targetUserId, newRole } = intent.payload
            return await model.changeMemberRole(groupId, targetUserId, newRole)
        }
        case 'PROCESS_JOIN_REQUEST': {
            const { groupId, targetUserId, approve } = intent.payload
            return await model.processJoinRequest(groupId, targetUserId, approve)
        }
        case 'REMOVE_MEMBER': {
            const { groupId, targetUserId } = intent.payload
            await model.removeMember(groupId, targetUserId)
            break
        }
        case 'FETCH_MEMBERS': {
            const { groupId, status } = intent.payload
            return await model.fetchMembers(groupId, status)
        }
        case 'FETCH_USER_GROUPS': {
            return await model.fetchUserGroups()
        }
        case 'FETCH_MEMBER_STATUS': {
            const { groupId, targetUserId } = intent.payload
            return await model.fetchMemberStatus(groupId, targetUserId)
        }
        case 'FETCH_BROWSE_GROUPS': {
            const { filters } = intent.payload
            return await model.fetchBrowseGroups(filters)
        }
        default:
            throw new Error(`Unknown group intent: ${intent.type}`)
    }
}
