package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MajBdd {

    public static void updateBDD(String fileName) {

        String str = fileName;

        //enlever l'extension
        Pattern p = Pattern.compile("([a-zA-Z_0-9-0-9]*:\\d{2}:\\d{2})");
        Matcher m = p.matcher(str);
        m.find();
        String avEXT = m.group();
        System.out.println("avant extension = " + avEXT + "\n");

        //recuperer le numero de la salle
        Pattern p1 = Pattern.compile("(^[a-zA-Z]\\d+)");
        Matcher m1 = p1.matcher(avEXT);
        m1.find();
        String salle = m1.group();
        System.out.println("salle = " + salle + "\n");

        //recuperer la date
        Pattern p2 = Pattern.compile("(19|20)\\d\\d([- /.])(0[1-9]|1[012])\\2(0[1-9]|[12][0-9]|3[01])");
        Matcher m2 = p2.matcher(avEXT);
        m2.find();
        String date = m2.group();
        System.out.println("date = " + date + "\n");

        //recuperer l'heure
        Pattern p3 = Pattern.compile("(\\d{2}:\\d{2}:\\d{2}$)");
        Matcher m3 = p3.matcher(avEXT);
        m3.find();
        String heure = m3.group();
        System.out.println("heure = " + heure + "\n");

        Persistence pr = new Persistence();

        //recuperer l'idRoom
        Room room = new Room();
        room.setRoomNumber(salle);
        room = pr.readRoom(room);
        System.out.println(room.toString());

        //recuperer l'id cours
        Calendar calendar = new Calendar();
        calendar.setDate(date);
        calendar.setHoure(heure);
        calendar.setIdRoom(room.getIdRoom());
        calendar = pr.readCalendar(calendar);

        System.out.println("l'utilisateur esssssst " + calendar.getIdUser());
        System.out.println(calendar.toString());

        //insirer la video
        Video video = new Video();
        video.setIdCourse(calendar.getIdCourse());
        video.setPath(str);

        pr.persistVideo(video);

    }

}
