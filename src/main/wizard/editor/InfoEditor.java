package wizard.editor;


import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;


/**
 * Graphical editor for the school start date, end date, and timezone.
 *
 * @author Jonathan Uhler
 */
public class InfoEditor extends JPanel {

	/** Data entry form for the first date of school. */
	private JSpinner firstDayTagSpinner;
	/** Data entry form for the last date of school. */
	private JSpinner lastDayTagSpinner;
	/** Data entry form for the timezone. */
	private JTextField timezoneTextField;
	

	/**
	 * Constructs a new {@code InfoEditor}.
	 */
	public InfoEditor() {
		this.setLayout(new GridBagLayout());

		SpinnerDateModel sdm1 = new SpinnerDateModel(new Date(), null, null, Calendar.MONTH);
		SpinnerDateModel sdm2 = new SpinnerDateModel(new Date(), null, null, Calendar.MONTH);
		this.firstDayTagSpinner = new JSpinner(sdm1);
		this.lastDayTagSpinner = new JSpinner(sdm2);
		this.timezoneTextField = new JTextField();

		JSpinner.DateEditor de1 = new JSpinner.DateEditor(this.firstDayTagSpinner, "yyyy-MM-dd");
		JSpinner.DateEditor de2 = new JSpinner.DateEditor(this.lastDayTagSpinner, "yyyy-MM-dd");
		this.firstDayTagSpinner.setEditor(de1);
		this.lastDayTagSpinner.setEditor(de2);
		this.timezoneTextField.setColumns(12);
		this.timezoneTextField.setToolTipText("The Unix tz_id for the institution timezone.");

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(new JLabel("First Date"), gbc);
		gbc.gridx++;
		this.add(this.firstDayTagSpinner, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		this.add(new JLabel("Last Date"), gbc);
		gbc.gridx++;
		this.add(this.lastDayTagSpinner, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		this.add(new JLabel("TZ Identifier"), gbc);
		gbc.gridx++;
		this.add(this.timezoneTextField, gbc);
	}


	/**
	 * Returns the first date of school in {@code yyyy-MM-dd} format.
	 *
	 * @return the first day of school.
	 */
	public String getFirstDayTag() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format((Date) this.firstDayTagSpinner.getValue());
	}


	/**
	 * Returns the last date of school in {@code yyyy-MM-dd} format.
	 *
	 * @return the last day of school.
	 */
	public String getLastDayTag() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format((Date) this.lastDayTagSpinner.getValue());
	}


	/**
	 * Returns the unix TZ identifier.
	 *
	 * @return the unix TZ identifier.
	 */
	public String getTimezone() {
		return this.timezoneTextField.getText();
	}

}
