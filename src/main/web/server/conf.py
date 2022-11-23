# +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
# conf.py
# Period-Countdown
#
# Created by Jonathan Uhler
# +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


import os
import sys
import json
import traceback
from typing import Final


IS_LOADED: Final = None
CONF_FILE_SYS_PROPERTY: Final = "pc_conf_file"
CONF_FILE_KEYS: Final = [
    "codebase_dir",
    "database_dir",
    "transport_log_file",
    "transport_pid_file",
    "transport_keystore_file",
    "transport_keystore_password",
    "server_whitelist",
    "server_log_file",
    "apache_cert_file",
    "apache_privkey_file",
    "apache_fullchain_file",
    "apache_document_root",
    "apache_script_alias",
    "apache_log_file",
    "apache_access_file",
    "oauth_token",
    "oauth_secret"
]


CODEBASE_DIR: Final = None
DATABASE_DIR: Final = None
TRANSPORT_LOG_FILE: Final = None
TRANSPORT_PID_FILE: Final = None
TRANSPORT_KEYSTORE_FILE: Final = None
TRANSPORT_KEYSTORE_PASSWORD: Final = None
SERVER_WHITELIST: Final = None
SERVER_LOG_FILE: Final = None
APACHE_CERT_FILE: Final = None
APACHE_PRIVKEY_FILE: Final = None
APACHE_FULLCHAIN_FILE: Final = None
APACHE_DOCUMENT_ROOT: Final = None
APACHE_SCRIPT_ALIAS: Final = None
APACHE_LOG_FILE: Final = None
APACHE_ACCESS_FILE: Final = None
OAUTH_TOKEN: Final = None
OAUTH_SECRET: Final = None


# ====================================================================================================
# def append_paths
#
# Appends two file paths to form a merged final path
#
# Arguments--
#
#  head: the head of the uri
#
#  tail: the tail of the uri
#
# Returns--
#
#  If the tail is a valid uri on its own, it is returned unmodified.
#  If the head is the start of a valid absolute uri, a merged path is returned.
#  Otherwise an error is thrown.
#
def append_paths(head: str, tail: str) -> str:
    if (tail.startswith("/")):
        try:
            with open(tail, "a+") as test:
                pass
        except Exception as e:
            raise ValueError(f"tail for file path starts with / but is invalid: {e}")
        return tail
    
    if (head == None or tail == None):
        raise ValueError(f"head or tail for file path is null: {head}, {tail}")
    if (head == "" or tail == ""):
        raise ValueError(f"head or tail for file path is blank: {head}, {tail}")
    if (not head.startswith("/")):
        raise ValueError(f"head doesn't start with /: {head}")

    if (head.endswith("/")):
        head = head[0, len(head) - 1]

    path: str = head + "/" + tail
    try:
        with open(path, "a+") as test:
            pass
    except Exception as e:
        raise ValueError(f"merged path is invalid: {e}")
    return path
# end: def append_paths


# ====================================================================================================
# public static void load
#
# Loads configuration information. Load will only be attempted once upon the first import of this class
#
def load() -> None:
    global IS_LOADED
    if (IS_LOADED):
        return
    IS_LOADED = True
    
    global CODEBASE_DIR
    global DATABASE_DIR
    global TRANSPORT_LOG_FILE
    global TRANSPORT_PID_FILE
    global TRANSPORT_KEYSTORE_FILE
    global TRANSPORT_KEYSTORE_PASSWORD
    global SERVER_WHITELIST
    global SERVER_LOG_FILE
    global APACHE_CERT_FILE
    global APACHE_PRIVKEY_FILE
    global APACHE_FULLCHAIN_FILE
    global APACHE_DOCUMENT_ROOT
    global APACHE_SCRIPT_ALIAS
    global APACHE_LOG_FILE
    global APACHE_ACCESS_FILE
    global OAUTH_TOKEN
    global OAUTH_SECRET

    conf_file_path: str = os.getenv(CONF_FILE_SYS_PROPERTY)
    if (conf_file_path == None or conf_file_path == ""):
        raise ValueError("Configuration file system property not set or is blank")

    with open(conf_file_path, "r") as conf_file: # Error should be handled by the caller
        conf_properties: dict = json.load(conf_file)

    if (not isinstance(conf_properties, dict)):
        raise TypeError(f"conf json file is not dict, found {type(conf_properties)}")

    for key in CONF_FILE_KEYS:
        if (not key in conf_properties):
            raise KeyError(f"missing key in conf file: {key}")

    CODEBASE_DIR = conf_properties["codebase_dir"]
    DATABASE_DIR = conf_properties["database_dir"]
    TRANSPORT_LOG_FILE = append_paths(CODEBASE_DIR, conf_properties["transport_log_file"])
    TRANSPORT_PID_FILE = append_paths(CODEBASE_DIR, conf_properties["transport_pid_file"])
    TRANSPORT_KEYSTORE_FILE = append_paths(CODEBASE_DIR, conf_properties["transport_keystore_file"])
    TRANSPORT_KEYSTORE_PASSWORD = conf_properties["transport_keystore_password"]
    SERVER_WHITELIST = append_paths(CODEBASE_DIR, conf_properties["server_whitelist"])
    SERVER_LOG_FILE = append_paths(CODEBASE_DIR, conf_properties["server_log_file"])
    APACHE_CERT_FILE = append_paths(CODEBASE_DIR, conf_properties["apache_cert_file"])
    APACHE_PRIVKEY_FILE = append_paths(CODEBASE_DIR, conf_properties["apache_privkey_file"])
    APACHE_DOCUMENT_ROOT = conf_properties["apache_document_root"]
    APACHE_SCRIPT_ALIAS = conf_properties["apache_script_alias"]
    APACHE_LOG_FILE = conf_properties["apache_log_file"]
    APACHE_ACCESS_FILE = conf_properties["apache_access_file"]
    OAUTH_TOKEN = conf_properties["oauth_token"]
    OAUTH_SECRET = conf_properties["oauth_secret"]

    if (OAUTH_TOKEN == "" or
        OAUTH_SECRET == ""):
        raise ValueError(f"oauth token or secret is blank")
# end: def load


# ----------------------------------------------------------------------------------------------------
try:
    load()
except Exception as e:
    print(f"FATAL (conf.py) Could not initialize configuration. Uncaught exception: {e}")
    print("----- STACK TRACE BEGINS -----")
    print(f"{traceback.format_exc()}")
    print("----- STACK TRACE ENDS -----")
    sys.exit(4)
# end
