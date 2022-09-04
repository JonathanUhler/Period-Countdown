package web.transport.commands;


import util.Log;
import school.SchoolAPI;
import user.UserAPI;
import java.util.Map;
import java.util.List;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;


public abstract class Command {

	// Constants used throughout the transport code
	public static final String SUCCESS = "SUCCESS";
	public static final String ERR_PARSE = "ERR_PARSE";
	public static final String ERR_KEY = "ERR_KEY";
	public static final String ERR_INVALID = "ERR_INVALID";
	public static final String ERR_INIT = "ERR_INIT";
	public static final String ERR_GENERIC = "ERR_GENERIC";

	public static final String GET_USER_PERIOD = "GetUserPeriod";
	public static final String GET_PERIOD_NUMBERS = "GetPeriodNumbers";
	public static final String GET_TIME_REMAINING = "GetTimeRemaining";
	public static final String GET_AVAILABLE_SCHOOLS = "GetAvailableSchools";
	public static final String GET_NEXT_UP_LIST = "GetNextUpList";
	public static final String SET_USER_PERIOD = "SetUserPeriod";
	public static final String SET_CURRENT_SCHOOL = "SetCurrentSchool";
	public static final String SET_NEXT_UP = "SetNextUp";
	public static final String LOGIN_USER = "LoginUser";

	public static String returnErr(String logMessage, String opcode, String userID, String returnCode) {
		Log.stdlog(Log.ERROR, "Command", logMessage);
		return "{\"Opcode\": \"" + opcode + "\", " +
			"\"UserID\": \"" + userID + "\", " +
			"\"ReturnCode\": \"" + returnCode + "\", " +
			"\"OutputPayload\": {\"Message\": \"" + logMessage + "\"}}"; 
	}
	

	// Held by the Command class, passed to all subclasses of Command
	@SerializedName("Opcode")
	public String opcode;
	@SerializedName("UserID")
	public String userID;
	@SerializedName("ReturnCode")
	public String returnCode;


	// Must be overriden by the subclass. Takes in a request and returns a response, both as strings
	public abstract String process(String request, SchoolAPI schoolAPI, UserAPI userAPI) throws JsonSyntaxException;

}
