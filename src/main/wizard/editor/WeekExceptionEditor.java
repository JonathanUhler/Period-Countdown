package wizard.editor;


import wizard.entry.WeekException;
import wizard.interfaces.EditorViewport;


public class WeekExceptionEditor extends EditorViewport<WeekException> {

	public WeekExceptionEditor() {
		this(true);
	}
	

	public WeekExceptionEditor(boolean mutable) {
		super(mutable);
	}
	

	@Override
	public WeekException entryFactory(boolean mutable) {
		return new WeekException(mutable);
	}

}
