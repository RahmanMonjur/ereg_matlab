package com.example.android.androidskeletonapp.ui.main;


import android.app.Application;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

import java.util.HashMap;
import java.util.Map;

public class GlobalClass extends Application {

    private OrganisationUnit userScopeOrgUid;
    public OrganisationUnit getOrgUid() {
        return userScopeOrgUid;
    }
    public void setOrgUid(OrganisationUnit pOrgUid) {
        userScopeOrgUid = pOrgUid;
    }

    private String userDateFormat;
    public String getUserDateFormat() {
        return userDateFormat;
    }
    public void setUserDateFormat(String dateFormat) {
        userDateFormat = dateFormat;
    }

    private String userLocale;
    public String getUserLocale() {return userLocale;}
    public void setUserLocale(String userLocale) { this.userLocale = userLocale;}


    public HashMap<String, String> setTranslatedWords() {
        HashMap<String, String> translatedWords = new HashMap<>();
        translatedWords.put("Yes","হ্যাঁ");
        translatedWords.put("No","না");
        translatedWords.put("Program Stages","প্রোগ্রাম স্টেজ মেনু");
        translatedWords.put("Click to select date","তারিখ লিখতে এখানে ক্লিক করুন");
        translatedWords.put("Enrolled Participants","নিবন্ধিতদের তালিকা");
        translatedWords.put("Please choose an organisation unit","আপনার কর্মস্থল নির্বাচন করুন");
        translatedWords.put("Home","হোম পেজ");
        translatedWords.put("Input any value to be more specific","আরও নির্দিষ্ট হতে যেকোনো ডাটা ইনপুট করুন");
        translatedWords.put("Please select from list","তালিকা থেকে নির্বাচন করুন");
        translatedWords.put("Enrollment Confirmation","তালিকাভুক্তি নিশ্চিতকরণ");
        translatedWords.put("This TEI has no enrollment in this program","প্রোগ্রামটিতে এই অংশগ্রহণকারী তালিকাভুক্ত হয়নি");
        translatedWords.put("Do you want to enroll this TEI now?","আপনি কি এখনই নথিভুক্ত করতে চান?");
        translatedWords.put("Yes, I want to enroll","হ্যাঁ, আমি তালিকাভুক্ত করতে চাই");

        return  translatedWords;
    }
    public String getTranslatedWord(String text) {
        String locale = getUserLocale() == null ? "" : getUserLocale();
        String matchedValue = "";
        HashMap<String, String> translatedWords = setTranslatedWords();
        for (String key:translatedWords.keySet()) {
            if (key.equalsIgnoreCase(text)) {
                matchedValue = translatedWords.get(key);
                break;
            }
        }
        if(locale.isEmpty() || locale == "bn")
            return matchedValue.isEmpty() ? text : matchedValue;
        else
            return text;
    }
}
