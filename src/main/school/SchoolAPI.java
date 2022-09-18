// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolAPI.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package school;


import util.UTCTime;
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
	// public String getTimezone
	//
	public String getTimezone() {
		return this.year.getTimezone();
	}
	// end: public String getTimezone


	// ====================================================================================================
	// public SchoolPeriod getCurrentPeriod
	//
	// Gets the current period defined by the time "now"
	//
	// Arguments--
	//
	//  now: the time to search for
	//
	// Returns--
	//
	//  The current SchoolPeriod object if one is found, otherwise null
	//
	public SchoolPeriod getCurrentPeriod(UTCTime now) {
		if (now == null)
			return null;
		
		return this.year.getPeriod(now);
	}
	// end: public SchoolPeriod getCurrentPeriod


	// ====================================================================================================
	// public SchoolPeriod getNextPeriod
	//
	// Gets the next period in the school year
	//
	// Arguments--
	//
	//  now: the time to search for
	//
	// Returns--
	//
	//  The next period if one exists, otherwise null
	//
	public SchoolPeriod getNextPeriod(UTCTime now) {
		SchoolPeriod currentPeriod = this.getCurrentPeriod(now);

		if (currentPeriod != null) {
			UTCTime currentPeriodEnd = currentPeriod.getEnd();
			// Go from AA:BB:59.999 -> CC:DD:00.000. Periods should be defined in the json file to the second
			// precision, and milliseconds are added as either 000 or 999 depending on if the time is the
			// start or the end of the period
			UTCTime nextPeriodStart = currentPeriodEnd.plus(1, UTCTime.MILLISECONDS);

			return this.getCurrentPeriod(nextPeriodStart);
		}
		else {
			UTCTime walk = now;

			for (int i = 0; i < Duration.DAYS_PER_YEAR; i++) {
				SchoolPeriod nextPeriod = this.getNextPeriodToday(walk);
				if (nextPeriod != null && nextPeriod.isCounted())
					return nextPeriod;
				
				walk = walk.plus(1, UTCTime.DAYS);
				walk = walk.toMidnight();
			}

			return null;
		}
	}
	// end: public SchoolPeriod getNextPeriod


	// ====================================================================================================
	// public SchoolPeriod getNextPeriodToday
	//
	// Gets the next period up until the last period in the current day
	//
	// Arguments--
	//
	//  now: the time to search for
	//
	// Returns--
	//
	//  The next period in the current day if one exists, otherwise null
	//
	public SchoolPeriod getNextPeriodToday(UTCTime now) {
		SchoolPeriod currentPeriod = this.getCurrentPeriod(now);
		if (currentPeriod == null || currentPeriod.isLast())
			return null;

		UTCTime currentPeriodEnd = currentPeriod.getEnd();
		UTCTime nextPeriodStart = currentPeriodEnd.plus(1, UTCTime.MILLISECONDS);
		return this.getCurrentPeriod(nextPeriodStart);
	}
	// end: public SchoolPeriod getNextPeriodToday


	// ====================================================================================================
	// public Duration getTimeRemaining
	//
	// Gets the time remaining in the current period or until the next real period
	//
	// Arguments--
	//
	//  now: the start time
	//
	// Returns--
	//
	//  A Duratin object representing the amount of time between "now" and the end of the current
	//  period (if counted) or the start of the next counted period
	//
	public Duration getTimeRemaining(UTCTime now) {
		UTCTime walk = now;

		for (int i = 0; i < Duration.DAYS_PER_YEAR; i++) {
			SchoolPeriod currentPeriod = this.getCurrentPeriod(walk);
			SchoolPeriod nextPeriod = this.getNextPeriod(walk);

			if (currentPeriod.isCounted() && !nextPeriod.isCounted())
				return new Duration(now, currentPeriod.getEnd().plus(1, UTCTime.MILLISECONDS));

			while (nextPeriod != null && !nextPeriod.isLast()) {
				if (nextPeriod.isCounted())
					return new Duration(now, nextPeriod.getStart());

				walk = nextPeriod.getEnd().plus(1, UTCTime.MILLISECONDS);
				nextPeriod = this.getCurrentPeriod(walk);
			}

			walk = walk.plus(1, UTCTime.DAYS);
			walk = walk.toMidnight();
		}
		
		return null;
	}
	// end: public Duration getTimeRemaining

}
// end: public class SchoolAPI
