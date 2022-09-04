package web.transport.commands;


import util.Tools;
import school.SchoolAPI;
import user.UserAPI;
import java.util.Map;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;


public class GetNextUpList extends Command {

	public class InputPayload {
	}
	
	public class OutputPayload {
		@SerializedName("NextUp")
		public String nextUp;
		@SerializedName("NextUpList")
		public List<String> nextUpList;
	}
	
	@SerializedName("InputPayload")
	public InputPayload inputPayload;

	@SerializedName("OutputPayload")
	public OutputPayload outputPayload;


	@Override
	public String process(String request, SchoolAPI schoolAPI, UserAPI userAPI) throws JsonSyntaxException {
		Gson gson = new Gson();
		GetNextUpList command = gson.fromJson(request, GetNextUpList.class);
		
		String opcode = command.opcode;
		String userID = command.userID;

		String nextUp = userAPI.getNextUp();
		List<String> nextUpList = Tools.getNextUpList(schoolAPI, userAPI);

		GetNextUpList response = new GetNextUpList();
		response.outputPayload = response.new OutputPayload();
		response.opcode = opcode;
		response.userID = userID;
		response.returnCode = Command.SUCCESS;
		response.outputPayload.nextUp = nextUp;
		response.outputPayload.nextUpList = nextUpList;

		return gson.toJson(response);
	}

}
