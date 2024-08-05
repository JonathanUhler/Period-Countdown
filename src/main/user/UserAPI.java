package user;


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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import jnet.Log;
import time.UTCTime;
import os.OSPath;
import school.SchoolPeriod;
import school.SchoolJson;
import school.SchoolYear;
import school.SchoolAPI;


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
    private Path path;
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
     * @param path  a {@code Path} object that points to the user json file on the disk.
     *
     * @throws NullPointerException      if {@code path == null}.
     * @throws FileNotFoundException     if the user json file does not exist.
     * @throws IllegalArgumentException  upon any parse error.
     * @throws IllegalArgumentException  if {@code OSPath.isInJar(path)} is {@code true}.
     */
    public UserAPI(Path path) throws FileNotFoundException {
        if (path == null)
            throw new NullPointerException("path was null");
        if (OSPath.isInJar(path))
            throw new IllegalArgumentException("expected disk path, found pointer to jar: " + path);
	
        this.loadFromDisk(path);
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
     * If the load is sucessful, the instance variables {@code json} and {@code path} are set.
     *
     * @param path  the path of the user json file on the disk.
     *
     * @throws FileNotFoundException     if the user json file does not exist.
     * @throws IllegalArgumentException  upon any parse error.
     */
    private void loadFromDisk(Path path) throws FileNotFoundException {
        Gson gson = new Gson();
        this.path = path;
        
        FileReader userReader = null;
        try {
            userReader = new FileReader(this.path.toString());
        }
        catch (FileNotFoundException fnfe) {
            // Load from jar to get the local version of the file that can be written to the
            // newly created file
            this.loadFromJar();
            
            // Write the template User.json file to the expected path
            try {
                this.path = path; // Override path from the loadFromJar() call
                File userDirectory = new File(this.path.toString());
                if (!userDirectory.getParentFile().exists())
                    userDirectory.getParentFile().mkdirs();
		
                FileWriter userWriter = new FileWriter(this.path.toString());
                gson.toJson(this.json, userWriter); // json is set from the loadFromJar call above
                userWriter.flush();
                userWriter.close();
            }
            catch (IOException | JsonSyntaxException e) {
                throw new IllegalArgumentException("while attempting to create the resource '" +
                                                   this.path + "' an exception was thrown:\n" + e);
            }
        }
        
        
        // Read the user json file once it is certain the local file structure has been created
        try {
            userReader = new FileReader(this.path.toString());
            this.json = gson.fromJson(userReader, UserJson.class);
        }
        catch (FileNotFoundException | JsonSyntaxException e) {
            throw new IllegalArgumentException("json cannot be parsed: " + e);
        }
    }
    
    
    /**
     * Loads the default {@code User.json} file from the list of jar artifacts. If the load is 
     * sucessful, the instance variables {@code json} and {@code path} are set. This method
     * does not write any files to the disk.
     *
     * @throws FileNotFoundException     if the user json file does not exist as a jar artifact.
     * @throws IllegalArgumentException  upon any parse error.
     */
    private void loadFromJar() throws FileNotFoundException {
        Gson gson = new Gson();
        this.path = OSPath.join(OSPath.getUserJsonJarPath(), OSPath.getUserJsonFile());
        
        // Get the file as a stream
        InputStream jsonStream = Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream(this.path.toString());
        if (jsonStream == null)
            throw new FileNotFoundException("json resource \"" + this.path + "\" was null");
        
        // Read the stream with a reader and load with GSON
        InputStreamReader jsonReader = new InputStreamReader(jsonStream);
        
        try {
            this.json = gson.fromJson(jsonReader, UserJson.class);
        }
        catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("json cannot be loaded locally: " + e);
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
            throw new NullPointerException("json is null, cannot validate");
        
        if (this.json.settings == null)
            throw new NullPointerException("missing settings in school json file");
        if (this.json.schools == null)
            throw new NullPointerException("missing schools");
        
        if (!this.json.settings.containsKey(UserJson.NEXT_UP))
            throw new IllegalArgumentException("missing key " + UserJson.NEXT_UP);
        if (!this.json.settings.containsKey(UserJson.THEME))
            throw new IllegalArgumentException("missing key " + UserJson.THEME);
        if (!this.json.settings.containsKey(UserJson.FONT))
            throw new IllegalArgumentException("missing key " + UserJson.FONT);
        if (!this.json.settings.containsKey(UserJson.SCHOOL_JSON))
            throw new IllegalArgumentException("missing key " + UserJson.SCHOOL_JSON);
        
        for (String schoolName : this.json.schools.keySet()) {
            UserJsonSchoolDef school = this.json.schools.get(schoolName);
            if (school.periods == null)
                throw new IllegalArgumentException("user school info " + schoolName +
                                                   " does not contain Periods list");
            
            for (Map<String, String> period : school.periods.values()) {
                if (!period.containsKey(UserJson.NAME))
                    throw new IllegalArgumentException("period missing key " + UserJson.NAME);
                if (!period.containsKey(UserJson.TEACHER))
                    throw new IllegalArgumentException("period missing key " + UserJson.TEACHER);
                if (!period.containsKey(UserJson.ROOM))
                    throw new IllegalArgumentException("period missing key " + UserJson.ROOM);
            }
        }
        
        // Set the schoolDef instance variable, creating that definition in the json file if
        // it does not exist
        Path schoolPath = Paths.get(this.json.settings.get(UserJson.SCHOOL_JSON));
        if (!this.json.schools.keySet().contains(schoolPath.getFileName().toString()))
            this.addSchool(schoolPath);
        this.schoolDef = this.json.schools.get(schoolPath.getFileName().toString());
    }
    
    
    /**
     * Adds user information for a school to the user's json file. If the school json file cannot
     * be loaded and validated by {@code SchoolAPI}, no change is made.
     *
     * @param schoolPath  the path to the school JSON file.
     *
     * @see SchoolAPI
     */
    private void addSchool(Path schoolPath) {
        SchoolAPI schoolAPI;
        try {
            schoolAPI = new SchoolAPI(schoolPath);
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
        this.json.schools.put(schoolPath.getFileName().toString(), schoolInfo);
        this.updateJsonFile();
    }
    
    
    /**
     * Returns the path to the current school json file.
     *
     * @return the path to the current school json file.
     */
    public Path getSchoolFile() {
        return Paths.get(this.json.settings.get(UserJson.SCHOOL_JSON));
    }
    
    
    /**
     * Returns a list of known school json file names.
     *
     * @return a list of known school json file names.
     */
    public List<String> getAvailableSchools() {
        List<String> schoolJsonNames = new ArrayList<>();
        
        // ** Detect any school files that are part of the application itself (native support) **
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
            Log.error("cannot load jarfile resources: " + e);
            return new ArrayList<>();
        }
	
        // Read all of the resource entries in the jar file
        Enumeration<JarEntry> jarResources = jarFile.entries();
        while (jarResources.hasMoreElements()) {
            JarEntry jarResource = jarResources.nextElement();
            String resourceName = jarResource.getName();
            Path resourcePath = Paths.get(resourceName);
            if (OSPath.isSchoolInJar(resourcePath) && OSPath.isJsonFile(resourcePath))
                schoolJsonNames.add(resourcePath.toString());
        }
        
        // ** Detect any school files on the disk (local) **
        File schoolFolder = new File(OSPath.getSchoolJsonDiskPath().toString());
        File[] schoolFiles = schoolFolder.listFiles();
        if (schoolFiles != null) {
            for (File schoolFile : schoolFiles) {
                Path schoolPath = schoolFile.toPath();
                if (OSPath.isJsonFile(schoolPath))
                    schoolJsonNames.add(schoolPath.toString());
            }
        }
        
        // ** Return list of school names **
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
     * Returns the list of next class information for this user's information.
     *
     * @param schoolAPI  school information to use.
     * @param timezone   the unix TZ identifier for the school.
     *
     * @return the list of next class information.
     */
    public List<String> getNextUpList(SchoolAPI schoolAPI, String timezone) {
        return this.getNextUpList(schoolAPI, timezone, UTCTime.now());
    }
    
    
    /**
     * Returns the list of next class information for this user's information.
     *
     * @param schoolAPI  school information to use.
     * @param timezone   the unix TZ identifier for the school.
     * @param now        the time to get the next up information for.
     *
     * @return the list of next class information.
     */
    public List<String> getNextUpList(SchoolAPI schoolAPI, String timezone, UTCTime now) {
        if (schoolAPI == null)
            return new ArrayList<>();
	
        String nextUp = this.getNextUp();
        ArrayList<String> nextUpList = new ArrayList<>();
	
        SchoolPeriod nextPeriod = schoolAPI.getNextPeriodToday(now);
        while (nextPeriod != null) {
            if (nextUp.equals(UserJson.NEXT_UP_DISABLED))
                break;
            
            // Get the class (with user data like teacher and room) based on the generic period,
            // if that generic period can have a class
            UserPeriod nextClass = null;
            if (nextPeriod.isCounted())
                nextClass = this.getPeriod(nextPeriod);
            
            // Format the string
            // Default periodString is "<period/class name> | <start>-<end>"
            UTCTime periodStart = nextPeriod.getStart();
            UTCTime periodEnd = nextPeriod.getEnd();
            
            // Add 1 to the end time ms to make the end time the same as the start time of
            // the next period
            periodEnd = periodEnd.plus(1, UTCTime.MILLISECONDS);
            
            try {
                periodStart = periodStart.to(timezone);
                periodEnd = periodEnd.to(timezone);
            }
            catch (IllegalArgumentException e) {
                // Ignore, just continue with UTC time
            }

            String periodString;
            if (nextClass == null)
                periodString = nextPeriod.getName();
            else {
                periodString = nextClass.getName() + " | " +
                    String.format("%02d", periodStart.get(UTCTime.HOUR)) + ":" +
                    String.format("%02d", periodStart.get(UTCTime.MINUTE)) + "-" +
                    String.format("%02d", periodEnd.get(UTCTime.HOUR)) + ":" +
                    String.format("%02d", periodEnd.get(UTCTime.MINUTE));
            }
                
            // If the school period has a class during it add " | <teacher>, <room>"
            if (!nextPeriod.isFree() && nextClass != null && !nextClass.isFree()) {
                String teacher = nextClass.getTeacher();
                String room = nextClass.getRoom();
                
                // Format based on what data is available (either one, the other, both, or neither)
                if (!teacher.equals("") && room.equals(""))
                    periodString += " | " + teacher;
                else if (!room.equals("") && teacher.equals(""))
                    periodString += " | " + room;
                else if (!room.equals("") && !teacher.equals(""))
                    periodString += " | " + teacher + ", " + room;
            }
            // If the period has something during it (a period, lunch, brunch, etc.) add it to
            // the list
            if (nextPeriod.isCounted())
                nextUpList.add(periodString);
            
            // Get next period
            nextPeriod = schoolAPI.getNextPeriodToday(periodStart);
            
            // If only the next period should be shown and that period has been found, skip
            // the rest of the search
            if (nextUp.equals(UserJson.NEXT_UP_ONE) && nextUpList.size() == 1)
                break;
        }
        
        return nextUpList;
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
     * @param path  the path to the school file.
     */
    public void setSchoolFile(Path path) {
        if (path == null || !OSPath.isJsonFile(path))
            return;
        this.json.settings.put(UserJson.SCHOOL_JSON, path.toString());
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
            writer = new FileWriter(this.path.toString());
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
