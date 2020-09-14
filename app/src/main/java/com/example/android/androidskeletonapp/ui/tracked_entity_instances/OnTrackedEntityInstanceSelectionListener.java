package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

public interface OnTrackedEntityInstanceSelectionListener {
    void onTrackedEntityInstanceSelected(String programUid, String teiUid);
}
