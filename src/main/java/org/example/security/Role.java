package org.example.security;

import java.util.Set;

public enum Role {
    USER(Set.of(
            Permission.USER_READ_SELF,
            Permission.EVENT_READ_SELF,
            Permission.FILE_UPLOAD_SELF
    )),
    MODERATOR(Set.of(
            Permission.USER_READ_SELF,
            Permission.USER_READ_ALL,

            Permission.EVENT_READ_SELF,
            Permission.EVENT_READ_ALL,
            Permission.EVENT_WRITE_ALL,
            Permission.EVENT_DELETE_ALL,

            Permission.FILE_UPLOAD_SELF,
            Permission.FILE_READ_ALL,
            Permission.FILE_DELETE_ALL
    )),
    ADMIN(Set.of(Permission.values()));


    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

}
