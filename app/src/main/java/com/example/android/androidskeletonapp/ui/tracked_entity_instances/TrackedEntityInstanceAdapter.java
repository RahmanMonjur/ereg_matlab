package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.example.android.androidskeletonapp.ui.base.ListItemWithCardAndSyncHolder;
import com.example.android.androidskeletonapp.ui.data_entry.EnrollmentFormActivity;
import com.example.android.androidskeletonapp.ui.main.GlobalClass;
import com.example.android.androidskeletonapp.ui.tracker_import_conflicts.TrackerImportConflictsAdapter;

import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.net.InetAddress;
import java.text.MessageFormat;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.android.androidskeletonapp.data.service.AttributeHelper.teiSubtitle2First;
import static com.example.android.androidskeletonapp.data.service.AttributeHelper.teiSubtitle2Second;
import static com.example.android.androidskeletonapp.data.service.ImageHelper.getBitmap;
import static com.example.android.androidskeletonapp.data.service.StyleBinderHelper.setBackgroundColor;
import static com.example.android.androidskeletonapp.data.service.StyleBinderHelper.setState;

public class TrackedEntityInstanceAdapter extends PagedListAdapter<TrackedEntityInstance, ListItemWithCardAndSyncHolder> {

    private final AppCompatActivity activity;
    private DataSource<?, TrackedEntityInstance> source;
    private OnTrackedEntityInstanceSelectionListener listener;
    private String programUid;
    GlobalClass globalVars;

    public TrackedEntityInstanceAdapter(AppCompatActivity activity, OnTrackedEntityInstanceSelectionListener listener, String programUid) {
        super(new DiffByIdItemCallback<>());
        this.activity = activity;
        this.listener = listener;
        this.programUid = programUid;
    }

