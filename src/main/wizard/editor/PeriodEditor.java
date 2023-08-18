package wizard.editor;


import wizard.entry.Period;
import wizard.interfaces.EditorViewport;


public class PeriodEditor extends EditorViewport<Period> {

	public PeriodEditor() {
		super();
	}
	

	public PeriodEditor(boolean mutable) {
		super(mutable);
	}
	

	@Override
	public Period entryFactory(boolean mutable) {
		return new Period(mutable);
	}

}
