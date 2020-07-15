import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private int PORT;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private FilaBloqueante<Tarefa> filaTarefas0 = new FilaBloqueante<>();
	private FilaBloqueante<Tarefa> filaTarefas90 = new FilaBloqueante<>();
	private FilaBloqueante<Tarefa> filaTarefas180 = new FilaBloqueante<>();
	private FilaBloqueante<Tarefa> filaTarefas270 = new FilaBloqueante<>();
	private ConjuntoRespostas conjuntoRespostas = new ConjuntoRespostas();
	private ArrayList<String> totalWorkers = new ArrayList<>();
	
	public Server(int port) {
		this.PORT = port;
	}
	
	private void init() {
		try {
			ServerSocket ss = new ServerSocket(PORT);
			System.out.println("SERVER: Server started at " + ss);
			
			while(true) {
				Socket s = ss.accept();
				System.out.println("SERVER: a socket é: " + s);
				out = new ObjectOutputStream(s.getOutputStream());
				in = new ObjectInputStream(s.getInputStream());
				try {
					String id = (String) in.readObject();
					if (id.equals("cliente")) {
						conjuntoRespostas.incrementarNumClientes();
						conjuntoRespostas.acrescentarFila(conjuntoRespostas.getNumClientes());
						new DealWithClient(s, in, out, conjuntoRespostas.getNumClientes(), filaTarefas0, filaTarefas90, filaTarefas180, filaTarefas270, conjuntoRespostas.getFilaRespostas(conjuntoRespostas.getNumClientes()), totalWorkers).start();
						System.out.println("SERVER: DEAL WITH CLIENT CRIADO");
					}
					if (id.equals("trabalhador0")) {
						new DealWithWorker("trabalhador0", s, out, in, filaTarefas0, conjuntoRespostas, totalWorkers).start();
						totalWorkers.add("trabalhador0");
						System.out.println("SERVER: DEAL WITH WORKER0");
					}
					if (id.equals("trabalhador90")) {
						new DealWithWorker("trabalhador90", s, out, in, filaTarefas90, conjuntoRespostas, totalWorkers).start();
						totalWorkers.add("trabalhador90");
						System.out.println("SERVER: DEAL WITH WORKER90");
					}
					if (id.equals("trabalhador180")) {
						new DealWithWorker("trabalhador180", s, out, in, filaTarefas180, conjuntoRespostas, totalWorkers).start();
						totalWorkers.add("trabalhador180");
						System.out.println("SERVER: DEAL WITH WORKER180");
					}
					if (id.equals("trabalhador270")) {
						new DealWithWorker("trabalhador270", s, out, in, filaTarefas270, conjuntoRespostas, totalWorkers).start();
						totalWorkers.add("trabalhador270");
						System.out.println("SERVER: DEAL WITH WORKER270");
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		int p = Integer.parseInt(args[0]);
//		int p = 8080;
		new Server(p).init();
	}
}