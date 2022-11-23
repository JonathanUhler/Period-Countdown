# +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
# commands.py
# Period-Countdown
#
# Created by Jonathan Uhler
# +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


import log
import crc
from typing import Final
from socket import SocketType


# *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
# ADD NEW STATUS CODES AND OPCODES HERE. DON'T FORGET TO ADD ROUTINES TO THE TRANSPORT CODE!!!
#
SUCCESS: Final = "SUCCESS"
ERR_PARSE: Final = "ERR_PARSE"
ERR_KEY: Final = "ERR_KEY"
ERR_INVALID: Final = "ERR_INVALID"
ERR_INIT: Final = "ERR_INIT"
ERR_CRC: Final = "ERR_CRC"
#
GET_USER_PERIOD: Final = "GetUserPeriod"
GET_PERIOD_NUMBERS: Final = "GetPeriodNumbers"
GET_TIME_REMAINING: Final = "GetTimeRemaining"
GET_AVAILABLE_SCHOOLS: Final = "GetAvailableSchools"
GET_NEXT_UP_LIST: Final = "GetNextUpList"
SET_USER_PERIOD: Final = "SetUserPeriod"
SET_CURRENT_SCHOOL: Final = "SetCurrentSchool"
SET_NEXT_UP: Final = "SetNextUp"
#
# *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=


# ====================================================================================================
# def return_err
#
# Creates a formatted error payload
#
# Arguments--
#
#  log_message: a message to print to the logfile (and also include in the output payload of the
#               error response)
#
#  opcode:      the opcode of the failed command
#
#  user_id:     the UID of the user who requested the failed command
#
#  return_code: the status code of the command
#
# Returns--
#
#  A formatted JSON payload for the error in the following format:
#
#    {
#      "Opcode": <OPCODE>,
#      "UserID": <USER ID>,
#      "ReturnCode": <RETURN CODE>,
#      "OutputPayload": {
#        "Message": <LOG MESSAGE>
#      }
#    }
#
def return_err(log_message: str, opcode: str, user_id: str, return_code: str) -> str:
    log.stdlog(log.ERROR, "commands", log_message)
    return f"{{\"Opcode\": \"{opcode}\", \"UserID\": \"{user_id}\", " + \
        f"\"ReturnCode\": \"{return_code}\", \"OutputPayload\": {{\"Message\": \"{log_message}\"}}}}"
# end: def return_err


# ====================================================================================================
# def construct_msg
#
# Constructs a message with an input payload to send to the Java transport
#
# Arguments--
#
#  opcode:        the opcode of the command being sent
#
#  user_id:       the UID from google of the user if one is logged in. If the browser sending the request
#                 does not have a user logged in with google, then this field should be an empty str ("")
#
#  input_payload: a hashmap of values comprising the input payload of the command
#
# Returns--
#
#  The formatted message, as a single JSON-like string
#
def construct_msg(opcode: str, user_id: str, input_payload: dict[str, str]) -> str:
    if (not isinstance(opcode, str) or
        not isinstance(user_id, str) or
        not isinstance(input_payload, dict)):
        log.stdlog(log.ERROR, "commands", "construct_msg called with invalid arguments")
        log.stdlog(log.ERROR, "commands", f"\topcode: {opcode}")
        log.stdlog(log.ERROR, "commands", f"\tuser_id: {user_id}")
        log.stdlog(log.ERROR, "commands", f"\tinput: {input_payload}")
        return f"{{\"Opcode\": \"\", \"UserID\": \"\", \"InputPayload\": {{}}}}"

    message: str = f"{{\"Opcode\": \"{opcode}\", \"UserID\": \"{user_id}\", \"InputPayload\": {{"

    for i in range(len(input_payload)):
        item: tuple = list(input_payload.items())[i]
        key: str = item[0]
        value: str = item[1]

        message += f"\"{key}\": \"{value}\""
        if (i < len(input_payload) - 1):
            message += ", "
    message += f"}}}}"

    return message
# end: def construct_msg


# ====================================================================================================
# def send
#
# Sends a message to the java transport and returns the response or handles any error
#
# Arguments--
#
#  send_socket:   a socket object pointing to the java transport
#
#  opcode:        the opcode of the command being sent
#
#  user_id:       the google UID of the user sending the command, blank ("") for generic
#
#  input_payload: the input payload of the command
#
# Returns--
#
#  A JSON-like output payload representing either the response from the java transport or an
#  error payload if an error occured
#
def send(send_socket: SocketType, opcode: str, user_id: str, input_payload: dict) -> dict:
    if (not isinstance(send_socket, SocketType) or
        not isinstance(user_id, str) or
        not isinstance(opcode, str) or
        not (input_payload == None or isinstance(input_payload, dict))):
        return return_err("send called with invalid args", "", "", ERR_INVALID)

    message: str = construct_msg(opcode, user_id, input_payload)
    message = message.replace("\n", "") # Remove any newlines that would split the message upon send

    # Add the crc and final newline to show the end of the message
    crc32: str = crc.get_for(message)
    log.stdlog(log.INFO, "commands", f"adding crc32 checksum to message: {crc32}")
    message += crc32
    message += "\n\r" # VERY important for letting java recieve message

    # Send the message
    log.stdlog(log.INFO, "commands", "sending message")
    log.stdlog(log.INFO, "commands", f"\t{message}")
    send_socket.send(bytes(message, log.ENCODING))

    # Listen for a response and check the crc
    response: str = send_socket.recv(8000).decode(log.ENCODING)
    if (not crc.check(response)):
        return return_err(f"CRC check failed (see logging from crc.py)", "", "", ERR_CRC)

    response = response.replace("\n", "") # Remove any newlines after the crc bytes
    response = response[0:len(response) - crc.NUM_NIBBLES] # Remove crc

    # Parsing and error checking on the response
    response_dict: dict
    try:
        response_dict = eval(response)
    except Exception as e:
        return return_err(f"transport response cannot be parsed as dict: {e}. Resp={response}", "", "", ERR_PARSE)

    if (not isinstance(response_dict, dict) or
        not "Opcode" in response_dict or
        not "UserID" in response_dict or
        not "ReturnCode" in response_dict or
        not "OutputPayload" in response_dict):
        return return_err(f"transport response is not a dict or is missing keys", "", "", ERR_INVALID)

    if (response_dict["ReturnCode"] != SUCCESS):
        return return_err(f"transport response has non SUCCESS return code", "", "", response_dict["ReturnCode"])

    return response_dict
# end: def send
