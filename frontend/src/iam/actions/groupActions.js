// src/iam/actions/groupActions.js

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
            return await model.fetchMembers(
                intent.payload.groupId,
                intent.payload.status
            )

        case 'FETCH_MEMBER_STATUS':
            return await model.fetchMemberStatus(
                intent.payload.groupId,
                intent.payload.targetUserId
            )

        case 'PENDING_JOIN_REQUEST':
            console.log(intent.payload)
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

        case 'KICK_MEMBER':
            await model.removeMember(
                intent.payload.groupId,
                intent.payload.targetUserId
            )
            return

        case 'BAN_MEMBER':
            return await model.banMember(
                intent.payload.groupId,
                intent.payload.targetUserId
            )

        case 'UNBAN_MEMBER':
            return await model.unbanMember(
                intent.payload.groupId,
                intent.payload.targetUserId
            )

        // === groupPosts: post CRUD ===
        case 'FETCH_GROUP_POSTS':
            return await model.fetchPosts(
                intent.payload.groupId,
                intent.payload.page,
                intent.payload.size,
                intent.payload.search
            )

        case 'FETCH_GROUP_POST':
            return await model.fetchPost(
                intent.payload.groupId,
                intent.payload.postId
            )

        case 'CREATE_GROUP_POST':
            console.log(intent.payload)
            return await model.createPost(
                intent.payload.groupId,
                intent.payload.postData
            )

        case 'UPDATE_GROUP_POST':
            return await model.updatePost(
                intent.payload.groupId,
                intent.payload.postId,
                intent.payload.postData
            )

        case 'DELETE_GROUP_POST':
            await model.deletePost(intent.payload.groupId, intent.payload.postId)
            return
        default:
            throw new Error(`Unknown group intent: ${intent.type}`)
    }
}
