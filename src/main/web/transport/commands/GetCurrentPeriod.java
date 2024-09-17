package web.transport.commands;


import com.google.gson.Gson;
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
        @SerializedName("CurrentDuration")
        public String currentDuration;
        @SerializedName("NextStatus")
        public String nextStatus;
        @SerializedName("NextDuration")
        public String nextDuration;
    }

    @SerializedName("InputPayload")
    public InputPayload inputPayload;
    @SerializedName("OutputPayload")
    public OutputPayload outputPayload;


    @Override
    public Command process(String request, SchoolAPI schoolAPI, UserAPI userAPI) {
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
        response.outputPayload.nextStatus = null;
        response.outputPayload.nextDuration = null;

        UserPeriod currentUserPeriod = userAPI.getPeriod(currentSchoolPeriod);
        response.outputPayload.currentName = currentUserPeriod.getName();
        response.outputPayload.currentStatus = currentUserPeriod.getStatus();

        if (nextSchoolPeriod != null) {
            UserPeriod nextUserPeriod = userAPI.getPeriod(nextSchoolPeriod);
            Duration nextUpTime = new Duration(nextSchoolPeriod.getStart(),
                                               nextSchoolPeriod.getEnd().plus(1, UTCTime.SECONDS));
            response.outputPayload.nextDuration = nextUpTime.toString();
            response.outputPayload.nextStatus = nextUserPeriod.getStatus();
        }
        if (currentSchoolPeriod != null && nextSchoolPeriod != null) {
            Duration currentDuration = new Duration(currentSchoolPeriod.getEnd(),
                                                    nextSchoolPeriod.getStart());
            response.outputPayload.currentDuration = currentDuration.toString();
        }
        
        return response;
    }

}
