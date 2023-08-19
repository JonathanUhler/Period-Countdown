package util;


import jnet.Log;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.DateTimeException;


/**
 * Represents a coordinated moment in time, in UTC.
 *
 * @author Jonathan Uhler
 */
public class UTCTime implements Comparable<UTCTime> {

	/** The concept of a year. */
	public static final ChronoField YEAR = ChronoField.YEAR;
	/** The concept of a month. */
	public static final ChronoField MONTH = ChronoField.MONTH_OF_YEAR;
	/** The concept of a day in a month. */
	public static final ChronoField DAY = ChronoField.DAY_OF_MONTH;
	/** The concept of a day in a week. */
	public static final ChronoField DAY_OF_WEEK = ChronoField.DAY_OF_WEEK;
	/** The concept of an hour. */
	public static final ChronoField HOUR = ChronoField.HOUR_OF_DAY;
	/** The concept of a minute. */
	public static final ChronoField MINUTE = ChronoField.MINUTE_OF_HOUR;
	/** The concept of a second. */
	public static final ChronoField SECOND = ChronoField.SECOND_OF_MINUTE;
	/** The concept of a millisecond. */
	public static final ChronoField MILLISECOND = ChronoField.MILLI_OF_SECOND;

	/** The unit of year. */
	public static final ChronoUnit YEARS = ChronoUnit.YEARS;
	/** The unit of month. */
	public static final ChronoUnit MONTHS = ChronoUnit.MONTHS;
	/** The unit of day. */
	public static final ChronoUnit DAYS = ChronoUnit.DAYS;
	/** The unit of hour. */
	public static final ChronoUnit HOURS = ChronoUnit.HOURS;
	/** The unit of minute. */
	public static final ChronoUnit MINUTES = ChronoUnit.MINUTES;
	/** The unit of second. */
	public static final ChronoUnit SECONDS = ChronoUnit.SECONDS;
	/** The unit of millisecond. */
	public static final ChronoUnit MILLISECONDS = ChronoUnit.MILLIS;

	/** The concept of sunday. */
	public static final DayOfWeek SUNDAY = DayOfWeek.SUNDAY;
	/** The concept of monday. */
	public static final DayOfWeek MONDAY = DayOfWeek.MONDAY;
	/** The concept of tuesday. */
	public static final DayOfWeek TUESDAY = DayOfWeek.TUESDAY;
	/** The concept of wednesday. */
	public static final DayOfWeek WEDNESDAY = DayOfWeek.WEDNESDAY;
	/** The concept of thrusday. */
	public static final DayOfWeek THURSDAY = DayOfWeek.THURSDAY;
	/** The concept of friday. */
	public static final DayOfWeek FRIDAY = DayOfWeek.FRIDAY;
	/** The concept of saturday. */
	public static final DayOfWeek SATURDAY = DayOfWeek.SATURDAY;


	/** The format of a full timestamp. */
	private static final DateTimeFormatter DATE_TIME_FORMAT =
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS VV");

	/** Internal representation of the time held by this UTCTime. */
	private ZonedDateTime datetime;


	/**
	 * Constructs a new {@code UTCTime} object from a {@code ZonedDateTime} object.
	 *
	 * @param datetime  a {@code ZonedDateTime} object.
	 *
	 * @throws NullPointerException  if {@code datetime == null}.
	 */
	private UTCTime(ZonedDateTime datetime) {
		if (datetime == null)
			throw new NullPointerException("datetime was null");
		this.datetime = datetime;
	}


	/**
	 * Ensures that a {@code ZonedDateTime} object is in UTC.
	 *
	 * @param local  a {@code ZonedDateTime} that may or may not be in UTC.
	 *
	 * @return a new {@code UTCTime} object representing the instant of the argument object in UTC.
	 */
	private static UTCTime ensureUTC(ZonedDateTime local) {
		ZonedDateTime utc = local.withZoneSameInstant(ZoneOffset.UTC);
		return new UTCTime(utc);
	}


	/**
	 * Gets the current time as a {@code UTCTime} object.
	 *
	 * @return the current time as a {@code UTCTime} object.
	 */
	public static UTCTime now() {
		ZonedDateTime localNow = ZonedDateTime.now();
		return UTCTime.ensureUTC(localNow);
	}


