// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SSLServer.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package web.transport;


import util.Log;
import school.SchoolAPI;
import user.UserAPI;
import user.UserJson;
import web.transport.commands.*;
import javax.net.ssl.SSLSocket;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.FileNotFoundException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class SSLServer
//
// Example server for an SSL connection. Runs on 127.0.0.1 unless otherwise specified by the second
// constructor.
//
// Notable threads--
//
//  private void add(Socket) --> clientThread:   runs the SSLServer.listenOnClient(SSLClientSock) method
//                                               so the SSLServer.accept() method can run at the same
//                                               time
//
public class SSLServer {
	
	public static final String IP_ADDR = "127.0.0.1";
	public static final int PORT = 9000;
	public static final int BACKLOG = 50;
	public static final String[] PROTOCOLS = new String[] {"TLSv1.2", "TLSv1.1", "TLSv1"};
    public static final String[] CIPHER_SUITES = new String[] {"TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA",
															   "TLS_RSA_WITH_AES_128_CBC_SHA",
															   "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
															   "TLS_RSA_WITH_AES_128_CBC_SHA256",
															   "TLS_RSA_WITH_AES_128_GCM_SHA256",
															   "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
															   "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
															   "TLS_RSA_WITH_AES_256_CBC_SHA",
															   "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
															   "TLS_RSA_WITH_AES_256_CBC_SHA256",
															   "TLS_RSA_WITH_AES_256_GCM_SHA384",
															   "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
															   "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"};
	

	private ArrayList<SSLClientSock> clientConnections;
	private SSLServerSock serverSocket;
	private String ip;
	private int port;


	// ----------------------------------------------------------------------------------------------------
	// public SSLServer
	//
	public SSLServer() {
		this(SSLServer.IP_ADDR, SSLServer.PORT);
	}
	// end: public SSLServer


	// ----------------------------------------------------------------------------------------------------
	// public SSLServer
	//
	// Arguments--
	//
	//  ip:   the ip to start the server on
	//
	//  port: the port to start the server on
	//
	public SSLServer(String ip, int port) {
		this.ip = ip;
		this.port = port;
		
		this.clientConnections = new ArrayList<>();
		
		this.serverSocket = new SSLServerSock();
		this.serverSocket.bind(this.ip, this.port, SSLServer.BACKLOG,
							   SSLServer.PROTOCOLS, SSLServer.CIPHER_SUITES);
		
		Thread acceptThread = new Thread(() -> this.accept());
		acceptThread.start();
	}
	// end: public SSLServer


	// ====================================================================================================
	// private void accept
	//
	// Accepts and adds incoming client connections
	//
	private void accept() {
		while (true) {
			//Log.stdout(Log.INFO, "SSLServer", "accept :: ready to handle incoming connection");
			SSLSocket clientConnection = this.serverSocket.accept();
			if (clientConnection == null) {
				Log.stdout(Log.FATAL, "SSLServer", "null returned by SSLServerSock.accept(). " +
						   "The SSLServerSock was probably initialized incorrectly. Try verifying " +
						   "you have the correct keyStore file and password set");
				System.exit(Log.FATAL);
			}

			this.add(clientConnection);
		}
	}
	// end: private void accept


	// ====================================================================================================
	// private void add
	//
	// Adds a new client connection, spawns threads to handle communication with that client, and sends
	// the new client the current state of the board and their assigned color
	//
	// Arguments--
	//
	//  clientConnection: a java Socket object for the client that connected
	//
	private void add(SSLSocket clientConnection) {
		if (clientConnection == null)
			return;

		SSLClientSock clientSocket = new SSLClientSock(clientConnection);
		clientSocket.connect(this.ip, this.port, SSLServer.PROTOCOLS, SSLServer.CIPHER_SUITES);
		
		this.clientConnections.add(clientSocket);
		Thread clientThread = new Thread(() -> this.listenOnClient(clientSocket));
		clientThread.start();
	}
	// end: private void add


	// ====================================================================================================
	// private void listenOnClient
	//
	// Listens on a specific client
	//
	// Arguments--
	//
	//  clientSocket: the ClientSock object to listen to
	//
	private void listenOnClient(SSLClientSock clientSocket) {
		while (true) {
			//Log.stdout(Log.INFO, "SSLServer", "listenOnClient :: ready to process message from " + clientSocket);
			String recv = this.serverSocket.recv(clientSocket);
			if (recv == null) {
				this.clientConnections.remove(clientSocket);
				return; // Ignore this client once it has disconnected
			}

			Log.stdlog(Log.INFO, "SSLServer", "Received information from client");
			Log.stdlog(Log.INFO, "SSLServer", "\t" + recv);
			
			String response = this.handleRequest(recv);
			this.send(response, clientSocket);
		}
	}
	// end: private void listenOnClient


