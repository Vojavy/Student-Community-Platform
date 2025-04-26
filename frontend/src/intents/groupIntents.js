// src/intents/groupIntents.js

// === groups: listing and browsing user's groups ===
export const fetchUserGroupsIntent = ({ page = 0, size = 25 } = {}) => ({
    type: 'FETCH_USER_GROUPS',
    payload: { page, size }
})

export const fetchBrowseGroupsIntent = (filters) => ({
    type: 'FETCH_BROWSE_GROUPS',
    payload: { filters }
})

// === group: single‐group operations ===
export const createGroupIntent = (groupData) => ({
    type: 'CREATE_GROUP',
    payload: { groupData }
})

export const fetchGroupIntent = (groupId) => ({
    type: 'FETCH_GROUP',
    payload: { groupId }
})

export const updateGroupSettingsIntent = (groupId, settingsData) => ({
    type: 'UPDATE_GROUP_SETTINGS',
    payload: { groupId, settingsData }
})

export const deleteGroupIntent = (groupId) => ({
    type: 'DELETE_GROUP',
    payload: { groupId }
})

export const joinGroupIntent = (groupId) => ({
    type: 'JOIN_GROUP',
    payload: { groupId }
})

export const leaveGroupIntent = (groupId) => ({
    type: 'LEAVE_GROUP',
    payload: { groupId }
})

export const inviteUserIntent = (groupId, targetUserId, role) => ({
    type: 'INVITE_USER',
    payload: { groupId, targetUserId, role }
})

// === groupmembers: member management within a group ===
export const fetchMembersIntent = (groupId, status = null) => ({
    type: 'FETCH_MEMBERS',
    payload: { groupId, status }
})

export const fetchMemberStatusIntent = (groupId, targetUserId) => ({
    type: 'FETCH_MEMBER_STATUS',
    payload: { groupId, targetUserId }
})

export const processJoinRequestIntent = (groupId, userId, approve) => ({
    type: 'PROCESS_JOIN_REQUEST',
    payload: { groupId, userId, approve }
})

export const removeMemberIntent = (groupId, targetUserId) => ({
    type: 'REMOVE_MEMBER',
    payload: { groupId, targetUserId }
})

export const banMemberIntent = (groupId, targetUserId) => ({
    type: 'BAN_MEMBER',
    payload: { groupId, targetUserId }
})

export const unbanMemberIntent = (groupId, userId) => ({
    type: 'UNBAN_MEMBER',
    payload: { groupId, userId }
})

export const changeMemberRoleIntent = (groupId, userId, newRole) => ({
    type: 'CHANGE_MEMBER_ROLE',
    payload: { groupId, userId, newRole }
})