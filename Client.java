import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Client {
	private int PORT;
	private String add;
	private Socket socket;
	private JFrame frame;
	private JLabel viewer;
	private File[] files;
	private JList<String> listaDireita;
	private JList<String> listaEsquerda;
	private BufferedImage logo;
	private ObjectOutputStream outServer;
	private ObjectInputStream inServer;
	private List<ImagemNomeBuff> listaImagens;
	private ArrayList<RespostaTrabalhador> listaRespostas = new ArrayList<>();
	private HashMap<String, BufferedImage> mapaImagens = new HashMap<>();
	private ArrayList<String> tiposExistentes = new ArrayList<>();
	private int id;
	private int numProcura = 1;
	private int[] guardarQuantasProcuras = new int[256];
	
	public Client(String add, int port) {
		this.PORT = port;
		this.add = add;
		try {
			ConnectToServer();
			BuildGui();
			receberID();
			System.out.println("CLIENT: recebeu id!!!");
			new receberResposta().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void BuildGui() {
		frame = new JFrame("Find Images");
		viewer = new JLabel();
		String[] espaco = {"                                         "};
		listaDireita = new JList<String>(espaco);
		listaEsquerda = new JList<String>(espaco);
		frame.setLayout(new BorderLayout());
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addFrameContent();
	}
	
	private void ConnectToServer() throws IOException {
		InetAddress address = InetAddress.getByName(add);
		socket = new Socket (address, PORT);
		System.out.println("CLIENT: criou a socket");
		inServer = new ObjectInputStream(socket.getInputStream());
		outServer = new ObjectOutputStream(socket.getOutputStream());
		mandarId();
	}
	
	private void init() {
		frame.setVisible(true);
	}
	
	private void mandarId() {
		String id = "cliente";
		try {
			outServer.writeObject(id);
			System.out.println("CLIENT: enviou identificacao");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void receberID() {
		try {
			id = (int) inServer.readObject();
			System.out.println("Client: id = " + id);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void retirarRepetidos() {
		RespostaTrabalhador[] vetor = new RespostaTrabalhador[listaImagens.size()];
		System.out.println("tamanho LISTA DE IMAGENS = " + listaImagens.size());
		ArrayList<RespostaTrabalhador> temp = new ArrayList<>();
		for (RespostaTrabalhador rt : listaRespostas) {
			int index = rt.getIndex();
			System.out.println("INDEX É = " + index + " numero encontrados = " + rt.getNumeroVezes());
			if (vetor[index] == null) {
				System.out.println("INDEX É NULL");
				vetor[index] = rt;
			}
			else {
				System.out.println("INDEX NAO É NULL");
				vetor[index] = somaRespostaTrabalhador(vetor[index], rt);
			}
		}
		for (RespostaTrabalhador resp : vetor) {
			temp.add(resp);
		}
		System.out.println("TAMANHO DO VETOR RESPOSTAS = " + temp.size());
		listaRespostas = temp;
		System.out.println("TAMANHO DA LISTA DE RESPOSTAS = " + listaRespostas.size()); 
		for (RespostaTrabalhador resp : listaRespostas) {
			System.out.println("LISTA RESPOSTAS: indice: " + resp.getIndex() + " numero de vezes: " + resp.getNumeroVezes());
		}
	}
	
	private RespostaTrabalhador somaRespostaTrabalhador(RespostaTrabalhador rt1, RespostaTrabalhador rt2) {
		ArrayList<Coordenadas> listaCoordenadas = rt1.getListaCoordenadas();
		listaCoordenadas.addAll(rt2.getListaCoordenadas());
		int numeroVezes = rt1.getNumeroVezes() + rt2.getNumeroVezes();
		return new RespostaTrabalhador(id, listaCoordenadas, rt1.getIndex(), numeroVezes);
	}
	
	private void organizarListaRespostas() {
		listaRespostas.sort(RespostaComparator);
		for (int h = 0; h < listaRespostas.size(); h++) {
			if (listaRespostas.get(h).getNumeroVezes() != 0) {
				int index = listaRespostas.get(h).getIndex();
				String nome = listaImagens.get(index).getNome();
				BufferedImage i = listaImagens.get(index).getImagem();
				for (Coordenadas c : listaRespostas.get(h).getListaCoordenadas()) {
					int cx = c.getValorX();
					int cy = c.getValorY();
					int comp = c.getComp();
					int alt = c.getAlt();
					retanguloVermelho(cx, cy, comp, alt, i);
				}
				mapaImagens.put(nome, i);
			}
		}
	}
	
	Comparator<RespostaTrabalhador> RespostaComparator = (r1, r2) -> r2.getNumeroVezes() - r1.getNumeroVezes();
	
	private void retanguloVermelho(int a, int b, int c, int d, BufferedImage image) {
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.RED);
		g2d.drawRect(a, b, c, d);
		g2d.dispose();
	}
	
	private void addFrameContent() {
		JPanel painel = new JPanel();
		frame.add(listaDireita, BorderLayout.EAST);
		frame.add(listaEsquerda, BorderLayout.WEST);
		frame.add(painel, BorderLayout.SOUTH);
		JPanel painelDePastas = new JPanel();
		painel.setLayout(new GridLayout(1, 2));
		painelDePastas.setLayout(new BorderLayout());		
		JButton pastas = new JButton("Pasta");
		JButton imagem = new JButton("Imagem");
		JButton procura = new JButton("Procura");
		
		painel.add(procura);
		painelDePastas.add(pastas, BorderLayout.NORTH);
		painelDePastas.add(imagem,BorderLayout.SOUTH);
		painel.add(painelDePastas);
		
		//BUTAO PROCURAR PASTA IMAGENS
		pastas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(doProcurarImagens);
			}
		});
		
		//BUTAO PROCURAR LOGO
		imagem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(doProcurarLogo);
			}
		});
		
		//BUTAO PROCURAR (cruzar imagens n' shit)!
		procura.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				SwingUtilities.invokeLater(doButaoProcurar);
				new btnProcurar().start();
			}
		});
		
		//BUTAO PROCURAR LOGO
	}
	
	private byte[] convertToByteArray (BufferedImage imagem) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ImageIO.write(imagem, "png", baos);
				baos.flush();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			byte[] imagemEmBytes = baos.toByteArray();
			return imagemEmBytes;
	}
	
	final Runnable doProcurarImagens = new Runnable() {
		public void run() {
			listaRespostas.clear();
			JFileChooser jfc = new JFileChooser(".");
			 jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);		 
			 int returnValue = jfc.showOpenDialog(null);
			 
			 if (returnValue == JFileChooser.APPROVE_OPTION) {
				 File selectedFile = jfc.getSelectedFile();
				 files = new File(selectedFile.getAbsolutePath()).listFiles(new FileFilter() {
				     public boolean accept(File f) {    
				          return true;
				     }			     
				});
			 }
		}
	};
	
	final Runnable doProcurarLogo = new Runnable() {
		public void run() {
			JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int returnValue = jfc.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = jfc.getSelectedFile();
				try {
					logo = ImageIO.read(new File(selectedFile.getAbsolutePath()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			 }
		}
	};
	
	final Runnable doAdicionarPainelResposta = new Runnable() {
		public void run() {
			String[] nomes = new String[listaRespostas.size()];
			for(int j = 0; j < listaRespostas.size(); j++) {
				if (listaRespostas.get(j).getNumeroVezes() != 0) {
					int ind = listaRespostas.get(j).getIndex();
					nomes[j] = listaImagens.get(ind).getNome();
				}
			}
			listaDireita.setListData(nomes);
			System.out.println("Adicionou painel respostas!!!");
		}
	};
	
	final Runnable doAdicionarImagens = new Runnable() {
		public void run() {
			listaDireita.addListSelectionListener(new ListSelectionListener(){
				@Override
				public void valueChanged(ListSelectionEvent e) {
					String selectedValue = listaDireita.getSelectedValue();
					if (selectedValue != null) {
						ImageIcon icon = new ImageIcon (mapaImagens.get(selectedValue));
						viewer.setIcon(icon);
						frame.add(viewer);
						System.out.println("Imagens devem aparecer");
					}
				}
			});
		}
	};
	
	final Runnable doAtualizarListaEsquerda = new Runnable() {
		public void run() {
			String[] tipos = new String[tiposExistentes.size()];
			for (int j = 0; j < tiposExistentes.size(); j++) {
				tipos[j] = tiposExistentes.get(j);
			}
			listaEsquerda.setListData(tipos);
		}
	};
	
	private class receberResposta extends Thread {
		public void run() {
			try {
				while (true) {
					int valorFixo = guardarQuantasProcuras[numProcura - 1];
					Object obj = inServer.readObject();
					if (obj instanceof RespostaTrabalhador) {
						RespostaTrabalhador resposta = (RespostaTrabalhador) obj;
						System.out.println("CLIENT: recebeu resposta (index) " + resposta.getIndex());
						listaRespostas.add(resposta);
						System.out.println("CLIENT: tamanho listaRespostas = " + listaRespostas.size());
						System.out.println("CLIENT: tamanho listaImagens = " + listaImagens.size());
						System.out.println("CLIENT: tamanho dos pedidos = " + valorFixo);
						if (listaRespostas.size() == listaImagens.size() * valorFixo) {
							System.out.println("CLIENT: RECEBEU TUDO!!!");
							retirarRepetidos();		
							organizarListaRespostas();
							SwingUtilities.invokeLater(doAdicionarPainelResposta);
							SwingUtilities.invokeLater(doAdicionarImagens);
						}
					}
					if (obj instanceof ArrayList) {
						ArrayList<String> lol = (ArrayList<String>) obj;
						System.out.println("CLIENT: numero de procuras disponiveis = " + lol.size());
						ArrayList<String> aux = new ArrayList<>();
						for (String s : lol) {
							if (! aux.contains(s)) {
								aux.add(s);
							}
						}
						tiposExistentes = aux;
						for (String s : listaEsquerda.getSelectedValuesList()) {
							if (! tiposExistentes.contains(s)) {
								System.out.println("ERRO!!!");
								JanelaErro janela = new JanelaErro();
								janela.init();
							}
						}
						SwingUtilities.invokeLater(doAtualizarListaEsquerda);
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class btnProcurar extends Thread {
		public void run() {	
			guardarQuantasProcuras[numProcura] = listaEsquerda.getSelectedValuesList().size();
			numProcura++;
			
			
			System.out.println("Carreguei em PROCURAR!!!");
			ArrayList<ImagemNomeByte> imgsEnviar = new ArrayList<>();
			listaImagens = new LinkedList<>();
			for (int i = 0; i < files.length; i++) {
				byte[] imagem;
				BufferedImage imagemBuff;
				try {
					imagem = convertToByteArray(ImageIO.read(files[i]));
					imagemBuff = ImageIO.read(files[i]);
					String nome = files[i].getName();
					ImagemNomeByte imgComNome = new ImagemNomeByte(imagem, nome, i);
					ImagemNomeBuff inb = new ImagemNomeBuff(imagemBuff, nome, i);
					imgsEnviar.add(imgComNome);					
					listaImagens.add(inb);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			byte[] logoEnviar = convertToByteArray(logo);
			Pedido p = new Pedido(id, imgsEnviar, logoEnviar, (ArrayList<String>) listaEsquerda.getSelectedValuesList());
			System.out.println("depois do primeiro try!!!");
			try {
				System.out.println("CLIENTE: antes de enviar pedido!!!");
				System.out.println("CLIENTE: tamanho da lista a enviar = " + p.getSize());
				outServer.writeObject(p);
				System.out.println("CLIENTE: Enviou o pedido?");				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static void main (String[] args) {
		String end = args[0];
		int p = Integer.parseInt(args[1]);
//		String end = "0.0.0.0";
//		int p = 8080;
		Client c = new Client(end, p);
		c.init();
	}
}
