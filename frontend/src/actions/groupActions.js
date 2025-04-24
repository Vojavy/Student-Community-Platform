export const handleGroupIntent = async (intent, { model, coordinator }) => {
    switch (intent.type) {
        case 'CREATE_GROUP':
            return await model.createGroup(intent.payload.groupData)
        case 'JOIN_GROUP':
            return await model.joinGroup(intent.payload.groupId)
        case 'INVITE_USER':
            return await model.inviteUser(
                intent.payload.groupId,
                intent.payload.targetUserId,
                intent.payload.role
            )
        case 'CHANGE_MEMBER_ROLE':
            return await model.changeMemberRole(
                intent.payload.groupId,
                intent.payload.targetUserId,
                intent.payload.newRole
            )
        case 'PROCESS_JOIN_REQUEST':
            return await model.processJoinRequest(
                intent.payload.groupId,
                intent.payload.targetUserId,
                intent.payload.approve
            )
        case 'REMOVE_MEMBER':
            await model.removeMember(
                intent.payload.groupId,
                intent.payload.targetUserId
            )
            return
        case 'FETCH_MEMBERS':
            return await model.fetchMembers(
                intent.payload.groupId,
                intent.payload.status
            )
        case 'FETCH_USER_GROUPS':
            return await model.fetchUserGroups(
                intent.payload.page,
                intent.payload.size
            )
        case 'FETCH_MEMBER_STATUS':
            return await model.fetchMemberStatus(
                intent.payload.groupId,
                intent.payload.targetUserId
            )
        case 'FETCH_BROWSE_GROUPS':
            return await model.fetchBrowseGroups(intent.payload.filters)
        case 'FETCH_GROUP': {
            const { groupId } = intent.payload
            return await model.fetchGroup(groupId)
        }
        default:
            throw new Error(`Unknown group intent: ${intent.type}`)
    }
}
