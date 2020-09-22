package com.example.android.androidskeletonapp.data.service.dashboards;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.filters.internal.Which;
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.joda.time.chrono.AssembledChronology;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DashboardService {
    @GET("dashboards")
    Call<Payload<Dashboard>> getDashboards(@Query("fields") @Which Fields<Dashboard> fields, @Query("paging") boolean paging);


}
