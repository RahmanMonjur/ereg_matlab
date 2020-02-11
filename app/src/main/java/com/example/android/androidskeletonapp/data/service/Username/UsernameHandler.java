package com.example.android.androidskeletonapp.data.service.Username;

import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.constant.Constant;

import java.util.Collection;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class UsernameHandler extends IdentifiableHandlerImpl<Username> {

    private final CollectionCleaner<Username> collectionCleaner;

    @Inject
    UsernameHandler(IdentifiableObjectStore<Username> optionStore,
                    CollectionCleaner<Username> collectionCleaner) {
        super(optionStore);
        this.collectionCleaner = collectionCleaner;
    }

    @Override
    protected void afterCollectionHandled(Collection<Username> usernames) {
        collectionCleaner.deleteNotPresent(usernames);
    }
}
