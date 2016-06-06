package test;

public class ModulePlanning {

	private int idPlanning;
	private int idModule;

	public ModulePlanning() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ModulePlanning(int idPlanning, int idModule) {
		super();
		this.idPlanning = idPlanning;
		this.idModule = idModule;
	}

	public int getIdPlanning() {
		return idPlanning;
	}

	public void setIdPlanning(int idPlanning) {
		this.idPlanning = idPlanning;
	}

	public int getIdModule() {
		return idModule;
	}

	public void setIdModule(int idModule) {
		this.idModule = idModule;
	}

	@Override
	public String toString() {
		return "ModulePlanning [idPlanning=" + idPlanning + ", idModule=" + idModule + "]";
	}

}
