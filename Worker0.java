import java.io.IOException;

public class Worker0 extends Worker {
	public Worker0(String add, int port) {
		super(add, port);
	}

	@Override
	protected void mandarId() {
		String id = "trabalhador0";
		try {
			outServer.writeObject(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("WORKER: mandou out");
		
	}

	@Override
	public boolean mosaicocorrspondepixel (int a, int b) {
		for (int x = 0; x < logo.getWidth(); x++) {
			for (int y = 0; y < logo.getHeight(); y++) {
				if (imagem.getRGB(x + a, y + b) != logo.getRGB(x, y)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void main (String[] args) {
		String end = args[0];
		int p = Integer.parseInt(args[1]);
//		String end = "0.0.0.0";
//		int p = 8080;
		Worker w = new Worker0(end, p);
		w.init();
	}
}