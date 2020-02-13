package com.example.android.androidskeletonapp.data.service.Username;

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.CoreColumns;
import org.hisp.dhis.android.core.common.IdentifiableColumns;

public final class UsernameTableInfo {

    private UsernameTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "User";
        }

        @Override
        public CoreColumns columns() {
            return new Columns();
        }
    };

    public static class Columns extends IdentifiableColumns {
        public static final String VALUE = "value";

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    VALUE
            );
        }
    }
}
