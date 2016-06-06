package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.plaf.basic.BasicScrollPaneUI.VSBChangeListener;

public class Persistence {

	@SuppressWarnings("unused")
	static void persistVideo(Video video) {
		try {

			String insertVideoQuery = "INSERT INTO video (id_Video, path, nb_Like, id_Course) VALUES (?,?,?,?)";

			Connection dbConnection = JdbcConnection.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(insertVideoQuery);

			// Set values of parameters in the query.
			preparedStatement.setInt(1, video.getIdVideo());
			preparedStatement.setString(2, video.getPath());
			preparedStatement.setInt(3, video.getNbLike());
			preparedStatement.setInt(4, video.getIdCourse());

			preparedStatement.executeUpdate();

			preparedStatement.close();
		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
	}

	@SuppressWarnings("unused")
	static Calendar readCalendar(Calendar calendar) {
		Calendar readCalendar = new Calendar();
		try {

			String selectCalendarQuery = "SELECT * FROM calendar AS c WHERE c.calendar_date = ? AND c.houre = ? AND c.id_room = ?";

			Connection dbConnection = JdbcConnection.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(selectCalendarQuery);

			preparedStatement.setString(1, calendar.getDate());
			preparedStatement.setString(2, calendar.getHoure());
			preparedStatement.setInt(3, calendar.getIdRoom());

			ResultSet result = preparedStatement.executeQuery();

			while (result.next()) {
				readCalendar.setIdCalendar(result.getInt("id_calendar"));
				readCalendar.setDate(result.getString("calendar_date"));
				readCalendar.setHoure(result.getString("houre"));
				readCalendar.setIdRoom(result.getInt("id_room"));
				readCalendar.setIdCourse(result.getInt("id_course"));
				readCalendar.setIdUser(result.getInt("id_user"));
			}

			preparedStatement.close();

		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
		return readCalendar;
	}

	static Room readRoom(Room room) {
		Room readRoom = new Room();
		try {

			String selectRoomQuery = "SELECT * FROM room AS r WHERE r.room_number = ?";

			Connection dbConnection = JdbcConnection.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(selectRoomQuery);

			preparedStatement.setString(1, room.getRoomNumber());

			ResultSet result = preparedStatement.executeQuery();

			while (result.next()) {
				readRoom.setIdRoom(result.getInt("id_room"));
				readRoom.setRoomNumber(result.getString("room_number"));
			}

			preparedStatement.close();

		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
		return readRoom;
	}

	static RoomPlanning readRoomPlanning(RoomPlanning roomPlanning) {
		RoomPlanning readRoomPlanning = new RoomPlanning();
		try {

			String selectRoomPlanningQuery = "SELECT * FROM room_planning AS rp WHERE rp.id_room = ?";

			Connection dbConnection = JdbcConnection.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(selectRoomPlanningQuery);

			preparedStatement.setInt(1, roomPlanning.getIdRoom());

			ResultSet result = preparedStatement.executeQuery();

			while (result.next()) {
				readRoomPlanning.setPlanningDate(result.getString("planning_date"));
				readRoomPlanning.setHoure(result.getString("houre"));
				readRoomPlanning.setIdRoom(result.getInt("id_room"));
				readRoomPlanning.setIdPlanning(result.getInt("id_planning"));
			}

			preparedStatement.close();

		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
		return readRoomPlanning;
	}
	
	static ModulePlanning readModulelanning(ModulePlanning modulePlanning) {
		ModulePlanning readModulePlanning = new ModulePlanning();
		try {

			String selectRoomPlanningQuery = "SELECT * FROM module_planning AS mp WHERE mp.id_planning = ?";

			Connection dbConnection = JdbcConnection.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(selectRoomPlanningQuery);

			preparedStatement.setInt(1, modulePlanning.getIdPlanning());

			ResultSet result = preparedStatement.executeQuery();

			while (result.next()) {
				readModulePlanning.setIdPlanning(result.getInt("id_planning"));
				readModulePlanning.setIdModule(result.getInt("id_module"));
			}

			preparedStatement.close();

		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
		return readModulePlanning;
	}

}
