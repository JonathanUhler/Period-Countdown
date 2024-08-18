import os
import sys
import logging
from logging import FileHandler, Formatter
from tempfile import NamedTemporaryFile
from argparse import ArgumentParser, Namespace
from typing import Final
from flask import Flask
from pnet.secure.psslclientsocket import PSSLClientSocket


logger: Final = logging.getLogger(__name__)
server: Final = Flask(__name__)
transport_client: Final = None


@server.route("/", methods = ["GET"])
def index() -> str:
    return "Hello from pc_server.py!"


def _load_properties(properties_file: str) -> dict:
    properties: dict = {}
    with open(properties_file, "r", encoding = "utf-8") as f:
        for line in f:
            line = line.strip()
            if (not line or line.startswith("#") or line.startswith("//")):
                continue
            key, value = line.split("=", 1)
            properties[key.strip()] = value.strip()
    return properties


def _write_pid_file(properties: dict) -> None:
    pid_path: str = properties.get("server.pidFile")
    pid: int = os.getpid()
    if (pid_path is None):
        logger.warning(f"server.pidFile is not defined, pid is {pid}")
        return

    pid_dir: str = os.path.dirname(pid_path)
    pid_filename, pid_ext = os.path.splitext(os.path.basename(pid_path))
    try:
        with NamedTemporaryFile(delete = True,
                                mode = "w+",
                                prefix = pid_filename,
                                suffix = pid_ext,
                                dir = pid_dir) as pid_file:
            pid_file.write(str(pid))
    except OSError as e:
        logger.critical(f"cannot write server.pidFile: {e}")
        sys.exit(1)


def _set_log_file(properties: dict) -> None:
    log_file: str = properties.get("server.logFile")
    if (log_file is None):
        logger.warning("server.logFile is not defined")
        return

    logger.handlers.clear()
    fh: FileHandler = FileHandler(log_file)
    format_str: str = "%(asctime)s %(levelname)s %(filename)s %(funcName)s %(message)s"
    fh.setFormatter(Formatter(format_str))
    logger.addHandler(fh)
    logger.setLevel(logging.DEBUG)


def _connect_transport_client(properties: dict) -> None:
    transport_ip: str = properties.get("transport.ip")
    transport_port: int = properties.get("transport.port")
    transport_cafile: str = properties.get("transport.caFile")
    if (transport_ip is None):
        logger.critical("invalid transport.ip: found None")
        sys.exit(1)
    try:
        transport_port = int(transport_port)
    except ValueError as e:
        logger.critical(f"invalid transport.port: {e}")
        sys.exit(1)
    if (transport_cafile is None):
        logger.critical("invalid transport.caFile: found None")
        sys.exit(1)

    global transport_client
    try:
        transport_client = PSSLClientSocket(transport_cafile)
        transport_client.connect(transport_ip, transport_port)
    except OSError as e:
        logger.critical(f"network error while attempting to connect to transport: {e}")
        sys.exit(1)


def _get_host(properties: dict) -> tuple:
    ip: str = properties.get("server.ip")
    port: int = properties.get("server.port")
    if (ip is None):
        logger.critical("invalid server.ip: found None")
        sys.exit(1)
    try:
        port = int(port)
    except ValueError as e:
        logger.critical(f"invalid server.port: {e}")
        sys.exit(1)
    return (ip, port)


def _get_ssl_context(properties: dict) -> tuple:
    cafile: str = properties.get("server.caFile")
    keyfile: str = properties.get("server.privKey")
    if (cafile is None):
        logger.critical("invalid server.caFile: found None")
        sys.exit(1)
    if (keyfile is None):
        logger.critical("invalid server.privKey: found None")
        sys.exit(1)
    return (cafile, keyfile)


def run(properties_file: str) -> Flask:
    properties: dict = _load_properties(properties_file)
    _set_log_file(properties)
    _write_pid_file(properties)
    _connect_transport_client(properties)

    server_ip, server_port = _get_host(properties)
    ssl_context = _get_ssl_context(properties)
    try:
        server.run(host = server_ip,
                   port = server_port,
                   ssl_context = ssl_context)
    except Exception as e:
        log.error(f"uncaught exception thrown by run: {e}")

    return server


if (__name__ == "__main__"):
    parser: ArgumentParser = ArgumentParser()
    parser.add_argument("properties_file")
    args: Namespace = parser.parse_args()

    run(args.properties_file)
