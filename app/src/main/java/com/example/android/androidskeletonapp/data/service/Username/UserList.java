package com.example.android.androidskeletonapp.data.service.Username;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;

import java.util.List;

public class UserList {
    private String uid;

    private UserCredentials userCredentials;
    private List<OrganisationUnit> organisationUnits;

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

}
