// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// DateTime.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package util;


import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Locale;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class DateTime extends GregorianCalendar
//
// Wrapper for the Java Calendar class that includes extra utilites for constructing based on the
// information accessible from the json files and comparing with the concept of day and week tags
//
public class DateTime extends GregorianCalendar {

	public static final String DATE_DELIMITER = "-";
	// stackoverflow.com/questions/15491894/regex-to-validate-date-formats-dd-mm-yyyy-dd-mm-yyyy-dd-mm-yyyy-dd-mmm-yyyy
	public static final String DATE_FORMAT = "([0-9]{4}" + DATE_DELIMITER + "?((0[13-9]|1[012])" + DATE_DELIMITER +
		"?(0[1-9]|[12][0-9]|30)|(0[13578]|1[02])" + DATE_DELIMITER + "?31|02" + DATE_DELIMITER +
		"?(0[1-9]|1[0-9]|2[0-8]))|([0-9]{2}(([2468][048]|[02468][48])|[13579][26])|([13579][26]|[02468][048])00)" +
		DATE_DELIMITER + "?02" + DATE_DELIMITER + "?29)";
	public static final String TIME_DELIMITER = ":";
	public static final String TIME_FORMAT = "(0[0-9]|1[0-9]|2[0-3])" + TIME_DELIMITER + "[0-5][0-9]" +
		TIME_DELIMITER + "[0-5][0-9]" + TIME_DELIMITER + "[0-9][0-9][0-9]";
	public static final String SIMPLE_TIME_FORMAT = "(0[0-9]|1[0-9]|2[0-3])" + TIME_DELIMITER + "[0-5][0-9]";
	public static final String DATE_TIME_DELIMITER = "T";
	public static final String DATE_TIME_FORMAT = DATE_FORMAT + DATE_TIME_DELIMITER + TIME_FORMAT;


	// ----------------------------------------------------------------------------------------------------
	// public DateTime
	//
	public DateTime() {
		super();
	}
	// end: public DateTime


	// ----------------------------------------------------------------------------------------------------
	// public DateTime
	//
	// Overriden constructor of the GregorianCalendar class, but not used in the base version of
	// Period-Countdown
	//
	public DateTime(TimeZone zone, Locale aLocale) {
		super(zone, aLocale);
	}
	// end: public DateTime
	

	// ----------------------------------------------------------------------------------------------------
	// public DateTime
	//
	// Arguments--
	//
	//  year:   the 4-digit (at least for the next 8000 years) year
	//
	//  month:  the month of the year from 1-12
	//
	//  day:    the day of the month from 0-[28-31]
	//
	//  hour:   the hour of the day from 0-23
	//
	//  minute: the minute of the time from 0-59
	//
	//  second: the second of the time from 0-59
	//
	//  ms:     the millisecond of the time from 0-999
	//
	public DateTime(int year, int month, int day, int hour, int minute, int second, int ms) {
		super(year, month - 1, day, hour, minute, second); // -1900/-1 for weird alignments in GregorianCalendar
		this.set(DateTime.MILLISECOND, ms);
		this.set(DateTime.ZONE_OFFSET, this.getTimeZone().getRawOffset());
		this.set(DateTime.DST_OFFSET, this.getTimeZone().getDSTSavings());
	}
	// end: public DateTime


