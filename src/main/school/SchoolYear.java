// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolYear.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package school;


import util.Log;
import util.DateTime;
import util.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class SchoolYear
//
// Slightly cleaned Java representation of the json data in the school json file. The closest
// representation of the json data would be a SchoolJson object, although that is much harder to work with
//
public class SchoolYear {

	private SchoolJson schoolJson;
	private HashMap<String, SchoolWeek> weekDefs; // Map of each unique type of week defined in the school json. The
	                                              // keys are the week type/name (e.g. DEFAULT or HBCBC) and the
	                                              // values are the generic week (with no date association) of that
	                                              // type
	private HashMap<String, SchoolWeek> year; // Map of all the weeks that overlap with the school year. The keys
	                                          // are the week tags (date of the Sunday of that week in format
	                                          // yyyy-mm-dd) and the values are that week in the year

	// Information from the "Info" section of the school json file
	private String firstPeriod;
	private String lastPeriod;
	private String firstDayTag;
	private String lastDayTag;
	

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
			else
				throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
															  "\"Days\" was not specified in school file " +
															  "and \"days\" argument is null"));
		}

		// Initialize class information
		this.initInfo();
		this.initWeekDefs();
		this.initYear();
	}


	// ====================================================================================================
	// private void initInfo
	//
	// Initializes this class with the "Info" section of the school json file
	//
	private void initInfo() throws IllegalArgumentException {
		if (!this.schoolJson.info.containsKey(SchoolJson.FIRST_PERIOD) ||
			!this.schoolJson.info.containsKey(SchoolJson.LAST_PERIOD) ||
			!this.schoolJson.info.containsKey(SchoolJson.FIRST_DAY_TAG) ||
			!this.schoolJson.info.containsKey(SchoolJson.LAST_DAY_TAG))
			throw new IllegalArgumentException("school json Info missing key");

		this.firstPeriod = this.schoolJson.info.get(SchoolJson.FIRST_PERIOD);
		this.lastPeriod = this.schoolJson.info.get(SchoolJson.LAST_PERIOD);
		this.firstDayTag = this.schoolJson.info.get(SchoolJson.FIRST_DAY_TAG);
		this.lastDayTag = this.schoolJson.info.get(SchoolJson.LAST_DAY_TAG);
	}
	// end: private void initInfo


	// ====================================================================================================
	// private void initWeekDefs
	//
	// Initializes a list of all unique week definitions from the "Weeks" section of the school json file
	//
	private void initWeekDefs() throws IllegalArgumentException {
		this.weekDefs = new HashMap<>(); // Keys are week definition names, values are the SchoolWeek objects
		
		if (!this.schoolJson.weeks.containsKey(SchoolJson.DEFAULT))
			throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
														  "json does not contain DEFAULT week"));

		for (String weekType : this.schoolJson.weeks.keySet()) {
			// For each week, create a list of the SchoolDay objects within that week
			ArrayList<SchoolDay> dayDefs = new ArrayList<>();
			List<String> dayTypes = this.schoolJson.weeks.get(weekType);
			
			if (dayTypes.size() != Duration.DAYS_PER_WEEK)
				throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
															  "week type \"" + weekType + "\" does not have 7 days"));

			for (String dayType : dayTypes) {
				// For each of the days, create a list of the SchoolPeriod objects within that day
				ArrayList<SchoolPeriod> periodDefs = new ArrayList<>();
			    List<Map<String, String>> periodsInDay = this.schoolJson.days.get(dayType);

				if (periodsInDay.size() == 0)
					throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
																  "day type \"" + dayType + "\" has 0 periods"));

				for (Map<String, String> period : periodsInDay) {
					if (!period.containsKey(SchoolJson.TYPE) ||
						!period.containsKey(SchoolJson.NAME) ||
						!period.containsKey(SchoolJson.START) ||
						!period.containsKey(SchoolJson.END))
						throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
																	  "period has missing key: " + period));

					String type = period.get(SchoolJson.TYPE);
					String name = period.get(SchoolJson.NAME);
					String start = period.get(SchoolJson.START);
					String end = period.get(SchoolJson.END);
					periodDefs.add(new SchoolPeriod(type, name, start, end));
				}

				dayDefs.add(new SchoolDay(dayType, periodDefs));
			}

			this.weekDefs.put(weekType, new SchoolWeek(weekType, dayDefs));
		}
	}
	// end: private void initWeekDefs

	
	// ====================================================================================================
	// private void initYear
	//
	// Initializes a list of every week in the year in order
	//
	private void initYear() throws IllegalArgumentException {
		this.year = new HashMap<>(); // Keys are the week tags in formation YYYY-MM-DD of the sunday for that week
		
		// The declaration of these DateTime objects without a catch for the IllegalArgumentException is fine since
		// this method can throw exceptions while parsing anyway. The intent of this class is to be initialized
		// with a catch
		DateTime firstDay = DateTime.getInstance(this.firstDayTag);
		DateTime lastDay = DateTime.getInstance(this.lastDayTag);

		DateTime current = DateTime.getInstance(firstDay.getTagForClosest(DateTime.SUNDAY));
		DateTime end = DateTime.getInstance(lastDay.getTagForClosest(DateTime.SATURDAY));

		// Loop through the bounds of the year defined in the school json file
		while (current.before(end)) {
			String weekTag = current.getWeekTag();

			// Add the default week
			if (!this.weekDefs.containsKey(SchoolJson.DEFAULT))
				throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear", "default week not available"));
			this.year.put(weekTag, this.weekDefs.get(SchoolJson.DEFAULT));

			// Check for an exception for this week
			for (Map<String, String> exception : this.schoolJson.exceptions) {
				if (!exception.containsKey(SchoolJson.TYPE) || !exception.containsKey(SchoolJson.WEEK_TAG))
					throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
																  "week exception is missing key: " + exception));

				// At this point the exception structure is valid to index
				String exceptionTag = exception.get(SchoolJson.WEEK_TAG);
				// Week tags are preferred to be used for the week exception list, but a day tag within that
				// week can also be used. The tag is converted to be a week tag with this line
				String exceptionWeekTag = DateTime.getInstance(exceptionTag).getWeekTag();
				if (weekTag.equals(exceptionWeekTag)) {
					// A match was found, now validate that the "Type" for the exception exists and add to the
					// list of SchoolWeek objects for the year
					String exceptionType = exception.get(SchoolJson.TYPE);
					if (!this.weekDefs.containsKey(exceptionType))
						throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
																	  "week exception has invalid type: " +
																	  exceptionType));

					this.year.put(weekTag, this.weekDefs.get(exceptionType));
				}
			}

			current.add(DateTime.DATE, Duration.DAYS_PER_WEEK);
		}
	}
	// end: private void initYear


	// ====================================================================================================
	// public SchoolWeek getWeek
	//
	// Returns a week given a time
	//
	// Argument--
	//
	//  when: a DateTime object representing some point within the week to get
	//
	// Returns--
	//
	//  A SchoolWeek object if one is found during the argument when, otherwise null
	//
	public SchoolWeek getWeek(DateTime when) {
		if (when == null)
			return null;
		
		String weekTag = when.getWeekTag();
		if (this.year.containsKey(weekTag))
			return this.year.get(weekTag);
		return null;
	}
	// end: public SchoolWeek getWeek


	// ====================================================================================================
	// public String toString
	//
	// Returns a string representation of the SchoolYear
	//
	// Returns--
	//
	//  A string with all the information for the school year. This is created using the toString methods
	//  for the other classes in this package. This returns a very long string, but one that is good for
	//  verbose debugging
	//
	@Override
	public String toString() {
		String str = super.toString() +
			";FirstPeriod=" + this.firstPeriod +
			",LastPeriod=" + this.lastPeriod +
			",FirstDayTag=" + this.firstDayTag +
			",LastDayTag=" + this.lastDayTag +
			",Weeks=[";

		for (String weekTag : this.year.keySet())
			str += "\n\t" + weekTag + "=" + this.year.get(weekTag).toString().replace("\n", "\n\t");

		str += "\n]";
		return str;
	}
	// end: public String toString

}
// end: public class SchoolYear
