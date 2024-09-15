package desktop.wizard;


import java.util.Set;
import java.util.HashSet;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import time.UTCTime;
import school.SchoolJson;


public class ExceptionsEditor extends JPanel {

    private class ExceptionEntry extends JPanel {

        public JSpinner dateSpinner;
        public JComboBox<String> weekComboBox;


        public ExceptionEntry() {
            this.dateSpinner = new JSpinner(new SpinnerDateModel());
            this.weekComboBox = new JComboBox<>(ExceptionsEditor.this.dataSource.getWeekNames());

            this.dateSpinner.setEditor(new JSpinner.DateEditor(this.dateSpinner, "yyyy-MM-dd"));

            this.add(new JLabel("Date"));
            this.add(this.dateSpinner);
            this.add(new JLabel("Week Type"));
            this.add(this.weekComboBox);
        }

    }


    private WeeksEditor dataSource;
    private EntryList<ExceptionEntry> entries;


    public ExceptionsEditor(WeeksEditor dataSource) {
        this.dataSource = dataSource;
        this.entries = new EntryList<>() {
                @Override
                public ExceptionEntry entryFactory() {
                    return new ExceptionEntry();
                }
            };

        this.add(new JScrollPane(this.entries));
    }


    public void update() {
        String[] names = this.dataSource.getWeekNames();
        for (ExceptionEntry entry : this.entries) {
            String previousItem = (String) entry.weekComboBox.getSelectedItem();
            entry.weekComboBox.setModel(new DefaultComboBoxModel<>(names));
            entry.weekComboBox.setSelectedItem(previousItem);
        }
    }


    public List<Map<String, String>> collect(List<String> errors) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<Map<String, String>> data = new ArrayList<>();
        Set<String> seenWeekTags = new HashSet<>();
        for (ExceptionEntry entry : this.entries) {
            String week = (String) entry.weekComboBox.getSelectedItem();
            String weekTag = dateFormat.format((Date) entry.dateSpinner.getValue());
            weekTag = UTCTime.of(weekTag, "Z").getWeekTag();

            if (seenWeekTags.contains(weekTag)) {
                errors.add("Exceptions: Multiple week exceptions exist for the week of " + weekTag);
            }
            seenWeekTags.add(weekTag);

            Map<String, String> entryData = new HashMap<>();
            entryData.put(SchoolJson.TYPE, week);
            entryData.put(SchoolJson.WEEK_TAG, weekTag);

            data.add(entryData);
        }
        return data;
    }

}
