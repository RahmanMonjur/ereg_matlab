package com.example.android.androidskeletonapp.ui.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.androidskeletonapp.R;

public class SimpleListWithSyncHolder extends BaseListItemHolder {

    public final ImageView syncIcon;
    public final RecyclerView recyclerView;
    public final TextView rightText;
    public final RelativeLayout lnkDetail;

    public SimpleListWithSyncHolder(@NonNull View view) {
        super(view);
        syncIcon = view.findViewById(R.id.syncIcon);
        recyclerView = view.findViewById(R.id.importConflictsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rightText = view.findViewById(R.id.rightText);
        lnkDetail = view.findViewById(R.id.lnkDetail);
    }
}