package com.quiz.php.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;

import com.quiz.php.ui.SettingsActivity;
import com.quiz.php.persistence.DBHelper;
import com.quiz.php.ui.TimePickerDialogPreference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by fabricio on 1/29/14.
 */
public class Quiz {

    private static Quiz sQuiz;
    private final SharedPreferences mPrefs;

    private ArrayList<Question> mQuestions;
    private Context mContext;

    private Boolean mIsFinish;
    private String mTime;

    //time left control
    private CountDownTimer mTimer;

    public Quiz(Context context){
        mContext = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        startQuiz();
    }

    public void reset() {
        sQuiz = null;
        mTimer.cancel();
    }

    public static Quiz get (Context context) {
        if (sQuiz == null) {
            sQuiz = new Quiz(context.getApplicationContext());
        }
        return sQuiz;
    }

    public Question getQuestion(int index) {
        return mQuestions.get(index);
    }

    public int getNumQuestions() {
        return mQuestions.size();
    }

    public void startQuiz () {
        setIsFinish(false);

        int num_questions = mPrefs.getInt(SettingsActivity.PREF_NUM_QUESTIONS, 0);

        String type = mPrefs.getString(SettingsActivity.PREF_TYPE, null);

        Log.d("quiz", String.format("Selected type %s", type));

        //get questions
        DBHelper helper = new DBHelper(mContext);
        mQuestions = helper.getRandomQuestions(num_questions, type);

        startTimer();

    }

    public ArrayList<Question> getQuestions() {
        return mQuestions;
    }

    public String getTime() {
        return mTime;
    }

    public Boolean isFinish() {
        return mIsFinish;
    }

    public void setIsFinish(Boolean mIsFinish) {
        this.mIsFinish = mIsFinish;

        if (mTimer != null && mIsFinish) {
            mTimer.cancel();
        }
    }

    public void startTimer() {
        String time = mPrefs.getString(SettingsActivity.PREF_TIME, null);

        //first run
        if (time == null) {
            time = TimePickerDialogPreference.DEFAULT_TIME;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = null;
        try {
            date = sdf.parse("1970-01-01 "+ time +":00");


        } catch (ParseException e) {
            e.printStackTrace();
        }

        long timeLimit = date.getTime() ;


        mTimer = new CountDownTimer(timeLimit, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //@todo store millisUntilFinished for pause feature

                int seconds = (int) (millisUntilFinished / 1000) % 60 ;
                int minutes = (int) ((millisUntilFinished / (1000*60)) % 60);
                int hours   = (int) ((millisUntilFinished / (1000*60*60)) % 24);

                Log.d("pref", String.valueOf(minutes ) + " : " + String.valueOf(seconds));

                mTime = String.format("%d:%02d:%02d", hours, minutes, seconds);
            }

            @Override
            public void onFinish() {
                sQuiz.setIsFinish(true);
            }

        }.start();
    }
}