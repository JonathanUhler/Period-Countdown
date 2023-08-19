package wizard.editor;


import wizard.entry.Day;
import wizard.interfaces.EditorViewport;


/**
 * Graphical editor for defining {@code Day} objects.
 *
 * @author Jonathan Uhler
 */
public class DayEditor extends EditorViewport<Day> {

	/**
	 * Constructs a new mutable {@code DayEditor}.
	 */
	public DayEditor() {
		this(true);
	}
	

	/**
	 * Constructs a new {@code DayEditor} with the specified mutability.
	 *
	 * @param mutable  whether entries of this editor are mutable.
	 */
	public DayEditor(boolean mutable) {
		super(mutable);
	}
	

	/**
	 * Creates and returns a new {@code Day} object with the specified mutability.
	 *
	 * @param mutable  whether the entry is mutable.
	 */
	@Override
	public Day entryFactory(boolean mutable) {
		return new Day(mutable);
	}

}
