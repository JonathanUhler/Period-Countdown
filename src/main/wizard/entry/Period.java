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


public class Period extends EditorEntry {

	private JTextField nameTextField;
	private JFormattedTextField startTimeField;
	private JFormattedTextField endTimeField;
	private JComboBox<String> typeComboBox;
	private JComboBox<EditorEntry> immutableSelectorComboBox;
	private boolean immutableComboBoxUsable;


	public Period() {
		super();
	}


	public Period(boolean mutable) {
		super(mutable);
	}


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
