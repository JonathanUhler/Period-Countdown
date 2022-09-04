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
import java.util.Map;
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


	// ----------------------------------------------------------------------------------------------------
	// public UserAPI
	//
	public UserAPI(String jsonName) throws FileNotFoundException,
										   IllegalArgumentException
	{
		this(jsonName, false);
	}
	// end: public UserAPI
	

	// ----------------------------------------------------------------------------------------------------
	// public UserAPI
	//
	// Arguments--
	//
	//  jsonName: name of the user json file
	//
	//  useLocal: whether to use the local (built with the jar) User.json file or the stored file
	//
	public UserAPI(String jsonName, boolean useLocal) throws FileNotFoundException,
															 IllegalArgumentException
	{
		if (useLocal)
			this.loadLocal(jsonName);
		else
			this.loadStored(jsonName);


		// Validate the json data
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
	// private void loadStored
	//
	// Load a User.json file stored on the current machine
	//
	// Arguments--
	//
	//  jsonName: the name of the json file (without the path)
	//
	// Returns--
	//
	//  This method mutates the instance variables jsonPath and json directly
	//
	private void loadStored(String jsonName) throws FileNotFoundException,
													IllegalArgumentException
	{
		Gson gson = new Gson();
		this.jsonPath = UserJson.EXPECTED_PATH + jsonName;

		FileReader userReader = null;
		try {
			userReader = new FileReader(this.jsonPath);
		}
		catch (FileNotFoundException e) {
			this.loadLocal(jsonName);

			// Write the template User.json file to the expected path
			try {
				this.jsonPath = UserJson.EXPECTED_PATH + jsonName; // Override jsonPath from the loadLocal() call
				File userDirectory = new File(UserJson.EXPECTED_PATH);
				if (!userDirectory.exists())
					userDirectory.mkdir();
				
				FileWriter userWriter = new FileWriter(this.jsonPath);
				gson.toJson(this.json, userWriter); // this.json is set from the loadLocal() call above
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
	// end: private void loadStored


	// ====================================================================================================
	// private void loadLocal
	//
	// Load the default User.json file packaged with the jar archive
	//
	// Arguments--
	//
	//  jsonName: the name of the json file within the jar archive (without the path)
	//
	// Returns--
	//
	//  This method mutates the instance variables jsonPath and json directly
	//
	private void loadLocal(String jsonName) throws FileNotFoundException,
												   IllegalArgumentException
	{
		Gson gson = new Gson();
		this.jsonPath = UserJson.INTERNAL_PATH + jsonName;

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
	// end: private void loadLocal
	

	// ====================================================================================================
	// private void validate
	//
	// Validates the user json file
	//
	private void validate() throws IllegalArgumentException {
		if (this.json == null)
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI", "json is null, cannot validate"));

		if (this.json.settings == null ||
			this.json.periods == null)
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI", "settings or periods is null"));

		if (!this.json.settings.containsKey(UserJson.NEXT_UP) ||
			!this.json.settings.containsKey(UserJson.THEME) ||
			!this.json.settings.containsKey(UserJson.FONT) ||
			!this.json.settings.containsKey(UserJson.SCHOOL_JSON))
			throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI",
														  "settings missing key: " + this.json.settings));

		for (Map<String, String> period : this.json.periods.values()) {
			if (!period.containsKey(UserJson.NAME) ||
				!period.containsKey(UserJson.TEACHER) ||
				!period.containsKey(UserJson.ROOM))
				throw new IllegalArgumentException(Log.format(Log.ERROR, "UserAPI", "period missing key: " + period));
		}
	}
	// end: private void validate
	

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
	public Map<String, List<Map<String, String>>> attemptGetDays() {
		return this.json.days;
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
		return new ArrayList<>(this.json.periods.keySet());
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
		if (!this.json.periods.containsKey(schoolPeriodType))
			return new UserPeriod("???", "???");

		// Valid numbered period that is not Nothing or Special. Need to check if the user has named this
		// period as a free period in the User.json file
		Map<String, String> userPeriod = this.json.periods.get(schoolPeriodType);
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
		
		this.json.periods.put(key, value);
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
	// Updates the user's json file based on this.json
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
	}
	// end: private void updateJsonFile

}
// end: public class UserAPI
