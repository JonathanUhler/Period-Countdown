package web.transport.commands;


import school.SchoolAPI;
import user.UserAPI;
import java.util.Map;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;


public class SetNextUp extends Command {

	public class InputPayload {
		@SerializedName("NextUp")
		public String nextUp;
	}
	
	public class OutputPayload {
	}

	@SerializedName("InputPayload")
	public InputPayload inputPayload;

	@SerializedName("OutputPayload")
	public OutputPayload outputPayload;


	@Override
	public String process(String request, SchoolAPI schoolAPI, UserAPI userAPI) throws JsonSyntaxException {
		Gson gson = new Gson();
		SetNextUp command = gson.fromJson(request, SetNextUp.class);

		String opcode = command.opcode;
		String userID = command.userID;

		if (command.inputPayload == null ||
			command.inputPayload.nextUp == null)
			return Command.returnErr("SetNextUp missing keys", opcode, userID, Command.ERR_KEY);

		String nextUp = command.inputPayload.nextUp;
		userAPI.setNextUp(nextUp);

		SetNextUp response = new SetNextUp();
		response.outputPayload = response.new OutputPayload();
		response.opcode = opcode;
		response.userID = userID;
		response.returnCode = Command.SUCCESS;

		return gson.toJson(response);
	}

}
