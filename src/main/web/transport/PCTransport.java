package web.transport;


import java.io.IOException;
import java.util.logging.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;


public class PCTransport implements Runnable {

    public static final Logger LOGGER = Logger.getLogger(PCTransport.class.getName());


    @Parameters(paramLabel = "IP", description = "Specify the address to start the transport on.")
    private String ip;
    @Parameters(paramLabel = "PORT", description = "Specify the port to start the transport on.")
    private int port;


    public static void main(String[] args) {
        new CommandLine(new PCTransport()).execute(args);
    }


    @Override
    public void run() {
        try {
            new TransportServer(this.ip, this.port);
        }
        catch (IOException e) {
            LOGGER.severe("fatal network error from transport: " + e);
            return;
        }
    }

}
