package com.example.android.androidskeletonapp.data.service.Username;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserInfo {
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
