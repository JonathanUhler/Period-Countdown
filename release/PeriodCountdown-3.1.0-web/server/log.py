# +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
# log.py
# Period-Countdown
#
# Created by Jonathan Uhler
# +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


import sys
from typing import Final


ENCODING: Final = "utf8" # Put this here because python can't import this field statically from other files :(


# *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=
# THE FOLLOWING OPTIONS CAN BE CHANGED IF DESIRED
#
ENABLE_LOG: Final = False
STDLOG_FILE: Final = "Server.log"
#
# *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=


DEBUG: Final = 0
INFO: Final = 1
WARN: Final = 2
ERROR: Final = 3
FATAL: Final = 4
level_to_string: list[str] = ["DEBUG", "INFO", "WARN", "ERROR", "FATAL"]


# ====================================================================================================
# def stdout
#
# Prints to the standard output
#
# Arguments--
#
#  level:    the level of the message (info, debug, warn, error, fatal). If the level is unknown, it
#            will be replaced by "Log.stdout"
#
#  location: the location the message originated from, such as a class or method name
#
#  message:  the message to print
#
def stdout(level: int, location: str, message: str) -> None:
    if (not isinstance(level, int) or
        not isinstance(location, str) or
        not isinstance(message, str)):
        return

    if (level < DEBUG or level > FATAL):
        print(f"Log.stdout ({location})  {message}")
    else:
        print(f"{level_to_string[level]} ({location})  {message}")
# end: def stdout


# ====================================================================================================
# def stdlog
#
# Writes a message to a standard log file, and optionally prints it to the standard output
#
# Arguments--
#
#  level:    the level of the message (info, debug, warn, error, fatal). If the level is unknown, it
#            will be replaced by "Log.stdout"
#
#  location: the location the message originated from, such as a class or method name
#
#  message:  the message to print
#
#  prnt:     an optional parameter to force the message to print (or not) to the standard output. By
#            default, the message is only printed if it is WARN or higher
#
def stdlog(level: int, location: str, message: str, prnt: bool = False) -> None:
    if (not isinstance(level, int) or
        not isinstance(location, str) or
        not isinstance(message, str) or
        not isinstance(prnt, bool)):
        print(f"WARN (log)  stdlog called with invalid arguments, cannot log")
        print(f"WARN (log)  \tmessage: {message}")
        return

    if (prnt == False):
        prnt = (level == WARN or level == ERROR or level == FATAL)

    if (prnt):
        stdout(level, location, message)

    if (ENABLE_LOG):
        with open(STDLOG_FILE, "a") as writer:
            if (level < DEBUG or level > FATAL):
                writer.write(f"Log.stdout ({location})  {message}\n")
            else:
                writer.write(f"{level_to_string[level]} ({location})  {message}\n")

    if (level == FATAL):
        sys.exit(FATAL)
# end: def stdlog
