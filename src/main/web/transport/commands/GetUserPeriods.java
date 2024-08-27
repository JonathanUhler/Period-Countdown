package web.transport.commands;


import java.util.Map;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import school.SchoolAPI;
import school.SchoolPeriod;
import user.UserAPI;
import user.UserPeriod;
import user.UserJson;
import time.UTCTime;


/**
 * Requests the user-defined properties for each period of the current school JSON file.
 *
 * See the Period Countdown Web Specification for more information on this command.
 *
 * @author Jonathan Uhler
 */
public class GetUserPeriods extends Command {


    public class InputPayload {
    }
	
    public class OutputPayload {
        @SerializedName("UserPeriods")
        public Map<String, Map<String, String>> userPeriods;
    }

    @SerializedName("InputPayload")
    public InputPayload inputPayload;
    @SerializedName("OutputPayload")
    public OutputPayload outputPayload;


    @Override
    public Command process(String request, SchoolAPI schoolAPI, UserAPI userAPI) {
        Gson gson = new Gson();
        GetUserPeriods command = gson.fromJson(request, GetUserPeriods.class);

        GetUserPeriods response = new GetUserPeriods();
        response.opcode = command.opcode;
        response.userId = command.userId;
        response.returnCode = ReturnCode.SUCCESS;
        response.outputPayload = response.new OutputPayload();
        response.outputPayload.userPeriods = new HashMap<>();

        UTCTime now = UTCTime.now();
        for (String periodKey : userAPI.getPeriodKeys()) {
            SchoolPeriod schoolPeriod = new SchoolPeriod(periodKey, "", now, now, false);
            UserPeriod userPeriod = userAPI.getPeriod(schoolPeriod);

            Map<String, String> periodInfo = new HashMap<>();
            periodInfo.put(UserJson.NAME, userPeriod.getName());
            periodInfo.put(UserJson.TEACHER, userPeriod.getTeacher());
            periodInfo.put(UserJson.ROOM, userPeriod.getRoom());
            response.outputPayload.userPeriods.put(periodKey, periodInfo);
        }
        
        return response;
    }

}
