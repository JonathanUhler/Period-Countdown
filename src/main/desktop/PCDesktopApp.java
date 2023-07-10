package desktop;


import jnet.Log;
import javax.swing.JFrame;


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
			Log.gfxmsg("Error", "Unchecked exception thrown: " + e);
		}
	}

}
