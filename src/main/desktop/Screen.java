package desktop;


import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;
import time.UTCTime;
import time.Duration;
import os.OSPath;
import school.SchoolAPI;
import school.SchoolPeriod;
import school.SchoolJson;
import user.UserAPI;
import user.UserPeriod;
import user.UserJson;


/**
 * Graphical interface for the desktop application. This class is completely isolated from
 * the user and school API classes, which are shared by the desktop and web user interfaces.
 *
 * The {@code start} method is used to begin a timer that will refresh the screen every second.
 *
 * This class also contains several getters and setters for the information in the user json file.
 *
 * @author Jonathan Uhler
 */
public class Screen extends JPanel {

    private SchoolAPI schoolAPI;
    private UserAPI userAPI;
    
    
    /**
     * Constructs a new {@code Screen} object.
     */
    public Screen() {
        // userAPI must be defined first, since data within it may be used to define schoolAPI
        try {
            this.userAPI = new UserAPI(OSPath.join(OSPath.getUserJsonDiskPath(),
                                                   OSPath.getUserJsonFile()));
        }
        catch (IOException | IllegalArgumentException e) {
            PCDesktopApp.displayMessage("Error", "Screen: Exception when creating UserAPI\n" + e);
        }
        
        try {
            if (this.userAPI == null) {
                throw new IllegalArgumentException("userAPI is null, cannot get school file name");
            }
            // Create schoolAPI with the user's preferred school file. Also, since some
            // institutions (like colleges) have "Days" data that is specific to each user rather
            // than a consistent bell schedule, there is an option to store that data in User.json
            // instead of the school file
            this.schoolAPI = new SchoolAPI(this.userAPI.getSchoolFile());
        }
        catch (FileNotFoundException | IllegalArgumentException e) {
            PCDesktopApp.displayMessage("Error", "Screen: Exception when creating SchoolAPI\n" + e);
        }
    }
    
    
    /**
     * Returns a list of period numbers defined by the user json file.
     *
     * @return a list of period numbers defined by the user json file.
     */
    protected List<String> getUserPeriodKeys() {
        if (this.userAPI == null) {
            return null;
        }
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
        if (this.userAPI == null) {
            return null;
        }
        SchoolPeriod keyPeriod = new SchoolPeriod(key, "", UTCTime.now(), UTCTime.now(), false);
        return this.userAPI.getPeriod(keyPeriod);
    }
    
    
    /**
     * Returns the current school json file path.
     *
     * @return the current school json file path.
     */
    protected Path getUserSchoolFile() {
        if (this.userAPI == null) {
            return null;
        }
        return this.userAPI.getSchoolFile();
    }
    
    
    /**
     * Returns a list of available school json file names.
     *
     * @return a list of available school json file names.
     */
    protected List<String> getAvailableSchools() {
        if (this.userAPI == null) {
            return new ArrayList<>();
        }
        return this.userAPI.getAvailableSchools();
    }
    
    
    /**
     * Returns the theme color.
     *
     * @return the theme color.
     */
    protected int getUserTheme() {
        if (this.userAPI == null) {
            return 0x000000;
        }
        return this.userAPI.getTheme();
    }
    
    
    /**
     * Returns the name of the font.
     *
     * @return the name of the font.
     */
    protected String getUserFont() {
        if (this.userAPI == null) {
            return null;
        }
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
        if (this.userAPI == null) {
            return;
        }
        if (file == null) {
            return;
        }
        
        this.userAPI.setSchoolFile(Paths.get(file));
        try {
            this.schoolAPI = new SchoolAPI(this.userAPI.getSchoolFile());
        }
        catch (FileNotFoundException | IllegalArgumentException e) {
            PCDesktopApp.displayMessage("Error", "Screen: Exception when creating SchoolAPI\n" + e);
        }
    }
    
    
    /**
     * Sets the theme color.
     *
     * @param r  the red channel.
     * @param g  the green channel.
     * @param b  the blue channel.
     */
    protected void setUserTheme(int r, int g, int b) {
        if (this.userAPI != null) {
            this.userAPI.setTheme(r, g, b);
        }
    }
    
    
    /**
     * Sets the font name.
     *
     * @param font  the name of the font.
     */
    protected void setUserFont(String font) {
        if (this.userAPI != null) {
            this.userAPI.setFont(font);
        }
    }
    
    
    /**
     * Gets the width, in pixels, of a string.
     *
     * @param font  the current font.
     * @param text  the string.
     *
     * @return the width of the string, in pixels, when displayed with the specified font.
     */
    private int getTextWidth(String text, Font font) {
        if (font == null || text == null) {
            return 0;
        }

        AffineTransform affineTransform = new AffineTransform();
        FontRenderContext fontRenderContext = new FontRenderContext(affineTransform, true, true);
        Rectangle2D fontBounds = font.getStringBounds(text, fontRenderContext);
        return (int) fontBounds.getWidth();
    }
    
    
    /**
     * Draws information to the screen.
     *
     * @param g  the {@code Graphics} object to draw with.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.schoolAPI == null || this.userAPI == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;

        // Get timing information
        UTCTime now = UTCTime.now();
        ZonedDateTime localNow = now.to(ZoneId.systemDefault().getId()).asZonedDateTime();
        SchoolPeriod schoolPeriod = schoolAPI.getCurrentPeriod(now);
        SchoolPeriod nextSchoolPeriod = schoolAPI.getNextCountedPeriod(now);
        Duration timeRemaining = schoolAPI.getTimeRemaining(now);
        if (timeRemaining == null) {
            timeRemaining = new Duration(0, 0, 0, 0);
        }

        UserPeriod userPeriod = userAPI.getPeriod(schoolPeriod);
        String periodName = userPeriod.getName();
        String periodStatus = userPeriod.getStatus();

        // Get common graphical information and calculate sizing
        Dimension size = this.getSize();
        int width = size.width;
        int height = size.height;
        int smallMargin = (int) (height * 0.06);
        int baseMargin = (int) (width * 0.1);
        int sixthMargin = (int) (height * 0.16);
        int fifthMargin = (int) (height * 0.20);
        int thirdMargin = (int) (height * 0.27);
        int halfMargin = (int) (height * 0.5);
        int threeFifthsMargin = (int) (height * 0.6);
        int nineTenthsMargin = (int) (height * 0.9);
        int fullMargin = (int) (height * 0.95);
        int userThemeColor = this.getUserTheme();
        String userFontName = this.getUserFont();
        Color themeColor = new Color(userThemeColor);

        Font secondaryFont = new Font(userFontName, Font.PLAIN, (int) (height * 0.05));
        Font tertiaryFont = new Font(userFontName, Font.PLAIN, (int) (height * 0.04));
        Color secondaryColor = new Color(0, 0, 0);
        Color tertiaryColor = new Color(200, 200, 200);

        this.setBackground(new Color(245, 245, 245));

        String userString = periodName + " | " + periodStatus;
        Font userFont = new Font(userFontName, Font.PLAIN, smallMargin);
        int userWidth = this.getTextWidth(userString, userFont);
        int userMargin = (width - userWidth) / 2;
        g2.setColor(secondaryColor);
        g2.setFont(userFont);
        g2.drawString(userString, userMargin, fifthMargin);

        String dayString = localNow.format(DateTimeFormatter.ofPattern("EEEE"));
        int dayWidth = this.getTextWidth(dayString, secondaryFont);
        g2.setFont(secondaryFont);
        g2.drawString(dayString, width - dayWidth - baseMargin, nineTenthsMargin);

        String dateString = localNow.format(DateTimeFormatter.ofPattern("MM/dd/yy"));
        int dateWidth = this.getTextWidth(dateString, tertiaryFont);
        g2.setFont(tertiaryFont);
        g2.setColor(tertiaryColor);
        g2.drawString(dateString, width - dateWidth - baseMargin, fullMargin);

        if (nextSchoolPeriod != null) {
            Duration nextUpTime = new Duration(nextSchoolPeriod.getStart(),
                                               nextSchoolPeriod.getEnd().plus(1, UTCTime.SECONDS));
            String nextUpTimeStr = nextUpTime.toString();
            g2.setFont(tertiaryFont);
            g2.drawString(nextUpTimeStr, baseMargin, fullMargin);

            String nextUpString = userAPI.getPeriod(nextSchoolPeriod).getName();
            g2.setColor(secondaryColor);
            g2.setFont(secondaryFont);
            g2.drawString(nextUpString, baseMargin, nineTenthsMargin);
        }

        String timeString = timeRemaining.toString();
        Font timeFont = new Font(userFontName, Font.BOLD, sixthMargin);
        int timeWidth = this.getTextWidth(timeString, timeFont);
        int timeMargin = (width - timeWidth) / 2;
        GradientPaint themePaint = new GradientPaint(timeMargin,
                                                     halfMargin - sixthMargin / 2,
                                                     themeColor,
                                                     timeMargin,
                                                     halfMargin,
                                                     themeColor.brighter());
        g2.setPaint(themePaint);
        g2.setFont(timeFont);
        g2.drawString(timeString, timeMargin, halfMargin);

        if (nextSchoolPeriod != null) {
            Duration totalTime = new Duration(schoolPeriod.getStart(), nextSchoolPeriod.getStart());

            String durationString = totalTime.toString();
            int durationWidth = this.getTextWidth(durationString, secondaryFont);
            int durationMargin = (width - durationWidth) / 2;
            g2.setColor(tertiaryColor);
            g2.setFont(secondaryFont);
            g2.drawString(durationString, durationMargin, thirdMargin);

            int timeCompletedWidth =
                (int) (threeFifthsMargin * totalTime.portionComplete(timeRemaining));
            int progressMargin = (width - threeFifthsMargin) / 2;
            themePaint = new GradientPaint(timeMargin,
                                           threeFifthsMargin,
                                           themeColor,
                                           timeMargin,
                                           threeFifthsMargin + smallMargin,
                                           themeColor.brighter());
            g2.setStroke(new BasicStroke(2));
            g2.setPaint(themePaint);
            g2.drawRoundRect(progressMargin,
                             threeFifthsMargin,
                             threeFifthsMargin,
                             smallMargin, 6, 6);
            g2.fillRoundRect(progressMargin,
                             threeFifthsMargin,
                             timeCompletedWidth,
                             smallMargin, 6, 6);
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
     *
     * This loop will take over the current thread. Any caller of this method should not expect
     * the thread to be returned to them. Upon an {@code InterruptedException}, a message
     * is logged and the loop will attempt to recover.
     */
    public void start() {
        while (true) {
            this.repaint();
            
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                PCDesktopApp.displayMessage("Error", "Screen: InterruptedException\n" + e);
            }			
        }
    }
    
}
