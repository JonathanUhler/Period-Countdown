package natfe;


import util.Log;
import javax.swing.JFrame;


public class PeriodCountdown {

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

			screen.start();
		}
		catch (Exception e) {
			Log.gfxmsg("Error", "Unchecked exception thrown: " + e);
		}
	}

}
