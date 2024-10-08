package desktop;


import java.io.IOException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Desktop;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JColorChooser;
import school.SchoolJson;
import user.UserJson;
import user.UserPeriod;
import desktop.wizard.Wizard;


/**
 * Manages the application menu bar for the desktop version. Takes in a {@code Screen} object
 * that is used to access information from the {@code UserAPI} through protected methods.
 *
 * @author Jonathan Uhler
 */
public class Menu extends JMenuBar {
    
    /** The manager of this {@code Menu}. */
    private Screen screen;
    
    
    /**
     * Constructs a new {@code Menu} object.
     *
     * @param screen  the manager of this {@code Menu}.
     */
    public Menu(Screen screen) {
        this.screen = screen;
        
        // App settings menu
        JMenu settings = new JMenu("Settings");
        
        JMenuItem classInformation = new JMenuItem("Class Information");
        JMenuItem schoolInformation = new JMenuItem("School Information");
        JMenuItem theme = new JMenuItem("Theme");
        JMenuItem font = new JMenuItem("Font");

        classInformation.addActionListener(e -> this.classInformationAction());
        schoolInformation.addActionListener(e -> this.schoolInformationAction());
        theme.addActionListener(e -> this.themeAction());
        font.addActionListener(e -> this.fontAction());

        settings.add(classInformation);        
        settings.add(schoolInformation);
        settings.add(theme);
        settings.add(font);
        this.add(settings);
        
        // Help menu
        JMenu help = new JMenu("Help");

        JMenuItem dataWizard = new JMenuItem("School Data Wizard");
        JMenuItem submitIssue = new JMenuItem("Submit An Issue");

        dataWizard.addActionListener(e -> this.dataWizardAction());
        submitIssue.addActionListener(e -> this.submitIssueAction());

        help.add(dataWizard);
        help.add(submitIssue);
        this.add(help);
    }
    
    
    /**
     * Action method for the class information panel.
     *
     * This allows the user to interface with the class options and read or write the class name,
     * teacher name, and room number for each period in the JSON file.
     */
    private void classInformationAction() {
        List<String> periodNumbers = this.screen.getUserPeriodKeys();
        if (periodNumbers == null) {
            return;
        }
	
        // Create a structure to access the JTextFields later. The first set of keys is the period
        // numbers. The second set of keys is the field name ("Teacher", "Room", or "Name")
        Map<String, Map<String, JTextField>> periodInputFields = new HashMap<>();
        // List of JPanels that each hold the information in an entry to periodInputFields
        List<JPanel> periodInputPanels = new ArrayList<>();
        
        // Initialize periodInputFields
        for (String periodNumber : periodNumbers) {
            UserPeriod periodInfo = this.screen.getUserPeriod(periodNumber);
            if (periodInfo == null) {
                continue;
            }

            String status = periodInfo.getStatus();
            String name = periodInfo.getName();
            String teacher = periodInfo.getTeacher();
            String room = periodInfo.getRoom();
            
            Map<String, JTextField> periodInputField = new HashMap<>();
            
            JTextField nameTextField = new JTextField(name);
            JTextField teacherTextField = new JTextField(teacher);
            JTextField roomTextField = new JTextField(room);
            
            // 8 columns is the graphical width, but not the maximum number of characters.
            // That is defined in UserJson.java
            nameTextField.setColumns(8);
            teacherTextField.setColumns(8);
            roomTextField.setColumns(8);
            
            // Constructing entry for periodInputFields
            periodInputField.put(UserJson.NAME, nameTextField);
            periodInputField.put(UserJson.TEACHER, teacherTextField);
            periodInputField.put(UserJson.ROOM, roomTextField);
            
            periodInputFields.put(periodNumber, periodInputField);
            
            // Constructing entry for periodInputPanels
            JPanel periodInputPanel = new JPanel();
            periodInputPanel.add(new JLabel(status + ": "));
            periodInputPanel.add(new JLabel(UserJson.NAME));
            periodInputPanel.add(nameTextField);
            periodInputPanel.add(new JLabel(UserJson.TEACHER));
            periodInputPanel.add(teacherTextField);
            periodInputPanel.add(new JLabel(UserJson.ROOM));
            periodInputPanel.add(roomTextField);
            periodInputPanels.add(periodInputPanel);
        }
        
        // Main panel
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(550, 50 * periodNumbers.size()));
        for (JPanel periodInputPanel : periodInputPanels) {
            panel.add(periodInputPanel);
        }
        
