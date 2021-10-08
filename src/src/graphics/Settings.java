// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Settings.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/13/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Settings.java
// Class Diagram
/*

+-----------------------------------------------------------------------------------------------------+
|                                               Settings                                              |
+-----------------------------------------------------------------------------------------------------+
| -schoolYear: SchoolYear                                                                             |
+-----------------------------------------------------------------------------------------------------+
| +Settings()                                                                                         |
+-----------------------------------------------------------------------------------------------------+
| -periodNamesAction(): void                                                                          |
| -getPeriodNames(): JMenuItem                                                                        |
| -periodInfoAction(): void                                                                           |
| -getPeriodInfo(): JMenuItem                                                                         |
| -themeAction(): void                                                                                |
| -getTheme(): JMenuItem                                                                              |
| -getNextUp(): JMenu                                                                                 |
| -schoolFileAction(): void                                                                           |
| -getSchoolFile(): JMenu                                                                             |
| +getSettingsMenu(): JMenu                                                                           |
+-----------------------------------------------------------------------------------------------------+

*/
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package graphics;


import javax.swing.*;
import java.util.ArrayList;
import java.util.Map;
import school.*;
import calendar.*;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class Settings
//
// Class to handle the user changing their settings
//
public class Settings {

    private final SchoolYear schoolYear;


    // ----------------------------------------------------------------------------------------------------
    // public Settings
    //
    // Constructor for Settings class
    //
    // Arguments--
    //
    // None
    //
    public Settings() throws Exception {
        this.schoolYear = new SchoolYear(SchoolDisplay.getSchoolData(), SchoolDisplay.userData); // Initialize the class's SchoolYear object
    }
    // end: public Settings


    // ====================================================================================================
    // private void periodNamesAction
    //
    // Popup for settings the period names
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // None
    //
    private void periodNamesAction() throws Exception {
        ArrayList<Object> message = new ArrayList<>(); // Initialize an ArrayList to hold the message and textboxes for setting the period names

        // Loop through each of the periods
        for (int i = SchoolCalendar.getFirstPeriod(); i <= SchoolCalendar.getLastPeriod(); i++) {
            Map<String, Object> period = schoolYear.getUserDataByPeriod(i);// Get the object from the user data for the period information
            JTextField periodTextField = new JTextField((String) period.get(SchoolCalendar.getNameTerm)); // Create a textbox with the current period name as the current value
            message.add(this.schoolYear.getPeriodNameByNumber(i)); // Add a text prompt to the message
            message.add(periodTextField); // Add the textbox to the message
        }

        // Display the prompt to edit the data
        int confirmEditPeriodNames = JOptionPane.showConfirmDialog(
                null,
                message.toArray(new Object[0]),
                "Edit Period Names",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null);

        // Check if the user confirmed their changes
        if (confirmEditPeriodNames == JOptionPane.OK_OPTION) {
            // Loop through each of the periods
            for (int i = SchoolCalendar.getFirstPeriod(); i <= SchoolCalendar.getLastPeriod(); i++) {
                int periodNameTextIndex = 2 * i - 1; // Calculate the index of the textbox (because in the ArrayList the order is 0=text, 1=textbox, 2=text...
                schoolYear.setUserPeriodNameByPeriod(i, ((JTextField) message.get(periodNameTextIndex)).getText()); // Set the new data
            }
        }
    }
    // end: private void periodNamesAction


    // ====================================================================================================
    // private JMenuItem getPeriodNames
    //
    // Get the menu item object for setting the period names
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // periodNamesMenu: the menu item object for setting the period names
    //
    private JMenuItem getPeriodNames() {
        JMenuItem periodNamesMenu = new JMenuItem("Period Names"); // Create the new object and add a name to it

        periodNamesMenu.addActionListener(e -> { // Add an action listener that calls the periodNamesAction on clicking the button
            try { this.periodNamesAction(); } catch (Exception ex) { ex.printStackTrace(); }
        });

        return periodNamesMenu; // Return the object
    }
    // end: private JMenuItem getPeriodNames


    // ====================================================================================================
    private void periodInfoAction() throws Exception {
        ArrayList<Object> message = new ArrayList<>();

        for (int i = SchoolCalendar.getFirstPeriod(); i <= SchoolCalendar.getLastPeriod(); i++) {
            Map<String, Object> period = schoolYear.getUserDataByPeriod(i);

            JTextField teacherTextField = new JTextField((String) period.get(SchoolCalendar.getTeacherNameTerm));
            message.add(this.schoolYear.getPeriodNameByNumber(i) + " | Teacher");
            message.add(teacherTextField);

            JTextField roomTextField = new JTextField(String.valueOf(period.get(SchoolCalendar.getRoomNumberTerm)));
            message.add(this.schoolYear.getPeriodNameByNumber(i) + " | Room Number");
            message.add(roomTextField);
        }

        int confirmEditPeriodNames = JOptionPane.showConfirmDialog(
                null,
                message.toArray(new Object[0]),
                "Edit Period Information",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null);

        if (confirmEditPeriodNames == JOptionPane.OK_OPTION) {
            for (int i = SchoolCalendar.getFirstPeriod(); i <= SchoolCalendar.getLastPeriod(); i++) {
                int teacherNameTextIndex = 4 * i - 3; // ALGEBRA!
                int roomNumTextIndex = 4 * i - 1; // ALGEBRA AGAIN!
                schoolYear.setUserPeriodInfoByPeriod(i, ((JTextField) message.get(teacherNameTextIndex)).getText(), ((JTextField) message.get(roomNumTextIndex)).getText());
            }
        }
    }


