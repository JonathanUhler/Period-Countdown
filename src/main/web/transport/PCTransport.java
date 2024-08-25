package web.transport;


import java.io.IOException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;


/**
 * Command line interface and entry point for running the Period Countdown transport.
 *
 * This interface takes a single command line argument specifying the path to a Java properties
 * file that contains configuration information for the transport.
 *
 * @author Jonathan Uhler
 */
public class PCTransport implements Runnable {

    /** A logger used by the transport to report runtime information. */
    public static final Logger LOGGER = Logger.getLogger(PCTransport.class.getName());


    @Parameters(paramLabel = "PROPERTIES",
                description = "Specify the location of the server properties file.")
    private String propertiesFile;


    /**
     * Command line entry point
     *
     * @param args  command line arguments.
     */
    public static void main(String[] args) {
        new CommandLine(new PCTransport()).execute(args);
    }


    /**
     * Loads the properties file.
     *
     * This method does not perform any validation on the existence or value of entries in the
     * properties file. Values must be validated before being used.
     *
     * If reading the properties file fails, a fatal error is raised and the process exits.
     *
     * @return the properties file specified from the command line as a {@code Properties} object.
     */
    private Properties loadProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(this.propertiesFile));
        }
        catch (IOException e) {
            PCTransport.LOGGER.severe("cannot load properties '" + this.propertiesFile + "': " + e);
            System.exit(1);
        }
        return properties;
    }


    /**
     * Loads the keystore and keystore password for SSL connections.
     *
     * If any error occurs setting up the keystore, a fatal error is raised and the process exits.
     *
     * @param properties  the transport properties, which must contain the keystore and password.
     */
    private void setKeyStore(Properties properties) {
        String keyStore = properties.getProperty("transport.keyStore");
        String keyStorePassword = properties.getProperty("transport.keyStorePassword");
        if (keyStore == null) {
            PCTransport.LOGGER.severe("invalid transport.keyStore: found null");
            System.exit(1);
        }
        if (keyStorePassword == null) {
            PCTransport.LOGGER.severe("invalid transport.keyStorePassword: found null");
            System.exit(1);
        }
        System.setProperty("javax.net.ssl.keyStore", keyStore);
        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
    }


    /**
     * Writes a file with the transport process ID.
     *
     * If no PID file is specified in the transport properties, a warning message is logged, but
     * the process will continue without writing the PID file. If the PID file is specified but
     * cannot be written, a fatal error is raised and the process exits.
     *
     * @param properties  the transport properties, which is recommended to contain a PID file path.
     */
    private void writePidFile(Properties properties) {
        String pidPath = properties.getProperty("transport.pidFile");
        long pid = ProcessHandle.current().pid();
        if (pidPath == null) {
            PCTransport.LOGGER.warning("tranport.pidFile is not defined, pid is " + pid);
            return;
        }

        File pidFile = new File(pidPath);
        pidFile.deleteOnExit();

        try (PrintWriter pidWriter = new PrintWriter(pidFile)) {
            pidWriter.println(pid);
        }
        catch (IOException e) {
            PCTransport.LOGGER.severe("cannot write transport.pidFile: " + e);
            System.exit(1);
        }
    }


    /**
     * Adds a file handler to {@code PCTransport.LOGGER}.
     *
     * If no log file is specified in the transport properties, a warning message is printed to
     * the standard output, and future log messages are directed to the default console handler.
     * If a log file is specified but cannot be added as a file handler, a fatal error is raised
     * and the process exits.
     *
     * @param properties  the transport properties, which is recommended to contain a log file path.
     */
    private void setLogFile(Properties properties) {
        String logFile = properties.getProperty("transport.logFile");
        if (logFile == null) {
            PCTransport.LOGGER.warning("transport.logFile is not defined");
            return;
        }

        PCTransport.LOGGER.setUseParentHandlers(false);
        try {
            FileHandler fh = new FileHandler(logFile);
            PCTransport.LOGGER.addHandler(fh);
        }
        catch (IOException e) {
            PCTransport.LOGGER.severe("cannot write transport.logFile: " + e);
            System.exit(1);
        }
    }


    /**
     * Returns the IP address of the transport.
     *
     * If the IP address is not specified in the properties file, a fatal error is raised and the
     * process exits.
     *
     * @param properties  the transport properties which contains the IP address.
     */
    private String getIP(Properties properties) {
        String ip = properties.getProperty("transport.ip");
        if (ip == null) {
            PCTransport.LOGGER.severe("invalid transport.ip: found null");
            System.exit(1);
        }
        return ip;
    }


    /**
     * Returns the port of the transport.
     *
     * If the port is not a valid integer, a fatal error is raised and the process exits.
     *
     * @param properties  the transport properties which contains the port.
     */
    private int getPort(Properties properties) {
        int port = -1;
        try {
            port = Integer.parseInt(properties.getProperty("transport.port"));
        }
        catch (NumberFormatException e) {
            PCTransport.LOGGER.severe("invalid transport.port: " + e);
            System.exit(1);
        }
        return port;
    }


    /**
     * Sets up and runs the transport.
     *
     * Before starting, the transport will perform the following actions in order, exiting on
     * any fatal error:
     * - Load the transport properties file
     * - Add a file handler to the transport logger
     * - Sets the Java keystore and keystore password
     * - Writes the transport process ID file
     * - Loads the transport host address and port
     */
    @Override
    public void run() {
        Properties properties = this.loadProperties();
        this.setLogFile(properties);
        this.setKeyStore(properties);
        this.writePidFile(properties);

        String ip = this.getIP(properties);
        int port = this.getPort(properties);
        try {
            new TransportServer(properties, ip, port);
        }
        catch (IOException | RuntimeException e) {
            PCTransport.LOGGER.severe("fatal exception in transport: " + e);
            return;
        }
    }

}
