package com.example.android.androidskeletonapp.data.service.Username;

import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.user.User;

import java.util.Collection;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class UsernameHandler extends IdentifiableHandlerImpl<User> {

    private final CollectionCleaner<User> collectionCleaner;

    @Inject
    UsernameHandler(IdentifiableObjectStore<User> optionStore,
                    CollectionCleaner<User> collectionCleaner) {
        super(optionStore);
        this.collectionCleaner = collectionCleaner;
    }

    @Override
    protected void afterCollectionHandled(Collection<User> usernames) {
        collectionCleaner.deleteNotPresent(usernames);
    }
}
