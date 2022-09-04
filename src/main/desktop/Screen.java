// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Screen.java
// Period-Countdown (Desktop)
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package desktop;


import util.Log;
import util.DateTime;
import util.Duration;
import util.Tools;
import school.SchoolAPI;
import school.SchoolPeriod;
import school.SchoolJson;
import user.UserAPI;
import user.UserPeriod;
import user.UserJson;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class Screen extends JPanel
//
// Main class to handle displaying information. Information is shown in the paintComponent method. The
// start() method is used to begin a timer that will refresh the screen every second. This class also
// contains several getters and setters for the options in User.json
//
public class Screen extends JPanel {

	// Font constants. The default font is Arial, which is changed later on to match what is in User.json
	private static String FONT_NAME = "Arial";
	private static int FONT_STYLE = Font.PLAIN;

	// Display margins -- A buffer size to separate lines of text
	private int MARGIN = this.getSize().width / 40;
	

	// APIs to access information derived from the json data
	private SchoolAPI schoolAPI;
	private UserAPI userAPI;
	

	// ----------------------------------------------------------------------------------------------------
	// public Screen
	//
	public Screen() {
		// userAPI must be defined first, since data within it is needed to define schoolAPI
		try {
			this.userAPI = new UserAPI(UserJson.DEFAULT_FILE);
			Screen.FONT_NAME = this.userAPI.getFont(); // Set user's preferred font
		}
		catch (FileNotFoundException | IllegalArgumentException e) {
			Log.gfxmsg("Error", "Screen: Exception when creating UserAPI\n\n" + e);
		}

		try {
			if (this.userAPI == null)
				throw new IllegalArgumentException("userAPI is null, cannot get school json file name");
			// Create schoolAPI with the user's preferred school file. Also, since some institutions (like
			// colleges) have "Days" data that is specific to each user rather than a consistent bell schedule,
			// there is an option to store that data in User.json instead of the school file
			this.schoolAPI = new SchoolAPI(this.userAPI.getSchoolFile(), this.userAPI.attemptGetDays());
		}
		catch (FileNotFoundException | IllegalArgumentException e) {
			Log.gfxmsg("Error", "Screen: Exception when creating SchoolAPI\n\n" + e);
		}
	}
	// end: public Screen


	// ====================================================================================================
	// GET methods
	protected ArrayList<String> getUserPeriodKeys() {
		if (this.userAPI == null)
			return null;
		return this.userAPI.getPeriodKeys();
	}

	protected UserPeriod getUserPeriod(String key) {
		if (this.userAPI == null)
			return null;
		// Access to getPeriod here only needs the key, so the other information can be placeholder
		return this.userAPI.getPeriod(new SchoolPeriod(key, "", "00:00", "00:00"));
	}

	protected String getUserSchoolFile() {
		if (this.userAPI == null)
			return null;
		return this.userAPI.getSchoolFile();
	}

	protected ArrayList<String> getAvailableSchools() {
		if (this.userAPI == null)
			return new ArrayList<>();
		return this.userAPI.getAvailableSchools();
	}

	protected String getUserNextUp() {
		if (this.userAPI == null)
			return null;
		return this.userAPI.getNextUp();
	}

	protected int getUserTheme() {
		if (this.userAPI == null)
			return 0xffffff;
		return this.userAPI.getTheme();
	}

	protected String getUserFont() {
		if (this.userAPI == null)
			return null;
		return this.userAPI.getFont();
	}
	// end: GET methods


	// ====================================================================================================
	// SET methods
	protected void setUserPeriod(String key, String name, String teacher, String room) {
		if (this.userAPI != null) {
			Map<String, String> data = new HashMap<>();
			data.put(UserJson.NAME, name);
			data.put(UserJson.TEACHER, teacher);
			data.put(UserJson.ROOM, room);
			this.userAPI.setPeriod(key, data);
		}
	}

	protected void setUserSchoolFile(String file) {
		if (this.userAPI == null)
			return;
		
		this.userAPI.setSchoolFile(file);
		// School file was changed, so use the same routine as the constructor to reload the information
		try {
			if (this.userAPI == null)
				throw new IllegalArgumentException("userAPI is null, cannot get school json file name");
			this.schoolAPI = new SchoolAPI(this.userAPI.getSchoolFile(), this.userAPI.attemptGetDays());
		}
		catch (FileNotFoundException | IllegalArgumentException e) {
			Log.gfxmsg("Error", "Screen: Exception when creating SchoolAPI\n\n" + e);
		}
	}

	protected void setUserNextUp(String verbosity) {
		if (this.userAPI != null)
			this.userAPI.setNextUp(verbosity);
	}

	protected void setUserTheme(int r, int g, int b) {
		if (this.userAPI != null)
			this.userAPI.setTheme(r, g, b);
	}

	protected void setUserFont(String font) {
		if (this.userAPI != null)
			this.userAPI.setFont(font);
		Screen.FONT_NAME = font;
	}
	// end: SET methods


