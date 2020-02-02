package com.example.android.androidskeletonapp.ui.data_entry.field_type_holder;

public class SpinnerItems {
    private  String id;
    private String value;

    public SpinnerItems ( String Id , String Value ) {
        this.id = Id;
        this.value = Value;
    }

    public String getId () {
        return id;
    }

    public String getValue () {
        return value;
    }

    @Override
    public String toString () {
        return value;
    }
}
