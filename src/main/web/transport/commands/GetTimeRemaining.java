package web.transport.commands;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import school.SchoolAPI;
import school.SchoolPeriod;
import user.UserAPI;
import time.UTCTime;
import time.Duration;


/**
 * Requests information about the amount of time remaining in the current period.
 *
 * See the Period Countdown Web Specification for more information on this command.
 *
 * @author Jonathan Uhler
 */
public class GetTimeRemaining extends Command {


    public class InputPayload {
    }
	
    public class OutputPayload {
        @SerializedName("TimeRemaining")
        public String timeRemaining;
        @SerializedName("EndTime")
        public String endTime;
        @SerializedName("ExpireTime")
        public String expireTime;
    }

    @SerializedName("InputPayload")
    public InputPayload inputPayload;
    @SerializedName("OutputPayload")
    public OutputPayload outputPayload;


    @Override
    public String process(String request, SchoolAPI schoolAPI, UserAPI userAPI) {
        Gson gson = new Gson();
        GetTimeRemaining command = gson.fromJson(request, GetTimeRemaining.class);

        UTCTime now = UTCTime.now();
        SchoolPeriod currentPeriod = schoolAPI.getCurrentPeriod(now);
        Duration timeRemaining = schoolAPI.getTimeRemaining(now);

        GetTimeRemaining response = new GetTimeRemaining();
        response.opcode = command.opcode;
        response.userId = command.userId;
        response.returnCode = ReturnCode.SUCCESS;
        response.outputPayload = response.new OutputPayload();
        response.outputPayload.timeRemaining = timeRemaining.toString();
        response.outputPayload.endTime = timeRemaining.getEnd().toString();
        response.outputPayload.expireTime = currentPeriod.getEnd().toString();

        return gson.toJson(response);
    }

}
