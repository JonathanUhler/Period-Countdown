// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolWeek.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package school;


import util.DateTime;
import java.util.ArrayList;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class SchoolWeek
//
// Java representation of a week defined in the "Weeks" section of the school json file
//
public class SchoolWeek {

	private String type;
	private ArrayList<SchoolDay> days;
	

	// ----------------------------------------------------------------------------------------------------
	// public SchoolWeek
	//
	// Arguments--
	//
	//  type: the type of week, must be one of the keys defined in the "Weeks" section of the school json file
	//
	//  days: a list of SchoolDay objects within this week
	//
	public SchoolWeek(String type, ArrayList<SchoolDay> days) {
		this.type = type;
		this.days = days;
	}
	// end: public SchoolWeek


    // ====================================================================================================
	// public SchoolDay getDay
	//
	// Returns a day within this week
	//
	// Arguments--
	//
	//  when: a DateTime object representing the time to get a day for within this week. Note that the
	//        exact date of when does not matter, only the day of week matters because this is a
	//        generic class without any concept of when in time this week exists
	//
	// Returns--
	//
	//  The SchoolDay object that contains the time when if one exists, otherwise null
	public SchoolDay getDay(DateTime when) {
		if (when == null)
			return null;
		
		int dayIndex = when.getDayIndex();
		if (dayIndex >= 0 && dayIndex < 7)
			return this.days.get(dayIndex);
		return null;
	}
	// end: public SchoolDay getDay


	// ====================================================================================================
	// public String toString
	//
	// Returns a string representation of this SchoolWeek
	//
	// Returns--
	//
	//  A list-like string containing all the days below this week
	//
	@Override
	public String toString() {
		String str = "SCHOOLWEEK: Type=" + this.type + ",Days=[";

		for (SchoolDay day : this.days)
			str += "\n\t" + day.toString().replace("\n", "\n\t");

		str += "\n]";
		return str;
	}
	// end: public String toString

}
// end: public class SchoolWeek
