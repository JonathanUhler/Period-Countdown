import os
import logging
from typing import Final
from flask import Flask
from pnet.pclientsocket import PClientSocket


logger: Final = logging.getLogger(__name__)
server: Final = Flask(__name__)
transport_client: Final = PClientSocket()


@server.route("/", methods = ["GET"])
def index() -> str:
    return "Hello from pc_server.py!"


def run(transport_ip: str, transport_port: int) -> Flask:
    try:
        transport_client.connect(transport_ip, transport_port)
    except OSError as e:
        logger.error(f"network error while attempting to connect to transport: {e}")

    transport_client.send('{"Opcode": "GET_TIME_REMAINING"}')
    print(transport_client.srecv())

    server.secret_key = os.urandom(24)
    return server


if (__name__ == "__main__"):
    run("localhost", 9000)
