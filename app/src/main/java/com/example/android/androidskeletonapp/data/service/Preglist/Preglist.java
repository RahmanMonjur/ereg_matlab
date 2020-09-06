package com.example.android.androidskeletonapp.data.service.Preglist;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



@JsonIgnoreProperties(ignoreUnknown = true)
public class Preglist {
    private  String dhis_id;
    private  String elem_id;
    private  String wom_name;
    private  String hus_name;
    private  String address;
    private  String phone_no;
    private  String gage;

    public Preglist (String dhis_id, @Nullable String elem_id, @Nullable String wom_name, @Nullable String hus_name, @Nullable String address, @Nullable String phone_no, @Nullable String gage) {
        if (dhis_id == null) {
            throw new NullPointerException("Null uid");
        } else {
            this.dhis_id = dhis_id;
            this.elem_id = elem_id;
            this.wom_name = wom_name;
            this.hus_name = hus_name;
            this.address = address;
            this.phone_no = phone_no;
            this.gage = gage;
        }
    }

    public String dhis_id() {
        return this.dhis_id;
    }

    @Nullable
    public String elem_id() {
        return this.elem_id;
    }

    @Nullable
    public String wom_name() {
        return this.wom_name;
    }

    @Nullable
    public String hus_name() {
        return this.hus_name;
    }

    @Nullable
    public String address() {
        return this.address;
    }

    @Nullable
    public String phone_no() {
        return this.phone_no;
    }

    @Nullable
    public String gage() {
        return this.gage;
    }

}
