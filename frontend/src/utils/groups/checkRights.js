export default function checkRights(userRole, requiredRole) {
    const hierarchy = ['member', 'helper', 'admin', 'owner'];
    return hierarchy.indexOf(userRole) >= hierarchy.indexOf(requiredRole);
}