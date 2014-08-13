package com.quiz.php.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;

import com.quiz.php.R;
import com.quiz.php.core.Quiz;

/**
 * Created by fabricio on 1/29/14.
 */
public class QuizSummaryListActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new QuizSummaryListFragment();
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        //show dialog if not finished
        final Quiz quiz = Quiz.get(this);
        if (! quiz.isFinish()) {

            new AlertDialog.Builder(this)
                    .setTitle(R.string.close_title)
                    .setMessage(R.string.close_msg)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            quiz.setIsFinish(true);
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        } else {
            super.onBackPressed();
        }

    }

}
