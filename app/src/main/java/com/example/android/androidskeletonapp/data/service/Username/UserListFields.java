package com.example.android.androidskeletonapp.data.service.Username;

import org.hisp.dhis.android.core.arch.api.fields.internal.Field;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.fields.internal.NestedField;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitFields;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.internal.UserCredentialsFields;

public class UserListFields {

    public static final String USER_CREDENTIALS = "userCredentials";
    public static final String ORGANISATION_UNITS = "organisationUnits";
    public static final String USER_GROUPS = "userGroups";

    public static final Field<UserList, String> uid = Field.create(BaseIdentifiableObject.UID);
    public static final NestedField<UserList, UserCredentials> userCredentials
            = NestedField.create(USER_CREDENTIALS);
    public static final NestedField<UserList, OrganisationUnit> organisationUnits
            = NestedField.create(ORGANISATION_UNITS);
    public static final NestedField<UserList, UserGroup> userGroups
            = NestedField.create(USER_GROUPS);

    public static final Fields<UserList> allFields = Fields.<UserList>builder().fields(
            uid, userCredentials.with(UserCredentialsFields.allFields),
            organisationUnits.with(OrganisationUnitFields.fieldsInUserCall),
            userGroups.with(UserGroupFields.allfields)
    ).build();

    private UserListFields() {

    }
}
