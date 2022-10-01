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

	public static final String PID_FILE = "/var/www/Period-Countdown-WSGI/logs/PCTransport.pid";
	

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
		// Check for the keystore and keystore password arguments. If these aren't specified (e.g. they're
		// null) then the transport cannot start securely
		if (System.getProperty("javax.net.ssl.keyStore") == null ||
			System.getProperty("javax.net.ssl.keyStorePassword") == null) {
			Log.stdlog(Log.ERROR, "PCTransport", "keystore file or password not specified");
			Log.stdlog(Log.ERROR, "PCTransport", "\t" +
					   "keyStore=" + System.getProperty("javax.net.ssl.keyStore") + ", " + 
					   "keyStorePassword=" + System.getProperty("javax.net.ssl.keyStorePassword"));
			System.exit(Log.ERROR);
		}

		try {
			OptionParser optionParser = new OptionParser(PCTransport.class);
			optionParser.parse(args);
		}
		catch (Exception e) {
			Log.stdlog(Log.FATAL, "PCTransport", "cannot parse command line arguments");
		}

		// Write the pid file
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(PCTransport.PID_FILE),
														   StandardCharsets.UTF_8,
														   StandardOpenOption.WRITE,
														   StandardOpenOption.CREATE)) {
			writer.write(ProcessHandle.current().pid() + "");
		} catch (IOException e) {
			Log.stdlog(Log.FATAL, "PCTransport", "Could not get pid, cannot start transport");
			System.exit(1);
		}
		
		SSLServer transportServer = new SSLServer(PCTransport.transport_ip, PCTransport.transport_port);
	}
	// end: public static void main

}
// end: public class PCTransport
