// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Tools.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package util;


import school.SchoolAPI;
import school.SchoolPeriod;
import user.UserAPI;
import user.UserPeriod;
import user.UserJson;
import java.util.ArrayList;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class Tools
//
// Miscellaneous tools that do not fit into any other util class
//
public class Tools {

	// ====================================================================================================
	// public static String pad
	//
	// Pads a string to a given width with a given character. If the string is longer than width, no
	// change is made
	//
	// Arguments--
	//
	//  str:       the string to pad. Pads out to the left (e.g. pad("hi", 5, ".") --> "   hi")
	//
	//  width:     the width of the final string if padded. If the starting string is longer than width,
	//             no change is made
	//
	//  character: the character to pad with
	public static String pad(String str, int width, char character) {
		if (str == null)
			str = "";
		return String.format("%" + width + "s", str).replace(' ', character);
	}
	// end: public static String pad


	public static ArrayList<String> getNextUpList(SchoolAPI schoolAPI, UserAPI userAPI, String timezone) {
		return Tools.getNextUpList(schoolAPI, userAPI, timezone, UTCTime.now());
	}
	

	public static ArrayList<String> getNextUpList(SchoolAPI schoolAPI, UserAPI userAPI,
												  String timezone, UTCTime now)
	{
		if (schoolAPI == null || userAPI == null)
			return new ArrayList<>();
		
		String nextUp = userAPI.getNextUp();
		ArrayList<String> nextUpList = new ArrayList<>();
		
		SchoolPeriod nextPeriod = schoolAPI.getNextPeriodToday(now);
		while (nextPeriod != null) {
			if (nextUp.equals(UserJson.NEXT_UP_DISABLED))
				break;

			// Get the class (with user data like teacher and room) based on the generic period, if that
			// generic period can have a class
			UserPeriod nextClass = null;
			if (nextPeriod.isCounted())
				nextClass = userAPI.getPeriod(nextPeriod);

			// Format the string
			// Default periodString is "<period/class name> | <start>-<end>"
			UTCTime periodStart = nextPeriod.getStart();
			UTCTime periodEnd = nextPeriod.getEnd();

			// Add 1 to the end time ms to make the end time the same as the start time of the next period
			periodEnd = periodEnd.plus(1, UTCTime.MILLISECONDS);

			try {
				periodStart = periodStart.to(timezone);
				periodEnd = periodEnd.to(timezone);
			}
			catch (IllegalArgumentException e) {
				// Ignore, just continue with UTC time
			}

			String periodString =
				((nextClass == null) ? nextPeriod.getName() : nextClass.getName()) + " | " +
				Tools.pad(Integer.toString(periodStart.get(UTCTime.HOUR)), 2, '0') + ":" +
				Tools.pad(Integer.toString(periodStart.get(UTCTime.MINUTE)), 2, '0') + "-" +
				Tools.pad(Integer.toString(periodEnd.get(UTCTime.HOUR)), 2, '0') + ":" +
				Tools.pad(Integer.toString(periodEnd.get(UTCTime.MINUTE)), 2, '0');
			
			// If the school period has a class during it add " | <teacher>, <room>"
			if (!nextPeriod.isFree() && nextClass != null && !nextClass.isFree()) {
				String teacher = nextClass.getTeacher();
				String room = nextClass.getRoom();

				// Format based on what data is available (either one, the other, both, or neither)
				if (!teacher.equals("") && room.equals(""))
					periodString += " | " + teacher;
				else if (!room.equals("") && teacher.equals(""))
					periodString += " | " + room;
				else if (!room.equals("") && !teacher.equals(""))
					periodString += " | " + teacher + ", " + room;
			}
			// If the period has something during it (a period, lunch, brunch, etc.) add it to the list
			if (nextPeriod.isCounted())
				nextUpList.add(periodString);

			// Get next period
			nextPeriod = schoolAPI.getNextPeriodToday(periodStart);

			// If only the next period should be shown and that period has been found, skip the rest of the search
			if (nextUp.equals(UserJson.NEXT_UP_ONE) && nextUpList.size() == 1)
				break;
		}

		return nextUpList;
	}

}
// end: public class Tools
