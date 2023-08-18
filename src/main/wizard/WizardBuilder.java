package wizard;


import wizard.interfaces.EditorEntry;
import wizard.entry.*;
import wizard.editor.*;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;


public class WizardBuilder {

	public static Map<String, Object> build(String firstDayTag,
											String lastDayTag,
											String timezone,
											List<EditorEntry> periods,
											List<EditorEntry> days,
											List<EditorEntry> weeks,
											List<EditorEntry> weekExceptions)
	{
		Map<String, Object> infoJson = new HashMap<>();
		Map<String, Object> daysJson = new HashMap<>();
		Map<String, Object> weeksJson = new HashMap<>();
		List<Object> weekExceptionsJson = new ArrayList<>();

		int lastPeriod = WizardBuilder.countAcademicPeriods(periods);
		infoJson.put("FirstPeriod", "1");
		infoJson.put("LastPeriod", Integer.toString(lastPeriod));
		infoJson.put("FirstDayTag", firstDayTag);
		infoJson.put("LastDayTag", lastDayTag);
		infoJson.put("Timezone", timezone);
		
		for (EditorEntry day : days) {
			Map<String, Object> dayJson = WizardBuilder.dayToJson(day, periods);
			daysJson.put((String) dayJson.get("Name"), dayJson.get("Periods"));
		}

		for (EditorEntry week : weeks) {
			Map<String, Object> weekJson = WizardBuilder.weekToJson(week);
			weeksJson.put((String) weekJson.get("Name"), weekJson.get("Days"));
		}

		for (EditorEntry weekEx : weekExceptions) {
			Map<String, String> weekExceptionJson = WizardBuilder.weekExceptionToJson(weekEx);
			weekExceptionsJson.add(weekExceptionJson);
		}

		Map<String, Object> json = new HashMap<>();
		json.put("Info", infoJson);
		json.put("Days", daysJson);
		json.put("Weeks", weeksJson);
		json.put("Exceptions", weekExceptionsJson);
		return json;
	}


	private static boolean beforeTime(String timeStr1, String timeStr2) {
		DateFormat df = new SimpleDateFormat("HH:mm");
		Date time1;
		Date time2;
		try {
			time1 = df.parse(timeStr1);
			time2 = df.parse(timeStr2);
		}
		catch (ParseException e) {
			return false;
		}

		return time1.before(time2);
	}


	private static boolean beforeDate(String dateStr1, String dateStr2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date1;
		Date date2;
		try {
			date1 = df.parse(dateStr1);
			date2 = df.parse(dateStr2);
		}
		catch (ParseException e) {
			return false;
		}

		return date1.before(date2);
	}


	public static String validateInfo(String firstDayTag, String lastDayTag) {
		String errors = "";
		if (WizardBuilder.beforeDate(lastDayTag, firstDayTag))
			errors += "Last date before first date\n";
		
		if (errors.equals(""))
			return null;
		return errors;
	}


	private static int countAcademicPeriods(List<EditorEntry> periods) {
		int count = 0;
		for (EditorEntry period : periods) {
			Map<String, Object> periodInfo = period.collectFromMutableComponent();
			String periodType = (String) periodInfo.get("Type");
			if (periodType.equals("Academic"))
				count++;
		}
		return count;
	}
	

	private static String getPeriodType(EditorEntry period,
										List<EditorEntry> periods)
	{
		if (!(period instanceof Period))
			return null;

		Map<String, Object> periodInfo = period.collectFromMutableComponent();
		String periodName = (String) periodInfo.get("Name");
		String periodType = (String) periodInfo.get("Type");
		if (periodName.equals("Free"))
			return "Nothing";
		if (periodType.equals("Non-Academic"))
			return "Special";

		int periodNum = 1;
		for (EditorEntry other : periods) {
			if (!(other instanceof Period))
				continue;
			if (period.equals(other))
				break;
			
			Map<String, Object> otherInfo = other.collectFromMutableComponent();
			String otherType = (String) otherInfo.get("Type");
			if (otherType.equals("Academic"))
				periodNum++;
		}
		return Integer.toString(periodNum);
	}
	

	public static Map<String, String> periodToJson(EditorEntry period,
												   List<EditorEntry> periods)
	{
		if (period == null || periods == null || !(period instanceof Period))
			return null;

		Map<String, Object> periodInfo = period.collectFromMutableComponent();
		String name = (String) periodInfo.get("Name");
		String start = (String) periodInfo.get("Start");
		String end = (String) periodInfo.get("End");
		String type = WizardBuilder.getPeriodType(period, periods);

		Map<String, String> json = new HashMap<>();
		json.put("Name", name);
		json.put("Start", start);
		json.put("End", end);
		json.put("Type", type);
		return json;
	}
	

