package school;


import java.util.Map;
import java.util.List;
import com.google.gson.annotations.SerializedName;


/**
 * Defines the object representation for the school json file as well as important constants.
 *
 * @author Jonathan Uhler
 */
public class SchoolJson {
    
    /** Json tag for the number of the first period possible. */
    public static final String FIRST_PERIOD = "FirstPeriod";
    /** Json tag for the number of the last period possible. */
    public static final String LAST_PERIOD = "LastPeriod";
    /** Json tag for the number of the first day tag possible. */
    public static final String FIRST_DAY_TAG = "FirstDayTag";
    /** Json tag for the number of the last day tag possible. */
    public static final String LAST_DAY_TAG = "LastDayTag";
    /** Json tag for the unix TZ identifier of the school. */
    public static final String TIMEZONE = "Timezone";
    
    /** Json tag for the type of a period. */
    public static final String TYPE = "Type";
    /** Json value for a period with no significance. */
    public static final String NOTHING = "Nothing";
    /**
     * Json value for a period with no academic class, but that is "significant" (e.g. should be 
     * counted, such as lunch, brunch, or tutorial).
     */
    public static final String SPECIAL = "Special";
    /** Json tag for the programmer-defined name of a period. */
    public static final String NAME = "Name";
    /** Json tag for the start timestamp of a period, in the defined timezone. */
    public static final String START = "Start";
    /** Json tag for the end timestamp of a period, in the defined timezone. */
    public static final String END = "End";
    
    /** Value for the default week definition that should be used if no week exception exists. */
    public static final String DEFAULT = "DEFAULT";
    
    /** Json tag for the calendar week tag on which an exception occurs. */
    public static final String WEEK_TAG = "WeekTag";
    // The "TYPE" keyword is also used here for week type
    
    
    /** Info section from the json file. */
    @SerializedName("Info")
    public Map<String, String> info;
    
    /** Days section from the json file. */
    @SerializedName("Days")
    public Map<String, List<Map<String, String>>> days;
    
    /** Weeks section from the json file. */
    @SerializedName("Weeks")
    public Map<String, List<String>> weeks;
    
    /** Week exceptions section from the json file. */
    @SerializedName("Exceptions")
    public List<Map<String, String>> exceptions;
    
}
