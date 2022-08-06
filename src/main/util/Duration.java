package util;


public class Duration {

	public static final int DAYS_PER_YEAR = 365;
	public static final int DAYS_PER_WEEK = 7;
	public static final int HOURS_PER_DAY = 24;
	public static final int MINUTES_PER_HOUR = 60;
	public static final int SECONDS_PER_MINUTE = 60;
	public static final int MS_PER_SECOND = 1000;
	public static final int MS_PER_MINUTE = MS_PER_SECOND * SECONDS_PER_MINUTE;
	public static final int MS_PER_HOUR = MS_PER_MINUTE * MINUTES_PER_HOUR;
	

	private int hours;
	private int minutes;
	private int seconds;
	private int millis;


	public Duration(DateTime start, DateTime end) {
		long startEpoch = start.getEpoch();
		long endEpoch = end.getEpoch();
		long deltaEpoch = endEpoch - startEpoch;

		this.hours = 0;
		this.minutes = 0;
		this.seconds = 0;
		this.millis = 0;

		if (deltaEpoch > 0) {
			this.hours = (int) (deltaEpoch / Duration.MS_PER_HOUR);
			deltaEpoch -= this.hours * Duration.MS_PER_HOUR;

			this.minutes = (int) (deltaEpoch / Duration.MS_PER_MINUTE);
			deltaEpoch -= this.minutes * Duration.MS_PER_MINUTE;

			this.seconds = (int) (deltaEpoch / Duration.MS_PER_SECOND);
			deltaEpoch -= this.seconds * Duration.MS_PER_SECOND;

			this.millis = (int) (deltaEpoch);
		}
	}


	public Duration(int hours, int minutes, int seconds, int millis) {
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		this.millis = millis;
	}


	public int hr() {
		return this.hours;
	}


	public int min() {
		return this.minutes;
	}


	public int sec() {
		return this.seconds;
	}


	public int ms() {
		return this.millis;
	}


	@Override
	public String toString() {
		if (this.hours == 0) {
			if (this.minutes == 0)
				return Tools.pad(Integer.toString(this.seconds), 2, '0');
			return Tools.pad(Integer.toString(this.minutes), 2, '0') + ":" +
				Tools.pad(Integer.toString(this.seconds), 2, '0');
		}
		return this.hours + ":" +
			Tools.pad(Integer.toString(this.minutes), 2, '0') + ":" +
			Tools.pad(Integer.toString(this.seconds), 2, '0');
	}

}
