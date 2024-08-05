package wizard.editor;


import wizard.entry.Period;
import wizard.interfaces.EditorViewport;


/**
 * Graphical editor for defining {@code Period} objects.
 *
 * @author Jonathan Uhler
 */
public class PeriodEditor extends EditorViewport<Period> {
    
    /**
     * Constructs a new mutable {@code PeriodEditor}.
     */
    public PeriodEditor() {
        this(true);
    }
    
    
    /**
     * Constructs a new {@code PeriodEditor} with the specified mutability.
     *
     * @param mutable  whether entries of this editor are mutable.
     */
    public PeriodEditor(boolean mutable) {
        super(mutable);
    }
    
    
    /**
     * Creates and returns a new {@code Period} object with the specified mutability.
     *
     * @param mutable  whether the entry is mutable.
     */
    @Override
    public Period entryFactory(boolean mutable) {
        return new Period(mutable);
    }
    
}
