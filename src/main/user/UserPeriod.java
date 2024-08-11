package user;


/**
 * Represents the user-defined data in a school period.
 *
 * This information exists as opposed to the {@code SchoolPeriod} class which defines generic
 * timing and type data. This class holds user-specific data (teacher, room, and name).
 *
 * @author Jonathan Uhler
 */
public class UserPeriod {
    
    /** The user-defined name of the period. */
    private String name;
    /** The period status (e.g. {@code "Free"} or the name of the corresponding SchoolPeriod. */
    private String status;
    /** The name of the period's teacher. */
    private String teacher;
    /** The room number for this class. */
    private String room;
    
    
    /**
     * Constructs a new {@code UserPeriod} object without a teacher or room.
     *
     * @param name    the user-defined name for this period.
     * @param status  the status of the class. This is what is "happening" during this period,
     *                which can be anything but is usually {@code "Free"} or the value of
     *                {@code SchoolPeriod::getName} for the corresponding school period.
     */
    public UserPeriod(String name, String status) {
        this(name, status, null, null);
    }
    
    
    /**
     * Constructs a new {@code UserPeriod} object
     *
     * @param name     the user-defined name for this period.
     * @param status   the status of the class. This is what is "happening" during this period,
     *                 which can be anything but is usually {@code "Free"} or the value of
     *                 {@code SchoolPeriod::getName} for the corresponding school period.
     * @param teacher  the name of the teacher.
     * @param room     the room number/name for the class.
     */
    public UserPeriod(String name, String status, String teacher, String room) {
        this.name = name;
        this.status = status;
        this.teacher = teacher;
        this.room = room;
    }
    
    
    /**
     * Returns the user-defined name of this period.
     *
     * @return the user-defined name of this period.
     */
    public String getName() {
        return this.name;
    }
    
    
    /**
     * Returns the status of this period.
     *
     * @return the status of this period.
     */
    public String getStatus() {
        return this.status;
    }
    
    
    /**
     * Returns the name of this period's teacher.
     *
     * @return the name of this period's teacher.
     */
    public String getTeacher() {
        return this.teacher;
    }
    
    
    /**
     * Returns the room number/name for this period.
     *
     * @return the room number/name for this period.
     */
    public String getRoom() {
        return this.room;
    }
    
    
    /**
     * Returns whether this period is free. Being "free" is determined by the user-defined name. In
     * this case, if the name is {@code "free"}, {@code "none"}, or {@code "n/a"} (case 
     * insensitive).
     *
     * @return whether this period is free.
     */
    public boolean isFree() {
        return this.status.toLowerCase().equals("free") ||
            this.status.toLowerCase().equals("none") ||
            this.status.toLowerCase().equals("n/a");
    }
    
}