	public static String validatePeriodList(List<EditorEntry> periods) {
		String errors = "";

		if (WizardBuilder.countAcademicPeriods(periods) == 0)
			errors += "At least one 'Academic' period is required\n";

		Set<String> usedNames = new HashSet<>();
		for (EditorEntry period : periods) {
			if (!(period instanceof Period))
				continue;

			Map<String, Object> periodInfo = period.collectFromMutableComponent();
			String name = (String) periodInfo.get("Name");

			if (usedNames.contains(name)) {
				errors += "Duplicate name '" + name + "'\n";
				continue;
			}
			usedNames.add(name);
		}
		
		if (errors.equals(""))
			return null;
		return errors;
	}


	private static Map<String, String> createBufferPeriod(String lastEndStr,
														  String nextStartStr)
	{
		Map<String, String> bufferPeriod = new HashMap<>();
		
		String bufferName = "Between Classes";
		if (lastEndStr.equals("00:00"))
			bufferName = "Before Classes";
		else if (nextStartStr == null)
			bufferName = "After Classes";

		String endStr = nextStartStr;
		if (nextStartStr == null)
			endStr = "23:59";
		
		bufferPeriod.put("Name", bufferName);
		bufferPeriod.put("Start", lastEndStr);
		bufferPeriod.put("End", endStr);
		bufferPeriod.put("Type", "Nothing");
		return bufferPeriod;
	}


	private static void sortPeriodEntries(List<EditorEntry> periods) {
		if (periods == null)
			return;

		for (int i = 0; i < periods.size() - 1; i++) {
			for (int j = 0; j < periods.size() - i - 1; j++) {
				EditorEntry period1 = periods.get(j);
				EditorEntry period2 = periods.get(j + 1);
				String start1Str = (String) period1.collectFromMutableComponent().get("Start");
				String start2Str = (String) period2.collectFromMutableComponent().get("Start");

				if (WizardBuilder.beforeTime(start2Str, start1Str)) {
					periods.set(j, period2);
					periods.set(j + 1, period1);
				}
			}
		}
	}
	

	public static Map<String, Object> dayToJson(EditorEntry day,
												List<EditorEntry> allPeriodEntries)
	{
		if (day == null || !(day instanceof Day))
			return null;

		Map<String, Object> dayInfo = day.collectFromMutableComponent();
		String dayName = (String) dayInfo.get("Name");
		PeriodEditor periodEditor = (PeriodEditor) dayInfo.get("Periods");
		List<EditorEntry> periodEntries = periodEditor.getEditorList().getEntries();
		WizardBuilder.sortPeriodEntries(periodEntries);

		List<Map<String, String>> periods = new ArrayList<>();
		String lastEndStr = "00:00";
		for (int i = 0; i < periodEntries.size(); i++) {
			EditorEntry periodEntry = periodEntries.get(i);

			Map<String, String> period = WizardBuilder.periodToJson(periodEntry,
																	allPeriodEntries);
			String startStr = period.get("Start");
			String endStr = period.get("End");
			if (!lastEndStr.equals(startStr)) {
				Map<String, String> bufferPeriod = WizardBuilder.createBufferPeriod(lastEndStr,
																					startStr);
				periods.add(bufferPeriod);
			}
			periods.add(period);
			lastEndStr = endStr;
		}

		if (!lastEndStr.equals("23:59")) {
			Map<String, String> bufferPeriod = WizardBuilder.createBufferPeriod(lastEndStr, null);
			periods.add(bufferPeriod);
		}

		Map<String, Object> json = new HashMap<>();
		json.put("Name", dayName);
		json.put("Periods", periods);
		return json;
	}


