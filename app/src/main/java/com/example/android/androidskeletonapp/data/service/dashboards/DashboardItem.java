package com.example.android.androidskeletonapp.data.service.dashboards;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardItem {
    private String uid;
    @JsonProperty(value = "id")
    public String getUid() {
        return uid;
    }

    @JsonProperty(value = "id")
    public void setUid(String uid) {
        this.uid = uid;
    }

    private String type;
    @JsonProperty(value = "type")
    public String getType() {
        return type;
    }

    @JsonProperty(value = "type")
    public void setType(String type) {
        this.type = type;
    }

    private ReportTable reportTable;

    @JsonProperty(value = "reportTable")
    public ReportTable getReportTable() {
        return reportTable;
    }

    @JsonProperty(value = "reportTable")
    public void setReportTable(ReportTable reportTable) {
        this.reportTable = reportTable;
    }

    private Chart chart;

    @JsonProperty(value = "chart")
    public Chart getChart() {
        return chart;
    }

    @JsonProperty(value = "chart")
    public void setChart(Chart chart) {
        this.chart = chart;
    }
}
