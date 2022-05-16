// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// PeriodData.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/21/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package calendar;


import school.*;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class PeriodData
//
// Period match data structure
//
public class PeriodData {

    private SchoolDay day; // SchoolDay object
    private SchoolPeriod period; // SchoolPeriod object
    private int index; // Day index


    // ----------------------------------------------------------------------------------------------------
    // public PeriodData
    //
    // Arguments--
    //
    // day:     SchoolDay object
    //
    // period:  SchoolPeriod object
    //
    // index:   day index
    //
    public PeriodData(SchoolDay day, SchoolPeriod period, int index) {
        this.day = day;
        this.period = period;
        this.index = index;
    }
    // end: public PeriodData


    // ====================================================================================================
    // GET methods
    public SchoolDay getDay() {
        return day;
    }

    public SchoolPeriod getPeriod() {
        return period;
    }

    public int getIndex() {
        return index;
    }
    // end: GET methods

}
// end: public class PeriodData