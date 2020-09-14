package com.example.android.androidskeletonapp.data.service.Preglist;

import com.example.android.androidskeletonapp.data.service.Username.UserList;

import org.hisp.dhis.android.core.arch.api.fields.internal.Field;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

public class TeiFields {

    private static FieldsHelper<Tei> fh = new FieldsHelper<>();

    public static final Fields<Tei> allFields = Fields.<Tei>builder()
            .build();

    private TeiFields() {}
}
