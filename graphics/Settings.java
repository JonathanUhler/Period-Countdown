// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Settings.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/13/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


import javax.swing.*;
import java.util.ArrayList;
import java.util.Map;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class Settings
//
// Class to handle the user changing their settings
public class Settings {

    private final SchoolYear schoolYear;


    public Settings() throws Exception {
        this.schoolYear = new SchoolYear(SchoolDisplay.schoolData, SchoolDisplay.userData);
    }


    private void periodNamesAction() throws Exception {
        ArrayList<Object> message = new ArrayList<>();

        for (int i = SchoolCalendar.getFirstPeriod(); i <= SchoolCalendar.getLastPeriod(); i++) {
            Map<String, Object> period = schoolYear.getUserDataByPeriod(i);
            JTextField periodTextField = new JTextField((String) period.get(SchoolCalendar.getNameTerm));
            message.add("Period " + i);
            message.add(periodTextField);
        }

        int confirmEditPeriodNames = JOptionPane.showConfirmDialog(
                null,
                message.toArray(new Object[0]),
                "Edit Period Names",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null);

        if (confirmEditPeriodNames == JOptionPane.OK_OPTION) {
            for (int i = SchoolCalendar.getFirstPeriod(); i <= SchoolCalendar.getLastPeriod(); i++) {
                int periodNameTextIndex = 2 * i - 1;
                schoolYear.setUserPeriodNameByPeriod(i, ((JTextField) message.get(periodNameTextIndex)).getText());
            }
        }
    }


    private JMenuItem getPeriodNames() {
        JMenuItem periodNamesMenu = new JMenuItem("Period Names");
        periodNamesMenu.addActionListener(e -> {
            try { this.periodNamesAction(); } catch (Exception ex) { ex.printStackTrace(); }
        });
        return periodNamesMenu;
    }


    private void periodInfoAction() throws Exception {
        ArrayList<Object> message = new ArrayList<>();

        for (int i = SchoolCalendar.getFirstPeriod(); i <= SchoolCalendar.getLastPeriod(); i++) {
            Map<String, Object> period = schoolYear.getUserDataByPeriod(i);

            JTextField teacherTextField = new JTextField((String) period.get(SchoolCalendar.getTeacherNameTerm));
            message.add("Per  " + i + " | Teacher");
            message.add(teacherTextField);

            JTextField roomTextField = new JTextField(String.valueOf(period.get(SchoolCalendar.getRoomNumberTerm)));
            message.add("Per  " + i + " | Room Number");
            message.add(roomTextField);
        }

        int confirmEditPeriodNames = JOptionPane.showConfirmDialog(
                null,
                message.toArray(new Object[0]),
                "Edit Period Information",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null);

        if (confirmEditPeriodNames == JOptionPane.OK_OPTION) {
            for (int i = SchoolCalendar.getFirstPeriod(); i <= SchoolCalendar.getLastPeriod(); i++) {
                int teacherNameTextIndex = 4 * i - 3; // ALGEBRA!
                int roomNumTextIndex = 4 * i - 1; // ALGEBRA AGAIN!
                schoolYear.setUserPeriodInfoByPeriod(i, ((JTextField) message.get(teacherNameTextIndex)).getText(), ((JTextField) message.get(roomNumTextIndex)).getText());
            }
        }
    }


    private JMenuItem getPeriodInfo() {
        JMenuItem periodInfoMenu = new JMenuItem("Period Information");
        periodInfoMenu.addActionListener(e -> {
            try { this.periodInfoAction(); } catch (Exception ex) { ex.printStackTrace(); }
        });
        return periodInfoMenu;
    }


    private void themeAction() {

    }


    private JMenuItem getTheme() {
        JMenuItem themeMenu = new JMenuItem("Theme (coming soon...)");
        themeMenu.addActionListener(e -> this.themeAction());
        return themeMenu;
    }


    private void nextUpAction(int verbosity) throws Exception {
        this.schoolYear.setUserNextUp(verbosity);
    }


    private JMenu getNextUp() {
        JMenu nextUpMenu = new JMenu("Next Up...");

        JMenuItem none = new JMenuItem("None");
        JMenuItem oneName = new JMenuItem("Next class - Name only");
        JMenuItem oneAll = new JMenuItem("Next class - All information");
        JMenuItem allName = new JMenuItem("All classes - Name only");
        JMenuItem allAll = new JMenuItem("All classes - All information");

        none.addActionListener(e -> {
            try { this.nextUpAction(NextUp.NONE); } catch (Exception ex) { ex.printStackTrace(); }
        });
        oneName.addActionListener(e -> {
            try { this.nextUpAction(NextUp.ONE_NAME); } catch (Exception ex) { ex.printStackTrace(); }
        });
        oneAll.addActionListener(e -> {
            try { this.nextUpAction(NextUp.ONE_ALL_INFO); } catch (Exception ex) { ex.printStackTrace(); }
        });
        allName.addActionListener(e -> {
            try { this.nextUpAction(NextUp.ALL_NAME); } catch (Exception ex) { ex.printStackTrace(); }
        });
        allAll.addActionListener(e -> {
            try { this.nextUpAction(NextUp.ALL_ALL_INFO); } catch (Exception ex) { ex.printStackTrace(); }
        });

        nextUpMenu.add(none);
        nextUpMenu.add(oneName);
        nextUpMenu.add(oneAll);
        nextUpMenu.add(allName);
        nextUpMenu.add(allAll);

        return nextUpMenu;
    }


    public JMenuBar getSettingsMenu() {
        JMenuBar settingsBar = new JMenuBar();
        JMenu settingsMenu = new JMenu("Settings");

        settingsMenu.add(this.getPeriodNames());
        settingsMenu.add(this.getPeriodInfo());
        settingsMenu.add(this.getTheme());
        settingsMenu.add(this.getNextUp());
        settingsBar.add(settingsMenu);

        return settingsBar;
    }

}
