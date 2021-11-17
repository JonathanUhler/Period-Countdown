// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Support.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/28/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Support.java
// Class Diagram
/*

+-----------------------------------------------------------------------------------------------------+
|                                               Support                                               |
+-----------------------------------------------------------------------------------------------------+
| -versionAction(): void                                                                              |
| -getVersion(): JMenuItem                                                                            |
| -getIssue(): JMenuItem                                                                              |
| -updateJsonAction(): void                                                                           |
| -updateJarAction(): void                                                                            |
| -updateAction(): void                                                                               |
| -getUpdate(): JMenuItem                                                                             |
| +getSupportMenu(): JMenu                                                                            |
+-----------------------------------------------------------------------------------------------------+

*/
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package graphics;


import calendar.SchoolCalendar;
import main.PeriodCountdown;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import updater.Download;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class Support
//
// Handles the "support" menu and update routine
//
public class Support {

    // ====================================================================================================
    // private void versionAction
    //
    // Popup to show the app version
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // None
    //
    private void versionAction() {
        // Display the popup with the version message
        JOptionPane.showConfirmDialog(
                null,
                "Version " + PeriodCountdown.VERSION,
                "Version",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null);
    }
    // end: private void versionAction


    // ====================================================================================================
    // private JMenuItem getVersion
    //
    // Gets the menu item object for the version
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // versionMenu: the version menu item object
    //
    private JMenuItem getVersion() {
        JMenuItem versionMenu = new JMenuItem("Version"); // Set the text for the version button

        versionMenu.addActionListener(e -> {
            try { this.versionAction(); } catch (Exception ex) { ex.printStackTrace(); } // Add an action listener that calls the versionAction method upon clicking the button
        });

        return versionMenu; // Return the menu item
    }
    // end: private JMenuItem getVersion


    // ====================================================================================================
    // private JMenuItem getIssue
    //
    // Gets the menu item object for the Github issue hyperlink
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // issueMenu:   the issue hyperlink JMenuItem object
    //
    private JMenuItem getIssue() {
        JMenuItem issueMenu = new JMenuItem("Submit an Issue"); // Set the name of the new menu item

        issueMenu.addActionListener(e -> { // Add an action listener that opens the github issue link upon clicking
            try {Desktop.getDesktop().browse(java.net.URI.create("https://github.com/JonathanUhler/Period-Countdown/issues/new"));} catch (IOException ex) {ex.printStackTrace();}
        });

        return issueMenu; // Return the menu item
    }
    // end: private JMenuItem getIssue


    // ====================================================================================================
    // private void updateJsonAction
    //
    // Downloads and updates the JSON school data
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // None
    //
    private void updateJsonAction() throws Exception {
        // Download the file from the github URL
        Download.download("https://raw.githubusercontent.com/JonathanUhler/Period-Countdown/main/src/json/MVHS_School.json");

        // Get the location of the downloaded file
        Path source = Paths.get(new File(new File(new File(SchoolDisplay.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath()).getAbsolutePath()).getParent() +
                SchoolCalendar.FILE_SEP + "MVHS_School.json");
        // Set the destination of the file as the ~/.periodcountdown/json directory
        Path dest = Paths.get(SchoolDisplay.periodCountdownDirectory + SchoolCalendar.FILE_SEP + "json" + SchoolCalendar.FILE_SEP + SchoolDisplay.defaultSchoolData);

        Files.delete(dest); // Delete the file if it exists
        Files.move(source, dest); // Move the new file to its place
    }
    // end: private void updateJsonAction


    // ====================================================================================================
    // private void updateJarAction
    //
    // Downloads and updates the JAR file for Period-Countdown
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // None
    //
    private void updateJarAction() throws Exception {
        // Download the new JAR file
        // The new file will be in the same place as the current JAR and will replace it immediately, but a restart is needed for the chages to take effect
        Download.download("https://raw.githubusercontent.com/JonathanUhler/Period-Countdown/main/src/PeriodCountdown.jar");
    }
    // end: private void updateJarAction


    // ====================================================================================================
    // private void updateAction
    //
    // Updates the JSON school data and the JAR file
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // None
    //
    private void updateAction() {
        // Set the message to display warning the user that the update process will pull remote files to their computer
        ArrayList<Object> message = new ArrayList<>();
        message.add("Current Version " + PeriodCountdown.VERSION +
                "\nUpdating this software will pull the latest changes from https://github.com/JonathanUhler/Period-Countdown/ to your local machine" +
                "\nIf you do not consent to pulling remote files to your machine, press \"Cancel\"");

        // Display the update confirmation dialog box
        int confirmUpdate = JOptionPane.showConfirmDialog(
                null,
                message.toArray(new Object[0]),
                "Confirm Update",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null);

        // If the user is okay with the update process, continue
        if (confirmUpdate == JOptionPane.OK_OPTION) {
            // Try to update the JSON and JAR files
            try {
                this.updateJsonAction();
                this.updateJarAction();
            }
            // Catch any errors from the update process
            catch (Exception e) {
                JOptionPane.showConfirmDialog(
                        null,
                        "The update could not be completed\n\nError: " + e.getMessage() + "\n\nTry:\n\t1) Updating using a privileged account\n\t2) Connecting to the internet", // Print out the error message
                        "Update Failure",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null);

                // Exit the method
                return;
            }

            // Show a confirmation message and prompt the user to restart the app
            JOptionPane.showConfirmDialog(
                    null,
                    "Latest branch from Github has successfully been pulled.\nA restart of this software is needed for changes to take effect.",
                    "Update Success",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null);
        }
    }
    // end: private void updateAction


    // ====================================================================================================
    // private JMenuItem getUpdate
    //
    // Gets the menu item for the update button
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // updateMenu:  the JMenuItem object for the update button
    //
    private JMenuItem getUpdate() {
        JMenuItem updateMenu = new JMenuItem("Update"); // Set the name of a new JMenuItem

        updateMenu.addActionListener(e -> {
            this.updateAction(); // Add an action listener that calls the updateAction method upon click
        });

        return updateMenu; // Return the button
    }


    // ====================================================================================================
    // public JMenu getSupportMenu
    //
    // Returns the "support" menu with the issues hyperlink, update routine, and other choices
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // supportMenu: the JMenu for the support options
    //
    public JMenu getSupportMenu() {
        JMenu supportMenu = new JMenu("Support"); // Create and name the new menu

        // Add all the options to the menu
        supportMenu.add(this.getVersion());
        supportMenu.add(this.getUpdate());
        supportMenu.add(this.getIssue());

        return supportMenu; // Return the menu
    }
    // end: public JMenu getSupportMenu

}
// end: public class Support