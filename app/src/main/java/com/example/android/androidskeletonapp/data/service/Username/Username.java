package com.example.android.androidskeletonapp.data.service.Username;


import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.CoreObject;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_Username.Builder.class)
public abstract class Username extends BaseIdentifiableObject implements CoreObject {

    @Nullable
    @JsonProperty()
    public abstract String surName();

    @Nullable
    @JsonProperty()
    public abstract String firstName();

    @NonNull
    public static Username create(Cursor cursor) {
        return $AutoValue_Username.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_Username.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {

        public abstract Builder id(Long id);

        public abstract Builder surName(String surname);

        public abstract Builder firstName(String firstName);

        public abstract Username build();
    }

}
