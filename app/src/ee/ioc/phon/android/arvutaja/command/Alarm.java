package ee.ioc.phon.android.arvutaja.command;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ee.ioc.phon.android.arvutaja.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.AlarmClock;

/**
 * <p>Alarm expressions have the form:</p>
 * 
 * <pre>
 * alarm 23 : 00
 * alarm in 120 minutes
 * alarm in 3 hours and 120 minutes
 * </pre>
 *
 * @author Kaarel Kaljurand
 */
public class Alarm extends AbstractCommand {

	public static final Pattern p1 = Pattern.compile("alarm ([0-9]+) : ([0-9]+)");
	public static final Pattern p2 = Pattern.compile("alarm in ([0-9]+) minutes");
	public static final Pattern p3 = Pattern.compile("alarm in ([0-9]+) hours and ([0-9]+) minutes");

	private final String mCommand;
	private final Context mContext;

	public Alarm(String command, Context context) {
		mCommand = command;
		mContext = context;
	}

	public Intent getIntent() throws CommandParseException {
		Calendar cal;
		try {
			cal = getCalendar(mCommand);
		} catch (Exception e) {
			throw new CommandParseException();
		}

		Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
		intent.putExtra(AlarmClock.EXTRA_HOUR, cal.get(Calendar.HOUR_OF_DAY));
		intent.putExtra(AlarmClock.EXTRA_MINUTES, cal.get(Calendar.MINUTE));
		intent.putExtra(AlarmClock.EXTRA_MESSAGE, mContext.getString(R.string.alarmExtraMessage));

		// API Level 11
		// AlarmClock.EXTRA_SKIP_UI
		String skipUi = "android.intent.extra.alarm.SKIP_UI";
		intent.putExtra(skipUi, true);
		return intent;
	}


	public Uri getSuggestion() {
		return Uri.parse("https://play.google.com/store/apps/details?id=com.vp.alarmClockPlusDock");
	}


	private static Calendar getCalendar(String command) throws CommandParseException {
		Matcher m = p1.matcher(command);
		if (m.matches()) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(m.group(1)));
			cal.set(Calendar.MINUTE, Integer.parseInt(m.group(2)));
			return cal;
		}
		return getAlarmTime(command);
	}


	private static Calendar getAlarmTime(String command) throws CommandParseException {
		Calendar c = Calendar.getInstance();
		Matcher m = p2.matcher(command);
		if (m.matches()) {
			c.add(Calendar.MINUTE, Integer.parseInt(m.group(1)));
			return c;
		}
		m = p3.matcher(command);
		if (m.matches()) {
			c.add(Calendar.HOUR_OF_DAY, Integer.parseInt(m.group(1)));
			c.add(Calendar.MINUTE, Integer.parseInt(m.group(2)));
			return c;
		}
		throw new CommandParseException();
	}
}