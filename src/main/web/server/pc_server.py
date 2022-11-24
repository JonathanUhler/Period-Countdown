# +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
# pc_server.py
# Period-Countdown
#
# Created by Jonathan Uhler
# +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


import conf
import log
import commands
from user import User
import os
import sys
import click
import ssl
import socket
import requests
import json
from typing import Final
from flask import Flask, request, render_template, redirect, url_for
from flask_login import LoginManager, current_user, login_required, login_user, logout_user, login_required
from oauthlib.oauth2 import WebApplicationClient
from flask import Flask, request, render_template


GOOGLE_AUTHORIZATION_ENDPOINT: Final = "authorization_endpoint"
GOOGLE_TOKEN_ENDPOINT: Final = "token_endpoint"
GOOGLE_USERINFO_ENDPOINT: Final = "userinfo_endpoint"
GOOGLE_AUTHORIZATION_CODE: Final = "code"
GOOGLE_SUB: Final = "sub"
GOOGLE_DISCOVERY_URL: Final = "https://accounts.google.com/.well-known/openid-configuration"


# Setup whitelist
whitelist: Final = []
with open(conf.SERVER_WHITELIST, "r") as whitelist_file:
    try:
        whitelist = json.load(whitelist_file)
        if (not isinstance(whitelist, list)):
            raise TypeError(f"whitelist file is not list, found: {type(whitelist)}")
    except Exception as e:
        log.stdlog(log.FATAL, "pc_server", f"cannot load whitelist file: {e}");
# Make sure whitelist ids are consistently strings
for uid_index in range(len(whitelist)):
    whitelist[uid_index] = str(whitelist[uid_index])

# Setup communcation links
web_server: Final = Flask(__name__)
login_manager: Final = LoginManager()
oauth_client: Final = WebApplicationClient(conf.OAUTH_TOKEN)
send_socket: Final = ssl.wrap_socket(socket.socket(socket.AF_INET, socket.SOCK_STREAM));


# ====================================================================================================
# def load_user
#
# Attempts to load the current user based on their id
#
# Arguments--
#
#  user_id: the id of the user as a string. This comes from the "sub" field of the user's google token
#
# Returns--
#
#  An User object if the user could be loaded, otherwise None
#
@login_manager.user_loader
def load_user(user_id: str) -> User:
    # An user object can be immediately returned. If this user does not exist in the backend/database,
    # the creation of an entry for this user will created based on their id (sub value) upon the
    # first request to the transport
    return User(user_id)
# end: def load_user


# ====================================================================================================
# def get_google_value
#
# Fetch the authorization endpoint URL from google to verify an user's login attempt
#
# Returns--
#
#  The authorization endpoint URL, if successful. Otherwise, returns None
#
def get_google_value(key: str) -> str:
    try:
        response = requests.get(GOOGLE_DISCOVERY_URL)
    except Exception as e:
        log.stdlog(log.ERROR, "pc_server", f"Error thrown during google discovery: {e}")
        return None

    json: dict = response.json()
    if (not key in json):
        log.stdlog(log.ERROR, "pc_server", f"{key} not in google discovery payload")
        return None

    value: str = json[key]
    if (value == None or value == ""):
        log.stdlog(log.ERROR, "pc_server", f"{key} is None or empty: value={value}")
        return None
    return value
# end: def get_google_value


# ====================================================================================================
# ERROR HANDLING
@web_server.errorhandler(403)
def err403(error) -> str:
    return render_template("403.html"), 403

@web_server.errorhandler(404)
def err405(error) -> str:
    return render_template("404.html"), 404

@web_server.errorhandler(405)
def err405(error) -> str:
    return render_template("405.html"), 405

@web_server.errorhandler(500)
def err500(error) -> str:
    return render_template("500.html", error=str(error)), 500
# end: ERROR HANDLING


