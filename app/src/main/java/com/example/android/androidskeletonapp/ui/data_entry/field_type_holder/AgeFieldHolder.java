package com.example.android.androidskeletonapp.ui.data_entry.field_type_holder;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.DateFormatHelper;
import com.example.android.androidskeletonapp.data.service.forms.FormField;
import com.example.android.androidskeletonapp.ui.main.GlobalClass;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class AgeFieldHolder extends FieldHolder {

    private TextInputEditText input_date;
    private TextInputEditText input_day;
    private TextInputEditText input_month;
    private TextInputEditText input_year;
    private String dateFormat;

    private ViewDataBinding binding;

    private Calendar selectedCalendar;


    private String label;
    private TextInputLayout inputLayout;

    AgeFieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView, valueSavedListener);
        this.input_date = itemView.findViewById(R.id.date_picker);
        input_date.setFocusable(false);
        this.input_day = itemView.findViewById(R.id.input_days);
        this.input_month = itemView.findViewById(R.id.input_month);
        this.input_year = itemView.findViewById(R.id.input_year);
        dateFormat = Sdk.d2().systemInfoModule().systemInfo().blockingGet().dateFormat();
    }

    void bind(FormField fieldItem) {
        super.bind(fieldItem);

        try {
            input_date.setText(fieldItem.getValue() != null ?
                    fieldItem.getValue() :
                    itemView.getContext().getString(R.string.date_button_text));
            if (fieldItem.getValue() != null) {
                dateFormat = DateFormatHelper.getFormat(fieldItem.getValue());
                Date dob = DateFormatHelper.parseDateAutoFormat(fieldItem.getValue());
                Date enrolDate = new Date();
                int[] dateDifference = getDifference(dob, enrolDate);
                input_day.setText(String.valueOf(dateDifference[2]));
                input_month.setText(String.valueOf(dateDifference[1]));
                input_year.setText(String.valueOf(dateDifference[0]));

                input_date.setTextSize(20);
                input_day.setTextSize(20);
                input_month.setTextSize(20);
                input_year.setTextSize(20);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        input_date.setOnClickListener(view -> {
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
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        input_year.setOnFocusChangeListener((view, b) -> {
                if (b == false)
                    dateFocusChange(fieldItem.getUid());
        });

        input_month.setOnFocusChangeListener((view, b) -> {
            if (b == false)
                dateFocusChange(fieldItem.getUid());
        });

        input_day.setOnFocusChangeListener((view, b) -> {
            if (b == false)
                dateFocusChange(fieldItem.getUid());
        });
    }

    private void dateFocusChange(String fieldUid){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, input_day.getText().toString().length() > 0 ? 0-Integer.parseInt(input_day.getText().toString()) : 0 );
        calendar.add(Calendar.MONTH, input_month.getText().toString().length() > 0 ? 0-Integer.parseInt(input_month.getText().toString()) : 0);
        calendar.add(Calendar.YEAR, input_year.getText().toString().length() > 0 ? 0-Integer.parseInt(input_year.getText().toString()) : 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int[] dateDifference = getDifference(calendar.getTime(), Calendar.getInstance().getTime());
        this.input_day.setText(String.valueOf(dateDifference[2]));
        this.input_month.setText(String.valueOf(dateDifference[1]));
        this.input_year.setText(String.valueOf(dateDifference[0]));

        String birthDate = DateFormatHelper.getDateAsSystemFormat(calendar.getTime());
        if (!input_date.getText().toString().equals(birthDate)) {
            input_date.setText(birthDate);
            valueSavedListener.onValueSaved(fieldUid, birthDate);
        }
    }

    private String getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date date = calendar.getTime();
        return DateFormatHelper.getDateAsSystemFormat(date);
    }

    public static int[] getDifference(Date startDate, Date endDate) {
        org.joda.time.Period interval = new org.joda.time.Period(startDate.getTime(), endDate.getTime(), org.joda.time.PeriodType.yearMonthDayTime());
        return new int[]{interval.getYears(), interval.getMonths(), interval.getDays()};
    }


}