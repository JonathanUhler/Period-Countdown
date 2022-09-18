// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// UserJson.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package user;


import java.util.Map;
import java.util.List;
import com.google.gson.annotations.SerializedName;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class UserJson
//
// Java representation of the raw user json file
//
public class UserJson {

	// +=+= CONSTANTS =+=+
	// Misc
	public static final int MAX_FIELD_LEN = 15;
	public static final String LAST_TIME = "23:59";

	// Operating system
	private static final String HOME = System.getProperty("user.home");
	private static final String OPERATING_SYSTEM = System.getProperty("os.name");
	private static final boolean IS_WIN = OPERATING_SYSTEM.startsWith("Windows");
	private static final boolean IS_LIN = OPERATING_SYSTEM.startsWith("Linux");
	private static final boolean IS_MAC = OPERATING_SYSTEM.startsWith("Mac");
	private static final boolean IS_UNIX = IS_LIN || IS_MAC;
	
	// File info
	public static final String EXPECTED_PATH = (IS_WIN) ? (HOME + "\\AppData\\PeriodCountdown\\") : // Windows
		((IS_UNIX) ? ((IS_MAC) ? (HOME + "/Library/Application Support/PeriodCountdown/") : // Mac
					  (HOME + "/.PeriodCountdown/")) : // Linux
		 (".PeriodCountdown/")); // Unknown
	public static final String INTERNAL_PATH = "assets/json/";
	public static final String DEFAULT_FILE = "User.json";
	public static final String FILE_NAME_REGEX = "[a-zA-Z0-9_/\\\\]+\\.json";
	
	// Period information
	public static final String PERIODS = "Periods";
	public static final String NAME = "Name";
	public static final String TEACHER = "Teacher";
	public static final String ROOM = "Room";

	// Settings
	public static final String SETTINGS = "Settings";
	public static final String NEXT_UP = "NextUp";
	public static final String NEXT_UP_DISABLED = "Disabled";
	public static final String NEXT_UP_ONE = "Next Class";
	public static final String NEXT_UP_ALL = "All Classes";
	public static final String THEME = "Theme";
	public static final String FONT = "Font";
	public static final String SCHOOL_JSON = "SchoolJson";
	// end: CONSTANTS
	

	// +=+= JSON DATA =+=+
	@SerializedName("Days")
	// Allows user-specific day data (like for colleges) to be in the user file instead of the school file
	public Map<String, List<Map<String, String>>> days;
	@SerializedName("Periods")
	public Map<String, Map<String, String>> periods;
	@SerializedName("Settings")
	public Map<String, String> settings;
	// end: JSON DATA 

}
// end: public class UserJson
