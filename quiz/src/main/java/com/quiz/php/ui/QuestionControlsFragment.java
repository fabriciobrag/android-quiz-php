package com.quiz.php.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quiz.php.R;
import com.quiz.php.core.Quiz;


/**
 * Created by fabricio on 3/5/14.
 */
public class QuestionControlsFragment extends Fragment
        implements View.OnClickListener{

    ViewPager mViewPager;
    Button mNextButton;
    Button mPrevButton;
    Quiz mQuiz;

    TextView mTimer;
    Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            mTimer.setText(mQuiz.getTime());
            mTimerHandle.postDelayed(this, 500);

        }
    };
    Handler mTimerHandle = new Handler();
    private AdRequest mAdRequest;
    private AdView mAdView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mQuiz = Quiz.get(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        //stop timer
        mTimerHandle.removeCallbacks(mTimerRunnable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.question_controlls_fragment, parent, false);

        mNextButton = (Button) v.findViewById(R.id.question_nextButton);
        mNextButton.setOnClickListener(this);
        mPrevButton = (Button) v.findViewById(R.id.question_prevButton);
        mPrevButton.setOnClickListener(this);

        mTimer = (TextView) v.findViewById(R.id.timerTextView);
        mTimerHandle.postDelayed(mTimerRunnable, 0);

        //admob
//        mAdView = (AdView) v.findViewById(R.id.adView);
//        mAdRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(mAdRequest);

        return v;
    }

    @Override
    public void onClick(View view) {
        if (mViewPager == null) {
            mViewPager = (ViewPager) getActivity().findViewById(R.id.viewPagerQuestion);
        }

        //refresh add
//        mAdRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(mAdRequest);

        switch (view.getId()) {

            //next button
            case R.id.question_nextButton:
                if (mViewPager.getCurrentItem() == mQuiz.getNumQuestions() - 1 ) {

                    if (!mQuiz.isFinish()) {
                        Toast.makeText(getActivity(), getActivity().getResources().getText(R.string.no_more_questions),
                                Toast.LENGTH_SHORT).show();
                    }
                    getActivity().finish();
                }

                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1) ;
                break;

            //prev button
            case R.id.question_prevButton:
                if (mViewPager.getCurrentItem() == 0) {
                    getActivity().finish();
                }
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                break;
        }
    }
}
