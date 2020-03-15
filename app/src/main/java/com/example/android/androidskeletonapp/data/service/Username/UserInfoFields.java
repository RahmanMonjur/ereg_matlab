package com.example.android.androidskeletonapp.data.service.Username;

import org.hisp.dhis.android.core.arch.api.fields.internal.Field;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.user.User;

public class UserInfoFields {
    public static final Field<UserInfo, String> uid = Field.create("id");
    public static final Fields<UserInfo> allFields = Fields.<UserInfo>builder().fields(
            uid
    ).build();

    private UserInfoFields() {

    }
}
