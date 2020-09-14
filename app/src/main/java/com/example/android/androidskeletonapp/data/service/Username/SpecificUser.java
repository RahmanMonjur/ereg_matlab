package com.example.android.androidskeletonapp.data.service.Username;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

import java.util.List;

public class SpecificUser {

    private List<OrganisationUnit> organisationUnits;

    @JsonProperty(value = "organisationUnits")
    public List<OrganisationUnit> getOrganisationUnits() {
        return organisationUnits;
    }

    @JsonProperty(value = "organisationUnits")
    public void setOrganisationUnits(List<OrganisationUnit> organisationUnits) {
        this.organisationUnits = organisationUnits;
    }

    private List<UserGroup> userGroups;

    @JsonProperty(value = "userGroups")
    public List<UserGroup> getUserGroups() {
        return userGroups;
    }

    @JsonProperty(value = "userGroups")
    public void setUserGroups(List<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

}
