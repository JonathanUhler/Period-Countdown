// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// UserPeriod.java
// Period-Countdown
//
// Created by Jonathan Uhler
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package user;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class UserPeriod
//
// Java representation of the data for a period in the user's json file. This is as opposed to
// the school.SchoolPeriod class which holds generic data like start/end time and type. This class
// holds user-specific data like teacher and room number
public class UserPeriod {

	private String name;
	private String status;
	private String teacher;
	private String room;


	// ----------------------------------------------------------------------------------------------------
	// public UserPeriod
	//
	public UserPeriod(String name, String status) {
		this(name, status, null, null);
	}
	// end: public UserPeriod
	

	// ----------------------------------------------------------------------------------------------------
	// public UserPeriod
	//
	// Arguments--
	//
	//  name:    the user-defined name for the period (e.g. Chemistry, Algebra 2, Social Studies, etc.)
	//
	//  status:  status of the class (what is happening during this class). This is usually going to be
	//           "Free" for free periods, or the "Name" term of the corresponding SchoolPeriod, which
	//           is information accessible from the UserAPI.getPeriod(SchoolPeriod) method
	//
	//  teacher: the user-defined name for the teacher, can be blank
	//
	//  room:    the user-defined room number of the class, can be blank
	//
	public UserPeriod(String name, String status, String teacher, String room) {
		this.name = name;
		this.status = status;
		this.teacher = teacher;
		this.room = room;
	}


	// ====================================================================================================
	// GET methods
	public String getName() {
		return this.name;
	}

	public String getStatus() {
		return this.status;
	}

	public String getTeacher() {
		return this.teacher;
	}

	public String getRoom() {
		return this.room;
	}

	public boolean isFree() {
		return this.status.toLowerCase().equals("free") ||
			this.status.toLowerCase().equals("none") ||
			this.status.toLowerCase().equals("n/a");
	}
	// end: GET methods

}
// end: public class UserPeriod
