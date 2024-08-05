package wizard.entry;


import wizard.WizardManager;
import wizard.interfaces.EditorEntry;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.JComboBox;
import javax.swing.JLabel;


/**
 * Graphical entry point for information that defines a single period.
 *
 * @author Jonathan Uhler
 */
public class Period extends EditorEntry {

    /** A data entry field to enter the name of this period. */
    private JTextField nameTextField;
    /** A data entry field to enter the start time of this period in HH:mm format. */
    private JFormattedTextField startTimeField;
    /** A data entry field to enter the end time of this period in HH:mm format. */
    private JFormattedTextField endTimeField;
    /** A selector to choose the type of this period as either academic or non-academic. */
    private JComboBox<String> typeComboBox;
    /** A selector to choose a pre-defined period type for if this entry is immutable. */
    private JComboBox<EditorEntry> immutableSelectorComboBox;
    /**
     * Whether the immutableSelectorComboBox can be used. This is a distinct value from whether 
     * this entry is mutable, and can be overriden by the constructing class if desided. If set to 
     * {@code false} and the entry is immutable, a label is displayed with the combo box's selected
     * value instead of the combo box itself.
     */
    private boolean immutableComboBoxUsable;


    /**
     * Constructs a new mutable {@code Period} object.
     */
    public Period() {
        super();
    }


    /**
     * Constructs a new {@code Period} object with the specified mutability.
     *
     * @param mutable  whether this entry is mutable.
     */
    public Period(boolean mutable) {
        super(mutable);
    }


    /**
     * Creates an immutable period that spans the entire day and will result with a "Nothing" type.
     *
     * @return an immutable period that spans the entire day and will result with a "Nothing" type.
     */
    public static Period getFreeDayPeriod() {
        Period p = new Period(false);
        p.nameTextField.setText("Free");
        p.startTimeField.setText("00:00");
        p.endTimeField.setText("23:59");
        p.typeComboBox.setSelectedItem("Non-Academic");
        p.immutableSelectorComboBox.setSelectedItem(p);
        p.setImmutableComboBoxUsable(false);
        return p;
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
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");

        this.nameTextField = new JTextField();
        this.startTimeField = new JFormattedTextField(dateFormat);
        this.endTimeField = new JFormattedTextField(dateFormat);
        this.typeComboBox = new JComboBox<>(new String[] {"Academic", "Non-Academic"});
        this.immutableSelectorComboBox = new JComboBox<>();
        this.immutableComboBoxUsable = true;

        this.nameTextField.setColumns(8);
        this.startTimeField.setColumns(4);
        this.endTimeField.setColumns(4);
    }


    @Override
    public void initMutableComponent() {
        GridBagConstraints gbc = super.getLayoutConstraints();
		
        gbc.gridy = 1;
        this.add(new JLabel("Name: "), gbc);

        gbc.gridx++;
        this.add(this.nameTextField, gbc);

        gbc.gridx++;
        this.add(new JLabel("Type: "), gbc);

        gbc.gridx++;
        this.add(this.typeComboBox, gbc);
    }


    @Override
    public void initImmutableComponent() {
        GridBagConstraints gbc = super.getLayoutConstraints();

        EditorEntry lastSelected = (EditorEntry) this.immutableSelectorComboBox.getSelectedItem();
        this.immutableSelectorComboBox = new JComboBox<>();
        List<EditorEntry> defined = WizardManager.getPeriodEditor().getEditorList().getEntries();
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
        this.add(new JLabel("Type: " + this.typeComboBox.getSelectedItem() + " "), gbc);

        gbc.gridx++;
        this.add(new JLabel(" Start: "), gbc);

        gbc.gridx++;
        this.add(this.startTimeField, gbc);

        gbc.gridx++;
        this.add(new JLabel("End: "), gbc);

        gbc.gridx++;
        this.add(this.endTimeField, gbc);
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
        this.typeComboBox.setSelectedItem((String) m.get("Type"));

        this.reinitComponent();
    }


    @Override
    public Map<String, Object> collectFromMutableComponent() {
        Map<String, Object> m = new HashMap<>();
        m.put("Name", this.nameTextField.getText());
        m.put("Start", this.startTimeField.getText());
        m.put("End", this.endTimeField.getText());
        m.put("Type", this.typeComboBox.getSelectedItem());
        return m;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Period))
            return false;

        Map<String, Object> oInfo = ((Period) o).collectFromMutableComponent();
        String oName = (String) oInfo.get("Name");
        String oType = (String) oInfo.get("Type");

        Map<String, Object> myInfo = this.collectFromMutableComponent();
        String myName = (String) myInfo.get("Name");
        String myType = (String) myInfo.get("Type");

        return myName.equals(oName) && myType.equals(oType);
    }


    @Override
    public String toString() {
        if (this.nameTextField != null)
            return this.nameTextField.getText();
        return "(Unnamed Period)";
    }

}
