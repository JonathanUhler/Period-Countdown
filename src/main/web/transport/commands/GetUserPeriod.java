package web.transport.commands;


import util.DateTime;
import school.SchoolAPI;
import school.SchoolPeriod;
import user.UserAPI;
import user.UserPeriod;
import java.util.Map;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;


public class GetUserPeriod extends Command {

	public class InputPayload {
		@SerializedName("Type")
		public String type;
	}
	
	public class OutputPayload {
		@SerializedName("Name")
		public String name;
		@SerializedName("Status")
		public String status;
		@SerializedName("Teacher")
		public String teacher;
		@SerializedName("Room")
		public String room;
	}
	
	@SerializedName("InputPayload")
	public InputPayload inputPayload;

	@SerializedName("OutputPayload")
	public OutputPayload outputPayload;


	@Override
	public String process(String request, SchoolAPI schoolAPI, UserAPI userAPI) throws JsonSyntaxException {
		// This command has an optional input payload. If the Type (period #) field is not given,
		// the output payload is based on the current period (if one exists). If the Type field is
		// given and is valid, then that user period is returned.

		Gson gson = new Gson();
		GetUserPeriod command = gson.fromJson(request, GetUserPeriod.class);

		String opcode = command.opcode;
		String userID = command.userID;

		SchoolPeriod schoolPeriod = schoolAPI.getCurrentPeriod(new DateTime());
		UserPeriod userPeriod = null;
		if (command.inputPayload != null && command.inputPayload.type != null) {
			String type = command.inputPayload.type;
			schoolPeriod = new SchoolPeriod(type, "", "00:00", "00:00");
		}
		userPeriod = userAPI.getPeriod(schoolPeriod);

		GetUserPeriod response = new GetUserPeriod();
		response.outputPayload = response.new OutputPayload();
		response.opcode = opcode;
		response.userID = userID;
		response.returnCode = Command.SUCCESS;
		response.outputPayload.name = userPeriod.getName();
		response.outputPayload.status = userPeriod.getStatus();
		response.outputPayload.teacher = userPeriod.getTeacher();
		response.outputPayload.room = userPeriod.getRoom();

		return gson.toJson(response);
	}

}
