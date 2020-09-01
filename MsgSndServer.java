package msgSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

public class MsgSndServer {
	private ArrayList<MsgSndClientUser> userList;
	private MsgSndServer() {
		userList = new ArrayList<MsgSndClientUser>();
	}

	private ServerSocket server;
	private static int PORT_NO = 2999;

	public static void main(String[] args) {
		if(args.length == 1) {
			int wkport = Integer.parseInt(args[0]);
			if((wkport >= 1024) && (wkport <= 65535)) PORT_NO = wkport;
		}
		System.out.println("口口 MsgSndServer Start 口口");
		MsgSndServer application = MsgSndServer.getInstance();
		application.start();
		System.out.println("口口 MsgSndServer End 口口");
	}

	private static MsgSndServer instance;
	public static MsgSndServer getInstance() {
		if(instance == null) {
			instance = new MsgSndServer();
		}
		return instance;
	}

	static ArrayList<String> history = new ArrayList<>();
	public void start() {
		try {
			System.out.println(" MsgSndServer PORT =" + PORT_NO);
			server = new ServerSocket(PORT_NO);
			
			while(!server.isClosed()) {
				Socket client = server.accept();
				System.out.println("MsgSndServer Accept=" + client.getLocalPort() + " " + client.getPort());

				MsgSndClientUser user = new MsgSndClientUser(client);
				addUser(user);
				MsgSndClientUser[] users = getUsers();
				//System.out.println(users.length);
				//System.out.println(history);
				for(String s : history) {
					users[users.length - 1].sendMessage(s);
				}
			}
			System.out.println("MsgSndServer Closed");
		}catch(Exception err) {
			err.printStackTrace();
		}

	}
	public void addUser(MsgSndClientUser user) {
		if(userList.contains(user)) return;

		userList.add(user);
		System.out.println("addUser=[" + user +"]");
	}

	public MsgSndClientUser getUser(String name) {
		for(int i = 0; i < userList.size();i++) {
			MsgSndClientUser user = userList.get(i);
			if(user.getName().equals(name)) return user;
		}
		return null;
	}

	public MsgSndClientUser[] getUsers() {
		MsgSndClientUser[] users = new MsgSndClientUser[userList.size()];
		userList.toArray(users);
		return users;
	}

	public void removeUser(MsgSndClientUser user) {
		userList.remove(user);
		System.out.println("removeUser=[" + user + "]");
	}

	public void clearUser() { userList.clear(); }

	public void close() throws IOException{
		server.close();
	}
}
class MessageEvent extends EventObject{
	private MsgSndClientUser source;
	private String name;
	private String target;
	private String value;

	public MessageEvent(MsgSndClientUser source ,String name,String target,String value) {
		super(source);
		this.source = source;
		this.name = name;
		this.target = target;
		this.value = value;
	}

	public MsgSndClientUser getUser() { return source; }

	public String getName(){ return name; }

	public String getTarget() {
		return target;
	}

	public String getValue() {
		return value;
	}
}
interface MessageListener extends EventListener{
	void messageThrow(MessageEvent e);
}

class MsgSndClientUser implements Runnable,MessageListener{
	private Socket socket;

	private String name = "notSetYet";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private MsgSndServer server = MsgSndServer.getInstance();

	private ArrayList<MessageListener> messageListeners;

	public MsgSndClientUser(Socket socket) {
		messageListeners = new ArrayList<MessageListener>();
		this.socket = socket;

		addMessageListener(this);

		Thread thread = new Thread(this);
		thread.start();
	}

	public void run() {
		try {

			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			while(!socket.isClosed()) {
				String line = reader.readLine();
				System.out.println("→INPUT=" + line);
				
				String[] msg = line.split(" ",3);
				String msgName = msg[0];
				
				String msgTarget = (msg.length < 3 ? "" : msg[1]);
				if(msgName.equals("snd") && msgTarget.equals("unknown")) {
					MsgSndServer.history.add(line.replace(msgTarget, this.getName()+"->all"));
				}
				String msgValue = (msg.length < 3 ? "" : msg[2]);
				System.out.println(msgName + " " + msgTarget + " " + msgValue);
				reachedMessage(msgName,msgTarget,msgValue);
			}
		}
		catch(Exception err) { err.printStackTrace(); }
	}

	public void messageThrow(MessageEvent e) {
		String msgType = e.getName();
		String msgTarget = e.getTarget();
		String msgValue = e.getValue();

		if(msgType.equals("snd")) {
			MsgSndClientUser[] users = server.getUsers();
			String temp = "all";
			if(msgTarget.equals("unknown")) {
				for(int i = 0 ; i < users.length ; i++) {
					if(this.getName().equals(users[i].getName())) {
						users[i].sendMessage(msgType + " " + "->" + temp + " " + msgValue);
					}else {
						users[i].sendMessage(msgType + " " + name + "->" + temp + " " + msgValue);
					}
				}
			}else {
				for(int i = 0 ; i < users.length ; i++) {
					if(msgTarget.equals(users[i].getName())) {
						temp = msgTarget;
						users[i].sendMessage(msgType + " " + name + " " + msgValue);
						this.sendMessage(msgType + " " + "->" + temp + " " + msgValue);
					}
				}
			}
		}else if(msgType.equals("close")) {
			try {
				close();
			}catch(IOException err) { err.printStackTrace(); }
		}else if(msgType.equals("setName")) {
			String name = msgValue;

			if(name.indexOf(" ") == -1) {
				String before = getName();
				setName(name);
				sendMessage("sucessful setName");
				reachedMessage("msg","unknown", before + " から " + name + "に名前を変更しました");

			}else {
				sendMessage("error 名前に半角空白文字を使うことはできません");
			}
		}

		else if(msgType.equals("getUsers")) {
			String result = "";
			MsgSndClientUser[] users = server.getUsers();
			for(int i = 0;i < users.length;i++) result += users[i].getName() + " " ;
			sendMessage("users " + "unknown " + result);
		}
	}
	public MsgSndClientUser selectU(String name) {
		MsgSndClientUser[] users = server.getUsers();
		for(int i = 0 ; i < users.length ; i++) {
			if(users[i].getName().equals(name)) return users[i];
		}
		return null;
	}
	public String toString() {
		return "NAME = " + getName();
	}

	public void close() throws IOException{
		server.removeUser(this);
		messageListeners.clear();
		socket.close();
	}

	public void sendMessage(String message) {
		try {
//			OutputStream output = socket.getOutputStream();
//			PrintWriter writer = new PrintWriter(output);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream(),StandardCharsets.UTF_8),true);

			writer.println(message);

			writer.flush();
			System.out.println("←sendMessage=" + message);
		}catch(Exception err) {}
	}

	public void reachedMessage(String name,String target,String value) {
		MessageEvent event = new MessageEvent(this,name,target,value);
		for(int i = 0;i < messageListeners.size();i++) {
			messageListeners.get(i).messageThrow(event);
		}
	}

	public void addMessageListener(MessageListener l) {
		messageListeners.add(l);
	}

	public void removeMessageListener(MessageListener l) {
		messageListeners.remove(l);
	}

	public MessageListener[] getMessageListeners() {
		MessageListener[] listeners = new MessageListener[messageListeners.size()];
		messageListeners.toArray(listeners);
		return listeners;
	}
}
