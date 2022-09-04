package web.transport.commands;


import school.SchoolAPI;
import user.UserAPI;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;


public class GsonVerifier extends Command {

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
		return Command.returnErr("Generic GsonVerifier command used", "", "", ERR_GENERIC);
	}

}
