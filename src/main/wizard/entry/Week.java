package wizard.entry;


import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JTextField;
import wizard.interfaces.EditorEntry;
import wizard.editor.DayEditor;


/**
 * Graphical entry point for information that defines a single week.
 *
 * @author Jonathan Uhler
 */
public class Week extends EditorEntry {
    
    /** An editor to enter all days of this week. */
    private DayEditor editor;
    /** A data entry field to enter the name of this week. */
    private JTextField nameTextField;
    
    
    /**
     * Constructs a new mutable {@code Week} object.
     */
    public Week() {
        super();
    }
    
    
    /**
     * Constructs a new {@code Week} object with the specified mutability.
     *
     * @param mutable  whether this entry is mutable.
     */
    public Week(boolean mutable) {
        super(mutable);
    }
    
    
    /**
     * Creates a mutable week with the preset name of "DEFAULT".
     *
     * @return a mutable week with the preset name of "DEFAULT".
     */
    public static Week getDefaultWeek() {
        Week w = new Week();
        w.nameTextField.setText("DEFAULT");
        return w;
    }
    
    
    @Override
    public void preinitComponent() {
        this.editor = new DayEditor(false);
        this.nameTextField = new JTextField();
        
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
    public void initImmutableComponent() { }
    
    
    @Override
    public Map<String, Object> collectFromMutableComponent() {
        Map<String, Object> m = new HashMap<>();
        m.put("Name", this.nameTextField.getText());
        m.put("Days", this.editor);
        return m;
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Week))
            return false;
        
        Map<String, Object> oInfo = ((Week) o).collectFromMutableComponent();
        String oName = (String) oInfo.get("Name");
        
        Map<String, Object> myInfo = this.collectFromMutableComponent();
        String myName = (String) myInfo.get("Name");
        
        return myName.equals(oName);
    }
    
    
    @Override
    public String toString() {
        if (this.nameTextField != null)
            return this.nameTextField.getText();
        return "(Unnamed Week)";
    }
    
}
