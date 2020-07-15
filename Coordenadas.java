import java.io.Serializable;

public class Coordenadas implements Serializable {
	private int valorX;
	private int valorY;
	private int comp;
	private int alt;
	
	public Coordenadas (int x, int y, int comp, int alt) {
		this.valorX = x;
		this.valorY = y;
		this.comp = comp;
		this.alt = alt;
	}
	
	public int getValorX() {
		return valorX;
	}
	
	public int getValorY() {
		return valorY;
	}
	
	public int getComp() {
		return comp;
	}
	
	public int getAlt() {
		return alt;
	}

}
