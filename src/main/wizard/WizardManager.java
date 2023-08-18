package wizard;


import wizard.interfaces.EditorEntry;
import wizard.entry.Period;
import wizard.entry.Day;
import wizard.editor.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class WizardManager {

	private static List<EditorEntry> entries;

	private static boolean visitedInfo;
	private static boolean visitedPeriods;
	private static boolean visitedDays;
	private static boolean visitedWeeks;
	private static boolean visitedWeekExceptions;
	
	private static InfoEditor infoEditor;
	private static PeriodEditor periodEditor;
	private static DayEditor dayEditor;
	private static WeekEditor weekEditor;
	private static WeekExceptionEditor weekExceptionEditor;
	private static JSONViewer jsonViewer;


	protected static void setup() {
		WizardManager.entries = new ArrayList<>();

		WizardManager.visitedInfo = false;
		WizardManager.visitedPeriods = false;
		WizardManager.visitedDays = false;
		WizardManager.visitedWeeks = false;
		WizardManager.visitedWeekExceptions = false;

		WizardManager.infoEditor = new InfoEditor();
		WizardManager.periodEditor = new PeriodEditor();
		WizardManager.dayEditor = new DayEditor();
		WizardManager.weekEditor = new WeekEditor();
		WizardManager.weekExceptionEditor = new WeekExceptionEditor();
		WizardManager.jsonViewer = new JSONViewer();

		WizardManager.periodEditor.addManualEntry(Period.getFreeDayPeriod());
		WizardManager.dayEditor.addManualEntry(Day.getWeekend());
		WizardManager.dayEditor.addManualEntry(Day.getHoliday());
	}


	public static void registerEditorEntry(EditorEntry entry) {
		WizardManager.entries.add(entry);
	}


	protected static void notifyEditorEntries(int switchedToIndex) {
		for (EditorEntry entry : WizardManager.entries)
			entry.reinitComponent();

		String infoFirstDayTag = WizardManager.infoEditor.getFirstDayTag();
		String infoLastDayTag = WizardManager.infoEditor.getLastDayTag();
		String infoTimezone = WizardManager.infoEditor.getTimezone();
		String infoErrors = WizardBuilder.validateInfo(infoFirstDayTag, infoLastDayTag);
		if (infoErrors != null && WizardManager.visitedInfo)
			Wizard.displayMessage("Errors Exist in 'Info'", infoErrors);

		List<EditorEntry> periods = WizardManager.periodEditor.getEditorList().getEntries();
		String periodErrors = WizardBuilder.validatePeriodList(periods);
		if (periodErrors != null && WizardManager.visitedPeriods)
			Wizard.displayMessage("Errors Exist in 'Periods'", periodErrors);
		
		List<EditorEntry> days = WizardManager.dayEditor.getEditorList().getEntries();
		String dayErrors = WizardBuilder.validateDayList(days, periods);
		if (dayErrors != null && WizardManager.visitedDays)
			Wizard.displayMessage("Errors Exist in 'Days'", dayErrors);
		
		List<EditorEntry> weeks = WizardManager.weekEditor.getEditorList().getEntries();
		String weekErrors = WizardBuilder.validateWeekList(weeks, days);
		if (weekErrors != null && WizardManager.visitedWeeks)
			Wizard.displayMessage("Errors Exist in 'Weeks'", weekErrors);

		List<EditorEntry> weekExs = WizardManager.weekExceptionEditor.getEditorList().getEntries();
		String weekExceptionErrors = WizardBuilder.validateWeekExceptionList(weekExs, weeks);
		if (weekExceptionErrors != null && WizardManager.visitedWeekExceptions)
			Wizard.displayMessage("Errors Exist in 'Week Exceptions'", weekExceptionErrors);

		switch (switchedToIndex) {
		case 0 -> WizardManager.visitedInfo = true;
		case 1 -> WizardManager.visitedPeriods = true;
		case 2 -> WizardManager.visitedDays = true;
		case 3 -> WizardManager.visitedWeeks = true;
		case 4 -> WizardManager.visitedWeekExceptions = true;
		}

		String errors = "";
		if (infoErrors != null)
			errors += "# Errors Exist in 'Info'\n" + infoErrors;
		if (periodErrors != null)
			errors += "# Errors Exist in 'Periods'\n" + periodErrors;
		if (dayErrors != null)
			errors += "# Errors Exist in 'Days'\n" + dayErrors;
		if (weekErrors != null)
			errors += "# Errors Exist in 'Week'\n" + weekErrors;
		if (weekExceptionErrors != null)
			errors += "# Errors Exist in 'Week Exceptions'\n" + weekExceptionErrors;

		if (!errors.equals(""))
			WizardManager.jsonViewer.setJson(errors);
		else {
			Map<String, Object> json = WizardBuilder.build(infoFirstDayTag,
														   infoLastDayTag,
														   infoTimezone,
														   periods, days,
														   weeks, weekExs);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonStr = gson.toJson(json);
			WizardManager.jsonViewer.setJson(jsonStr);
		}
	}


	public static InfoEditor getInfoEditor() {
		return WizardManager.infoEditor;
	}


	public static PeriodEditor getPeriodEditor() {
		return WizardManager.periodEditor;
	}


	public static DayEditor getDayEditor() {
		return WizardManager.dayEditor;
	}


	public static WeekEditor getWeekEditor() {
		return WizardManager.weekEditor;
	}


	public static WeekExceptionEditor getWeekExceptionEditor() {
		return WizardManager.weekExceptionEditor;
	}


	public static JSONViewer getJsonViewer() {
		return WizardManager.jsonViewer;
	}

}
