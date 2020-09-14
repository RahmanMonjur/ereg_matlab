package com.example.android.androidskeletonapp.ui.base;

import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.android.androidskeletonapp.R;

public class ListItemWithCardHolder extends BaseListItemHolder {

    public final CardView cardSimple;
    public final FrameLayout cardFrameSimple;


    public ListItemWithCardHolder(@NonNull View view) {
        super(view);
        cardSimple = view.findViewById(R.id.cardSimple);
        cardFrameSimple = view.findViewById(R.id.cardFrameSimple);
    }
}