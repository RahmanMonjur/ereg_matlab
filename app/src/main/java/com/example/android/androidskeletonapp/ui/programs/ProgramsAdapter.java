package com.example.android.androidskeletonapp.ui.programs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.StyleBinderHelper;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemWithCardHolder;

import org.hisp.dhis.android.core.program.Program;

public class ProgramsAdapter extends PagedListAdapter<Program, ListItemWithCardHolder> {

    private final OnProgramSelectionListener programSelectionListener;

    ProgramsAdapter(OnProgramSelectionListener programSelectionListener) {
        super(new DiffByIdItemCallback<>());
        this.programSelectionListener = programSelectionListener;
    }

    @NonNull
    @Override
    public ListItemWithCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_simple, parent, false);
        return new ListItemWithCardHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemWithCardHolder holder, int position) {
        Program program = getItem(position);
        holder.title.setText(program.displayName());
        holder.subtitle1.setText(program.description());

        //holder.subtitle1.setText(program.programType() == ProgramType.WITH_REGISTRATION ?
        //        "Program with registration" : "Program without registration");
        StyleBinderHelper.bindStyle(holder, program.style());

        holder.cardSimple.setOnClickListener(view -> programSelectionListener
                .onProgramSelected(program.uid(), program.programType()));
    }
}
