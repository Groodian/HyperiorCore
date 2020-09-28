package de.groodian.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public abstract class Client {

	private DataPackage loginPack;
	private InetSocketAddress address;

	private Socket login;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	private Thread mainThread;
	private boolean listening;

	public Client(String hostname, int port, DataPackage loginPack) {
		this.loginPack = loginPack;
		address = new InetSocketAddress(hostname, port);
		listening = true;

		mainThread = new Thread(new Runnable() {
			@Override
			public void run() {
				login();
			}
		});
		mainThread.start();
	}

	protected abstract void handleDataPackage(DataPackage dataPackage);

	public void sendMessage(DataPackage pack) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					if (oos != null) {
						oos.writeObject(pack);
						oos.flush();
					} else {
						System.err.println("[Client] Message could not be sent, because the client is not logged in!");
					}
				} catch (Exception e) {
					System.err.println("[Client] Message could not be sent!");
				}

			}
		}).start();
	}

	private void login() {
		try {
			System.out.println("[Client] Connecting...");
			login = new Socket();
			login.connect(address, 5000);
			System.out.println("[Client] Connected to: " + login.getRemoteSocketAddress());

			System.out.println("[Client] Logging in...");
			oos = new ObjectOutputStream(new BufferedOutputStream(login.getOutputStream()));
			oos.writeObject(loginPack);
			oos.flush();
			System.out.println("[Client] Logged in.");

			startListening();
		} catch (ConnectException e) {
			System.err.println("[Client] The server is unreachable.");
			repairConnection();
		} catch (Exception e) {
			System.err.println("[Client] An error occurred.");
			e.printStackTrace();
			repairConnection();
		}
	}

	private void startListening() {
		try {
			// the object stream format has a header, and the ObjectInputStream reads that
			// header on construction. therefore, it is blocking until it receives that
			// header over the socket.
			ois = new ObjectInputStream(new BufferedInputStream(login.getInputStream()));
			while (listening) {
				// Waiting for messages
				Object pack = ois.readObject();
				if (pack instanceof DataPackage) {
					handleDataPackage((DataPackage) pack);
				} else {
					System.err.println("[Client] Unknown pack: " + pack);
				}

			}
		} catch (SocketException | EOFException e) {
			System.err.println("[Client] Connection lost.");
			repairConnection();
		} catch (Exception e) {
			System.err.println("[Client] An error occurred.");
			e.printStackTrace();
			repairConnection();
		}
	}

	private void repairConnection() {
		if (listening) {
			System.out.println("[Client] Trying to reconnect in 5 seconds.");
			closeAllStreams();

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			login();
		}
	}

	private void closeAllStreams() {
		try {
			if (oos != null) {
				oos.close();
				oos = null;
			}
			if (ois != null) {
				ois.close();
				ois = null;
			}
			if (login != null) {
				login.close();
				login = null;
			}
		} catch (SocketException e) {
			// do nothing
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		System.out.println("[Client] Closing connection...");
		listening = false;
		closeAllStreams();
		mainThread.interrupt();
		System.out.println("[Client] Connection closed.");
	}

}