import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public abstract class Worker {
	private int PORT;
	private String add;
	private Socket socket;
	private ObjectInputStream inServer;
	protected ObjectOutputStream outServer;
	protected BufferedImage imagem;
	protected BufferedImage logo;
	protected int index;
	protected int id;
	protected RespostaTrabalhador resposta;
	
	public Worker(String add, int port)  {
		this.PORT = port;
		this.add = add;
		try {
			ConnectToServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void init() {
		System.out.println("WORKER: ENTROU EM INIT");
		while(! Thread.interrupted()) {
			oBoloTodo();
		}
	}
	
	protected void ConnectToServer() throws IOException {
		InetAddress address = InetAddress.getByName(add);
		socket = new Socket (address, PORT);
		System.out.println("WORKER: criou a socket");
		outServer = new ObjectOutputStream(socket.getOutputStream());
		inServer = new ObjectInputStream(socket.getInputStream());
		mandarId();
	}
	
	private BufferedImage convertToBufferedImage (byte[] img, String nome) throws IOException {
		InputStream in = new ByteArrayInputStream(img);
		BufferedImage buff = ImageIO.read(in);
		ImageIO.write(buff, "png", new File(nome + ".png"));
		return buff;
	}
	
	protected abstract void mandarId();
	
	private void enviarResultado() {
		try {
			outServer.writeObject(resposta);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void encontrar() {
		ArrayList<Coordenadas> respostaCoordenadas = new ArrayList<>();
		int numeroVezes = 0;
		for (int a = 0; a < imagem.getWidth() - logo.getWidth(); a++) {
			for (int b = 0; b < imagem.getHeight() - logo.getHeight(); b++) {
				if (mosaicocorrspondepixel(a, b)) {
					System.out.println("ENCONTREI!!!");
					numeroVezes++;
					Coordenadas c = new Coordenadas(a, b, logo.getWidth(), logo.getHeight());
					respostaCoordenadas.add(c);
				}
			}
		}
		this.resposta = new RespostaTrabalhador (id, respostaCoordenadas, index, numeroVezes);
	}
	
	private void oBoloTodo() {
		Tarefa tarefa;
		try {
			Object obj = inServer.readObject();
			if (obj instanceof Tarefa) {
				tarefa = (Tarefa) obj;
				System.out.println("WORKER: recebeu tarefa???");
				imagem = convertToBufferedImage (tarefa.getImagem(), tarefa.getNome());
				logo = convertToBufferedImage (tarefa.getLogo(), "logo");
				index = tarefa.getIndex();
				id = tarefa.getId();
				encontrar();
				enviarResultado();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("DESLIGOU-SE");
			Reconnect();
		}
	}
	
	private void Reconnect() {
		System.out.println("Worker está a tentar reconectar");
		for (int i = 0; i < 10; i++) {
			try {
				if (socket.isClosed()) {
					ConnectToServer();
					break;
				}
			} catch (IOException e) {
			}
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
			}
		}
		if (socket.isClosed()) {
			System.out.println("Reconecção falhada, vai desligar!");
			Thread.currentThread().interrupt();
		}
	}
	
	public abstract boolean mosaicocorrspondepixel (int a, int b);
}