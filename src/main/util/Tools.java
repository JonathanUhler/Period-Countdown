package util;


public class Tools {

	public static String pad(String str, int width, char character) {
		if (str == null)
			str = "";
		return String.format("%" + width + "s", str).replace(' ', character);
	}

}
