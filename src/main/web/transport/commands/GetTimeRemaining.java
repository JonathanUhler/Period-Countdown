package web.transport.commands;


import util.DateTime;
import util.Duration;
import school.SchoolAPI;
import user.UserAPI;
import java.util.Map;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;


public class GetTimeRemaining extends Command {

	public class InputPayload {
	}
	
	public class OutputPayload {
		@SerializedName("TimeRemaining")
		public String timeRemaining;
	}

	@SerializedName("InputPayload")
	public InputPayload inputPayload;

	@SerializedName("OutputPayload")
	public OutputPayload outputPayload;


	@Override
	public String process(String request, SchoolAPI schoolAPI, UserAPI userAPI) throws JsonSyntaxException {
		Gson gson = new Gson();
		GetTimeRemaining command = gson.fromJson(request, GetTimeRemaining.class);
		
		String opcode = command.opcode;
		String userID = command.userID;

		Duration timeRemaining = schoolAPI.getTimeRemaining(new DateTime());

		GetTimeRemaining response = new GetTimeRemaining();
		response.outputPayload = response.new OutputPayload();
		response.opcode = opcode;
		response.userID = userID;
		response.returnCode = Command.SUCCESS;
		response.outputPayload.timeRemaining = timeRemaining.toString();

		return gson.toJson(response);
	}

}
