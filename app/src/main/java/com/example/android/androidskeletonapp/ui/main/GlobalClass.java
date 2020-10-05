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
        translatedWords.put("Welcome","স্বাগত");
        translatedWords.put("Yes","হ্যাঁ");
        translatedWords.put("No","না");
        translatedWords.put("Program Stages","প্রোগ্রাম স্টেজ নির্বাচন");
        translatedWords.put("Click to select date","ক্লিক করে তারিখ দিন");
        translatedWords.put("Enrolled Participants","নিবন্ধিতদের তালিকা");
        translatedWords.put("Please choose an organisation unit","আপনার কর্মস্থল নির্বাচন করুন");
        translatedWords.put("Home","হোম পেজ");
        translatedWords.put("Input any value to be more specific","আরও নির্দিষ্ট হতে যেকোনো ডাটা ইনপুট করুন");
        translatedWords.put("Please select from list","তালিকা থেকে নির্বাচন করুন");
        translatedWords.put("Enrollment Confirmation","তালিকাভুক্তি নিশ্চিতকরণ");
        translatedWords.put("This TEI has no enrollment in this program","প্রোগ্রামটিতে এই অংশগ্রহণকারী তালিকাভুক্ত হয়নি");
        translatedWords.put("Do you want to enroll this TEI now?","আপনি কি এখনই তালিকাুক্ত করতে চান?");
        translatedWords.put("Yes, I want to enroll","হ্যাঁ, আমি তালিকাভুক্ত করতে চাই");
        translatedWords.put("Delete Confirmation","ডিলিট নিশ্চিতকরণ");
        translatedWords.put("Do you really want to delete?","আপনি কি সত্যিই মুছতে চান?");
        translatedWords.put("Yes, I want to delete","হ্যাঁ, আমি মুছতে চাই");
        translatedWords.put("Exit Confirmation","প্রস্থানের নিশ্চিতকরণ");
        translatedWords.put("Do you really want to exit?","আপনি কি সত্যিই প্রস্থান করতে চান?");
        translatedWords.put("Yes, I want to exit","হ্যাঁ, আমি প্রস্থান করতে চাই");
        translatedWords.put("Day","দিন");
        translatedWords.put("Month","মাস");
        translatedWords.put("Year","বছর");
        translatedWords.put("You have selected","আপনি নির্বাচন করেছেন");
        translatedWords.put("Number of women enrolled in the Org Unit","এই কর্মস্থলে মোট নিবন্ধিতের সংখ্যা");
        translatedWords.put("Number of women enrolled in last month","গত মাসে নিবন্ধনের সংখ্যা");
        translatedWords.put("Number of HH visited in last month","গত মাসে বাড়ি পরিদর্শনের সংখ্যা");
        translatedWords.put("Visit Date","পরিদর্শনের তারিখ");
        translatedWords.put("Enrollment Date","নিবন্ধনের তারিখ");
        translatedWords.put("No tracked entity instances found","কাউকে খুঁজে পাওয়া যায়নি");
        translatedWords.put("Updating metadata…","মেটাডাটা আপডেট হচ্ছে...");
        translatedWords.put("Downloading data…","ডাটা ডাউনলোড হচ্ছে...");
        translatedWords.put("Uploading data…","ডাটা আপলোড হচ্ছে...");
        translatedWords.put("Update metadata","আপডেট মেটাডাটা");
        translatedWords.put("Update metadata and data","আপডেট মেটাডাটা এবং ডাটা");
        translatedWords.put("Download data","ডাউনলোড ডাটা");
        translatedWords.put("Upload data","আপলোড ডাটা");
        translatedWords.put("Downloading metadata and data...","মেটাডাটা এবং ডাটা ডাউনলোড হচ্ছে...");
        translatedWords.put("No program stages found","কোনো প্রোগ্রাম স্টেজ পাওয়া যায়নি");
        translatedWords.put("No events found","কোনো ইভেন্ট পাওয়া যায়নি");
        translatedWords.put("This is a mandatory field. Please input a value.","এটি বাধ্যতামূলক। ডাটা ইনপুট করুন।");
        translatedWords.put("You did not fill up some mandatory fields","কিছু বাধ্যতামূলক ডাটা দেওয়া হয়নি");
        translatedWords.put("Something went wrong with the Element application. Please check.","এলিমেন্ট অ্যাপ্লিকেশনটিতে কিছু ভুল হয়েছে। চেক করুন।");
        translatedWords.put("You can change the program by clicking below","নোট: নিচের লিস্ট বাটন এ ক্লিক করে প্রোগ্রাম পরিবর্তন করুন");
        translatedWords.put("Do not forget to enroll in the MCH program","নোট: MCH প্রোগ্রাম এ রেজিস্ট্রেশন করতে ভুলবেন না");
        translatedWords.put("You do not have stable internet connection now.\nplease try later.","এখন আপনার ইন্টারনেট কানেকশন নেই।\nপরে চেষ্টা করুন।");
        translatedWords.put("Sorry, you're allowed to see this page","আপনি এই তালিকা দেখতে পাবেন না।");
        translatedWords.put("Pregnant women list","গর্ভবতীর তালিকা");
        translatedWords.put("Client Register Program","ক্লায়েন্ট রেজিস্টার প্রোগ্রাম");
        translatedWords.put("Organisation Unit","কর্মস্থল নির্বাচন");
        translatedWords.put("Current Pregnancies","গর্ভবতীর তালিকা (৩২ সপ্তাহের অধিক)");
        translatedWords.put("Dashboard","ড্যাশবোর্ড");
        translatedWords.put("","");
        translatedWords.put("","");
        //translatedWords.put("","");

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
