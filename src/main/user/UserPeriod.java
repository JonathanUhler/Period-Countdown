package user;


public class UserPeriod {

	private String name;
	private String status;
	private String teacher;
	private String room;


	public UserPeriod(String name, String status) {
		this(name, status, null, null);
	}
	

	public UserPeriod(String name, String status, String teacher, String room) {
		this.name = name;
		this.status = status;
		this.teacher = teacher;
		this.room = room;
	}


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

}
