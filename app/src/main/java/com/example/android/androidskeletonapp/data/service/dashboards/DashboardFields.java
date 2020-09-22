package com.example.android.androidskeletonapp.data.service.dashboards;

import com.example.android.androidskeletonapp.data.service.Preglist.Preglist;
import com.example.android.androidskeletonapp.data.service.Username.UserList;

import org.hisp.dhis.android.core.arch.api.fields.internal.Field;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.fields.internal.NestedField;
import org.hisp.dhis.android.core.user.UserCredentials;

public class DashboardFields {
    private static final Field<Dashboard, String> id = Field.create("id");
    private static final Field<Dashboard, String> name = Field.create("name");
    public static final NestedField<Dashboard, DashboardItem> dashboardItems
            = NestedField.create("dashboardItems");

    public static final Fields<Dashboard> allFields = Fields.<Dashboard>builder().fields(
            id, name, dashboardItems
    ).build();

    private DashboardFields() {

    }

}
