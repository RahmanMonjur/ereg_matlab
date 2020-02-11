package com.example.android.androidskeletonapp.data.service.Username;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DoubleFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class UsernameCollectionRepository extends ReadOnlyIdentifiableCollectionRepositoryImpl<
        Username, UsernameCollectionRepository> {

    @Inject
    UsernameCollectionRepository(
            final IdentifiableObjectStore<Username> store,
            final Map<String, ChildrenAppender<Username>> childrenAppenders,
            final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new UsernameCollectionRepository(store, childrenAppenders, s)));
    }

    public DoubleFilterConnector<UsernameCollectionRepository> byValue() {
        return cf.doubleC(UsernameTableInfo.Columns.VALUE);
    }

}
