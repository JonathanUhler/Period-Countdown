package web.transport.commands;


import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import school.SchoolAPI;
import user.UserAPI;


/**
 * Requests the style and functional settings of the user.
 *
 * See the Period Countdown Web Specification for more information on this command.
 *
 * @author Jonathan Uhler
 */
public class GetUserSettings extends Command {


    public class InputPayload {
    }
	
    public class OutputPayload {
        @SerializedName("Theme")
        public String theme;
        @SerializedName("Font")
        public String font;
        @SerializedName("SchoolJson")
        public String schoolJson;
        @SerializedName("AvailableSchools")
        public List<String> availableSchools;
    }

    @SerializedName("InputPayload")
    public InputPayload inputPayload;
    @SerializedName("OutputPayload")
    public OutputPayload outputPayload;


    @Override
    public Command process(String request, SchoolAPI schoolAPI, UserAPI userAPI) {
        Gson gson = new Gson();
        GetUserSettings command = gson.fromJson(request, GetUserSettings.class);

        GetUserSettings response = new GetUserSettings();
        response.opcode = command.opcode;
        response.userId = command.userId;
        response.returnCode = ReturnCode.SUCCESS;
        response.outputPayload = response.new OutputPayload();
        response.outputPayload.theme = Integer.toString(userAPI.getTheme());
        response.outputPayload.font = userAPI.getFont();
        response.outputPayload.schoolJson = userAPI.getSchoolFile().toString();
        response.outputPayload.availableSchools = new ArrayList<>();  // Left for DB to fill
        
        return response;
    }

}
