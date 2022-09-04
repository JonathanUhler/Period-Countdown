// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// CRC.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package web.transport;


import util.Log;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class CRC
//
// A class to handle crc generation, application, and verification for network safety
//
public class CRC {

	public static final int NUM_NIBBLES = 8; 
	

	// ====================================================================================================
	// public static int crc32
	//
	// Generates a 4 byte crc from the input string
	// Adapted from C routine: https://rosettacode.org/wiki/CRC-32#Library
	//
	// Arguments--
	//
	//  str: the string to generate a crc for
	//
	// Returns--
	//
	//  A 4 byte checksum as an int that is assumed to be unsigned
	//
	public static int crc32(String str) {
		int crc = 0;
		int[] table = new int[256];
		int rem = 0;
		int octet = 0;

		for (int i = 0; i < table.length; i++) {
			rem = i;
			for (int j = 0; j < 8; j++) {
				if ((rem & 1) != 0) {
					rem >>>= 1; // 3 ">" for unsigned shift
					rem ^= 0xedb88320;
				}
				else
					rem >>>= 1; // 3 ">" for unsigned shift
			}
			table[i] = rem;
		}

		crc = ~crc;
		for (int i = 0; i < str.length(); i++) {
			char p = str.substring(i).charAt(0);
			octet = (int) p;
			crc = (crc >>> 8) ^ table[(crc & 0xff) ^ octet];
		}  
		return ~crc;
	}
	// end: public static int crc32


	// ====================================================================================================
	// public static String getFor
	//
	// Generates a 4 byte crc as a string
	//
	// Arguments--
	//
	//  str: the string to generate a crc for
	//
	// Returns--
	//
	//  A 4 byte checksum as an 8-character string
	//
	public static String getFor(String str) {
		int crc32 = CRC.crc32(str);

		String hexString = Integer.toHexString(crc32);
		return String.format("%1$" + CRC.NUM_NIBBLES + "s", hexString).replace(" ", "0");
	}
	// end: public static String getFor


	// ====================================================================================================
	// public static boolean check
	//
	// Checks for a valid crc on a string, asumming the last four characters of the string, when converted
	// to a 4 byte word, represent the crc value for the remainder of the string.
	//
	// Arguments--
	//
	//  str: the string to check the crc of
	//
	// Returns--
	//
	//  Whether the given crc included with "str" matched the generated crc
	//
	public static boolean check(String str) {
		if (str.length() < CRC.NUM_NIBBLES) {
			Log.stdlog(Log.ERROR, "CRC", "check called with an invalid string (too short)");
			Log.stdlog(Log.ERROR, "CRC", "\t" + str);
			return false;
		}

		String given = str.substring(str.length() - CRC.NUM_NIBBLES);
		String raw = str.substring(0, str.length() - CRC.NUM_NIBBLES);
		String gen = CRC.getFor(raw);

		if (given.length() != CRC.NUM_NIBBLES || gen.length() != CRC.NUM_NIBBLES) {
			Log.stdlog(Log.ERROR, "CRC", "given crc and/or generated crc have invalid length");
			Log.stdlog(Log.ERROR, "CRC", "\tgiven: " + given);
			Log.stdlog(Log.ERROR, "CRC", "\tgen:   " + gen);
			return false;
		}

		boolean passed = gen.equals(given);
		if (!passed) {
			Log.stdlog(Log.ERROR, "CRC", "CRC check failed, given was not equal to generated");
			Log.stdlog(Log.ERROR, "CRC", "\tfull msg: " + str);
			Log.stdlog(Log.ERROR, "CRC", "\tgiven crc: " + given);
			Log.stdlog(Log.ERROR, "CRC", "\tgen crc:   " + gen);
		}
		return passed;
	}
	// end: public static boolean check
	
}
// end: public class CRC
