package natfe;


import util.Log;
import util.DateTime;
import util.Duration;
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


public class Screen extends JPanel {

	// Size and display constants
	// Fonts
	public static final String FONT_NAME = "Arial";
	public static final int FONT_STYLE = Font.PLAIN;

	// Display margins
	private int MARGIN = this.getSize().width / 40;
	

	private SchoolAPI schoolAPI;
	private UserAPI userAPI;
	

	public Screen() {
		try {
			this.userAPI = new UserAPI(UserJson.DEFAULT_FILE);
		}
		catch (FileNotFoundException | IllegalArgumentException e) {
			Log.gfxmsg("Error", "Screen: Exception when creating UserAPI\n\n" + e);
		}

		try {
			if (this.userAPI == null)
				throw new IllegalArgumentException("userAPI is null, cannot get school json file name");
			this.schoolAPI = new SchoolAPI(this.userAPI.getSchoolFile(), this.userAPI.attemptGetDays());
		}
		catch (FileNotFoundException | IllegalArgumentException e) {
			Log.gfxmsg("Error", "Screen: Exception when creating SchoolAPI\n\n" + e);
		}
	}


	protected ArrayList<String> getUserPeriodKeys() {
		if (this.userAPI == null)
			return null;
		return this.userAPI.getPeriodKeys();
	}


	protected UserPeriod getUserPeriod(String key) {
		if (this.userAPI == null)
			return null;
		return this.userAPI.getPeriod(new SchoolPeriod(key, "", "00:00", "00:00"));
	}


	protected void setUserPeriod(String key, String name, String teacher, String room) {
		if (this.userAPI != null) {
			Map<String, String> data = new HashMap<>();
			data.put(UserJson.NAME, name);
			data.put(UserJson.TEACHER, teacher);
			data.put(UserJson.ROOM, room);
			this.userAPI.setPeriod(key, data);
		}
	}


	protected String getUserSchoolFile() {
		if (this.userAPI == null)
			return null;
		return this.userAPI.getSchoolFile();
	}


	protected void setUserSchoolFile(String file) {
		if (this.userAPI == null)
			return;
		
		this.userAPI.setSchoolFile(file);
		try {
			if (this.userAPI == null)
				throw new IllegalArgumentException("userAPI is null, cannot get school json file name");
			this.schoolAPI = new SchoolAPI(this.userAPI.getSchoolFile());
		}
		catch (FileNotFoundException | IllegalArgumentException e) {
			Log.gfxmsg("Error", "Screen: Exception when creating SchoolAPI\n\n" + e);
		}
	}


	protected String getUserNextUp() {
		if (this.userAPI == null)
			return null;
		return this.userAPI.getNextUp();
	}


	protected void setUserNextUp(String verbosity) {
		if (this.userAPI != null)
			this.userAPI.setNextUp(verbosity);
	}


	protected int getUserTheme() {
		if (this.userAPI == null)
			return 0xffffff;
		return this.userAPI.getTheme();
	}


	protected void setUserTheme(int r, int g, int b) {
		if (this.userAPI != null)
			this.userAPI.setTheme(r, g, b);
	}


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
        int textWidth = width + 1;
        while (textWidth > width) {
            textWidth = this.getTextWidth(font, text);
            font = new Font(font.getName(), font.getStyle(), font.getSize() - 1);
        }
        return font;
    }


	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.MARGIN = this.getSize().width / 40;
		int textY = 0;
		
		if (this.schoolAPI == null || this.userAPI == null)
			return;

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
		ArrayList<String> nextPeriods = new ArrayList<>();
		
		SchoolPeriod nextPeriod = this.schoolAPI.getNextPeriodToday(now);
		while (nextPeriod != null) {
			if (nextUp.equals(UserJson.NEXT_UP_DISABLED))
				break;

			UserPeriod nextClass = null;
			if (nextPeriod.isCounted())
				nextClass = this.userAPI.getPeriod(nextPeriod);

			String periodString = ((nextClass == null) ? nextPeriod.getName() : nextClass.getName()) + " | " +
				nextPeriod.getStartTimeString() + "-" + nextPeriod.getEndTimeString();
			if (!nextPeriod.isFree() && !nextClass.isFree() && nextClass != null)
				periodString += " | " + nextClass.getTeacher() + ", " + nextClass.getRoom();
			nextPeriods.add(periodString);
			
			nextPeriod = this.schoolAPI.getNextPeriodToday(nextPeriod.getEndTime());
			if (nextUp.equals(UserJson.NEXT_UP_ONE))
				break;
		}

		// Displaying
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
		String timeRemainingStr = timeRemaining.toString();
		int timeRemainingWidth = this.getSize().width - (this.MARGIN * 2);
		Font timeRemainingFont = this.getFontForWidth(timeRemainingStr, timeRemainingWidth);
		textY += timeRemainingFont.getSize() + this.MARGIN;
		g.setFont(timeRemainingFont);
		g.setColor(Color.BLACK);
		g.drawString(timeRemainingStr, this.MARGIN, textY);

		// Displaying next up
		int nextUpWidth = this.getSize().width - (this.MARGIN);
		Font nextUpFont = this.getFontForWidth("*************** | **:**-**:** | ***************, ***************",
											   nextUpWidth);
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
			textY += (this.MARGIN / 2) + ((nextUpFont.getSize() + (this.MARGIN / 2)) * i);
			g.drawString(nextPeriodStr, this.MARGIN, textY);
		}
	}
	

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(400, 400);
	}

}
