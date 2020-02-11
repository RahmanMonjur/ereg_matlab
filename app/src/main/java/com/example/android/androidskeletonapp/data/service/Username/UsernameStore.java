package com.example.android.androidskeletonapp.data.service.Username;

import android.database.sqlite.SQLiteStatement;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.IdentifiableStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory;
import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.constant.ConstantTableInfo;

import androidx.annotation.NonNull;

import static org.hisp.dhis.android.core.arch.db.stores.internal.StoreUtils.sqLiteBind;

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
