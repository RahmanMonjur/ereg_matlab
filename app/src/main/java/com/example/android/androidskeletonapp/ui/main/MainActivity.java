package com.example.android.androidskeletonapp.ui.main;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.data.service.DateFormatHelper;
import com.example.android.androidskeletonapp.data.service.Preglist.Tei;
import com.example.android.androidskeletonapp.data.service.Preglist.TeiFields;
import com.example.android.androidskeletonapp.data.service.Preglist.TeiService;
import com.example.android.androidskeletonapp.data.service.SyncStatusHelper;
import com.example.android.androidskeletonapp.data.service.Username.SpecificUser;
import com.example.android.androidskeletonapp.data.service.Username.SpecificUserFields;
import com.example.android.androidskeletonapp.data.service.Username.SpecificUserService;
import com.example.android.androidskeletonapp.data.service.Username.UserCredent;
import com.example.android.androidskeletonapp.data.service.Username.UserCredentFields;
import com.example.android.androidskeletonapp.data.service.Username.UserCredentialService;
import com.example.android.androidskeletonapp.data.service.Username.UserGroup;
import com.example.android.androidskeletonapp.data.service.Username.UserList;
import com.example.android.androidskeletonapp.data.service.Username.UserListFields;
import com.example.android.androidskeletonapp.data.service.Username.UserListService;
import com.example.android.androidskeletonapp.data.service.Username.Username;
import com.example.android.androidskeletonapp.data.service.Username.UsernameFields;
import com.example.android.androidskeletonapp.data.service.Username.UsernameService;
import com.example.android.androidskeletonapp.data.service.dashboards.Dashboard;
import com.example.android.androidskeletonapp.data.service.dashboards.DashboardFields;
import com.example.android.androidskeletonapp.data.service.dashboards.DashboardItem;
import com.example.android.androidskeletonapp.data.service.dashboards.DashboardService;
import com.example.android.androidskeletonapp.data.service.dashboards.ReportTable;
import com.example.android.androidskeletonapp.data.service.dashboards.ReportTableData;
import com.example.android.androidskeletonapp.data.service.dashboards.ReportTableHeader;
import com.example.android.androidskeletonapp.data.service.dashboards.ReportTableService;
import com.example.android.androidskeletonapp.ui.code_executor.CodeExecutorActivity;
import com.example.android.androidskeletonapp.ui.d2_errors.D2ErrorActivity;
import com.example.android.androidskeletonapp.ui.foreign_key_violations.ForeignKeyViolationsActivity;
import com.example.android.androidskeletonapp.ui.tracked_entity_instances.TrackedEntityInstancesActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserRole;
import org.hisp.dhis.android.core.user.internal.UserCredentialsFields;
import org.hisp.dhis.android.core.user.internal.UserFields;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.androidskeletonapp.data.service.LogOutService.logOut;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CompositeDisposable compositeDisposable;

    private FloatingActionButton syncMetadataButton;
    private FloatingActionButton syncDataButton;
    private FloatingActionButton uploadDataButton;

    private TextView syncStatusText;
    private ProgressBar progressBar;

    private boolean isSyncing = false;
    GlobalClass globalVars;

    File myInternalFile;
    private String filename = "fwalist.txt";

    public static Intent getMainActivityIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        globalVars = (GlobalClass) getApplicationContext();
        compositeDisposable = new CompositeDisposable();
        myInternalFile = this.getFileStreamPath(filename);

        OrganisationUnit orgunit = Sdk.d2().organisationUnitModule().organisationUnits()
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .one().blockingGet();
        if (orgunit != null) {
            String locale = "en";
            String regex = "^[0-9a-zA-Z \\/_?:.,\\s-]+$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(orgunit.displayName());
            if (!matcher.matches()) {
                locale = "bn";
            }
            globalVars.setUserLocale(locale);
        }
        this.setTitle(globalVars.getTranslatedWord("Home"));

        User user = getUser();
        TextView greeting = findViewById(R.id.greeting);
        greeting.setText(globalVars.getTranslatedWord("Welcome") + String.format(" %s!", user.displayName()));

        inflateMainView();
        createNavigationView(user);

    }


    @Override
    protected void onResume() {
        super.onResume();
        updateSyncDataAndButtons();
    }

    private User getUser() {
        return Sdk.d2().userModule().user().blockingGet();
    }

    private User getUserFromCursor() {
        try (Cursor cursor = Sdk.d2().databaseAdapter().query("SELECT * FROM user;")) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                return User.create(cursor);
            } else {
                return null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    private void inflateMainView() {
        syncMetadataButton = findViewById(R.id.syncMetadataButton);
        syncDataButton = findViewById(R.id.syncDataButton);
        uploadDataButton = findViewById(R.id.uploadDataButton);
        ((TextView) findViewById(R.id.syncMetadataText)).setText(globalVars.getTranslatedWord("Update metadata and data"));
        ((TextView) findViewById(R.id.syncDataText)).setText(globalVars.getTranslatedWord("Download data"));
        ((TextView) findViewById(R.id.uploadDataText)).setText(globalVars.getTranslatedWord("Upload data"));

        syncStatusText = findViewById(R.id.notificator);
        progressBar = findViewById(R.id.syncProgressBar);

        if (SyncStatusHelper.programCount() + SyncStatusHelper.trackedEntityInstanceCount() == 0) {
            downloadMetadataAndData();
        }

        syncMetadataButton.setOnClickListener(view -> {
            if (isNetworkConnected()) {
                setSyncing();
                Snackbar.make(view, globalVars.getTranslatedWord("Downloading metadata and data..."), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                syncStatusText.setText(globalVars.getTranslatedWord("Downloading metadata and data..."));
                downloadMetadataAndData();
            } else {
                Toast.makeText(this, globalVars.getTranslatedWord("You do not have stable internet connection now.\nplease try later."), Toast.LENGTH_LONG).show();
            }
        });

        syncDataButton.setOnClickListener(view -> {
            setSyncing();
            Snackbar.make(view, globalVars.getTranslatedWord("Downloading data…"), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            syncStatusText.setText(globalVars.getTranslatedWord(getResources().getString(R.string.syncing_data)));
            downloadData();
        });

        uploadDataButton.setOnClickListener(view -> {
            if (isNetworkConnected()) {
                setSyncing();
                Snackbar.make(view, globalVars.getTranslatedWord("Uploading data…"), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                syncStatusText.setText(globalVars.getTranslatedWord(getResources().getString(R.string.uploading_data)));
                uploadData();
            } else {
                Toast.makeText(this, globalVars.getTranslatedWord("You do not have stable internet connection now.\nplease try later."), Toast.LENGTH_LONG).show();
            }
        });

        /*
        try {
            Sdk.d2().databaseAdapter().execSQL("CREATE TABLE IF NOT EXISTS NewUserGroups(UserUid VARCHAR, UserName VARCHAR, UserGroups VARCHAR);");

        } catch (Exception ex){
            Log.d("TAG","errrrrrrrrr");
        }
        */
    }

    private void downloadInitialMetadata(){
        setSyncing();
        syncMetadata();
    }

    private void setSyncing() {
        isSyncing = true;
        progressBar.setVisibility(View.VISIBLE);
        syncStatusText.setVisibility(View.VISIBLE);
        updateSyncDataAndButtons();
    }

    private void setSyncingFinished() {
        isSyncing = false;
        progressBar.setVisibility(View.GONE);
        syncStatusText.setVisibility(View.GONE);
        updateSyncDataAndButtons();
    }

    private void disableAllButtons() {
        setEnabledButton(syncMetadataButton, false);
        setEnabledButton(syncDataButton, false);
        setEnabledButton(uploadDataButton, false);
    }

    private void enablePossibleButtons(boolean metadataSynced) {
        if (!isSyncing) {
            setEnabledButton(syncMetadataButton, true);
            if (metadataSynced) {
                setEnabledButton(syncDataButton, true);
                if (SyncStatusHelper.isThereDataToUpload()) {
                    setEnabledButton(uploadDataButton, true);
                }
            }
        }
    }

    private void setEnabledButton(FloatingActionButton floatingActionButton, boolean enabled) {
        floatingActionButton.setEnabled(enabled);
        floatingActionButton.setAlpha(enabled ? 1.0f : 0.3f);
    }

    private void updateSyncDataAndButtons() {
        disableAllButtons();

        int programCount = SyncStatusHelper.programCount();
        int dataSetCount = SyncStatusHelper.dataSetCount();
        int trackedEntityInstanceCount = SyncStatusHelper.trackedEntityInstanceCount();
        int singleEventCount = SyncStatusHelper.singleEventCount();
        int dataValueCount = SyncStatusHelper.dataValueCount();

        enablePossibleButtons(programCount + dataSetCount > 0);


        if (globalVars.getOrgUid() != null) {
            Date trialDate = new Date();
            try {
                trialDate = DateFormatHelper.parseDateAutoFormat("2018-10-13");
            } catch (Exception e) {
            }

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1);


            int totalEnroll = Sdk.d2().enrollmentModule().enrollments()
                    .byOrganisationUnit().eq(globalVars.getOrgUid().uid())
                    .byProgram().eq("WSGAb5XwJ3Y")
                    .byStatus().eq(EnrollmentStatus.ACTIVE)
                    .byCreated().after(trialDate)
                    .blockingCount();

            int lastMonthEnroll = Sdk.d2().enrollmentModule().enrollments()
                    .byOrganisationUnit().eq(globalVars.getOrgUid().uid())
                    .byCreated().after(trialDate)
                    .byProgram().eq("WSGAb5XwJ3Y")
                    .byStatus().eq(EnrollmentStatus.ACTIVE)
                    .byEnrollmentDate().after(calendar.getTime())
                    .blockingCount();

            int lastMonthVisit = Sdk.d2().eventModule().events()
                    .byProgramStageUid().eq("V3hCCKHGAaz")
                    .byOrganisationUnitUid().eq(globalVars.getOrgUid().uid())
                    .byCreated().after(calendar.getTime())
                    .blockingCount();

            ((TextView)findViewById(R.id.lblInd1)).setText(globalVars.getTranslatedWord("Number of women enrolled in the Org Unit"));
            ((TextView)findViewById(R.id.lblInd2)).setText(globalVars.getTranslatedWord("Number of women enrolled in last month"));
            ((TextView)findViewById(R.id.lblInd3)).setText(globalVars.getTranslatedWord("Number of HH visited in last month"));
            ((TextView)findViewById(R.id.indicator1)).setText(MessageFormat.format("{0}", totalEnroll));
            ((TextView)findViewById(R.id.indicator2)).setText(MessageFormat.format("{0}", lastMonthEnroll));
            ((TextView)findViewById(R.id.indicator3)).setText(MessageFormat.format("{0}", lastMonthVisit));
        }

    }

    private void createNavigationView(User user) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        TextView firstName = headerView.findViewById(R.id.firstName);
        TextView email = headerView.findViewById(R.id.email);
        firstName.setText(user.surname());
        email.setText(user.email());
    }

    private void syncMetadata() {
        compositeDisposable.add(downloadMetadata()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(Throwable::printStackTrace)
                .doOnComplete(() -> {
                    setSyncingFinished();
                })
                .doOnError(throwable -> Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show())
                .subscribe());
    }

    private Observable<D2Progress> downloadMetadata() {
        return Sdk.d2().metadataModule().download();
    }

    private void downloadMetadataAndData(){
        setSyncing();
            //syncStatusText.setText(globalVars.getTranslatedWord("Downloading metadata and data..."));
                compositeDisposable.add(
                    downloadMetadata()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnComplete(() -> {
                                downloadDataMain();
                            })
                            .doOnError(Throwable::printStackTrace)
                            .subscribe());

    }

    private void downloadDataMain() {
        syncStatusText.setText(globalVars.getTranslatedWord("Downloading metadata and data..."));
        compositeDisposable.add(
                downloadTrackedEntityInstances()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> {
                            //setSyncingFinished();
                            if (globalVars.getUserLocale() == null) {
                                String locale = "en";
                                String orgunitname = Sdk.d2().organisationUnitModule().organisationUnits()
                                        .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                                        .one().blockingGet().displayName();
                                String regex = "^[0-9a-zA-Z \\/_?:.,\\s-]+$";
                                Pattern pattern = Pattern.compile(regex);
                                Matcher matcher = pattern.matcher(orgunitname);
                                if (!matcher.matches())
                                    locale = "bn";
                                globalVars.setUserLocale(locale);
                            }

                            if (myInternalFile.exists()) {
                                boolean deleted = myInternalFile.delete();
                            }
                            myInternalFile = new File(this.getFilesDir(), filename);
                            Log.d("MainActivity", "Started first service");
                            UserCredentialService userservice = Sdk.d2().retrofit().create(UserCredentialService.class);
                            Call<Payload<UserCredent>> call = userservice.getUsernames(UserCredentFields.allFields, false);
                            call.enqueue(new Callback<Payload<UserCredent>>() {
                                @Override
                                public void onResponse(Call<Payload<UserCredent>> call, Response<Payload<UserCredent>> response) {
                                    List<UserCredent> users = response.body().items();
                                    for (UserCredent uc : users) {
                                        List<UserRole> urs = uc.getUserRoles();
                                        for (UserRole ur : urs) {
                                            if (ur.uid().equals("lItc9BR90WI")) {
                                                SpecificUserService specUserService = Sdk.d2().retrofit().create(SpecificUserService.class);
                                                Call<SpecificUser> call1 = specUserService.getUsers(uc.getUserInfo().getUid(), SpecificUserFields.someFields, false);
                                                call1.enqueue(new Callback<SpecificUser>() {
                                                    @Override
                                                    public void onResponse(Call<SpecificUser> call1, Response<SpecificUser> response1) {
                                                        try {
                                                            //Sdk.d2().databaseAdapter().delete("NewUserGroups");

                                                            BufferedWriter buf = new BufferedWriter(new FileWriter(myInternalFile, true));
                                                            List<OrganisationUnit> ous1 = response1.body().getOrganisationUnits();
                                                            String union = (ous1.size() > 0) ? ous1.get(0).path() : "";
                                                            List<UserGroup> ugs1 = response1.body().getUserGroups();

                                                            for (UserGroup grp : ugs1) {
                                                                //Extracting FWA users only
                                                                if (grp.getUid().equals("ow22Lm7dg4l")) {
                                                                    buf.append(uc.getUsername() + '~' + uc.getDisplayName() + '~' + union);
                                                                    buf.newLine();

                                                                    //Sdk.d2().databaseAdapter().execSQL("INSERT INTO NewUserGroups(UserUid, UserName , UserGroups ) VALUES ('"+uc.getUsername()+"', '"+uc.getDisplayName()+"','' )");
                                                                }
                                                            }

                                                            buf.close();
                                                        } catch (Exception ex) {

                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<SpecificUser> call1, Throwable t1) {
                                                        Toast.makeText(MainActivity.this, t1.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Payload<UserCredent>> call, Throwable t) {
                                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            Log.d("MainActivity", "Started second service");
                            String org = Sdk.d2().organisationUnitModule().organisationUnits()
                                    .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                                    .one().blockingGet().path().split("/")[5];
                            ArrayList<String> teiuids = new ArrayList<>();
                            TeiService teiservice = Sdk.d2().retrofit().create(TeiService.class);
                            Call<Payload<Tei>> callt = teiservice.getTrackedEntityInstances(TeiFields.allFields, false, org, "DESCENDANTS", "ZBIqxwVixn8", "2018-10-13");
                            callt.enqueue(new Callback<Payload<Tei>>() {
                                @Override
                                public void onResponse(Call<Payload<Tei>> call, Response<Payload<Tei>> response) {
                                    List<Tei> teis = response.body().items();
                                    for (Tei tei : teis) {
                                        teiuids.add(tei.getUid());
                                    }
                                    Sdk.d2().trackedEntityModule().trackedEntityInstanceDownloader().byUid().in(teiuids).download()
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .doOnComplete(() -> {
                                                Log.d("MainActivity", "TEI downloaded");
                                            })
                                            .doOnError(Throwable::printStackTrace)
                                            .subscribe();
                                }
                                @Override
                                public void onFailure(Call<Payload<Tei>> call, Throwable t) {
                                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            List<List<String>> tables = new ArrayList<>();
                            DashboardService dashservice = Sdk.d2().retrofit().create(DashboardService.class);
                            Call<Payload<Dashboard>> dashcall = dashservice.getDashboards(DashboardFields.allFields, false);
                            dashcall.enqueue(new Callback<Payload<Dashboard>>() {
                                @Override
                                public void onResponse(Call<Payload<Dashboard>> call, Response<Payload<Dashboard>> response) {
                                    List<Dashboard> dashboards = response.body().items();
                                    for (Dashboard dashboard : dashboards) {
                                        for (DashboardItem dashboardItem : dashboard.getDashboardItems()) {
                                            ReportTable rtable = dashboardItem.getReportTable();
                                            if (rtable != null) {
                                                ReportTableService rtableservice = Sdk.d2().retrofit().create(ReportTableService.class);
                                                Call<ReportTableData> rtablecall = rtableservice.getReportTableData(rtable.getUid(), false);
                                                rtablecall.enqueue(new Callback<ReportTableData>() {
                                                    @Override
                                                    public void onResponse(Call<ReportTableData> call, Response<ReportTableData> response) {
                                                        ReportTableData rtdata = response.body();
                                                        if (rtdata != null){
                                                            List<ReportTableHeader> headers = rtdata.getHeaders();
                                                            for (List<String> rows : rtdata.getRows()) {
                                                                List<String> tableCells = new ArrayList<>();
                                                                for (int i = 0; i < headers.size(); i++) {
                                                                    ReportTableHeader header = headers.get(i);
                                                                    if (!header.getHidden()) {
                                                                        tableCells.add(rows.get(i));
                                                                    }
                                                                }
                                                                tables.add(tableCells);
                                                            }
                                                        }
                                                        Log.d("MainActivity", String.valueOf(tables.size()));
                                                    }
                                                    @Override
                                                    public void onFailure(Call<ReportTableData> call, Throwable t) {
                                                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                                @Override
                                public void onFailure(Call<Payload<Dashboard>> call, Throwable t) {
                                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                            if (globalVars.getOrgUid() == null) {
                                ActivityStarter.startActivity(this, OrgUnitsActivity.getOrgUnitIntent(this), false);
                            }
                            setSyncingFinished();
                        })
                        .doOnError(Throwable::printStackTrace)
                        .subscribe());
    }

    private void downloadData() {
        compositeDisposable.add(
                Observable.merge(
                        downloadTrackedEntityInstances(),
                        downloadSingleEvents(),
                        downloadAggregatedData()
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> {
                            setSyncingFinished();
                        })
                        .doOnError(Throwable::printStackTrace)
                        .subscribe());
    }

    private void downloadInitialData() {
        syncStatusText.setText("Downloading initial metadata and data...");
        compositeDisposable.add(
                downloadTrackedEntityInstances()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> {
                            String locale = "en";
                            String orgunitname = Sdk.d2().organisationUnitModule().organisationUnits()
                                    .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                                    .one().blockingGet().displayName();
                            String regex = "^[0-9a-zA-Z \\/_?:.,\\s-]+$";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(orgunitname);
                            if (!matcher.matches())
                                locale = "bn";
                            globalVars.setUserLocale(locale);

                            setSyncingFinished();
                            ActivityStarter.startActivity(this, OrgUnitsActivity.getOrgUnitIntent(this), false);
                        })
                        .doOnError(Throwable::printStackTrace)
                        .subscribe());
    }

    private Observable<D2Progress> downloadTrackedEntityInstances() {
        return Sdk.d2().trackedEntityModule()
                .trackedEntityInstanceDownloader()
                .limit(1000)
                .limitByOrgunit(false).limitByProgram(false).download();
    }

    /*
    private Observable<D2Progress> downloadTrackedEntityInstancesBySearchScopes() {
        String org = Sdk.d2().organisationUnitModule().organisationUnits()
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .one().blockingGet().path().split("/")[5];
        ArrayList<String> teiuids = new ArrayList<>();
        TeiService teiservice = Sdk.d2().retrofit().create(TeiService.class);
        Call<Payload<TrackedEntityInstance>> callt = teiservice.getTrackedEntityInstances(TeiFields.allFields, false,org,"DESCENDANTS","ZBIqxwVixn8","2018-10-13");
        callt.enqueue(new Callback<Payload<TrackedEntityInstance>>() {
            @Override
            public void onResponse(Call<Payload<TrackedEntityInstance>> call, Response<Payload<TrackedEntityInstance>> response) {
                List<TrackedEntityInstance> teis = response.body().items();
                for (TrackedEntityInstance tei : teis) {
                    teiuids.add(tei.uid());
                }
            }
            @Override
            public void onFailure(Call<Payload<TrackedEntityInstance>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return Sdk.d2().trackedEntityModule()
                .trackedEntityInstanceDownloader()
                .byUid().in(teiuids).download();
    }
    */

    private Observable<D2Progress> downloadSingleEvents() {
        return Sdk.d2().eventModule().eventDownloader()
                .limit(1000)
                .limitByOrgunit(false).limitByProgram(false).download();
    }

    private Observable<D2Progress> downloadAggregatedData() {
        return Sdk.d2().aggregatedModule().data().download();
    }

    private void uploadData() {
        compositeDisposable.add(
                Sdk.d2().fileResourceModule().fileResources().upload()
                        .concatWith(Sdk.d2().trackedEntityModule().trackedEntityInstances().upload())
                        .concatWith(Sdk.d2().dataValueModule().dataValues().upload())
                        .concatWith(Sdk.d2().eventModule().events().upload())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(this::setSyncingFinished)
                        .doOnError(Throwable::printStackTrace)
                        .subscribe());
    }

    private void wipeData() {
        compositeDisposable.add(
                Observable.fromCallable(() -> Sdk.d2().wipeModule().wipeData())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError(Throwable::printStackTrace)
                        .doOnComplete(this::setSyncingFinished)
                        .subscribe());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();if (id == R.id.navPrograms) {
            //ActivityStarter.startActivity(this, ProgramsActivity.getProgramActivityIntent(this), false);
            ActivityStarter.startActivity(this,
                    TrackedEntityInstancesActivity
                            .getTrackedEntityInstancesActivityIntent(this, "ZBIqxwVixn8"),
                    false);
        } else if (id == R.id.navOrgUnit) {
            ActivityStarter.startActivity(this, OrgUnitsActivity.getOrgUnitIntent(this), false);
        } else if (id == R.id.navPregList) {
            ActivityStarter.startActivity(this, PreglistActivity.getPreglistIntent(this), false);
        } else if (id == R.id.navD2Errors) {
            ActivityStarter.startActivity(this, D2ErrorActivity.getIntent(this), false);
        }  else if (id == R.id.navWipeData) {
            syncStatusText.setText(R.string.wiping_data);
            wipeData();
        } else if (id == R.id.navExit) {
            compositeDisposable.add(logOut(this));
        }

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean isNetworkConnected() {
        /*
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
        */

        try {
            String command = "ping -c 1 eregistries.mohfw.gov.bd";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }


    }
}
