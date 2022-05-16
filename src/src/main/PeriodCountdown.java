// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// PeriodCountdown.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/8/21
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
    public static final String VERSION = "pre-2.0.3.1"; // Current version string


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
        periodCountdownFrame = new JFrame(frameName);

        JMenuBar periodCountdownMenuBar = new JMenuBar();
        periodCountdownMenuBar.add(new Settings().getSettingsMenu());
        periodCountdownMenuBar.add(new Support().getSupportMenu());
        periodCountdownFrame.setJMenuBar(periodCountdownMenuBar);

        periodCountdownFrame.add(frameContents);

        periodCountdownFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        periodCountdownFrame.pack();
        periodCountdownFrame.setVisible(true);
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
    public static void main(String[] args) throws Exception {
        PeriodPanel periodCountdownPanel = new PeriodPanel(375, 375);
        initFrame("Period Countdown", periodCountdownPanel);

        periodCountdownPanel.animate();
    }
    // end: public static void main

}
// end: public class PeriodCountdown