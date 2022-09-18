// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolYear.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package school;


import util.Log;
import util.Interval;
import util.UTCTime;
import util.Duration;
import user.UserJson;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class SchoolYear
//
// Holds all the SchoolPeriod objects for the entire year, as constructed from the school json file
//
public class SchoolYear {

	private SchoolJson schoolJson;
	private ArrayList<SchoolPeriod> year;

	// Information from the "Info" section of the school json file
	private String firstPeriod;
	private String lastPeriod;
	private String firstDayTag;
	private String lastDayTag;
	private String timezone;


    // ----------------------------------------------------------------------------------------------------
	// public SchoolYear
	//
	// Throws exceptions upon invalid json file or json data
	//
	// Arguments--
	//
	//  jsonPath: the path of the school json file as a jar resource
	//
	//  days:     a parameter made optional by the SchoolAPI constructors. Allows the "Days" field of
	//            the school json file to come from another source. However, if "Days" is present in the
	//            school json file, that is always used first
	//
	public SchoolYear(String jsonPath, Map<String, List<Map<String, String>>> days) throws FileNotFoundException,
																						   IllegalArgumentException
	{
		// Init objects
		this.year = new ArrayList<>();
		
		// Read the json file as a jar resource stream
		InputStream schoolStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(jsonPath);
		if (schoolStream == null)
			throw new FileNotFoundException(Log.format(Log.ERROR, "SchoolYear",
													   "json resource \"" + jsonPath + "\" is null"));
		
		InputStreamReader schoolReader = new InputStreamReader(schoolStream);

		// Load json file with GSON as a SchoolJson object
		Gson gson = new Gson();
		try {
			this.schoolJson = gson.fromJson(schoolReader, SchoolJson.class);
		}
		catch (JsonSyntaxException e) {
			throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear", "json cannot be parsed: " + e));
		}

		// Determine if the days argument should be used
		if (this.schoolJson.days == null) {
			if (days != null)
				this.schoolJson.days = days;
			else {
				throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
															  "\"Days\" was not specified in school file " +
															  "and \"days\" argument is null"));
			}
		}

		// Initialize class information
		this.initInfo();
		this.initYear();
	}
	// end: public SchoolYear


	// ====================================================================================================
	// private void initInfo
	//
	// Initializes this class with the "Info" section of the school json file. Only does basic checks
    // for valid data and sets instance variables
	//
	private void initInfo() throws IllegalArgumentException {
		if (!this.schoolJson.info.containsKey(SchoolJson.FIRST_PERIOD) ||
			!this.schoolJson.info.containsKey(SchoolJson.LAST_PERIOD) ||
			!this.schoolJson.info.containsKey(SchoolJson.FIRST_DAY_TAG) ||
			!this.schoolJson.info.containsKey(SchoolJson.LAST_DAY_TAG) ||
			!this.schoolJson.info.containsKey(SchoolJson.TIMEZONE))
			throw new IllegalArgumentException("school json Info missing key. Required keys are:\n" +
											   "- " + SchoolJson.FIRST_PERIOD + "\n" +
											   "- " + SchoolJson.LAST_PERIOD + "\n" +
											   "- " + SchoolJson.FIRST_DAY_TAG + "\n" +
											   "- " + SchoolJson.LAST_DAY_TAG + "\n" +
											   "- " + SchoolJson.TIMEZONE);

		this.firstPeriod = this.schoolJson.info.get(SchoolJson.FIRST_PERIOD);
		this.lastPeriod = this.schoolJson.info.get(SchoolJson.LAST_PERIOD);
		this.firstDayTag = this.schoolJson.info.get(SchoolJson.FIRST_DAY_TAG);
		this.lastDayTag = this.schoolJson.info.get(SchoolJson.LAST_DAY_TAG);
		this.timezone = this.schoolJson.info.get(SchoolJson.TIMEZONE);
	}
	// end: private void initInfo


    // ====================================================================================================
    // private void initYear
    //
	// Initializes the this.year structure
	//
    private void initYear() throws IllegalArgumentException {
		UTCTime firstDay = UTCTime.of(this.firstDayTag, this.timezone);
		UTCTime lastDay = UTCTime.of(this.lastDayTag, this.timezone);

		UTCTime current = firstDay.shiftedToClosest(UTCTime.SUNDAY);
		UTCTime end = lastDay.shiftedToClosest(UTCTime.SATURDAY);

		while (current.isBefore(end)) {
			// Search for a week exception in order to get the correct week structure
			String weekTag = current.getWeekTag();
			String weekType = SchoolJson.DEFAULT;
			for (Map<String, String> exception : this.schoolJson.exceptions) {
				if (!exception.containsKey(SchoolJson.TYPE) || !exception.containsKey(SchoolJson.WEEK_TAG))
					throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
																  "week exception is missing key: " + exception));

				// At this point the exception structure is valid to index
				String exceptionTag = exception.get(SchoolJson.WEEK_TAG);
				// Week tags are preferred to be used for the week exception list, but a day tag within that
				// week can also be used. The tag is converted to be a week tag with this line
				String exceptionWeekTag = UTCTime.of(exceptionTag, this.timezone).getWeekTag();
				if (weekTag.equals(exceptionWeekTag)) {
					weekType = exception.get(SchoolJson.TYPE);
					break;
				}
			}

			// At this point, we have either "DEFAULT" or the name of a special week type in the weekType variables.
			// We want to search for that week type, then go through each of the day types in that week, and finally
			// each of the period definitions for those days. This should create all the periods for a given week
			if (!this.schoolJson.weeks.containsKey(weekType))
				throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
															  "json does not contain week type def: " + weekType));

			List<String> dayTypes = this.schoolJson.weeks.get(weekType);

			if (dayTypes.size() != Duration.DAYS_PER_WEEK)
				throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
															  "week \"" + weekType + "\" does not have 7 days"));

			for (String dayType : dayTypes) {
				if (!this.schoolJson.days.containsKey(dayType))
					throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
																  "json does not contain day type def: " + dayType));

				// Get the list of period definitions for this day. Then loop through each of the periods and
				// create a SchoolPeriod object to add to the year
				List<Map<String, String>> periodDefs = this.schoolJson.days.get(dayType);

				if (periodDefs.size() == 0)
					throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
																  "day \"" + dayType + "\" has 0 periods"));

				UTCTime previousEndTime = null;

				for (Map<String, String> periodDef : periodDefs) {
					if (!periodDef.containsKey(SchoolJson.TYPE) ||
						!periodDef.containsKey(SchoolJson.NAME) ||
						!periodDef.containsKey(SchoolJson.START) ||
						!periodDef.containsKey(SchoolJson.END))
						throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
																	  "period has missing key: " + periodDef));

					String type = periodDef.get(SchoolJson.TYPE);
					String name = periodDef.get(SchoolJson.NAME);
					String startStr = periodDef.get(SchoolJson.START);
					String endStr = periodDef.get(SchoolJson.END);

					// Convert the "current" time to the timezone specified by the json. Use the day tag of
					// that local object to create the local strings for the start/end. Then construct
					// UTCTime objects for the start/end in UTC
					UTCTime localDate = current.to(this.timezone);
					UTCTime startTime = UTCTime.of(localDate.getDayTag() + "T" + startStr + ":00.000", this.timezone);
					UTCTime endTime = null;
					if (!endStr.equals(UserJson.LAST_TIME)) {
						endTime = UTCTime.of(localDate.getDayTag() + "T" + endStr + ":00.999", this.timezone);
						endTime = endTime.plus(-1, UTCTime.SECONDS);
					}
					else {
						endTime = UTCTime.of(localDate.plus(1, UTCTime.DAYS).getDayTag() +
											 "T00:00:00.000", this.timezone);
						endTime = endTime.plus(-1, UTCTime.MILLISECONDS);
					}

					// Add 1 ms to shift from **:**:59.999 to **:**:00.000, which should be the start time of the
					// current period. If that shift from the previous end time does not yield the current period
					// there is a discontinuity of >1 ms which is illegal
					if (previousEndTime != null &&
						!(previousEndTime.plus(1, UTCTime.MILLISECONDS)).isEqual(startTime))
						throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
																	  "previous end + 1ms != next start: " +
																	  previousEndTime + ", " + startTime));
					previousEndTime = endTime;
					
					this.year.add(new SchoolPeriod(type, name, startTime, endTime, endStr.equals(UserJson.LAST_TIME)));
				}

				// Go to the next day
				current = current.plus(1, UTCTime.DAYS);
			}
		}
	}
	// end: private void initYear


	// ====================================================================================================
	// private int periodSearch
	//
	// Binary search implementation to find the period that occupies the target "now" time
	//
	// Arguments--
	//
	//  now: the target time to find a period for
	//
	//  min: the minimum index to search
	//
	//  max: the maximum index to search
	//
	// Returns--
	//
	//  The index of the element, or -1 if no such element is found
	//
	private int periodSearch(UTCTime now, int min, int max) {
		if (min <= max) {
			int middle = (min + max) / 2;

			SchoolPeriod period = this.year.get(middle);
			UTCTime start = period.getStart();
			UTCTime end = period.getEnd();

			if (start.compareTo(now) <= 0 && now.compareTo(end) <= 0) // period is during now
				return middle;
			else if (start.compareTo(now) > 0) // period is after now
				return this.periodSearch(now, min, middle - 1);
			else
				return this.periodSearch(now, middle + 1, max); // period is before now
		}

		return -1;
	}
	// end: private int periodSearch


	// ====================================================================================================
	// public SchoolPeriod getPeriod
	//
	// Gets a period from the year structure at a given time
	//
	// Arguments--
	//
	//  now: the time to get a period for, not null
	//
	// Returns--
	//
	//  The period if one could be found, otherwise null
	//
	public SchoolPeriod getPeriod(UTCTime now) {
		if (now == null)
			return null;
		
		int index = this.periodSearch(now, 0, this.year.size());

		if (index == -1 || index >= this.year.size())
			return null;
		else
			return this.year.get(index);
	}
	// end: public SchoolPeriod getPeriod


	// ====================================================================================================
	// public String getTimezone
	//
	// Returns--
	//
	//  The timezone ID string for the json file
	//
	public String getTimezone() {
		return this.timezone;
	}
	// end: public String getTimezone


	// ====================================================================================================
	// public String toString
	//
	// Returns--
	//
	//  A string representation of this object
	//
	@Override
	public String toString() {
		String str = "";

		for (SchoolPeriod period : this.year) {
			str += period + "\n";

			if (period.isLast())
				str += "\n";
		}

		return str;
	}
	// end: public String toString

}
// end: public class SchoolYear
