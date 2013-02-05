package jp.muo.smsproxy;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SmsPreferenceActivity extends PreferenceActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BatteryLevelObserver.updateStatus(this.getApplicationContext());
		addPreferencesFromResource(R.xml.prefs);
	}
}
