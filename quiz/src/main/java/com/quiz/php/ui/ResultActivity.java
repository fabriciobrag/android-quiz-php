package com.quiz.php.ui;

import android.support.v4.app.Fragment;

/**
 * Created by fabricio on 2/11/14.
 */
public class ResultActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new ResultFragment();
    }
}
