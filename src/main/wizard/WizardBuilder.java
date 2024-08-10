package wizard;


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
import wizard.interfaces.EditorEntry;
import wizard.entry.*;
import wizard.editor.*;


/**
 * Performs real-time validation of data entered by the user and converts the object representation
 * of school data to a JSON string.
 *
 * @author Jonathan Uhler
 */
public class WizardBuilder {
    
    /**
     * Builds a final JSON-like structure using the {@code java.util} data structures.
     *
     * This method assumes that all arguments represent valid data as determined by the validator
     * methods in this class.
     *
     * @param firstDayTag     the date of the first day of school from the {@code InfoEditor}.
     * @param lastDayTag      the date of the last day of school from the {@code InfoEditor}.
     * @param timezone        the unix TZ identifier from the {@code InfoEditor}.
     * @param periods         a list of {@code Period}s.
     * @param days            a list of {@code Day}s.
     * @param weeks           a list of {@code Week}s.
     * @param weekExceptions  a list of {@code WeekException}s.
     *
     * @return a hashmap that can be parsed to a JSON string, which represents the final output
     *         from the data generator.
     *
     * @throws NullPointerException  if any argument is {@code null}.
     */
    public static Map<String, Object> build(String firstDayTag,
                                            String lastDayTag,
                                            String timezone,
                                            List<EditorEntry> periods,
                                            List<EditorEntry> days,
                                            List<EditorEntry> weeks,
                                            List<EditorEntry> weekExceptions)
    {
        if (firstDayTag == null)
            throw new NullPointerException("firstDayTag was null");
        if (lastDayTag == null)
            throw new NullPointerException("lastDayTag was null");
        if (timezone == null)
            throw new NullPointerException("timezone was null");
        if (periods == null)
            throw new NullPointerException("periods was null");
        if (days == null)
            throw new NullPointerException("days was null");
        if (weeks == null)
            throw new NullPointerException("weeks was null");
        if (weekExceptions == null)
            throw new NullPointerException("weekExceptions was null");
	
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
    
    
    /**
     * Determines if one time string in {@code HH:mm} format is strictly before another. This method
     * assumes the two time strings occur on the same date. If any parse error occurs in the
     * argument strings, then {@code false} is returned.
     *
     * @param timeStr1  a time string.
     * @param timeStr2  a second time string, to determine if {@code timeStr1 < timeStr2}.
     *
     * @return whether {@code timeStr1 < timeStr2}.
     */
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
    
    
    /**
     * Determines if one date string in {@code yyyy-MM-dd} format is strictly before another, to 
     * the nearest day. If any parse error occurs in the argument strings, then {@code false} is 
     * returned.
     *
     * @param timeStr1  a date string.
     * @param timeStr2  a second date string, to determine if {@code dateStr1 < dateStr2}.
     *
     * @return whether {@code dateStr1 < dateStr2}.
     */
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
    
    
    /**
     * Validates data from the "Info" section of the data generator. Any errors are returned
     * as a single string with entries separated by a newline character. If no errors are found,
     * {@code null} is returned.
     *
     * The checks performed include:
     * <ul>
     * <li> {@code firstDayTag <= lastDayTag}
     * </ul>
     *
     * @param firstDayTag     the date of the first day of school from the {@code InfoEditor}.
     * @param lastDayTag      the date of the last day of school from the {@code InfoEditor}.
     *
     * @return messages describing validation errors, or {@code null} if validation was successful.
     *
     * @throws NullPointerException  if any argument is {@code null}.
     */
    public static String validateInfo(String firstDayTag, String lastDayTag) {
        if (firstDayTag == null)
            throw new NullPointerException("firstDayTag was null");
        if (lastDayTag == null)
            throw new NullPointerException("lastDayTag was null");
	
        String errors = "";
        if (WizardBuilder.beforeDate(lastDayTag, firstDayTag))
            errors += "Last date before first date\n";
	
        if (errors.equals(""))
            return null;
        return errors;
    }
    
    
    /**
     * Returns the number of periods defined by the user that were classified as having the
     * type "Academic".
     *
     * @param periods  a list of {@code Period}s.
     *
     * @return the number of {@code Period} objects in {@code periods} that have the type
     *         "Academic".
     *
     * @throws NullPointerExcepction  if {@periods == null}.
     */
    private static int countAcademicPeriods(List<EditorEntry> periods) {
        if (periods == null)
            throw new NullPointerException("periods was null");
	
        int count = 0;
        for (EditorEntry period : periods) {
            Map<String, Object> periodInfo = period.collectFromMutableComponent();
            String periodType = (String) periodInfo.get("Type");
            if (periodType.equals("Academic"))
                count++;
        }
        return count;
    }
    
    
    /**
     * Determines the JSON type of a period object.
     *
     * If a period is "Non-Academic" and defined by the user, the type is "Special". If the name of
     * the period is exactly "Free" (which includes the pre-defined free period), then the type is
     * "Nothing". Otherwise, the period type is a string literal of an integer in the interval 
     * {@code [1, countAcademicPeriods(periods)]} determined by the position of {@code period} in 
     * {@code periods}.
     *
     * If any argument is {@code null}, then {@code null} is returned.
     *
     * By the implementation of this algorithm, if {@code period} is "Academic" but not in 
     * {@code periods}, then {@code countAcademicPeriods(periods)} is always returned.
     *
     * @param period   the {@code Period} object to determine the type of.
     * @param periods  the list of all {@code Period} objects.
     *
     * @return the type of the period to be used in the JSON file.
     */
    private static String getPeriodType(EditorEntry period,
                                        List<EditorEntry> periods)
    {
        if (period == null || periods == null || !(period instanceof Period))
            return null;
        
        Map<String, Object> periodInfo = period.collectFromMutableComponent();
        String periodName = (String) periodInfo.get("Name");
        String periodType = (String) periodInfo.get("Type");
        // Special cases for non-academic and free periods
        if (periodName.equals("Free"))
            return "Nothing";
        if (periodType.equals("Non-Academic"))
            return "Special";
        
        // Ordinary case for an academic period that must be numbered by its position in the list
        // of all periods defined by the user.
        int periodNum = 1;
        for (EditorEntry other : periods) {
            if (!(other instanceof Period))
                continue;
            if (period.equals(other))
                break;
            
            Map<String, Object> otherInfo = other.collectFromMutableComponent();
            String otherType = (String) otherInfo.get("Type");
            // Only increment when another academic period is passed.
            if (otherType.equals("Academic"))
                periodNum++;
        }
        return Integer.toString(periodNum);
    }
    
    
    /**
     * Converts a {@code Period} object to a JSON representation. If any argument is null, then
     * {@code null} is returned.
     *
     * @param period   the {@code Period} object to convert.
     * @param periods  the list of all {@code Period} objects.
     *
     * @return a hashmap that can be converted to a JSON string representing this period.
     */
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
    
    
    /**
     * Validates data from the "Periods" section of the data generator. Any errors are returned
     * as a single string with entries separated by a newline character. If no errors are found,
     * {@code null} is returned.
     *
     * The checks performed include:
     * <ul>
     * <li> At least one period has {@code type == "Academic"}
     * <li> No duplicate names exist
     * </ul>
     *
     * @param periods  the list of all {@code Period} objects.
     *
     * @return messages describing validation errors, or {@code null} if validation was successful.
     *
     * @throws NullPointerException  if {@code periods == null}.
     */
    public static String validatePeriodList(List<EditorEntry> periods) {
        if (periods == null)
            throw new IllegalArgumentException("periods was null");
	
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
    
    
    /**
     * Creates a buffer period as a hashmap that can be converted to JSON. A buffer period is
     * one with the type "Nothing" that fills discontinuities in the period times defined
     * by the user.
     *
     * The name of the returned period is "Before Classes" if the previous end time is "00:00",
     * "After Classes" if the next start time is {@code null}, and "Between Classes" otherwise.
     *
     * @param lastEndStr    the time string in {@code HH:mm} format of the previoius period end.
     * @param nextStartStr  the time string in {@code HH:mm} format of the next period start, or
     *                      {@code null} if there is no such next period exists.
     *
     * @return a JSON-like buffer period that fills the discontinuity between the two time strings.
     *
     * @throws NullPointerException  if {@code lastEndStr == null}.
     */
    private static Map<String, String> createBufferPeriod(String lastEndStr,
                                                          String nextStartStr)
    {
        if (lastEndStr == null)
            throw new NullPointerException("lastEndStr was null");
	
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
    
    
    /**
     * Sorts a list of {@code Period} objects by their start times from earliest to latest. The
     * pointer to the list is sorted, and no value is returned. If the list is {@code null},
     * no operation occurs.
     *
     * @param periods  a list of {@code Period} objects.
     */
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
    
    
    /**
     * Converts a {@code Day} object to a JSON representation. If any argument is null, then
     * {@code null} is returned.
     *
     * @param day               the {@code Day} object to convert.
     * @param allPeriodEntries  the list of all {@code Period} objects.
     *
     * @return a hashmap that can be converted to a JSON string representing the specified day.
     */
    public static Map<String, Object> dayToJson(EditorEntry day,
                                                List<EditorEntry> allPeriodEntries)
    {
        if (day == null || allPeriodEntries == null || !(day instanceof Day))
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
            
            Map<String, String> period = WizardBuilder.periodToJson(periodEntry, allPeriodEntries);
            String startStr = period.get("Start");
            String endStr = period.get("End");
            // Add buffer periods if the user-defined periods are not back-to-back.
            if (!lastEndStr.equals(startStr)) {
                Map<String, String> bufferPeriod = WizardBuilder.createBufferPeriod(lastEndStr,
                                                                                    startStr);
                periods.add(bufferPeriod);
            }
            // If the period is "Free" and all day, then substitute with the day name, e.g. for
            // weekends or holidays
            if (period.get("Start").equals("00:00") &&
                period.get("End").equals("23:59") &&
                period.get("Type").equals("Nothing") &&
                period.get("Name").equals("Free"))
                {
                    period.put("Name", dayName);
                }
            periods.add(period);
            lastEndStr = endStr;
        }
        
        // Add an additional buffer period that would not have been added in the for-loop above
        // if the user-defined periods do not go all the way to the end of the day.
        if (!lastEndStr.equals("23:59")) {
            Map<String, String> bufferPeriod = WizardBuilder.createBufferPeriod(lastEndStr, null);
            periods.add(bufferPeriod);
        }
        
        Map<String, Object> json = new HashMap<>();
        json.put("Name", dayName);
        json.put("Periods", periods);
        return json;
    }
    
    
    /**
     * Validates data from the "Days" section of the data generator. Any errors are returned
     * as a single string with entries separated by a newline character. If no errors are found,
     * {@code null} is returned.
     *
     * The checks performed include:
     * <ul>
     * <li> No duplicate names exist
     * <li> All periods referenced in a given day exist
     * <li> Every day has at least one period
     * <li> All periods referenced in a given day have {@code startTime > endTime}
     * <li> For each period, {@code p[n]}, in a day, {@code p[n].endTime <= p[n+1].startTime}
     * </ul>
     *
     * @param days              the list of all {@code Day} objects.
     * @param allPeriodEntries  the list of all {@code Period} objects.
     *
     * @return messages describing validation errors, or {@code null} if validation was successful.
     *
     * @throws NullPointerException  if any argument is {@code null}.
     */
    public static String validateDayList(List<EditorEntry> days,
                                         List<EditorEntry> allPeriodEntries)
    {
        if (days == null)
            throw new NullPointerException("days was null");
        if (allPeriodEntries == null)
            throw new NullPointerException("allPeriodEntries was null");
	
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
    
    
    /**
     * Converts a {@code Week} object to a JSON representation. If any argument is null, then
     * {@code null} is returned.
     *
     * @param week  the {@code Week} object to convert.
     *
     * @return a hashmap that can be converted to a JSON string representing this week.
     */
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
    
    
    /**
     * Validates data from the "Weeks" section of the data generator. Any errors are returned
     * as a single string with entries separated by a newline character. If no errors are found,
     * {@code null} is returned.
     *
     * The checks performed include:
     * <ul>
     * <li> No duplicate names exist
     * <li> A week with the name "DEFAULT" exists
     * <li> All days referenced in a given week exist
     * <li> Every week has exactly seven days
     * </ul>
     *
     * @param weeks          the list of all {@code Week} objects.
     * @param allDayEntries  the list of all {@code Day} objects.
     *
     * @return messages describing validation errors, or {@code null} if validation was successful.
     *
     * @throws NullPointerException  if any argument is {@code null}.
     */
    public static String validateWeekList(List<EditorEntry> weeks,
                                          List<EditorEntry> allDayEntries)
    {
        if (weeks == null)
            throw new NullPointerException("weeks was null");
        if (allDayEntries == null)
            throw new NullPointerException("allDayEntries was null");
	
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
    
    
    /**
     * Converts a {@code WeekException} object to a JSON representation. If any argument is null,
     * then {@code null} is returned.
     *
     * @param weekException  the {@code WeekException} object to convert.
     *
     * @return a hashmap that can be converted to a JSON string representing this week exception.
     */
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
    
    
    /**
     * Validates data from the "Exceptions" section of the data generator. Any errors are returned
     * as a single string with entries separated by a newline character. If no errors are found,
     * {@code null} is returned.
     *
     * The checks performed include:
     * <ul>
     * <li> The week referenced in a given week exception exists
     * </ul>
     *
     * @param weekExceptions  the list of all {@code WeekException} objects.
     * @param allWeekEntries  the list of all {@code Week} objects.
     *
     * @return messages describing validation errors, or {@code null} if validation was successful.
     *
     * @throws NullPointerException  if any argument is {@code null}.
     */
    public static String validateWeekExceptionList(List<EditorEntry> weekExceptions,
                                                   List<EditorEntry> allWeekEntries)
    {
        if (weekExceptions == null)
            throw new NullPointerException("weekExceptions was null");
        if (allWeekEntries == null)
            throw new NullPointerException("allWeekEntries was null");
	
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
