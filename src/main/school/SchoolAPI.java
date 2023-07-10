package school;


import util.UTCTime;
import util.Duration;
import java.util.Map;
import java.util.List;
import java.io.FileNotFoundException;


/**
 * Interface for accessing information derived from the school json file. This includes things like
 * time remaining, upcoming periods, and the current period. More data is available from the other
 * classes in this package, but it is avised to use this API instead
 *
 * @author Jonathan Uhler
 */
public class SchoolAPI {

	/** Definition for the school year. This is an object-form of the school json file, making the
		data more accessible and providing some simple API methods. */
	private SchoolYear year;

	
	/**
	 * Constructs a new {@code SchoolAPI} object with a specified json file name.
	 *
	 * @param jsonName  the name of the json file. No path should be specified. All json files
	 *                  are expected to be at the location described by 
     *                  {@code SchoolJson.EXPECTED_PATH} as an artifact in the jar file.
	 *
	 * @throws FileNotFoundException     if the json file does not exist.
	 * @throws IllegalArgumentException  if any json parse error occurs.
	 *
	 * @see SchoolYear
	 */
	public SchoolAPI(String jsonName) throws FileNotFoundException {
		this(jsonName, null);
	}


	/**
	 * Constructs a new {@code SchoolAPI} object with a specified json file name.
	 *
	 * @param jsonName  the name of the json file. No path should be specified. All json files
	 *                  are expected to be at the location described by 
     *                  {@code SchoolJson.EXPECTED_PATH} as an artifact in the jar file.
	 * @param days      an optional parameter to manually specify the definition of each day type
	 *                  for the given school (e.g. for the college/university format). If this
	 *                  argument is not {@code null}, it will be used in place of any {@code "Days"}
	 *                  definition in the json file.
	 *
	 * @throws FileNotFoundException     if the json file does not exist.
	 * @throws IllegalArgumentException  if any json parse error occurs.
	 *
	 * @see SchoolYear
	 */
	public SchoolAPI(String jsonName,
					 Map<String, List<Map<String, String>>> days) throws FileNotFoundException {
		this.year = new SchoolYear(SchoolJson.EXPECTED_PATH + jsonName, days);
	}


	/**
	 * Returns the timezone of the loaded school json file as a Unix timezone identifier (e.g. 
	 * {@code "America/Los_Angeles"} for much of the west coast of the United States).
	 * <p>
	 * This method is a wrapper for accessing {@code SchoolYear::getTimezone}.
	 *
	 * @return the timezone of the loaded school json file as a Unix timezone identifier.
	 */
	public String getTimezone() {
		return this.year.getTimezone();
	}


	/**
	 * Returns the first possible period number.
	 * <p>
	 * This method is a wrapper for accessing {@code SchoolYear::getFirstPeriod}.
	 *
	 * @return the first possible period number
	 */
	public int getFirstPeriod() {
		return this.year.getFirstPeriod();
	}


	/**
	 * Returns the last possible period number.
	 * <p>
	 * This method is a wrapper for accessing {@code SchoolYear::getLastPeriod}.
	 *
	 * @return the last possible period number
	 */
	public int getLastPeriod() {
		return this.year.getLastPeriod();
	}


	/**
	 * Returns the {@code SchoolPeriod} object such that {@code start <= now <= end}. Note that
	 * the definition of a period runs from the start to the end, inclusive on both sides. This is
	 * because the end time is 1 millisecond before the period trasition (e.g. 13:59:59.999 for a
	 * period where the bell rings at 14:00).
	 *
	 * @param now  the time to get the current period for.
	 *
	 * @return the current period object, if one exists. If {@code now == null} or no period exists
	 *         as specified by {@code now}, then {@code null} is returned.
	 */
	public SchoolPeriod getCurrentPeriod(UTCTime now) {
		if (now == null)
			return null;
		return this.year.getPeriod(now);
	}


