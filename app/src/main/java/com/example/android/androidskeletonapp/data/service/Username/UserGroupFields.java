package com.example.android.androidskeletonapp.data.service.Username;

import org.hisp.dhis.android.core.arch.api.fields.internal.Field;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;

public class UserGroupFields {
    private static final Field<UserGroup, String> uid = Field.create("Uid");

    public static final Fields<UserGroup> allfields = Fields.<UserGroup>builder()
            .fields(
                    uid
            ).build();

    private  UserGroupFields(){

    }
}
