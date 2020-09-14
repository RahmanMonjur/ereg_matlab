package com.example.android.androidskeletonapp.data.service.forms;

import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ValueType;

public class FormField {

    private final boolean isEditable;
    private String uid;
    private String optionSetUid;
    private ValueType valueType;
    private String formLabel;
    private String formDescription;
    private String formWarning;
    private String formError;
    private String mandatory;
    private String value;
    private String optionCode;
    private boolean editable;
    private ObjectStyle objectStyle;

    public FormField(String uid, String optionSetUid,
                     ValueType valueType, String formLabel, String formDescription, String formWarning, String formError,
                     String mandatory, String value, String optionCode,
                     boolean isEditable, ObjectStyle objectStyle) {
        this.uid = uid;
        this.optionSetUid = optionSetUid;
        this.valueType = valueType;
        this.formLabel = formLabel;
        this.formDescription = formDescription;
        this.formWarning = formWarning;
        this.formError = formError;
        this.mandatory = mandatory;
        this.value = value;
        this.optionCode = optionCode;
        this.isEditable = isEditable;
        this.objectStyle = objectStyle;
    }

    public ObjectStyle getObjectStyle() {
        return objectStyle;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public String getUid() {
        return uid;
    }

    public String getOptionSetUid() {
        return optionSetUid;
    }

    public String getFormLabel() {
        return formLabel;
    }

    public String getFormDescription() {
        return formDescription;
    }

    public String getFormWarning() {
        return formWarning;
    }

    public String getFormError() {
        return formError;
    }

    public String getMandatory() {
        return mandatory;
    }

    public String getOptionCode() {
        return optionCode;
    }

    public String getValue() {
        return value;
    }

    public ValueType getValueType() {
        return valueType;
    }
}
