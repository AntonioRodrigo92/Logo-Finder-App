import java.io.IOException;

public class Worker180 extends Worker {
	public Worker180(String add, int port) {
		super(add, port);
	}

	@Override
	protected void mandarId() {
		String id = "trabalhador180";
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
				if (imagem.getRGB(x + a, y + b) != logo.getRGB(logo.getWidth() - 1 - x, logo.getHeight() - 1 - y)) {
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
		Worker w = new Worker180(end, p);
		w.init();
	}
}
