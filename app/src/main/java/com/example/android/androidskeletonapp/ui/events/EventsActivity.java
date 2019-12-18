package com.example.android.androidskeletonapp.ui.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.base.ListActivity;
import com.example.android.androidskeletonapp.ui.event_form.EventFormActivity;
import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventCollectionRepository;
import org.hisp.dhis.android.core.event.EventCreateProjection;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.text.TextUtils.isEmpty;

public class EventsActivity extends ListActivity {
    private String selectedProgram;
    private String selectedProgramStage;
    private String selectedTei;
    private CompositeDisposable compositeDisposable;
    private EventAdapter adapter;
    private final int EVENT_RQ = 1210;

    private enum IntentExtra {
        PROGRAM,
        PROGRAM_STAGE,
        TEI
    }

    public static Intent getIntent(Context context, String programUid, String programStageUid, String trackedEntityInstanceUid) {
        Bundle bundle = new Bundle();
        if (!isEmpty(programUid))
            bundle.putString(IntentExtra.PROGRAM.name(), programUid);
        if (!isEmpty(programStageUid))
            bundle.putString(IntentExtra.PROGRAM_STAGE.name(), programStageUid);
        if (!isEmpty(trackedEntityInstanceUid))
            bundle.putString(IntentExtra.TEI.name(), trackedEntityInstanceUid);

        Intent intent = new Intent(context, EventsActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_events, R.id.eventsToolbar, R.id.eventsRecyclerView);
        selectedProgram = getIntent().getStringExtra(IntentExtra.PROGRAM.name());
        selectedProgramStage = getIntent().getStringExtra(IntentExtra.PROGRAM_STAGE.name());
        selectedTei = getIntent().getStringExtra(IntentExtra.TEI.name());
        compositeDisposable = new CompositeDisposable();
        observeEvents();

        if (isEmpty(selectedProgram))
            findViewById(R.id.eventButton).setVisibility(View.GONE);

        findViewById(R.id.eventButton).setOnClickListener(view ->
                {
                    compositeDisposable.add(
                            Sdk.d2().programModule().programs().uid(selectedProgram).get()
                                    .map(program -> {
                                        String orgUnit = Sdk.d2().organisationUnitModule().organisationUnits()
                                                .byProgramUids(Collections.singletonList(selectedProgram))
                                                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                                                .one().blockingGet().uid();
                                        String stage = Sdk.d2().programModule().programStages()
                                                .byProgramUid().eq(program.uid())
                                                .one().blockingGet().uid();
                                        String attrOptionCombo = program.categoryCombo() != null ?
                                                Sdk.d2().categoryModule().categoryOptionCombos()
                                                        .byCategoryComboUid().eq(program.categoryComboUid())
                                                        .one().blockingGet().uid() : null;

                                        return Sdk.d2().eventModule().events()
                                                .byProgramUid().eq(getIntent().getStringExtra(IntentExtra.PROGRAM.name()))
                                                .byProgramStageUid().eq(getIntent().getStringExtra(IntentExtra.PROGRAM_STAGE.name()))
                                                .blockingAdd(
                                                        EventCreateProjection.builder()
                                                                .organisationUnit(orgUnit)
                                                                .program(program.uid())
                                                                .programStage(stage)
                                                                .attributeOptionCombo(attrOptionCombo)
                                                                .build());

                                    })
                                    .filter(eventUid -> !eventUid.isEmpty())
                                    .map(eventUid ->
                                            EventFormActivity.getFormActivityIntent(EventsActivity.this,
                                                    eventUid,
                                                    selectedProgram,
                                                    selectedProgramStage,
                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                            .one().blockingGet().uid(), EventFormActivity.FormType.CREATE))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            activityIntent ->
                                                    ActivityStarter.startActivityForResult(
                                                            EventsActivity.this, activityIntent, EVENT_RQ),
                                            Throwable::printStackTrace
                                    ));
                }
        );

    }

    private void observeEvents() {
        adapter = new EventAdapter(this);
        recyclerView.setAdapter(adapter);

        getEventRepository().getPaged(20).observe(this, eventsPagedList -> {
            adapter.setSource(eventsPagedList.getDataSource());
            adapter.submitList(eventsPagedList);
            findViewById(R.id.eventsNotificator).setVisibility(
                    eventsPagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private EventCollectionRepository getEventRepository() {
        EventCollectionRepository eventRepository =
                Sdk.d2().eventModule().events().withTrackedEntityDataValues();
        if (!isEmpty(selectedProgram)) {
            return eventRepository.byProgramUid().eq(selectedProgram)
                    .byProgramStageUid().eq(selectedProgramStage).byTrackedEntityInstanceUids(Lists.newArrayList(selectedTei));
        } else {
            return eventRepository;
        }
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
        if (requestCode == EVENT_RQ && resultCode == RESULT_OK) {
            adapter.invalidateSource();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
