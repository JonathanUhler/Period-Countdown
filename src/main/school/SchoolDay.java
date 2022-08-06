package school;


import util.DateTime;
import java.util.ArrayList;


public class SchoolDay {

	private String type;
	private ArrayList<SchoolPeriod> periods;
	

	public SchoolDay(String type, ArrayList<SchoolPeriod> periods) {
		this.type = type;
		this.periods = periods;
	}


	public SchoolPeriod getPeriod(DateTime when) {
		if (when == null)
			return null;

		for (SchoolPeriod period : this.periods) {
			DateTime periodStart = period.getStartTime(when);
			DateTime periodEnd = period.getEndTime(when);

			if (periodStart == null && periodEnd == null)
				return null;

			if ((periodStart.equals(when) || periodStart.before(when)) &&
				periodEnd.after(when))
				return period;
		}
		
		return null;
	}


	@Override
	public String toString() {
		String str = "SCHOOLDAY: Type=" + this.type + ",Periods=[";

		for (SchoolPeriod period : this.periods)
			str += "\n\t" + period;

		str += "\n]";
		return str;
	}

}
