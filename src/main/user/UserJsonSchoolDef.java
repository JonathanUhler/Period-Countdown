// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// UserJsonSchoolDef.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package user;


import java.util.Map;
import java.util.List;
import com.google.gson.annotations.SerializedName;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class UserJsonSchoolDef
//
// Java representation of a school definition within the User.json file. This definition contains
// a map of periods (period numbers and the associated information) and the optional "Days"
// element to use instead of the "Days" defined in a school file
//
public class UserJsonSchoolDef {

	@SerializedName("Periods")
	public Map<String, Map<String, String>> periods;
	@SerializedName("Days")
	// Allows user-specific day data (like for colleges) to be in the user file instead of the school file
	public Map<String, List<Map<String, String>>> days;
	
}
// end: public class UserJsonSchoolDef
