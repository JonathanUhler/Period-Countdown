package school;


import util.DateTime;
import java.util.ArrayList;


public class SchoolWeek {

	private String type;
	private ArrayList<SchoolDay> days;
	

	public SchoolWeek(String type, ArrayList<SchoolDay> days) {
		this.type = type;
		this.days = days;
	}


	public SchoolDay getDay(DateTime when) {
		if (when == null)
			return null;
		
		int dayIndex = when.getDayIndex();
		if (dayIndex > 0 && dayIndex < 7)
			return this.days.get(dayIndex);
		return null;
	}


	@Override
	public String toString() {
		String str = "SCHOOLWEEK: Type=" + this.type + ",Days=[";

		for (SchoolDay day : this.days)
			str += "\n\t" + day.toString().replace("\n", "\n\t");

		str += "\n]";
		return str;
	}

}
