package wizard.interfaces;


import java.util.List;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;

/**
 * Graphical viewport to handle the display of an {@code EditorList} to which entries of
 * a specific type can be added.
 *
 * @author Jonathan Uhler
 */
public abstract class EditorViewport<E extends EditorEntry> extends JPanel {
    
    /** Whether entries in this viewport are mutable. */
    private boolean mutable;
    /** The list of editor entries associated with this viewport. */
    private EditorList<E> list;
    /** A button to add a new entry to the list. */
    private JButton addEntryButton;
    
    
    /**
     * Constructs a new {@code EditorViewport} that is mutable. For more information on mutability,
     * see {@code EditorEntry}.
     *
     * @see EditorEntry
     */
    public EditorViewport() {
        this(true);
    }
    
    
    /**
     * Constructs a new {@code EditorViewport} with the specified mutability. For more information
     * on mutability, see {@code EditorEntry}.
     *
     * @param mutable  wehther entries of this viewport are mutable.
     *
     * @see EditorEntry
     */
    public EditorViewport(boolean mutable) {
        this.setLayout(new GridBagLayout());
	
        this.mutable = mutable;
        this.list = new EditorList<>();
        this.addEntryButton = new JButton("New Entry");
	
        this.addEntryButton.addActionListener(e -> this.addManualEntry(this.entryFactory(mutable)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(new JScrollPane(this.list), gbc);
        
        gbc.gridy++;
        this.add(this.addEntryButton, gbc);
    }
    
    
    /**
     * Adds an entry to the viewport's list manually. Automatic adds should call the
     * {@code entryFactory} method of this class. After the add, the viewport and its list
     * are repainted.
     *
     * @param entry  the {@code EditorEntry} to add.
     */
    public void addManualEntry(EditorEntry entry) {
        this.list.addEntry(entry);
        this.list.revalidate();
        this.list.repaint();
        this.revalidate();
        this.repaint();
    }
    
    
    /**
     * Returns the {@code EditorList} associated with this viewport.
     *
     * @return the {@code EditorList} associated with this viewport.
     */
    public EditorList<E> getEditorList() {
        return this.list;
    }
    
    
    /**
     * Constructs and returns a new, non-{@code null} editor entry of the type supported by this
     * viewport. The entry should have the same mutability as specified by the argument.
     *
     * @param mutable  whether the returned entry is mutable.
     *
     * @return a new, non-{@code null} editor entry of the type supported by this viewport.
     */
    public abstract E entryFactory(boolean mutable);
    
}
