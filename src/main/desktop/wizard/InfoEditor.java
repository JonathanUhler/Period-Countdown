package desktop.wizard;


import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.JComboBox;
import time.UTCTime;


public class InfoEditor extends JPanel {

    private JSpinner firstDayTagSpinner;
    private JSpinner lastDayTagSpinner;
    private JComboBox<String> timezoneComboBox;


    public InfoEditor() {
        this.setLayout(new GridBagLayout());

        this.firstDayTagSpinner = new JSpinner(new SpinnerDateModel());
        this.lastDayTagSpinner = new JSpinner(new SpinnerDateModel());
        this.timezoneComboBox = new JComboBox<>(UTCTime.TIMEZONES);

        this.firstDayTagSpinner.setEditor(new JSpinner.DateEditor(this.firstDayTagSpinner,
                                                                  "yyyy-MM-dd"));
        this.lastDayTagSpinner.setEditor(new JSpinner.DateEditor(this.lastDayTagSpinner,
                                                                 "yyyy-MM-dd"));
        
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
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
        this.add(new JLabel("Timezone"), gbc);
        gbc.gridx++;
        this.add(this.timezoneComboBox, gbc);
    }


    public String getFirstDayTag() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format((Date) this.firstDayTagSpinner.getValue());
    }


    public String getLastDayTag() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format((Date) this.lastDayTagSpinner.getValue());
    }


    public String getTimezone() {
        return (String) this.timezoneComboBox.getSelectedItem();
    }
    
}
