package com.example.android.androidskeletonapp.data.service.Username;

import android.net.wifi.hotspot2.pps.Credential;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.filters.internal.Which;
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserCredentialService {
    @GET("userCredentials")
    Call<Payload<UserCredent>> getUsernames(@Query("fields") @Which Fields<UserCredent> fields,
                                                @Query("paging") boolean paging);
}
