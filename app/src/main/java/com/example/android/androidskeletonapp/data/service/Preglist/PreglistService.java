package com.example.android.androidskeletonapp.data.service.Preglist;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.filters.internal.Which;
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PreglistService {
    @GET("Preglist")
    Call<Payload<Preglist>> getPreglist (@Query("fields") @Which Fields<Preglist> fields, @Query("paging") boolean paging);
}
