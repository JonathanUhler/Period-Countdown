package web.transport;


import java.io.IOException;
import java.util.Properties;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jnet.Bytes;
import jnet.JClientSocket;
import jnet.secure.JSSLServer;
import school.SchoolAPI;
import user.UserAPI;
import web.transport.commands.*;


public class TransportServer extends JSSLServer {

    public TransportServer(Properties properties, String ip, int port) throws IOException {
        super(ip, port);
    }


    private String process(String commandStr) {
        // Parse command and extract general information (opcode and user ID)
        Gson gson = new Gson();
        Command command;
        try {
            command = gson.fromJson(commandStr, Command.class);
            System.out.println(command.opcode + ", " + command.userId + ", " + command.returnCode);
        }
        catch (JsonSyntaxException e) {
            return Command.error(Command.ReturnCode.ERR_PARSE, "cannot parse Command: " + e);
        }

        Command.Opcode opcode = command.opcode;
        if (opcode == null) {
            return Command.error(Command.ReturnCode.ERR_PAYLOAD, "missing Opcode");
        }

        // From the user ID, construct time APIs that can be used to get data about this user
        UserAPI userAPI;
        SchoolAPI schoolAPI;

        try {
            userAPI = new UserAPI();
        }
        catch (IOException | RuntimeException e) {
            return Command.error(Command.ReturnCode.ERR_RESPONSE,
                                 "exception when creating UserAPI: " + e);
        }

        try {
            schoolAPI = new SchoolAPI(userAPI.getSchoolFile());
        }
        catch (IOException | RuntimeException e) {
            return Command.error(Command.ReturnCode.ERR_RESPONSE,
                                 "exception when creating SchoolAPI: " + e);
        }

        // Process the command based on the provided opcode and return response information
        try {
            switch (opcode) {
            case GET_TIME_REMAINING:
                return new GetTimeRemaining().process(commandStr, schoolAPI, userAPI);
            default:
                return Command.error(Command.ReturnCode.ERR_RESPONSE, "unknown opcode: " + opcode);
            }
        }
        catch (RuntimeException e) {
            return Command.error(Command.ReturnCode.ERR_RESPONSE, "command process error: " + e);
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
