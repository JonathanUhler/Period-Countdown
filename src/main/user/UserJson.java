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
