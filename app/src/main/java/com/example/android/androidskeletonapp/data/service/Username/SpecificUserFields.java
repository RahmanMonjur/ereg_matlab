package com.example.android.androidskeletonapp.data.service.Username;

import org.hisp.dhis.android.core.arch.api.fields.internal.Field;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.fields.internal.NestedField;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitFields;

public class SpecificUserFields {

    public static final String ORGANISATION_UNITS = "organisationUnits";
    public static final String USER_GROUPS = "userGroups";

    public static final NestedField<SpecificUser, OrganisationUnit> organisationUnits
            = NestedField.create(ORGANISATION_UNITS);
    public static final NestedField<SpecificUser, UserGroup> userGroups
            = NestedField.create(USER_GROUPS);

    public static final Fields<SpecificUser> someFields = Fields.<SpecificUser>builder()
            .fields(
            organisationUnits.with(OrganisationUnitFields.fieldsInUserCall),
            userGroups.with(UserGroupFields.allfields)
    ).build();

    private SpecificUserFields() {

    }
}
