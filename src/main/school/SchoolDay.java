// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolDay.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package school;


import util.DateTime;
import java.util.ArrayList;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class SchoolDay
//
// Java representation of one of the days defined in the "Days" section of the school json file
public class SchoolDay {

	private String type;
	private ArrayList<SchoolPeriod> periods;
	

	// ----------------------------------------------------------------------------------------------------
	// public SchoolDay
	//
	// Arguments--
	//
	//  type:    the type of day, must be one of the keys defined in the "Days" section of the school json file
	//
	//  periods: a list of periods contained within this day
	//
	public SchoolDay(String type, ArrayList<SchoolPeriod> periods) {
		this.type = type;
		this.periods = periods;
	}
	// end: public SchoolDay


	// ====================================================================================================
	// public SchoolPeriod getPeriod
	//
	// Returns a period within this day
	//
	// Arguments--
	//
	//  when: a DateTime object representing the time of the period within this day
	//
	// Returns--
	//
	//  The period within this day during the argument when if such a period exists, otherwise null
	//
	public SchoolPeriod getPeriod(DateTime when) {
		if (when == null)
			return null;

		for (SchoolPeriod period : this.periods) {
			DateTime periodStart = period.getStartTime(when);
			DateTime periodEnd = period.getEndTime(when);

			if (periodStart == null && periodEnd == null)
				return null;

			if ((periodStart.equals(when) || periodStart.before(when)) &&
				periodEnd.after(when))
				return period;
		}
		
		return null;
	}
	// end: public SchoolPeriod getPeriod


	// ====================================================================================================
	// public String toString
	//
	// Returns a string representation of this SchoolDay
	//
	// Returns--
	//
	//  A list-like string with all the periods below this day
	@Override
	public String toString() {
		String str = "SCHOOLDAY: Type=" + this.type + ",Periods=[";

		for (SchoolPeriod period : this.periods)
			str += "\n\t" + period;

		str += "\n]";
		return str;
	}
	// end: public String toString

}
// end: pubic class SchoolDay
