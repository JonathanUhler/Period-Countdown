// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolPeriod.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package school;


import util.UTCTime;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class SchoolPeriod
//
// Represents the concept of a block of time during the school year defined by the json file. This can
// include: scheduled classes, special blocks like lunch or study hall, and filler blocks like
// passing periods.
//
// Each period has a start and end time defined to millisecond precision as util.UTCTime objects. For
// any period P_(n) that is not the first or last period in the school year:
//
//  P_(n).start < P_(n).end
//  P_(n).end = 1ms + P_(n + 1).start
//
// The properties of a SchoolPeriod object are defined by the type field. The options of this field
// are defined in more detail in the documentation for the constructor.
//
public class SchoolPeriod {

	private String type;
	private String name;
	private UTCTime start;
	private UTCTime end;
	private boolean isLast;


	// ----------------------------------------------------------------------------------------------------
	// public SchoolPeriod
	//
	// Arguments--
	//
	//  type:   the "type" of the period, not null. Can be one of the following:
	//            "Nothing": this period is a filler event. It should not be counted by isCounted and
	//                       does not contain an education class (isFree == true)
	//            "Special": this period has an important event other than an educational class.
	//                       It WILL be included by isCounted, but is still "free"
	//            {N | N > 0, N = Z}: identifies the number of an educational class which should be
	//                                counted and is NOT free
	//
	//  name:   the name of the period, can be anything, not null
	//
	//  start:  the start time of the period, not null
	//
	//  end:    the end time of the period, not null
	//
	//  isLast: whether this period is the last in its containing day (local time)
	//
	public SchoolPeriod(String type, String name,
						UTCTime start, UTCTime end,
						boolean isLast) throws IllegalArgumentException
	{
		if (type == null || name == null || start == null || end == null)
			throw new IllegalArgumentException("SchoolPeriod constructed with null argument(s)\ntype: " + type +
											   "\nname: " + name + "\nstart: " + start + "\nend: " + end);

		try {
			Integer.parseInt(type);
		}
		catch (NumberFormatException e) {
			// If the type is not a number, not "NOTHING" and not "SPECIAL", then it is an error
			if (!type.equals(SchoolJson.NOTHING) && !type.equals(SchoolJson.SPECIAL))
				throw new IllegalArgumentException("SchoolPeriod constructed with invalid type\ntype: " + type);
		}
		
		this.type = type;
		this.name = name;
		this.start = start;
		this.end = end;
		this.isLast = isLast;
	}


	public String getName() {
		return this.name;
	}


	public String getType() {
		return this.type;
	}


	public UTCTime getStart() {
		return this.start;
	}


	public UTCTime getEnd() {
		return this.end;
	}


	public boolean isLast() {
		return this.isLast;
	}

	
	public boolean isCounted() {
		return !this.type.equals(SchoolJson.NOTHING);
	}


	public boolean isFree() {
		return (this.type.equals(SchoolJson.NOTHING) ||
				this.type.equals(SchoolJson.SPECIAL));
	}


	@Override
	public String toString() {
		return this.start + " - " + this.end + "\tType=" + this.type + ", Name=" + this.name;
	}

}
// end: public class SchoolPeriod
