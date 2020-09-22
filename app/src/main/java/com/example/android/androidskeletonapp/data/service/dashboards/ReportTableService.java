package com.example.android.androidskeletonapp.data.service.dashboards;

import com.example.android.androidskeletonapp.data.service.Username.SpecificUser;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.filters.internal.Which;
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReportTableService {
    @GET("reportTables/{id}/data")
    Call<ReportTableData> getReportTableData(@Path(value = "id", encoded = false) String reptable,
                                                  @Query("paging") boolean paging);
}
