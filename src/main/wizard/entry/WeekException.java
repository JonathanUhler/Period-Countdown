package wizard.entry;


import wizard.WizardManager;
import wizard.interfaces.EditorEntry;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.awt.GridBagConstraints;
import javax.swing.JSpinner;
import javax.swing.JComboBox;
import javax.swing.SpinnerDateModel;
import javax.swing.JLabel;


public class WeekException extends EditorEntry {

	private JSpinner dateSpinner;
	private JComboBox<EditorEntry> weekTypeComboBox;


	public WeekException() {
		super();
	}


	public WeekException(boolean mutable) {
		super(mutable);
	}


	@Override
	public void preinitComponent() {
		SpinnerDateModel sdm = new SpinnerDateModel(new Date(), null, null, Calendar.MONTH);
		this.dateSpinner = new JSpinner(sdm);
		this.weekTypeComboBox = new JComboBox<>();
		
		JSpinner.DateEditor de = new JSpinner.DateEditor(this.dateSpinner, "yyyy-MM-dd");
		this.dateSpinner.setEditor(de);
	}


	@Override
	public void initMutableComponent() {
		GridBagConstraints gbc = super.getLayoutConstraints();

		EditorEntry lastSelected = (EditorEntry) this.weekTypeComboBox.getSelectedItem();
		this.weekTypeComboBox = new JComboBox<>();
		List<EditorEntry> defined = WizardManager.getWeekEditor().getEditorList().getEntries();
		for (EditorEntry e : defined)
			this.weekTypeComboBox.addItem(e);
		this.weekTypeComboBox.setSelectedItem(lastSelected);

		gbc.gridy = 1;
		this.add(new JLabel("Date of Exception: "), gbc);

		gbc.gridx++;
		this.add(this.dateSpinner, gbc);

		gbc.gridx++;
		this.add(new JLabel(", Week Type: "), gbc);

		gbc.gridx++;
		this.add(this.weekTypeComboBox, gbc);
	}


	@Override
	public void initImmutableComponent() { }


	@Override
	public Map<String, Object> collectFromMutableComponent() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date weekTag = (Date) this.dateSpinner.getValue();
		
		Map<String, Object> m = new HashMap<>();
		m.put("WeekTag", df.format(weekTag));
		m.put("Type", this.weekTypeComboBox.getSelectedItem());
		return m;
	}

}
