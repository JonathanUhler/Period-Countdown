package wizard.editor;


import wizard.entry.Week;
import wizard.interfaces.EditorViewport;


/**
 * Graphical editor for defining {@code Week} objects.
 *
 * @author Jonathan Uhler
 */
public class WeekEditor extends EditorViewport<Week> {

	/**
	 * Constructs a new mutable {@code WeekEditor} with a "DEFAULT" week.
	 */
	public WeekEditor() {
		this(true);
	}
	

	/**
	 * Constructs a new {@code WeekEditor} with the specified mutability and a "DEFAULT" week.
	 *
	 * @param mutable  whether entries of this editor are mutable.
	 */
	public WeekEditor(boolean mutable) {
		super(mutable);

		super.addManualEntry(Week.getDefaultWeek());
	}
	

	/**
	 * Creates and returns a new {@code Week} object with the specified mutability.
	 *
	 * @param mutable  whether the entry is mutable.
	 */
	@Override
	public Week entryFactory(boolean mutable) {
		return new Week(mutable);
	}

}
