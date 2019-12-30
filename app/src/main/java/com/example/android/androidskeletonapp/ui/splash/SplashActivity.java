package com.example.android.androidskeletonapp.ui.splash;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.login.LoginActivity;
import com.example.android.androidskeletonapp.ui.main.GlobalClass;
import com.example.android.androidskeletonapp.ui.main.MainActivity;
import com.example.android.androidskeletonapp.ui.main.OrgUnitsActivity;
import com.example.android.androidskeletonapp.ui.programs.ProgramsActivity;
import com.facebook.stetho.Stetho;

import org.hisp.dhis.android.core.D2Manager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

    private final static boolean DEBUG = true;
    private Disposable disposable;
    GlobalClass globalVars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        globalVars = (GlobalClass) getApplicationContext();

        if (DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        disposable = D2Manager.instantiateD2(Sdk.getD2Configuration(this))
                .flatMap(d2 -> d2.userModule().isLogged())
                .doOnSuccess(isLogged -> {
                    if (isLogged) {
                        if(globalVars.getOrgUid() == null) {
                            ActivityStarter.startActivity(this, OrgUnitsActivity.getOrgUnitIntent(this),true);
                        }
                        else {
                            ActivityStarter.startActivity(this, MainActivity.getMainActivityIntent(this), true);
//                            if (Sdk.d2().programModule().programs().blockingCount() > 0) {
//                                ActivityStarter.startActivity(this, ProgramsActivity.getProgramActivityIntent(this), true);
//                            } else {
//                                ActivityStarter.startActivity(this, MainActivity.getMainActivityIntent(this), true);
//                            }
                        }
                    } else {
                        ActivityStarter.startActivity(this, LoginActivity.getLoginActivityIntent(this),true);
                    }
                }).doOnError(throwable -> {
                    throwable.printStackTrace();
                    ActivityStarter.startActivity(this, LoginActivity.getLoginActivityIntent(this),true);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }
}