package com.example.android.androidskeletonapp.ui.data_sets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.StyleBinderHelper;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemWithCardHolder;

import org.hisp.dhis.android.core.dataset.DataSet;

public class DataSetsAdapter extends PagedListAdapter<DataSet, ListItemWithCardHolder> {

    DataSetsAdapter() {
        super(new DiffByIdItemCallback<>());
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
        DataSet dataSet = getItem(position);
        holder.title.setText(dataSet.displayName());
        holder.subtitle1.setText(dataSet.periodType().name());
        StyleBinderHelper.bindStyle(holder, dataSet.style());
    }
}
