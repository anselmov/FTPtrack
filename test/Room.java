package test;

public class Room {
	
	private int idRoom;
	private String roomNumber;
	
	public Room(int idRoom, String roomNumber) {
		super();
		this.idRoom = idRoom;
		this.roomNumber = roomNumber;
	}

	public int getIdRoom() {
		return idRoom;
	}

	public void setIdRoom(int idRoom) {
		this.idRoom = idRoom;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	@Override
	public String toString() {
		return "Room [idRoom=" + idRoom + ", roomNumber=" + roomNumber + "]";
	}

	public Room() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