    @NonNull
    @Override
    public ListItemWithCardAndSyncHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_with_all_features, parent, false);
        return new ListItemWithCardAndSyncHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemWithCardAndSyncHolder holder, int position) {
        TrackedEntityInstance trackedEntityInstance = getItem(position);
        globalVars = (GlobalClass) this.activity.getApplicationContext();

        List<TrackedEntityAttributeValue> values = trackedEntityInstance.trackedEntityAttributeValues();
        holder.title.setText(valueAt(values, "QWTcaK2mXeD") + " - " + valueAt(values, "etJ8MaVKH2g"));
        holder.subtitle1.setText(valueAt(values, "N8skKU3roph") + " - " + valueAt(values, "KSSF2lnMKca"));
        holder.subtitle2.setText(valueAt(values, "I056EjniPUi") + " - " + valueAt(values, "aQEvaiBpohU"));
        //holder.title.setText(valueAt(values, teiTitle(trackedEntityInstance)));
        //holder.subtitle1.setText(valueAt(values, teiSubtitle1(trackedEntityInstance)));
        //holder.subtitle2.setText(setSubtitle2(values, trackedEntityInstance));
        holder.rightText.setText(DateFormatHelper.getDateAsSystemFormat(trackedEntityInstance.created()));
        setImage(trackedEntityInstance, holder);
        if (trackedEntityInstance.state().equals(State.TO_POST)) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(view -> {
                new AlertDialog.Builder(this.activity)
                        .setTitle(globalVars.getTranslatedWord("Delete Confirmation"))
                        .setMessage(globalVars.getTranslatedWord("Do you really want to delete?"))
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton(globalVars.getTranslatedWord("Yes, I want to delete"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    Sdk.d2().trackedEntityModule().trackedEntityInstances().uid(trackedEntityInstance.uid()).blockingDelete();
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
        if (trackedEntityInstance.state() == State.TO_POST ||
                trackedEntityInstance.state() == State.TO_UPDATE) {
            holder.sync.setVisibility(View.VISIBLE);
            holder.sync.setOnClickListener(v -> {
                holder.sync.setVisibility(View.GONE);
                RotateAnimation rotateAnim = new RotateAnimation(0f, 359f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnim.setDuration(2500);
                rotateAnim.setRepeatMode(Animation.INFINITE);
                holder.syncIcon.startAnimation(rotateAnim);

                Disposable disposable = syncTei(trackedEntityInstance.uid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                data -> {
                                },
                                Throwable::printStackTrace,
                                () -> {
                                    holder.syncIcon.clearAnimation();
                                    invalidateSource();
                                }
                        );
            });
        } else {
            holder.sync.setVisibility(View.GONE);
            holder.sync.setOnClickListener(null);
        }
        */

        // Assigning Sync button's functionalities to Sync Icon, and will ignore Sync button
        holder.sync.setVisibility(View.GONE);
        if (trackedEntityInstance.state().equals(State.TO_POST) ||
                trackedEntityInstance.state().equals(State.TO_UPDATE)) {
            holder.syncIcon.setOnClickListener(v -> {
                if(isNetworkConnected()){
                RotateAnimation rotateAnim = new RotateAnimation(0f, 359f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnim.setDuration(2500);
                rotateAnim.setRepeatMode(Animation.INFINITE);
                holder.syncIcon.startAnimation(rotateAnim);

                Disposable disposable = syncTei(trackedEntityInstance.uid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                data -> {
                                },
                                Throwable::printStackTrace,
                                () -> {
                                    holder.syncIcon.clearAnimation();
                                    invalidateSource();
                                    Toast.makeText(this.activity,"Synced",Toast.LENGTH_LONG).show();
                                }
                        );
                } else {
                    Toast.makeText(this.activity, globalVars.getTranslatedWord("You do not have stable internet connection now.\nplease try later."), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            holder.syncIcon.setOnClickListener(null);
        }

        holder.cardImageView.setOnClickListener(v -> {
            ActivityStarter.startActivity( this.activity,
                    EnrollmentFormActivity.getFormActivityIntent(
                            this.activity.getApplicationContext(),
                            trackedEntityInstance.uid(),
                            programUid), false);
        });

        setBackgroundColor(R.color.colorAccentDark, holder.icon);
        setState(trackedEntityInstance.state(), holder.syncIcon);
        setConflicts(trackedEntityInstance.uid(), holder);

        holder.lnkDetail.setOnClickListener(view -> listener
                .onTrackedEntityInstanceSelected(programUid, trackedEntityInstance.uid()));

    }

    private Observable<D2Progress> syncTei(String teiUid) {
        return Sdk.d2().trackedEntityModule().trackedEntityInstances()
                .byUid().eq(teiUid)
                .upload();
    }

    private String valueAt(List<TrackedEntityAttributeValue> values, String attributeUid) {
        for (TrackedEntityAttributeValue attributeValue : values) {
            if (attributeValue.trackedEntityAttribute().equals(attributeUid)) {
                return attributeValue.value();
            }
        }
        return null;
    }

    private String setSubtitle2(List<TrackedEntityAttributeValue> values, TrackedEntityInstance trackedEntityInstance) {
        String firstSubtitle = valueAt(values, teiSubtitle2First(trackedEntityInstance));
        String secondSubtitle = valueAt(values, teiSubtitle2Second(trackedEntityInstance));
        if (firstSubtitle != null) {
            if (secondSubtitle != null) {
                return MessageFormat.format("{0} - {1}", firstSubtitle, secondSubtitle);
            } else {
                return firstSubtitle;
            }
        } else {
            if (secondSubtitle != null) {
                return secondSubtitle;
            } else {
                return null;
            }
        }
    }

    private void setConflicts(String trackedEntityInstanceUid, ListItemWithCardAndSyncHolder holder) {
        TrackerImportConflictsAdapter adapter = new TrackerImportConflictsAdapter();
        holder.recyclerView.setAdapter(adapter);
        adapter.setTrackerImportConflicts(Sdk.d2().importModule().trackerImportConflicts()
                .byTrackedEntityInstanceUid().eq(trackedEntityInstanceUid).blockingGet());
    }

    private void setImage(TrackedEntityInstance trackedEntityInstance, ListItemWithCardAndSyncHolder holder) {
        Bitmap teiImage = getBitmap(trackedEntityInstance);
        if (teiImage != null) {
            holder.icon.setVisibility(View.INVISIBLE);
            holder.bitmap.setImageBitmap(teiImage);
            holder.bitmap.setVisibility(View.VISIBLE);
        } else {
            holder.bitmap.setVisibility(View.GONE);
            holder.icon.setImageResource(R.drawable.ic_person_black_24dp);
            holder.icon.setVisibility(View.VISIBLE);
        }
    }

    public void setSource(DataSource<?, TrackedEntityInstance> dataSource) {
        this.source = dataSource;
    }

    public void invalidateSource() {
        try {
            source.invalidate();
        } catch (Exception ex){

        }
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
