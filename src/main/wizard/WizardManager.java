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


/**
 * Static manager for all the data entered by the user. The protected {@code setup} method must be
 * called before operations can be performed.
 *
 * @author Jonathan Uhler
 */
public class WizardManager {

	/** A list of all editor entries of every type, used to reinit when tabs are switched. */
	private static List<EditorEntry> entries;

	/** Whether the info tab has been visited and errors should be displayed. */
	private static boolean visitedInfo;
	/** Whether the periods tab has been visited and errors should be displayed. */
	private static boolean visitedPeriods;
	/** Whether the days tab has been visited and errors should be displayed. */
	private static boolean visitedDays;
	/** Whether the weeks tab has been visited and errors should be displayed. */
	private static boolean visitedWeeks;
	/** Whether the week exceptions tab has been visited and errors should be displayed. */
	private static boolean visitedWeekExceptions;

	/** The data editor for basic info. */
	private static InfoEditor infoEditor;
	/** The data editor for periods. */
	private static PeriodEditor periodEditor;
	/** The data editor for days. */
	private static DayEditor dayEditor;
	/** The data editor for weeks. */
	private static WeekEditor weekEditor;
	/** The data editor for week exceptions. */
	private static WeekExceptionEditor weekExceptionEditor;
	/** The viewer for the final json output. */
	private static JSONViewer jsonViewer;


	/**
	 * Initializes the members of the {@code WizardManager} class. This method must be called
	 * by a package class before other methods can be used.
	 */
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


	/**
	 * Remembers an editor entry, whose {@code reinitComponent} method is called when data tabs
	 * are switched.
	 *
	 * @param entry  the editor entry to register for reinitialization.
	 */
	public static void registerEditorEntry(EditorEntry entry) {
		WizardManager.entries.add(entry);
	}


	/**
	 * Notifies all registered editor entries of a change in data tab. This method also performs
	 * validation on all existing data and generates the final json output, if no errors are
	 * present.
	 *
	 * @param switchedToIndex  the index of the data tab that the user is now viewing.
	 */
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


	/**
	 * Returns the info editor.
	 *
	 * @return the info editor.
	 */
	public static InfoEditor getInfoEditor() {
		return WizardManager.infoEditor;
	}


	/**
	 * Returns the period editor.
	 *
	 * @return the period editor.
	 */
	public static PeriodEditor getPeriodEditor() {
		return WizardManager.periodEditor;
	}


	/**
	 * Returns the day editor.
	 *
	 * @return the day editor.
	 */
	public static DayEditor getDayEditor() {
		return WizardManager.dayEditor;
	}


	/**
	 * Returns the week editor.
	 *
	 * @return the week editor.
	 */
	public static WeekEditor getWeekEditor() {
		return WizardManager.weekEditor;
	}


	/**
	 * Returns the week exception editor.
	 *
	 * @return the week exception editor.
	 */
	public static WeekExceptionEditor getWeekExceptionEditor() {
		return WizardManager.weekExceptionEditor;
	}


	/**
	 * Returns the json viewer.
	 *
	 * @return the json viewer.
	 */
	public static JSONViewer getJsonViewer() {
		return WizardManager.jsonViewer;
	}

}
