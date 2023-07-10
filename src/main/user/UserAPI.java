package user;


import jnet.Log;
import school.SchoolPeriod;
import school.SchoolJson;
import school.SchoolYear;
import school.SchoolAPI;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;


/**
 * Interface for accessing user-editable information from the user json file. This most includes
 * graphical settings (font, color, etc.) and basic period information (name, room, teacher).
 * Optionally, the {@code "Days"} field can be redefined in the user json file instead of the
 * school json file.
 *
 * @author Jonathan Uhler
 */
public class UserAPI {

	/** The full path to the user json file on the disk. */
	private String jsonPath;
	/** An object which holds all information in the user json file. */
	private UserJson json;
	/** An object for the information in an entry to the {@code "Schools"} field. */
	private UserJsonSchoolDef schoolDef;


	/**
	 * Constructs a new {@code UserAPI} object from the default jar artifact.
	 *
	 * @throws FileNotFoundException     if the user json file does not exist as a jar artifact.
	 * @throws IllegalArgumentException  upon any parse error.
	 */
	public UserAPI() throws FileNotFoundException {
		this.loadFromJar();
		this.validate();
	}
	

	/**
	 * Constructs a new {@code UserAPI} object from a file on the disk.
	 *
	 * @param jsonPath  the full path to the json file on the disk.
	 *
	 * @throws FileNotFoundException     if the user json file does not exist.
	 * @throws IllegalArgumentException  upon any parse error.
	 */
	public UserAPI(String jsonPath) throws FileNotFoundException {
		this.loadFromDisk(jsonPath);
		this.validate();
	}


	/**
	 * Constructs a new {@code UserAPI} object from a {@code UserJson} object.
	 *
	 * @param json  the {@code UserJson} object containing user data.
	 *
	 * @throws NullPointerException      if {@code json == null}.
	 * @throws IllegalArgumentException  upon any parse error.
	 */
	public UserAPI(UserJson json) throws IllegalArgumentException {
		if (json == null)
			throw new NullPointerException("UserJson object was null");
		this.json = json;

		// Validate the json data
		this.validate();
	}


