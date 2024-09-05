package desktop;


import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.io.IOException;
import java.io.FileNotFoundException;
import time.UTCTime;
import time.Duration;
import os.OSPath;
import school.SchoolAPI;
import school.SchoolPeriod;
import user.UserAPI;
import user.UserPeriod;


public class Schedule extends JPanel {

    private SchoolAPI schoolAPI;
    private UserAPI userAPI;


    public Schedule() {
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


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.userAPI == null || this.schoolAPI == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        Dimension size = this.getSize();
        int width = size.width;
        int height = size.height;
        int baseMargin = (int) (height * 0.08);
        int panelHeight = height - 2 * baseMargin;
        int panelWidth = width - 2 * baseMargin;
        double hourHeight = (double) panelHeight / (double) Duration.HOURS_PER_DAY;
        double dayWidth = (double) panelWidth / (double) Duration.DAYS_PER_WEEK;

        Color backgroundColor = new Color(245, 245, 245);
        Color themeColor = new Color(this.userAPI.getTheme());
        Color secondaryColor = new Color(0, 0, 0);
        Color tertiaryColor = new Color(200, 200, 200);
        String userFontName = this.userAPI.getFont();
        Font userFont = new Font(userFontName, Font.PLAIN, (int) (height * 0.06));
        Font secondaryFont = new Font(userFontName, Font.PLAIN, (int) (height * 0.05));
        Font tertiaryFont = new Font(userFontName, Font.PLAIN, (int) (height * 0.02));

        this.setBackground(backgroundColor);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(baseMargin, baseMargin, panelWidth, panelHeight, 6, 6);
        g2.setColor(tertiaryColor);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(baseMargin, baseMargin, panelWidth, panelHeight, 6, 6);

        g2.setStroke(new BasicStroke(1));
        for (int day = 1; day < Duration.DAYS_PER_WEEK; day++) {
            int dayMargin = (int) (baseMargin + day * dayWidth);
            g2.drawLine(dayMargin, baseMargin, dayMargin, baseMargin + panelHeight);
        }
        g2.setFont(tertiaryFont);
        for (int hour = 1; hour < Duration.HOURS_PER_DAY; hour++) {
            int hourMargin = (int) (baseMargin + hour * hourHeight);
            g2.drawLine(baseMargin, hourMargin, baseMargin + panelWidth, hourMargin);
            g2.drawString(String.format("%02d:00", hour), baseMargin / 4, hourMargin);
        }

        String timezone = this.schoolAPI.getTimezone();
        UTCTime time = UTCTime.now().shiftedToPrevious(UTCTime.SUNDAY).to(timezone).toMidnight();
        UTCTime end = time.plus(Duration.DAYS_PER_WEEK, UTCTime.DAYS);
        while (time.isBefore(end)) {
            SchoolPeriod schoolPeriod = this.schoolAPI.getNextCountedPeriod(time);
            UTCTime schoolPeriodStart = schoolPeriod.getStart().to(timezone);
            UTCTime schoolPeriodEnd = schoolPeriod.getEnd().to(timezone);
            if (!schoolPeriodStart.isBefore(end)) {
                break;
            }

            double periodStartHours =
                (double) schoolPeriodStart.get(UTCTime.HOUR) +
                (double) schoolPeriodStart.get(UTCTime.MINUTE) / (double) Duration.MINUTES_PER_HOUR;
            double periodEndHours =
                (double) schoolPeriodEnd.get(UTCTime.HOUR) +
                (double) schoolPeriodEnd.get(UTCTime.MINUTE) / (double) Duration.MINUTES_PER_HOUR;
            int periodMargin = (int) (baseMargin + periodStartHours * hourHeight);
            int periodHeight = (int) ((periodEndHours - periodStartHours) * hourHeight);

            int day = schoolPeriodStart.get(UTCTime.DAY_OF_WEEK) % Duration.DAYS_PER_WEEK;
            int dayMargin = (int) (baseMargin + day * dayWidth);

            g2.setColor(secondaryColor);
            g2.fillRect(dayMargin, periodMargin, (int) dayWidth, periodHeight);

            time = schoolPeriodEnd.plus(1, UTCTime.MILLISECONDS);
        }
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 580);
    }

}
