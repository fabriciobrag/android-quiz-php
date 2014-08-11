package com.quiz.php.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quiz.php.R;
import com.quiz.php.core.Category;
import com.quiz.php.core.Question;
import com.quiz.php.core.Quiz;
import com.quiz.php.core.Util;
import com.quiz.php.persistence.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fabricio on 2/11/14.
 */
public class ResultFragment extends Fragment {

    private Quiz mQuiz;
    private ArrayList<Question> mQuestions;
    private int mScore;
    private HashMap<Integer, List<Question>> mQuestionsCategory;
    private Context mContext;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = getActivity();
        mQuiz = Quiz.get(getActivity());
        mQuestions = mQuiz.getQuestions();
        mScore = 0;
        mQuestionsCategory = new HashMap<Integer, List<Question>>();
        for (Question q : mQuestions) {
            if (q.isCorrect()) {
                mScore++;
            }
            //Log.d("result", q.toString());
            int id = q.getCategory().getId();
            if (mQuestionsCategory.get(id) == null) {
                mQuestionsCategory.put(id, new ArrayList<Question>());
            }
            mQuestionsCategory.get(id).add(q);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.result_fragment, parent, false);

        TextView scoreTextView  = (TextView) v.findViewById(R.id.score_result);
        scoreTextView.setText(String.format("%d / %d", mScore, mQuestions.size()));

        TextView avgTextView = (TextView) v.findViewById(R.id.percent_result);
        avgTextView.setText(getAverageString(mScore, mQuestions.size()) + "%");

        LinearLayout categoriesResult = (LinearLayout) v.findViewById(R.id.categories_result);


        DBHelper db = new DBHelper(mContext);
        ArrayList<Category> categories = db.getAllCategories();



        for (Category c : categories) {
            ArrayList<Question>  listQuestions = (ArrayList<Question>) mQuestionsCategory.get(c.getId());
            if (listQuestions != null) {
                int scoreCategory = 0;

                for (Question q : listQuestions) {
                    if (q.isCorrect()) {
                        scoreCategory++;
                    }
                }

                TextView categoryTextView  = new TextView(mContext);
                categoryTextView.setText(c.getCategory());
                categoryTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                categoriesResult.addView(categoryTextView);
                categoriesResult.setPadding(0,10,0,0);

                TextView score = new TextView(mContext);
                //%%\o/%%
                score.setText(String.format("%s / %s (%s%%)",
                        scoreCategory, listQuestions.size(), getAverageString(scoreCategory, listQuestions.size())));
                score.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                score.setPadding(0,0,4,0);

                categoriesResult.addView(score);

                categoriesResult.addView(getDividerView());

                Log.d("result", String.format("Cat: %s - Score: %s / %s",
                        c.getCategory(), scoreCategory, listQuestions.size()));
            }
        }


        Button resultSummary = (Button) v.findViewById(R.id.result_summary);
        resultSummary.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        Button resultRestart = (Button) v.findViewById(R.id.result_restart);
        resultRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    private String getAverageString(int score, int size) {

        if (size == 0) {
            return "0";
        }
        float p =  (score * 100.0f) / size;
        return String.format("%d", (int)p);
    }


    private View getDividerView() {

        View dividerView = new View(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Util.dpToPx(mContext, 1));
        lp.setMargins(0,5,0,5);
        dividerView.setLayoutParams(lp);


        dividerView.setBackgroundResource(android.R.color.darker_gray);

        return dividerView;
    }
}
