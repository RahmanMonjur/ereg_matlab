package com.example.android.androidskeletonapp.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.DateFormatHelper;
import com.example.android.androidskeletonapp.data.service.StyleBinderHelper;
import com.example.android.androidskeletonapp.ui.base.ListItemWithCardHolder;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreglistsAdapter extends RecyclerView.Adapter<ListItemWithCardHolder> {
    private ArrayList<TrackedEntityInstance> teilists;
    private LayoutInflater mInflater;

    PreglistsAdapter(Context context, ArrayList<TrackedEntityInstance> teilists){
        this.mInflater = LayoutInflater.from(context);
        this.teilists = teilists;
    }

    @Override
    public ListItemWithCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_simple, parent, false);
        return new ListItemWithCardHolder(view);
    }

    @Override
    public void onBindViewHolder(ListItemWithCardHolder holder, int position) {
        TrackedEntityInstance tei = teilists.get(position);
        List<TrackedEntityAttributeValue> values = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                .byTrackedEntityInstance().eq(tei.uid()).blockingGet();

        String pregident = Sdk.d2().eventModule().events().byProgramStageUid().eq("Ty22Qt2u4QL")
                .byTrackedEntityInstanceUids(Arrays.asList(tei.uid())).one().blockingGet().uid();

        TrackedEntityDataValueObjectRepository valueRep =
                Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                        .value(
                                pregident,
                                "MKBM582qYs3"
                        );

        String lmp = valueRep.blockingExists() ? valueRep.blockingGet().value() : "No LMP";

        if (values != null) {
            holder.title.setText(valueAt(values, "QWTcaK2mXeD") + " - " + valueAt(values, "etJ8MaVKH2g"));
            holder.subtitle1.setText(
                    valueAt(values, "TolBbVqMWdR") + " - " +
                            valueAt(values, "N8skKU3roph") + " - " +
                            valueAt(values, "KSSF2lnMKca") + " - " + lmp
            );
        } else {
            holder.title.setText(tei.uid());
            holder.subtitle1.setText(DateFormatHelper.formatDate(tei.created()));
        }
        StyleBinderHelper.bindStyle(holder, null);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return teilists.size();
    }

    private String valueAt(List<TrackedEntityAttributeValue> values, String attributeUid) {
        for (TrackedEntityAttributeValue attributeValue : values) {
            if (attributeValue.trackedEntityAttribute().equals(attributeUid)) {
                return attributeValue.value();
            }
        }
        return null;
    }
}
