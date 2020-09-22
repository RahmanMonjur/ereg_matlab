package com.example.android.androidskeletonapp.data.service.dashboards;

import androidx.annotation.Nullable;

import com.example.android.androidskeletonapp.data.service.Username.UserGroup;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Dashboard {

    private String uid;
    @JsonProperty(value = "id")
    public String getUid() {
        return uid;
    }

    @JsonProperty(value = "id")
    public void setUid(String uid) {
        this.uid = uid;
    }

    private String name;
    @JsonProperty(value = "name")
    public String getName() {
        return name;
    }

    @JsonProperty(value = "name")
    public void setName(String name) {
        this.name = name;
    }

    private List<DashboardItem> dashboardItems;

    @JsonProperty(value = "dashboardItems")
    public List<DashboardItem> getDashboardItems() {
        return dashboardItems;
    }

    @JsonProperty(value = "dashboardItems")
    public void setDashboardItems(List<DashboardItem> dashboardItems) {
        this.dashboardItems = dashboardItems;
    }

}
