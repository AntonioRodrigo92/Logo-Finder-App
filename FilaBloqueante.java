import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

public class FilaBloqueante <T> implements Serializable {
	private Deque<T> fila;
	
	public FilaBloqueante () {
		fila = new ArrayDeque<>();
	}
	
	public synchronized void offer (T t) throws InterruptedException {
		fila.addLast(t);
		notifyAll();
	}
	
	public synchronized T poll () throws InterruptedException {
		while (fila.size() <= 0) {
			wait();
		}
		notifyAll();
		return fila.poll();
	}
	
	public int size() {
		return fila.size();
	}
	
	public void clear() {
		fila.clear();
	}
	
	public boolean empty() {
		return fila.size() == 0;
	}
	
	public void remove (T t) {
		fila.removeFirstOccurrence(t);
	}
}