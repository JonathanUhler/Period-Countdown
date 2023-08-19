package desktop;


import jnet.Log;
import school.SchoolJson;
import user.UserJson;
import user.UserPeriod;
import wizard.Wizard;
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
		this.add(settings);

		JMenuItem classInformation = new JMenuItem("Class Information");
		classInformation.addActionListener(e -> this.classInformationAction());
		settings.add(classInformation);

		JMenuItem schoolInformation = new JMenuItem("School Information");
		schoolInformation.addActionListener(e -> this.schoolInformationAction());
		settings.add(schoolInformation);

		JMenuItem nextUp = new JMenuItem("Next Up");
		nextUp.addActionListener(e -> this.nextUpAction());
		settings.add(nextUp);

		JMenuItem theme = new JMenuItem("Theme");
		theme.addActionListener(e -> this.themeAction());
		settings.add(theme);

		JMenuItem font = new JMenuItem("Font");
		font.addActionListener(e -> this.fontAction());
		settings.add(font);

		// Help menu
		JMenu help = new JMenu("Help");
		this.add(help);

		JMenuItem dataWizard = new JMenuItem("School Data Wizard");
		dataWizard.addActionListener(e -> this.dataWizardAction());
		help.add(dataWizard);

		JMenuItem submitIssue = new JMenuItem("Submit An Issue");
		submitIssue.addActionListener(e -> this.submitIssueAction());
		help.add(submitIssue);
	}


	/**
	 * Action method for the class information panel.
	 */
	private void classInformationAction() {
		List<String> periodNumbers = this.screen.getUserPeriodKeys();
		if (periodNumbers == null)
			return;
		
		// Create a structure to access the JTextFields later. The first set of keys is the period
		// numbers. The second set of keys is the field name ("Teacher", "Room", or "Name")
		Map<String, Map<String, JTextField>> periodInputFields = new HashMap<>();
		// List of JPanels that each hold the information in an entry to periodInputFields
		List<JPanel> periodInputPanels = new ArrayList<>();

		// Initialize periodInputFields
		for (String periodNumber : periodNumbers) {
			UserPeriod periodInfo = this.screen.getUserPeriod(periodNumber);
			if (periodInfo == null)
				continue;

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
				Map<String, JTextField> periodInputField = periodInputFields.get(key);
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


	/**
	 * Action method for the school json file selector.
	 */
	private void schoolInformationAction() {
		List<String> schoolJsonNames = this.screen.getAvailableSchools();
		JComboBox<String> options = new JComboBox<>(schoolJsonNames.toArray(new String[0]));
		options.setSelectedItem(this.screen.getUserSchoolFile());
		JComponent[] components = new JComponent[] {new JLabel("Select data file:"), options};
		
		int confirm = PCDesktopApp.displayDialog("School Information", components);
		if (confirm != JOptionPane.OK_OPTION)
			return;

		this.screen.setUserSchoolFile((String) options.getSelectedItem());
	}


	/**
	 * Action method for next up verbosity selector.
	 */
	private void nextUpAction() {
		JComboBox<String> options = new JComboBox<>(new String[]{UserJson.NEXT_UP_DISABLED,
																 UserJson.NEXT_UP_ONE,
																 UserJson.NEXT_UP_ALL});
		options.setSelectedItem(this.screen.getUserNextUp());
		JComponent[] components = new JComponent[] {new JLabel("Select verbosity:"), options};
		
		int confirm = PCDesktopApp.displayDialog("Next Up", components);
		if (confirm != JOptionPane.OK_OPTION)
			return;

		String verbosity = (String) options.getSelectedItem();
		this.screen.setUserNextUp(verbosity);
	}


	/**
	 * Action method for the theme color picker.
	 */
	private void themeAction() {
		JColorChooser colorChooser = new JColorChooser();
		colorChooser.setColor(new Color(this.screen.getUserTheme()));
		JComponent[] components = new JComponent[] {colorChooser};

		int confirm = PCDesktopApp.displayDialog("Theme", components);
		if (confirm != JOptionPane.OK_OPTION)
			return;

		Color color = colorChooser.getColor();
		this.screen.setUserTheme(color.getRed(), color.getGreen(), color.getBlue());
	}


	/**
	 * Action method for the font selector.
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
		if (confirm != JOptionPane.OK_OPTION)
			return;

		// Font name is set directly. If the font used in the Font(String) constructor is
		// invalid, there is no error and the default font is chosen instead, so this
		// operation is safe
		String font = (String) options.getSelectedItem();
		this.screen.setUserFont(font);
	}


	/**
	 * Opens the data generator.
	 */
	private void dataWizardAction() {
		Wizard.main(null);
	}


	/**
	 * Opens the "new issue" page on github after allowing the user to confirm they want
	 * to proceed to an external website.
	 */
	private void submitIssueAction() {
		JComponent[] components = new JComponent[] {new JLabel("Continue to github.com?")};
		int confirm = PCDesktopApp.displayDialog("Submit An Issue", components);
		if (confirm != JOptionPane.OK_OPTION)
			return;

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
		else
			PCDesktopApp.displayMessage("Browser Error", "Unabled to open link:\n" + url);
	}

}
