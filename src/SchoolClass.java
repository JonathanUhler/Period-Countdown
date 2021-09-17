// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolClass.java
// Period-Countdown
//
// Created by Jonathan Uhler on 8/22/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class SchoolClass
//
// Defines the structure for a school class
//
public class SchoolClass {

    private final int periodNumber; // Integer period number of the class
    private final String className; // String name of the class
    private final String roomNumber; // The room the class is taught in
    private final String teacherName; // The name of the teacher that teaches the class


    // ----------------------------------------------------------------------------------------------------
    // public SchoolClass
    //
    // SchoolClass constructor
    //
    // Arguments--
    //
    // periodNumber:    the number of the current period
    //
    // className:       a string representing the name of the school class
    //
    // roomNumber:      the room that the class is taught in
    //
    // teacherName:     a string representing the name of the teacher teaching the class
    //
    public SchoolClass(int periodNumber, String className, String roomNumber, String teacherName) throws Exception {
        // Check that all constructor arguments are correct
        CalendarHelper.calendarAssert((periodNumber >= SchoolCalendar.getFirstPeriod()) &&
                (periodNumber <= SchoolCalendar.getLastPeriod()) &&
                (teacherName != null),
                "SchoolClass.SchoolClass constructed with invalid arguments",
                String.valueOf(periodNumber), className, String.valueOf(roomNumber), teacherName);

        // Set the class variables to the constructor variables
        this.periodNumber = periodNumber;
        this.className = className;
        this.roomNumber = roomNumber;
        this.teacherName = teacherName;
    }
    // end: public SchoolClass


    // ====================================================================================================
    // GET methods
    public int getPeriodNumber() {
        return periodNumber;
    }

    public String getClassName() {
        return className;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getTeacherName() {
        return teacherName;
    }
    // end: GET methods


    // ====================================================================================================
    // public String toString
    //
    // SchoolClass toString method
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // Textual representation of this class
    //
    @Override
    public String toString() {
        String classDescription =  "\n\tperiodNumber:\t" + this.getPeriodNumber() + "\n\tclassName:\t" +
                this.getClassName() + "\n\tteacherName:\t" + this.getTeacherName() + "\n\troomNumber:\t" + this.getRoomNumber();
        return super.toString() + ": " + classDescription;
    }
    // end: public String toString
}
// end: public class SchoolClass