# +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
# crc.py
# Period-Countdown
#
# Created by Jonathan Uhler
# +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


import log
import binascii
from typing import Final


NUM_NIBBLES: Final = 8


# ====================================================================================================
# def crc32
#
# Generates a 4 byte crc from the input string
#
# Arguments--
#
#  str: the string to generate a crc for
#
# Returns--
#
#  A 4 byte checksum as an int that is assumed to be unsigned
#
def crc32(string: str) -> int:
    crc: int = binascii.crc32(bytes(string, log.ENCODING))
    return crc
# end: def crc32


# ====================================================================================================
# def get_for
#
# Generates a 4 byte crc as a string
#
# Arguments--
#
#  string: the string to generate a crc for
#
# Returns--
#
#  A 4 byte checksum as an 8-character string
#
def get_for(string: str) -> str:
    crc: int = crc32(string)
    hex_string: str = format(crc, f"0{NUM_NIBBLES}x")
    return hex_string
# end: def get_for


# ====================================================================================================
# def check
#
# Checks for a valid crc on a string, asumming the last four characters of the string, when converted
# to a 4 byte word, represent the crc value for the remainder of the string.
#
# Arguments--
#
#  string: the string to check the crc of
#
# Returns--
#
#  Whether the given crc included with "str" matched the generated crc
#
def check(string: str) -> bool:
    string = string.replace("\n", "") # Remove any newlines, especially the trailing newline
    
    given: str = string[len(string) - NUM_NIBBLES:]
    raw: str = string[0:len(string) - NUM_NIBBLES]
    gen: str = get_for(raw)

    if (len(given) != NUM_NIBBLES or len(gen) != NUM_NIBBLES):
        log.stdlog(log.ERROR, "crc", "given crc and/or generator crc have invalid length")
        log.stdlog(log.ERROR, "crc", f"\tgiven: {given}")
        log.stdlog(log.ERROR, "crc", f"\tgen: {gen}")

    passed: bool = (gen == given)
    if (not passed):
        log.stdlog(log.ERROR, "crc", "CRC check failed, given was not equal to generated")
        log.stdlog(log.ERROR, "crc", f"\tfull msg: {string}")
        log.stdlog(log.ERROR, "crc", f"\traw msg: {raw}")
        log.stdlog(log.ERROR, "crc", f"\tgiven: {given}")
        log.stdlog(log.ERROR, "crc", f"\tgen: {gen}")
    return passed
# end: def check
