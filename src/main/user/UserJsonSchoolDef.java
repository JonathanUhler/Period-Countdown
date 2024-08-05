package user;


import java.util.Map;
import java.util.List;
import com.google.gson.annotations.SerializedName;


/**
 * Defines a school used by the user json file. This definition contains a map of periods with
 * their information (teacher, room, and name) as well as the optional {@code "Days"} field
 * to use instead of the {@code "Days"} defined in a school data file.
 *
 * @author Jonathan Uhler
 */
public class UserJsonSchoolDef {

    /** Information about periods for the given school. */
    @SerializedName("Periods")
    public Map<String, Map<String, String>> periods;
	
}
