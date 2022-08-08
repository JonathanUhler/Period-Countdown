// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Tools.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package util;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class Tools
//
// Miscellaneous tools that do not fit into any other util class
//
public class Tools {

	// ====================================================================================================
	// public static String pad
	//
	// Pads a string to a given width with a given character. If the string is longer than width, no
	// change is made
	//
	// Arguments--
	//
	//  str:       the string to pad. Pads out to the left (e.g. pad("hi", 5, ".") --> "   hi")
	//
	//  width:     the width of the final string if padded. If the starting string is longer than width,
	//             no change is made
	//
	//  character: the character to pad with
	public static String pad(String str, int width, char character) {
		if (str == null)
			str = "";
		return String.format("%" + width + "s", str).replace(' ', character);
	}
	// end: public static String pad

}
// end: public class Tools
