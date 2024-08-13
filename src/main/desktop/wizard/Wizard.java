package desktop.wizard;


import javax.swing.JFrame;
import javax.swing.JTabbedPane;


public class Wizard {

    private static int currentTabIndex;


    public void display() {
        JFrame frame = new JFrame("School Data Wizard");
        JTabbedPane dataPane = new JTabbedPane();

        InfoEditor infoEditor = new InfoEditor();
        PeriodsEditor periodsEditor = new PeriodsEditor();
        DaysEditor daysEditor = new DaysEditor(periodsEditor);
        WeeksEditor weeksEditor = new WeeksEditor(daysEditor);
        ExceptionsEditor exceptionsEditor = new ExceptionsEditor(weeksEditor);

        dataPane.add("Info", infoEditor);
        dataPane.add("Periods", periodsEditor);
        dataPane.add("Days", daysEditor);
        dataPane.add("Weeks", weeksEditor);
        dataPane.add("Exceptions", exceptionsEditor);
        /*
        dataPane.add("Export", );
        */

        Wizard.currentTabIndex = dataPane.getSelectedIndex();
        dataPane.addChangeListener(e -> {
                int newIndex = dataPane.getSelectedIndex();
                if (newIndex > currentTabIndex) {
                    daysEditor.update();
                    weeksEditor.update();
                    exceptionsEditor.update();
                }
                Wizard.currentTabIndex = newIndex;
            });

        frame.setContentPane(dataPane);
        frame.pack();
        frame.setVisible(true);
    }

}
