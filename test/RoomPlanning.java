package test;

public class RoomPlanning {

	private String planningDate;
	private String houre;
	private int idRoom;
	private int idPlanning;

	public RoomPlanning() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RoomPlanning(String planningDate, String houre, int idRoom, int idPlanning) {
		super();
		this.planningDate = planningDate;
		this.houre = houre;
		this.idRoom = idRoom;
		this.idPlanning = idPlanning;
	}

	public String getPlanningDate() {
		return planningDate;
	}

	public void setPlanningDate(String planningDate) {
		this.planningDate = planningDate;
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

	public int getIdPlanning() {
		return idPlanning;
	}

	public void setIdPlanning(int idPlanning) {
		this.idPlanning = idPlanning;
	}

	@Override
	public String toString() {
		return "RoomPlanning [planningDate=" + planningDate + ", houre=" + houre + ", idRoom=" + idRoom
				+ ", idPlanning=" + idPlanning + "]";
	}

}
