package desktop;


import jnet.Log;
import util.UTCTime;
import util.Duration;
import util.Tools;
import util.OSPath;
import school.SchoolAPI;
import school.SchoolPeriod;
import school.SchoolJson;
import user.UserAPI;
import user.UserPeriod;
import user.UserJson;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;


/**
 * Graphical interface for the desktop application. This class is completely isolated from
 * the user and school API classes, which are shared by the desktop and web user interfaces.
 * <p>
 * The {@code start} method is used to begin a timer that will refresh the screen every second.
 * <p>
 * This class also contains several getters and setters for the information in the user json file.
 *
 * @author Jonathan Uhler
 */
public class Screen extends JPanel {

    /** Name of the font. The default font is Arial, which is changed later by the user json file */
    private static String FONT_NAME = "Arial";
    /** Font style. */
    private static int FONT_STYLE = Font.PLAIN;

    /** A graphical buffer between displayed elements. */
    private int MARGIN = this.getSize().width / 40;
	

    /** API to access information from the school json file. */
    private SchoolAPI schoolAPI;
    /** API to access information from the school user file. */
    private UserAPI userAPI;
	

    /**
     * Constructs a new {@code Screen} object.
     */
    public Screen() {
        // userAPI must be defined first, since data within it is needed to define schoolAPI
        try {
            this.userAPI = new UserAPI(OSPath.join(OSPath.getUserJsonDiskPath(),
                                                   OSPath.getUserJsonFile()));
            Screen.FONT_NAME = this.userAPI.getFont(); // Set user's preferred font
        }
        catch (FileNotFoundException | IllegalArgumentException e) {
            Log.gfxmsg("Error", "Screen: Exception when creating UserAPI\n\n" + e);
        }

        try {
            if (this.userAPI == null)
                throw new IllegalArgumentException("userAPI is null, cannot get school file name");
            // Create schoolAPI with the user's preferred school file. Also, since some
            // institutions (like colleges) have "Days" data that is specific to each user rather
            // than a consistent bell schedule, there is an option to store that data in User.json
            // instead of the school file
            this.schoolAPI = new SchoolAPI(this.userAPI.getSchoolFile());
        }
        catch (FileNotFoundException | IllegalArgumentException e) {
            e.printStackTrace();
            Log.gfxmsg("Error", "Screen: Exception when creating SchoolAPI\n\n" + e);
        }
    }


    /**
     * Returns a list of period numbers defined by the user json file.
     *
     * @return a list of period numbers defined by the user json file.
     */
    protected List<String> getUserPeriodKeys() {
        if (this.userAPI == null)
            return null;
        return this.userAPI.getPeriodKeys();
    }


    /**
     * Returns a {@code UserPeriod} object for the given period number.
     *
     * @param key  the period number to get an object for.
     *
     * @return a {@code UserPeriod} object for the given period number.
     */
    protected UserPeriod getUserPeriod(String key) {
        if (this.userAPI == null)
            return null;
        // Access to getPeriod here only needs the key, so the other information can be placeholder
        SchoolPeriod keyPeriod = new SchoolPeriod(key, "", UTCTime.now(), UTCTime.now(), false);
        return this.userAPI.getPeriod(keyPeriod);
    }


    /**
     * Returns the current school json file path.
     *
     * @return the current school json file path.
     */
    protected Path getUserSchoolFile() {
        if (this.userAPI == null)
            return null;
        return this.userAPI.getSchoolFile();
    }


    /**
     * Returns a list of available school json file names.
     *
     * @return a list of available school json file names.
     */
    protected List<String> getAvailableSchools() {
        if (this.userAPI == null)
            return new ArrayList<>();
        return this.userAPI.getAvailableSchools();
    }


    /**
     * Returns the verbosity for the "next up" feature.
     *
     * @return the verbosity for the "next up" feature.
     */
    protected String getUserNextUp() {
        if (this.userAPI == null)
            return null;
        return this.userAPI.getNextUp();
    }


    /**
     * Returns the theme color.
     *
     * @return the theme color.
     */
    protected int getUserTheme() {
        if (this.userAPI == null)
            return 0xffffff;
        return this.userAPI.getTheme();
    }


    /**
     * Returns the name of the font.
     *
     * @return the name of the font.
     */
    protected String getUserFont() {
        if (this.userAPI == null)
            return null;
        return this.userAPI.getFont();
    }


