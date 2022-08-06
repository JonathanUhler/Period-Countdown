package school;


import util.DateTime;
import util.Duration;
import java.util.Map;
import java.util.List;
import java.io.FileNotFoundException;


public class SchoolAPI {

	private SchoolYear year;
	

	public SchoolAPI(String jsonName) throws FileNotFoundException,
											 IllegalArgumentException
	{
		this(jsonName, null);
	}


	public SchoolAPI(String jsonName, Map<String, List<Map<String, String>>> days) throws FileNotFoundException,
																						  IllegalArgumentException
	{
		this.year = new SchoolYear(SchoolJson.EXPECTED_PATH + jsonName, days);
	}
	

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
			for (int i = 0; i < Duration.DAYS_PER_YEAR; i++) {
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


	public SchoolPeriod getNextPeriodToday(DateTime now) {
		SchoolPeriod currentPeriod = this.getCurrentPeriod(now);
		if (currentPeriod == null)
			return null;
		
		DateTime nextPeriodStart = currentPeriod.getEndTime(now);
		return this.getCurrentPeriod(nextPeriodStart);
	}


	// Unconditionally gets time until the start of the next period to be counted, even if a match
	// is not found on the current day specified by the argument now.
	public Duration getTimeRemaining(DateTime now) {
		DateTime walk = (DateTime) now.clone();

		// Scan the next year for a valid period
		for (int i = 0; i < Duration.DAYS_PER_YEAR; i++) {
			SchoolPeriod nextPeriod = this.getNextPeriodToday(walk);
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
	
}
