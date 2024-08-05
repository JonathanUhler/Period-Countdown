package wizard.entry;


import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import wizard.WizardManager;
import wizard.interfaces.EditorEntry;
import wizard.editor.PeriodEditor;


/**
 * Graphical entry point for information that defines a single day.
 *
 * @author Jonathan Uhler
 */
public class Day extends EditorEntry {
    
    /** An editor to enter all periods of this day. */
    private PeriodEditor editor;
    /** A data entry field to enter the name of this day. */
    private JTextField nameTextField;
    /** A selector to choose a pre-defined day type for if this entry is immutable. */
    private JComboBox<EditorEntry> immutableSelectorComboBox;
    /**
     * Whether the immutableSelectorComboBox can be used. This is a distinct value from whether 
     * this entry is mutable, and can be overriden by the constructing class if desided. If set to 
     * {@code false} and the entry is immutable, a label is displayed with the combo box's selected
     * value instead of the combo box itself.
     */
    private boolean immutableComboBoxUsable;
    
    
    /**
     * Constructs a new mutable {@code Day} object.
     */
    public Day() {
        super();
    }
    
    
    /**
     * Constructs a new {@code Day} object with the specified mutability.
     *
     * @param mutable  whether this entry is mutable.
     */
    public Day(boolean mutable) {
        super(mutable);
    }
    
    
    /**
     * Creates an immutable day with the name of "Weekend" and a single, day-long free period.
     *
     * @return an immutable day with the name of "Weekend" and a single, day-long free period.
     */
    public static Day getWeekend() {
        Day d = new Day(false);
        d.nameTextField.setText("Weekend");
        d.editor.addManualEntry(Period.getFreeDayPeriod());
        d.setImmutableComboBoxUsable(false);
        return d;
    }
    
    
    /**
     * Creates an immutable day with the name of "Holiday" and a single, day-long free period.
     *
     * @return an immutable day with the name of "Holiday" and a single, day-long free period.
     */
    public static Day getHoliday() {
        Day d = new Day(false);
        d.nameTextField.setText("Holiday");
        d.editor.addManualEntry(Period.getFreeDayPeriod());
        d.setImmutableComboBoxUsable(false);
        return d;
    }
    
    
    /**
     * Sets whether the immutableSelectorComboBox can be used. This is a distinct value from 
     * whether this entry is mutable, and can be overriden by the constructing class if desided. 
     * If set to {@code false} and the entry is immutable, a label is displayed with the combobox's
     * selected value instead of the combobox itself.
     *
     * @param usable  whether the combobox selector is usable if this component is immutable.
     */
    public void setImmutableComboBoxUsable(boolean usable) {
        this.immutableComboBoxUsable = usable;
    }
    
    
    @Override
    public void preinitComponent() {
        this.editor = new PeriodEditor(false);
        this.nameTextField = new JTextField();
        this.immutableSelectorComboBox = new JComboBox<>();
        this.immutableComboBoxUsable = true;
        
        this.nameTextField.setColumns(8);
    }
    
    
    @Override
    public void initMutableComponent() {
        GridBagConstraints gbc = super.getLayoutConstraints();
	
        gbc.gridy = 0;
        this.add(new JLabel("Name: "), gbc);
        
        gbc.gridx++;
        this.add(this.nameTextField, gbc);
        
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.gridwidth = 2;
        this.add(this.editor, gbc);
    }
    
    
    @Override
    public void initImmutableComponent() {
        GridBagConstraints gbc = super.getLayoutConstraints();
        
        EditorEntry lastSelected = (EditorEntry) this.immutableSelectorComboBox.getSelectedItem();
        this.immutableSelectorComboBox = new JComboBox<>();
        List<EditorEntry> defined = WizardManager.getDayEditor().getEditorList().getEntries();
        for (EditorEntry e : defined)
            this.immutableSelectorComboBox.addItem(e);
        this.immutableSelectorComboBox.setSelectedItem(lastSelected);
        this.immutableSelectorComboBox.addActionListener(e -> this.immutableSelectorAction());
	
        gbc.gridy = 1;
        if (this.immutableComboBoxUsable)
            this.add(this.immutableSelectorComboBox, gbc);
        else
            this.add(new JLabel("(" + this.nameTextField.getText() + ") "), gbc);
        
        gbc.gridx++;
        String periodStr = "";
        for (EditorEntry e : this.editor.getEditorList().getEntries())
            periodStr += e + "<br>";
        this.add(new JLabel("<html>" + periodStr + "</html>"), gbc);
    }
    
    
    /**
     * Acts on an entry change of the immutable selector combobox.
     */
    private void immutableSelectorAction() {
        EditorEntry entry = (EditorEntry) this.immutableSelectorComboBox.getSelectedItem();
        if (entry == null)
            return;
        
        Map<String, Object> m = entry.collectFromMutableComponent();
        this.nameTextField.setText((String) m.get("Name"));
        this.editor = (PeriodEditor) m.get("Periods");
        
        this.reinitComponent();
    }
    
    
    @Override
    public Map<String, Object> collectFromMutableComponent() {
        Map<String, Object> m = new HashMap<>();
        m.put("Name", this.nameTextField.getText());
        m.put("Periods", this.editor);
        return m;
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Day))
            return false;
        
        Map<String, Object> oInfo = ((Day) o).collectFromMutableComponent();
        String oName = (String) oInfo.get("Name");
        
        Map<String, Object> myInfo = this.collectFromMutableComponent();
        String myName = (String) myInfo.get("Name");
        
        return myName.equals(oName);
    }
    
    
    @Override
    public String toString() {
        if (this.nameTextField != null)
            return this.nameTextField.getText();
        return "(Unnamed Day)";
    }
    
}
