// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolPeriod.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package school;


import util.DateTime;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class SchoolPeriod
//
// Java representation of one of the maps within a day defined in the "Days" section of the school json
//
public class SchoolPeriod {

	private String type;
	private String name;
	private String start;
	private String end;
	

	// ----------------------------------------------------------------------------------------------------
	// public SchoolPeriod
	//
	// Arguments--
	//
	//  type:  the period type. Must be:
	//          - Any integer N: signifies the period/class number for a period
	//          - "Special": something that is not a period, but is an important event and should be
	//            counted in time calculations and the Next Up feature (e.g. lunch, study hall, etc.)
	//          - "Nothing": an event used to fill time. Does not matter and should NOT be counted
	//            (e.g. passing periods, before/after school starts)
	//
	//  name:  the name of the period. Defined by the programmer. Can be anything
	//
	//  start: the start time of the period in format HH:MM
	//
	//  end:   the end time of the period in format HH:MM
	//
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
	// end: public SchoolPeriod


	// ====================================================================================================
	// public DateTime getStartTime
	//
	// Returns the start time of this period as a DateTime object based on the date listed in the argument.
	// Because this SchoolPeriod is generic (it only has start/end time, but no date that it is on) an
	// argument must be given to supply the date
	//
	// Arguments--
	//
	//  date: the date the period occurs on, which is appended to the start time to get the final DateTime
	//
	// Returns--
	//
	//  A DateTime object representing the date and time the period starts
	//
	public DateTime getStartTime(DateTime date) {
		if (date == null)
			return null;
		
		return DateTime.getInstance(date.getDayTag() +
									DateTime.DATE_TIME_DELIMITER + this.start +
									DateTime.TIME_DELIMITER + "00" + DateTime.TIME_DELIMITER + "000");
	}
	// end: public DateTime getStartTime


	// ====================================================================================================
	// public DateTime getEndTime
	//
	// Returns the end time of this period as a DateTime object based on the date listed in the argument.
	// Because this SchoolPeriod is generic (it only has start/end time, but no date that it is on) an
	// argument must be given to supply the date
	//
	// Arguments--
	//
	//  date: the date the period occurs on, which is appended to the end time to get the final DateTime
	//
	// Returns--
	//
	//  A DateTime object representing the date and time the period ends
	//
	public DateTime getEndTime(DateTime date) {
		if (date == null)
			return null;
		
		return DateTime.getInstance(date.getDayTag() +
									DateTime.DATE_TIME_DELIMITER + this.end +
									DateTime.TIME_DELIMITER + "00" + DateTime.TIME_DELIMITER + "000");
	}
	// end: public DateTime getEndTime


	// ====================================================================================================
	// GET methods
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
	// end: GET methods


	// ====================================================================================================
	// public String toString
	//
	// Returns a string representation of this SchoolPeriod
	//
	@Override
	public String toString() {
		return "SCHOOLPERIOD: Type=" + this.type + ",Name=" + this.name + ",Start=" + this.start + ",End=" + this.end;
	}
	// end: public String toString

}
// end: public class SchoolPeriod
