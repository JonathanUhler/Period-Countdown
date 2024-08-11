package school;


import time.UTCTime;


/**
 * Represents the concept of a block of time during the school year defined by the json file. This
 * "block of time" can include:
 *
 * - Scheduled academic classes
 * - Special blocks (e.g. lunch, study hall)
 * - Non-significant, "filler" blocks (e.g. passing period, before classes, after classes)
 * 
 * Each period has a start and end time defined to the nearst millisecond of precision as
 * {@code UTCTime} objects. For any period {@code P_n} that is not the first or last period in the
 * school year, the following must be true:
 * 
 * - {@code P_n.start < P_n.end}. That is, the minimum length of a period is 1 millisecond.
 * - {@code P_n.end = P_(n + 1).start + 1 ms}. That is, no point exists in the exclusive interval
 *   between the start and end of the school year where there is any gap of time.
 * 
 * The properties of a {@code SchoolPeriod} object are defined by the {@code "Type"} field. The
 * options of this field are defined in more detail in the documentation for this class's
 * constructor.
 */
public class SchoolPeriod {
    
    /** The type of the period, either Nothing, Special, or a number. */
    private String type;
    /** Programmer defined name, can be anything (e.g. "Biology"). */
    private String name;
    /** Start time, inclusive, of the period. */
    private UTCTime start;
    /** End time, inclusive (e.g. should end with .999 ms), of the period. */
    private UTCTime end;
    /** Whether this period is the last period of the day (e.g. {@code end ?= 23:59:59.999}. */
    private boolean isLast;
    
    
    /**
     * Constructs a new {@code SchoolPeriod} object from the information in the school json file.
     * <p>
     * The "type" of a period is defined by the following:
     * <ul>
     * <li> {@code "Nothing"}: this period is non-significant filler. It should not be counted
     *      by {@code isCounted} and does not contain an educational class ({@code isFree == true}).
     * <li> {@code "Special"}: this period has an important event other than an academic class.
     * <li> An integer {@code n} such that {@code n >= FirstPeriod && n <= LastPeriod}. This
     *      identified the number of an academic class which should be counted and is not free.
     * </ul>
     *
     * @param type    the "type" of the period. See above for more detail.
     * @param name    the name of the period.
     * @param start   the start time of the period.
     * @param end     the end time of the period.
     * @param isLast  whether this period is the last in its containing day (local time).
     *
     * @throws NullPointerException      if any argument is null.
     * @throws IllegalArgumentException  if {@code start} is after {@code end}.
     * @throws IllegalArgumentException  if {@code type} is not {@code "Nothing"},
     *                                   {@code "Special"}, or an integer.
     */
    public SchoolPeriod(String type, String name, UTCTime start, UTCTime end, boolean isLast) {
        if (type == null) {
            throw new NullPointerException("type cannot be null");
        }
        if (name == null) {
            throw new NullPointerException("name cannot be  null");
        }
        if (start == null) {
            throw new NullPointerException("start cannot be null");
        }
        if (end == null) {
            throw new NullPointerException("end cannot be null");
        }
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException("start cannot be after end");
        }
        if (!type.equals(SchoolJson.NOTHING) && !type.equals(SchoolJson.SPECIAL)) {
            try {
                Integer.parseInt(type);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("invalid type for SchoolPeriod: " + type);
            }
        }
	
        this.type = type;
        this.name = name;
        this.start = start;
        this.end = end;
        this.isLast = isLast;
    }
    
    
    /**
     * Returns the name of the period.
     *
     * @return the name of the period.
     */
    public String getName() {
        return this.name;
    }
    
    
    /**
     * Returns the type of the period.
     *
     * @return the type of the period.
     */
    public String getType() {
        return this.type;
    }
    
    
    /**
     * Returns the start of the period.
     *
     * @return the start of the period.
     */
    public UTCTime getStart() {
        return this.start;
    }
    
    
    /**
     * Returns the end of the period.
     *
     * @return the end of the period.
     */
    public UTCTime getEnd() {
        return this.end;
    }
    
    
    /**
     * Returns whether this period is the last in its day (local time).
     *
     * @return whether this period is the last in its day (local time).
     */
    public boolean isLast() {
        return this.isLast;
    }
    
    
    /**
     * Returns whether this period is counted. A "counted" period is one whose type is not
     * {@code "Nothing"}.
     *
     * @return whether this period is counted.
     */
    public boolean isCounted() {
        return !this.type.equals(SchoolJson.NOTHING);
    }
    
    
    /**
     * Returns whether this period is free. A "free" period is one whose type is not an integer.
     *
     * @return whether this period is free.
     */
    public boolean isFree() {
        return this.type.equals(SchoolJson.NOTHING) || this.type.equals(SchoolJson.SPECIAL);
    }
    
    
    /**
     * Returns a string representation of this {@code SchoolPeriod}.
     *
     * @return a string representation of this {@code SchoolPeriod}.
     */
    @Override
    public String toString() {
        return this.start + " - " + this.end + "\tType=" + this.type + ", Name=" + this.name;
    }
    
}
