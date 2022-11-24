// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Conf.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package web.transport;


import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class Conf
//
// Configuration information for the server and transport
//
public class Conf {

	private static boolean IS_LOADED; // Whether configuration data has already been loaded
	private static final String CONF_FILE = System.getProperty("user.home") + "/PCConfig.json";
	private static final String[] CONF_FILE_KEYS = new String[] {
		"codebase_dir",
		"database_dir",
		"transport_log_file",
		"transport_pid_file",
		"transport_keystore_file",
		"transport_keystore_password",
		"server_whitelist",
		"server_log_file",
		"apache_cert_file",
		"apache_privkey_file",
		"apache_fullchain_file",
		"apache_document_root",
		"apache_script_alias",
		"apache_log_file",
		"apache_access_file",
		"oauth_token",
		"oauth_secret"
	};


	public static String CODEBASE_DIR;
	public static String DATABASE_DIR;
	public static String TRANSPORT_LOG_FILE;
	public static String TRANSPORT_PID_FILE;
	public static String TRANSPORT_KEYSTORE_FILE;
	public static String TRANSPORT_KEYSTORE_PASSWORD;
	public static String SERVER_WHITELIST;
	public static String SERVER_LOG_FILE;
	public static String APACHE_CERT_FILE;
	public static String APACHE_PRIVKEY_FILE;
	public static String APACHE_FULLCHAIN_FILE;
	public static String APACHE_DOCUMENT_ROOT;
	public static String APACHE_SCRIPT_ALIAS;
	public static String APACHE_LOG_FILE;
	public static String APACHE_ACCESS_FILE;
	public static String OAUTH_TOKEN;
	public static String OAUTH_SECRET;


	// ----------------------------------------------------------------------------------------------------
	// Static initialization block, similar to a constructor for static classes
	//
	static {
		try {
			Conf.load();
		}
		catch (Exception e) {
			System.out.println("FATAL (Conf) Could not initialize configuration. Uncaught exception: " + e);
			System.out.println("----- STACK TRACE BEGINS -----");
			e.printStackTrace();
			System.out.println("----- STACK TRACE ENDS -----");
			System.exit(4);
		}
	}
	// end


	// ====================================================================================================
	// private static String appendPaths
	//
	// Appends two file paths to form a merged final path
	//
	// Arguments--
	//
	//  head: the head of the uri
	//
	//  tail: the tail of the uri
	//
	// Returns--
	//
	//  If the tail is a valid uri on its own, it is returned unmodified.
	//  If the head is the start of a valid absolute uri, a merged path is returned.
	//  Otherwise an error is thrown.
	//
	private static String appendPaths(String head, String tail) {
		if (tail != null && tail.startsWith("/")) {
			try {
				File file = new File(tail);
				file.getParentFile().mkdirs();
				file.createNewFile();
				return tail; // File successfully check for existance
			}
			catch (IOException e) {
				throw new IllegalArgumentException("tail for file path starts with / but is invalid: " + e);
			}
		}

		if (head == null || tail == null)
			throw new IllegalArgumentException("head or tail for file path is null: " + head + ", " + tail);
		if (head.equals("") || tail.equals(""))
			throw new IllegalArgumentException("head or tail for file path is blank: " + head + ", " + tail);
		if (!head.startsWith("/"))
			throw new IllegalArgumentException("head doesn't start with /: " + head);
		
		if (head.endsWith("/"))
			head = head.substring(0, head.length() - 1);
		
		String path = head + "/" + tail;
		try {
			File file = new File(path);
			file.getParentFile().mkdirs();
			file.createNewFile();
			return path; // File successfully checked for existance
		}
		catch (IOException e) {
			throw new IllegalArgumentException("merged path is invalid: " + e);
		}		
	}
	// end: private static String appendPaths


	// ====================================================================================================
	// public static void load
	//
	// Loads configuration information. Load will only be attempted once upon the first import of this class
	//
	@SuppressWarnings("unchecked")
	public static void load() throws Exception {
		if (Conf.IS_LOADED)
			return;
		Conf.IS_LOADED = true;
		
		Gson gson = new Gson();
		Map<String, String> confProperties;
		try {
			confProperties = gson.fromJson(Files.newBufferedReader(Paths.get(Conf.CONF_FILE)), Map.class);
		}
		catch (Exception e) {
			throw new IOException("could not load configuration file expected at \"" + Conf.CONF_FILE + "\": " + e);
		}

		for (String key : Conf.CONF_FILE_KEYS) {
			if (!confProperties.containsKey(key))
				throw new IllegalArgumentException("missing key in conf file: " + key);
		}

	    Conf.CODEBASE_DIR = confProperties.get("codebase_dir");
		Conf.DATABASE_DIR = confProperties.get("database_dir");
		Conf.TRANSPORT_LOG_FILE = appendPaths(Conf.CODEBASE_DIR, confProperties.get("transport_log_file"));
		Conf.TRANSPORT_PID_FILE = appendPaths(Conf.CODEBASE_DIR, confProperties.get("transport_pid_file"));
		Conf.TRANSPORT_KEYSTORE_FILE = appendPaths(Conf.CODEBASE_DIR, confProperties.get("transport_keystore_file"));
		Conf.TRANSPORT_KEYSTORE_PASSWORD = confProperties.get("transport_keystore_password");
		Conf.SERVER_WHITELIST = appendPaths(Conf.CODEBASE_DIR, confProperties.get("server_whitelist"));
		Conf.SERVER_LOG_FILE = appendPaths(Conf.CODEBASE_DIR, confProperties.get("server_log_file"));
		Conf.APACHE_CERT_FILE = appendPaths(Conf.CODEBASE_DIR, confProperties.get("apache_cert_file"));
		Conf.APACHE_PRIVKEY_FILE = appendPaths(Conf.CODEBASE_DIR, confProperties.get("apache_privkey_file"));
		Conf.APACHE_DOCUMENT_ROOT = confProperties.get("apache_document_root");
		Conf.APACHE_SCRIPT_ALIAS = confProperties.get("apache_script_alias");
		Conf.APACHE_LOG_FILE = confProperties.get("apache_log_file");
		Conf.APACHE_ACCESS_FILE = confProperties.get("apache_access_file");
		Conf.OAUTH_TOKEN = confProperties.get("oauth_token");
		Conf.OAUTH_SECRET = confProperties.get("oauth_secret");

		if (Conf.OAUTH_TOKEN == "" ||
			Conf.OAUTH_SECRET == "")
			throw new IllegalArgumentException("oauth token or secret is blank");
	}
	// end: public static void load
	
}
// end: public class Conf
