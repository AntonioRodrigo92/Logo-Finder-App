import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class DealWithClient extends Thread {
	private ObjectInputStream inClient;
	private ObjectOutputStream outClient;
	private Socket socket;
	private Pedido pedido;
	private FilaBloqueante<Tarefa> filaTarefas0;
	private FilaBloqueante<Tarefa> filaTarefas90;
	private FilaBloqueante<Tarefa> filaTarefas180;
	private FilaBloqueante<Tarefa> filaTarefas270;
	private FilaBloqueante<RespostaTrabalhador> filaRespostas;
	private ArrayList<String> totalWorkers;
	private int id;
	
	public DealWithClient (Socket socket, ObjectInputStream inClient, ObjectOutputStream outClient, int id, FilaBloqueante<Tarefa> filaTarefas0, FilaBloqueante<Tarefa> filaTarefas90, FilaBloqueante<Tarefa> filaTarefas180, FilaBloqueante<Tarefa> filaTarefas270, FilaBloqueante<RespostaTrabalhador> filaRespostas, ArrayList<String> totalWorkers) {
		this.socket = socket;
		this.inClient = inClient;
		this.outClient = outClient;
		this.filaTarefas0 = filaTarefas0;
		this.filaTarefas90 = filaTarefas90;
		this.filaTarefas180 = filaTarefas180;
		this.filaTarefas270 = filaTarefas270;
		this.filaRespostas = filaRespostas;
		this.id = id;
		this.totalWorkers = totalWorkers;
	}
	
	public void run() {
		System.out.println("DEAL WITH CLIENT: client Connected at " + socket);
		enviarID();
		new EnviarProcurasDisponiveis().start();
		new ReceberDoCliente().start();
		new EnviarRespostaAoCliente().start();
	}
	
	private void enviarID() {
		try {
			outClient.writeObject(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class EnviarRespostaAoCliente extends Thread {
		public void run() {
			try {
				while (true) {
					System.out.println("DealWithClient: enviar uma resposta???");
					RespostaTrabalhador resposta = filaRespostas.poll();
					enviarAoCliente(resposta);
					System.out.println("DealWithClient: resposta (index) enviada ao cliente " + resposta.getIndex());
					System.out.println("DealWithClient: " + filaRespostas.size());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class EnviarProcurasDisponiveis extends Thread {
		public void run() {
			System.out.println("Deal With Client: dentro do EnviarProcurasDisponiveis");
			try {
				int procurasDisponiveisAntigas = 0;
				while (true) {
					if (totalWorkers.size() != procurasDisponiveisAntigas) {
						outClient.reset();
						enviarAoCliente(totalWorkers);
						System.out.println("Enviada nova lista ao cliente");
						procurasDisponiveisAntigas = totalWorkers.size();
					}
					sleep(1250);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class ReceberDoCliente extends Thread {
		public void run() {
			try {
				while (true) {
					pedido = (Pedido) inClient.readObject();
					System.out.println("DEAL WITH CLIENT RECEBEU PEDIDO!!!");
					byte[] logo = pedido.getLogo();
					
					for (int i = 0; i < pedido.getSize(); i++) {
						byte[] imagem = pedido.getListaImagens().get(i).getImagem();
						String nome = pedido.getListaImagens().get(i).getNome();
						int index = pedido.getListaImagens().get(i).getIndex();
						int id = pedido.getId();
						ArrayList<String> tipo = pedido.getTipo();
						
						for (String tp : tipo) {
							System.out.println("O TIPO É: " + tp);
							Tarefa t = new Tarefa (id, imagem, nome, logo, index, tp);
							if (tp.equals("trabalhador0")) {
								try {
									filaTarefas0.offer(t);
									System.out.println("DEAL WITH CLIENT METEU EM RECURSO PARTILHADO!!!");
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							if (tp.equals("trabalhador90")) {
								try {
									filaTarefas90.offer(t);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							if (tp.equals("trabalhador180")) {
								try {
									filaTarefas180.offer(t);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							if (tp.equals("trabalhador270")) {
								try {
									filaTarefas270.offer(t);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("O cliente " + id + " desligou-se");
			}
		}
	}
	
	private synchronized void enviarAoCliente (Object obj) {
		try {
			outClient.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
