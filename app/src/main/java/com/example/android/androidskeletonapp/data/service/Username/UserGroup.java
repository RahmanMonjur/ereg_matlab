package com.example.android.androidskeletonapp.data.service.Username;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserGroup {

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