	// ====================================================================================================
	// Font to width
    private int getTextWidth(Font font, String text) {
		if (font == null || text == null)
			return 0;
		
        return (int) (font.getStringBounds(text,
                new FontRenderContext(new AffineTransform(), true, true))
                .getWidth());
    }

    private Font getFontForWidth(String text, int width) {
		if (width < 0 || text == null)
			return null;

		Font font = new Font(Screen.FONT_NAME, Screen.FONT_STYLE, width);
        int textWidth = width + 1; // Start with textWidth larger than width to run the while loop
        while (textWidth > width) {
            textWidth = this.getTextWidth(font, text);
            font = new Font(font.getName(), font.getStyle(), font.getSize() - 1);
        }
        return font;
    }
	// end: Font to width


	// ====================================================================================================
	// public void paintComponent
	//
	// Draws information to the screen
	//
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Cannot draw anything if the APIs are undefined
		if (this.schoolAPI == null || this.userAPI == null)
			return;

		// Get current time
		DateTime now = new DateTime();

		// School json API information
		SchoolPeriod schoolPeriod = schoolAPI.getCurrentPeriod(now);
		Duration timeRemaining = schoolAPI.getTimeRemaining(now);
		if (timeRemaining == null)
			timeRemaining = new Duration(0, 0, 0, 0);

		// User json API information
		UserPeriod userPeriod = userAPI.getPeriod(schoolPeriod);
		String periodName = userPeriod.getName();
		String periodStatus = userPeriod.getStatus();

		// Next up information
		String nextUp = this.userAPI.getNextUp();
		ArrayList<String> nextPeriods = Tools.getNextUpList(this.schoolAPI, this.userAPI, now);

		// Displaying
		this.MARGIN = this.getSize().width / 40; // Update margin size if the screen has changed size
		int textY = 0;
		this.setBackground(new Color(this.getUserTheme()));

		// Displaying user data
		String userStr = periodName + " | " + periodStatus;
		int userWidth = this.getSize().width - (this.MARGIN * 2);
		Font userFont = this.getFontForWidth(userStr, userWidth);
		textY += userFont.getSize();
		g.setFont(userFont);
		g.setColor(Color.BLACK);
		g.drawString(userStr, this.MARGIN, textY);

		// Displaying time data
		// Create timeRemainingLen as a string which is the length of the current time string as all zeros. This
		// fixes a minor bug with fonts where digits are not the same width and the time remaining will resize
		// every time it changes which looks bad. Because this string is constant (at least until a significant
		// digit changes, like hours going 100 -> 99), the font size is also constant.
		String timeRemainingLen = "0".repeat(String.valueOf(timeRemaining.hr()).length()) + ":00:00";
		String timeRemainingStr = timeRemaining.toString();
		int timeRemainingWidth = this.getSize().width - (this.MARGIN * 2);
		Font timeRemainingFont = this.getFontForWidth(timeRemainingLen, timeRemainingWidth);
		textY += timeRemainingFont.getSize() + this.MARGIN;
		g.setFont(timeRemainingFont);
		g.setColor(Color.BLACK);
		g.drawString(timeRemainingStr, this.MARGIN, textY);

		// Displaying next up
		int nextUpWidth = this.getSize().width - (this.MARGIN);
		Font nextUpFont = this.getFontForWidth("********** | **:**-**:** | **********, **********", nextUpWidth);
		if (!nextUp.equals(UserJson.NEXT_UP_DISABLED)) {
			textY += this.MARGIN + this.MARGIN + nextUpFont.getSize();
			g.setFont(new Font(Screen.FONT_NAME, Font.BOLD, nextUpFont.getSize()));
			g.setColor(Color.BLACK);
			g.drawString("Upcoming Periods", this.MARGIN, textY);
			g.setFont(new Font(Screen.FONT_NAME, Screen.FONT_STYLE, nextUpFont.getSize()));
			if (nextPeriods.size() == 0) {
				textY += (this.MARGIN / 2) + nextUpFont.getSize();
				g.drawString("No more classes today", this.MARGIN, textY);
			}
		}
		for (int i = 0; i < nextPeriods.size(); i++) {
			String nextPeriodStr = nextPeriods.get(i);
			textY += (this.MARGIN / 2) + ((nextUpFont.getSize() + (this.MARGIN / 2)));
			g.drawString(nextPeriodStr, this.MARGIN, textY);
		}
	}
	// end: public void paintComponent
	

	// ====================================================================================================
	// public Dimension getPreferredSize
	//
	// Return the window size
	//
	// Returns--
	//
	//  The windows size (400 by 400)
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(400, 400);
	}
	// end: public Dimension getPreferredSize


	// ====================================================================================================
	// public void start
	//
	// Starts the refresh loop. Must be called separately after the Screen object is created
	//
	public void start() {
		while (true) {
			this.repaint();
			
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				Log.gfxmsg("Error", "Screen: InterruptedException from Thread.sleep\n\n" + e);
			}			
		}
	}
	// end: public void start

}
// end: public class Screen