	// ====================================================================================================
	// private String handleRequest
	//
	// Handles an incoming request
	//
	// Arguments--
	//
	//  recv: the received String
	//
	// Returns--
	//
	//  A formatted message indicating the response to the request. This is in the format:
	//
	//   {
	//     "Opcode": <OPCODE SENT IN>,
	//     "UserID": <USER ID SENT IN>,
	//     "ReturnCode": <RETURN CODE>,
	//     "OutputPayload": {
	//       <PARAMETERS>
	//     }
	//   }
	//
	private String handleRequest(String recv) {
		Gson gson = new Gson();

		// Parse the command to check for Opcode and UserID
		GsonVerifier abstractCommand = null;
		try {
			abstractCommand = gson.fromJson(recv, GsonVerifier.class);
		}
		catch (RuntimeException e) {
			return Command.returnErr("request cannot be parsed as GsonVerifier: " + e, "", "", Command.ERR_PARSE);
		}

		if (abstractCommand.opcode == null ||
			abstractCommand.userID == null) {
			return Command.returnErr("request is missing Opcode or UserID", "", "", Command.ERR_KEY);
		}

		String opcode = abstractCommand.opcode;
		String userID = abstractCommand.userID;
		
		UserAPI userAPI = null;
		SchoolAPI schoolAPI = null;

		// If there is no user ID, use the default User.json file for getting data
		if (userID.equals("")) {
			try {
				userAPI = new UserAPI(UserJson.DEFAULT_FILE, true);
			}
			catch (FileNotFoundException | IllegalArgumentException e) {
				return Command.returnErr("exception when creating UserAPI: " + e, opcode, userID, Command.ERR_INIT);
			}
		}
		else {
			/* MARK: Access data base here
		    1) Check if userID is a key in the database
			   a) If it is: read the json string and parse as a UserJson object
			   b) If not: store the User.json template, then do the read
			2) Initialize the UserAPI object with the UserJson object
			*/
			
			/* MARK: only until database is set up */
			return Command.returnErr("user IDs are not yet supported", opcode, userID, Command.ERR_INIT);
		}

		// Load school file, which is the same regardless of the user's User.json file
		try {
			schoolAPI = new SchoolAPI(userAPI.getSchoolFile(), userAPI.attemptGetDays());
		}
		catch (FileNotFoundException | IllegalArgumentException e) {
			return Command.returnErr("exception when creating SchoolAPI: " + e, opcode, userID, Command.ERR_INIT);
		}

		// By this point, the command is guaranteed to have an opcode and userID field
		try {			
			switch (opcode) {
			case Command.GET_AVAILABLE_SCHOOLS:
				return new GetAvailableSchools().process(recv, schoolAPI, userAPI);
			case Command.GET_NEXT_UP_LIST:
				return new GetNextUpList().process(recv, schoolAPI, userAPI);
			case Command.GET_PERIOD_NUMBERS:
				return new GetPeriodNumbers().process(recv, schoolAPI, userAPI);
			case Command.GET_TIME_REMAINING:
				return new GetTimeRemaining().process(recv, schoolAPI, userAPI);
			case Command.GET_USER_PERIOD:
				return new GetUserPeriod().process(recv, schoolAPI, userAPI);
			case Command.LOGIN_USER:
				return new LoginUser().process(recv, schoolAPI, userAPI);
			case Command.SET_CURRENT_SCHOOL:
				return new SetCurrentSchool().process(recv, schoolAPI, userAPI);
			case Command.SET_NEXT_UP:
				return new SetNextUp().process(recv, schoolAPI, userAPI);
			case Command.SET_USER_PERIOD:
				return new SetUserPeriod().process(recv, schoolAPI, userAPI);
			default:
				return Command.returnErr("undefined command requested", opcode, userID, Command.ERR_INVALID);
			}
		}
		catch (JsonSyntaxException e) {
			return Command.returnErr("specific command cannot be parsed: " + e, opcode, userID, Command.ERR_PARSE);
		}
	}
	// end: private String handleRequest


	// ====================================================================================================
	// private void send
	//
	// Send a message to the given client
	//
	// Arguments--
	//
	//  messasge: the message to send
	//
	private void send(String message, SSLClientSock clientSocket) {
		// The SSLClientSock objects here allow use of the IN and OUT buffers to read/write messages. It is up to
		// the actual client on the client-side to call the .recv() method of ClientSock in order to receive the
		// data sent by the server.
		if (clientSocket != null)
			this.serverSocket.send(message, clientSocket);
	}
	// end: private void send
	

	// ====================================================================================================
	// private void sendAll
	//
	// Send a message to all connected clients
	//
	// Arguments--
	//
	//  messasge: the message to send
	//
	private void sendAll(String message) {
		// Send a message to all clients based on the SSLClientSock representations. The SSLClientSock objects
		// here allow use of the IN and OUT buffers to read/write messages. It is up to the actual client on the
		// client-side to call the .recv() method of ClientSock in order to receive the data sent by the server.
		for (SSLClientSock clientSocket : this.clientConnections) {
			if (clientSocket != null)
				this.serverSocket.send(message, clientSocket);
		}
	}
	// end: private void sendAll

}
// end: public class SSLServer