# ====================================================================================================
# def index
#
# Routing method for "/". Displays everything displayed on main screen of the desktop app.
#
# Returns--
#
#  A rendered HTML page, coming from template/index.html
#
@web_server.route("/", methods = ["GET"])
def index() -> str:
    if (request.method == "GET"):
        # Get the id from the current user
        user_id: str = current_user.get_id()
        if (user_id == None):
            user_id = "" # Convert null id to empty string format
        
        # Get the current user period, time remaining in the period, and list of next classes
        user_period: dict = commands.send(send_socket, commands.GET_USER_PERIOD, user_id, {})
        time_remaining: dict = commands.send(send_socket, commands.GET_TIME_REMAINING, user_id, {})
        next_up_list: dict = commands.send(send_socket, commands.GET_NEXT_UP_LIST, user_id, {})

        # Check return codes of the commands sent
        if (user_period["ReturnCode"] != commands.SUCCESS or
            time_remaining["ReturnCode"] != commands.SUCCESS or
            next_up_list["ReturnCode"] != commands.SUCCESS):
            return err500("(index)  command response had non-SUCCESS return code")

        # If all the return codes were successful, the output payloads are guaranteed to contain the keys used
        period_name: str = user_period["OutputPayload"]["Name"]
        period_status: str = user_period["OutputPayload"]["Status"]
        time: str = time_remaining["OutputPayload"]["TimeRemaining"]
        end_time: str = time_remaining["OutputPayload"]["EndTime"]
        expire_time: str = time_remaining["OutputPayload"]["ExpireTime"]
        next_up_level: str = next_up_list["OutputPayload"]["NextUp"]
        next_up: list = next_up_list["OutputPayload"]["NextUpList"]

        # Return the rendered template
        return render_template("index.html",
                               authenticated=current_user.is_authenticated,
                               end_time=f"{end_time}",
                               expire_time=f"{expire_time}",
                               status=f"{period_name} | {period_status}",
                               time_remaining=f"{time}",
                               next_up_list=next_up if next_up_level != "Disabled" else None)
# end: def index


