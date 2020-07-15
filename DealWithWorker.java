import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class DealWithWorker extends Thread {
	private ObjectOutputStream outWorker;
	private ObjectInputStream inWorker;
	private Socket socket;
	private FilaBloqueante<Tarefa> filaTarefas;
	private ConjuntoRespostas conjuntoRespostas;
	private ArrayList<RespostaTrabalhador> conjuntoTotalRespostas;
	private ArrayList<String> totalWorkers;
	
	public DealWithWorker (String id, Socket socket, ObjectOutputStream outWorker, ObjectInputStream inWorker, FilaBloqueante<Tarefa> filaTarefas, ConjuntoRespostas conjuntoRespostas, ArrayList<String> totalWorkers) {
		this.socket = socket;
		this.outWorker = outWorker;
		this.inWorker = inWorker;
		this.filaTarefas = filaTarefas;
		this.conjuntoRespostas = conjuntoRespostas;
		this.conjuntoTotalRespostas = new ArrayList<>();
		this.totalWorkers = totalWorkers;
		new KaTaMorri(id).start();
	}
	
	public void run() {
		System.out.println("DEAL WITH WORKER: client Connected at " + socket);
		try {
			System.out.println("Deal with Worker conectou");
			while (true) {		
				enviarAoWorker();
				receberDoWorker();
			}
		} catch (IOException e) {
			System.out.println("DealWithWorker: IOException");
		} catch (ClassNotFoundException e) {
			System.out.println("DealWithWorker: ClassNotFoundException");
		}
	}
	
	private void enviarAoWorker() throws IOException {
		try {
			System.out.println("Deal With Worker: se parou aqui, a fila está vazia");
			Tarefa tarefa = filaTarefas.poll();
			System.out.println("Deal With Worker: retirada tarefa");
			mandarOutServer(tarefa);
			new Temporizador(tarefa).start();
			System.out.println("Deal With Worker: enviado ao worker " + tarefa.getNome());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void receberDoWorker() throws ClassNotFoundException, IOException {
		Object obj = inWorker.readObject();
		if (obj instanceof RespostaTrabalhador) {
			RespostaTrabalhador resposta = (RespostaTrabalhador) obj;
			conjuntoRespostas.adicionarAoConjunto(resposta.getId(), resposta);
			conjuntoTotalRespostas.add(resposta);
		}
		else {
			try {
				filaTarefas.offer((Tarefa)obj);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class KaTaMorri extends Thread {
		private String id;
		public KaTaMorri(String id) {
			this.id = id;
		}
		public void run() {
			try {
				while (true) {
					sleep(1000);
					mandarOutServer("");
				}
			} catch (InterruptedException | IOException e) {
				System.out.println(id + " parou");
				retirarProcurasDisponiveis(id);
			}
		}
	}
	
	private synchronized void retirarProcurasDisponiveis (String id) {
		totalWorkers.remove(id);
	}
	
	private class Temporizador extends Thread {
		private Tarefa tarefa;
		
		public Temporizador (Tarefa tarefa) {
			this.tarefa = tarefa;
		}
		public void run () {
			try {
				sleep(5000);
				if (! containsIndex(tarefa)) {
					System.out.println("a tarefa com index " + tarefa.getIndex() + " foi enviada há 5 segundos e não recebida");
					filaTarefas.offer(tarefa);
					System.out.println("tarefa reintroduzida na fila");
					inWorker.close();
					System.out.println("trabalhador morto");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean containsIndex (Tarefa tarefa) {
		for (RespostaTrabalhador r : conjuntoTotalRespostas) {
			if (r.getId() == tarefa.getId() && r.getIndex() == tarefa.getIndex()) {
				return true;
			}
		}
		return false;
	}
	
	private synchronized void mandarOutServer(Object obj) throws IOException {
		outWorker.writeObject(obj);
	}
}
