# +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
# pc_server.py
# Period-Countdown
#
# Created by Jonathan Uhler
# +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


import log
import commands
import os
import sys
import click
import ssl
import socket
from typing import Final
from flask import Flask, request, render_template


# * web_server is the Flask web server, which should be run through a production framework like Apache
# * send_socket is a secure socket to communicate with the Java transport
web_server: Final = Flask(__name__)
send_socket: Final = ssl.wrap_socket(socket.socket(socket.AF_INET, socket.SOCK_STREAM));


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
        # Get the current user period, time remaining in the period, and list of next classes
        user_period: dict = commands.send(send_socket, commands.GET_USER_PERIOD, "", {})
        time_remaining: dict = commands.send(send_socket, commands.GET_TIME_REMAINING, "", {})
        next_up_list: dict = commands.send(send_socket, commands.GET_NEXT_UP_LIST, "", {})

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
                               end_time=f"{end_time}",
                               expire_time=f"{expire_time}",
                               status=f"{period_name} | {period_status}",
                               time_remaining=f"{time}",
                               next_up_list=next_up if next_up_level != "Disabled" else None)
# end: def index


# ====================================================================================================
# def settings
#
# Routing method for "/settings". Displays a list of buttons to redirect to a page for a given setting
#
# Returns--
#
#  A rendered HTML page, coming from template/settings.html
#
@web_server.route("/settings", methods = ["GET"])
def settings():
    if (request.method == "GET"):
        return err403(None) # MARK: currently settings are not supported since there is no database
# end: def settings


# ====================================================================================================
# def classinformation
#
# Routing method for "/settings/classinformation". Displays the same text fields as the
# "Class Information" option on the desktop app.
#
# Returns--
#
#  A rendered HTML page, coming from template/classinformation.html
#
@web_server.route("/settings/classinformation", methods = ["GET", "PUT"])
def classinformation():
    if (request.method == "GET"):
        return err403(None)
    elif (request.method == "PUT"):
        return err403(None)
# end: def classinformation


# ====================================================================================================
# def schoolinformation
#
# Routing method for "/settings/schoolinformation". Displays the option box with the list of
# available school files (same as "School Information" option on the desktop app).
#
# Returns--
#
#  A rendered HTML page, coming from template/schoolinformation.html
#
@web_server.route("/settings/schoolinformation", methods = ["GET", "PUT"])
def schoolinformation():
    if (request.method == "GET"):
        return err403(None)
    elif (request.method == "PUT"):
        return err403(None)
# end: def schoolinformation


# ====================================================================================================
# def nextup
#
# Routing method for "/settings/nextup". Displays a selection box for the user to choose the
# next up feature verbosity (same as the "Next Up" option on the desktop app).
#
# Returns--
#
#  A rendered HTML page, coming from template/nextup.html
#
@web_server.route("/settings/nextup", methods = ["GET", "PUT"])
def nextup():
    if (request.method == "GET"):
        return err403(None)
    elif (request.method == "PUT"):
        return err403(None)
# end: def nextup


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
@click.option("--keyfile", nargs=1, default="server_keystore.pem", show_default=True, help="set server keystore file")
@click.option("--certfile", nargs=1, default="server_cert.pem", show_default=True, help="set server certificate file")
@click.option("--log-file", nargs=1, help="set log file; if not set, log file will not be written")
def run_development(server_ip: str, server_port: int,
                    transport_ip: str, transport_port: int,
                    keyfile: str, certfile: str,
                    log_file: str) -> None:
    log.stdout(log.WARN, "pc_server", "Starting in development mode! If this was intended, ignore this message")

    # Use the run method below, which connects the send_socket and just returns the web server object.
    # In a production setting, the returned Flask object from run(str, str) is configured further by
    # the framework
    run(transport_ip, transport_port, log_file)

    # In the development environment, configure the web server manually
    try:
        web_server.run(host = server_ip, port = server_port, ssl_context = (certfile, keyfile))
    except PermissionError:
        log.stdlog(log.ERROR, "pc_server", "PermissionError thrown, run with sudo or change --server-port from 443")
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
def run(transport_ip: str, transport_port: int, log_file: str) -> Flask:
    if (log_file != None):
        os.environ[log.LOG_FILE_SYS_PROPERTY] = log_file
    
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
