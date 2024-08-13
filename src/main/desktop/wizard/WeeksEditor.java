package desktop.wizard;


import java.util.List;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import time.Duration;


public class WeeksEditor extends JPanel {

    private class WeekEntry extends JPanel {

        private JTextField nameTextField;
        private EntryList<JComboBox<String>> days;


        public WeekEntry() {
            this.setLayout(new GridBagLayout());

            this.nameTextField = new JTextField("", 10);
            this.days = new EntryList<>(false) {
                    @Override
                    public JComboBox<String> entryFactory() {
                        return new JComboBox<>(new String[] {"a", "b"});
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


        public String getName() {
            return this.nameTextField.getText();
        }

    }


    private EntryList<WeekEntry> entries;


    public WeeksEditor() {
        this.entries = new EntryList<>() {
                @Override
                public WeekEntry entryFactory() {
                    return new WeekEntry();
                }
            };

        this.add(new JScrollPane(this.entries));
    }


    public List<String> getWeekNames() {
        List<String> names = new ArrayList<>();
        for (WeekEntry entry : this.entries) {
            names.add(entry.getName());
        }
        return names;
    }

}