# ====================================================================================================
# def settings
#
# Routing method for "/settings". Displays a of text fields, combo boxes, and other controls to
# change user settings
#
# Returns--
#
#  A rendered HTML page, coming from template/settings.html
#
@web_server.route("/settings", methods = ["GET", "POST"])
def settings():
    if (not current_user.is_authenticated):
        return redirect(url_for("login"))

    # Get the id from the current user
    user_id: str = current_user.get_id()
    if (user_id == None):
        user_id = "" # Convert null id to empty string format
    
    if (request.method == "GET"): # Display user's settings
        # Get user settings information
        period_numbers: dict = commands.send(send_socket, commands.GET_PERIOD_NUMBERS, user_id, {})
        available_schools: dict = commands.send(send_socket, commands.GET_AVAILABLE_SCHOOLS, user_id, {})
        next_up_list: dict = commands.send(send_socket, commands.GET_NEXT_UP_LIST, user_id, {})

        # Check return codes of the commands sent
        if (available_schools["ReturnCode"] != commands.SUCCESS or
            next_up_list["ReturnCode"] != commands.SUCCESS or
            period_numbers["ReturnCode"] != commands.SUCCESS):
            return err500("(settings)  command response had non-SUCCESS return code")

        period_numbers_list: list = period_numbers["OutputPayload"]["PeriodNumbers"]
        periods: dict = {}
        current_school: str = available_schools["OutputPayload"]["CurrentSchool"]
        schools_list: list = available_schools["OutputPayload"]["AvailableSchools"]
        current_next_up: str = next_up_list["OutputPayload"]["NextUp"]
        next_up: list = ["Disabled", "Next Class", "All Classes"]

        # Get user period information
        for period_number in period_numbers_list:
            user_period: dict = commands.send(send_socket, commands.GET_USER_PERIOD, user_id, {"Type": period_number})
            if (user_period["ReturnCode"] != commands.SUCCESS):
                return err500("(settings)  get period information command response had non-SUCCESS return code")

            # Create the structure for the user's period. This will be unpacked by Jinja when rendering the template
            periods[period_number] = {
                "Name": user_period["OutputPayload"]["Name"],
                "Teacher": user_period["OutputPayload"]["Teacher"],
                "Room": user_period["OutputPayload"]["Room"]
            }

        # Return a rendered template containing the user's options
        return render_template("settings.html",
                               periods=periods,
                               current_school=current_school,
                               schools_list=schools_list,
                               current_next_up=current_next_up,
                               next_up=next_up)
    elif (request.method == "POST"): # Update user's settings
        # Get the list of period numbers to index the form data
        period_numbers: dict = commands.send(send_socket, commands.GET_PERIOD_NUMBERS, user_id, {})
        if (period_numbers["ReturnCode"] != commands.SUCCESS):
            return err500("(settings)  command response had non-SUCCESS return code")
        period_numbers_list: list = period_numbers["OutputPayload"]["PeriodNumbers"]

        # Set class information
        for period_number in period_numbers_list:
            period_name: str = request.form[commands.SET_USER_PERIOD + period_number + "Name"]
            period_teacher: str = request.form[commands.SET_USER_PERIOD + period_number + "Teacher"]
            period_room: str = request.form[commands.SET_USER_PERIOD + period_number + "Room"]

            commands.send(send_socket, commands.SET_USER_PERIOD, user_id, {
                "Type": period_number,
                "Name": period_name,
                "Teacher": period_teacher,
                "Room": period_room
            })

        # Set school information
        current_school: str = request.form[commands.SET_CURRENT_SCHOOL]
        commands.send(send_socket, commands.SET_CURRENT_SCHOOL, user_id, {"CurrentSchool": current_school})

        # Set next up information
        next_up: str = request.form[commands.SET_NEXT_UP]
        commands.send(send_socket, commands.SET_NEXT_UP, user_id, {"NextUp": next_up})
        
        # Reload settings page
        return redirect(url_for("settings"))
# end: def settings


# ====================================================================================================
# def login
#
# Routing method for "/login". Displays nothing, but handles an user login attempt, redirecting to
# "/login/callback"
#
# Returns--
#
#  A redirect request to "/login/callback"
#
@web_server.route("/login")
def login():
    authorization_endpoint: str = get_google_value(GOOGLE_AUTHORIZATION_ENDPOINT)
    
    request_uri = oauth_client.prepare_request_uri(
        authorization_endpoint,
        redirect_uri=request.base_url + "/callback", # Redirect to ".../login" (current) + "/callback"
        scope=["openid"], # Request only the user's UID
        prompt="consent"
    )

    return redirect(request_uri)
# end: def login


# ====================================================================================================
# def login_callback
#
# Routing method for "/login/callback". Handles an user login
#
# Returns--
#
#  A redirect request to "/"
#
@web_server.route("/login/callback")
def login_callback():
    authorization_code: str = request.args.get(GOOGLE_AUTHORIZATION_CODE)
    token_endpoint: str = get_google_value(GOOGLE_TOKEN_ENDPOINT)

    token_url, headers, body = oauth_client.prepare_token_request(
        token_endpoint,
        authorization_response=request.url,
        redirect_url=request.base_url,
        code=authorization_code
    )
    token_response = requests.post(
        token_url,
        headers=headers,
        data=body,
        auth=(conf.OAUTH_TOKEN, conf.OAUTH_SECRET)
    )

    oauth_client.parse_request_body_response(json.dumps(token_response.json()))

    userinfo_endpoint = get_google_value(GOOGLE_USERINFO_ENDPOINT)
    uri, headers, body = oauth_client.add_token(userinfo_endpoint)
    userinfo_response = requests.get(uri, headers=headers, data=body)
    userinfo_json = userinfo_response.json()

    if (not GOOGLE_SUB in userinfo_json):
        return err500("(login_callback)  Malformed response from OAuth2: Google unique ID (sub field) does not exist")
    else:
        unique_id = userinfo_json[GOOGLE_SUB]

        # Check whitelist
        if (not str(unique_id) in whitelist):
            logout_user()
            return render_template("whitelist.html", sub=unique_id)

        # Login user
        user: User = User(sub=unique_id)
        login_user(user)
        return redirect(url_for("index"))
