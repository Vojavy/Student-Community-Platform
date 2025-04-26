// src/actions/groupActions.js

export const handleGroupIntent = async (intent, { model, coordinator }) => {
    switch (intent.type) {
        // === groups: listing and browsing user's groups ===
        case 'FETCH_USER_GROUPS':
            return await model.fetchUserGroups(intent.payload.page, intent.payload.size)
        case 'FETCH_BROWSE_GROUPS':
            return await model.fetchBrowseGroups(intent.payload.filters)

        // === group: single‐group operations ===
        case 'CREATE_GROUP':
            return await model.createGroup(intent.payload.groupData)
        case 'FETCH_GROUP':
            return await model.fetchGroup(intent.payload.groupId)
        case 'UPDATE_GROUP_SETTINGS':
            return await model.updateGroupSettings(
                intent.payload.groupId,
                intent.payload.settingsData
            )
        case 'DELETE_GROUP':
            await model.deleteGroup(intent.payload.groupId)
            return
        case 'JOIN_GROUP':
            return await model.joinGroup(intent.payload.groupId)
        case 'LEAVE_GROUP':
            return await model.leaveGroup(intent.payload.groupId)
        case 'INVITE_USER':
            return await model.inviteUser(
                intent.payload.groupId,
                intent.payload.targetUserId,
                intent.payload.role
            )

        // === groupmembers: member management within a group ===
        case 'FETCH_MEMBERS':
            return await model.fetchMembers(intent.payload.groupId, intent.payload.status)
        case 'FETCH_MEMBER_STATUS':
            return await model.fetchMemberStatus(
                intent.payload.groupId,
                intent.payload.targetUserId
            )
        case 'PROCESS_JOIN_REQUEST':
            return await model.processJoinRequest(
                intent.payload.groupId,
                intent.payload.targetUserId,
                intent.payload.approve
            )
        case 'CHANGE_MEMBER_ROLE':
            return await model.changeMemberRole(
                intent.payload.groupId,
                intent.payload.targetUserId,
                intent.payload.newRole
            )
        case 'REMOVE_MEMBER':
            await model.removeMember(intent.payload.groupId, intent.payload.targetUserId)
            return
        case 'BAN_MEMBER':
            return await model.banMember(
                intent.payload.groupId,
                intent.payload.targetUserId
            )

        default:
            throw new Error(`Unknown group intent: ${intent.type}`)
    }
}
