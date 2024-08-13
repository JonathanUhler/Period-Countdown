package desktop.wizard;


import java.util.List;
import java.util.ArrayList;
import java.util.Date;
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

        private JComboBox<String> typeComboBox;
        private JTextField nameTextField;
        private JSpinner startSpinner;
        private JSpinner endSpinner;


        public PeriodEntry() {
            this.typeComboBox = new JComboBox<>(new String[] {"Academic",
                                                              SchoolJson.NOTHING,
                                                              SchoolJson.SPECIAL});
            this.nameTextField = new JTextField("", 10);
            this.startSpinner =
                new JSpinner(new SpinnerDateModel());
            this.endSpinner =
                new JSpinner(new SpinnerDateModel());

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


        public String getType() {
            return (String) this.typeComboBox.getSelectedItem();
        }


        public String getName() {
            return this.nameTextField.getText();
        }


        public String getStart() {
            DateFormat df = new SimpleDateFormat("HH:mm");
            return df.format((Date) this.startSpinner.getValue());
        }


        public String getEnd() {
            DateFormat df = new SimpleDateFormat("HH:mm");
            return df.format((Date) this.endSpinner.getValue());
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


    public List<String> getPeriodNames() {
        List<String> names = new ArrayList<>();
        for (PeriodEntry entry : this.entries) {
            names.add(entry.getName());
        }
        return names;
    }

}
