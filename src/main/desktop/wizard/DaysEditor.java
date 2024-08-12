package desktop.wizard;


import java.util.List;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;


public class DaysEditor extends JPanel {

    private class PeriodChoiceEntry extends JPanel {

        public PeriodChoiceEntry() {
            this.add(new JLabel("sdlkjfsjdlkfjklsdfjkl"));
        }
        
    }


    private class DayEntry extends JPanel {

        private JTextField nameTextField;
        private EditorList<PeriodChoiceEntry> periods;

        public DayEntry() {
            this.setLayout(new GridBagLayout());

            this.nameTextField = new JTextField("", 10);
            this.periods = new EditorList<>();

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


    private JButton newEntryButton;
    private EditorList<DayEntry> entries;


    public DaysEditor() {
        this.setLayout(new GridBagLayout());

        this.newEntryButton = new JButton("Add Day Type");
        this.entries = new EditorList<>();

        this.newEntryButton.addActionListener(e -> this.entries.addEntry(new DayEntry()));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        this.add(new JScrollPane(this.entries), gbc);
        gbc.gridy++;
        this.add(this.newEntryButton, gbc);
    }


    public List<String> getDayNames() {
        List<String> names = new ArrayList<>();
        for (DayEntry entry : this.entries) {
            names.add(entry.getName());
        }
        return names;
    }

}
