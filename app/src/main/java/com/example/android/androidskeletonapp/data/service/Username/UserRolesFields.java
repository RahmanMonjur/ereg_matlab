package com.example.android.androidskeletonapp.data.service.Username;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.user.UserRole;

public class UserRolesFields {
    private static FieldsHelper<UserRole> fh = new FieldsHelper<>();

    static final Fields<UserRole> allFields = Fields.<UserRole>builder()
            .fields(fh.getIdentifiableFields())
            .build();

    private UserRolesFields() {}
}
