package school;


import util.DateTime;


public class SchoolPeriod {

	private String type;
	private String name;
	private String start;
	private String end;
	

	public SchoolPeriod(String type, String name,
						String start, String end) throws IllegalArgumentException
	{
		if (type == null || name == null || start == null || end == null)
			throw new IllegalArgumentException("SchoolPeriod constructed with null argument(s)\ntype: " + type +
											   "\nname: " + name + "\nstart: " + start + "\nend: " + end);

		if (!start.matches(DateTime.SIMPLE_TIME_FORMAT) || !end.matches(DateTime.SIMPLE_TIME_FORMAT))
			throw new IllegalArgumentException("SchoolPeriod constructed with invalid times\nstart: " + start +
											   "\nend: " + end);

		try {
			Integer.parseInt(type);
		}
		catch (NumberFormatException e) {
			if (!type.equals(SchoolJson.NOTHING) && !type.equals(SchoolJson.SPECIAL))
				throw new IllegalArgumentException("SchoolPeriod constructed with invalid type\ntype: " + type);
		}
		
		this.type = type;
		this.name = name;
		this.start = start;
		this.end = end;
	}


	protected DateTime getStartTime(DateTime date) {
		if (date == null)
			return null;
		
		return DateTime.getInstance(date.getDayTag() +
									DateTime.DATE_TIME_DELIMITER + this.start +
									DateTime.TIME_DELIMITER + "00" + DateTime.TIME_DELIMITER + "000");
	}


	protected DateTime getEndTime(DateTime date) {
		if (date == null)
			return null;
		
		return DateTime.getInstance(date.getDayTag() +
									DateTime.DATE_TIME_DELIMITER + this.end +
									DateTime.TIME_DELIMITER + "00" + DateTime.TIME_DELIMITER + "000");
	}


	public DateTime getEndTime() {
		return this.getEndTime(new DateTime()); // Default to right now
	}


	public String getStartTimeString() {
		return this.start;
	}


	public String getEndTimeString() {
		return this.end;
	}


	public String getName() {
		return this.name;
	}


	public String getType() {
		return this.type;
	}


	public boolean isLast() {
		return this.end.equals("23:59");
	}


	public boolean isCounted() {
		return !this.type.equals(SchoolJson.NOTHING);
	}


	public boolean isFree() {
		return (this.type.equals(SchoolJson.NOTHING) || this.type.equals(SchoolJson.SPECIAL));
	}


	@Override
	public String toString() {
		return "SCHOOLPERIOD: Type=" + this.type + ",Name=" + this.name + ",Start=" + this.start + ",End=" + this.end;
	}

}
