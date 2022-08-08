// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolJson.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package school;


import java.util.Map;
import java.util.List;
import com.google.gson.annotations.SerializedName;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class SchoolJson
//
// Java representation of the entire school json file
//
public class SchoolJson {

	// +=+= CONSTANTS =+=+
	// File path
	public static final String EXPECTED_PATH = "assets/json/";
	public static final String DEFAULT_FILE = "MVHS_School.json";

	// Info
	public static final String FIRST_PERIOD = "FirstPeriod";
	public static final String LAST_PERIOD = "LastPeriod";
	public static final String FIRST_DAY_TAG = "FirstDayTag";
	public static final String LAST_DAY_TAG = "LastDayTag";

	// Days
	public static final String TYPE = "Type";
	public static final String NOTHING = "Nothing";
	public static final String SPECIAL = "Special";
	public static final String NAME = "Name";
	public static final String START = "Start";
	public static final String END = "End";

	// Weeks
	public static final String DEFAULT = "DEFAULT";

	// Exceptions
	public static final String WEEK_TAG = "WeekTag";
	// The "TYPE" keyword is also used here
	// end: CONSTANTS
	

	// +=+= JSON DATA =+=+
	@SerializedName("Info")
	public Map<String, String> info;
	@SerializedName("Days")
	public Map<String, List<Map<String, String>>> days;
	@SerializedName("Weeks")
	public Map<String, List<String>> weeks;
	@SerializedName("Exceptions")
	public List<Map<String, String>> exceptions;
	// end: JSON DATA

}
// end: public class SchoolJson
