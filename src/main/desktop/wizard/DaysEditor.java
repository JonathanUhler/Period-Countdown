package desktop.wizard;


import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import school.SchoolJson;


public class DaysEditor extends JPanel {

    private class PeriodChoiceEntry extends JPanel {

        public JComboBox<String> periodNameComboBox;


        public PeriodChoiceEntry() {
            this.periodNameComboBox = new JComboBox<>(DaysEditor.this.dataSource.getPeriodNames());

            this.add(this.periodNameComboBox);
        }
        
    }


    private class DayEntry extends JPanel {

        public JTextField nameTextField;
        public EntryList<PeriodChoiceEntry> periods;


        public DayEntry() {
            this.setLayout(new GridBagLayout());

            this.nameTextField = new JTextField("", 10);
            this.periods = new EntryList<>() {
                @Override
                public PeriodChoiceEntry entryFactory() {
                    return new PeriodChoiceEntry();
                }
            };

            GridBagConstraints gbc = new GridBagConstraints();

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            this.add(new JLabel("Name"), gbc);

            gbc.gridx++;
            this.add(this.nameTextField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            this.add(new JLabel("Periods"), gbc);

            gbc.gridy++;
            this.add(this.periods, gbc);
        }

    }


    private PeriodsEditor dataSource;
    private EntryList<DayEntry> entries;


    public DaysEditor(PeriodsEditor dataSource) {
        this.dataSource = dataSource;
        this.entries = new EntryList<>() {
                @Override
                public DayEntry entryFactory() {
                    return new DayEntry();
                }
            };

        this.add(new JScrollPane(this.entries));
    }


    public String[] getDayNames() {
        List<String> names = new ArrayList<>();
        for (DayEntry entry : this.entries) {
            names.add(entry.nameTextField.getText());
        }
        return names.toArray(new String[0]);
    }


    public void update() {
        String[] names = this.dataSource.getPeriodNames();
        for (DayEntry entry : this.entries) {
            for (PeriodChoiceEntry period : entry.periods) {
                String previousItem = (String) period.periodNameComboBox.getSelectedItem();
                period.periodNameComboBox.setModel(new DefaultComboBoxModel<>(names));
                period.periodNameComboBox.setSelectedItem(previousItem);
            }
        }
    }


    private Map<String, String> createBufferPeriod(String lastEndStr, String nextStartStr) {
        if (lastEndStr.equals(nextStartStr)) {
            return null;
        }

        boolean isFirst = lastEndStr.equals("00:00");
        boolean isLast = nextStartStr == null;

        String name;
        if (isFirst && isLast) {
            name = "Free";
        }
        else if (isFirst) {
            name = "Before Classes";
        }
        else if (isLast) {
            name = "After Classes";
        }
        else {
            name = "Between Classes";
        }

        String start = lastEndStr;
        String end = isLast ? "23:59" : nextStartStr;

        Map<String, String> buffer = new HashMap<>();
        buffer.put(SchoolJson.TYPE, SchoolJson.NOTHING);
        buffer.put(SchoolJson.NAME, name);
        buffer.put(SchoolJson.START, start);
        buffer.put(SchoolJson.END, end);
        return buffer;
    }


    private void addBufferPeriods(List<Map<String, String>> entryData) {
        entryData.sort((period1, period2) -> {
                String period1Start = period1.get(SchoolJson.START);
                String period2Start = period2.get(SchoolJson.START);
                return period1Start.compareTo(period2Start);
            });

        String lastEnd = "00:00";
        for (int i = 0; i < entryData.size(); i++) {
            Map<String, String> period = entryData.get(i);
            String periodStart = period.get(SchoolJson.START);
            String periodEnd = period.get(SchoolJson.END);

            Map<String, String> buffer = this.createBufferPeriod(lastEnd, periodStart);
            if (buffer != null) {
                entryData.add(i, buffer);
                i++;
            }

            lastEnd = periodEnd;
        }

        if (!lastEnd.equals("23:59")) {
            entryData.add(this.createBufferPeriod(lastEnd, null));
        }
    }


    public Map<String, List<Map<String, String>>> collect(Map<String, Map<String, String>> periods,
                                                          List<String> errors)
    {
        Map<String, List<Map<String, String>>> data = new HashMap<>();
        Set<String> seenNames = new HashSet<>();
        for (DayEntry entry : this.entries) {
            String name = entry.nameTextField.getText();

            if (seenNames.contains(name)) {
                errors.add("Days: Duplicate name '" + name + "'");
            }
            seenNames.add(name);

            List<Map<String, String>> entryData = new ArrayList<>();
            for (PeriodChoiceEntry periodEntry : entry.periods) {
                String periodName = (String) periodEntry.periodNameComboBox.getSelectedItem();
                entryData.add(periods.get(periodName));
            }
            this.addBufferPeriods(entryData);

            data.put(name, entryData);
        }
        return data;
    }

}
