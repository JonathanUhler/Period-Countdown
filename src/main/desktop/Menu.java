// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Menu.java
// Period-Countdown (Desktop)
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package desktop;


import util.Log;
import school.SchoolJson;
import user.UserJson;
import user.UserPeriod;
import java.io.IOException;
import java.io.File;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JColorChooser;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class Menu extends JMenuBar
//
// Manages the app menu for the desktop version. Takes in a Screen object that is used to access the
// getters and setters of the created UserAPI
//
public class Menu extends JMenuBar {

	private Screen screen;
	

	// ----------------------------------------------------------------------------------------------------
	// public Menu
	//
	// Arguments--
	//
	// screen: Screen object used to access setters and getters for the User.json file
	//
	public Menu(Screen screen) {
		this.screen = screen;
		
		JMenu settings = new JMenu("Settings");
		this.add(settings);

		// Create menu items
		JMenuItem classInformation = new JMenuItem("Class Information");
		classInformation.addActionListener(e -> this.classInformation());
		settings.add(classInformation);

		JMenuItem schoolInformation = new JMenuItem("School Information");
		schoolInformation.addActionListener(e -> this.schoolInformation());
		settings.add(schoolInformation);

		JMenuItem nextUp = new JMenuItem("Next Up");
		nextUp.addActionListener(e -> this.nextUp());
		settings.add(nextUp);

		JMenuItem theme = new JMenuItem("Theme");
		theme.addActionListener(e -> this.theme());
		settings.add(theme);

		JMenuItem font = new JMenuItem("Font");
		font.addActionListener(e -> this.font());
		settings.add(font);
	}
	// end: public Menu


	// ====================================================================================================
	// private void classInformation
	//
	// Action method for the Class Information menu item. Displays a name, teacher, and room box
	// for each valid period throughout the day.
	//
	private void classInformation() {
		ArrayList<String> periodNumbers = this.screen.getUserPeriodKeys();
		if (periodNumbers == null)
			return;
		
		// Create a structure to access the JTextFields later. The first set of keys is the period
		// numbers. The second set of keys is the field name ("Teacher", "Room", or "Name")
		HashMap<String, HashMap<String, JTextField>> periodInputFields = new HashMap<>();
		// List of JPanels that each hold the information in an entry to periodInputFields
		ArrayList<JPanel> periodInputPanels = new ArrayList<>();

		// Initialize periodInputFields
		for (String periodNumber : periodNumbers) {
			UserPeriod periodInfo = this.screen.getUserPeriod(periodNumber);
			if (periodInfo == null)
				continue;

			String name = periodInfo.getName();
			String teacher = periodInfo.getTeacher();
			String room = periodInfo.getRoom();

			HashMap<String, JTextField> periodInputField = new HashMap<>();
			
			JTextField nameTextField = new JTextField(name);
			JTextField teacherTextField = new JTextField(teacher);
			JTextField roomTextField = new JTextField(room);

			// 8 columns is the graphical width, but not the maximum number of characters. That is defined
			// in UserJson.java
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
			periodInputPanel.add(new JLabel(periodNumber + ": "));
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
		if (confirm == JOptionPane.OK_OPTION) {
			// Set all of the information
			for (String key : periodInputFields.keySet()) {
				HashMap<String, JTextField> periodInputField = periodInputFields.get(key);
				String name = periodInputField.get(UserJson.NAME).getText();
				String teacher = periodInputField.get(UserJson.TEACHER).getText();
				String room = periodInputField.get(UserJson.ROOM).getText();

				// Limit entries for the three fields to a maximum length for display purposes
				if (name.length() > UserJson.MAX_FIELD_LEN)
					name = name.substring(0, UserJson.MAX_FIELD_LEN) + "...";
				if (teacher.length() > UserJson.MAX_FIELD_LEN)
					teacher = teacher.substring(0, UserJson.MAX_FIELD_LEN) + "...";
				if (room.length() > UserJson.MAX_FIELD_LEN)
					room = room.substring(0, UserJson.MAX_FIELD_LEN) + "...";

				this.screen.setUserPeriod(key, name, teacher, room);
			}
		}
	}
	// end: private void classInformation


	// ====================================================================================================
	// private void schoolInformation
	//
	// Action method for School Information menu item. Lists all the available school json files packaged
	// with the jar file
	//
	private void schoolInformation() {
		// Get and load the path to the running jar file, independent of the working directory
		String jarPath = PCDesktopApp.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(jarPath);
		}
		catch (IOException e) {
			Log.gfxmsg("Internal Error", "Cannot load jarfile resources: " + e);
			return;
		}
		
		ArrayList<String> schoolJsonNames = new ArrayList<>();
		// Read all of the resource entries in the jar file
		Enumeration<JarEntry> jarResources = jarFile.entries();
		while (jarResources.hasMoreElements()) {
			JarEntry jarResource = jarResources.nextElement();
			String resourceName = jarResource.getName();
			// If the resource is not the User.json file and otherwise matches the json file regex, then
			// add just the file name (SchoolJson.EXPECTED_PATH == "/assets/json", which is removed to just
			// get the name of the file)
			if (!resourceName.endsWith(UserJson.DEFAULT_FILE) && resourceName.matches(UserJson.FILE_NAME_REGEX)) {
				if (resourceName.startsWith(SchoolJson.EXPECTED_PATH))
					schoolJsonNames.add(resourceName.substring(SchoolJson.EXPECTED_PATH.length()));
				else
					schoolJsonNames.add(resourceName);
			}
		}

		// Main panel
		JPanel panel = new JPanel();
		JComboBox<String> options = new JComboBox<>(schoolJsonNames.toArray(new String[0]));
		options.setSelectedItem(this.screen.getUserSchoolFile());
		panel.add(new JLabel("Select data file:"));
		panel.add(options);

		int confirm = JOptionPane.showConfirmDialog(null, panel, "School Information",
													JOptionPane.OK_CANCEL_OPTION,
													JOptionPane.PLAIN_MESSAGE, null);
		if (confirm == JOptionPane.OK_OPTION) {
			this.screen.setUserSchoolFile((String) options.getSelectedItem());
		}
	}
	// end: private void schoolInformation


