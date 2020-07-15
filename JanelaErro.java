import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JanelaErro {
	private JFrame frame;
	
	
	public JanelaErro() {
		frame = new JFrame("Ocorreu um erro");
		frame.setSize(600, 100);
		addFrameContent();
	}
	
	public void init() {
		frame.setVisible(true);
	}
	
	private void addFrameContent() {
		JPanel painel = new JPanel();	
		JLabel label1 = new JLabel("Ocorreu um erro: foram reduzidas as opções de procura.");
		JLabel label2 = new JLabel("Tente novamente mais tarde");
		painel.add(label1);
		painel.add(label2);
		frame.add(painel);
	}
}
