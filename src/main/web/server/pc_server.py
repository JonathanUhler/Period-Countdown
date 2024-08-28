"""
Period countdown web server implementation in Python.

Author: Jonathan Uhler
"""


import os
import sys
import json
import logging
from logging import FileHandler, Formatter
from tempfile import NamedTemporaryFile
from argparse import ArgumentParser, Namespace
from typing import Final
import requests
import flask
from flask import Flask, request
import flask_login
from flask_login import LoginManager
from oauthlib.oauth2 import WebApplicationClient
from pnet.secure.psslclientsocket import PSSLClientSocket
from user import User
import commands
from commands import Opcode, ReturnCode


OPENID_URL: Final = "https://accounts.google.com/.well-known/openid-configuration"


properties: Final = None
logger: Final = logging.getLogger(__name__)
server: Final = Flask(__name__)
login_manager: Final = LoginManager()
oauth_client: Final = None
transport_client: Final = None


@login_manager.user_loader
def load_user(sub: str) -> User:
    """
    Loads a database user from a given identifier.

    Arguments:
     sub (str): the unique identifier of the database user.

    Returns:
     User: the database user for the given identifier.
    """

    return User(sub)


@server.errorhandler(500)
def error_500(message: str = "An internal server error occurred.") -> str:
    """
    Routing method for 500 errors.

    Arguments:
     message (str): an optional message that describes the error. By default, this is a generic
                    string about "an internal server error".

    Returns:
     str: a rendered template for the 500 error.
    """

    return flask.render_template("500.html", message = message), 500


@server.route("/", methods = ["GET"])
def index() -> str:
    """
    Routing method for the index page.

    The index page only supports GET requests. The content servered will change depending on
    whether the current database user is logged in and authenticated with OAuth 2. For
    users who are not logged in, a generic home page is displayed. Otherwise, timing and class
    information is requested from the transport and formatted in the rendered template.

    Return:
     str: a rendered template of the index page.
    """

    sub: str = flask_login.current_user.get_id()
    if (sub is None):
        return flask.render_template("index.html", authenticated = False)

    time_remaining_resp: dict = commands.send(transport_client, Opcode.GET_TIME_REMAINING, sub)
    current_period_resp: dict = commands.send(transport_client, Opcode.GET_CURRENT_PERIOD, sub)
    user_settings_resp: dict = commands.send(transport_client, Opcode.GET_USER_SETTINGS, sub)
    if (time_remaining_resp is None):
        logger.error("malformed GET_TIME_REMAINING from transport")
        return error_500("An internal error occurred while gathering your timing data.")
    if (time_remaining_resp["ReturnCode"] != ReturnCode.SUCCESS.name):
        logger.error(f"unsuccessful GET_TIME_REMAINING from transport: {time_remaining_resp}")
        return error_500("An internal error occurred while processing your timing data.")
    if (current_period_resp is None):
        logger.error("malformed GET_CURRENT_PERIOD from transport")
        return error_500("An internal error occurred while gathering your class data.")
    if (current_period_resp["ReturnCode"] != ReturnCode.SUCCESS.name):
        logger.error(f"unsuccessful GET_CURRENT_PERIOD from transport: {current_period_resp}")
        return error_500("An internal error occurred while processing your class data.")
    if (user_settings_resp is None):
        logger.error("malformed GET_USER_SETTINGS from transport")
        return error_500("An internal error occurred while gathering your data.")
    if (user_settings_resp["ReturnCode"] != ReturnCode.SUCCESS.name):
        logger.error(f"unsuccessful GET_USER_SETTINGS from transport: {user_settings_resp}")
        return error_500("An internal error occurred while processing your data.")

    time_remaining: str = time_remaining_resp["OutputPayload"]["TimeRemaining"]
    end_time: str = time_remaining_resp["OutputPayload"]["EndTime"]
    expire_time: str = time_remaining_resp["OutputPayload"]["ExpireTime"]
    current_name: str = current_period_resp["OutputPayload"]["CurrentName"]
    current_status: str = current_period_resp["OutputPayload"]["CurrentStatus"]
    next_name: str = current_period_resp["OutputPayload"]["NextName"]
    next_duration: str = current_period_resp["OutputPayload"]["NextDuration"]
    theme: str = user_settings_resp["OutputPayload"]["Theme"]
    font: str = user_settings_resp["OutputPayload"]["Font"]

    theme = hex(int(theme))[2:].zfill(6)

    return flask.render_template("index.html",
                                 authenticated = True,
                                 time_remaining = time_remaining,
                                 end_time = end_time,
                                 expire_time = expire_time,
                                 current_period = f"{current_name} | {current_status}",
                                 next_period = next_name,
                                 next_period_duration = next_duration,
                                 theme = f"#{theme}",
                                 font = font)


