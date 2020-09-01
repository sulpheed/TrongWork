package msgSender;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.EventHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MsgSndClient2 extends JFrame implements Runnable,ActionListener,MouseListener{
	private static String target = "unknown";

	public static String getTarget() {
		return target;
	}

	public static void setTarget(String target) {
		MsgSndClient2.target = target;
	}
	public static void resetTarget() {
		target = "unknown";
	}
	public static void main(String[] args) {
		MsgSndClient2 window = new MsgSndClient2();
		window.setSize(800,600);
		window.setVisible(true);
	}

	private static final String APPNAME = "メッセンジャークライアント";
	private static String HOST ="localhost";
	private static int PORT_NO = 2999;

	private Socket socket;
	private Thread thread;

	private DefaultListModel<String> userListModel = new DefaultListModel<String>();

	private JTextField hostTextField;
	private JTextField portTextField;
	private JList<String> userList;
	private JTextArea msgTextArea;
	private JTextField msgTextField;
	private JTextField nameTextField;
	private JButton connectButton;
	private JButton submitButton;
	private JButton renameButton;
	private JButton deselectButton;

	public MsgSndClient2() {
		super(APPNAME);

		JPanel topPanel = new JPanel();
		JPanel leftPanel = new JPanel();
		JPanel buttomPanel = new JPanel();

		JPanel userPanel = new JPanel();

		hostTextField = new JTextField(HOST);
		portTextField = new JTextField(PORT_NO);
		userList = new JList<String>(userListModel);
		msgTextArea = new JTextArea();
		msgTextField = new JTextField();
		nameTextField = new JTextField();
		connectButton = new JButton("接続");
		submitButton = new JButton("送信");
		renameButton = new JButton("名前の変更");
		deselectButton = new JButton("対象削除");

		connectButton.addActionListener(this);
		connectButton.setActionCommand("connect");

		submitButton.addActionListener(this);
		submitButton.setActionCommand("submit");

		renameButton.addActionListener(this);
		renameButton.setActionCommand("rename");
		
		deselectButton.addActionListener(this);
		deselectButton.setActionCommand("deselect");

		userPanel.setLayout(new BorderLayout(1,2));
		userPanel.add(new JLabel("参加ユーザー"),BorderLayout.NORTH);
		userList.addMouseListener(this);
		userPanel.add(deselectButton, BorderLayout.AFTER_LAST_LINE);
		userPanel.add(new JScrollPane(userList),BorderLayout.CENTER);

		topPanel.setLayout(new GridLayout(2,4));
		topPanel.add(new JLabel("ホスト名："),BorderLayout.WEST);
		topPanel.add(hostTextField,BorderLayout.WEST);
		topPanel.add(new JLabel("ホスト番号："),BorderLayout.WEST);
		topPanel.add(portTextField,BorderLayout.WEST);
		topPanel.add(connectButton,BorderLayout.WEST);
		topPanel.add(new JLabel("名前"),BorderLayout.CENTER);
		topPanel.add(nameTextField,BorderLayout.CENTER);
		topPanel.add(renameButton,BorderLayout.CENTER);

		nameTextField.setPreferredSize(new Dimension(200, nameTextField.getPreferredSize().height));

		leftPanel.setLayout(new GridLayout(1,1));
		leftPanel.add(userPanel);

		buttomPanel.setLayout(new BorderLayout());
		buttomPanel.add(msgTextField,BorderLayout.CENTER);
		buttomPanel.add(submitButton,BorderLayout.EAST);

		msgTextArea.setEditable(false);

		this.getContentPane().add(new JScrollPane(msgTextArea),BorderLayout.CENTER);
		this.getContentPane().add(topPanel,BorderLayout.NORTH);
		this.getContentPane().add(leftPanel,BorderLayout.WEST);
		this.getContentPane().add(buttomPanel,BorderLayout.SOUTH);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {close();}
				catch(Exception err){}
			}
		});
		connectServer();

		thread = new Thread(this);
		thread.start();
	}

	public void connectServer() {
		try {
			socket = new Socket(HOST,PORT_NO);
			msgTextArea.append(">サーバーに接続しました\n");
		}
		catch(Exception err) {
			msgTextArea.append("ERROR>" + err + "\n");
		}
	}

	public void close() throws IOException{
		sendMessage("close");
		socket.close();
	}

	public void sendMessage(String msg) {
		try {
//			OutputStream output = socket.getOutputStream();
//			PrintWriter writer = new PrintWriter(output);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream(),StandardCharsets.UTF_8),true);

			writer.println(msg);
			writer.flush();
		}
		catch(Exception err) {msgTextArea.append("ERROR>" + err + "\n");}
	}

	public void reachedMessage(String name,String target, String value) {
		if(name.equals("users")) {
			if(value.equals("")) {
				userList.setModel(new DefaultListModel<String>());
			}
			else {
				String[] users = value.split(" ");
				userList.setListData(users);
			}
		}
		else if (name.equals("snd")) {
			msgTextArea.append(target + ">" + value + "\n");
		}
		else if (name.equals("msg")) {
			msgTextArea.append(target + ">" + value + "\n");
		}
		else if (name.equals("sucessful")) {
			if (value.equals("setName")) msgTextArea.append(">名前を変更しました\n");
		}
		else if (name.equals("error")) {
			msgTextArea.append("ERROR>" + value + "\n");
		}
	}

	public void run() {
		try {
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader ( new InputStreamReader(input,StandardCharsets.UTF_8));
			while(!socket.isClosed()) {
				String line = reader.readLine();
				String[] msg = line.split(" ",3);
				String msgName = msg[0];
				String msgTarget = (msg.length < 3 ? "" : msg[1]);
				String msgValue = (msg.length < 3 ? "" : msg[2]);
				reachedMessage(msgName,msgTarget,msgValue);
			}
		}
		catch(Exception err) {}
	}
	public void actionPerformed (ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd.equals("connect")) {
			int wkport = Integer.parseInt(portTextField.getText());
			if((wkport >= 1024) && (wkport <= 65535)) {
				PORT_NO = wkport;
			}else {
				msgTextArea.append("ERROR:ポート番号は1024～65535で指定" + "\n");
				return;
			}
			try {
				close();
			}catch(IOException err) {err.printStackTrace();}
			connectServer();
		}
		else if ( cmd.equals("submit")) {
			sendMessage("snd " + getTarget() + " " + msgTextField.getText());
			msgTextField.setText("");
		}
		else if ( cmd.equals("rename")) {
			sendMessage("setName " + getTarget() + " " + nameTextField.getText());
		}else if( cmd.equals("deselect")) {
			resetTarget();
		}
		sendMessage("getUsers");
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		JList list = (JList)e.getSource();
		String value = (String)list.getModel().getElementAt(list.locationToIndex(e.getPoint()));
        target = value;
        System.out.println(value);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
