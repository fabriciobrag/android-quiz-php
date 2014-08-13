package com.quiz.php.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.quiz.php.R;
import com.quiz.php.core.Alternative;
import com.quiz.php.core.Question;
import com.quiz.php.core.Quiz;

import java.util.ArrayList;


/**
 * Created by fabricio on 1/29/14.
 */
public class QuestionFragment extends Fragment {

    public static final String EXTRA_QUESTION_INDEX = "com.android.quiz.question_index";
    private Context mContext;
    private CodeTextView mTxtViewQuestion;
    private Question mQuestion;
    private Quiz mQuiz;
    int mCurrent;

    private RadioGroup mAnswers;
    private ArrayList<Alternative> mAlternatives;
    private RadioButton[] mRadioAlternatives;
    private View mRootView;
    private CheckBox[] mCheckAlternatives;
    private TextView mChooseText;


    public static QuestionFragment newInstance (int index) {

        QuestionFragment fragment = new QuestionFragment();

        Bundle args = new Bundle();
        args.putInt(EXTRA_QUESTION_INDEX, index);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mContext = getActivity();
        mQuiz = Quiz.get(mContext);
        mCurrent = getArguments().getInt(EXTRA_QUESTION_INDEX);

        mQuestion = mQuiz.getQuestion(mCurrent);
        mAlternatives = mQuestion.getAlternatives();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.question_fragment, parent, false);

        mTxtViewQuestion = (CodeTextView) mRootView.findViewById(R.id.questionTxtView);
        mTxtViewQuestion.setTextHighlighted(mQuestion.getQuestion());

        switch (mQuestion.getType()) {
            case 1:
                setSingleQuestion(inflater);
                break;
            case 2:
                setTextQuestion();
                break;
            case 3:
                setMultiQuestion();
                break;
        }

