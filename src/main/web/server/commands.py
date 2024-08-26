import json
from enum import Enum, auto
from pnet.secure.psslclientsocket import PSSLClientSocket


class Opcode(Enum):
    GET_TIME_REMAINING = auto()



class ReturnCode(Enum):
    SUCCESS = auto()
    SIGNED_OUT = auto()
    ERR_PARSE = auto()
    ERR_PAYLOAD = auto()
    ERR_RESPONSE = auto()
    ERR_GENERIC = auto()



def send(transport_client: PSSLClientSocket,
         opcode: Opcode,
         user_id: str,
         input_payload: dict = None):
    message: dict = {}
    message["Opcode"] = opcode.name
    if (user_id is not None):
        message["UserID"] = user_id
    if (input_payload is not None):
        message["InputPayload"] = input_payload

    message_str: str = json.dumps(message)
    transport_client.send(message_str)

    response_str: str = transport_client.srecv()
    try:
        response: dict = json.loads(response_str)
    except json.decoder.JSONDecodeError:
        return None

    if ("Opcode" not in response or "ReturnCode" not in response):
        return None
    return response
