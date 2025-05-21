package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class SocketController {

	public String serverName;
	public int serverPort;
	public ServerSocket s;
	public List<Client> connectedClient;
	public List<Room> allRooms;
	public UserManager userManager;

	public SocketController() {
		connectedClient = new ArrayList<Client>();
		allRooms = new ArrayList<Room>();
		userManager = new UserManager();
	}

	public void OpenSocket(int port) {
		try {
			s = new ServerSocket(port);
			new Thread(() -> {
				while (!s.isClosed()) {
					try {
						Socket clientSocket = s.accept();
						new ClientCommunicateThread(clientSocket).start();
					} catch (IOException e) {
						if (!s.isClosed())
							e.printStackTrace();
					}
				}
			}).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void CloseSocket() {
		try {
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getThisIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return "127.0.0.1";
		}
	}
}
