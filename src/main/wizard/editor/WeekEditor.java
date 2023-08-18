package wizard.editor;


import wizard.entry.Week;
import wizard.interfaces.EditorViewport;


public class WeekEditor extends EditorViewport<Week> {

	public WeekEditor() {
		this(true);
	}
	

	public WeekEditor(boolean mutable) {
		super(mutable);

		super.addManualEntry(Week.getDefaultWeek());
	}
	

	@Override
	public Week entryFactory(boolean mutable) {
		return new Week(mutable);
	}

}
