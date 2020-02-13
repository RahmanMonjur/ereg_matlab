package com.example.android.androidskeletonapp.data.service.Username;

import org.hisp.dhis.android.core.arch.modules.internal.MetadataModuleDownloader;
import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.user.User;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class UsernameModuleDownloader implements MetadataModuleDownloader<List<User>> {

    private final UsernameCallFactory usernameCallFactory;

    @Inject
    UsernameModuleDownloader(UsernameCallFactory usernameCallFactory) {
        this.usernameCallFactory = usernameCallFactory;
    }

    @Override
    public Callable<List<User>> downloadMetadata() {
        return usernameCallFactory.create();
    }
}
