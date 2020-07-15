import java.io.Serializable;
import java.util.ArrayList;

public class Pedido implements Serializable {
	private int id;
	private ArrayList<ImagemNomeByte> listaImagens;
	private byte[] logo;
	private ArrayList<String> tipo;
	
	public Pedido (int id, ArrayList<ImagemNomeByte> listaImagens, byte[] logo, ArrayList<String> tipo) {
		this.id = id;
		this.listaImagens = listaImagens;
		this.logo = logo;
		this.tipo = tipo;
	}
	
	public int getId() {
		return id;
	}

	public ArrayList<ImagemNomeByte> getListaImagens() {
		return listaImagens;
	}
	
	public byte[] getLogo() {
		return logo;
	}
	
	public int getSize() {
		return listaImagens.size();
	}
	
	public ArrayList<String> getTipo() {
		return tipo;
	}

}
