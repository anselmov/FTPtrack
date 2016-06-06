package test;

public class Calendar {

	private String dateCalendar;
	private String houre;
	private int idRoom;
	private int idCourse;
	private int idUser;
	private int idCalendar;

	public Calendar(int idCalendar, String dateCalendar, String houre, int idRoom, int idCourse, int idUser) {
		super();
		this.idCalendar = idCalendar;
		this.dateCalendar = dateCalendar;
		this.houre = houre;
		this.idRoom = idRoom;
		this.idCourse = idCourse;
		this.idUser = idUser;
	}

	public String getDate() {
		return dateCalendar;
	}

	public void setDate(String dateCalendar) {
		this.dateCalendar = dateCalendar;
	}

	public String getHoure() {
		return houre;
	}

	public void setHoure(String houre) {
		this.houre = houre;
	}

	public int getIdRoom() {
		return idRoom;
	}

	public void setIdRoom(int idRoom) {
		this.idRoom = idRoom;
	}

	public int getIdCourse() {
		return idCourse;
	}

	public void setIdCourse(int idCourse) {
		this.idCourse = idCourse;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public Calendar() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getIdCalendar() {
		return idCalendar;
	}

	public void setIdCalendar(int idCalendar) {
		this.idCalendar = idCalendar;
	}

	@Override
	public String toString() {
		return "Calendar [date=" + dateCalendar + ", houre=" + houre + ", idRoom=" + idRoom + ", idCourse=" + idCourse
				+ ", idUser=" + idUser + ", idCalendar=" + idCalendar + "]";
	}

}