	public static String validateDayList(List<EditorEntry> days,
										 List<EditorEntry> allPeriodEntries)
	{
		String errors = "";

		Set<String> usedNames = new HashSet<>();
		for (EditorEntry day : days) {
			if (!(day instanceof Day))
				continue;

			Map<String, Object> dayInfo = day.collectFromMutableComponent();
			String dayName = (String) dayInfo.get("Name");
			PeriodEditor periodEditor = (PeriodEditor) dayInfo.get("Periods");
			List<EditorEntry> periodEntries = periodEditor.getEditorList().getEntries();
			WizardBuilder.sortPeriodEntries(periodEntries);

			if (usedNames.contains(dayName)) {
				errors += "Duplicate name '" + dayName + "'\n";
				continue;
			}
			usedNames.add(dayName);

			if (periodEntries.size() == 0) {
				errors += "Day '" + dayName + "' must contain at least one period\n";
				continue;
			}

			for (EditorEntry period : periodEntries) {
				Map<String, Object> periodInfo = period.collectFromMutableComponent();
				String periodName = (String) periodInfo.get("Name");
				String start = (String) periodInfo.get("Start");
				String end = (String) periodInfo.get("End");

				if (WizardBuilder.beforeTime(end, start) || end.equals(start)) {
					errors += "Period '" + periodName + "' in day '" + dayName +
						"' has end time <= start time\n";
					continue;
				}
				
				if (!allPeriodEntries.contains(period))
					errors += "Unknown period '" + period + "' in day '" + dayName + "'\n";
			}

			for (int i = 0; i < periodEntries.size() - 1; i++) {
				EditorEntry period1 = periodEntries.get(i);
				EditorEntry period2 = periodEntries.get(i + 1);
				String end1Str = (String) period1.collectFromMutableComponent().get("End");
				String start2Str = (String) period2.collectFromMutableComponent().get("Start");

				if (WizardBuilder.beforeTime(start2Str, end1Str)) {
					errors += "Day '" + dayName + "' has period overlap between end=" +
						end1Str + " and start=" + start2Str + "\n";
					continue;
				}
			}
		}
		
		if (errors.equals(""))
			return null;
		return errors;
	}
	

	public static Map<String, Object> weekToJson(EditorEntry week) {
		if (week == null || !(week instanceof Week))
			return null;

		Map<String, Object> weekInfo = week.collectFromMutableComponent();
		String weekName = (String) weekInfo.get("Name");
		DayEditor dayEditor = (DayEditor) weekInfo.get("Days");
		List<EditorEntry> dayEntries = dayEditor.getEditorList().getEntries();

		List<String> days = new ArrayList<>();
		for (EditorEntry dayEntry : dayEntries) {
			Map<String, Object> dayInfo = dayEntry.collectFromMutableComponent();
			String dayName = (String) dayInfo.get("Name");
			days.add(dayName);
		}

		Map<String, Object> json = new HashMap<>();
		json.put("Name", weekName);
		json.put("Days", days);
		return json;
	}


	public static String validateWeekList(List<EditorEntry> weeks,
										  List<EditorEntry> allDayEntries)
	{
		String errors = "";

		boolean defaultWeekExists = false;
		Set<String> usedNames = new HashSet<>();
		for (EditorEntry week : weeks) {
			if (!(week instanceof Week))
				continue;

			Map<String, Object> weekInfo = week.collectFromMutableComponent();
			String weekName = (String) weekInfo.get("Name");
			if (weekName.equals("DEFAULT"))
				defaultWeekExists = true;

			if (usedNames.contains(weekName)) {
				errors += "Duplicate name '" + weekName + "'\n";
				continue;
			}
			usedNames.add(weekName);
			
			DayEditor dayEditor = (DayEditor) weekInfo.get("Days");
			List<EditorEntry> dayEntries = dayEditor.getEditorList().getEntries();
			if (dayEntries.size() != 7) {
				errors += "Week '" + weekName + "' has " + dayEntries.size() + " days (req 7)\n";
				continue;
			}

			for (EditorEntry day : dayEntries) {
				if (!allDayEntries.contains(day))
					errors += "Unknown day '" + day + "' in week '" + weekName + "'\n";
			}
		}

		if (!defaultWeekExists)
			errors += "Missing entry: no 'DEFAULT' week is defined";

		if (errors.equals(""))
			return null;
		return errors;
	}


	public static Map<String, String> weekExceptionToJson(EditorEntry weekException) {
		if (weekException == null || !(weekException instanceof WeekException))
			return null;

		Map<String, Object> weekExceptionInfo = weekException.collectFromMutableComponent();
		String weekTag = (String) weekExceptionInfo.get("WeekTag");
		String weekType = weekExceptionInfo.get("Type").toString();

		Map<String, String> json = new HashMap<>();
		json.put("WeekTag", weekTag);
		json.put("Type", weekType);
		return json;
	}


	public static String validateWeekExceptionList(List<EditorEntry> weekExceptions,
												   List<EditorEntry> allWeekEntries)
	{
		String errors = "";

		for (EditorEntry weekException : weekExceptions) {
			if (!(weekException instanceof WeekException))
				continue;

			Map<String, Object> weekExceptionInfo = weekException.collectFromMutableComponent();
			Week weekExceptionType = (Week) weekExceptionInfo.get("Type");

			if (!allWeekEntries.contains(weekExceptionType))
				errors += "Unknown week type '" + weekExceptionType + "'\n";
		}

		if (errors.equals(""))
			return null;
		return errors;
	}

}
