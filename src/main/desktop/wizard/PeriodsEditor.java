package desktop.wizard;


import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import school.SchoolPeriod;
import school.SchoolJson;


public class PeriodsEditor extends JPanel {

    private class PeriodEntry extends JPanel {

        public JComboBox<String> typeComboBox;
        public JTextField nameTextField;
        public JSpinner startSpinner;
        public JSpinner endSpinner;


        public PeriodEntry() {
            this.typeComboBox = new JComboBox<>(new String[] {"Academic",
                                                              SchoolJson.NOTHING,
                                                              SchoolJson.SPECIAL});
            this.nameTextField = new JTextField("", 10);
            this.startSpinner =
                new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.HOUR));
            this.endSpinner =
                new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.HOUR));

            this.startSpinner.setEditor(new JSpinner.DateEditor(this.startSpinner, "HH:mm"));
            this.endSpinner.setEditor(new JSpinner.DateEditor(this.endSpinner, "HH:mm"));

            this.add(new JLabel("Name"));
            this.add(this.nameTextField);
            this.add(new JLabel("Type"));
            this.add(this.typeComboBox);
            this.add(new JLabel("Start"));
            this.add(this.startSpinner);
            this.add(new JLabel("End"));
            this.add(this.endSpinner);
        }

    }


    private EntryList<PeriodEntry> entries;


    public PeriodsEditor() {
        this.entries = new EntryList<>() {
                @Override
                public PeriodEntry entryFactory() {
                    return new PeriodEntry();
                }
            };

        this.add(new JScrollPane(this.entries));
    }


    public String[] getPeriodNames() {
        List<String> names = new ArrayList<>();
        for (PeriodEntry entry : this.entries) {
            names.add(entry.nameTextField.getText());
        }
        return names.toArray(new String[0]);
    }


    public int getNumAcademicPeriods() {
        int count = 0;
        for (PeriodEntry entry : this.entries) {
            if (entry.typeComboBox.getSelectedItem().equals("Academic")) {
                count++;
            }
        }
        return count;
    }


    public Map<String, Map<String, String>> collect(List<String> errors) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");

        Map<String, Map<String, String>> data = new HashMap<>();
        Set<String> seenNames = new HashSet<>();
        int academicPeriodNum = 1;
        for (PeriodEntry entry : this.entries) {
            Date startTime = (Date) entry.startSpinner.getValue();
            Date endTime = (Date) entry.endSpinner.getValue();
            startTime.setYear(0);
            startTime.setMonth(0);
            startTime.setDate(1);
            endTime.setYear(0);
            endTime.setMonth(0);
            endTime.setDate(1);

            String type = (String) entry.typeComboBox.getSelectedItem();
            if (type.equals("Academic")) {
                type = Integer.toString(academicPeriodNum);
                academicPeriodNum++;
            }
            String name = entry.nameTextField.getText();
            String start = dateFormat.format(startTime);
            String end = dateFormat.format(endTime);

            if (!startTime.before(endTime)) {
                errors.add("Periods: Period '" + name + "' Start must be before End");
            }
            if (seenNames.contains(name)) {
                errors.add("Periods: Duplicate name '" + name + "'");
            }
            seenNames.add(name);

            Map<String, String> entryData = new HashMap<>();
            entryData.put(SchoolJson.TYPE, type);
            entryData.put(SchoolJson.NAME, name);
            entryData.put(SchoolJson.START, start);
            entryData.put(SchoolJson.END, end);
            data.put(name, entryData);
        }
        return data;
    }

}
