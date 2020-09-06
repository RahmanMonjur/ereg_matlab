package com.example.android.androidskeletonapp.ui.programs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.example.android.androidskeletonapp.ui.tracked_entity_instances.TrackedEntityInstanceAdapter;
import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.arch.helpers.AccessHelper;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection;
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import javax.inject.Singleton;

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
        globalVars = (GlobalClass) getApplicationContext();
        TextView instruction = (TextView)findViewById(R.id.enrollmentInstruction);
        selectedProgram = getIntent().getStringExtra(IntentExtra.PROGRAM.name());
        //String title = (selectedProgram == null) ? "" : Sdk.d2().programModule().programs().byUid().eq(selectedProgram).one().blockingGet().displayName();
        List<TrackedEntityAttributeValue> teiAttrVal = Sdk.d2().trackedEntityModule().trackedEntityInstances().withTrackedEntityAttributeValues().uid(getIntent().getStringExtra(IntentExtra.TEI.name())).blockingGet().trackedEntityAttributeValues();
        getSupportActionBar().setTitle(valueAt(teiAttrVal, "QWTcaK2mXeD") + " - "+ globalVars.getTranslatedWord("Program Stages"));

        ((TextView) findViewById(R.id.programStageNotificator)).setText(globalVars.getTranslatedWord("No program stages found"));

        observeProgramStages(selectedProgram, getIntent().getStringExtra(IntentExtra.TEI.name()));

        listProgram = (Spinner)findViewById(R.id.listProgram);
        listProgram.setAdapter(new ArrayAdapter<SpinnerItems>(this,
                R.layout.spinner_row, getPrograms()));

        listProgram.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!((SpinnerItems)listProgram.getSelectedItem()).getId().equals("")) {
                    TextView textView = (TextView) view;
                    ((TextView) adapterView.getChildAt(0)).setTextSize(20);

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
                                    .setTitle(globalVars.getTranslatedWord("Enrollment Confirmation"))
                                    .setMessage(globalVars.getTranslatedWord("This TEI has no enrollment in this program")+".\n"+
                                            globalVars.getTranslatedWord("Do you want to enroll this TEI now?"))
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .setPositiveButton(globalVars.getTranslatedWord("Yes, I want to enroll"), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            if (EnrollmentFormService.getInstance().init(
                                                    globalVars,
                                                    Sdk.d2(),
                                                    getIntent().getStringExtra(IntentExtra.TEI.name()),
                                                    selectedProgram,null)) {

                                                selectedEnrollment = EnrollmentFormService.getInstance().getEnrollmentUid();
                                                observeProgramStages(selectedProgram, getIntent().getStringExtra(IntentExtra.TEI.name()));
                                            }

                                        }})
                                    .setNegativeButton(globalVars.getTranslatedWord("No"), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            listProgram.setSelection(0);
                                            selectedProgram = "ZBIqxwVixn8";
                                            instruction.setText((selectedProgram.equals("ZBIqxwVixn8") && enrollment == null) ?
                                                    globalVars.getTranslatedWord("Do not forget to enroll in the MCH program") :
                                                    globalVars.getTranslatedWord("You can change the program by clicking below"));
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


        Enrollment enrollment = Sdk.d2().enrollmentModule().enrollments()
                .byTrackedEntityInstance().eq(getIntent().getStringExtra(IntentExtra.TEI.name()))
                .byProgram().eq("WSGAb5XwJ3Y")
                .one().blockingGet();
        instruction.setText((selectedProgram.equals("ZBIqxwVixn8") && enrollment == null) ?
                globalVars.getTranslatedWord("Do not forget to enroll in the MCH program") :
                globalVars.getTranslatedWord("You can change the program by clicking below"));


    }

    private void observeProgramStages(String programUid, String teiUid) {
        Sdk.d2().programModule().programStages()
                .byProgramUid().eq(programUid)
                .orderBySortOrder(RepositoryScope.OrderByDirection.ASC)
                .getPaged(20).observe(this, programStagePagedList -> {
            ProgramStagesAdapter adapter = new ProgramStagesAdapter(this, programUid, teiUid, programStagePagedList);
            adapter.submitList(programStagePagedList);
            recyclerView.setAdapter(adapter);
            findViewById(R.id.programStageNotificator).setVisibility(
                    programStagePagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
        /*
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
        */
    }

    private String valueAt(List<TrackedEntityAttributeValue> values, String attributeUid) {
        for (TrackedEntityAttributeValue attributeValue : values) {
            if (attributeValue.trackedEntityAttribute().equals(attributeUid)) {
                return attributeValue.value();
            }
        }
        return "";
    }

    @Override
    public void onProgramStageSelected(String programUid, String programStageUid, String teiUid) {

        List<Event> events = Sdk.d2().eventModule().events()
                .byProgramUid().eq(programUid)
                .byProgramStageUid().eq(programStageUid)
                .byTrackedEntityInstanceUids(Lists.newArrayList(teiUid))
                .blockingGet();

        ProgramStage programStage = Sdk.d2().programModule().programStages().uid(programStageUid).blockingGet();

        if (events.size() > 0 && programStage.repeatable()) {
            ActivityStarter.startActivity(this,
                    EventsActivity
                            .getIntent(this, programUid, programStageUid, teiUid),
                    false);
        } else if (events.size() > 0 && !programStage.repeatable()) {
            ActivityStarter.startActivity(this,
                    EventFormActivity
                            .getFormActivityIntent(this,
                                    events.get(0).uid(),
                                    programUid, programStageUid, selectedEnrollment,
                                    EventFormActivity.FormType.CHECK),
                    false);
        } else {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
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
