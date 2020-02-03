package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.data.service.DateFormatHelper;
import com.example.android.androidskeletonapp.ui.base.ListActivity;
import com.example.android.androidskeletonapp.ui.data_entry.EnrollmentFormActivity;
import com.example.android.androidskeletonapp.ui.main.GlobalClass;
import com.example.android.androidskeletonapp.ui.main.MainActivity;
import com.example.android.androidskeletonapp.ui.main.OrgUnitsActivity;
import com.example.android.androidskeletonapp.ui.programs.ProgramStagesActivity;

import org.hisp.dhis.android.core.D2Manager;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCollectionRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection;
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryCollectionRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.text.TextUtils.isEmpty;

public class TrackedEntityInstancesActivity extends ListActivity  implements OnTrackedEntityInstanceSelectionListener {

    private CompositeDisposable compositeDisposable;
    private String selectedProgram;
    private String newTeiUid;
    private final int ENROLLMENT_RQ = 1210;
    private TrackedEntityInstanceAdapter adapter;
    private static EditText etFirstName;
    GlobalClass globalVars;

    @Override
    public void onTrackedEntityInstanceSelected(String programUid, String teiUid) {
        ActivityStarter.startActivity(this,
                ProgramStagesActivity
                        .getProgramStagesActivityIntent(this, programUid, teiUid),
                false);
    }

    private enum IntentExtra {
        PROGRAM
    }

    public static Intent getTrackedEntityInstancesActivityIntent(Context context, String program) {
        Intent intent = new Intent(context, TrackedEntityInstancesActivity.class);
        intent.putExtra(IntentExtra.PROGRAM.name(), program);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerSetup(R.layout.activity_tracked_entity_instances, R.id.trackedEntityInstancesToolbar,
                R.id.trackedEntityInstancesRecyclerView);

        globalVars = (GlobalClass) getApplicationContext();
        if(globalVars.getOrgUid() == null) {
            if (Sdk.d2() == null){
                D2Manager.instantiateD2(Sdk.getD2Configuration(this));
            }
            else {
                if (Sdk.d2().organisationUnitModule().organisationUnits().byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE).blockingCount() == 1) {
                    globalVars.setOrgUid(Sdk.d2().organisationUnitModule().organisationUnits().byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE).one().blockingGet());
                } else {
                    ActivityStarter.startActivity(this, OrgUnitsActivity.getOrgUnitIntent(this), true);
                }
            }
        }

        selectedProgram = getIntent().getStringExtra(IntentExtra.PROGRAM.name());
        String title = (selectedProgram == null) ? "" : Sdk.d2().programModule().programs().byUid().eq(selectedProgram).one().blockingGet().displayName();
        getSupportActionBar().setTitle( title + " - Enrolled Participants");

        compositeDisposable = new CompositeDisposable();
        observeTrackedEntityInstances();

        if (isEmpty(selectedProgram))
            findViewById(R.id.enrollmentButton).setVisibility(View.GONE);

        findViewById(R.id.enrollmentButton).setOnClickListener(view -> compositeDisposable.add(
                Sdk.d2().programModule().programs().uid(selectedProgram).get()
                        .map(program -> Sdk.d2().trackedEntityModule().trackedEntityInstances()
                                .blockingAdd(
                                        TrackedEntityInstanceCreateProjection.builder()
                                                .organisationUnit(globalVars.getOrgUid().uid())
                                                .trackedEntityType(program.trackedEntityType().uid())
                                                .build()
                                ))
                        .map(teiUid -> {
                            newTeiUid = teiUid;
                            return  teiUid;
                        })
                        .map(teiUid -> EnrollmentFormActivity.getFormActivityIntent(
                                TrackedEntityInstancesActivity.this,
                                teiUid,
                                selectedProgram
                                ))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                activityIntent ->
                                        ActivityStarter.startActivityForResult(
                                                TrackedEntityInstancesActivity.this, activityIntent,ENROLLMENT_RQ),
                                Throwable::printStackTrace
                        )
        ));

        etFirstName = findViewById(R.id.txtParamFirstName);
        ImageButton searchButton = findViewById(R.id.btnSearchTei);
        searchButton.setOnClickListener(view -> {
            getTrackedEntityInstanceQuery().observe(this, trackedEntityInstancePagedList -> {
                adapter.submitList(trackedEntityInstancePagedList);
                findViewById(R.id.trackedEntityInstancesNotificator).setVisibility(
                        trackedEntityInstancePagedList.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }

    private void observeTrackedEntityInstances() {
        adapter = new TrackedEntityInstanceAdapter( this, this, selectedProgram);
        recyclerView.setAdapter(adapter);

        getTeiRepository().getPaged(20).observe(this, trackedEntityInstancePagedList -> {
            adapter.setSource(trackedEntityInstancePagedList.getDataSource());
            adapter.submitList(trackedEntityInstancePagedList);
            findViewById(R.id.trackedEntityInstancesNotificator).setVisibility(
                    trackedEntityInstancePagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    // I don't find the TEI after opening it in the form.

    private TrackedEntityInstanceQueryCollectionRepository getTeiRepository() {
        TrackedEntityInstanceQueryCollectionRepository teiRepository =
                Sdk.d2().trackedEntityModule().trackedEntityInstanceQuery()
                        .offlineOnly();
        if (!isEmpty(selectedProgram)) {
            return teiRepository.byProgram().eq(selectedProgram);
        } else {
            return teiRepository;
        }
    }

    /*
    private TrackedEntityInstanceCollectionRepository getTeiRepository() {
        Date date = new Date();
        try{
            date = DateFormatHelper.parseDateAutoFormat("2017-10-13");
        } catch (Exception e){};

        TrackedEntityInstanceCollectionRepository teiRepository =
                Sdk.d2().trackedEntityModule().trackedEntityInstances()
                        .byCreated().after(date)
                        .withTrackedEntityAttributeValues();
        if (!isEmpty(selectedProgram)) {
            List<String> programUids = new ArrayList<>();
            programUids.add(selectedProgram);
            return teiRepository.byProgramUids(programUids);
        } else {
            return teiRepository;
        }
    }
    */
    private LiveData<PagedList<TrackedEntityInstance>> getTrackedEntityInstanceQuery() {
        adapter = new TrackedEntityInstanceAdapter( this,this, selectedProgram);
        recyclerView.setAdapter(adapter);
        return Sdk.d2().trackedEntityModule().trackedEntityInstanceQuery()
                .byQuery().like(etFirstName.getText().toString())
                .offlineFirst().getPaged(15);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == ENROLLMENT_RQ && resultCode == RESULT_OK){
            adapter.invalidateSource();
        }
        else if (requestCode == ENROLLMENT_RQ && resultCode == RESULT_CANCELED) {
            if(newTeiUid != null){
                try {
                    Sdk.d2().trackedEntityModule().trackedEntityInstances().uid(newTeiUid).blockingDelete();
                } catch (D2Error d2Error) {
                    d2Error.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        etFirstName.setText("");
        observeTrackedEntityInstances();

    }
}
