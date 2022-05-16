// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Settings.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/13/21
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
    public Settings() throws Exception {
        this.schoolYear = new SchoolYear(SchoolDisplay.getSchoolData(), SchoolDisplay.userData);
    }
    // end: public Settings


    // ====================================================================================================
    // private void periodNamesAction
    //
    // Popup for settings the period names
    //
    private void periodNamesAction() throws Exception {
        ArrayList<Object> message = new ArrayList<>();

        // Loop through each of the periods in the day and get the user's information for that period. Add
        // the period name (based on the period number in the day) along with a text box to change the user
        // defined class name
        // In this context the "period name" displayed next to the text box is final and cannot be changed
        // (it is defined in the school json file and is something generic like "period 1"). The class name
        // which can be changed by the user is what is entered into the text box (something like "chemistry")
        for (int i = SchoolCalendar.getFirstPeriod(); i <= SchoolCalendar.getLastPeriod(); i++) {
            Map<String, Object> period = schoolYear.getUserDataByPeriod(i);
            JTextField periodTextField = new JTextField((String) period.get(SchoolCalendar.getNameTerm));
            message.add(this.schoolYear.getPeriodNameByNumber(i));
            message.add(periodTextField);
        }

        int confirmEditPeriodNames = JOptionPane.showConfirmDialog(
                null,
                message.toArray(new Object[0]),
                "Edit Period Names",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null);

        // If the user accepts the prompt to change the period names, then we must loop through each of the
        // text boxes and update the information. Because everything is in a 1 dimensional array (both the
        // text boxes that we want to access and the labels displayed in the message) the array should be
        // indexed with the equation 2i-1 where i is the text box number
        if (confirmEditPeriodNames == JOptionPane.OK_OPTION) {
            for (int i = SchoolCalendar.getFirstPeriod(); i <= SchoolCalendar.getLastPeriod(); i++) {
                int periodNameTextIndex = 2 * i - 1;
                schoolYear.setUserPeriodNameByPeriod(i,
                        ((JTextField) message.get(periodNameTextIndex)).getText());
            }
        }
    }
    // end: private void periodNamesAction


    // ====================================================================================================
    // private JMenuItem getPeriodNames
    //
    // Initialize and return the menu item object for setting the period names
    //
    // Returns--
    //
    // periodNamesMenu: the menu item object for setting the period names
    //
    private JMenuItem getPeriodNames() {
        JMenuItem periodNamesMenu = new JMenuItem("Period Names");

        periodNamesMenu.addActionListener(e -> {
            try { this.periodNamesAction(); }
            catch (Exception ex) { ex.printStackTrace(); }
        });

        return periodNamesMenu;
    }
    // end: private JMenuItem getPeriodNames


    // ====================================================================================================
    // private void periodInfoAction
    //
    // Similarly to periodNamesAction, creates a popup to edit the information about periods
    //
    private void periodInfoAction() throws Exception {
        ArrayList<Object> message = new ArrayList<>();

        // Loop through each of the periods in the day and add the prompts and text boxes to the message
        // that will be displayed in the popup to the user. The order for this information is:
        //  Teacher for period text
        //  Teacher name entry field
        //  Room number for period text
        //  Room number entry field
        for (int i = SchoolCalendar.getFirstPeriod(); i <= SchoolCalendar.getLastPeriod(); i++) {
            Map<String, Object> period = schoolYear.getUserDataByPeriod(i);

            String currentTeacherName = (String) period.get(SchoolCalendar.getTeacherNameTerm);
            JTextField teacherTextField = new JTextField(currentTeacherName);
            message.add(this.schoolYear.getPeriodNameByNumber(i) + " | Teacher");
            message.add(teacherTextField);

            String currentRoom = String.valueOf(period.get(SchoolCalendar.getRoomNumberTerm));
            JTextField roomTextField = new JTextField(currentRoom);
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

        // When the user accepts the prompt, like with the method above to update the period names, we must
        // index the 1d array "message" at specific points to get the JTextFields out.
        //  The equation to index a teacher name field is 4i-3 where i is the period number
        //  The equation to index a room number field is 4i-1 where i is the period number
        if (confirmEditPeriodNames == JOptionPane.OK_OPTION) {
            for (int i = SchoolCalendar.getFirstPeriod(); i <= SchoolCalendar.getLastPeriod(); i++) {
                int teacherNameTextIndex = 4 * i - 3;
                int roomNumTextIndex = 4 * i - 1;
                schoolYear.setUserPeriodInfoByPeriod(i,
                        ((JTextField) message.get(teacherNameTextIndex)).getText(),
                        ((JTextField) message.get(roomNumTextIndex)).getText());
            }
        }
    }
    // end: private void periodInfoAction


    // ====================================================================================================
    // private JMenuItem getPeriodInfo
    //
    // Initialize and returns the menu item to update the period information
    //
    // Returns--
    //
    // A JMenuItem object that, when clicked, runs the periodInfoAction method to update the period info
    //
    private JMenuItem getPeriodInfo() {
        JMenuItem periodInfoMenu = new JMenuItem("Period Information");
        periodInfoMenu.addActionListener(e -> {
            try { this.periodInfoAction(); }
            catch (Exception ex) { ex.printStackTrace(); }
        });
        return periodInfoMenu;
    }
    // end: private JMenuItem getPeriodInfo


    // ====================================================================================================
    // private void themeAction
    //
    // Updates the theme color of the application
    //
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
    // end: private void themeAction


    // ====================================================================================================
    // private JMenuItem getTheme
    //
    // Initialize and returns a menu item to update the color theme of the app
    //
    // Returns--
    //
    // A JMenuItem object that, when clicked, allows the user to enter a 3 byte hex string to change the
    // background color of the application window
    //
    private JMenuItem getTheme() {
        JMenuItem themeMenu = new JMenuItem("Theme");
        themeMenu.addActionListener(e -> {
            try { this.themeAction(); }
            catch (Exception ex) { ex.printStackTrace(); }
        });
        return themeMenu;
    }
    // end: private JMenuItem getTheme


    // ====================================================================================================
    // private void nextUpAction
    //
    // Updates the verbosity of the "next up" feature that displays a list of the next classes in the day.
    // For more information about this feature, see graphics.NextUp.java
    //
    // Arguments--
    //
    // verbosity:   the verbosity level to set the feature at
    //
    private void nextUpAction(int verbosity) throws Exception {
        this.schoolYear.setUserNextUp(verbosity);
    }
    // end: private void nextUpAction


    // ====================================================================================================
    // private JMenu getNextUp
    //
    // Initialize and returns a separate menu to manage the next up feature
    //
    // Returns--
    //
    // A JMenu object that contains the 5 (at the time of writing) possible verbosity levels to display
    // the next up feature with. The user can click on the JMenu and then select from the menu items
    //
    private JMenu getNextUp() {
        JMenu nextUpMenu = new JMenu("Next Up...");

        JMenuItem none = new JMenuItem("None");
        JMenuItem oneName = new JMenuItem("Next class - Name only");
        JMenuItem oneAll = new JMenuItem("Next class - All information");
        JMenuItem allName = new JMenuItem("All classes - Name only");
        JMenuItem allAll = new JMenuItem("All classes - All information");

        none.addActionListener(e -> {
            try { this.nextUpAction(NextUp.NONE); }
            catch (Exception ex) { ex.printStackTrace(); }
        });
        oneName.addActionListener(e -> {
            try { this.nextUpAction(NextUp.ONE_NAME); }
            catch (Exception ex) { ex.printStackTrace(); }
        });
        oneAll.addActionListener(e -> {
            try { this.nextUpAction(NextUp.ONE_ALL_INFO); }
            catch (Exception ex) { ex.printStackTrace(); }
        });
        allName.addActionListener(e -> {
            try { this.nextUpAction(NextUp.ALL_NAME); }
            catch (Exception ex) { ex.printStackTrace(); }
        });
        allAll.addActionListener(e -> {
            try { this.nextUpAction(NextUp.ALL_ALL_INFO); }
            catch (Exception ex) { ex.printStackTrace(); }
        });

        nextUpMenu.add(none);
        nextUpMenu.add(oneName);
        nextUpMenu.add(oneAll);
        nextUpMenu.add(allName);
        nextUpMenu.add(allAll);

        return nextUpMenu;
    }
    // end: private JMenu getNextUp


    // ====================================================================================================
    // private void schoolFileAction
    //
    // Updates the file name of the school data json file
    //
    // Arguments--
    //
    // file:    the name (ONLY!!, in the format "xxxxx.json") of the new json file that contains the school
    //          data information
    //
    private void schoolFileAction(String file) throws Exception {
        this.schoolYear.setUserSchoolFile(file);
    }
    // end: private void schoolFileAction


    // ====================================================================================================
    // private JMenu getSchoolFile
    //
    // Initialize and returns a separate menu to manage the school json file. Any pre-made files (such as
    // MVHS) will always have their own option here. However, this is also a feature to use a user-made
    // file which can be specified through this menu
    //
    // Returns--
    //
    // A JMenu object that lets the user choose between a pre-made json file or enter a custom file
    // by its name
    //
    private JMenu getSchoolFile() {
        JMenu schoolFileMenu = new JMenu("School...");

        JMenuItem MVHS = new JMenuItem("MVHS");
        JMenuItem custom = new JMenuItem("Custom...");

        MVHS.addActionListener(e -> {
            try { this.schoolFileAction("MVHS_School.json"); }
            catch (Exception ex) { ex.printStackTrace(); }
        });

        custom.addActionListener(e -> {
            // When allowing the user to choose a custom file name, make sure to prompt them to only
            // enter a file name (not full path). In order to prevent locking the app through crashes,
            // there is a safety feature when defining a file so that any non-existent file is
            // created from the default MVHS data
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
    // end: private JMenu getSchoolFile


    // ====================================================================================================
    // public JMenu getSettingsMenu
    //
    // Initialize and returns the full settings menu with all the settings features
    //
    // Returns--
    //
    // A JMenu object with items that link back to the rest of the methods in this class
    //
    public JMenu getSettingsMenu() {
        JMenu settingsMenu = new JMenu("Settings");

        settingsMenu.add(this.getPeriodNames());
        settingsMenu.add(this.getPeriodInfo());
        settingsMenu.add(this.getTheme());
        settingsMenu.add(this.getNextUp());
        settingsMenu.add(this.getSchoolFile());

        return settingsMenu;
    }
    // end: public JMenu getSettingsMenu

}
// end: public class Settings