	// ====================================================================================================
	// public static DateTime getInstance
	//
	// Factory-style constructor. Constructs and returns a DateTime object based on a string tag in one
	// of two forms.
	//
	// Arguments--
	//
	//  tag: the string date/time to build a DateTime object from. Must be in one of the following formats
	//       and must be a valid date and time (including leap years)
	//       - YYYY-MM-DD, or
	//       - YYYY-MM-DDTHH:MM:SS:mmm, where
	//
	//       - Y = year
	//       - M = month, minute
	//       - D = day
	//       - H = hour
	//       - m = millisecond
	//       - "-" = literal char
	//       - "T" = literal char
	//       - ":" = literal char
	//
	// Returns--
	//
	//  A DateTime object if one could be constructed. Throws an exception if the argument tag is invalid
	//
	public static DateTime getInstance(String tag) throws IllegalArgumentException {
		if (!tag.matches(DateTime.DATE_TIME_FORMAT) &&
			!tag.matches(DateTime.DATE_FORMAT))
			throw new IllegalArgumentException(Log.format(Log.ERROR, "DateTime",
														  "getInstance called with invalid tag: " + tag));
		
		// Note: Parsing as integers is safe after a match has been confirmed. The regex check confirms the
		//       sections are valid integers in range

		int year = new DateTime().get(DateTime.YEAR);
		int month = new DateTime().get(DateTime.MONTH) + 1;
		int day = new DateTime().get(DateTime.DAY_OF_MONTH);
		int hour = 0;
		int minute = 0;
		int second = 0;
		int ms = 0;

		String dateTag = tag;
		String timeTag = tag;
		// If the argument matches the whole format, split into the date and time tags individually
		if (tag.matches(DateTime.DATE_TIME_FORMAT)) {
			String[] dateTimeTagSplit = tag.split(DateTime.DATE_TIME_DELIMITER);
			dateTag = dateTimeTagSplit[0];
			timeTag = dateTimeTagSplit[1];
		}

		// Process the date and time components separately
		if (dateTag.matches(DateTime.DATE_FORMAT)) {
			String[] dateTagSplit = dateTag.split(DateTime.DATE_DELIMITER);
			year = Integer.parseInt(dateTagSplit[0]);
			month = Integer.parseInt(dateTagSplit[1]);
			day = Integer.parseInt(dateTagSplit[2]);
		}
		if (timeTag.matches(DateTime.TIME_FORMAT)) {
			String[] timeTagSplit = timeTag.split(DateTime.TIME_DELIMITER);
			hour = Integer.parseInt(timeTagSplit[0]);
			minute = Integer.parseInt(timeTagSplit[1]);
			second = Integer.parseInt(timeTagSplit[2]);
			ms = Integer.parseInt(timeTagSplit[3]);
		}

		return new DateTime(year, month, day, hour, minute, second, ms);
	}
	// end: public static DateTime getInstance


	// ====================================================================================================
	// GET methods
	public long getEpoch() {
		return this.getTimeInMillis();
	}


	public int getDayIndex() {
		return this.get(DateTime.DAY_OF_WEEK) - 1; // Days Sun-Sat are 1 aligned, so subtract 1 to get an index
	}

	public String getDayTag() {
		int year = this.get(DateTime.YEAR);
		int month = this.get(DateTime.MONTH) + 1; // MONTH is 0 aligned
		int day = this.get(DateTime.DATE); // "DATE" is the day of month (0~31). "DAY_OF_MONTH" also works

	    return Tools.pad(Integer.toString(year), 4, '0') + DateTime.DATE_DELIMITER +
			Tools.pad(Integer.toString(month), 2, '0') + DateTime.DATE_DELIMITER +
			Tools.pad(Integer.toString(day), 2, '0');
	}


	public String getTagForClosest(int day) {
		if (day < DateTime.SUNDAY || day > DateTime.SATURDAY) {
			Log.gfxmsg("Internal Error", "DateTime.getTagForClosest called with invalid day\nday: " + day);
			return null;
		}

		DateTime newTime = new DateTime();
		newTime.setEpoch(this.getEpoch());

		int current = this.get(DateTime.DATE);
		int closestDay = this.get(DateTime.DAY_OF_WEEK) - day; // If this goes negative it will negate in the next line
		int closestDate = current - closestDay;
		newTime.set(DateTime.DATE, closestDate);

		return newTime.getDayTag();
	}


	public String getWeekTag() {
		return this.getTagForClosest(DateTime.SUNDAY);
	}
	// end: GET methods


	// ====================================================================================================
	// SET methods
	public void setEpoch(long epoch) {
		this.setTimeInMillis(epoch);
	}


	public void setToMidnight() {
		this.set(DateTime.AM_PM, DateTime.AM);
		this.set(DateTime.HOUR, 0);
		this.set(DateTime.HOUR_OF_DAY, 0);
		this.set(DateTime.MINUTE, 0);
		this.set(DateTime.SECOND, 0);
		this.set(DateTime.MILLISECOND, 0);
	}
	
	@Override
	public void add(int field, int amount) {
		if (field < 0 || field >= FIELD_COUNT) {
			Log.gfxmsg("Internal Error", "DateTime.add called with invalid field\nfield: " + field);
			return;
		}
		
		int prevValue = this.get(field);
		this.set(field, prevValue + amount);
	}
	// end: SET methods
	
}
// end: public class DateTime
