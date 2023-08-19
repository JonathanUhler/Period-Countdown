package wizard.editor;


import wizard.entry.WeekException;
import wizard.interfaces.EditorViewport;


/**
 * Graphical editor for defining {@code WeekException} objects.
 *
 * @author Jonathan Uhler
 */
public class WeekExceptionEditor extends EditorViewport<WeekException> {

	/**
	 * Constructs a new mutable {@code WeekExceptionEditor}.
	 */
	public WeekExceptionEditor() {
		this(true);
	}
	

	/**
	 * Constructs a new {@code WeekExceptionEditor} with the specified mutability.
	 *
	 * @param mutable  whether entries of this editor are mutable.
	 */
	public WeekExceptionEditor(boolean mutable) {
		super(mutable);
	}
	

	/**
	 * Creates and returns a new {@code WeekException} object with the specified mutability.
	 *
	 * @param mutable  whether the entry is mutable.
	 */
	@Override
	public WeekException entryFactory(boolean mutable) {
		return new WeekException(mutable);
	}

}
