package com.example.android.androidskeletonapp.ui.data_entry.field_type_holder;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.DateFormatHelper;
import com.example.android.androidskeletonapp.data.service.forms.FormField;
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
        dateFormat = "yyyy-MM-dd";
    }

    void bind(FormField fieldItem) {
        super.bind(fieldItem);

        try {
            dateFormat = DateFormatHelper.getFormat(fieldItem.getValue());
            input_date.setText(fieldItem.getValue() != null ?
                    fieldItem.getValue() :
                    itemView.getContext().getString(R.string.date_button_text));
            Date dob = DateFormatHelper.parseDateAutoFormat(fieldItem.getValue());
            Date enrolDate= new Date();
            int[] dateDifference = getDifference(dob, enrolDate);
            input_day.setText(String.valueOf(dateDifference[2]));
            input_month.setText(String.valueOf(dateDifference[1]));
            input_year.setText(String.valueOf(dateDifference[0]));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        input_date.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(itemView.getContext(), (datePicker, year, month, day) ->
                    valueSavedListener.onValueSaved(fieldItem.getUid(), getDate(year, month, day, dateFormat)),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        input_year.setOnFocusChangeListener((view, b) -> {
                if (b == false)
                    dateFocusChange();
        });

        input_month.setOnFocusChangeListener((view, b) -> {
            if (b == false)
                dateFocusChange();
        });

        input_day.setOnFocusChangeListener((view, b) -> {
            if (b == false)
                dateFocusChange();
        });
    }

    private void dateFocusChange(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, input_day.getText().toString().length() > 0 ? 0-Integer.parseInt(input_day.getText().toString()) : 0 );
        calendar.add(Calendar.MONTH, input_month.getText().toString().length() > 0 ? 0-Integer.parseInt(input_month.getText().toString()) : 0);
        calendar.add(Calendar.YEAR, input_year.getText().toString().length() > 0 ? 0-Integer.parseInt(input_year.getText().toString()) : 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        String birthDate = DateFormatHelper.formatDate(calendar.getTime());
        if(dateFormat.equals("dd-MM-yyyy")){
            birthDate = DateFormatHelper.formatEnglishDate(calendar.getTime());
        }

        int[] dateDifference = getDifference(calendar.getTime(), Calendar.getInstance().getTime());
        this.input_day.setText(String.valueOf(dateDifference[2]));
        this.input_month.setText(String.valueOf(dateDifference[1]));
        this.input_year.setText(String.valueOf(dateDifference[0]));

        if (!input_date.getText().toString().equals(birthDate)) {
            input_date.setText(birthDate);
        }
    }

    private String getDate(int year, int month, int day, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date date = calendar.getTime();
        return DateFormatHelper.formatSimpleDate(date);
    }

    public static int[] getDifference(Date startDate, Date endDate) {
        org.joda.time.Period interval = new org.joda.time.Period(startDate.getTime(), endDate.getTime(), org.joda.time.PeriodType.yearMonthDayTime());
        return new int[]{interval.getYears(), interval.getMonths(), interval.getDays()};
    }


}