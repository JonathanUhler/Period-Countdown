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
        /** Recoverable. No user ID was provided; no further processing can be completed. */
        SIGNED_OUT,
        /** A formatting error exists in the request JSON string. */
        ERR_PARSE,
        /** The content of the parsed JSON string is invalid (e.g. a key is missing). */
        ERR_PAYLOAD,
        /** An error occurred that prevents all response data from being generated. */
        ERR_RESPONSE,
        /** An error occurred. */
        ERR_GENERIC
    }


    /**
     * The operation code of a command. When the transport returns a {@code SUCCESS} response,
     * the opcode of the response will match the request.
     */
    public enum Opcode {
        /** A special opcode used when an error occurs on an input payload without an opcode. */
        ERROR,
        /** Requests information about the time remaining in the current period. */
        GET_TIME_REMAINING,
        /** Requests information about the current and next period. */
        GET_CURRENT_PERIOD,
        /** Requests the user-defined properties for each period of the current school file. */
        GET_USER_PERIODS,
        /** Requests the style and functional settings of the user. */
        GET_USER_SETTINGS,
        /** Updates the contents of a school file. */
        SET_SCHOOL_JSON,
        /** Updates one or more user-defined periods for the current school file. */
        SET_USER_PERIODS,
        /** Udates one or more fields related to the user's settings */
        SET_USER_SETTINGS
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
     * A generic {@code Command} cannot be processed, and attempting the process will throw an
     * {@code UnsupportedOperationException}.
     *
     * @param request    the transport request as a JSON string, which can be parsed into a
     *                   command object.
     * @param schoolAPI  the school API for the database user who sent the transport request.
     * @param userAPI    the user API for the database user who sent the transport request.
     *
     * @return the transport response as a JSON string.
     */
    public Command process(String request, SchoolAPI schoolAPI, UserAPI userAPI) {
        throw new UnsupportedOperationException("generic Command cannot be processed");
    }


    /**
     * Creates a generic error payload as a JSON string.
     *
     * @param returnCode  the error return code.
     * @param message     a message describing the error.
     */
    public static String error(ReturnCode returnCode, String message) {
        return Command.error(Opcode.ERROR, "", returnCode, message);
    }


    /**
     * Creates an error payload as a JSON string for a specific invalid request.
     *
     * @param opcode      the opcode of the request that caused the error.
     * @param returnCode  the error return code.
     * @param message     a message describing the error.
     */
    public static String error(Opcode opcode, ReturnCode returnCode, String message) {
        return Command.error(opcode, "", returnCode, message);
    }


    /**
     * Creates an error payload as a JSON string for a specific request database user.
     *
     * @param opcode      the opcode of the request that caused the error.
     * @param userId      the unique identifier of the user related to the error.
     * @param returnCode  the error return code.
     * @param message     a message describing the error.
     */
    public static String error(Opcode opcode,
                               String userId,
                               ReturnCode returnCode,
                               String message)
    {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> outputPayload = new HashMap<>();
        outputPayload.put("Message", message);
        response.put("Opcode", opcode);
        response.put("UserID", userId);
        response.put("ReturnCode", returnCode.name());
        response.put("OutputPayload", outputPayload);

        Gson gson = new Gson();
        PCTransport.LOGGER.warning("Command.error: " + response);
        return gson.toJson(response);
    }

}
