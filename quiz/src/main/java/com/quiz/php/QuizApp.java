package com.quiz.php;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


/**
 * Created by fabricio on 5/30/14.
 */
public class QuizApp extends Application {


    synchronized Tracker getTracker() {

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

        return analytics.newTracker(R.xml.ga_tracker);
    }

    public void screenView(String screenName) {

        Tracker t = getTracker();

        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());

    }
}
