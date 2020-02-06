package com.example.android.androidskeletonapp.ui.data_entry.field_type_holder;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.DateFormatHelper;
import com.example.android.androidskeletonapp.data.service.forms.FormField;
import com.example.android.androidskeletonapp.ui.main.GlobalClass;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

class DateFieldHolder extends FieldHolder {

    private final Button dateButton;
    GlobalClass globalVars;

    DateFieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView, valueSavedListener);
        this.dateButton = itemView.findViewById(R.id.dateButton);
    }

    void bind(FormField fieldItem) {
        super.bind(fieldItem);
        globalVars = (GlobalClass) this.itemView.getContext().getApplicationContext();

        try {
            dateButton.setText(fieldItem.getValue() != null ?
                    DateFormatHelper.getDateAsSystemFormat(DateFormatHelper.parseSimpleDate(fieldItem.getValue())) :
                    globalVars.getTranslatedWord(itemView.getContext().getString(R.string.date_button_text)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateButton.setTextSize(20);
        if (fieldItem.isEditable() == true) {
            dateButton.setOnClickListener(view -> {
                Calendar calendar = Calendar.getInstance();
                if (fieldItem.getValue() != null && !fieldItem.getValue().equalsIgnoreCase("")) {
                    String[] dateparts = fieldItem.getValue().split("\\-");
                    if (dateparts.length == 3)
                        if (dateparts[2].length() == 4)
                            calendar.set(Integer.parseInt(dateparts[2]), Integer.parseInt(dateparts[1])-1,Integer.parseInt(dateparts[0]));
                        else
                            calendar.set(Integer.parseInt(dateparts[0]), Integer.parseInt(dateparts[1])-1,Integer.parseInt(dateparts[2]));
                }
                new DatePickerDialog(itemView.getContext(), (datePicker, year, month, day) ->
                        valueSavedListener.onValueSaved(fieldItem.getUid(), getDate(year, month, day)),
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            });
        }
        else {
            dateButton.setTextColor(Color.DKGRAY);
        }
    }



    private String getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date date = calendar.getTime();
        return DateFormatHelper.formatSimpleDate(date);
    }
}
