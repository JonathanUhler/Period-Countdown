package wizard.editor;


import wizard.entry.Day;
import wizard.interfaces.EditorViewport;


public class DayEditor extends EditorViewport<Day> {

	public DayEditor() {
		this(true);
	}
	

	public DayEditor(boolean mutable) {
		super(mutable);
	}
	

	@Override
	public Day entryFactory(boolean mutable) {
		return new Day(mutable);
	}

}
