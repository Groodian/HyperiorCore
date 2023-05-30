package de.groodian.network;

import java.io.*;
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

        mainThread = new Thread(() -> login());
        mainThread.start();
    }

    protected abstract void handleDataPackage(DataPackage dataPackage);

    protected abstract void onSuccessfulLogin();

    public void sendMessage(DataPackage pack) {
        if (listening) {
            new Thread(() -> {

                try {
                    if (oos != null) {
                        System.out.println("[ServiceClient] Sending pack: " + pack);
                        oos.writeObject(pack);
                        oos.flush();
                    } else {
                        System.err.println("[ServiceClient] Message could not be sent, because the client is not logged in!");
                    }
                } catch (Exception e) {
                    System.err.println("[ServiceClient] Message could not be sent!");
                }

            }).start();
        }
    }

    private void login() {
        if (listening) {
            try {
                System.out.println("[ServiceClient] Connecting...");
                login = new Socket();
                login.connect(address, 5000);
                System.out.println("[ServiceClient] Connected to: " + login.getRemoteSocketAddress());

                System.out.println("[ServiceClient] Logging in...");
                oos = new ObjectOutputStream(new BufferedOutputStream(login.getOutputStream()));
                oos.writeObject(loginPack);
                oos.flush();
                // the object stream format has a header, and the ObjectInputStream reads that
                // header on construction. therefore, it is blocking until it receives that
                // header over the socket.
                ois = new ObjectInputStream(new BufferedInputStream(login.getInputStream()));
                System.out.println("[ServiceClient] Logged in.");

                onSuccessfulLogin();
                startListening();
            } catch (ConnectException e) {
                System.err.println("[ServiceClient] The server is unreachable.");
                repairConnection();
            } catch (Exception e) {
                System.err.println("[ServiceClient] An error occurred.");
                e.printStackTrace();
                repairConnection();
            }
        }
    }

    private void startListening() {
        try {
            while (listening) {
                // Waiting for messages
                Object pack = ois.readObject();
                if (pack instanceof DataPackage) {
                    System.out.println("[ServiceClient] Pack received: " + pack);
                    handleDataPackage((DataPackage) pack);
                } else {
                    System.err.println("[ServiceClient] Unknown pack: " + pack);
                }

            }
        } catch (SocketException | EOFException e) {
            System.err.println("[ServiceClient] Connection lost.");
            repairConnection();
        } catch (Exception e) {
            System.err.println("[ServiceClient] An error occurred.");
            e.printStackTrace();
            repairConnection();
        }
    }

    private void repairConnection() {
        if (listening) {
            System.out.println("[ServiceClient] Trying to reconnect in 5 seconds.");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        System.out.println("[ServiceClient] Closing connection...");
        listening = false;
        closeAllStreams();
        mainThread.interrupt();
        System.out.println("[ServiceClient] Connection closed.");
    }

}