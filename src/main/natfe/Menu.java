package natfe;


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
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JColorChooser;


public class Menu extends JMenuBar {

	private Screen screen;
	

	public Menu(Screen screen) {
		this.screen = screen;
		
		JMenu settings = new JMenu("Settings");
		this.add(settings);

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
	}


	private void classInformation() {
		ArrayList<String> periodNumbers = this.screen.getUserPeriodKeys();
		if (periodNumbers == null)
			return;
		HashMap<String, HashMap<String, JTextField>> periodInputFields = new HashMap<>();
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

			nameTextField.setColumns(8);
			teacherTextField.setColumns(8);
			roomTextField.setColumns(8);
			
			periodInputField.put(UserJson.NAME, nameTextField);
			periodInputField.put(UserJson.TEACHER, teacherTextField);
			periodInputField.put(UserJson.ROOM, roomTextField);

			periodInputFields.put(periodNumber, periodInputField);

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

			
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(550, 50 * periodNumbers.size()));
		for (JPanel periodInputPanel : periodInputPanels) {
			panel.add(periodInputPanel);
		}

		int confirm = JOptionPane.showConfirmDialog(null, panel, "Class Information",
													JOptionPane.OK_CANCEL_OPTION,
													JOptionPane.PLAIN_MESSAGE, null);
		if (confirm == JOptionPane.OK_OPTION) {
			for (String key : periodInputFields.keySet()) {
				HashMap<String, JTextField> periodInputField = periodInputFields.get(key);
				String name = periodInputField.get(UserJson.NAME).getText();
				String teacher = periodInputField.get(UserJson.TEACHER).getText();
				String room = periodInputField.get(UserJson.ROOM).getText();

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


	private void schoolInformation() {
		String jarPath = PeriodCountdown.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(jarPath);
		}
		catch (IOException e) {
			Log.gfxmsg("Internal Error", "Cannot load jarfile resources: " + e);
			return;
		}
		
		ArrayList<String> schoolJsonNames = new ArrayList<>();
		Enumeration<JarEntry> jarResources = jarFile.entries();
		while (jarResources.hasMoreElements()) {
			JarEntry jarResource = jarResources.nextElement();
			String resourceName = jarResource.getName();
			if (!resourceName.endsWith(UserJson.DEFAULT_FILE) && resourceName.matches(UserJson.FILE_NAME_REGEX)) {
				if (resourceName.startsWith(SchoolJson.EXPECTED_PATH))
					schoolJsonNames.add(resourceName.substring(SchoolJson.EXPECTED_PATH.length()));
				else
					schoolJsonNames.add(resourceName);
			}
		}

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


	private void nextUp() {
		JPanel panel = new JPanel();
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

}
