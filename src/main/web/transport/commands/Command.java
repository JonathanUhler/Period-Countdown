package web.transport.commands;


import java.util.Map;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import school.SchoolAPI;
import user.UserAPI;
import web.transport.PCTransport;


/**
 * Generic representation of a transport command.
 *
 * This class must be inherited by command implementations in the {@code web.transport.command}
 * package, where an input/output payloads and additional header parameters can be specified.
 *
 * @author Jonathan Uhler
 */
public class Command {

    /**
     * The return code representing the processing status of the transport's response.
     */
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


    /**
     * The operation code of a command. When the transport returns a {@code SUCCESS} response,
     * the opcode of the response will match the request.
     */
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


    /**
     * Processes a specified type of command.
     *
     * Processing is provided with the stringified JSON of the incoming request, and APIs for
     * accessing information about the database user's school and user JSON files. Depending on
     * the command type, this information is used to build a response, whose stringified JSON is
     * returned.
     *
     * Children of this {@code Command} class must provide their own implementation of this method.
     * A generic {@code Command} cannot be processed, and will return a {@code ERR_GENERIC}
     * response.
     *
     * @param request    the transport request as a JSON string, which can be parsed into a
     *                   command object.
     * @param schoolAPI  the school API for the database user who sent the transport request.
     * @param userAPI    the user API for the database user who sent the transport request.
     *
     * @return the transport response as a JSON string.
     */
    public String process(String request, SchoolAPI schoolAPI, UserAPI userAPI) {
        return Command.error(ReturnCode.ERR_GENERIC, "generic Commands cannot be processed");
    }


    /**
     * Creates a generic error payload as a JSON string.
     *
     * @param returnCode  the error return code.
     * @param message     a message describing the error.
     */
    public static String error(ReturnCode returnCode, String message) {
        return Command.error("", returnCode, message);
    }


    /**
     * Creates an error payload as a JSON string for a specific database user.
     *
     * @param userId      the unique identifier of the user related to the error.
     * @param returnCode  the error return code.
     * @param message     a message describing the error.
     */
    public static String error(String userId, ReturnCode returnCode, String message) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> outputPayload = new HashMap<>();
        outputPayload.put("Message", message);
        response.put("Opcode", Opcode.ERROR.name());
        response.put("UserID", userId);
        response.put("ReturnCode", returnCode.name());
        response.put("OutputPayload", outputPayload);

        Gson gson = new Gson();
        PCTransport.LOGGER.warning("Command.error: " + response);
        return gson.toJson(response);
    }

}
