// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// UserAPI.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package user;


import util.Log;
import school.SchoolPeriod;
import school.SchoolJson;
import school.SchoolYear;
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


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class UserAPI
//
// API interface to access the information in the user json file
//
public class UserAPI {
	
	private String jsonPath;
	private UserJson json;
	private UserJsonSchoolDef schoolDef;


	// ----------------------------------------------------------------------------------------------------
	// public UserAPI
	//
	// Loads the default User.json file from the jar
	//
	public UserAPI() throws FileNotFoundException,
							IllegalArgumentException
	{
		this.loadFromJar();
		this.validate();
	}
	// end: public UserAPI
	

	// ----------------------------------------------------------------------------------------------------
	// public UserAPI
	//
	// Loads an user json file from a specified local path on the disk
	//
	// Arguments--
	//
	//  jsonPath: absolute path of the user json file
	//
	public UserAPI(String jsonPath) throws FileNotFoundException,
										   IllegalArgumentException
	{
		this.loadFromDisk(jsonPath);
		this.validate();
	}
	// end: public UserAPI


	// ----------------------------------------------------------------------------------------------------
	// public UserAPI
	//
	// Arguments--
	//
	//  json: a pre-prepared UserJson object
	//
	public UserAPI(UserJson json) throws IllegalArgumentException {		
		this.json = json;

		// Validate the json data
		this.validate();
	}
	// end: public UserAPI


