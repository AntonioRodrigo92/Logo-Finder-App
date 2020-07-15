import java.awt.image.BufferedImage;

public class ImagemNomeBuff {
	private BufferedImage imagem;
	private String nome;
	private int index;
	
	public ImagemNomeBuff (BufferedImage imagem, String nome, int index) {
		this.imagem = imagem;
		this.nome = nome;
		this.index = index;
	}

	public BufferedImage getImagem() {
		return imagem;
	}

	public String getNome() {
		return nome;
	}
	
	public int getIndex() {
		return index;
	}
}