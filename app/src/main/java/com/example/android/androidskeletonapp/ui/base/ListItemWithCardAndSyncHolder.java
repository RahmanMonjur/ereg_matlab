package com.example.android.androidskeletonapp.ui.base;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.androidskeletonapp.R;

public class ListItemWithCardAndSyncHolder extends BaseListItemHolder {

    public final CardView cardSyncDelete;
    public final CardView cardImageView;
    public  final FrameLayout cardFrameSyncDelete;
    public final RelativeLayout lnkDetail;
    public final ImageView syncIcon;
    public final RecyclerView recyclerView;
    public final TextView rightText;
    public final TextView subtitle2;

    public ListItemWithCardAndSyncHolder(@NonNull View view) {
        super(view);
        cardSyncDelete = view.findViewById(R.id.cardSyncDelete);
        cardImageView = view.findViewById(R.id.itemBitmapCardView);
        lnkDetail = view.findViewById(R.id.lnkDetail);
        cardFrameSyncDelete = view.findViewById(R.id.cardFrameSyncDelete);
        syncIcon = view.findViewById(R.id.syncIcon);
        recyclerView = view.findViewById(R.id.importConflictsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rightText = view.findViewById(R.id.rightText);
        subtitle2 = view.findViewById(R.id.itemSubtitle2);
    }
}