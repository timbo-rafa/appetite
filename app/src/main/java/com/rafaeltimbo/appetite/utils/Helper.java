package com.rafaeltimbo.appetite.utils;


import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.rafaeltimbo.appetite.BusinessListFilterDialog;
import com.rafaeltimbo.appetite.BusinessListRangeDialog;
import com.rafaeltimbo.appetite.service.SharedPreferencesService;

import static com.rafaeltimbo.appetite.utils.Constants.SHARED_PREFERENCES_DEFAULT_USERNAME;
import static com.rafaeltimbo.appetite.utils.Constants.SHARED_PREFERENCES_USER_KEY;
import static com.rafaeltimbo.appetite.utils.Constants.GREETING;

public final class Helper {

    // Set user greetings in the view indicated by textViewID. This might be used by many screens
    public static String setUserGreetingTextView(Activity context, int textViewId) {
        SharedPreferencesService preferences = new SharedPreferencesService(context);
        String username = preferences.getFromSharedPreferences(SHARED_PREFERENCES_USER_KEY);
        if( username.trim().length() == 0){
            username = SHARED_PREFERENCES_DEFAULT_USERNAME;
        }
        TextView loggedInUserGreeting = context.findViewById(textViewId);
        String greeting = GREETING + username;
        loggedInUserGreeting.setText(greeting);
        loggedInUserGreeting.setTypeface(Typeface.DEFAULT_BOLD);
        return username;
    }

    public static void OpenRangeDialog(AppCompatActivity context) {
        BusinessListRangeDialog rangeDialog = new BusinessListRangeDialog();
        rangeDialog.show(context.getSupportFragmentManager(), "Range Dialog");
    }

    public static void OpenFilterDialog(AppCompatActivity context) {
        BusinessListFilterDialog filterDialog = new BusinessListFilterDialog();
        filterDialog.show(context.getSupportFragmentManager(), "Filter Dialog");
    }
}
