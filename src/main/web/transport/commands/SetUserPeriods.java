package web.transport.commands;


import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import school.SchoolAPI;
import user.UserAPI;


/**
 * Updates one or more user-defined periods for the currently selected school JSON file.
 *
 * See the Period Countdown Web Specification for more information on this command.
 *
 * @author Jonathan Uhler
 */
public class SetUserPeriods extends Command {


    public class InputPayload {
        @SerializedName("UserPeriods")
        public Map<String, Map<String, String>> userPeriods;
    }
	
    public class OutputPayload {
    }

    @SerializedName("InputPayload")
    public InputPayload inputPayload;
    @SerializedName("OutputPayload")
    public OutputPayload outputPayload;


    @Override
    public Command process(String request, SchoolAPI schoolAPI, UserAPI userAPI) {
        Gson gson = new Gson();
        SetUserPeriods command = gson.fromJson(request, SetUserPeriods.class);
        if (command.inputPayload == null) {
            throw new NullPointerException("SetUserPeriod missing input payload");
        }

        for (String periodKey : command.inputPayload.userPeriods.keySet()) {
            Map<String, String> periodInfo = command.inputPayload.userPeriods.get(periodKey);
            userAPI.setPeriod(periodKey, periodInfo);
        }

        SetUserPeriods response = new SetUserPeriods();
        response.opcode = command.opcode;
        response.userId = command.userId;
        response.returnCode = ReturnCode.SUCCESS;
        response.outputPayload = response.new OutputPayload();
        
        return response;
    }

}
