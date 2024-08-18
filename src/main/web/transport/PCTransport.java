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


public class PCTransport implements Runnable {

    public static final Logger LOGGER = Logger.getLogger(PCTransport.class.getName());


    @Parameters(paramLabel = "PROPERTIES",
                description = "Specify the location of the server properties file.")
    private String propertiesFile;


    public static void main(String[] args) {
        new CommandLine(new PCTransport()).execute(args);
    }


    private Properties loadProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(this.propertiesFile));
        }
        catch (IOException e) {
            LOGGER.severe("cannot load properties file '" + this.propertiesFile + "': " + e);
            System.exit(1);
        }
        return properties;
    }


    private void setKeyStore(Properties properties) {
        String keyStore = properties.getProperty("transport.keyStore");
        String keyStorePassword = properties.getProperty("transport.keyStorePassword");
        if (keyStore == null) {
            LOGGER.severe("invalid transport.keyStore: found null");
            System.exit(1);
        }
        if (keyStorePassword == null) {
            LOGGER.severe("invalid transport.keyStorePassword: found null");
            System.exit(1);
        }
        System.setProperty("javax.net.ssl.keyStore", keyStore);
        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
    }


    private void writePidFile(Properties properties) {
        String pidPath = properties.getProperty("transport.pidFile");
        long pid = ProcessHandle.current().pid();
        if (pidPath == null) {
            LOGGER.warning("tranport.pidFile is not defined, pid is " + pid);
            return;
        }

        File pidFile = new File(pidPath);
        pidFile.deleteOnExit();

        try (PrintWriter pidWriter = new PrintWriter(pidFile)) {
            pidWriter.println(pid);
        }
        catch (IOException e) {
            LOGGER.severe("cannot write transport.pidFile: " + e);
            System.exit(1);
        }
    }


    private void setLogFile(Properties properties) {
        String logFile = properties.getProperty("transport.logFile");
        if (logFile == null) {
            LOGGER.warning("transport.logFile is not defined");
            return;
        }

        LOGGER.setUseParentHandlers(false);
        try {
            FileHandler fh = new FileHandler(logFile);
            LOGGER.addHandler(fh);
        }
        catch (IOException e) {
            LOGGER.severe("cannot write transport.logFile: " + e);
                System.exit(1);
        }
    }


    private String getIP(Properties properties) {
        String ip = properties.getProperty("transport.ip");
        if (ip == null) {
            LOGGER.severe("invalid transport.ip: found null");
            System.exit(1);
        }
        return ip;
    }


    private int getPort(Properties properties) {
        int port = -1;
        try {
            port = Integer.parseInt(properties.getProperty("transport.port"));
        }
        catch (NumberFormatException e) {
            LOGGER.severe("invalid transport.port: " + e);
            System.exit(1);
        }
        return port;
    }


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
            LOGGER.severe("fatal exception in transport: " + e);
            return;
        }
    }

}
