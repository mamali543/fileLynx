package backend.server.service.security.entities;

/**
 * This is an enum that defines the possible roles for users in the system
 */
public enum EROLE {
    ROLE_USER, // The user has basic access to the system
    ROLE_COMPAGNIE, // The user has elevated privileges to moderate content

    ROLE_SUPERADMIN
 }