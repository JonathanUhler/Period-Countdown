package web.transport.commands;


import java.nio.file.Paths;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import school.SchoolAPI;
import user.UserAPI;


/**
 * Updates one or more fields related to the user's settings.
 *
 * See the Period Countdown Web Specification for more information on this command.
 *
 * @author Jonathan Uhler
 */
public class SetUserSettings extends Command {


    public class InputPayload {
        @SerializedName("Theme")
        public String theme;
        @SerializedName("Font")
        public String font;
        @SerializedName("SchoolJson")
        public String schoolJson;
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
        SetUserSettings command = gson.fromJson(request, SetUserSettings.class);
        if (command.inputPayload == null) {
            throw new NullPointerException("SetUserSettings missing input payload");
        }

        String theme = command.inputPayload.theme;
        if (theme != null) {
            int rgb = Integer.parseInt(theme);
            int r = (rgb >> 8) & 0xFF;
            int g = (rgb >> 4) & 0xFF;
            int b = rgb & 0xFF;
            userAPI.setTheme(r, g, b);
        }
        String font = command.inputPayload.font;
        if (font != null) {
            userAPI.setFont(font);
        }
        String schoolJson = command.inputPayload.schoolJson;
        if (schoolJson != null) {
            userAPI.setSchoolFile(Paths.get(schoolJson));
        }

        SetUserSettings response = new SetUserSettings();
        response.opcode = command.opcode;
        response.userId = command.userId;
        response.returnCode = ReturnCode.SUCCESS;
        response.outputPayload = response.new OutputPayload();
        
        return response;
    }

}
