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


public class DaysEditor extends JPanel {

    private class PeriodChoiceEntry extends JPanel {

        private JComboBox<String> periodNameComboBox;


        public PeriodChoiceEntry() {
            this.periodNameComboBox = new JComboBox<>();
        }
        
    }


    private class DayEntry extends JPanel {

        private JTextField nameTextField;
        private EntryList<PeriodChoiceEntry> periods;


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

            this.periods.addEntry(new PeriodChoiceEntry());
        }


        public String getName() {
            return this.nameTextField.getText();
        }

    }


    private EntryList<DayEntry> entries;


    public DaysEditor() {
        this.entries = new EntryList<>() {
                @Override
                public DayEntry entryFactory() {
                    return new DayEntry();
                }
            };

        this.add(new JScrollPane(this.entries));
    }


    public List<String> getDayNames() {
        List<String> names = new ArrayList<>();
        for (DayEntry entry : this.entries) {
            names.add(entry.getName());
        }
        return names;
    }

}
