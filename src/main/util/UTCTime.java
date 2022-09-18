package util;


import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.DateTimeException;


public class UTCTime {

	// Implement the constants of ChronoField so they can be used with only the import of UTCTime
	public static final ChronoField YEAR = ChronoField.YEAR;
	public static final ChronoField MONTH = ChronoField.MONTH_OF_YEAR;
	public static final ChronoField DAY = ChronoField.DAY_OF_MONTH;
	public static final ChronoField DAY_OF_WEEK = ChronoField.DAY_OF_WEEK;
	public static final ChronoField HOUR = ChronoField.HOUR_OF_DAY;
	public static final ChronoField MINUTE = ChronoField.MINUTE_OF_HOUR;
	public static final ChronoField SECOND = ChronoField.SECOND_OF_MINUTE;
	public static final ChronoField MILLISECOND = ChronoField.MILLI_OF_SECOND;

	public static final ChronoUnit YEARS = ChronoUnit.YEARS;
	public static final ChronoUnit MONTHS = ChronoUnit.MONTHS;
	public static final ChronoUnit DAYS = ChronoUnit.DAYS;
	public static final ChronoUnit HOURS = ChronoUnit.HOURS;
	public static final ChronoUnit MINUTES = ChronoUnit.MINUTES;
	public static final ChronoUnit SECONDS = ChronoUnit.SECONDS;
	public static final ChronoUnit MILLISECONDS = ChronoUnit.MILLIS;

	public static final DayOfWeek SUNDAY = DayOfWeek.SUNDAY;
	public static final DayOfWeek MONDAY = DayOfWeek.MONDAY;
	public static final DayOfWeek TUESDAY = DayOfWeek.TUESDAY;
	public static final DayOfWeek WEDNESDAY = DayOfWeek.WEDNESDAY;
	public static final DayOfWeek THURSDAY = DayOfWeek.THURSDAY;
	public static final DayOfWeek FRIDAY = DayOfWeek.FRIDAY;
	public static final DayOfWeek SATURDAY = DayOfWeek.SATURDAY;


	private static final DateTimeFormatter DATE_TIME_FORMAT =
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS VV");

	private ZonedDateTime datetime; // Internal representation of the time held by this UTCTime


	private UTCTime(ZonedDateTime datetime) {
		this.datetime = datetime;
	}


	private static UTCTime ensureUTC(ZonedDateTime local) {
		ZonedDateTime utc = local.withZoneSameInstant(ZoneOffset.UTC);
		return new UTCTime(utc);
	}


	public static UTCTime now() {
		ZonedDateTime localNow = ZonedDateTime.now();
		return UTCTime.ensureUTC(localNow);
	}


	public static UTCTime of(String datetime, String timezone) throws IllegalArgumentException {
		try {
			ZonedDateTime local = ZonedDateTime.parse(datetime + " " + timezone, UTCTime.DATE_TIME_FORMAT);
			return UTCTime.ensureUTC(local);
		}
		catch (DateTimeException e) {
			try {
				// Allow construction from just a date yyyy-MM-dd
				ZonedDateTime local = ZonedDateTime.parse(datetime + "T00:00:00.000 " + timezone,
														  UTCTime.DATE_TIME_FORMAT);
				return UTCTime.ensureUTC(local);
			}
			catch (DateTimeException e2) {
				throw new IllegalArgumentException(Log.format(Log.ERROR, "UTCTime",
															  "UTCTime of failed for string: " + datetime +
															  ", make sure dates are in the format yyyy-MM-dd " +
															  "and times are in the format HH:mm:ss.SSS"));
			}
		}
	}


	public UTCTime to(String timezone) throws IllegalArgumentException {
		ZoneId zone = null;
		try {
			zone = ZoneId.of(timezone);
		}
		catch (DateTimeException e) {
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UTCTime",
														  "UTCTime to invalid timezone id: " + timezone + ", " + e));
		}
		
		ZonedDateTime local = this.datetime.withZoneSameInstant(zone);
		return new UTCTime(local);
	}


	public ZonedDateTime asZonedDateTime() {
		return this.datetime;
	}


	public boolean isBefore(UTCTime utcTime) {
		return this.datetime.isBefore(utcTime.asZonedDateTime());
	}


	public boolean isEqual(UTCTime utcTime) {
		return this.datetime.isEqual(utcTime.asZonedDateTime());
	}


	public int compareTo(UTCTime utcTime) {
		if (this.isEqual(utcTime))
			return 0;
		else if (this.isBefore(utcTime))
			return -1;
		else
			return 1;
	}


	public int get(ChronoField field) {
		return this.datetime.get(field);
	}


	public UTCTime plus(long amount, ChronoUnit unit) {
		if (unit == null)
			return null;
		
		return new UTCTime(this.datetime.plus(amount, unit));
	}


	public long getEpoch() {
		return this.datetime.toInstant().toEpochMilli();
	}


	public UTCTime shiftedToClosest(DayOfWeek day) {
		if (day == null)
			return null;

		if (this.datetime.getDayOfWeek() == day)
		    return new UTCTime(this.datetime);

		ZonedDateTime closestDateTime = this.datetime.with(TemporalAdjusters.previous(day));
		UTCTime closestUTC = UTCTime.ensureUTC(closestDateTime);
		return closestUTC;
	}


	public UTCTime toMidnight() {
		ZonedDateTime midnightDateTime = this.datetime.truncatedTo(UTCTime.DAYS);
		UTCTime midnightUTC = UTCTime.ensureUTC(midnightDateTime);
		return midnightUTC;
	}


	public String getDayTag() {
		return (Tools.pad(Integer.toString(this.datetime.get(UTCTime.YEAR)), 4, '0') + "-" +
				Tools.pad(Integer.toString(this.datetime.get(UTCTime.MONTH)), 2, '0') + "-" +
				Tools.pad(Integer.toString(this.datetime.get(UTCTime.DAY)), 2, '0'));
	}


	public String getTagForClosest(DayOfWeek day) {
		if (day == null)
			return null;
		
		if (this.datetime.getDayOfWeek() == day)
		    return this.getDayTag();

		ZonedDateTime closestDateTime = this.datetime.with(TemporalAdjusters.previous(day));
		UTCTime closestUTC = UTCTime.ensureUTC(closestDateTime); // closestDateTime should be UTC, but make sure anyway
		
		// closestUTC is "this" aligned to the nearest Sunday, so the day tag of closestUTC is the same
		// as the week tag of "this"
		return closestUTC.getDayTag();
	}


	public String getWeekTag() {
		return this.getTagForClosest(UTCTime.SUNDAY);
	}
	

	@Override
	public String toString() {
		return (Tools.pad(Integer.toString(this.datetime.get(UTCTime.YEAR)), 4, '0') + "-" +
				Tools.pad(Integer.toString(this.datetime.get(UTCTime.MONTH)), 2, '0') + "-" +
				Tools.pad(Integer.toString(this.datetime.get(UTCTime.DAY)), 2, '0') + "T" +
				Tools.pad(Integer.toString(this.datetime.get(UTCTime.HOUR)), 2, '0') + ":" +
				Tools.pad(Integer.toString(this.datetime.get(UTCTime.MINUTE)), 2, '0') + ":" +
				Tools.pad(Integer.toString(this.datetime.get(UTCTime.SECOND)), 2, '0') + "." +
				Tools.pad(Integer.toString(this.datetime.get(UTCTime.MILLISECOND)), 3, '0') + " " +
				this.datetime.getZone());
	}

}