	// ====================================================================================================
	// private void nextUp
	//
	// Action method for next up feature. Allows choice between verbosity options
	//
	private void nextUp() {
		JPanel panel = new JPanel();
		// List of choices, which are just string constants
		JComboBox<String> options = new JComboBox<>(new String[]{UserJson.NEXT_UP_DISABLED,
																 UserJson.NEXT_UP_ONE,
																 UserJson.NEXT_UP_ALL});
		options.setSelectedItem(this.screen.getUserNextUp());
		panel.add(new JLabel("Select verbosity:"));
		panel.add(options);
		
		int confirm = JOptionPane.showConfirmDialog(null, panel, "Next Up",
													JOptionPane.OK_CANCEL_OPTION,
													JOptionPane.PLAIN_MESSAGE, null);

		if (confirm == JOptionPane.OK_OPTION) {
			String verbosity = (String) options.getSelectedItem();
			this.screen.setUserNextUp(verbosity);
		}
	}
	// end: private void nextUp


	// ====================================================================================================
	// private void theme
	//
	// Action method for theme feature. Displays a JColorChooser that the user can interface with
	//
	private void theme() {
		JPanel panel = new JPanel();
		JColorChooser colorChooser = new JColorChooser();
		colorChooser.setColor(new Color(this.screen.getUserTheme()));

		panel.add(colorChooser);

		int confirm = JOptionPane.showConfirmDialog(null, panel, "Theme",
													JOptionPane.OK_CANCEL_OPTION,
													JOptionPane.PLAIN_MESSAGE, null);

		if (confirm == JOptionPane.OK_OPTION) {
			Color color = colorChooser.getColor();
			this.screen.setUserTheme(color.getRed(), color.getGreen(), color.getBlue());
		}
	}
	// end: private void theme


	// ====================================================================================================
	// private void font
	//
	// Action method for font feature. Reads all the available system fonts and displays a list of them
	//
	private void font() {
		JPanel panel = new JPanel();
		// Read list of font names, which can be used to construct a Font object
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		JComboBox<String> options = new JComboBox<>(fonts);

		options.setSelectedItem(this.screen.getUserFont());
		panel.add(new JLabel("Select font: "));
		panel.add(options);

		int confirm = JOptionPane.showConfirmDialog(null, panel, "Font",
													JOptionPane.OK_CANCEL_OPTION,
													JOptionPane.PLAIN_MESSAGE, null);

		if (confirm == JOptionPane.OK_OPTION) {
			// Font name is set directly. If the font used in the Font(String) constructor is invalid,
			// there is no error and the default font is chosen instead, so this operation is safe
			String font = (String) options.getSelectedItem();
			this.screen.setUserFont(font);
		}
	}
	// end: private void font

}
// end: public class Menu
