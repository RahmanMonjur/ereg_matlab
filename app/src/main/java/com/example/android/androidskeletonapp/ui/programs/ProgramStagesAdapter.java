package com.example.android.androidskeletonapp.ui.programs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.StyleBinderHelper;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemWithCardHolder;
import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.program.ProgramStage;

import java.util.ArrayList;
import java.util.List;

public class ProgramStagesAdapter extends PagedListAdapter<ProgramStage, ListItemWithCardHolder> {

    private final OnProgramStageSelectionListener programStageSelectionListener;
    private final String programUid;
    private final String trackedEntityInstanceUid;
    private final PagedList<ProgramStage> programStages;
    private ArrayList<String> pstagelist;

    ProgramStagesAdapter(OnProgramStageSelectionListener programStageSelectionListener, String programUid, String teiUid, PagedList<ProgramStage> pstages) {
        //super(new DiffByIdItemCallback<>());
        super(DIFF_CALLBACK);
        this.programStageSelectionListener = programStageSelectionListener;
        this.programUid = programUid;
        this.trackedEntityInstanceUid = teiUid;
        this.programStages = pstages;
        pstagelist = new ArrayList<String>();
    }

    public static final DiffUtil.ItemCallback<ProgramStage> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ProgramStage>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull ProgramStage oldInstance, @NonNull ProgramStage newInstance) {
                    return oldInstance == newInstance;
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull ProgramStage oldInstance, @NonNull ProgramStage newInstance) {
                    return oldInstance.id().equals(newInstance.id());

                }
            };

    @NonNull
    @Override
    public ListItemWithCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_simple_without_icon, parent, false);
        return new ListItemWithCardHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return programStages.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemWithCardHolder holder, int position) {

        ProgramStage programStage = getItem(position);
        if (pstagelist.indexOf(programStage.uid()) < 0) {
            pstagelist.add(programStage.uid());
            holder.title.setText(programStage.displayName());
            holder.title.setTextSize(20);
            Integer programStageInstancesCount = Sdk.d2().eventModule().events()
                    .byProgramStageUid().eq(programStage.uid())
                    .byTrackedEntityInstanceUids(Lists.newArrayList(trackedEntityInstanceUid))
                    .blockingCount();

            if (programStageInstancesCount > 1)
                holder.subtitle1.setText(programStageInstancesCount.toString() + " instances");
            else
                holder.subtitle1.setText(programStageInstancesCount.toString() + " instance");
            holder.subtitle1.setTextSize(16);
            //StyleBinderHelper.bindStyle(holder, programStage.style());

            holder.cardSimple.setOnClickListener(view -> programStageSelectionListener
                    .onProgramStageSelected(programUid, programStage.uid(), trackedEntityInstanceUid));
        } else {
            holder.itemView.setVisibility(View.INVISIBLE);
            holder.itemView.invalidate();
        }


    }


}
