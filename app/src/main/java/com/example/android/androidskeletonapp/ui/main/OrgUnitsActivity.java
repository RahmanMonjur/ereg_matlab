package com.example.android.androidskeletonapp.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.ui.base.ListActivity;
import com.example.android.androidskeletonapp.ui.programs.ProgramsActivity;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class OrgUnitsActivity extends ListActivity implements OnOrgUnitSelectionListener {
    GlobalClass globalVars;
    private Disposable disposable;
    public static Intent getOrgUnitIntent(Context context){
        return new Intent(context, OrgUnitsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerSetup(R.layout.activity_orgunits, R.id.programsToolbar, R.id.programsRecyclerView);
        globalVars = (GlobalClass) getApplicationContext();

        OrganisationUnit orgunit = Sdk.d2().organisationUnitModule().organisationUnits()
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .one().blockingGet();
        if (orgunit != null) {
            String locale = "en";
            String regex = "^[0-9a-zA-Z \\/_?:.,\\s-]+$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(orgunit.displayName());
            if (!matcher.matches())
                locale = "bn";
            globalVars.setUserLocale(locale);
        }
        this.setTitle(globalVars.getTranslatedWord("Home"));

        getSupportActionBar().setTitle(globalVars.getTranslatedWord("Please choose an organisation unit"));

        globalVars.setUserDateFormat(Sdk.d2().systemInfoModule().systemInfo().blockingGet().dateFormat());
        observeOrgUnits();
    }

    private void observeOrgUnits() {
        OrgUnitsAdapter adapter = new OrgUnitsAdapter(this);
        recyclerView.setAdapter(adapter);

        Sdk.d2().organisationUnitModule().organisationUnits()
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .getPaged(20).observe(this, orgsPagedList -> {
            adapter.submitList(orgsPagedList);
            findViewById(R.id.programsNotificator).setVisibility(
                    orgsPagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
        /*
        disposable = Sdk.d2().organisationUnitModule().organisationUnits().get()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(organisationUnitUids -> Sdk.d2().organisationUnitModule().organisationUnits()
                        .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                        .getPaged(20))
                .subscribe(orgs -> orgs.observe(this, orgsPagedList -> {
                    adapter.submitList(orgsPagedList);
                    findViewById(R.id.programsNotificator).setVisibility(
                            orgsPagedList.isEmpty() ? View.VISIBLE : View.GONE);
                }));
         */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    public void onOrgUnitSelected (OrganisationUnit orgUid) {
        globalVars.setOrgUid(orgUid);
        String orgname = orgUid.displayName();
        Toast.makeText(getApplicationContext(), globalVars.getTranslatedWord("You have selected") + ", "+ orgname, Toast.LENGTH_LONG).show();
        /*
        if (Sdk.d2().programModule().programs().blockingCount() > 0) {
            ActivityStarter.startActivity(this, ProgramsActivity.getProgramActivityIntent(this), true);
        } else {
            ActivityStarter.startActivity(this, MainActivity.getMainActivityIntent(this), true);
        }
        */
        ActivityStarter.startActivity(this, MainActivity.getMainActivityIntent(this), true);
    }


}
