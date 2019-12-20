package com.example.android.androidskeletonapp.ui.programs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.StyleBinderHelper;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemWithStyleHolder;
import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramStage;

public class ProgramStagesAdapter extends PagedListAdapter<ProgramStage, ListItemWithStyleHolder> {



    private final OnProgramStageSelectionListener programStageSelectionListener;
    private final String programUid;
    private final String trackedEntityInstanceUid;

    ProgramStagesAdapter(OnProgramStageSelectionListener programStageSelectionListener, String programUid, String teiUid) {
        super(new DiffByIdItemCallback<>());
        this.programStageSelectionListener = programStageSelectionListener;
        this.programUid = programUid;
        this.trackedEntityInstanceUid = teiUid;


    }

    @NonNull
    @Override
    public ListItemWithStyleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_with_style, parent, false);
        return new ListItemWithStyleHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemWithStyleHolder holder, int position) {
        ProgramStage programStage = getItem(position);
        holder.title.setText(programStage.displayName());
        Integer programStageInstancesCount = Sdk.d2().eventModule().events()
                .byProgramStageUid().eq(programStage.uid())
                .byTrackedEntityInstanceUids(Lists.newArrayList(trackedEntityInstanceUid))
                .blockingCount();

        if (programStageInstancesCount>0)
            holder.subtitle1.setText(programStageInstancesCount.toString() + " instances");
        else
            holder.subtitle1.setText(programStageInstancesCount.toString() + " instance");

        //holder.subtitle1.setText(program.programType() == ProgramType.WITH_REGISTRATION ?
        //        "Program with registration" : "Program without registration");
        StyleBinderHelper.bindStyle(holder, programStage.style());

        holder.card.setOnClickListener(view -> programStageSelectionListener
                .onProgramStageSelected(programUid, programStage.uid(), trackedEntityInstanceUid));
    }
}