	/**
	 * Creates a new {@code UTCTime} object from a datetime string and unix TZ identifier.
	 *
	 * @param datetime  a datetime string in the format {@code yyyy-MM-dd'T'HH:mm:ss.SSS} or
	 *                  a date-only string in the format {@code yyyy-MM-dd}.
	 * @param timezone  the unix TZ identifier of a timezone.
	 *
	 * @return a new {@code UTCTime} object from a datetime string and unix TZ identifier.
	 */
	public static UTCTime of(String datetime, String timezone) throws IllegalArgumentException {
		try {
			ZonedDateTime local = ZonedDateTime.parse(datetime + " " + timezone,
													  UTCTime.DATE_TIME_FORMAT);
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
															  "UTCTime of failed for string: " +
															  datetime + ", make sure dates are " +
															  "in the format yyyy-MM-dd and " +
															  "times are in the format " +
															  "HH:mm:ss.SSS"));
			}
		}
	}


	/**
	 * Creates a new {@code UTCTime} object representing the same instant in time as this object
	 * in the specified timezone.
	 *
	 * @param timezone  the unix TZ identifier to convert this {@code UTCTime} to.
	 *
	 * @return a new {@code UTCTime} representing the same instant in time with the specified
	 *         timezone.
	 */
	public UTCTime to(String timezone) throws IllegalArgumentException {
		ZoneId zone = null;
		try {
			zone = ZoneId.of(timezone);
		}
		catch (DateTimeException e) {
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UTCTime",
														  "UTCTime to invalid timezone id: " +
														  timezone + ", " + e));
		}
		
		ZonedDateTime local = this.datetime.withZoneSameInstant(zone);
		return new UTCTime(local);
	}


	/**
	 * Returns this {@code UTCTime} object as a {@code ZonedDateTime} object.
	 *
	 * @return this {@code UTCTime} object as a {@code ZonedDateTime} object.
	 */
	public ZonedDateTime asZonedDateTime() {
		return this.datetime;
	}


	/**
	 * Returns whether this {@code UTCTime} is chronologically before the argument.
	 *
	 * @param utcTime  a {@code UTCTime} to compare to.
	 *
	 * @return whether this {@code UTCTime} is chronologically before the argument.
	 */
	public boolean isBefore(UTCTime utcTime) {
		return this.datetime.isBefore(utcTime.asZonedDateTime());
	}


	/**
	 * Returns whether this {@code UTCTime} represents the same chronological instant as the 
	 * argument.
	 *
	 * @param utcTime  a {@code UTCTime} to compare to.
	 *
	 * @return whether this {@code UTCTime} represents the same chronological instant as the 
	 *         argument.
	 */
	public boolean isEqual(UTCTime utcTime) {
		return this.datetime.isEqual(utcTime.asZonedDateTime());
	}


	/**
	 * Compares this {@code UTCTime} object to another {@code UTCTime} object.
	 *
	 * @param utcTime  the {@code UTCTime} to compare to.
	 *
	 * @return {@code 0} if the times are equal, {@code -1} if this object is before the argument,
	 *         and {@code 1} if this object is after the argument.
	 *
	 * @see isBefore
	 * @see isEqual
	 */
	@Override
	public int compareTo(UTCTime utcTime) {
		if (this.isEqual(utcTime))
			return 0;
		else if (this.isBefore(utcTime))
			return -1;
		else
			return 1;
	}


	/**
	 * Gets the value of a field in this {@code UTCTime}.
	 *
	 * @param field  the time-related field to get the value of. This should be one of the
	 *               {@code ChronoField}s of {@code UTCTime}.
	 *
	 * @return the value of a field in this {@code UTCTime}.
	 */
	public int get(ChronoField field) {
		return this.datetime.get(field);
	}


	/**
	 * Returns a new {@code UTCTime} object where the specified time-related unit has the
	 * specified amount added to the original value.
	 *
	 * @param amount  the amount to add to the specified unit.
	 * @param unit    the unit to add the specified amount to.
	 *
	 * @return a new {@code UTCTime} object where the specified time-related unit has the
	 *         specified amount added to the original value. If {@code unit == null}, then
	 *         {@code null} is returned.
	 */
	public UTCTime plus(long amount, ChronoUnit unit) {
		if (unit == null)
			return null;
		
		return new UTCTime(this.datetime.plus(amount, unit));
	}


	/**
	 * Gets the millisecond offset from the unix epoch of this {@code UTCTime}.
	 *
	 * @return the millisecond offset from the unix epoch of this {@code UTCTime}.
	 */
	public long getEpoch() {
		return this.datetime.toInstant().toEpochMilli();
	}


	/**
	 * Returns a new {@code UTCTime} object with the day value set to the closest instance of the
	 * specified day of the week.
	 *
	 * @param day  the day of the week to shift towards.
	 *
	 * @return a new {@code UTCTime} object with the day value set to the closest instance of the
	 *         specified day of the week.
	 */
	public UTCTime shiftedToClosest(DayOfWeek day) {
		if (day == null)
			return null;

		if (this.datetime.getDayOfWeek() == day)
		    return new UTCTime(this.datetime);

		ZonedDateTime closestDateTime = this.datetime.with(TemporalAdjusters.previous(day));
		UTCTime closestUTC = UTCTime.ensureUTC(closestDateTime);
		return closestUTC;
	}


	/**
	 * Returns a new {@code UTCTime} object with the same date as this object and a time of 
	 * {@code 00:00:00.000}.
	 *
	 * @return a new {@code UTCTime} object with the same date as this object and a time of 
	 *         {@code 00:00:00.000}.
	 */
	public UTCTime toMidnight() {
		ZonedDateTime midnightDateTime = this.datetime.truncatedTo(UTCTime.DAYS);
		UTCTime midnightUTC = UTCTime.ensureUTC(midnightDateTime);
		return midnightUTC;
	}


	/**
	 * Returns a new {@code UTCTime} object representing midnight in the specified timezone.
	 * <p>
	 * The returned {@code UTCTime} object is still in UTC time. The operation is order is:
	 * <ul>
	 * <li> Convert this object to the specified timezone.
	 * <li> Convert the new object (in the specified local timezone) to midnight in that tz.
	 * <li> Convert the local-midnight object back to UTC time. The result may or may not have a
	 *      final time of {@code 00:00:00.000}.
	 * </ul>
	 *
	 * @param timezone  the unix TZ identifier to get a UTC-aligned midnight time for.
	 *
	 * @return a new {@code UTCTime} object representing midnight in the specified timezone.
	 */
	public UTCTime toMidnight(String timezone) {
		UTCTime localTime = this.to(timezone);
		localTime = localTime.toMidnight();
		return localTime.to("Z");
	}


	/**
	 * Returns the day tag of this {@code UTCTime}. The day tag is in the format {@code yyyy-MM-dd}.
	 *
	 * @return the day tag of this {@code UTCTime}.
	 */
	public String getDayTag() {
		return (Tools.pad(Integer.toString(this.datetime.get(UTCTime.YEAR)), 4, '0') + "-" +
				Tools.pad(Integer.toString(this.datetime.get(UTCTime.MONTH)), 2, '0') + "-" +
				Tools.pad(Integer.toString(this.datetime.get(UTCTime.DAY)), 2, '0'));
	}


	/**
	 * Returns the day tag of the result of {@code shiftedToClosest(day)}.
	 *
	 * @param day  the day of the week to get the day tag for.
	 *
	 * @return the day tag of the result of {@code shiftedToClosest(day)}.
	 *
	 * @see shiftedToClosest
	 */
	public String getTagForClosest(DayOfWeek day) {
		return this.shiftedToClosest(day).getDayTag();
	}


	/**
	 * Returns the day tag of the closest sunday to the current time. This method is identical
	 * to {@code getTagForClosest(UTCTime.SUNDAY)}.
	 *
	 * @return the day tag of the closest sunday to the current time.
	 */
	public String getWeekTag() {
		return this.getTagForClosest(UTCTime.SUNDAY);
	}
	

	/**
	 * Returns a string representation of this {@code UTCTime} object.
	 *
	 * @return a string representation of this {@code UTCTime} object.
	 */
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
