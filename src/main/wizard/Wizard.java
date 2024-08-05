package wizard;


import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import wizard.editor.*;


/**
 * Main class for the Period-Countdown school data JSON generator. Responsible for creating and
 * displaying a frame with tabs for data entry.
 *
 * @author Jonathan Uhler
 */
public class Wizard {
    
    /**
     * Displays a graphical message.
     *
     * @param title    the title of the message.
     * @param message  the message body to display.
     */
    public static void displayMessage(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title,
                                      JOptionPane.PLAIN_MESSAGE);
    }
    
    
    /**
     * Main entry point to the data generator.
     *
     * @param args  command line arguments.
     */
    public static void main(String[] args) {
        WizardManager.setup(); // This must be called to set up the static manager class
        InfoEditor infoEditor = WizardManager.getInfoEditor();
        PeriodEditor periodEditor = WizardManager.getPeriodEditor();
        DayEditor dayEditor = WizardManager.getDayEditor();
        WeekEditor weekEditor = WizardManager.getWeekEditor();
        WeekExceptionEditor weekExceptionEditor = WizardManager.getWeekExceptionEditor();
        JSONViewer jsonViewer = WizardManager.getJsonViewer();
	
        JFrame frame = new JFrame("School Data Wizard");
        JTabbedPane wizard = new JTabbedPane();
        
        wizard.addTab("Info", infoEditor);
        wizard.addTab("Periods", periodEditor);
        wizard.addTab("Days", dayEditor);
        wizard.addTab("Weeks", weekEditor);
        wizard.addTab("Exceptions", weekExceptionEditor);
        wizard.addTab("Final JSON", jsonViewer);
        wizard.addChangeListener(e -> WizardManager.notifyEditorEntries(wizard.getSelectedIndex()));
        
        frame.setContentPane(wizard);
        frame.pack();
        frame.setVisible(true);
    }
    
}
