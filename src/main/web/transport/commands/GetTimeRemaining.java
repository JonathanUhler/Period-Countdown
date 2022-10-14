package web.transport.commands;


import util.UTCTime;
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
		@SerializedName("EndTime")
		public String endTime;
		@SerializedName("ExpireTime")
		public String expireTime;
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

		UTCTime now = UTCTime.now();
		Duration timeRemaining = schoolAPI.getTimeRemaining(now);

		GetTimeRemaining response = new GetTimeRemaining();
		response.outputPayload = response.new OutputPayload();
		response.opcode = opcode;
		response.userID = userID;
		response.returnCode = Command.SUCCESS;
		response.outputPayload.timeRemaining = timeRemaining.toString();
		response.outputPayload.endTime = timeRemaining.getEnd().toString();
		// The expiry time of the time remaining data may or may not be the same as the "End" time. It is defined
		// as the end of the current period (e.g. 23:59:59.999 for something like an "After School" period). This
		// is an useful distinction to make when the period changes (e.g. during midnight), but the time
		// remaining does not since those two periods are merged
		response.outputPayload.expireTime = schoolAPI.getCurrentPeriod(now).getEnd().toString();

		return gson.toJson(response);
	}

}