	/**
	 * Gets the period immediately after the period returned by {@code getCurrentPeriod}. This is
	 * achieved by taking the end time of the current period and adding 1 millisecond, thus causing
	 * the time to overflow to the start of the next period whose time is at least 1 millisecond.
	 *
	 * @param now  the time to get the next period for.
	 *
	 * @return the next period object, if one exists. If the current period is {@code null},
	 *         {@code now == null}, or no next period exists, then {@code null} is returned.
	 *
	 * @see getCurrentPeriod
	 */
	public SchoolPeriod getNextPeriod(UTCTime now) {
		SchoolPeriod currentPeriod = this.getCurrentPeriod(now);

		if (currentPeriod != null) {
			UTCTime currentPeriodEnd = currentPeriod.getEnd();
			// Go from AA:BB:59.999 -> CC:DD:00.000. Periods should be defined in the json file to
			// the second precision, and milliseconds are added as either 000 or 999 depending on
			// if the time is the start or the end of the period
			UTCTime nextPeriodStart = currentPeriodEnd.plus(1, UTCTime.MILLISECONDS);

			return this.getCurrentPeriod(nextPeriodStart);
		}
		else {
			// If the current period is null, then "walk" the time pointer for the next calendar
			// year from the time specified by {@code now} to search for a period.
			UTCTime walk = now;

			for (int i = 0; i < Duration.DAYS_PER_YEAR; i++) {
				SchoolPeriod nextPeriod = this.getNextPeriodToday(walk);
				// If no next period was found today, then continue to the next day
				if (nextPeriod == null) {
					walk = walk.plus(1, UTCTime.DAYS);
					walk = walk.toMidnight(this.year.getTimezone());
					continue;
				}

				// If the period was found, return that
				if (nextPeriod != null && nextPeriod.isCounted())
					return nextPeriod;
				// If some period was found today, but wasn't "counted", then loop through
				// the rest of the periods in the current day to check for another
				// counter period
				else {
					while (nextPeriod != null && !nextPeriod.isLast()) {
						walk = nextPeriod.getEnd().plus(1, UTCTime.MILLISECONDS);
						nextPeriod = this.getNextPeriodToday(walk);
						if (nextPeriod != null && nextPeriod.isCounted())
							return nextPeriod;
					}
				}
			}

			return null;
		}
	}


	/**
	 * Returns the period immediately after the period returned by {@code getCurrentPeriod} if the
	 * current period is not the last period.
	 *
	 * @param now  the time to get the next period in the current day specified by {@code now}.
	 *
	 * @return the next period in the current day specified by the argument {@code now}. If
	 *         {@code now == null}, the current period is {@code null}, or the current period is
	 *         the last period in the day, {@code null} is returned.
	 */
	public SchoolPeriod getNextPeriodToday(UTCTime now) {
		SchoolPeriod currentPeriod = this.getCurrentPeriod(now);
		if (currentPeriod == null || currentPeriod.isLast())
			return null;

		UTCTime currentPeriodEnd = currentPeriod.getEnd();
		UTCTime nextPeriodStart = currentPeriodEnd.plus(1, UTCTime.MILLISECONDS);
		return this.getCurrentPeriod(nextPeriodStart);
	}


	/**
	 * Gets the time remaining. The range of the "remaining time" is started by the argument
	 * {@code now} and terminated by the end of the current period if 
	 * {@code currentPeriod.isCounted()}, else the start of the next period that is counted. At
	 * most, the next calendar year is searched for a valid period before the method is exited
	 * with a {@code null} return value.
	 *
	 * @param now  the time that starts the "remaining time" interval.
	 *
	 * @return the amount of remaining time.
	 *
	 * @see SchoolPeriod
	 */
	public Duration getTimeRemaining(UTCTime now) {
		UTCTime walk = now;
		
		for (int i = 0; i < Duration.DAYS_PER_YEAR; i++) {
			SchoolPeriod currentPeriod = this.getCurrentPeriod(walk);
			SchoolPeriod nextPeriod = this.getNextPeriod(walk);

			if (currentPeriod != null &&
				currentPeriod.isCounted() &&
				!nextPeriod.isCounted())
				return new Duration(now, currentPeriod.getEnd().plus(1, UTCTime.MILLISECONDS));

			// If the current period is not counted, then go through all other periods in
			// this day to check for a counted period
			while (nextPeriod != null && !nextPeriod.isLast()) {
				if (nextPeriod.isCounted())
					return new Duration(now, nextPeriod.getStart());

				walk = nextPeriod.getEnd().plus(1, UTCTime.MILLISECONDS);
				nextPeriod = this.getCurrentPeriod(walk);
			}

			// If no match found today, go to the next day and continue the search
			walk = walk.plus(1, UTCTime.DAYS);
			walk = walk.toMidnight(this.year.getTimezone());
		}
		
		return null;
	}

}
