// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// PCDesktopApp.java
// Period-Countdown (Desktop)
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package desktop;


import util.Log;
import javax.swing.JFrame;

// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class PCDesktopApp
//
// Main class for Period-Countdown (Desktop version). Creates and the displays the Screen and Menu
//
public class PCDesktopApp {

	// ====================================================================================================
	// public static void main
	//
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

			screen.start(); // This starts a while(true) loop, so nothing should be after it in this method
		}
		catch (Exception e) {
			// Catch any exceptions that aren't handled in the rest of the code
			Log.gfxmsg("Error", "Unchecked exception thrown: " + e);
		}
	}
	// end: public static void main

}
