package desktop;


import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;


/**
 * Main class for the Period-Countdown desktop application. Responsible for creating and displaying
 * a {@code Screen} with a {@code Menu} bar.
 *
 * @author Jonathan Uhler
 *
 * @see Screen
 * @see Menu
 */
public class PCDesktopApp {
    
    /**
     * Displays a graphical message.
     *
     * @param title    the title of the message.
     * @param message  the message body to display.
     */
    public static void displayMessage(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.PLAIN_MESSAGE);
    }


    /**
     * Displays a graphical message with arbitrary components.
     *
     * @param title       the title of the message.
     * @param components  a list of components to display.
     *
     * @return a {@code JOptionPane.*_OPTION} status code.
     */
    public static int displayDialog(String title, JComponent[] components) {
        JPanel panel = new JPanel();
        panel.setFocusable(true);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        for (int i = 0; i < components.length; i++) {
            panel.add(components[i], gbc);
        }
        
        return JOptionPane.showConfirmDialog(null,
                                             panel,
                                             title,
                                             JOptionPane.OK_CANCEL_OPTION,
                                             JOptionPane.PLAIN_MESSAGE,
                                             null);
    }
    
    
    /**
     * Main entry point to the desktop application.
     *
     * @param args  command line arguments.
     */
    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame("Period Countdown");
            Screen screen = new Screen();
            Menu menu = new Menu(screen);
            frame.add(screen);
            frame.setJMenuBar(menu);
            
            frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
            
            // This starts a while(true) loop, so nothing should be after it in this method
            screen.start();
        }
        catch (Exception e) {
            // Catch any exceptions that aren't handled in the rest of the code
            e.printStackTrace();
            PCDesktopApp.displayMessage("Error", "Unchecked exception thrown: " + e);
        }
    }
    
}
