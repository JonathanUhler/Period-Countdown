package web.transport.commands;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import school.SchoolAPI;
import school.SchoolPeriod;
import user.UserAPI;
import user.UserPeriod;
import time.UTCTime;
import time.Duration;


/**
 * Requests information about the current and next period.
 *
 * See the Period Countdown Web Specification for more information on this command.
 *
 * @author Jonathan Uhler
 */
public class GetCurrentPeriod extends Command {


    public class InputPayload {
    }
	
    public class OutputPayload {
        @SerializedName("CurrentName")
        public String currentName;
        @SerializedName("CurrentStatus")
        public String currentStatus;
        @SerializedName("NextName")
        public String nextName;
        @SerializedName("NextDuration")
        public String nextDuration;
    }

    @SerializedName("InputPayload")
    public InputPayload inputPayload;
    @SerializedName("OutputPayload")
    public OutputPayload outputPayload;


    @Override
    public String process(String request, SchoolAPI schoolAPI, UserAPI userAPI) {
        Gson gson = new Gson();
        GetCurrentPeriod command = gson.fromJson(request, GetCurrentPeriod.class);

        UTCTime now = UTCTime.now();
        SchoolPeriod currentSchoolPeriod = schoolAPI.getCurrentPeriod(now);
        SchoolPeriod nextSchoolPeriod = schoolAPI.getNextCountedPeriod(now);

        GetCurrentPeriod response = new GetCurrentPeriod();
        response.opcode = command.opcode;
        response.userId = command.userId;
        response.returnCode = ReturnCode.SUCCESS;
        response.outputPayload = response.new OutputPayload();
        response.outputPayload.currentName = null;
        response.outputPayload.currentStatus = null;
        response.outputPayload.nextName = null;
        response.outputPayload.nextDuration = null;

        if (currentSchoolPeriod != null) {
            UserPeriod currentUserPeriod = userAPI.getPeriod(currentSchoolPeriod);
            response.outputPayload.currentName = currentUserPeriod.getName();
            response.outputPayload.currentStatus = currentUserPeriod.getStatus();
        }
        if (nextSchoolPeriod != null) {
            UserPeriod nextUserPeriod = userAPI.getPeriod(nextSchoolPeriod);
            UTCTime nextStart = nextSchoolPeriod.getStart();
            UTCTime nextEnd = nextSchoolPeriod.getEnd().plus(1, UTCTime.SECONDS);
            Duration nextDuration = new Duration(nextStart, nextEnd);
            response.outputPayload.nextName = nextUserPeriod.getName();
            response.outputPayload.nextDuration = nextDuration.toString();
        }
        
        return gson.toJson(response);
    }

}
