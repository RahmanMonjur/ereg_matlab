package com.example.android.androidskeletonapp.ui.base;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.androidskeletonapp.R;

public class ListItemWithSyncAndCardHolder extends ListItemWithSyncHolder {

    public final CardView card;

    public ListItemWithSyncAndCardHolder(@NonNull View view) {
        super(view);
        card = view.findViewById(R.id.ListForCardAndSync);
    }
}