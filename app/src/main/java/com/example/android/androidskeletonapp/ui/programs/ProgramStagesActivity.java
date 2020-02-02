package com.example.android.androidskeletonapp.ui.programs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.data.service.forms.EnrollmentFormService;
import com.example.android.androidskeletonapp.ui.base.ListActivity;
import com.example.android.androidskeletonapp.ui.data_entry.EventFormActivity;
import com.example.android.androidskeletonapp.ui.data_entry.field_type_holder.SpinnerItems;
import com.example.android.androidskeletonapp.ui.events.EventsActivity;
import com.example.android.androidskeletonapp.ui.main.GlobalClass;
import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.arch.helpers.AccessHelper;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection;
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.program.Program;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProgramStagesActivity extends ListActivity implements OnProgramStageSelectionListener {

    GlobalClass globalVars;
    private Disposable disposable;
    private String selectedEnrollment;
    private String selectedProgram;
    private Spinner listProgram;
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
        //String title = (selectedProgram == null) ? "" : Sdk.d2().programModule().programs().byUid().eq(selectedProgram).one().blockingGet().displayName();
        getSupportActionBar().setTitle("Program Stages");
        globalVars = (GlobalClass) getApplicationContext();

        observeProgramStages(selectedProgram, getIntent().getStringExtra(IntentExtra.TEI.name()));

        listProgram = (Spinner)findViewById(R.id.listProgram);
        listProgram.setAdapter(new ArrayAdapter<SpinnerItems>(this.getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, getPrograms()));

        listProgram.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!((SpinnerItems)listProgram.getSelectedItem()).getId().equals("")) {
                    if (selectedProgram != ((SpinnerItems) listProgram.getSelectedItem()).getId()) {
                        selectedProgram = ((SpinnerItems)listProgram.getSelectedItem()).getId();
                        Enrollment enrollment = Sdk.d2().enrollmentModule().enrollments()
                                .byTrackedEntityInstance().eq(getIntent().getStringExtra(IntentExtra.TEI.name()))
                                .byProgram().eq(selectedProgram)
                                .byStatus().eq(EnrollmentStatus.ACTIVE)
                                .one().blockingGet();
                        if (enrollment != null) {
                            selectedEnrollment = enrollment.uid();
                            observeProgramStages(((SpinnerItems)listProgram.getSelectedItem()).getId(), getIntent().getStringExtra(IntentExtra.TEI.name()));
                        } else {
                            new AlertDialog.Builder(ProgramStagesActivity.this)
                                    .setTitle("Enrollment Confirmation")
                                    .setMessage("This TEI has no enrollment in this program.\nDo you want to enroll this TEI now?")
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .setPositiveButton("Yes, I want to enroll", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            if (EnrollmentFormService.getInstance().init(
                                                    Sdk.d2(),
                                                    getIntent().getStringExtra(IntentExtra.TEI.name()),
                                                    selectedProgram,
                                                    globalVars.getOrgUid().uid())) {

                                                selectedEnrollment = EnrollmentFormService.getInstance().getEnrollmentUid();
                                                observeProgramStages(selectedProgram, getIntent().getStringExtra(IntentExtra.TEI.name()));
                                            }

                                        }})
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            listProgram.setSelection(1);
                                            selectedProgram = "ZBIqxwVixn8";
                                        }})
                                    .show();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


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
        observeProgramStages(selectedProgram, getIntent().getStringExtra(IntentExtra.TEI.name()));

    }

    private List<SpinnerItems> getPrograms() {
        List<SpinnerItems> labels = new ArrayList<SpinnerItems>();

        List<Program> programs = Sdk.d2().programModule().programs()
                .byOrganisationUnitUid(globalVars.getOrgUid().uid())
                .blockingGet();
        //labels.add(new SpinnerItems("", "Please select a program to switch"));
        for (Program program : programs) labels.add(new SpinnerItems(program.uid(), program.name()));

        return labels;
    }

    private Date getNowWithoutTime() {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime( new Date() );
        gc.set( Calendar.HOUR_OF_DAY, 0 );
        gc.set( Calendar.MINUTE, 0 );
        gc.set( Calendar.SECOND, 0 );
        gc.set( Calendar.MILLISECOND, 0 );
        return gc.getTime();
    }

}
