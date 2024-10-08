package desktop.wizard;


import javax.swing.JFrame;
import javax.swing.JTabbedPane;


/**
 * Main class for the school data generation wizard.
 *
 * @author Jonathan Uhler
 */
public class Wizard {

    private static int currentTabIndex;


    /**
     * Creates and displays a new JFrame with the graphical components of the wizard.
     *
     * This is the only entry point to the wizard, and manages all data/graphical resources.
     */
    public void display() {
        JFrame frame = new JFrame("School Data Wizard");
        JTabbedPane dataPane = new JTabbedPane();

        InfoEditor infoEditor = new InfoEditor();
        PeriodsEditor periodsEditor = new PeriodsEditor();
        DaysEditor daysEditor = new DaysEditor(periodsEditor);
        WeeksEditor weeksEditor = new WeeksEditor(daysEditor);
        ExceptionsEditor exceptionsEditor = new ExceptionsEditor(weeksEditor);
        Builder builder = new Builder(infoEditor,
                                      periodsEditor,
                                      daysEditor,
                                      weeksEditor,
                                      exceptionsEditor);

        dataPane.add("Info", infoEditor);
        dataPane.add("Periods", periodsEditor);
        dataPane.add("Days", daysEditor);
        dataPane.add("Weeks", weeksEditor);
        dataPane.add("Exceptions", exceptionsEditor);
        dataPane.add("Export", builder);

        Wizard.currentTabIndex = dataPane.getSelectedIndex();
        dataPane.addChangeListener(e -> {
                int newIndex = dataPane.getSelectedIndex();
                daysEditor.update();
                weeksEditor.update();
                exceptionsEditor.update();
                if (newIndex == dataPane.getTabCount() - 1) {
                    builder.update();
                }
                Wizard.currentTabIndex = newIndex;
            });

        frame.setContentPane(dataPane);
        frame.pack();
        frame.setVisible(true);
    }

}
