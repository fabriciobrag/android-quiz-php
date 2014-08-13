package com.quiz.php.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.quiz.php.R;

/**
 * Created by fabricio on 4/30/14.
 */
public class TypeListPreference extends ListPreference {

    private Context mContext;

    public TypeListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        setSummary(getFormattedSummary(prefs.getString(SettingsActivity.PREF_TYPE, "0")));

        setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference pref, Object value) {
                pref.setSummary(getFormattedSummary(value.toString()));
                return true;
            }
        });
    }

    private String getFormattedSummary(String type) {
        String[] arrayTypes = mContext.getResources().getStringArray(R.array.entries_type_preference);

        return arrayTypes[Integer.parseInt(type)];
    }

    // NOTE:
    // The framework forgot to call notifyChanged() in setValue() on previous versions of android.
    // This bug has been fixed in android-4.4_r0.7.
    // Commit: platform/frameworks/base/+/94c02a1a1a6d7e6900e5a459e9cc699b9510e5a2
    // Time: Tue Jul 23 14:43:37 2013 -0700
    //
    // However on previous versions, we have to workaround it by ourselves.
    @Override
    public void setValue(String value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            super.setValue(value);
        } else {
            String oldValue = getValue();
            super.setValue(value);
            if (!TextUtils.equals(value, oldValue)) {
                notifyChanged();
            }
        }
    }
}
