import java.io.Serializable;
import java.util.ArrayList;

public class RespostaTrabalhador implements Serializable {
	private int id;
	private ArrayList<Coordenadas> listaCoordenadas;
	private int index;
	private int numeroVezes;
	
	public RespostaTrabalhador (int id, ArrayList<Coordenadas> listaCoordenadas, int index, int numeroVezes) {
		this.id = id;
		this.listaCoordenadas = listaCoordenadas;
		this.index = index;
		this.numeroVezes = numeroVezes;
	}

	public ArrayList<Coordenadas> getListaCoordenadas() {
		return listaCoordenadas;
	}

	public int getIndex() {
		return index;
	}
	
	public int getNumeroVezes() {
		return numeroVezes;
	}

	public int getId() {
		return id;
	}
}
