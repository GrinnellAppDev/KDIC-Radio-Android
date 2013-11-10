package edu.grinnell.schedule;

public class Show {

	protected String title;
	protected String day;
	protected String starttime;
	protected String endtime;


	public Show(String title, String starttime, String endtime, String day) {
		this.title = title;
		this.starttime = starttime;
		this.endtime = endtime;
		this.day = day;
	}

	public String getTitle() {
		return title;
	}

	public String getStartTime() {
		return starttime;
	}

	public String getEndTime() {
		return endtime;
	}
	
	public String getDay() {
		return day;
	}
}
