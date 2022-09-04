package web.transport.commands;


import school.SchoolAPI;
import user.UserAPI;
import java.util.Map;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;


public class SetCurrentSchool extends Command {

	public class InputPayload {
		@SerializedName("CurrentSchool")
		public String currentSchool;
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
		SetCurrentSchool command = gson.fromJson(request, SetCurrentSchool.class);

		String opcode = command.opcode;
		String userID = command.userID;

		if (command.inputPayload == null ||
			command.inputPayload.currentSchool == null)
			return Command.returnErr("SetCurrentSchool missing keys", opcode, userID, Command.ERR_KEY);

		String currentSchool = command.inputPayload.currentSchool;
		userAPI.setSchoolFile(currentSchool);

		SetCurrentSchool response = new SetCurrentSchool();
		response.outputPayload = response.new OutputPayload();
		response.opcode = opcode;
		response.userID = userID;
		response.returnCode = Command.SUCCESS;

		return gson.toJson(response);
	}

}