# end: def login_callback


@web_server.route("/logout")
def logout():
    logout_user()
    return redirect(url_for("index"))


# ====================================================================================================
# def run_development
#
# Runs the web server for development testing. This method takes in options for the server address,
# transport address, and SSL key information. This method should only be used for development
# purposes and can be run with "python3 pc_server.py [OPTIONS]" (see the __name__ == "__main__"
# block below).
#
# For running this web server in production, use the run(str, str) method as an entry point
#
# Arguments--
#
#  See command line arguments (--help for more information)
#
@click.command()
@click.option("--server-ip", nargs=1, default="127.0.0.1", show_default=True, help="set web server IP addr")
@click.option("--server-port", nargs=1, default=443, show_default=True, type=int, help="set web server port")
@click.option("--transport-ip", nargs=1, default="127.0.0.1", show_default=True, help="set transport IP addr")
@click.option("--transport-port", nargs=1, default=9000, show_default=True, type=int, help="set transport port")
def run_development(server_ip: str, server_port: int,
                    transport_ip: str, transport_port: int) -> None:
    log.stdout(log.WARN, "pc_server", "Starting in development mode! If this was intended, ignore this message")

    # Use the run method below, which connects the send_socket and just returns the web server object.
    # In a production setting, the returned Flask object from run(str, str) is configured further by
    # the framework
    run(transport_ip, transport_port)

    # In the development environment, configure the web server manually
    try:
        web_server.run(host = server_ip, port = server_port,
                       ssl_context = (conf.APACHE_CERT_FILE, conf.APACHE_PRIVKEY_FILE))
    except PermissionError:
        log.stdlog(log.ERROR, "pc_server", "PermissionError thrown, run with sudo or change --server-port from 443")
    except Exception as e:
        log.stdlog(log.ERROR, "pc_server", f"Uncaught exception thrown by run(): {e}")
    # end: def run_development


# ====================================================================================================
# def run
#
# Runs the web server in a production setting. Starts the secure socket connection with the Java
# transport then returns the Flask object. This object can be configured by the production
# framework (or by the run_development method above).
#
# Arguments--
#
#  transport_ip:   the IP address of the java transport
#
#  transport_port: the port of the java transport
#
# Returns--
#
#  The Flask object web_server, for further configuration by the caller of this method
#
def run(transport_ip: str, transport_port: int) -> Flask:
    try:
        send_socket.settimeout(30) # 30 second timeout
        send_socket.connect((transport_ip, transport_port))
    except TimeoutError as te:
        log.stdlog(log.ERROR, "pc_server", f"send_socket timed out")
    except InterruptedError as ie:
        log.stdlog(log.ERROR, "pc_server", f"InterruptedError thrown by send_socket.connect()")
        log.stdlog(log.ERROR, "pc_server", f"\t{ie}")
    except ConnectionRefusedError as cre:
        log.stdlog(log.ERROR, "pc_server", f"ConnectionRefusedError thrown by send_socket.connect()")
        log.stdlog(log.ERROR, "pc_server", f"\t{cre}")

    web_server.secret_key = os.urandom(24)
    login_manager.init_app(web_server)
    return web_server # Let a production framework or run_development set up the rest
# end: def run


# ====================================================================================================
# Main entry
#
if (__name__ == "__main__"):
    try:
        run_development()
    except Exception as e:
        log.stdlog(log.ERROR, "pc_server", f"unchecked exception thrown from run_development");
        log.stdlog(log.ERROR, "pc_server", f"\t{type(e)}: {e}");
    finally:
        send_socket.close()
# end: Main entry
