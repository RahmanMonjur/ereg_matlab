package com.example.android.androidskeletonapp.data.service.Username;

import android.database.Cursor;

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
    public abstract String userid();

    @Nullable
    @JsonProperty()
    public abstract String username();

    public static Username create(Cursor cursor) {
        return $AutoValue_Username.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_Username.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseIdentifiableObject.Builder<Builder> {

        public abstract Builder id(Long id);

        public abstract Builder userid(String userid);

        public abstract Builder username(String username);

        public abstract Username build();
    }

}
