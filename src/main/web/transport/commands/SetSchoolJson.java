package web.transport.commands;


import java.util.Map;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import school.SchoolAPI;
import school.SchoolJson;
import user.UserAPI;


/**
 * Updates the contents of a school file.
 *
 * See the Period Countdown Web Specification for more information on this command.
 *
 * @author Jonathan Uhler
 */
public class SetSchoolJson extends Command {


    public class InputPayload {
        @SerializedName("SchoolJson")
        public String schoolJson;
        @SerializedName("Content")
        public String content;
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
        SetSchoolJson command = gson.fromJson(request, SetSchoolJson.class);
        if (command.inputPayload == null) {
            throw new NullPointerException("SetSchoolJson missing input payload");
        }

        String name = command.inputPayload.schoolJson;
        String content = command.inputPayload.content;
        if (name == null) {
            throw new NullPointerException("SetSchoolJson missing SchoolJson key");
        }

        // Validate the school JSON content. If any parsing error is thrown here, it will be
        // handled by the TransportServer class. If no error is raised, then the input payload
        // is known to be valid and the `SchoolJson : Content` key-value pair will be added
        // to the database by TransportServer.
        SchoolJson json = gson.fromJson(content, SchoolJson.class);
        try {
            new SchoolAPI(json);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("invalid school json content: " + e);
        }

        SetSchoolJson response = new SetSchoolJson();
        response.opcode = command.opcode;
        response.userId = command.userId;
        response.returnCode = ReturnCode.SUCCESS;
        response.inputPayload = command.inputPayload;
        response.outputPayload = response.new OutputPayload();
        
        return response;
    }

}