def settings_post(sub: str) -> str:
    pass


def settings_get(sub: str) -> str:
    user_periods_resp: dict = commands.send(transport_client, Opcode.GET_USER_PERIODS, sub)
    user_settings_resp: dict = commands.send(transport_client, Opcode.GET_USER_SETTINGS, sub)
    if (user_periods_resp is None):
        logger.error("malformed GET_USER_PERIODS from transport")
        return error_500("An internal error occurred while gathering your course settings.")
    if (user_periods_resp["ReturnCode"] != ReturnCode.SUCCESS.name):
        logger.error(f"unsuccessful GET_USER_PERIODS from transport: {user_periods_resp}")
        return error_500("An internal error occurred while processing your course settings.")
    if (user_settings_resp is None):
        logger.error("malformed GET_USER_SETTINGS from transport")
        return error_500("An internal error occurred while gathering your course settings.")
    if (user_settings_resp["ReturnCode"] != ReturnCode.SUCCESS.name):
        logger.error(f"unsuccessful GET_USER_SETTINGS from transport: {user_settings_resp}")
        return error_500("An internal error occurred while processing your course settings.")

    user_periods: dict = user_periods_resp["OutputPayload"]["UserPeriods"]
    theme: str = user_settings_resp["OutputPayload"]["Theme"]
    font: str = user_settings_resp["OutputPayload"]["Font"]
    school_json: str = user_settings_resp["OutputPayload"]["SchoolJson"]
    available_schools: list = user_settings_resp["OutputPayload"]["AvailableSchools"]

    return flask.render_template("settings.html",
                                 user_periods = user_periods,
                                 theme = theme,
                                 font = font,
                                 school_json = school_json,
                                 available_schools = available_schools)


@server.route("/settings", methods = ["GET", "POST"])
def settings() -> str:
    """
    Routing method for the settings page.

    The settings page supports GET requests to gather existing settings information and POST
    requests with a JSON payload to update settings. This method will only serve users that
    are logged in.

    Return:
     str: a rendered template of the settings page.
    """

    sub: str = flask_login.current_user.get_id()
    if (sub is None):
        return flask.redirect(flask.url_for("index"))

    if (flask.request.method == "POST"):
        return settings_post(sub)
    return settings_get(sub)


def _get_openid_configuration(key: str) -> str:
    """
    Returns a value from Google's Open ID data for the provided key.

    Arguments:
     key (str): the key in the Open ID data to get.

    Returns:
     str: the value of the specified key. If the key does not exist or the Open ID request failed
          then `None` will be returned.
    """

    try:
        openid_configuration: dict = requests.get(OPENID_URL, timeout = 30).json()
        return openid_configuration.get(key)
    except requests.exceptions.RequestException as e:
        logger.error(f"error in openid_configuration GET: {e}")
        return None


@server.route("/login", methods = ["GET"])
def login() -> str:
    """
    Routing method for the login request page.

    Returns:
     str: a redirect to the OAuth login page, if the OAuth login request URI can be found.
    """

    authorization_endpoint: str = _get_openid_configuration("authorization_endpoint")
    if (authorization_endpoint is None):
        logger.error("'authorization_endpoint' is missing from openid_configuration")
        return error_500()

    request_uri = oauth_client.prepare_request_uri(
        authorization_endpoint,
        redirect_uri = flask.request.base_url + "/callback",
        scope = ["openid", "email"],
        prompt = "consent"
    )

    return flask.redirect(request_uri)


@server.route("/login/callback", methods = ["GET"])
def login_callback() -> str:
    """
    Routing method for the callback after an user has logged in with OAuth 2.

    Returns:
     str: a redirect to the index page if the user was successfully authenticated.
    """

    authorization_code: str = flask.request.args.get("code")
    token_endpoint: str = _get_openid_configuration("token_endpoint")
    if (authorization_code is None):
        logging.error("'authorization_code' is missing")
        return error_500()
    if (token_endpoint is None):
        logging.error("'token_endpoing' is miggin from openid_configuration")
        return error_500()

    token_url, headers, body = oauth_client.prepare_token_request(
        token_endpoint,
        authorization_response = flask.request.url,
        redirect_url = flask.request.base_url,
        code = authorization_code
    )

    oauth_token: str = properties.get("server.oauthToken")
    oauth_secret: str = properties.get("server.oauthSecret")
    if (oauth_token is None):
        logger.error("invalid server.oauthToken: found None")
        return error_500()
    if (oauth_secret is None):
        logger.error("invalid server.oauthSecret: found None")
        return error_500()
    try:
        token_response = requests.post(
            token_url,
            timeout = 30,
            headers = headers,
            data = body,
            auth = (oauth_token, oauth_secret)
        )
    except requests.exceptions.RequestException as e:
        logging.error(f"attempt to POST token_response failed: {e}")
        return error_500()

    oauth_client.parse_request_body_response(json.dumps(token_response.json()))
    userinfo_endpoint: str = _get_openid_configuration("userinfo_endpoint")
    uri, headers, body = oauth_client.add_token(userinfo_endpoint)
    try:
        userinfo_response: str = requests.get(uri, timeout = 30, headers = headers, data = body)
    except requests.exceptions.RequestException as e:
        logging.error(f"attempt to GET userinfo_response failed: {e}")
        return error_500()

    userinfo_json: dict = userinfo_response.json()
    if ("sub" not in userinfo_json):
        logging.error("'sub' missing from userinfo response")
        return error_500()
    sub: str = userinfo_json["sub"]
    user: User = User(sub)
    flask_login.login_user(user)
    return flask.redirect(flask.url_for("index"))


