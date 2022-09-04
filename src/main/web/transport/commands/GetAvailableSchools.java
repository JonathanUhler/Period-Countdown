package web.transport.commands;


import school.SchoolAPI;
import user.UserAPI;
import java.util.Map;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;


public class GetAvailableSchools extends Command {
	
	public class InputPayload {
	}

	public class OutputPayload {
		@SerializedName("CurrentSchool")
		public String currentSchool;
		@SerializedName("AvailableSchools")
		public List<String> availableSchools;
	}

	@SerializedName("InputPayload")
	public GetAvailableSchools.InputPayload inputPayload;

	@SerializedName("OutputPayload")
	public GetAvailableSchools.OutputPayload outputPayload;


	@Override
	public String process(String request, SchoolAPI schoolAPI, UserAPI userAPI) throws JsonSyntaxException {
		Gson gson = new Gson();
		GetAvailableSchools command = gson.fromJson(request, GetAvailableSchools.class);
		
		String opcode = command.opcode;
		String userID = command.userID;

		String currentSchool = userAPI.getSchoolFile();
		List<String> availableSchools = userAPI.getAvailableSchools();

		GetAvailableSchools response = new GetAvailableSchools();
		response.outputPayload = response.new OutputPayload();
		response.opcode = opcode;
		response.userID = userID;
		response.returnCode = Command.SUCCESS;
		response.outputPayload.currentSchool = currentSchool;
		response.outputPayload.availableSchools = availableSchools;

		return gson.toJson(response);
	}

}
