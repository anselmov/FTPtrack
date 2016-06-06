package test;

public class Video {
	
	public Video() {
		super();
		// TODO Auto-generated constructor stub
	}

	private int idVideo;
	private String path;
	private int nbLike;
	private int idCourse;
	
	public int getIdVideo() {
		return idVideo;
	}

	public void setIdVideo(int idVideo) {
		this.idVideo = idVideo;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getNbLike() {
		return nbLike;
	}

	public void setNbLike(int nbLike) {
		this.nbLike = nbLike;
	}

	public int getIdCourse() {
		return idCourse;
	}

	public void setIdCourse(int idCourse) {
		this.idCourse = idCourse;
	}

	public Video(int idVideo, String path, int nbLike, int duration, int idCourse) {
		super();
		this.idVideo = idVideo;
		this.path = path;
		this.nbLike = nbLike;
		this.idCourse = idCourse;
	}
	
	

}
