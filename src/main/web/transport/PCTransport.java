// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// PCTransport.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package web.transport;


import util.Log;
import javacli.annotations.Option;
import javacli.annotations.Version;
import javacli.OptionParser;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.Files;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class PCTransport
//
// Main class for the web version of Period Countdown. Acts as a transport between the APIs to access
// json data on the Java side and the Python web server
//
public class PCTransport {	

	@Option(name = "transport-ip",
			abbreviation = 'i',
			nargs = 1,
			type = String.class,
			defaultValue = "127.0.0.1",
			showDefault = true,
			help = "set transport IP addr")
	public static String transport_ip;
	
	@Option(name = "transport-port",
			nargs = 1,
			abbreviation = 'P',
			type = Integer.class,
			defaultValue = "9000",
			showDefault = true,
			help = "set transport port")
	public static Integer transport_port;
	

	// ====================================================================================================
	// public static void main
	//
	public static void main(String[] args) {
		// Parse command line options
		try {
			OptionParser optionParser = new OptionParser(PCTransport.class);
			optionParser.parse(args);
		}
		catch (Exception e) {
			Log.stdout(Log.FATAL, "PCTransport", "cannot parse command line arguments");
		}

		// Set log file as a JVM property, to allow for the log class to also be used by the desktop app
		if (Conf.TRANSPORT_LOG_FILE != null &&
			!Conf.TRANSPORT_LOG_FILE.equals(""))
			System.setProperty(Log.LOG_FILE_SYS_PROPERTY, Conf.TRANSPORT_LOG_FILE);
		
		// Check for the keystore file and keystore password. If these aren't specified (e.g. they're
		// null) then the transport cannot start securely
		if (Conf.TRANSPORT_KEYSTORE_FILE == null ||
		    Conf.TRANSPORT_KEYSTORE_PASSWORD == null ||
			Conf.TRANSPORT_KEYSTORE_FILE.equals("") ||
		    Conf.TRANSPORT_KEYSTORE_PASSWORD.equals("")) {
			Log.stdlog(Log.ERROR, "PCTransport", "keystore file or password not specified");
			Log.stdlog(Log.ERROR, "PCTransport", "\tkeyStore=" + Conf.TRANSPORT_KEYSTORE_FILE);
			Log.stdlog(Log.ERROR, "PCTransport", "\tkeyStorePassword=" + Conf.TRANSPORT_KEYSTORE_PASSWORD);
			System.exit(Log.ERROR);
		}
		// Set the JVM properties so java can access them
		System.setProperty("javax.net.ssl.keyStore", Conf.TRANSPORT_KEYSTORE_FILE);
		System.setProperty("javax.net.ssl.keyStorePassword", Conf.TRANSPORT_KEYSTORE_PASSWORD);

		// Write the pid file
		if (Conf.TRANSPORT_PID_FILE != null) {
			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(Conf.TRANSPORT_PID_FILE),
																 StandardCharsets.UTF_8,
																 StandardOpenOption.WRITE,
																 StandardOpenOption.CREATE)) {
				writer.write(ProcessHandle.current().pid() + "");
			} catch (IOException e) {
				Log.stdlog(Log.FATAL, "PCTransport",
						   "Could not write pid, will not start transport. Leave out --pid-file to start anyway");
				System.exit(1);
			}
		}
		
		SSLServer transportServer = new SSLServer(PCTransport.transport_ip, PCTransport.transport_port);
	}
	// end: public static void main

}
// end: public class PCTransport
