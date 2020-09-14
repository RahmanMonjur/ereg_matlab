package com.example.android.androidskeletonapp.data.service.Preglist;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tei {
    private String uid;
    @JsonProperty(value = "trackedEntityInstance")
    public String getUid() {
        return uid;
    }

    @JsonProperty(value = "trackedEntityInstance")
    public void setUid(String uid) {
        this.uid = uid;
    }
}
