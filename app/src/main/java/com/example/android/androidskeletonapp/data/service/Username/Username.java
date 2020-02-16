package com.example.android.androidskeletonapp.data.service.Username;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.user.UserCredentials;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Username {
    private  Long id;
    private  String uid;
    private  String code;
    private  String name;
    private  String displayName;
    private  Date created;
    private  Date lastUpdated;
    private  Boolean deleted;
    private  String birthday;
    private  String education;
    private  String gender;
    private  String jobTitle;
    private  String surname;
    private  String firstName;
    private  String introduction;
    private  String employer;
    private  String interests;
    private  String languages;
    private  String email;
    private  String phoneNumber;
    private  String nationality;
    private  UserCredentials userCredentials;
    private  List<OrganisationUnit> organisationUnits;
    private  List<OrganisationUnit> teiSearchOrganisationUnits;

    public Username (@Nullable Long id, String uid, @Nullable String code, @Nullable String name, @Nullable String displayName, @Nullable Date created, @Nullable Date lastUpdated, @Nullable Boolean deleted, @Nullable String birthday, @Nullable String education, @Nullable String gender, @Nullable String jobTitle, @Nullable String surname, @Nullable String firstName, @Nullable String introduction, @Nullable String employer, @Nullable String interests, @Nullable String languages, @Nullable String email, @Nullable String phoneNumber, @Nullable String nationality, @Nullable UserCredentials userCredentials, @Nullable List<OrganisationUnit> organisationUnits, @Nullable List<OrganisationUnit> teiSearchOrganisationUnits) {
        this.id = id;
        if (uid == null) {
            throw new NullPointerException("Null uid");
        } else {
            this.uid = uid;
            this.code = code;
            this.name = name;
            this.displayName = displayName;
            this.created = created;
            this.lastUpdated = lastUpdated;
            this.deleted = deleted;
            this.birthday = birthday;
            this.education = education;
            this.gender = gender;
            this.jobTitle = jobTitle;
            this.surname = surname;
            this.firstName = firstName;
            this.introduction = introduction;
            this.employer = employer;
            this.interests = interests;
            this.languages = languages;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.nationality = nationality;
            this.userCredentials = userCredentials;
            this.organisationUnits = organisationUnits;
            this.teiSearchOrganisationUnits = teiSearchOrganisationUnits;
        }
    }

    @JsonIgnore
    @Nullable
    public Long id() {
        return this.id;
    }

    @JsonProperty("id")
    public String uid() {
        return this.uid;
    }

    @Nullable
    public String code() {
        return this.code;
    }

    @Nullable
    public String name() {
        return this.name;
    }

    @Nullable
    public String displayName() {
        return this.displayName;
    }

    @Nullable
    public Date created() {
        return this.created;
    }

    @Nullable
    public Date lastUpdated() {
        return this.lastUpdated;
    }

    @Nullable
    public Boolean deleted() {
        return this.deleted;
    }

    @Nullable
    public String birthday() {
        return this.birthday;
    }

    @Nullable
    public String education() {
        return this.education;
    }

    @Nullable
    public String gender() {
        return this.gender;
    }

    @Nullable
    public String jobTitle() {
        return this.jobTitle;
    }

    @Nullable
    public String surname() {
        return this.surname;
    }

    @Nullable
    public String firstName() {
        return this.firstName;
    }

    @Nullable
    public String introduction() {
        return this.introduction;
    }

    @Nullable
    public String employer() {
        return this.employer;
    }

    @Nullable
    public String interests() {
        return this.interests;
    }

    @Nullable
    public String languages() {
        return this.languages;
    }

    @Nullable
    public String email() {
        return this.email;
    }

    @Nullable
    public String phoneNumber() {
        return this.phoneNumber;
    }

    @Nullable
    public String nationality() {
        return this.nationality;
    }

    @Nullable
    public UserCredentials userCredentials() {
        return this.userCredentials;
    }

    @Nullable
    public List<OrganisationUnit> organisationUnits() {
        return this.organisationUnits;
    }

    @Nullable
    public List<OrganisationUnit> teiSearchOrganisationUnits() {
        return this.teiSearchOrganisationUnits;
    }

    
}
