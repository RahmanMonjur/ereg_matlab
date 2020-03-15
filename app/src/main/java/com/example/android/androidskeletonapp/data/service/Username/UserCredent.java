package com.example.android.androidskeletonapp.data.service.Username;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.android.core.user.UserRole;

import java.util.List;

public class UserCredent {
    private UserInfo userInfo;
    @JsonProperty(value = "userInfo")
    public UserInfo getUserInfo() {
        return userInfo;
    }

    @JsonProperty(value = "userInfo")
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    private String username;
    @JsonProperty(value = "username")
    public String getUsername() {
        return username;
    }

    @JsonProperty(value = "username")
    public void setUsername(String username) {
        this.username = username;
    }

    private String displayName;
    @JsonProperty(value = "displayName")
    public String getDisplayName() { return displayName;}

    @JsonProperty(value = "displayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    private List<UserRole> userRoles;
    @JsonProperty(value = "userRoles")
    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    @JsonProperty(value = "userRoles")
    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

}
