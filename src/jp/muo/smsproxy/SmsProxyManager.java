package jp.muo.smsproxy;

import jp.muo.smsproxy.SmsProxyManager.Mode;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SmsProxyManager {
	public enum Mode {
		SMS, CALL
	};

	private static final String KEY_SMS_ENABLED = "sms_enabled";
	private static final String KEY_CALL_ENABLED = "call_enabled";
	private static final String KEY_PROXY_TO = "proxyTo";
	private Context ctx = null;
	private SharedPreferences prefs = null;
	private Mode sendMode = Mode.SMS;

	public SmsProxyManager(Context ctx) {
		this.ctx = ctx;
		prefs = PreferenceManager.getDefaultSharedPreferences(this.ctx);
	}

	public boolean isEnabled() {
		if (this.ctx == null || prefs == null) {
			return false;
		}
		switch (this.sendMode) {
		case SMS:
			return prefs.getBoolean(KEY_SMS_ENABLED, false);
		case CALL:
			return prefs.getBoolean(KEY_CALL_ENABLED, false);
		default:
			return false;
		}
	}

	public String getProxyTo() {
		if (this.ctx == null || prefs == null) {
			return "";
		}
		return prefs.getString(KEY_PROXY_TO, "");
	}

	public boolean send(String msgText) {
		if (this.ctx == null || prefs == null) {
			return false;
		}

		if (msgText.equals("")) {
			return false;
		}

		String msgSucceeded = "";
		String msgFailed = "";
		switch (this.sendMode) {
		case SMS:
			msgSucceeded = this.ctx.getString(R.string.forward_sms_ok);
			msgFailed = this.ctx.getString(R.string.forward_sms_ng);
			break;
		case CALL:
			msgSucceeded = this.ctx.getString(R.string.forward_call_ok);
			msgFailed = this.ctx.getString(R.string.forward_call_ng);
			break;
		}

		try {
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(this.getProxyTo(), null, msgText, null, null);
			Toast.makeText(this.ctx, msgSucceeded, Toast.LENGTH_LONG).show();
			return true;
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			Toast.makeText(this.ctx, msgFailed, Toast.LENGTH_LONG).show();
			return false;
		}
	}

	public void setType(Mode call) {
		this.sendMode = call;
	}
}
