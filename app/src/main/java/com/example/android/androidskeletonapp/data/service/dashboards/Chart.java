package com.example.android.androidskeletonapp.data.service.dashboards;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Chart {
    private String uid;
    @JsonProperty(value = "id")
    public String getUid() {
        return uid;
    }

    @JsonProperty(value = "id")
    public void setUid(String uid) {
        this.uid = uid;
    }
}
