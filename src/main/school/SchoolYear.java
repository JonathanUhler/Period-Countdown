package school;


import util.Log;
import util.DateTime;
import util.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;


public class SchoolYear {

	private SchoolJson schoolJson;
	private HashMap<String, SchoolWeek> weekDefs; // Map of each unique type of week defined in the school json. The
	                                              // keys are the week type/name (e.g. DEFAULT or HBCBC) and the
	                                              // values are the generic week (with no date association) of that
	                                              // type
	private HashMap<String, SchoolWeek> year; // Map of all the weeks that overlap with the school year. The keys
	                                          // are the week tags (date of the Sunday of that week in format
	                                          // yyyy-mm-dd) and the values are that week in the year

	private String firstPeriod;
	private String lastPeriod;
	private String firstDayTag;
	private String lastDayTag;
	

	public SchoolYear(String jsonPath, Map<String, List<Map<String, String>>> days) throws FileNotFoundException,
																						   IllegalArgumentException
	{
		InputStream schoolStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(jsonPath);
		if (schoolStream == null)
			throw new FileNotFoundException(Log.format(Log.ERROR, "SchoolYear",
													   "json resource \"" + jsonPath + "\" is null"));
		
		InputStreamReader schoolReader = new InputStreamReader(schoolStream);

		Gson gson = new Gson();
		try {
			this.schoolJson = gson.fromJson(schoolReader, SchoolJson.class);
		}
		catch (JsonSyntaxException e) {
			throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear", "json cannot be parsed: " + e));
		}

		if (this.schoolJson.days == null) {
			if (days != null)
				this.schoolJson.days = days;
			else
				throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
															  "\"Days\" was not specified in school file " +
															  "and \"days\" argument is null"));
		}

		this.initInfo();
		this.initWeekDefs();
		this.initYear();
	}


	private void initInfo() throws IllegalArgumentException {
		if (!this.schoolJson.info.containsKey(SchoolJson.FIRST_PERIOD) ||
			!this.schoolJson.info.containsKey(SchoolJson.LAST_PERIOD) ||
			!this.schoolJson.info.containsKey(SchoolJson.FIRST_DAY_TAG) ||
			!this.schoolJson.info.containsKey(SchoolJson.LAST_DAY_TAG))
			throw new IllegalArgumentException("school json Info missing key");

		this.firstPeriod = this.schoolJson.info.get(SchoolJson.FIRST_PERIOD);
		this.lastPeriod = this.schoolJson.info.get(SchoolJson.LAST_PERIOD);
		this.firstDayTag = this.schoolJson.info.get(SchoolJson.FIRST_DAY_TAG);
		this.lastDayTag = this.schoolJson.info.get(SchoolJson.LAST_DAY_TAG);
	}


	private void initWeekDefs() throws IllegalArgumentException {
		this.weekDefs = new HashMap<>();
		
		if (!this.schoolJson.weeks.containsKey(SchoolJson.DEFAULT))
			throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
														  "json does not contain DEFAULT week"));

		for (String weekType : this.schoolJson.weeks.keySet()) {
			ArrayList<SchoolDay> dayDefs = new ArrayList<>();
			List<String> dayTypes = this.schoolJson.weeks.get(weekType);
			
			if (dayTypes.size() != Duration.DAYS_PER_WEEK)
				throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
															  "week type \"" + weekType + "\" does not have 7 days"));

			for (String dayType : dayTypes) {
				ArrayList<SchoolPeriod> periodDefs = new ArrayList<>();
			    List<Map<String, String>> periodsInDay = this.schoolJson.days.get(dayType);

				if (periodsInDay.size() == 0)
					throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
																  "day type \"" + dayType + "\" has 0 periods"));

				for (Map<String, String> period : periodsInDay) {
					if (!period.containsKey(SchoolJson.TYPE) ||
						!period.containsKey(SchoolJson.NAME) ||
						!period.containsKey(SchoolJson.START) ||
						!period.containsKey(SchoolJson.END))
						throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
																	  "period has missing key: " + period));

					String type = period.get(SchoolJson.TYPE);
					String name = period.get(SchoolJson.NAME);
					String start = period.get(SchoolJson.START);
					String end = period.get(SchoolJson.END);
					periodDefs.add(new SchoolPeriod(type, name, start, end));
				}

				dayDefs.add(new SchoolDay(dayType, periodDefs));
			}

			this.weekDefs.put(weekType, new SchoolWeek(weekType, dayDefs));
		}
	}


	private void initYear() throws IllegalArgumentException {
		this.year = new HashMap<>();
		
		// The declaration of these Time objects without a catch for the IllegalArgumentException is fine since
		// this method can throw exceptions while parsing anyway. The intent of this class is to initialize
		// with a catch
		
		DateTime firstDay = DateTime.getInstance(this.firstDayTag);
		DateTime lastDay = DateTime.getInstance(this.lastDayTag);

		DateTime current = DateTime.getInstance(firstDay.getTagForClosest(DateTime.SUNDAY));
		DateTime end = DateTime.getInstance(lastDay.getTagForClosest(DateTime.SATURDAY));

		while (current.before(end)) {
			String weekTag = current.getWeekTag();

			// Add the default week
			if (!this.weekDefs.containsKey(SchoolJson.DEFAULT))
				throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear", "default week not available"));
			this.year.put(weekTag, this.weekDefs.get(SchoolJson.DEFAULT));

			// Check for an exception for this week
			for (Map<String, String> exception : this.schoolJson.exceptions) {
				if (!exception.containsKey(SchoolJson.TYPE) || !exception.containsKey(SchoolJson.WEEK_TAG))
					throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
																  "week exception is missing key: " + exception));

				// At this point the exception structure is valid to index
				String exceptionWeekTag = exception.get(SchoolJson.WEEK_TAG);
				if (weekTag.equals(exceptionWeekTag)) {
					// A match was found, now validate that the "Type" for the exception exists and add to the
					// list of SchoolWeek objects for the year
					String exceptionType = exception.get(SchoolJson.TYPE);
					if (!this.weekDefs.containsKey(exceptionType))
						throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
																	  "week exception has invalid type: " +
																	  exceptionType));

					this.year.put(weekTag, this.weekDefs.get(exceptionType));
				}
			}

			current.add(DateTime.DATE, Duration.DAYS_PER_WEEK);
		}
	}


	public SchoolWeek getWeek(DateTime when) {
		if (when == null)
			return null;
		
		String weekTag = when.getWeekTag();
		if (this.year.containsKey(weekTag))
			return this.year.get(weekTag);
		return null;
	}


	@Override
	public String toString() {
		String str = super.toString() +
			";FirstPeriod=" + this.firstPeriod +
			",LastPeriod=" + this.lastPeriod +
			",FirstDayTag=" + this.firstDayTag +
			",LastDayTag=" + this.lastDayTag +
			",Weeks=[";

		for (String weekTag : this.year.keySet())
			str += "\n\t" + weekTag + "=" + this.year.get(weekTag).toString().replace("\n", "\n\t");

		str += "\n]";
		return str;
	}

}
