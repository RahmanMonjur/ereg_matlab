package com.example.android.androidskeletonapp.data.service.Username;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.filters.internal.Which;
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.user.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpecificUserService {
    @GET("users/{id}")
    Call<SpecificUser> getUsers (@Path(value="id", encoded=false) String id, @Query("fields") @Which Fields<SpecificUser> fields,
                                      @Query("paging") boolean paging);
}
