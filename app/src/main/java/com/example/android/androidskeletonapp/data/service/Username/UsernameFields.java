package com.example.android.androidskeletonapp.data.service.Username;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.user.User;

public final class UsernameFields {

    private static final FieldsHelper<User> fh = new FieldsHelper<>();

    static final Fields<User> allFields = Fields.<User>builder()
            .fields(fh.getIdentifiableFields())
            .fields(fh.<Double>field(UsernameTableInfo.Columns.VALUE))
            .build();

    private UsernameFields() {

    }
}