        return mRootView;
    }

    private void setMultiQuestion() {

        mCheckAlternatives = new CheckBox[mAlternatives.size()];
        LinearLayout optionsContainer = (LinearLayout) mRootView.findViewById(R.id.multi_alternatives);

        //number of options to choose
        mChooseText = (TextView) mRootView.findViewById(R.id.choose_text);
        mChooseText.setVisibility(View.VISIBLE);
        mChooseText.setText("Choose: "+String.valueOf(mQuestion.getRemaining()));


        ArrayList<Integer> checkedIndices = new ArrayList<Integer>();
        for (int i = 0; i < mAlternatives.size(); i++) {
            mCheckAlternatives[i] = new CheckBox(mContext);

            mCheckAlternatives[i].setText(mAlternatives.get(i).getAlternative());
            mCheckAlternatives[i].setId(mAlternatives.get(i).getId());
            mCheckAlternatives[i].setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimensionPixelSize(R.dimen.text_size_medium));

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                mCheckAlternatives[i].setPadding(20, 20, 20, 20);
            }
            mCheckAlternatives[i].setButtonDrawable(getResources().getIdentifier(
                    "custom_button_" + i, "drawable",
                    getActivity().getApplicationContext().getPackageName()));

            mCheckAlternatives[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<Alternative> alternatives = mQuestion.getAlternatives();

                    for (int i = 0; i < alternatives.size(); i++) {

                        Alternative alt = alternatives.get(i);
                        if (v.getId() == alt.getId()) {

                            if (alt.isSelected()) {
                                mCheckAlternatives[i].setTypeface(null, Typeface.NORMAL);
                                alt.setSelected(false);
                            } else if (mQuestion.getRemaining() > 0) {
                                mCheckAlternatives[i].setTypeface(null, Typeface.BOLD);
                                alt.setSelected(true);
                            } else {
                                mCheckAlternatives[i].setChecked(false);
                            }
                        }
                    }
                    mChooseText.setText("Choose: "+String.valueOf(mQuestion.getRemaining()));

                    //checks if the question is answered
                    if (mQuestion.getNumOfChecked()  > 0) {
                        mQuestion.setAnswered(true);
                    } else {
                        mQuestion.setAnswered(false);
                    }
                }
            });

            //finished styles
            if (mQuiz.isFinish()) {
                mCheckAlternatives[i].setEnabled(false);
                mCheckAlternatives[i].setTextColor(Color.BLACK);

                if (mAlternatives.get(i).isCorrect()) {
                    mCheckAlternatives[i].setBackgroundColor(
                            getResources().getColor(R.color.bg_correct));
                }
            }

            if (mAlternatives.get(i).isSelected()) {
                checkedIndices.add(i);
            }

            optionsContainer.addView(mCheckAlternatives[i]);
        }

        for (Integer i : checkedIndices ){
            mCheckAlternatives[i].setChecked(true);
            mCheckAlternatives[i].setTypeface(null, Typeface.BOLD);
        }
    }

    private void setSingleQuestion(LayoutInflater inflater) {
        mAnswers = (RadioGroup) mRootView.findViewById(R.id.radio_single_alternatives);
        mAnswers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                mQuestion.setAnswered(true);

                ArrayList<Alternative> alternatives = mQuestion.getAlternatives();
                for (int i = 0; i < alternatives.size(); i++) {
                    Alternative alt = alternatives.get(i);

                    if (mRadioAlternatives != null) {
                        mRadioAlternatives[i].setTypeface(null, Typeface.BOLD);
                    }

                    if (id == alt.getId()) {
                        alt.setSelected(true);

                    } else {

                        alt.setSelected(false);
                        if (mRadioAlternatives != null) {
                            mRadioAlternatives[i].setTypeface(null, Typeface.NORMAL);
                        }
                    }
                }
            }
        });

        mRadioAlternatives = new RadioButton[mAlternatives.size()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        //not set selected in add loop
        int indexSelected = -1;

        for (int i = 0; i < mAlternatives.size(); i++) {
            mRadioAlternatives[i] = (RadioButton) inflater.inflate(R.layout.alternative_option, null);
            mRadioAlternatives[i].setText(mAlternatives.get(i).getAlternative());
            mRadioAlternatives[i].setId(mAlternatives.get(i).getId());
            mRadioAlternatives[i].setLayoutParams(layoutParams);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                mRadioAlternatives[i].setPadding(20, 20, 20, 20);
            }
            mRadioAlternatives[i].setButtonDrawable(getResources().getIdentifier(
                    "custom_button_" + i, "drawable", getActivity().getApplicationContext().getPackageName()));

            //finished styles
            if (mQuiz.isFinish()) {
                mRadioAlternatives[i].setEnabled(false);
                mRadioAlternatives[i].setTextColor(Color.BLACK);

                if (mAlternatives.get(i).isCorrect()) {
                    mRadioAlternatives[i].setBackgroundResource(R.color.bg_correct);
                }
            }

            //options to radio group
            mAnswers.addView(mRadioAlternatives[i]);

            if (mAlternatives.get(i).isSelected()) {
                indexSelected = i;
            }
        }

        if (indexSelected != -1) {
            mRadioAlternatives[indexSelected].setChecked(true);
        }
    }

    /**
     * Set InputText for open field questions
     */
    private void setTextQuestion() {

        final EditText inputAnswer = (EditText) mRootView.findViewById(R.id.answer_input);
        inputAnswer.setVisibility(View.VISIBLE);
        if (mQuestion.getInputAnswer() != null) {
            inputAnswer.setText(mQuestion.getInputAnswer());
        }

        if (mQuiz.isFinish()) {
            inputAnswer.setEnabled(false);
            if (mQuestion.isCorrect()) {
                inputAnswer.setTextColor(getResources().getColor(R.color.input_answer_correct));
            } else {
                inputAnswer.setTextColor(getResources().getColor(R.color.input_answer_wrong));
            }

        } else {
            inputAnswer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    mQuestion.setInputAnswer(inputAnswer.getText().toString());
                }
            });
        }

        if (mQuiz.isFinish()) { //answer textView
            TextView answerTextView = (TextView) mRootView.findViewById(R.id.answer_question);
            answerTextView.setVisibility(View.VISIBLE);
            answerTextView.setText("Answer: " + mQuestion.getAnswer());
            answerTextView.setTypeface(null, Typeface.BOLD);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
