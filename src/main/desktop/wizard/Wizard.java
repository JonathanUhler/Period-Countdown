package desktop.wizard;


import javax.swing.JFrame;
import javax.swing.JTabbedPane;


public class Wizard {

    public void display() {
        JFrame frame = new JFrame("School Data Wizard");
        JTabbedPane dataPane = new JTabbedPane();

        InfoEditor infoEditor = new InfoEditor();
        PeriodsEditor periodsEditor = new PeriodsEditor();
        DaysEditor daysEditor = new DaysEditor();
        WeeksEditor weeksEditor = new WeeksEditor();

        dataPane.add("Info", infoEditor);
        dataPane.add("Periods", periodsEditor);
        dataPane.add("Days", daysEditor);
        dataPane.add("Weeks", weeksEditor);
        /*
        dataPane.add("Exceptions", );
        dataPane.add("Export", );
        */

        frame.setContentPane(dataPane);
        frame.pack();
        frame.setVisible(true);
    }

}
