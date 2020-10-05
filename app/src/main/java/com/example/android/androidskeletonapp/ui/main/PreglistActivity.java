package com.example.android.androidskeletonapp.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.data.service.DateFormatHelper;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryCollectionRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

    private final List<String> haorgs = new ArrayList<String>() {
        {
            add("SyCdLP39qjJ");add("caH7L0l7Z0b");add("JL0d4VAxXfN");add("sMPspqB5a8X");add("U8ibH3fc5bj");
            add("DMG1rVFsLAt");add("rPsSYdeIis7");add("DzXSYzaTI4W");add("PrOeeFvp8Ve");add("TUEBbPr3U3S");
            add("iVyeghzjChU");add("TvwQvDrBG4R");add("kGgw9RkedbH");add("HmTNHCiMWMg");
            add("Ccq4NSvxhfj");add("Q2TEkh11av2");add("tPduDMQY7wH");add("QcSlTNu9GVB");
            add("R08BsvW4S1M");add("dZYtu4utzua");add("mSSqswDf1wk");add("qDrXZW6azhr");add("htO0gCYv8vF");add("NwxIfJC0ump");
            add("oKhPrOJi5KT");add("eXul9CiqvZa");add("weSM9nY2YYm");add("VhfOPUN1Wf5");
            add("jZevt6rnu3j");add("wnIa5VooKJX");add("ees4fvVdf86");add("b9WsbKjK8R2");add("WB1DwszpQoY");
            add("a873uq6vXsC");add("jRozLs8kQVA");add("K9hBqEhngFA");add("b80mW8xUiL1");add("ngWxtQhz5s7");
            add("LmicdGyUkzN");add("ZD6h0xOBoDn");add("nQZoHQpycKU");add("tAxkGgAp9c5");add("eS5U919BiBm");add("f2CoaqbUeYs");
            add("OVeqRRrXzPb");add("mWZafBP0hm6");add("aM1FCVtUzZZ");add("F3hxreKANf7");add("JwB0L8U6naj");
            add("JjY7st1ME7q");add("RfnMChqaC1f");add("Y20pVqNpSuk");add("wYvFif3I5gG");add("wWpQ22xrcO2");
            add("MHP71PBRlQn");add("GnLIf32laHv");add("v6FpuiPfFwm");add("xmj51GbryyY");add("kxUrTVzkL1m");
            add("XcpM3I5meLn");add("Q8wXoD3jxVV");add("qCjosByybXu");add("Bh77ly9Eox7");add("");add("kzad4f7OEAd");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerSetup(R.layout.activity_preglist, R.id.programsToolbar, R.id.programsRecyclerView);
        globalVars = (GlobalClass) getApplicationContext();
        getSupportActionBar().setTitle(globalVars.getTranslatedWord("Pregnant women list"));
        if (Sdk.d2().userModule().userRoles().byUid().eq("OGnAUcsRIIb").blockingCount() > 0) {
            loadPreglist();
        } else {
            ((TextView)findViewById(R.id.programsNotificator)).setText(globalVars.getTranslatedWord("Sorry, you're allowed to see this page"));
        }

    }

    private void loadPreglist() {

        ArrayList<TrackedEntityInstance> teis = getTeis(getTeiRepository());
        PreglistsAdapter adapter = new PreglistsAdapter(this,teis);
        recyclerView.setAdapter(adapter);

        /*
        ArrayList<String> teis = getTeiUids(getTeiRepository());
        PreglistAdapter adapter = new PreglistAdapter(teis);
        recyclerView.setAdapter(adapter);

        getTeiRepository().getPaged(20).observe(this, teilist -> {
            adapter.submitList(teilist);
            findViewById(R.id.programsNotificator).setVisibility(
                    teilist.isEmpty() ? View.VISIBLE : View.GONE);
        });
        */
    }

    private TrackedEntityInstanceQueryCollectionRepository getTeiRepository() {
        TrackedEntityInstanceQueryCollectionRepository teiRepository =
                Sdk.d2().trackedEntityModule().trackedEntityInstanceQuery()
                        .byProgram().eq("WSGAb5XwJ3Y")
                        .byOrgUnits().in(haorgs)
                        .byOrgUnitMode().eq(OrganisationUnitMode.DESCENDANTS)
                        .offlineOnly();
        return teiRepository;
    }

    private ArrayList<String> getTeiUids (TrackedEntityInstanceQueryCollectionRepository teiRepository) {
        List<TrackedEntityInstance> teilist = teiRepository.blockingGet();
        ArrayList<String> finalteilist = new ArrayList<String>();
        for (TrackedEntityInstance tei : teilist) {
            String pregident = Sdk.d2().eventModule().events().byProgramStageUid().eq("Ty22Qt2u4QL")
                    .byTrackedEntityInstanceUids(Arrays.asList(tei.uid())).one().blockingGet().uid();
            TrackedEntityDataValueObjectRepository valueRep =
                    Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                            .value(
                                    pregident,
                                    "MKBM582qYs3"
                            );

            String lmp = valueRep.blockingExists() ? valueRep.blockingGet().value() : "No LMP";
            try {
                Date datetoday = Calendar.getInstance().getTime();
                long difference = datetoday.getTime() - DateFormatHelper.parseDateAutoFormat(lmp).getTime();
                long days = difference / (24 * 60 * 60 * 1000);

                if (days > 223 && days < 450) {
                    finalteilist.add(tei.uid());
                }
            } catch (Exception ex) {

            }
        }
        return finalteilist;
    }

    private ArrayList<TrackedEntityInstance> getTeis (TrackedEntityInstanceQueryCollectionRepository teiRepository) {
        List<TrackedEntityInstance> teilist = teiRepository.blockingGet();
        ArrayList<TrackedEntityInstance> finalteilist = new ArrayList<TrackedEntityInstance>();
        for (TrackedEntityInstance tei : teilist) {
            Event event = Sdk.d2().eventModule().events().byProgramStageUid().eq("Ty22Qt2u4QL")
                    .byTrackedEntityInstanceUids(Arrays.asList(tei.uid())).one().blockingGet();
            TrackedEntityDataValueObjectRepository valueRep =
                    Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                            .value(
                                    (event != null) ? event.uid() : "",
                                    "MKBM582qYs3"
                            );

            String lmp = valueRep.blockingExists() ? valueRep.blockingGet().value() : "";
            try {
                Date datetoday = Calendar.getInstance().getTime();
                long difference = datetoday.getTime() - DateFormatHelper.parseDateAutoFormat(lmp).getTime();
                long days = difference / (24 * 60 * 60 * 1000);

                if (days > 223 && days < 350) {
                    finalteilist.add(tei);
                }
            } catch (Exception ex) {

            }
        }
        return finalteilist;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
