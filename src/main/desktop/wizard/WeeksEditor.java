package desktop.wizard;


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
import time.Duration;


public class WeeksEditor extends JPanel {

    private class WeekEntry extends JPanel {

        public JTextField nameTextField;
        public EntryList<JComboBox<String>> days;


        public WeekEntry() {
            this.setLayout(new GridBagLayout());

            this.nameTextField = new JTextField("", 10);
            this.days = new EntryList<>(false) {
                    @Override
                    public JComboBox<String> entryFactory() {
                        return new JComboBox<>(WeeksEditor.this.dataSource.getDayNames());
                    }
                };

            for (int i = 0; i < Duration.DAYS_PER_WEEK; i++) {
                this.days.addEntry(this.days.entryFactory());
            }

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
            this.add(new JLabel("Days"), gbc);

            gbc.gridx++;
            this.add(this.days, gbc);
        }

    }


    private DaysEditor dataSource;
    private EntryList<WeekEntry> entries;


    public WeeksEditor(DaysEditor dataSource) {
        this.dataSource = dataSource;
        this.entries = new EntryList<>() {
                @Override
                public WeekEntry entryFactory() {
                    return new WeekEntry();
                }
            };

        this.add(new JScrollPane(this.entries));
    }


    public String[] getWeekNames() {
        List<String> names = new ArrayList<>();
        for (WeekEntry entry : this.entries) {
            names.add(entry.nameTextField.getText());
        }
        return names.toArray(new String[0]);
    }


    public void update() {
        String[] names = this.dataSource.getDayNames();
        for (WeekEntry entry : this.entries) {
            for (JComboBox<String> day : entry.days) {
                String previousItem = (String) day.getSelectedItem();
                day.setModel(new DefaultComboBoxModel<>(names));
                day.setSelectedItem(previousItem);
            }
        }
    }


    public Map<String, List<String>> collect() {
        Map<String, List<String>> data = new HashMap<>();
        for (WeekEntry entry : this.entries) {
            String name = entry.nameTextField.getText();
            List<String> entryData = new ArrayList<>();
            for (JComboBox<String> day : entry.days) {
                entryData.add((String) day.getSelectedItem());
            }

            data.put(name, entryData);
        }
        return data;
    }

}
