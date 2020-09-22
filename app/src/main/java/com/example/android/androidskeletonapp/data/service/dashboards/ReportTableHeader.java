package com.example.android.androidskeletonapp.data.service.dashboards;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportTableHeader {
    private String name;
    @JsonProperty(value = "name")
    public String getName() {
        return name;
    }

    @JsonProperty(value = "name")
    public void setName(String name) {
        this.name = name;
    }

    private String column;
    @JsonProperty(value = "column")
    public String getColumn() {
        return column;
    }

    @JsonProperty(value = "column")
    public void setColumn(String column) {
        this.column = column;
    }

    private String valueType;
    @JsonProperty(value = "valueType")
    public String getValueType() {
        return valueType;
    }

    @JsonProperty(value = "valueType")
    public void setValueType(String valueType) {
        this.valueType = valueType;
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

    private Boolean hidden;
    @JsonProperty(value = "hidden")
    public Boolean getHidden() {
        return hidden;
    }

    @JsonProperty(value = "hidden")
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    private String meta;
    @JsonProperty(value = "meta")
    public String getMeta() {
        return meta;
    }

    @JsonProperty(value = "meta")
    public void setMeta(String meta) {
        this.meta = meta;
    }


}