    // ====================================================================================================
    private JMenuItem getPeriodInfo() {
        JMenuItem periodInfoMenu = new JMenuItem("Period Information");
        periodInfoMenu.addActionListener(e -> {
            try { this.periodInfoAction(); } catch (Exception ex) { ex.printStackTrace(); }
        });
        return periodInfoMenu;
    }


    // ====================================================================================================
    private void themeAction() throws Exception {
        ArrayList<Object> message = new ArrayList<>();
        message.add("Theme (3 hex bytes, ex: ffffff)");
        message.add(new JTextField(schoolYear.getUserTheme()));

        int confirmEditTheme = JOptionPane.showConfirmDialog(
                null,
                message.toArray(new Object[0]),
                "Edit Theme",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null);

        if (confirmEditTheme == JOptionPane.OK_OPTION) {
            String themeColor = ((JTextField) message.get(1)).getText();
            schoolYear.setUserTheme(themeColor);
        }
    }


    // ====================================================================================================
    private JMenuItem getTheme() {
        JMenuItem themeMenu = new JMenuItem("Theme");
        themeMenu.addActionListener(e -> {
            try { this.themeAction(); } catch (Exception ex) { ex.printStackTrace(); }
        });
        return themeMenu;
    }


    // ====================================================================================================
    private void nextUpAction(int verbosity) throws Exception {
        this.schoolYear.setUserNextUp(verbosity);
    }


    // ====================================================================================================
    private JMenu getNextUp() {
        JMenu nextUpMenu = new JMenu("Next Up...");

        JMenuItem none = new JMenuItem("None");
        JMenuItem oneName = new JMenuItem("Next class - Name only");
        JMenuItem oneAll = new JMenuItem("Next class - All information");
        JMenuItem allName = new JMenuItem("All classes - Name only");
        JMenuItem allAll = new JMenuItem("All classes - All information");

        none.addActionListener(e -> {
            try { this.nextUpAction(NextUp.NONE); } catch (Exception ex) { ex.printStackTrace(); }
        });
        oneName.addActionListener(e -> {
            try { this.nextUpAction(NextUp.ONE_NAME); } catch (Exception ex) { ex.printStackTrace(); }
        });
        oneAll.addActionListener(e -> {
            try { this.nextUpAction(NextUp.ONE_ALL_INFO); } catch (Exception ex) { ex.printStackTrace(); }
        });
        allName.addActionListener(e -> {
            try { this.nextUpAction(NextUp.ALL_NAME); } catch (Exception ex) { ex.printStackTrace(); }
        });
        allAll.addActionListener(e -> {
            try { this.nextUpAction(NextUp.ALL_ALL_INFO); } catch (Exception ex) { ex.printStackTrace(); }
        });

        nextUpMenu.add(none);
        nextUpMenu.add(oneName);
        nextUpMenu.add(oneAll);
        nextUpMenu.add(allName);
        nextUpMenu.add(allAll);

        return nextUpMenu;
    }


    // ====================================================================================================
    private void schoolFileAction(String file) throws Exception {
        this.schoolYear.setUserSchoolFile(file);
    }


    // ====================================================================================================
    private JMenu getSchoolFile() {
        JMenu schoolFileMenu = new JMenu("School...");

        JMenuItem MVHS = new JMenuItem("MVHS");
        JMenuItem custom = new JMenuItem("Custom...");

        MVHS.addActionListener(e -> {
            try { this.schoolFileAction("MVHS_School.json"); } catch (Exception ex) { ex.printStackTrace(); }
        });
        custom.addActionListener(e -> {
            ArrayList<Object> message = new ArrayList<>();
            message.add("School Data Filename (in format *.json)");
            message.add(new JTextField(schoolYear.getSchoolFileName()));

            int confirmEditFile = JOptionPane.showConfirmDialog(
                    null,
                    message.toArray(new Object[0]),
                    "Edit School Data File",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null);

            if (confirmEditFile == JOptionPane.OK_OPTION) {
                String newFile = ((JTextField) message.get(1)).getText();

                try {schoolYear.setUserSchoolFile(newFile);}
                catch (Exception ex) {ex.printStackTrace();}
            }
        });


        schoolFileMenu.add(MVHS);
        schoolFileMenu.add(custom);

        return schoolFileMenu;
    }


    // ====================================================================================================
    public JMenu getSettingsMenu() {
        JMenu settingsMenu = new JMenu("Settings");

        settingsMenu.add(this.getPeriodNames());
        settingsMenu.add(this.getPeriodInfo());
        settingsMenu.add(this.getTheme());
        settingsMenu.add(this.getNextUp());
        settingsMenu.add(this.getSchoolFile());

        return settingsMenu;
    }

}
// end: public class Settings