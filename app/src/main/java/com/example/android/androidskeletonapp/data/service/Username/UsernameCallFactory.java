package com.example.android.androidskeletonapp.data.service.Username;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.arch.call.factories.internal.ListCallFactoryImpl;
import org.hisp.dhis.android.core.arch.call.fetchers.internal.CallFetcher;
import org.hisp.dhis.android.core.arch.call.fetchers.internal.PayloadNoResourceCallFetcher;
import org.hisp.dhis.android.core.arch.call.internal.GenericCallData;
import org.hisp.dhis.android.core.arch.call.processors.internal.CallProcessor;
import org.hisp.dhis.android.core.arch.call.processors.internal.TransactionalNoResourceSyncCallProcessor;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class UsernameCallFactory extends ListCallFactoryImpl<Username> {

    private final UsernameService service;
    private final Handler<Username> handler;

    @Inject
    UsernameCallFactory(GenericCallData data,
                        APICallExecutor apiCallExecutor,
                        UsernameService service,
                        Handler<Username> handler) {
        super(data, apiCallExecutor);
        this.service = service;
        this.handler = handler;
    }

    @Override
    protected CallFetcher<Username> fetcher() {
        return new PayloadNoResourceCallFetcher<Username>(apiCallExecutor) {
            @Override
            protected retrofit2.Call<Payload<Username>> getCall() {
                return service.usernames(UsernameFields.allFields, Boolean.FALSE);
            }
        };
    }

    @Override
    protected CallProcessor<Username> processor() {
        return new TransactionalNoResourceSyncCallProcessor<>(
                data.databaseAdapter(),
                handler
        );
    }
}