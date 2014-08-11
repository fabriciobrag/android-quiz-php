package com.quiz.php.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import com.quiz.php.R;

/**
 * Created by fabricio on 1/29/14.
 */
public abstract class SingleFragmentActivity extends ActionBarActivity {

    public abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.container, fragment)
                    .commit();
        }

    }
}
