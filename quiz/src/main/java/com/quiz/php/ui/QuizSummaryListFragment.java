package com.quiz.php.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.quiz.php.R;
import com.quiz.php.core.Question;
import com.quiz.php.core.Quiz;

import java.util.ArrayList;

/**
 * Created by fabricio on 1/29/14.
 */
public class QuizSummaryListFragment extends ListFragment {

    public ArrayList<Question> mQuestions;
    private Quiz mQuiz;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mQuiz = Quiz.get(getActivity());

        mQuestions = mQuiz.getQuestions();
        //custom adapter
        QuestionListAdapter adapter = new QuestionListAdapter(mQuestions);
        setListAdapter(adapter);
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(android.os.Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setSelector(R.drawable.styled_activated_background_holo_light);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //start question pager
        Intent intent = new Intent(getActivity(), QuestionPagerActivity.class);
        intent.putExtra(QuestionFragment.EXTRA_QUESTION_INDEX, position);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((QuestionListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    //Custom adapter
    private class QuestionListAdapter extends ArrayAdapter<Question> {

        public QuestionListAdapter(ArrayList<Question> questions) {
            super(getActivity(), 0, questions);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.quiz_adapter_list, null);
//            }

            getListView().setSelector(R.drawable.styled_activated_background_holo_light);

            TextView numTxtView = (TextView) convertView.findViewById(R.id.block_num_question);
            if (numTxtView != null) {
                numTxtView.setText(String.format("#%s",position + 1));
            }
            TextView titleTxtView = (TextView) convertView.findViewById(R.id.block_title_question);
            Question q = getItem(position);
            titleTxtView.setText(q.getQuestion());

            LinearLayout questionNum = (LinearLayout) convertView.findViewById(R.id.question_num);

            if (q.isAnswered()) {
                questionNum.setBackgroundResource(R.color.styled_color);
            } else {
                questionNum.setBackgroundColor(Color.LTGRAY);
            }

            if (mQuiz.isFinish()) {

                if (q.isCorrect()) {
                    questionNum.setBackgroundResource(R.color.bg_correct);
                } else {
                    questionNum.setBackgroundResource(R.color.list_num_question_wrong);
                }
            }


            return convertView;
        }

        @Override
        public Question getItem(int position) {
            return super.getItem(position);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.summary, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_finish:
                resultActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void resultActivity() {
        Intent i = new Intent(getActivity(), ResultActivity.class);
        startActivity(i);

        //finish quiz
        mQuiz.setIsFinish(true);
    }
}