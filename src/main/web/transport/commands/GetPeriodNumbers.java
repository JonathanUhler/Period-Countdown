package web.transport.commands;


import school.SchoolAPI;
import user.UserAPI;
import java.util.Map;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;


public class GetPeriodNumbers extends Command {

	public class InputPayload {
	}
	
	public class OutputPayload {
		@SerializedName("PeriodNumbers")
		public List<String> periodNumbers;
	}
	
	@SerializedName("InputPayload")
	public InputPayload inputPayload;

	@SerializedName("OutputPayload")
	public OutputPayload outputPayload;


	@Override
	public String process(String request, SchoolAPI schoolAPI, UserAPI userAPI) throws JsonSyntaxException {
		Gson gson = new Gson();
		GetPeriodNumbers command = gson.fromJson(request, GetPeriodNumbers.class);
		
		String opcode = command.opcode;
		String userID = command.userID;

		List<String> periodNumbers = userAPI.getPeriodKeys();

		GetPeriodNumbers response = new GetPeriodNumbers();
		response.outputPayload = response.new OutputPayload();
		response.opcode = opcode;
		response.userID = userID;
		response.returnCode = Command.SUCCESS;
		response.outputPayload.periodNumbers = periodNumbers;

		return gson.toJson(response);
	}

}
