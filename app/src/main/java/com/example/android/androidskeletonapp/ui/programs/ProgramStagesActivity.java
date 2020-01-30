package com.example.android.androidskeletonapp.ui.programs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.base.ListActivity;
import com.example.android.androidskeletonapp.ui.data_entry.EventFormActivity;
import com.example.android.androidskeletonapp.ui.events.EventsActivity;
import com.example.android.androidskeletonapp.ui.main.GlobalClass;
import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProgramStagesActivity extends ListActivity implements OnProgramStageSelectionListener {

    GlobalClass globalVars;
    private Disposable disposable;
    private String selectedEnrollment;
    private String selectedProgram;
    private enum IntentExtra {
        PROGRAM,
        TEI
    }

    public static Intent getProgramStagesActivityIntent(Context context, String programUid, String teiUid) {
        Intent intent = new Intent(context, ProgramStagesActivity.class);
        intent.putExtra(IntentExtra.PROGRAM.name(), programUid);
        intent.putExtra(IntentExtra.TEI.name(), teiUid);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerSetup(R.layout.activity_program_stages, R.id.programStageToolbar, R.id.programStageRecyclerView);
        selectedProgram = getIntent().getStringExtra(IntentExtra.PROGRAM.name());
        String title = (selectedProgram == null) ? "" : Sdk.d2().programModule().programs().byUid().eq(selectedProgram).one().blockingGet().displayName();
        getSupportActionBar().setTitle(title + " - Program Stages");
        globalVars = (GlobalClass) getApplicationContext();
        selectedEnrollment = Sdk.d2().enrollmentModule().enrollments().byProgram().eq(getIntent().getStringExtra(IntentExtra.PROGRAM.name()))
                .byTrackedEntityInstance().eq(getIntent().getStringExtra(IntentExtra.TEI.name())).one().blockingGet().uid();
        observeProgramStages(getIntent().getStringExtra(IntentExtra.PROGRAM.name()), getIntent().getStringExtra(IntentExtra.TEI.name()));
    }

    private void observeProgramStages(String programUid, String teiUid) {
        ProgramStagesAdapter adapter = new ProgramStagesAdapter(this, programUid, teiUid);

        disposable = Sdk.d2().organisationUnitModule().organisationUnits().get()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(organisationUnitUids -> Sdk.d2().programModule().programStages()
                        .byProgramUid().eq(programUid)
                        .orderBySortOrder(RepositoryScope.OrderByDirection.ASC)
                        .getPaged(20))
                .subscribe(programStages -> programStages.observe(this, programStagePagedList -> {
                    adapter.submitList(programStagePagedList);
                    findViewById(R.id.programStageNotificator).setVisibility(
                            programStagePagedList.isEmpty() ? View.VISIBLE : View.GONE);
                }));

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onProgramStageSelected(String programUid, String programStageUid, String teiUid) {

        Integer eventsNumber = Sdk.d2().eventModule().events()
                .byProgramUid().eq(programUid)
                .byProgramStageUid().eq(programStageUid)
                .byTrackedEntityInstanceUids(Lists.newArrayList(teiUid))
                .blockingCount();

        if (eventsNumber>0) {
            ActivityStarter.startActivity(this,
                    EventsActivity
                            .getIntent(this, programUid, programStageUid, teiUid),
                    false);
        }
        else {

            ActivityStarter.startActivity(this,
                    EventFormActivity
                            .getFormActivityIntent(this, null, programUid, programStageUid, selectedEnrollment,
                                    EventFormActivity.FormType.CREATE),
                    false);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        observeProgramStages(getIntent().getStringExtra(IntentExtra.PROGRAM.name()), getIntent().getStringExtra(IntentExtra.TEI.name()));
        //Toast.makeText(this, "testing", Toast.LENGTH_SHORT).show();

    }


}
