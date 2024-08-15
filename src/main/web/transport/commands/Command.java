package web.transport.commands;


import com.google.gson.annotations.SerializedName;
import school.SchoolAPI;
import user.UserAPI;
import web.transport.PCTransport;


public class Command {

    public enum ReturnCode {
        /** The command was processed successfully and returned a valid response. */
        SUCCESS,
        /** No user ID was provided; no further processing can be completed. */
        SIGNED_OUT,
        /** A formatting error exists in the request JSON string. */
        ERR_PARSE,
        /** The content of the parsed JSON string is invalid (e.g. a key is missing). */
        ERR_PAYLOAD,
        /** An internal error occurred that revents a valid response from being sent. */
        ERR_RESPONSE,
        /** An error occurred. */
        ERR_GENERIC
    }


    public enum Opcode {
        /** A special opcode indicating a return code that is not 'SUCCESS'. */
        ERROR,
        /** Requests information about the time remaining in the current period. */
        GET_TIME_REMAINING
    }


    @SerializedName("Opcode")
    public Opcode opcode;
    @SerializedName("UserID")
    public String userId;
    @SerializedName("ReturnCode")
    public ReturnCode returnCode;


    public String process(String request, SchoolAPI schoolAPI, UserAPI userAPI) {
        return Command.error(ReturnCode.ERR_GENERIC, "generic Commands cannot be processed");
    }


    public static String error(ReturnCode returnCode, String message) {
        return Command.error("", returnCode, message);
    }


    public static String error(String userId, ReturnCode returnCode, String message) {
        String response = "{\"Opcode\": \"" + Opcode.ERROR + "\", " +
            "\"UserID\": \"" + userId + "\", " +
            "\"ReturnCode\": \"" + returnCode + "\", " +
            "\"OutputPayload\": {\"Message\": \"" + message + "\"}}";
        PCTransport.LOGGER.warning("Command.error: " + response);
        return response;
    }

}
