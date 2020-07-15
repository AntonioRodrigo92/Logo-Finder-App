import java.io.Serializable;

public class ImagemNomeByte implements Serializable {
	private byte[] imagem;
	private String nome;
	private int index;
	
	public ImagemNomeByte (byte[] imagem, String nome, int index) {
		this.imagem = imagem;
		this.nome = nome;
		this.index = index;
	}

	public byte[] getImagem() {
		return imagem;
	}

	public String getNome() {
		return nome;
	}
	
	public int getIndex() {
		return index;
	}

}
