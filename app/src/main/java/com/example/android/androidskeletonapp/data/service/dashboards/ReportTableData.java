package com.example.android.androidskeletonapp.data.service.dashboards;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ReportTableData {
    private String title;
    @JsonProperty(value = "title")
    public String getTitle() {
        return title;
    }

    @JsonProperty(value = "title")
    public void setTitle(String title) {
        this.title = title;
    }

    private List<ReportTableHeader> headers;
    @JsonProperty(value = "headers")
    public List<ReportTableHeader> getHeaders() {
        return headers;
    }

    @JsonProperty(value = "headers")
    public void setHeader(List<ReportTableHeader> headers) {
        this.headers = headers;
    }

    private List<List<String>> rows;
    @JsonProperty(value = "rows")
    public List<List<String>> getRows() {
        return rows;
    }

    @JsonProperty(value = "rows")
    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }
}
