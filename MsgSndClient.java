package msgSender;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MsgSndClient extends JFrame implements Runnable,ActionListener{

	public static void main(String[] args) {
		MsgSndClient window = new MsgSndClient();
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

	public MsgSndClient() {
		super(APPNAME);

		JPanel topPanel = new JPanel();
		JPanel leftPanel = new JPanel();
		JPanel bottomPanel = new JPanel();

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

		connectButton.addActionListener(this);
		connectButton.setActionCommand("connect");

		submitButton.addActionListener(this);
		submitButton.setActionCommand("submit");

		renameButton.addActionListener(this);
		renameButton.setActionCommand("rename");

		userPanel.setLayout(new BorderLayout());
		userPanel.add(new JLabel("参加ユーザー"),BorderLayout.NORTH);
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

		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(msgTextField,BorderLayout.CENTER);
		bottomPanel.add(submitButton,BorderLayout.EAST);

		msgTextArea.setEditable(false);

		this.getContentPane().add(new JScrollPane(msgTextArea),BorderLayout.CENTER);
		this.getContentPane().add(topPanel,BorderLayout.NORTH);
		this.getContentPane().add(leftPanel,BorderLayout.WEST);
		this.getContentPane().add(bottomPanel,BorderLayout.SOUTH);

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
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),StandardCharsets.UTF_8),true);

			writer.println(msg);
			writer.flush();
		}
		catch(Exception err) {msgTextArea.append("ERROR>" + err + "\n");}
	}

	public void reachedMessage(String name, String value) {
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
			msgTextArea.append(value + "\n");
		}
		else if (name.equals("msg")) {
			msgTextArea.append(value + "\n");
		}
		else if (name.equals("successful")) {
			if (value.equals("setName")) msgTextArea.append(">名前を変更しました\n");
		}
		else if (name.equals("error")) {
			msgTextArea.append("ERROR>" + value + "\n");
		}
	}

	public void run() {
		try {
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader (new InputStreamReader(input,StandardCharsets.UTF_8));
			while(!socket.isClosed()) {
				String line = reader.readLine();
				String[] msg = line.split(" ",2);
				String msgName = msg[0];
				String msgValue = (msg.length < 2 ? " " : msg[1]);
				reachedMessage(msgName,msgValue);
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
			sendMessage("snd " + msgTextField.getText());
			msgTextField.setText("");
		}
		else if ( cmd.equals("rename")) {
			sendMessage("setName " + nameTextField.getText());
		}
		sendMessage("getUsers");
	}
}