@server.route("/logout")
def logout() -> str:
    """
    Routing method for the logout page.

    Returns:
     str: a redirect to the index page after the user has been logged out.
    """

    flask_login.logout_user()
    return flask.redirect(flask.url_for("index"))


def _load_properties(properties_file: str) -> dict:
    """
    Loads the properties file used by the server as a dictionary.

    Arguments:
     properties_file (str): the path to the `.properties` file.

    Returns:
     dict: the parsed properties.
    """

    global properties
    properties = {}
    with open(properties_file, "r", encoding = "utf-8") as f:
        for line in f:
            line = line.strip()
            if (not line or line.startswith("#") or line.startswith("//")):
                continue
            key, value = line.split("=", 1)
            properties[key.strip()] = value.strip()


def _write_pid_file() -> None:
    """
    Writes the process ID of the server to the file specified in the global properties.

    If no PID file path is specified in the properties, a logger warning is printed but the process
    is allowed to continue.
    """

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


def _set_log_file() -> None:
    """
    Sets a file handler on the logger used by the server from the log file specified in the
    global properties.

    If no log file is specified in the properties, a logger warning is printed to the standard
    output and the logger continues to use the default console handler.
    """

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


def _connect_oauth_client() -> None:
    """
    Initializes the OAuth 2 client used to authenticate client connections during login.

    If any error ocurrs during initialization, or no OAuth token is specified in the global
    properties, a fatal error is raised and the process exits.
    """

    oauth_token: str = properties.get("server.oauthToken")
    if (oauth_token is None):
        logger.critical("invalid server.oauthToken: found None")
        sys.exit(1)

    global oauth_client
    oauth_client = WebApplicationClient(oauth_token)


def _connect_transport_client() -> None:
    """
    Initializes the client socket used to communicate with the Java transport.

    If any error occurs during the socket setup, or transport socket information is missing,
    a fatal error is raised and the process exits.
    """

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


def _get_host() -> tuple:
    """
    Returns the host information for the server.

    Returns:
     tuple: the web server IP address and port as a tuple in that order.
    """

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


def _get_ssl_context() -> tuple:
    """
    Returns the SSL context for the server.

    Returns:
     tuple: the certificate authority file, private key file, and Flask secret as a tuple in
            that order.
    """

    cafile: str = properties.get("server.caFile")
    keyfile: str = properties.get("server.privKey")
    secret: str = properties.get("server.secret")
    if (cafile is None):
        logger.critical("invalid server.caFile: found None")
        sys.exit(1)
    if (keyfile is None):
        logger.critical("invalid server.privKey: found None")
        sys.exit(1)
    if (secret is None):
        logger.critical("invalid server.secret: found None")
        sys.exit(1)
    return (cafile, keyfile, secret)


def run(properties_file: str) -> Flask:
    """
    Starts the web server and associated services.

    Arguments:
     properties_file (str): the path to the server `.properties` file.

    Returns:
     Flask: the web server.
    """

    _load_properties(properties_file)
    _set_log_file()
    _write_pid_file()
    _connect_oauth_client()
    _connect_transport_client()

    login_manager.init_app(server)

    server_ip, server_port = _get_host()
    cafile, keyfile, secret= _get_ssl_context()
    server.secret_key = secret
    try:
        server.run(host = server_ip,
                   port = server_port,
                   ssl_context = (cafile, keyfile))
    except RuntimeError as e:
        logger.error(f"uncaught exception thrown by run: {e}")

    return server


if (__name__ == "__main__"):
    parser: ArgumentParser = ArgumentParser()
    parser.add_argument("properties_file")
    args: Namespace = parser.parse_args()

    run(args.properties_file)
