package com.sollyu.android.appenv.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.sollyu.android.appenv.R;

/**
 * 作者: Sollyu
 * 时间: 16/11/2
 * 联系: sollyu@qq.com
 * 说明:
 */
public class SettingsActivity extends AppCompatActivity {
    private boolean isChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment(this)).commit();
    }


    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        SettingsActivity.this.setResult(isChanged ? 1 : 0);
        SettingsActivity.this.finish();

        super.onBackPressed();
    }

    public static class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

        public PrefsFragment() {
        }


        @SuppressLint("ValidFragment")
        private PrefsFragment(SettingsActivity settingsActivity) {
            this.settingsActivity = settingsActivity;
        }

        private SettingsActivity settingsActivity = null;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.content_settings);

            findPreference("show_system_app").setOnPreferenceChangeListener(this);
            findPreference("about").setOnPreferenceClickListener(this);
        }

        /**
         * Called when a Preference has been clicked.
         *
         * @param preference The Preference that was clicked.
         * @return True if the click was handled.
         */
        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()) {
                case "about":
                    startActivity(new Intent(settingsActivity, AboutActivity.class));
                    break;
            }
            return false;
        }

        /**
         * Called when a Preference has been changed by the user. This is
         * called before the state of the Preference is about to be updated and
         * before the state is persisted.
         *
         * @param preference The changed Preference.
         * @param newValue   The new value of the Preference.
         * @return True to update the state of the Preference with the new value.
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            settingsActivity.isChanged = !settingsActivity.isChanged;
            return true;
        }
    }

}
