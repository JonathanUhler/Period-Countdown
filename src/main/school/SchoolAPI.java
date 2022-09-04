// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolAPI.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package school;


import util.DateTime;
import util.Duration;
import java.util.Map;
import java.util.List;
import java.io.FileNotFoundException;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class SchoolAPI
//
// Interface for accessing information derived from the school json file. This includes things like
// time remaining, upcoming periods, and the current period. More data is available from the other
// classes in this package, but it is avised to use this API instead
//
public class SchoolAPI {

	private SchoolYear year;
	

	// ----------------------------------------------------------------------------------------------------
	// public SchoolAPI
	//
	public SchoolAPI(String jsonName) throws FileNotFoundException,
											 IllegalArgumentException
	{
		this(jsonName, null);
	}
	// end: public SchoolAPI


	// ----------------------------------------------------------------------------------------------------
	// public SchoolAPI
	//
	// Throws exceptions upon invalid declaration of the SchoolYear object "year"
	//
	// Arguments--
	//
	//  jsonName: the name of the school json file
	//
	//  days:     an optional parameter to replace the "Days" field in the school json file. Can be null if
	//            already defined in the school json file. The school json file definition also takes
	//            precedence over this parameter
	//
	public SchoolAPI(String jsonName, Map<String, List<Map<String, String>>> days) throws FileNotFoundException,
																						  IllegalArgumentException
	{
		this.year = new SchoolYear(SchoolJson.EXPECTED_PATH + jsonName, days);
	}
	// end: public SchoolAPI
	

	// ====================================================================================================
	// public SchoolPeriod getCurrentPeriod
	//
	// Returns the current period
	//
	// Arguments--
	//
	//  now: a DateTime object representing the time to search for the current period
	//
	// Returns--
	//
	//  The period that the argument now falls within. Upon an invalid (out of range for the year or
	//  null) argument, will return null
	//
	public SchoolPeriod getCurrentPeriod(DateTime now) {
		if (now == null)
			return null;

		SchoolWeek currentWeek = this.year.getWeek(now);
		if (currentWeek == null)
			return null;

		SchoolDay currentDay = currentWeek.getDay(now);
		if (currentDay == null)
			return null;
		
		return currentDay.getPeriod(now);
	}
	// end: public SchoolPeriod getCurrentPeriod


	// ====================================================================================================
	// public SchoolPeriod getNextPeriod
	//
	// Returns the next period
	//
	// Arguments--
	//
	//  now: a DateTime object representing the time to search for the next period
	//
	// Returns--
	//
	//  The next period on any day for the next year. If no such period can be found, returns null
	//
	public SchoolPeriod getNextPeriod(DateTime now) {
		SchoolPeriod currentPeriod = this.getCurrentPeriod(now);
		
		if (currentPeriod != null) {
			DateTime nextPeriodStart = currentPeriod.getEndTime(now);

			// End of day adjust
			if (currentPeriod.isLast())
				nextPeriodStart.add(DateTime.MINUTE, 1); // Bump by 1 minute to go from 23:59 to 00:00 the next day
			
			return this.getCurrentPeriod(nextPeriodStart);
		}
		else {
			DateTime walk = (DateTime) now.clone();

			// Scan the next year for a valid period
			for (int i = 0; i < Duration.DAYS_PER_YEAR; i++) { // i counts by days
				SchoolPeriod nextPeriod = this.getNextPeriodToday(walk);
				if (nextPeriod != null && nextPeriod.isCounted())
					return nextPeriod;

				// If no period was found today, increase to the next day. Set the walk time to the very start
				// of the day at this point so no periods are skipped. setToMidnight can't be called before
				// or at the start of the loop because that would include more time if the argument now is after
				// 00:00:00.000
				walk.add(DateTime.DATE, 1);
				walk.setToMidnight();
			}
			
			return null;
		}
	}
	// end: public SchoolPeriod getNextPeriod


	// ====================================================================================================
	// public SchoolPeriod getNextPeriodToday
	//
	// Returns the next period today
	//
	// Arguments--
	//
	//  now: a DateTime object representing the time and day to search for the next period
	//
	// Returns--
	//
	//  The next period in the current day (as defined by the argument now). If no such period exists,
	//  returns null
	//
	public SchoolPeriod getNextPeriodToday(DateTime now) {
		// If the current period is null or the last period in the day, there is not another valid
		// period today
		SchoolPeriod currentPeriod = this.getCurrentPeriod(now);
		if (currentPeriod == null || currentPeriod.isLast())
			return null;

		// If the current period is not the last one, get the next period (which by definition is
		// at or before the last period today)
		DateTime nextPeriodStart = currentPeriod.getEndTime(now);
		return this.getCurrentPeriod(nextPeriodStart);
	}
	// end: public SchoolPeriod getNextPeriodToday


	// ====================================================================================================
	// public Duration getTimeRemaining
	//
	// Unconditionally gets time until the start of the next period to be counted, even if a match
	// is not found on the current day specified by the argument now.
	//
	// Arguments--
	//
	//  now: the current time (start time for the duration until the start of the next period)
	//
	// Returns--
	//
	//  A Duration object representing the time until the next period. If no period can be found with the
	//  next year (as limited by getNextPeriod(DateTime)), returns null
	//
	public Duration getTimeRemaining(DateTime now) {
		DateTime walk = (DateTime) now.clone();

		// Scan the next year for a valid period
		for (int i = 0; i < Duration.DAYS_PER_YEAR; i++) {
			SchoolPeriod currentPeriod = this.getCurrentPeriod(walk);
			SchoolPeriod nextPeriod = this.getNextPeriodToday(walk);

			// If the next period is not counted but the current one is, then the time remaining is not until
			// the next counted period, but until the end of the current period
			if (currentPeriod.isCounted() && !nextPeriod.isCounted())
				return new Duration(now, currentPeriod.getEndTime(walk));
			
			// Loop through the periods today to check for the next counted period in this day
			while (nextPeriod != null && !nextPeriod.isLast()) {
				if (nextPeriod != null && nextPeriod.isCounted())
					// The next period was found. The time remaining in the current period is from the argument
					// now until the start time of the next period
					return new Duration(now, nextPeriod.getStartTime(walk));

				// If the next period was not found, set the current search/walk time to the end of the last
				// found period, then get the new current period and check again
				walk = nextPeriod.getEndTime(walk);
				nextPeriod = this.getCurrentPeriod(walk);
				System.out.println(nextPeriod + " --> " + nextPeriod.isCounted());
			}

			// If no period was found today, increase to the next day. Set the walk time to the very start
			// of the day at this point so no periods are skipped. setToMidnight can't be called before
			// or at the start of the loop because that would include more time if the argument now is after
			// 00:00:00.000
			walk.add(DateTime.DATE, 1);
			walk.setToMidnight();
		}
			
		return null;
	}
	// end: public Duration getTimeRemaining
	
}
// end: public class SchoolAPI
