import java.io.Serializable;

public class Tarefa implements Serializable  {
	private int id;
	private byte[] imagem;
	private String nome;
	private byte[] logo;
	private int index;
	private String tipo;
	
	
	public Tarefa (int id, byte[] imagem, String nome, byte[] logo, int index, String tipo) {
		this.id = id;
		this.imagem = imagem;
		this.nome = nome;
		this.logo = logo;
		this.index = index;
		this.tipo = tipo;
	}
	
	public int getId() {
		return id;
	}

	public byte[] getImagem() {
		return imagem;
	}

	public String getNome() {
		return nome;
	}

	public byte[] getLogo() {
		return logo;
	}
	
	public int getIndex() {
		return index;
	}
}
