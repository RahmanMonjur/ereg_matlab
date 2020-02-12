package com.example.android.androidskeletonapp.data.service.Username;

import android.database.sqlite.SQLiteStatement;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.IdentifiableStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory;

final class UsernameStore {

    private UsernameStore() {
    }

    private static StatementBinder<Username> BINDER = new IdentifiableStatementBinder<Username>() {
        @Override
        public void bindToStatement(@NonNull Username o, @NonNull SQLiteStatement sqLiteStatement) {
            super.bindToStatement(o, sqLiteStatement);
            sqLiteBind(sqLiteStatement, 7, o.value());
        }
    };

    public static IdentifiableObjectStore<Username> create(DatabaseAdapter databaseAdapter) {
        return StoreFactory.objectWithUidStore(databaseAdapter, UsernameTableInfo.TABLE_INFO, BINDER, Username::create);
    }
}
