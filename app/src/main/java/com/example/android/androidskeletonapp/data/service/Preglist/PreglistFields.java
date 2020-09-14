package com.example.android.androidskeletonapp.data.service.Preglist;

import org.hisp.dhis.android.core.arch.api.fields.internal.Field;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;

public final class PreglistFields {
    private static final Field<Preglist, String> dhis_id = Field.create("dhis_id");
    private static final Field<Preglist, String> elem_id = Field.create("elem_id");
    private static final Field<Preglist, String> wom_name = Field.create("wom_name");
    private static final Field<Preglist, String> hus_name = Field.create("hus_name");
    private static final Field<Preglist, String> address = Field.create("address");
    private static final Field<Preglist, String> phone_no = Field.create("phone_no");
    private static final Field<Preglist, String> gage = Field.create("gage");

    public static final Fields<Preglist> allFields = Fields.<Preglist>builder().fields(
            dhis_id, elem_id, wom_name, hus_name, address, phone_no, gage
    ).build();

    private PreglistFields() {

    }
}
