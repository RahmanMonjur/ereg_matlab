package com.example.android.androidskeletonapp.data.service.Username;

import org.hisp.dhis.android.core.arch.api.fields.internal.Field;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.fields.internal.NestedField;
import org.hisp.dhis.android.core.user.UserRole;


public class UserCredentFields {

    public static final NestedField<UserCredent, UserInfo> userInfo = NestedField.create("userInfo");
    public static final Field<UserCredent, String> username = Field.create("username");
    public static final Field<UserCredent, String> displayName = Field.create("displayName");
    public static final NestedField<UserCredent, UserRole> userRoles = NestedField.create("userRoles");

    public static final Fields<UserCredent> allFields = Fields.<UserCredent>builder().fields(
            userInfo.with(UserInfoFields.allFields),
            username,
            displayName,
            userRoles.with(UserRolesFields.allFields)
    ).build();

    private UserCredentFields() {

    }
}
