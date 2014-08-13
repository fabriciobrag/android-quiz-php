package com.quiz.php.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.quiz.php.R;
import com.quiz.php.core.Question;
import com.quiz.php.core.Quiz;

import java.util.ArrayList;

/**
 * Created by fabricio on 1/29/14.
 */
public class QuestionPagerActivity extends ActionBarActivity
        implements ViewPager.OnPageChangeListener {

    ViewPager mViewPager;
    ArrayList<Question> mQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.question_pager_activity);

        FragmentManager fm = getSupportFragmentManager();

        mViewPager = (ViewPager) findViewById(R.id.viewPagerQuestion);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setAdapter(new QuestionAdapter(fm));
        onPageSelected(0);

        mQuestions = Quiz.get(getBaseContext()).getQuestions();

        int index = getIntent().getIntExtra(QuestionFragment.EXTRA_QUESTION_INDEX, 0);
        Question q = mQuestions.get(index);
        for (int i = 0; i < mQuestions.size(); i++) {
            if (mQuestions.get(i).getId() == q.getId()) {
                mViewPager.setCurrentItem(i);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private class QuestionAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Question> mQuestions;

        private QuestionAdapter(FragmentManager fm) {
            super(fm);
            mQuestions = Quiz.get(getBaseContext()).getQuestions();
        }

        @Override
        public Fragment getItem(int i) {
            return  QuestionFragment.newInstance(i);
        }

        @Override
        public int getCount() {
            return mQuestions.size();
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) { }

    @Override
    public void onPageSelected(int i) {
        setTitle(String.format("Question #%s", i + 1));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

        if (state == ViewPager.SCROLL_STATE_IDLE) {
            // Hide the soft keyboard.
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(mViewPager.getWindowToken(), 0);

        }
    }
}
