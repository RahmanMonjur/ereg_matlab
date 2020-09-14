package com.example.android.androidskeletonapp.data.service.Username;

import org.hisp.dhis.android.core.arch.api.fields.internal.Field;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.fields.internal.NestedField;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitFields;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.internal.UserCredentialsFields;

public final class UsernameFields {

    public static final String SURNAME = "surname";
    public static final String FIRST_NAME = "firstName";
    public static final String USER_CREDENTIALS = "userCredentials";
    public static final String ORGANISATION_UNITS = "organisationUnits";

    private static final Field<User, String> uid = Field.create(BaseIdentifiableObject.UID);
    private static final Field<User, String> surname = Field.create(SURNAME);
    private static final Field<User, String> firstName = Field.create(FIRST_NAME);
    private static final NestedField<User, UserCredentials> userCredentials
            = NestedField.create(USER_CREDENTIALS);
    private static final NestedField<User, OrganisationUnit> organisationUnits
            = NestedField.create(ORGANISATION_UNITS);


    public static final Fields<User> allFields = Fields.<User>builder().fields(
            uid, surname, firstName, userCredentials.with(UserCredentialsFields.allFields),
            organisationUnits.with(OrganisationUnitFields.fieldsInUserCall)
    ).build();

    private UsernameFields() {

    }
}
