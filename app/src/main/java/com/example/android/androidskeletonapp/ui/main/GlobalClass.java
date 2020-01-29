package com.example.android.androidskeletonapp.ui.main;


import android.app.Application;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

public class GlobalClass extends Application {

    private OrganisationUnit userScopeOrgUid;
    public OrganisationUnit getOrgUid() {
        return userScopeOrgUid;
    }
    public void setOrgUid(OrganisationUnit pOrgUid) {
        userScopeOrgUid = pOrgUid;
    }

    private String userDateFormat;
    public String getUserDateFormat() {
        return userDateFormat;
    }
    public void setUserDateFormat(String dateFormat) {
        userDateFormat = dateFormat;
    }

}
