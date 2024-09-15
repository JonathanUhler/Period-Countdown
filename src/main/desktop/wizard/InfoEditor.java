package desktop.wizard;


import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.ZoneId;
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
import school.SchoolJson;


public class InfoEditor extends JPanel {

    private JSpinner firstDayTagSpinner;
    private JSpinner lastDayTagSpinner;
    private JComboBox<String> timezoneComboBox;


    public InfoEditor() {
        this.setLayout(new GridBagLayout());

        this.firstDayTagSpinner = new JSpinner(new SpinnerDateModel());
        this.lastDayTagSpinner = new JSpinner(new SpinnerDateModel());
        this.timezoneComboBox = new JComboBox<>(UTCTime.TIMEZONES);

        this.timezoneComboBox.setSelectedItem(ZoneId.systemDefault().getId());
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


    public Map<String, String> collect(int numAcademicPeriods, List<String> errors) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date firstDate = (Date) this.firstDayTagSpinner.getValue();
        Date lastDate = (Date) this.lastDayTagSpinner.getValue();
        String lastPeriod = Integer.toString(numAcademicPeriods);
        String firstDayTag = dateFormat.format(firstDate);
        String lastDayTag = dateFormat.format(lastDate);
        String timezone = (String) this.timezoneComboBox.getSelectedItem();

        if (!firstDate.before(lastDate)) {
            errors.add("Info: First Date must be before Last Date");
        }

        Map<String, String> data = new HashMap<>();
        data.put(SchoolJson.FIRST_PERIOD, "1");
        data.put(SchoolJson.LAST_PERIOD, lastPeriod);
        data.put(SchoolJson.FIRST_DAY_TAG, firstDayTag);
        data.put(SchoolJson.LAST_DAY_TAG, lastDayTag);
        data.put(SchoolJson.TIMEZONE, timezone);
        return data;
    }
    
}
