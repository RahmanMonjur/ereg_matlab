package com.example.android.androidskeletonapp.ui.events;

import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.DataSource;
import androidx.paging.PagedListAdapter;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.data.service.DateFormatHelper;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.SimpleListWithSyncHolder;
import com.example.android.androidskeletonapp.ui.data_entry.EventFormActivity;
import com.example.android.androidskeletonapp.ui.main.GlobalClass;
import com.example.android.androidskeletonapp.ui.tracker_import_conflicts.TrackerImportConflictsAdapter;

import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.android.androidskeletonapp.data.service.StyleBinderHelper.setBackgroundColor;
import static com.example.android.androidskeletonapp.data.service.StyleBinderHelper.setState;

public class EventAdapter extends PagedListAdapter<Event, SimpleListWithSyncHolder> {

    private final AppCompatActivity activity;
    private DataSource<?, Event> source;
    GlobalClass globalVars;

    public EventAdapter(AppCompatActivity activity) {
        super(new DiffByIdItemCallback<>());
        this.activity = activity;
    }

    @NonNull
    @Override
    public SimpleListWithSyncHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_simple_with_all_features, parent, false);
        return new SimpleListWithSyncHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleListWithSyncHolder holder, int position) {
        Event event = getItem(position);
        globalVars = (GlobalClass) this.activity.getApplicationContext();
        List<TrackedEntityDataValue> values = new ArrayList<>(event.trackedEntityDataValues());
        holder.title.setText(orgUnit(event.organisationUnit()).displayName());
        //holder.subtitle1.setText(valueAt(values, event.programStage()));
        //holder.subtitle2.setText(optionCombo(event.attributeOptionCombo()).displayName());
        holder.rightText.setText(DateFormatHelper.getDateAsSystemFormat(event.eventDate()));
        holder.icon.setImageResource(R.drawable.ic_programs_black_24dp);
        if (event.state().equals(State.TO_POST)) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(view -> {
                new AlertDialog.Builder(this.activity)
                        .setTitle(globalVars.getTranslatedWord("Delete Confirmation"))
                        .setMessage(globalVars.getTranslatedWord("Do you really want to delete?"))
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton(globalVars.getTranslatedWord("Yes, I want to delete"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    Sdk.d2().eventModule().events().uid(event.uid()).blockingDelete();
                                    invalidateSource();
                                    notifyDataSetChanged();
                                } catch (D2Error d2Error) {
                                    d2Error.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(globalVars.getTranslatedWord("No"), null).show();

            });
        }
        else {
            holder.delete.setVisibility(View.GONE);
        }

        /*
        if(event.state().equals(State.TO_POST) || event.state().equals(State.TO_UPDATE)) {
            holder.syncIcon.setOnClickListener(view -> {
                RotateAnimation rotateAnim = new RotateAnimation(0f, 359f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnim.setDuration(6500);
                rotateAnim.setRepeatMode(Animation.INFINITE);
                holder.syncIcon.startAnimation(rotateAnim);

                Disposable disposable = syncEvent(event.uid())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        data -> {

                                        },
                                        Throwable::printStackTrace,
                                        () -> {
                                            invalidateSource();
                                            Toast.makeText(this.activity,"Synced",Toast.LENGTH_LONG).show();
                                        }
                                );


            });
        }
        else {
            holder.syncIcon.setOnClickListener(null);
        }
        */

        holder.lnkDetail.setOnClickListener(view->{
            ActivityStarter.startActivity(
                    activity,
                    EventFormActivity.getFormActivityIntent(
                            activity,
                            event.uid(),
                            event.program(),
                            event.programStage(),
                            event.enrollment(),
                            EventFormActivity.FormType.CHECK
                    ),false
            );
        });

        setBackgroundColor(R.color.colorAccentDark, holder.icon);
        setState(event.state(), holder.syncIcon);
        setConflicts(event.uid(), holder);
    }

    private Observable<D2Progress> syncEvent(String event) {
        return Sdk.d2().eventModule().events()
                .byUid().eq(event)
                .upload();
    }

    private OrganisationUnit orgUnit(String orgUnitUid) {
        return Sdk.d2().organisationUnitModule().organisationUnits().uid(orgUnitUid).blockingGet();
    }

    private String valueAt(List<TrackedEntityDataValue> values, String stageUid) {
        for (TrackedEntityDataValue dataValue : values) {
            ProgramStageDataElement programStageDataElement = Sdk.d2().programModule().programStageDataElements()
                    .byDataElement().eq(dataValue.dataElement())
                    .byProgramStage().eq(stageUid)
                    .one().blockingGet();
            if (programStageDataElement.displayInReports()) {
                return String.format("%s: %s", programStageDataElement.displayName(), dataValue.value());
            }
        }
        return null;
    }

    private CategoryOptionCombo optionCombo(String attrOptionCombo) {
        return Sdk.d2().categoryModule().categoryOptionCombos().uid(attrOptionCombo).blockingGet();
    }

    private void setConflicts(String trackedEntityInstanceUid, SimpleListWithSyncHolder holder) {
        TrackerImportConflictsAdapter adapter = new TrackerImportConflictsAdapter();
        holder.recyclerView.setAdapter(adapter);
        adapter.setTrackerImportConflicts(Sdk.d2().importModule().trackerImportConflicts()
                .byTrackedEntityInstanceUid().eq(trackedEntityInstanceUid).blockingGet());
    }

    public void setSource(DataSource<?, Event> dataSource) {
        this.source = dataSource;
    }

    public void invalidateSource() {
        try {
            source.invalidate();
        } catch (Exception ex){

        }
    }


}
