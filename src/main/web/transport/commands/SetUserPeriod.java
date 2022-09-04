package web.transport.commands;


import school.SchoolAPI;
import user.UserAPI;
import user.UserJson;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;


public class SetUserPeriod extends Command {

	public class InputPayload {
		@SerializedName("Type")
		public String type;
		@SerializedName("Name")
		public String name;
		@SerializedName("Teacher")
		public String teacher;
		@SerializedName("Room")
		public String room;
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
		SetUserPeriod command = gson.fromJson(request, SetUserPeriod.class);

		String opcode = command.opcode;
		String userID = command.userID;

		if (command.inputPayload == null ||
			command.inputPayload.type == null ||
			command.inputPayload.name == null ||
			command.inputPayload.teacher == null ||
			command.inputPayload.room == null)
			return Command.returnErr("SetUserPeriod missing keys", opcode, userID, Command.ERR_KEY);

		String type = command.inputPayload.type;
		String name = command.inputPayload.name;
		String teacher = command.inputPayload.teacher;
		String room = command.inputPayload.room;
		Map<String, String> value = new HashMap<>();
		value.put(UserJson.NAME, name);
		value.put(UserJson.TEACHER, teacher);
		value.put(UserJson.ROOM, room);
		
		userAPI.setPeriod(type, value);

		SetUserPeriod response = new SetUserPeriod();
		response.outputPayload = response.new OutputPayload();
		response.opcode = opcode;
		response.userID = userID;
		response.returnCode = Command.SUCCESS;

		return gson.toJson(response);
	}

}
