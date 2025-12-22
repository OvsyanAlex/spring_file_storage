package org.example.security;

public enum Permission {
    USER_READ_SELF,
    USER_READ_ALL,
    USER_UPLOAD_ALL,

    EVENT_READ_SELF,
    EVENT_READ_ALL,
    EVENT_WRITE_ALL,
    EVENT_DELETE_ALL,

    FILE_UPLOAD_SELF,
    FILE_READ_ALL,
    FILE_DELETE_ALL
}