	// ====================================================================================================
	// private void loadFromDisk
	//
	// Load a User.json file stored on the current machine
	//
	// Arguments--
	//
	//  jsonPath: the absolute path of the json file on the local disk
	//
	// Returns--
	//
	//  This method mutates the instance variables jsonPath and json directly
	//
	private void loadFromDisk(String jsonPath) throws FileNotFoundException,
													IllegalArgumentException
	{
		Gson gson = new Gson();
		this.jsonPath = jsonPath;

		FileReader userReader = null;
		try {
			userReader = new FileReader(this.jsonPath);
		}
		catch (FileNotFoundException e) {
			// Load from jar to get the local version of the file that can be written to the newly created file
		    this.loadFromJar();

			// Write the template User.json file to the expected path
			try {
				this.jsonPath = jsonPath; // Override jsonPath from the loadFromJar() call
				File userDirectory = new File(this.jsonPath);
				if (!userDirectory.getParentFile().exists())
					userDirectory.getParentFile().mkdirs();
				
				FileWriter userWriter = new FileWriter(this.jsonPath);
				gson.toJson(this.json, userWriter); // this.json is set from the loadFromJar() call above
				userWriter.flush();
				userWriter.close();
			}
			catch (IOException | JsonSyntaxException e2) {
				throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
															  "while attempting to create the resource \"" +
															  this.jsonPath + "\" an exception was thrown:\n" + e2));
			}
		}


		// Read the user json file once it is certain the local file structure has been created
		try {
			userReader = new FileReader(this.jsonPath);
			this.json = gson.fromJson(userReader, UserJson.class);
		}
		catch (FileNotFoundException | JsonSyntaxException e) {
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI", "json cannot be parsed: " + e));
		}
	}
	// end: private void loadFromDisk


	// ====================================================================================================
	// private void loadFromJar
	//
	// Load the default User.json file packaged with the jar archive
	//
	// Returns--
	//
	//  This method mutates the instance variables jsonPath and json directly
	//
	private void loadFromJar() throws FileNotFoundException,
												   IllegalArgumentException
	{
		Gson gson = new Gson();
		this.jsonPath = UserJson.INTERNAL_PATH + UserJson.DEFAULT_FILE;

		// Get the file as a stream
		InputStream jsonStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(this.jsonPath);
		if (jsonStream == null)
			throw new FileNotFoundException(Log.format(Log.ERROR, "UserAPI",
													   "json resource \"" + this.jsonPath + "\" was null"));

		// Read the stream with a reader and load with GSON
		InputStreamReader jsonReader = new InputStreamReader(jsonStream);

		try {
			this.json = gson.fromJson(jsonReader, UserJson.class);
		}
		catch (JsonSyntaxException e) {
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI", "json cannot be loaded locally: " + e));
		}
	}
	// end: private void loadFromJar
	

	// ====================================================================================================
	// private void validate
	//
	// Validates the user json file
	//
	private void validate() throws IllegalArgumentException {
		if (this.json == null)
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI", "json is null, cannot validate"));

		if (this.json.settings == null ||
			this.json.schools == null)
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI", "settings or schools is null"));

		if (!this.json.settings.containsKey(UserJson.NEXT_UP) ||
			!this.json.settings.containsKey(UserJson.THEME) ||
			!this.json.settings.containsKey(UserJson.FONT) ||
			!this.json.settings.containsKey(UserJson.SCHOOL_JSON))
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
														  "settings missing key: " + this.json.settings));

		for (String schoolName : this.json.schools.keySet()) {
			UserJsonSchoolDef school = this.json.schools.get(schoolName);
			if (school.periods == null)
				throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
															  "user school info does not contain Periods list: " +
															  schoolName));
			
			for (Map<String, String> period : school.periods.values()) {
				if (!period.containsKey(UserJson.NAME) ||
					!period.containsKey(UserJson.TEACHER) ||
					!period.containsKey(UserJson.ROOM))
					throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
																  "period missing key: " + period));
			}
		}

		// Set the schoolDef instance variable, creating that definition in the json file if it does not exist
		String schoolJsonFile = this.json.settings.get(UserJson.SCHOOL_JSON);
		if (!this.json.schools.keySet().contains(schoolJsonFile))
			this.addSchool(schoolJsonFile);
		this.schoolDef = this.json.schools.get(schoolJsonFile);
	}
	// end: private void validate


	// ====================================================================================================
	// private void addSchool
	//
	// Adds the user definition for a school (periods and optionally an overriden "Days" field) to
	// the User.json file
	//
	// Arguments--
	//
	//  schoolJsonFile: the json file name for this school, used as a key in User.json
	//
	private void addSchool(String schoolJsonFile) {
		// If the data structure for this school has not been created in User.json, attempt to create
		// a SchoolAPI for that school to get the first and last period, then create the structure
		SchoolJson periodRange = null;


		// Read the referenced school json file as a SchoolJson object. We don't need a full SchoolYear or API
		InputStream schoolStream = Thread.currentThread()
			.getContextClassLoader()
			.getResourceAsStream(SchoolJson.EXPECTED_PATH + schoolJsonFile);
		if (schoolStream == null)
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
														  "json resource \"" + this.jsonPath + "\" is null"));

		InputStreamReader schoolReader = new InputStreamReader(schoolStream);

		// Load json file with GSON as a SchoolJson object
		Gson gson = new Gson();
		try {
			periodRange = gson.fromJson(schoolReader, SchoolJson.class);
		}
		catch (JsonSyntaxException e) {
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI", "json cannot be parsed: " + e));
		}

		// Get the first and last periods defined by this school file, used to create the correct-length structure
		int firstPeriod = 0;
		int lastPeriod = 0;
		try {
			firstPeriod = Integer.parseInt(periodRange.info.get(SchoolJson.FIRST_PERIOD));
			lastPeriod = Integer.parseInt(periodRange.info.get(SchoolJson.LAST_PERIOD));
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
														  "first or last period is not an integer"));
		}
		if (firstPeriod > lastPeriod)
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI", "first period > last period"));


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
	// end: private void addSchool
	

	// ====================================================================================================
	// public Map<String, List<Map<String, String>>> attemptGetDays
	//
	// Attempts to get the definition for the "Days" structure present in the school json file from the
	// user file instead. This is useful for institutions where a generic school file will not work
	// (like universities where each student has different "bell" schedules). The structure returned
	// is the raw structure of the "Days" definition
	//
	// Returns--
	//
	//  (See function description above)
	//
	public Map<String, List<Map<String, String>>> attemptGetDays() {
		return this.schoolDef.days;
	}
	// end: public Map<String, List<Map<String, String>>> attemptGetDays


	// ====================================================================================================
	// GET methods
	public String getSchoolFile() {
		return this.json.settings.get(UserJson.SCHOOL_JSON);
	}


	public ArrayList<String> getAvailableSchools() {
		// Get and load the path to the running jar file, independent of the working directory
		String jarPath = UserAPI.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(jarPath);
		}
		catch (IOException e) {
			Log.stdlog(Log.ERROR, "UserAPI", "cannot load jarfile resources: " + e);
			return new ArrayList<>();
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

		return schoolJsonNames;
	}
	

	public ArrayList<String> getPeriodKeys() {
		return new ArrayList<>(this.schoolDef.periods.keySet());
	}


	// Returns an UserPeriod object based on a SchoolPeriod object. Uses the type of the SchoolPeriod to index
	// the data in the user json file
	public UserPeriod getPeriod(SchoolPeriod schoolPeriod) {
		// Null period means nothing could be found for the next year, so the current time is probably
		// out of range for the school json file
		if (schoolPeriod == null)
			return new UserPeriod("Summer", "Free");

		String schoolPeriodType = schoolPeriod.getType(); // Nothing, Special, or a number

		// School period is free, this is not for free classes, but for periods without events (e.g. lunch,
		// tutorial, passing period)
		if (schoolPeriod.isFree())
			return new UserPeriod(schoolPeriod.getName(), "Free"); // Name of SchoolPeriod is generic like "Passing"

		// Not a free period (not Speical or Nothing, so should be a number) but the number does not match any
		// of the declared periods in the user file. Resort to a simple error bypass with "???"
		if (!this.schoolDef.periods.containsKey(schoolPeriodType))
			return new UserPeriod("???", "???");

		// Valid numbered period that is not Nothing or Special. Need to check if the user has named this
		// period as a free period in the User.json file
		Map<String, String> userPeriod = this.schoolDef.periods.get(schoolPeriodType);
		String userPeriodTeacher = userPeriod.get(UserJson.TEACHER);
		String userPeriodRoom = userPeriod.get(UserJson.ROOM);
		String userPeriodName = userPeriod.get(UserJson.NAME);
		if (userPeriodName.toLowerCase().equals("free") ||
			userPeriodName.toLowerCase().equals("none") ||
			userPeriodName.toLowerCase().equals("n/a"))
			return new UserPeriod(schoolPeriod.getName(), "Free");
		return new UserPeriod(userPeriodName, schoolPeriod.getName(), userPeriodTeacher, userPeriodRoom);
	}

	public String getNextUp() {
		return this.json.settings.get(UserJson.NEXT_UP);
	}

	public int getTheme() {
		String rgbStr = this.json.settings.get(UserJson.THEME);
		try {
			return Integer.parseInt(rgbStr);
		}
		catch (NumberFormatException e) {
			return 0xffffff; // 16777215 dec, 111111111111111111111111 bin, color for white
		}
	}

	public String getFont() {
		return this.json.settings.get(UserJson.FONT);
	}
	// end: GET methods


	// ====================================================================================================
	// SET methods
	//
	// Each of these methods calls the updateJsonFile() method to store the local changes to this.json
	// into the user's file
	//
	public void setSchoolFile(String file) {
		if (!file.matches(UserJson.FILE_NAME_REGEX) || file == null)
			return;
		this.json.settings.put(UserJson.SCHOOL_JSON, file);
		this.updateJsonFile();
	}

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

	public void setNextUp(String verbosity) {
		this.json.settings.put(UserJson.NEXT_UP, verbosity);
		this.updateJsonFile();
	}

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

	public void setFont(String font) {
		this.json.settings.put(UserJson.FONT, font);
		this.updateJsonFile();
	}
	// end: SET methods

	// ====================================================================================================
	// private void updateJsonFile
	//
	// Updates the user's json file based on this.json. Note, this method will not update the json file
	// if this UserAPI object was constructed with the loadFromJar flag set. An IOException will be
	// caught by this method, and the method will return without mutating the jar's User.json file
	//
	private void updateJsonFile() {
		try {
			FileWriter writer = new FileWriter(this.jsonPath);
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
	// end: private void updateJsonFile

}
// end: public class UserAPI
