package com.example.android.androidskeletonapp.ui.data_entry.field_type_holder;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.forms.EnrollmentFormService;
import com.example.android.androidskeletonapp.data.service.forms.FormField;
import com.example.android.androidskeletonapp.ui.main.GlobalClass;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UsernameFieldHolder extends FieldHolder {

    private final Spinner spinner;
    private Map<String,String> optionList;
    private String fieldUid;
    private String fieldCurrentValue;
    GlobalClass globalVars;

    UsernameFieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView, valueSavedListener);
        this.spinner = itemView.findViewById(R.id.spinner);
    }

    void bind(FormField fieldItem) {
        super.bind(fieldItem);
        spinner.setOnItemSelectedListener(null);

        globalVars = (GlobalClass) itemView.getContext().getApplicationContext();
        fieldUid = fieldItem.getUid();
        fieldCurrentValue = fieldItem.getValue();

        setUpSpinner(fieldItem.getOptionSetUid());

        if (fieldCurrentValue != null)
            setInitialValue(fieldCurrentValue);
    }

    private void setUpSpinner(String optionSetUid) {
        optionList = new HashMap<>();
        List<String> optionListNames = new ArrayList<>();
        optionListNames.add(globalVars.getTranslatedWord("Please select from list"));

        FileInputStream fis;
        try {
            if (EnrollmentFormService.getInstance() != null) {
                TrackedEntityAttributeValueObjectRepository valueRepository =
                        Sdk.d2().trackedEntityModule()
                                .trackedEntityAttributeValues()
                                .value("OhmSPuuHj53",
                                        Sdk.d2().enrollmentModule().enrollments().uid(
                                                EnrollmentFormService.getInstance().getEnrollmentUid()).blockingGet().trackedEntityInstance());
                if (valueRepository.blockingExists()) {
                    fis = itemView.getContext().openFileInput("fwalist.txt");
                    InputStreamReader inputStreamReader = new InputStreamReader(fis);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    while ((receiveString = bufferedReader.readLine()) != null) {
                        if (receiveString != "") {
                            String[] userData = receiveString.split("~");

                            if (userData.length > 2) {
                                String[] orgs = userData[2].split("/");
                                if (orgs.length > 5) {
                                    if (valueRepository.blockingGet().value().equals(orgs[5])) {
                                        optionList.put(userData[0], userData[1]);
                                    }
                                }
                            }
                            /*
                            if (userData.length > 0) {
                                optionList.put(userData[0], userData[1]);
                            }
                            */
                        }
                    }
                }
            }

            /*
            if (optionList.size()<1){
                return;
            }
            */
            for(String key: optionList.keySet()){
                optionListNames.add(optionList.get(key));
            }

            spinner.setAdapter(new ArrayAdapter<>(itemView.getContext(),
                    R.layout.spinner_row, optionListNames));
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i > 0) {
                        if (fieldCurrentValue == null || !Objects.equals(fieldCurrentValue, getKey(optionList, spinner.getSelectedItem().toString())))
                            valueSavedListener.onValueSaved(fieldUid, getKey(optionList, spinner.getSelectedItem().toString()));
                    } else if (fieldCurrentValue != null)
                        valueSavedListener.onValueSaved(fieldUid, null);

                    TextView textView = (TextView) view;
                    ((TextView) adapterView.getChildAt(0)).setTextSize(20);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
        //optionList = Sdk.d2().organisationUnitModule().organisationUnits().byLevel().eq(5).blockingGet();

        //optionListNames.add(label.getText().toString());
        //for (OrganisationUnit option : optionList) optionListNames.add(option.displayName());


    }

    private void setInitialValue(String selectedCode) {
        String value = optionList.get(selectedCode);
        ArrayAdapter<String> spinnerAdap = (ArrayAdapter<String>) spinner.getAdapter();
        int spinnerPosition = spinnerAdap.getPosition(value);
        if (spinnerPosition > -1 )
            spinner.setSelection(spinnerPosition);
    }

    private <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

}
