package com.example.android.androidskeletonapp.ui.data_entry.field_type_holder;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.forms.FormField;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

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
    private HashMap<String,String> optionList;
    private String fieldUid;
    private String fieldCurrentValue;

    UsernameFieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView, valueSavedListener);
        this.spinner = itemView.findViewById(R.id.spinner);
    }

    void bind(FormField fieldItem) {
        super.bind(fieldItem);
        fieldUid = fieldItem.getUid();
        fieldCurrentValue = fieldItem.getValue();

        setUpSpinner(fieldItem.getOptionSetUid());

        if (fieldCurrentValue != null)
            setInitialValue(fieldCurrentValue);
    }

    private void setUpSpinner(String optionSetUid) {
        optionList = new HashMap<>();
        List<String> optionListNames = new ArrayList<>();
        optionListNames.add("Please select from list");

        FileInputStream fis;
        try {
            fis = itemView.getContext().openFileInput("fwalist.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            while ( (receiveString = bufferedReader.readLine()) != null ) {
                if (receiveString != ""){
                    String[] separated = receiveString.split("~");
                    optionList.put(separated[0],separated[1]);
                }
            }
            if (optionList.size()<1){
                return;
            }
            for(String key: optionList.keySet()){
                optionListNames.add(optionList.get(key));
            }

        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }



        //optionList = Sdk.d2().organisationUnitModule().organisationUnits().byLevel().eq(5).blockingGet();



        //optionListNames.add(label.getText().toString());
        //for (OrganisationUnit option : optionList) optionListNames.add(option.displayName());

        spinner.setAdapter(new ArrayAdapter<>(itemView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, optionListNames));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    if (fieldCurrentValue == null || !Objects.equals(fieldCurrentValue, getKey(optionList, optionList.get(i - 1))))
                        valueSavedListener.onValueSaved(fieldUid, getKey(optionList, optionList.get(i - 1)));
                } else if (fieldCurrentValue != null)
                    valueSavedListener.onValueSaved(fieldUid, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setInitialValue(String selectedCode) {
        for (int i = 0; i < optionList.size(); i++)
            if (Objects.equals(getKey(optionList, optionList.get(i - 1)), selectedCode))
                spinner.setSelection(i + 1);
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
