package web.transport.commands;


import school.SchoolAPI;
import user.UserAPI;
import java.util.Map;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;


public class LoginUser extends Command {

    public class InputPayload {
	}

    public class OutputPayload {
	}

	@SerializedName("InputPayload")
	public InputPayload inputPayload;

	@SerializedName("OutputPayload")
	public OutputPayload outputPayload;


	@Override
	public String process(String request, SchoolAPI schoolAPI, UserAPI userAPI) throws JsonSyntaxException {
		/* MARK: DO STUFF WITH THE DATA HERE */

		Gson gson = new Gson();
		LoginUser command = gson.fromJson(request, LoginUser.class);

		String opcode = command.opcode;
		String userID = command.userID;
		
		LoginUser response = new LoginUser();
		response.outputPayload = response.new OutputPayload();
		response.opcode = opcode;
		response.userID = userID;
		response.returnCode = Command.SUCCESS;

		return gson.toJson(response);
	}

}
