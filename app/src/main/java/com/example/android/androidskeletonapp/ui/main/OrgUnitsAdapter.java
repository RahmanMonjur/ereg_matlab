package com.example.android.androidskeletonapp.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.StyleBinderHelper;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemWithCardHolder;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

public class OrgUnitsAdapter extends PagedListAdapter<OrganisationUnit, ListItemWithCardHolder> {

    private final OnOrgUnitSelectionListener orgUnitListener;

    OrgUnitsAdapter(OnOrgUnitSelectionListener listener){
        super(new DiffByIdItemCallback<>());
        this.orgUnitListener = listener;
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
        OrganisationUnit orgunit = getItem(position);
        holder.title.setText(orgunit.displayName());
        holder.subtitle1.setText("");
        StyleBinderHelper.bindStyle(holder, null);

        holder.cardSimple.setOnClickListener(view -> orgUnitListener.onOrgUnitSelected(orgunit));
    }
}
