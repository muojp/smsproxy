package jp.muo.smsproxy;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SmsProxyManager {
	public static final String TAG = "smsProxy";
	public enum Mode {
		SMS, CALL, BATTERY
	};

	private static final String KEY_SMS_ENABLED = "sms_enabled";
	private static final String KEY_CALL_ENABLED = "call_enabled";
	private static final String KEY_BAT_ENABLED = "bat_enabled";
	private static final String KEY_DIVIDE_MESSAGE = "divide_long_message";
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
		case BATTERY:
			return prefs.getBoolean(KEY_BAT_ENABLED, false);
		default:
			return false;
		}
	}

	public boolean isDivideEnabled() {
		if (this.ctx == null || prefs == null) {
			return false;
		}
		return prefs.getBoolean(KEY_DIVIDE_MESSAGE, false);
	}

	public String getProxyTo() {
		if (this.ctx == null || prefs == null) {
			return "";
		}
		return prefs.getString(KEY_PROXY_TO, "");
	}

	/**
	 * Sends SMS text message based on specified message types.
	 * @param smsMode message type: SMS / CALL
	 * @param msgText message body
	 * @return whether message submission succeeded or not.
	 */
	public boolean send(Mode smsMode, String msgText) {
		if (this.ctx == null || prefs == null || msgText.equals("")) {
			return false;
		}
		
		this.sendMode = smsMode;
		
		if (!isEnabled()) {
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
		case BATTERY:
			msgSucceeded = this.ctx.getString(R.string.notify_bat_ok);
			msgFailed = this.ctx.getString(R.string.notify_bat_ng);
			break;
		}

		try {
			SmsManager sms = SmsManager.getDefault();
			ArrayList<String> msgs = isDivideEnabled() ? sms.divideMessage(msgText) : new ArrayList<String>(Collections.singletonList(msgText));
			for (String msg : msgs) {
				sms.sendTextMessage(this.getProxyTo(), null, msg, null, null);
			}
			Toast.makeText(this.ctx, msgSucceeded, Toast.LENGTH_LONG).show();
			return true;
		} catch (Exception e) {
			if (e.getLocalizedMessage() != null)
			{
				System.err.println(e.getLocalizedMessage());
			}
			Toast.makeText(this.ctx, msgFailed, Toast.LENGTH_LONG).show();
			return false;
		}
	}
}
