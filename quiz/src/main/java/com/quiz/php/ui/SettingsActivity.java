package com.quiz.php.ui;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.quiz.php.QuizApp;
import com.quiz.php.R;
import com.quiz.php.core.Quiz;

public class SettingsActivity extends PreferenceActivity {

    public static final String PREF_TIME = "pref_time";
    public static final String PREF_NUM_QUESTIONS = "pref_num_questions";
    public static final String PREF_TYPE = "pref_type";

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Crashlytics.start(this);

        addPreferencesFromResource(R.xml.preference);
        mContext = this;
        setContentView(R.layout.preference_start);

        PreferenceManager.setDefaultValues(SettingsActivity.this, R.xml.preference, false);

        Button start = (Button) findViewById(R.id.btn_start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(mContext, QuizSummaryListActivity.class);

                Quiz.get(mContext).reset();

                startActivity(intent);
            }
        });

       ((QuizApp) getApplication()).screenView("Preference");

    }
}
