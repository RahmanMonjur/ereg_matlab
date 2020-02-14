package com.example.android.androidskeletonapp.data.service.Username;

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.CoreColumns;
import org.hisp.dhis.android.core.common.IdentifiableColumns;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.internal.UserFields;

public final class UsernameTableInfo {

    private UsernameTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "User";
        }

        @Override
        public Columns columns() {
            return new Columns();
        }
    };

    public static class Columns extends IdentifiableColumns {
        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    UsernameFields.SURNAME,
                    UsernameFields.FIRST_NAME,
                    UsernameFields.USER_CREDENTIALS,
                    UsernameFields.ORGANISATION_UNITS
            );
        }
    }
}
