// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// PeriodCountdown.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/8/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// PeriodCountdown.java
// Class Diagram
/*

+-----------------------------------------------------------------------------------------------------+
|                                           PeriodCountdown                                           |
+-----------------------------------------------------------------------------------------------------+
| -periodCountdownFrame: JFrame                                                                       |
+-----------------------------------------------------------------------------------------------------+
| -initFrame(String, PeriodPanel): void                                                               |
| +main(String[]): void                                                                               |
+-----------------------------------------------------------------------------------------------------+

*/
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package main;


import javax.swing.*;
import graphics.*;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class PeriodCountdown
//
// Main graphics manager class for Period-Countdown project
//
public class PeriodCountdown {

    private static JFrame periodCountdownFrame; // Period Countdown frame
    public static final String VERSION = "pre-2.0.2"; // Current version string


    // ====================================================================================================
    // private static void initFrame
    //
    // Initializes the app JFrame
    //
    // Arguments--
    //
    // frameName:       the name of the JFrame
    //
    // frameContents:   the PeriodPanel that contains the contents of the frame
    //
    private static void initFrame(String frameName, PeriodPanel frameContents) throws Exception {
        periodCountdownFrame = new JFrame(frameName); // Initialize the JFrame object

        JMenuBar periodCountdownMenuBar = new JMenuBar();
        periodCountdownMenuBar.add(new Settings().getSettingsMenu());
        periodCountdownMenuBar.add(new Support().getSupportMenu());
        periodCountdownFrame.setJMenuBar(periodCountdownMenuBar); // Add the menu bar at the top

        periodCountdownFrame.add(frameContents); // Add the PeriodPanel contents

        periodCountdownFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set the closing behaviour
        periodCountdownFrame.pack(); // Pack the frame and its components
        periodCountdownFrame.setVisible(true); // Display the frame
    }
    // end: private static void initFrame


    // ====================================================================================================
    // public static void main
    //
    // Java main method
    //
    // Arguments--
    //
    // args:    list of command line arguments
    //
    // Returns--
    //
    // None
    //
    public static void main(String[] args) throws Exception {
        PeriodPanel periodCountdownPanel = new PeriodPanel(375, 375); // Create a new contents JPanel
        initFrame("Period Countdown", periodCountdownPanel); // Initialize the frame

        periodCountdownPanel.animate(); // Begin the timer
    }
    // end: public static void main

}
// end: public class PeriodCountdown