        int confirm = JOptionPane.showConfirmDialog(null, panel, "Class Information",
                                                    JOptionPane.OK_CANCEL_OPTION,
                                                    JOptionPane.PLAIN_MESSAGE, null);
        
        if (confirm != JOptionPane.OK_OPTION) {
            return;
        }

        for (String key : periodInputFields.keySet()) {
            Map<String, JTextField> periodInputField = periodInputFields.get(key);
            String name = periodInputField.get(UserJson.NAME).getText();
            String teacher = periodInputField.get(UserJson.TEACHER).getText();
            String room = periodInputField.get(UserJson.ROOM).getText();

            if (name.length() > UserJson.MAX_FIELD_LEN) {
                name = name.substring(0, UserJson.MAX_FIELD_LEN) + "...";
            }
            if (teacher.length() > UserJson.MAX_FIELD_LEN) {
                teacher = teacher.substring(0, UserJson.MAX_FIELD_LEN) + "...";
            }
            if (room.length() > UserJson.MAX_FIELD_LEN) {
                room = room.substring(0, UserJson.MAX_FIELD_LEN) + "...";
            }
                
            this.screen.setUserPeriod(key, name, teacher, room);
        }
    }
    
    
    /**
     * Action method for the school json file selector.
     *
     * This allows the user to select a school JSON file from a combo box.
     */
    private void schoolInformationAction() {
        List<String> schoolJsonNames = this.screen.getAvailableSchools();
        JComboBox<String> options = new JComboBox<>(schoolJsonNames.toArray(new String[0]));
        options.setSelectedItem(this.screen.getUserSchoolFile());
        JComponent[] components = new JComponent[] {new JLabel("Select data file:"), options};
	
        int confirm = PCDesktopApp.displayDialog("School Information", components);
        if (confirm != JOptionPane.OK_OPTION) {
            return;
        }
        
        this.screen.setUserSchoolFile((String) options.getSelectedItem());
    }
    
    
    /**
     * Action method for the theme color picker.
     *
     * This allows the user to select a background color for the application using the built-in
     * {@code JColorChooser} component.
     */
    private void themeAction() {
        JColorChooser colorChooser = new JColorChooser();
        colorChooser.setColor(new Color(this.screen.getUserTheme()));
        JComponent[] components = new JComponent[] {colorChooser};
        
        int confirm = PCDesktopApp.displayDialog("Theme", components);
        if (confirm != JOptionPane.OK_OPTION) {
            return;
        }
        
        Color color = colorChooser.getColor();
        this.screen.setUserTheme(color.getRed(), color.getGreen(), color.getBlue());
    }
    
    
    /**
     * Action method for the font selector.
     *
     * This creates a combo box with all the available system fonts written out in that font
     * (so the user can see the font's style). The user is then able to select one of the fonts
     * from the combo box.
     */
    private void fontAction() {
        String[] fonts = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames();
        JComboBox<String> options = new JComboBox<>(fonts);
        options.setRenderer(new FontRenderer());
        options.setSelectedItem(this.screen.getUserFont());
        JComponent[] components = new JComponent[] {new JLabel("Select font: "), options};
	
        int confirm = PCDesktopApp.displayDialog("Font", components);
        if (confirm != JOptionPane.OK_OPTION) {
            return;
        }
        
        // Font name is set directly. If the font used in the Font(String) constructor is
        // invalid, there is no error and the default font is chosen instead, so this
        // operation is safe
        String font = (String) options.getSelectedItem();
        this.screen.setUserFont(font);
    }
    
    
    /**
     * Opens a new instance of the data generator.
     *
     * The data generator has its own UI built with swing. A new {@code JFrame} will be opened
     * and controlled by the wizard.
     */
    private void dataWizardAction() {
        Wizard wizard = new Wizard();
        wizard.display();
    }
    
    
    /**
     * Opens the "new issue" page on github after allowing the user to confirm they want
     * to proceed to an external website.
     */
    private void submitIssueAction() {
        String url = "https://github.com/JonathanUhler/Period-Countdown/issues/new/choose";
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null) {
            try {
                desktop.browse(new URI(url));
            }
            catch (IOException | URISyntaxException e) {
                PCDesktopApp.displayMessage("Browser Error", "Unabled to open link: " + e);
            }
        }
        else {
            PCDesktopApp.displayMessage("Browser Error", "Unabled to open link:\n" + url);
        }
    }
    
}
