package com.example.android.androidskeletonapp.data.service.Username;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.filters.internal.Which;
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.user.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface UsernameService {
    @GET("usernames")
    Call<Payload<Username>> usernames (@Query("fields") @Which Fields<Username> fields,
                                  @Query("paging") boolean paging);
}
