package com.example.android.androidskeletonapp.data.service.Preglist;

import com.example.android.androidskeletonapp.data.service.Username.UserCredent;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.filters.internal.Which;
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TeiService {
    @GET("trackedEntityInstances")
    Call<Payload<Tei>> getTrackedEntityInstances(@Query("fields") @Which Fields<Tei> fields,
                                                      @Query("paging") boolean paging,
                                                      @Query("ou") String ou,
                                                      @Query("ouMode") String oumode,
                                                      @Query("program") String program,
                                                      @Query("programStartDate") String startdt);
}