    /**
     * Sets information for a user-defined period.
     *
     * @param key      the period number to set.
     * @param name     the user-defined name of the period.
     * @param teacher  the name of the teacher.
     * @param room     the room name/number.
     */
    protected void setUserPeriod(String key, String name, String teacher, String room) {
        if (this.userAPI != null) {
            Map<String, String> data = new HashMap<>();
            data.put(UserJson.NAME, name);
            data.put(UserJson.TEACHER, teacher);
            data.put(UserJson.ROOM, room);
            this.userAPI.setPeriod(key, data);
        }
    }


    /**
     * Sets the school json file.
     *
     * @param file  the name of the school json file.
     */
    protected void setUserSchoolFile(String file) {
        if (this.userAPI == null)
            return;

        this.userAPI.setSchoolFile(Paths.get(file));
        // School file was changed, so use the same routine as the constructor to reload the
        // information
        try {
            if (this.userAPI == null)
                throw new IllegalArgumentException("userAPI is null, cannot get school file name");
            this.schoolAPI = new SchoolAPI(this.userAPI.getSchoolFile());
        }
        catch (FileNotFoundException | IllegalArgumentException e) {
            Log.gfxmsg("Error", "Screen: Exception when creating SchoolAPI\n\n" + e);
        }
    }


    /**
     * Sets the verbosity for the "next up" feature.
     *
     * @param verbosity  the verbosity for the "next up" feature.
     */
    protected void setUserNextUp(String verbosity) {
        if (this.userAPI != null)
            this.userAPI.setNextUp(verbosity);
    }


    /**
     * Sets the theme color.
     *
     * @param r  the red channel.
     * @param g  the green channel.
     * @param b  the blue channel.
     */
    protected void setUserTheme(int r, int g, int b) {
        if (this.userAPI != null)
            this.userAPI.setTheme(r, g, b);
    }


    /**
     * Sets the font name.
     *
     * @param font  the name of the font.
     */
    protected void setUserFont(String font) {
        if (this.userAPI != null)
            this.userAPI.setFont(font);
        Screen.FONT_NAME = font;
    }


    /**
     * Gets the width, in pixels, of a string.
     *
     * @param font  the current font.
     * @param text  the string.
     *
     * @return the width of the string, in pixels, when displayed with the specified font.
     */
    private int getTextWidth(Font font, String text) {
        if (font == null || text == null)
            return 0;
		
        return (int) (font.getStringBounds(text,
                                           new FontRenderContext(new AffineTransform(), true, true))
                      .getWidth());
    }


    /**
     * Gets a {@code Font} object such that the given string is less than or equal to the given
     * width.
     *
     * @param text   the string to fit.
     * @param width  the maximum allowable width of the string.
     *
     * @return the {@code Font} object that fits the text to the width.
     */
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


    /**
     * Draws information to the screen.
     *
     * @param g  the {@code Graphics} object to draw with.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Cannot draw anything if the APIs are undefined
        if (this.schoolAPI == null || this.userAPI == null)
            return;

        // Get current time
        UTCTime now = UTCTime.now();
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
        List<String> nextPeriods = Tools.getNextUpList(this.schoolAPI, this.userAPI,
                                                       this.schoolAPI.getTimezone(), now);

        // Displaying
        // Update margin size if the screen has changed size
        this.MARGIN = this.getSize().width / 40;
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
        // Create timeRemainingLen as a string which is the length of the current time string as
        // all zeros. This fixes a minor bug with fonts where digits are not the same width and
        // the time remaining will resize every time it changes which looks bad. Because this
        // string is constant (at least until a significant digit changes, like hours going
        // 100 -> 99), the font size is also constant.
        int numDigitsInHours = String.valueOf(timeRemaining.hr()).length();
        String timeRemainingLen = "0".repeat(numDigitsInHours) + ":00:00";
        String timeRemainingStr = timeRemaining.toString();
        int timeRemainingWidth = this.getSize().width - (this.MARGIN * 2);
        Font timeRemainingFont = this.getFontForWidth(timeRemainingLen, timeRemainingWidth);
        textY += timeRemainingFont.getSize() + this.MARGIN;
        g.setFont(timeRemainingFont);
        g.setColor(Color.BLACK);
        g.drawString(timeRemainingStr, this.MARGIN, textY);

        // Displaying next up
        int nextUpWidth = this.getSize().width - (this.MARGIN);
        String nextUpLen = "********** | **:**-**:** | **********, **********";
        Font nextUpFont = this.getFontForWidth(nextUpLen, nextUpWidth);
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
	

    /**
     * Returns the size of the windows. Default is 400x400 pixels.
     *
     * @return the size of the windows.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }


    /**
     * Starts the graphical refresh loop.
     */
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

}
