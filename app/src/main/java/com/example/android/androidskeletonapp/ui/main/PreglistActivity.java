package com.example.android.androidskeletonapp.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryCollectionRepository;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.Disposable;

import static android.text.TextUtils.isEmpty;

public class PreglistActivity extends ListActivity {
    GlobalClass globalVars;
    private Disposable disposable;
    public static Intent getPreglistIntent(Context context){
        return new Intent(context, PreglistActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerSetup(R.layout.activity_preglist, R.id.programsToolbar, R.id.programsRecyclerView);
        globalVars = (GlobalClass) getApplicationContext();
        getSupportActionBar().setTitle("Pregnant women list");
        loadPreglist();
    }

    private void loadPreglist() {
        PreglistAdapter adapter = new PreglistAdapter();
        recyclerView.setAdapter(adapter);



        getTeiRepository().getPaged(20).observe(this, teilist -> {
            adapter.submitList(teilist);
            findViewById(R.id.programsNotificator).setVisibility(
                    teilist.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    //

    private TrackedEntityInstanceQueryCollectionRepository getTeiRepository() {
        TrackedEntityInstanceQueryCollectionRepository teiRepository =
                Sdk.d2().trackedEntityModule().trackedEntityInstanceQuery()
                        .byProgram().eq("WSGAb5XwJ3Y")
                        .byOrgUnits().in(Arrays.asList("mSSqswDf1wk"))
                        .byOrgUnitMode().eq(OrganisationUnitMode.DESCENDANTS)
                        .offlineOnly();

        List<TrackedEntityInstance> teilist = teiRepository.blockingGet();
        List<TrackedEntityInstance> finalteilist = new LinkedList<>();
        for (TrackedEntityInstance tei:teilist) {
            String pregident = Sdk.d2().eventModule().events().byProgramStageUid().eq("Ty22Qt2u4QL")
                    .byTrackedEntityInstanceUids(Arrays.asList(tei.uid())).one().blockingGet().uid();
            TrackedEntityDataValueObjectRepository valueRep =
                    Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                            .value(
                                    pregident,
                                    "MKBM582qYs3"
                            );

            String lmp = valueRep.blockingExists() ? valueRep.blockingGet().value() : "No LMP";
            if (lmp != null){
                finalteilist.add(tei);
            }
        }
        return teiRepository;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