	/**
	 * Loads a {@code User.json} file stored somewhere on the current machine from a file path.
	 * If the load is sucessful, the instance variables {@code json} and {@code jsonPath} are set.
	 *
	 * @param jsonPath  the full path to the json file on the disk.
	 *
	 * @throws FileNotFoundException     if the user json file does not exist.
	 * @throws IllegalArgumentException  upon any parse error.
	 */
	private void loadFromDisk(String jsonPath) throws FileNotFoundException {
		Gson gson = new Gson();
		this.jsonPath = jsonPath;

		FileReader userReader = null;
		try {
			userReader = new FileReader(this.jsonPath);
		}
		catch (FileNotFoundException fnfe) {
			// Load from jar to get the local version of the file that can be written to the
			// newly created file
		    this.loadFromJar();

			// Write the template User.json file to the expected path
			try {
				this.jsonPath = jsonPath; // Override jsonPath from the loadFromJar() call
				File userDirectory = new File(this.jsonPath);
				if (!userDirectory.getParentFile().exists())
					userDirectory.getParentFile().mkdirs();
				
				FileWriter userWriter = new FileWriter(this.jsonPath);
				gson.toJson(this.json, userWriter); // json is set from the loadFromJar call above
				userWriter.flush();
				userWriter.close();
			}
			catch (IOException | JsonSyntaxException e) {
				throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
															  "while attempting to create the " +
															  "resource \"" + this.jsonPath +
															  "\" an exception was thrown:\n" + e));
			}
		}


		// Read the user json file once it is certain the local file structure has been created
		try {
			userReader = new FileReader(this.jsonPath);
			this.json = gson.fromJson(userReader, UserJson.class);
		}
		catch (FileNotFoundException | JsonSyntaxException e) {
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
														  "json cannot be parsed: " + e));
		}
	}


	/**
	 * Loads the default {@code User.json} file from the list of jar artifacts. If the load is 
	 * sucessful, the instance variables {@code json} and {@code jsonPath} are set. This method
	 * does not write any files to the disk.
	 *
	 * @throws FileNotFoundException     if the user json file does not exist as a jar artifact.
	 * @throws IllegalArgumentException  upon any parse error.
	 */
	private void loadFromJar() throws FileNotFoundException {
		Gson gson = new Gson();
		this.jsonPath = UserJson.INTERNAL_PATH + UserJson.DEFAULT_FILE;

		// Get the file as a stream
		InputStream jsonStream = Thread.currentThread()
			.getContextClassLoader()
			.getResourceAsStream(this.jsonPath);
		if (jsonStream == null)
			throw new FileNotFoundException(Log.format(Log.ERROR, "UserAPI", "json resource \"" +
													   this.jsonPath + "\" was null"));

		// Read the stream with a reader and load with GSON
		InputStreamReader jsonReader = new InputStreamReader(jsonStream);

		try {
			this.json = gson.fromJson(jsonReader, UserJson.class);
		}
		catch (JsonSyntaxException e) {
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
														  "json cannot be loaded locally: " + e));
		}
	}
	

	/**
	 * Validates the elements in the user json file.
	 *
	 * @throws NullPointerException      if the instance variable {@code json} is null.
	 * @throws NullPointerException      if the settings or schools section of the file is missing.
	 * @throws IllegalArgumentException  upon any validation error.
	 */
	private void validate() throws IllegalArgumentException {
		if (this.json == null)
			throw new NullPointerException(Log.format(Log.ERROR, "UserAPI",
														  "json is null, cannot validate"));

		if (this.json.settings == null)
			throw new NullPointerException(Log.format(Log.ERROR, "UserAPI", "missing settings"));
		if (this.json.schools == null)
			throw new NullPointerException(Log.format(Log.ERROR, "UserAPI", "missing schools"));

		if (!this.json.settings.containsKey(UserJson.NEXT_UP))
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
														  "missing key " + UserJson.NEXT_UP));
		if (!this.json.settings.containsKey(UserJson.THEME))
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
														  "missing key " + UserJson.THEME));
		if (!this.json.settings.containsKey(UserJson.FONT))
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
														  "missing key " + UserJson.FONT));
		if (!this.json.settings.containsKey(UserJson.SCHOOL_JSON))
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
														  "missing key " + UserJson.SCHOOL_JSON));

		for (String schoolName : this.json.schools.keySet()) {
			UserJsonSchoolDef school = this.json.schools.get(schoolName);
			if (school.periods == null)
				throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
															  "user school info " + schoolName +
															  " does not contain Periods list"));
			
			for (Map<String, String> period : school.periods.values()) {
				if (!period.containsKey(UserJson.NAME))
					throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
																  "period missing key " +
																  UserJson.NAME));
				if (!period.containsKey(UserJson.TEACHER))
					throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
																  "period missing key " +
																  UserJson.TEACHER));
				if (!period.containsKey(UserJson.ROOM))
					throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
																  "period missing key " +
																  UserJson.ROOM));
			}
		}

		// Set the schoolDef instance variable, creating that definition in the json file if
		// it does not exist
		String schoolJsonFile = this.json.settings.get(UserJson.SCHOOL_JSON);
		if (!this.json.schools.keySet().contains(schoolJsonFile))
			this.addSchool(schoolJsonFile);
		this.schoolDef = this.json.schools.get(schoolJsonFile);
	}


	/**
	 * Adds user information for a school to the user's json file. If the school json file cannot
	 * be loaded and validated by {@code SchoolAPI}, no change is made.
	 *
	 * @param schoolJsonFile  the name of the school json file.
	 *
	 * @see SchoolAPI
	 */
	private void addSchool(String schoolJsonFile) {
		SchoolAPI schoolAPI;
		try {
			schoolAPI = new SchoolAPI(schoolJsonFile);
		}
		catch(FileNotFoundException e) {
			return;
		}
			
		int firstPeriod = schoolAPI.getFirstPeriod();
		int lastPeriod = schoolAPI.getLastPeriod();

		// Create the new json structure
		Map<String, Map<String, String>> schoolPeriods = new HashMap<>();
		for (int period = firstPeriod; period <= lastPeriod; period++) {
			Map<String, String> periodInfo = new HashMap<>();
			periodInfo.put(UserJson.TEACHER, "");
			periodInfo.put(UserJson.ROOM, "");
			periodInfo.put(UserJson.NAME, "");
			schoolPeriods.put(period + "", periodInfo);
		}

		UserJsonSchoolDef schoolInfo = new UserJsonSchoolDef();
		schoolInfo.periods = schoolPeriods;
		this.json.schools.put(schoolJsonFile, schoolInfo);
		this.updateJsonFile();
	}
	

	/**
	 * Attempts to get the definition for the {@code "Days"} structure present in the school json
	 * file from the user file instead. This is useful for institutions where a generic school file
	 * will not work (like universities where each student has a different class schdule). The
	 * structure returned is the raw structure of the {@code "Days"} definition.
	 *
	 * @return the {@code "Days"} field of the user json file, if one exists. Note that no
	 *         validation is done by this method. These checks are expected to be performed by
	 *         the caller.
	 */
	public Map<String, List<Map<String, String>>> attemptGetDays() {
		return this.schoolDef.days;
	}


	/**
	 * Returns the name of the current school json file.
	 *
	 * @return the name of the current school json file.
	 */
	public String getSchoolFile() {
		return this.json.settings.get(UserJson.SCHOOL_JSON);
	}


	/**
	 * Returns a list of known school json file names.
	 *
	 * @return a list of known school json file names.
	 */
	public List<String> getAvailableSchools() {
		// Get and load the path to the running jar file, independent of the working directory
		String jarPath = UserAPI.class.getProtectionDomain()
			.getCodeSource()
			.getLocation()
			.getPath();
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(jarPath);
		}
		catch (IOException e) {
			Log.stdlog(Log.ERROR, "UserAPI", "cannot load jarfile resources: " + e);
			return new ArrayList<>();
		}
		
		List<String> schoolJsonNames = new ArrayList<>();
		// Read all of the resource entries in the jar file
		Enumeration<JarEntry> jarResources = jarFile.entries();
		while (jarResources.hasMoreElements()) {
			JarEntry jarResource = jarResources.nextElement();
			String resourceName = jarResource.getName();
			// If the resource is not the User.json file and otherwise matches the json file regex,
			// then add just the file name (SchoolJson.EXPECTED_PATH == "/assets/json", which is
			// removed to just get the name of the file).
			if (!resourceName.endsWith(UserJson.DEFAULT_FILE) &&
				resourceName.matches(UserJson.FILE_NAME_REGEX))
			{
				if (resourceName.startsWith(SchoolJson.EXPECTED_PATH))
					schoolJsonNames.add(resourceName.substring(SchoolJson.EXPECTED_PATH.length()));
				else
					schoolJsonNames.add(resourceName);
			}
		}

		return schoolJsonNames;
	}
	

	/**
	 * Returns a list of period names (which should be parsable as integers) in the user json file.
	 *
	 * @return a list of period names in the user json file. Note that this list may not actually
	 *         contain strings that are parsable as integers.
	 */
	public List<String> getPeriodKeys() {
		return new ArrayList<>(this.schoolDef.periods.keySet());
	}


	/**
	 * Returns an {@code UserPeriod} object for the same period as the given {@code SchoolPeriod}.
	 * The following {@code UserPeriod} will be returned:
	 * <ul>
	 * <li> If {@code schoolPeriod == null}, then "Summer | Free" is returned.
	 * <li> If {@code schoolPeriod.isFree()}, then the school period's name is returned.
	 * <li> If the type of {@code schoolPeriod} is undefined, then "??? | ???" is returned.
	 * <li> Else, the user-defined name is returned.
	 * </ul>
	 *
	 * @param schoolPeriod  the school period to get the corresponding user period for.
	 *
	 * @return an {@code UserPeriod} object for the same period as the given {@code SchoolPeriod}.
	 *
	 */
	public UserPeriod getPeriod(SchoolPeriod schoolPeriod) {
		// Null period means nothing could be found for the next year, so the current time is
		// probably out of range for the school json file
		if (schoolPeriod == null)
			return new UserPeriod("Summer", "Free");

		String schoolPeriodType = schoolPeriod.getType(); // Nothing, Special, or a number

		// School period is free, this is not for free classes, but for periods without events
		// (e.g. lunch, tutorial, passing period)
		if (schoolPeriod.isFree())
			return new UserPeriod(schoolPeriod.getName(), "Free");

		// Not a free period (not Speical or Nothing, so should be a number) but the number does
		// not match any of the declared periods in the user file. Resort to a simple error bypass
		// with "???"
		if (!this.schoolDef.periods.containsKey(schoolPeriodType))
			return new UserPeriod("???", "???");

		// Valid numbered period that is not Nothing or Special. Need to check if the user has
		// named this period as a free period in the User.json file
		Map<String, String> userPeriod = this.schoolDef.periods.get(schoolPeriodType);
		String userPeriodTeacher = userPeriod.get(UserJson.TEACHER);
		String userPeriodRoom = userPeriod.get(UserJson.ROOM);
		String userPeriodName = userPeriod.get(UserJson.NAME);
		if (userPeriodName.toLowerCase().equals("free") ||
			userPeriodName.toLowerCase().equals("none") ||
			userPeriodName.toLowerCase().equals("n/a"))
			return new UserPeriod(schoolPeriod.getName(), "Free");
		return new UserPeriod(userPeriodName,
							  schoolPeriod.getName(),
							  userPeriodTeacher,
							  userPeriodRoom);
	}


	/**
	 * Returns the verbosity level of the "next up" feature.
	 *
	 * @return the verbosity level of the "next up" feature.
	 */
	public String getNextUp() {
		return this.json.settings.get(UserJson.NEXT_UP);
	}


	/**
	 * Returns the theme color chosen by the user, as a 3-byte integer (for rgb).
	 *
	 * @return the theme color chosen by the user, as a 3-byte integer (for rgb).
	 */
	public int getTheme() {
		String rgbStr = this.json.settings.get(UserJson.THEME);
		try {
			return Integer.parseInt(rgbStr);
		}
		catch (NumberFormatException e) {
			return 0xffffff; // 16777215 dec, 111111111111111111111111 bin, color for white
		}
	}


	/**
	 * Returns the name of the font chosen by the user.
	 *
	 * @return the name of the font chosen by the user.
	 */
	public String getFont() {
		return this.json.settings.get(UserJson.FONT);
	}


	/**
	 * Updates the school file being viewed by the user. No change is made if the file is not
	 * a valid json file, or is null.
	 *
	 * @param file  the name of the school file.
	 */
	public void setSchoolFile(String file) {
		if (!file.matches(UserJson.FILE_NAME_REGEX) || file == null)
			return;
		this.json.settings.put(UserJson.SCHOOL_JSON, file);
		this.updateJsonFile();
	}


	/**
	 * Sets the information for a user-defined class period. If any argument is null, or the
	 * {@code value} map does not contain all required keys, no change is made.
	 *
	 * @param key    the period number being set.
	 * @param value  a map with the period teacher, room, and name.
	 */
	public void setPeriod(String key, Map<String, String> value) {
		if (key == null ||
			value == null ||
			!value.containsKey(UserJson.TEACHER) ||
			!value.containsKey(UserJson.ROOM) ||
			!value.containsKey(UserJson.NAME))
			return;
		
		this.schoolDef.periods.put(key, value);
		this.updateJsonFile();
	}


	/**
	 * Sets the verbosity level for the "next up" feature.
	 *
	 * @param verbosity  the verbosity level for the "next up" feature.
	 */
	public void setNextUp(String verbosity) {
		this.json.settings.put(UserJson.NEXT_UP, verbosity);
		this.updateJsonFile();
	}


	/**
	 * Sets the theme color from an rgb value. If any of the color channels is outside the
	 * interval {@code [0, 255]}, the color being set is forced to white.
	 *
	 * @param r  the red channel.
	 * @param g  the green channel.
	 * @param b  the blue channel.
	 */
	public void setTheme(int r, int g, int b) {
		if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
			r = 255;
			g = 255;
			b = 255;
		}

		int rgb = (((r << 8) | g) << 8) | b;

		this.json.settings.put(UserJson.THEME, Integer.toString(rgb));
		this.updateJsonFile();
	}


	/**
	 * Sets the name of the font used by the application.
	 *
	 * @param font  the name of the font.
	 */
	public void setFont(String font) {
		this.json.settings.put(UserJson.FONT, font);
		this.updateJsonFile();
	}


	/**
	 * Updates the user's json file based on the existing contents of {@code this.json}. This
	 * method will not update the json file if this {@code UserAPI} was constructed with the
	 * {@code loadFromJar} flag set.
	 */
	private void updateJsonFile() {
		FileWriter writer;
		try {
			writer = new FileWriter(this.jsonPath);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(this.json, writer);
			writer.flush();
			writer.close();
		}
		catch (IOException e) {
			return;
		}

		this.validate();
	}

}
