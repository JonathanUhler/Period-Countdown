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

	@Option(name = "pid-file",
			abbreviation = 'p',
			nargs = 1,
			type = String.class,
			help = "set pid file; if not set, pid file will not be written")
	public static String pid_file;

	@Option(name = "log-file",
			abbreviation = 'l',
			nargs = 1,
			type = String.class,
			help = "set log file; if not set, log file will not be written")
	public static String log_file;
	

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

		// If specified by the command line, set log file as a system property to be picked up once by util.Log
		if (PCTransport.log_file != null)
			System.setProperty(Log.LOG_FILE_SYS_PROPERTY, PCTransport.log_file);
		
		// Check for the keystore and keystore password arguments. If these aren't specified (e.g. they're
		// null) then the transport cannot start securely
		if (System.getProperty("javax.net.ssl.keyStore") == null ||
			System.getProperty("javax.net.ssl.keyStorePassword") == null) {
			Log.stdlog(Log.ERROR, "PCTransport", "keystore file or password not specified");
			Log.stdlog(Log.ERROR, "PCTransport",
					   "\tkeyStore=" + System.getProperty("javax.net.ssl.keyStore"));
			Log.stdlog(Log.ERROR, "PCTransport",
					   "\tkeyStorePassword=" + System.getProperty("javax.net.ssl.keyStorePassword"));
			System.exit(Log.ERROR);
		}

		// Write the pid file
		if (PCTransport.pid_file != null) {
			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(PCTransport.pid_file),
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
