package web.transport;


import java.io.IOException;
import java.util.Properties;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jnet.Bytes;
import jnet.JClientSocket;
import jnet.secure.JSSLServer;
import school.SchoolAPI;
import school.SchoolJson;
import user.UserAPI;
import user.UserJson;
import web.transport.commands.*;


/**
 * Secure server to receive and process transport commands.
 *
 * All transport commands require a unique user identifier that is registered with the
 * internal {@code TransportDatabase} instance.
 *
 * The transport will always return a payload command to the requesting client. If any error
 * occurs while processing the request, a special error payload will be returned.
 *
 * @author Jonathan Uhler
 */
public class TransportServer extends JSSLServer {

    private TransportDatabase database;


    /**
     * Constructs a new {@code TransportServer}.
     *
     * After construction, the server will be bound to the specified IP address and port.
     *
     * @param properties  transport properties used to initialize the database.
     * @param ip          the IP address to start the transport on.
     * @param port        the port to start the transport on.
     */
    public TransportServer(Properties properties, String ip, int port) throws IOException {
        super(ip, port);
        this.database = new TransportDatabase(properties);
    }


    /**
     * Processes an incoming request.
     *
     * @param commandStr  the stringified JSON of the incoming request.
     *
     * @return the stringified JSON of the return payload.
     */
    private String process(String commandStr) {
        // Parse command and extract general information (opcode and user ID)
        Gson gson = new Gson();
        Command command;
        try {
            command = gson.fromJson(commandStr, Command.class);
        }
        catch (JsonSyntaxException e) {
            return Command.error(Command.ReturnCode.ERR_PARSE, "cannot parse Command: " + e);
        }

        Command.Opcode opcode = command.opcode;
        String userId = command.userId;
        if (opcode == null) {
            return Command.error(Command.ReturnCode.ERR_PAYLOAD, "missing Opcode");
        }
        if (userId == null) {
            return Command.error(Command.ReturnCode.ERR_PAYLOAD, "missing UserID");
        }

        // From the user ID, construct time APIs that can be used to get data about this user
        UserAPI userAPI;
        SchoolAPI schoolAPI;

        try {
            UserJson userJson = this.database.getUserJson(userId);
            userAPI = new UserAPI(userJson);
        }
        catch (RuntimeException e) {
            return Command.error(userId, Command.ReturnCode.ERR_RESPONSE, "UserAPI: " + e);
        }

        try {
            SchoolJson schoolJson = this.database.getSchoolJson(userId, userAPI.getSchoolFile());
            schoolAPI = new SchoolAPI(schoolJson);
        }
        catch (IOException | RuntimeException e) {
            return Command.error(userId, Command.ReturnCode.ERR_RESPONSE, "SchoolAPI: " + e);
        }

        // Process the command based on the provided opcode and return response information
        try {
            switch (opcode) {
            case GET_TIME_REMAINING:
                return new GetTimeRemaining().process(commandStr, schoolAPI, userAPI);
            case GET_CURRENT_PERIOD:
                return new GetCurrentPeriod().process(commandStr, schoolAPI, userAPI);
            default:
                return Command.error(Command.ReturnCode.ERR_RESPONSE, "unknown opcode: " + opcode);
            }
        }
        catch (RuntimeException e) {
            return Command.error(userId, Command.ReturnCode.ERR_RESPONSE, "process error: " + e);
        }
    }


    @Override
    public void clientCommunicated(byte[] recv, JClientSocket clientSocket) {
        try {
            String response = this.process(Bytes.bytesToString(recv));
            this.send(response, clientSocket);
        }
        catch (IOException e) {
            PCTransport.LOGGER.warning("network error from " + clientSocket + ": " + e);
            return;
        }
    }


    @Override
    public void clientConnected(JClientSocket clientSocket) { }

    @Override
    public void clientDisconnected(JClientSocket clientSocket) { }

}
