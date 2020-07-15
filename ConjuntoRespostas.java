import java.util.HashMap;

public class ConjuntoRespostas {
	private HashMap<Integer, FilaBloqueante<RespostaTrabalhador> > matrizDeRespostas;
	private HashMap<Integer, FilaBloqueante<String> > matrizDeTipos;
	private int numClientes;
	
	public ConjuntoRespostas() {
		matrizDeRespostas = new HashMap<>();
		matrizDeTipos = new HashMap<>();
		numClientes = 0;
	}
	
	public void acrescentarFila(int id) {
		FilaBloqueante<RespostaTrabalhador> filaRespostas = new FilaBloqueante<>();
		matrizDeRespostas.put(id, filaRespostas);
	}
	
	public void acrescentarFilaTipos (int id) {
		FilaBloqueante<String> filaTipos = new FilaBloqueante<>();
		matrizDeTipos.put(id, filaTipos);
	}
	
	public void adicionarAoConjunto (int id, RespostaTrabalhador resposta) {
		try {
			matrizDeRespostas.get(id).offer(resposta);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void adicionarAoConjuntoTipos (int id, String tipo) {
		try {
			System.out.println("ConjuntoRespostas: adicionado tipo à fila bloqueante numero " + id);
			matrizDeTipos.get(id).offer(tipo);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void retirarAoConjuntoTipos (int id, String tipo) {
		matrizDeTipos.get(id).remove(tipo);
	}
	
	public FilaBloqueante<RespostaTrabalhador> getFilaRespostas (int id) {
		return matrizDeRespostas.get(id);
	}
	
	public FilaBloqueante<String> getFilaTipos (int id) {
		return matrizDeTipos.get(id);
	}
	
	public int getNumClientes() {
		return numClientes;
	}
	
	public void incrementarNumClientes() {
		numClientes++;
	}

}
