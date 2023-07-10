package user;


import java.util.Map;
import java.util.List;
import com.google.gson.annotations.SerializedName;


/**
 * Defines the object representation for the user json file as well as important constants.
 *
 * @author Jonathan Uhler
 */
public class UserJson {

	/** The maximum size of a value entered by the user. */
	public static final int MAX_FIELD_LEN = 15;
	/** The last timestamp of the day that can be defined in the user json file. */
	public static final String LAST_TIME = "23:59";

	/** The user's home directory. */
	private static final String HOME = System.getProperty("user.home");
	/** The user's operating system. */
	private static final String OPERATING_SYSTEM = System.getProperty("os.name");
	/** Whether the OS is windows. */
	private static final boolean IS_WIN = OPERATING_SYSTEM.startsWith("Windows");
	/** Whether the OS is a linux distro. */
	private static final boolean IS_LIN = OPERATING_SYSTEM.startsWith("Linux");
	/** Whether the OS is mac OSX. */
	private static final boolean IS_MAC = OPERATING_SYSTEM.startsWith("Mac");
	/** Whether the OS is unix-based. */
	private static final boolean IS_UNIX = IS_LIN || IS_MAC;
	
	/** The expected path to the Period Countdown directory. */
	public static final String EXPECTED_PATH =
		(IS_WIN) ? (HOME + "\\AppData\\PeriodCountdown\\") : // Windows
		((IS_UNIX) ? ((IS_MAC) ? (HOME + "/Library/Application Support/PeriodCountdown/") : // Mac
					  (HOME + "/.PeriodCountdown/")) : // Linux
		 (".PeriodCountdown/")); // Unknown
	/** The path to the default user json file as a jar artifact. */
	public static final String INTERNAL_PATH = "assets/json/";
	/** The name of the user json file. */
	public static final String DEFAULT_FILE = "User.json";
	/** Regex for a valid json file. */
	public static final String FILE_NAME_REGEX = "[a-zA-Z0-9_/\\\\]+\\.json";
	
	/** Json key for the list of schools. */
	public static final String SCHOOLS = "Schools";
	/** Json key for the list of periods for a school. */
	public static final String PERIODS = "Periods";
	/** Json key for the definition of each day structure. */
	public static final String DAYS = "Days";
	/** Json key for the name of a period. */
	public static final String NAME = "Name";
	/** Json key for the teacher's name for a given period. */
	public static final String TEACHER = "Teacher";
	/** Json key for the room number of a period. */
	public static final String ROOM = "Room";

	/** Json key for the user settings. */
	public static final String SETTINGS = "Settings";
	/** Json key for the next up verbosity. */
	public static final String NEXT_UP = "NextUp";
	/** Json value to distable the next up feature. */
	public static final String NEXT_UP_DISABLED = "Disabled";
	/** Json value to show the next class only for the next up feature. */
	public static final String NEXT_UP_ONE = "Next Class";
	/** Json value to show all classes in the current day for the next up feature. */
	public static final String NEXT_UP_ALL = "All Classes";
	/** Json key for the theme color. */
	public static final String THEME = "Theme";
	/** Json key for the name of the font. */
	public static final String FONT = "Font";
	/** Json key for the name of the school data file. */
	public static final String SCHOOL_JSON = "SchoolJson";
	

	/** Information for all school files the user has ever used. */
	@SerializedName("Schools")
	public Map<String, UserJsonSchoolDef> schools;

	/** User settings. */
	@SerializedName("Settings")
	public Map<String, String> settings;

}
