export const createGroupIntent = (groupData) => ({
    type: 'CREATE_GROUP',
    payload: { groupData }
})
export const joinGroupIntent = (groupId) => ({
    type: 'JOIN_GROUP',
    payload: { groupId }
})
export const inviteUserIntent = (groupId, targetUserId, role) => ({
    type: 'INVITE_USER',
    payload: { groupId, targetUserId, role }
})
export const changeMemberRoleIntent = (groupId, targetUserId, newRole) => ({
    type: 'CHANGE_MEMBER_ROLE',
    payload: { groupId, targetUserId, newRole }
})
export const processJoinRequestIntent = (groupId, targetUserId, approve) => ({
    type: 'PROCESS_JOIN_REQUEST',
    payload: { groupId, targetUserId, approve }
})
export const removeMemberIntent = (groupId, targetUserId) => ({
    type: 'REMOVE_MEMBER',
    payload: { groupId, targetUserId }
})
export const fetchMembersIntent = (groupId, status = null) => ({
    type: 'FETCH_MEMBERS',
    payload: { groupId, status }
})
export const fetchUserGroupsIntent = ({ page = 0, size = 25 } = {}) => ({
    type: 'FETCH_USER_GROUPS',
    payload: { page, size }
})
export const fetchMemberStatusIntent = (groupId, targetUserId) => ({
    type: 'FETCH_MEMBER_STATUS',
    payload: { groupId, targetUserId }
})
export const fetchBrowseGroupsIntent = (filters) => ({
    type: 'FETCH_BROWSE_GROUPS',
    payload: { filters }
})

export const fetchGroupIntent = (groupId) => ({
    type: 'FETCH_GROUP',
    payload: { groupId }
})