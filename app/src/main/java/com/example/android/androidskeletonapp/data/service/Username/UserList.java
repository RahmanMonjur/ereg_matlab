package com.example.android.androidskeletonapp.data.service.Username;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.user.UserCredentials;

import java.util.List;

public class UserList {
    private String uid;
    private UserCredentials userCredentials;
    private List<OrganisationUnit> organisationUnits;
    private List<UserGroup> userGroups;


    @JsonProperty(value = "id")
    public String getUid() {
        return uid;
    }

    @JsonProperty(value = "id")
    public void setUid(String uid) {
        this.uid = uid;
    }

    @JsonProperty(value = "userCredentials")
    public UserCredentials getUserCredentials() {
        return userCredentials;
    }

    @JsonProperty(value = "userCredentials")
    public void setUserCredentials(UserCredentials userCredentials) {
        this.userCredentials = userCredentials;
    }

    @JsonProperty(value = "organisationUnits")
    public List<OrganisationUnit> getOrganisationUnits() {
        return organisationUnits;
    }

    @JsonProperty(value = "organisationUnits")
    public void setOrganisationUnits(List<OrganisationUnit> organisationUnits) {
        this.organisationUnits = organisationUnits;
    }

    @JsonProperty(value = "userGroups")
    public List<UserGroup> getUserGroups() {
        return userGroups;
    }

    @JsonProperty(value = "userGroups")
    public void setUserGroups(List<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }



}
