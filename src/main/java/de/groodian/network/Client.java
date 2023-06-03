package de.groodian.network;

import de.groodian.hyperiorcore.main.Output;
import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public abstract class Client {

    private final DataPackage loginPack;
    private final InetSocketAddress address;
    private final Thread mainThread;

    private Socket login;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean listening;

    public Client(String hostname, int port, DataPackage loginPack) {
        this.loginPack = loginPack;
        address = new InetSocketAddress(hostname, port);
        listening = true;

        mainThread = new Thread(this::login);
        mainThread.start();
    }

    protected abstract void handleDataPackage(DataPackage dataPackage);

    protected abstract void onSuccessfulLogin();

    public void sendMessage(DataPackage pack) {
        if (listening) {
            new Thread(() -> {

                try {
                    if (oos != null) {
                        Output.send("[ServiceClient] Sending pack: " + pack);
                        oos.writeObject(pack);
                        oos.flush();
                    } else {
                        Output.send("[ServiceClient] Message could not be sent, because the client is not logged in!");
                    }
                } catch (Exception e) {
                    Output.send("[ServiceClient] Message could not be sent!");
                }

            }).start();
        }
    }

    private void login() {
        if (listening) {
            try {
                Output.send("[ServiceClient] Connecting...");
                login = new Socket();
                login.connect(address, 5000);
                Output.send("[ServiceClient] Connected to: " + login.getRemoteSocketAddress());

                Output.send("[ServiceClient] Logging in...");
                oos = new ObjectOutputStream(new BufferedOutputStream(login.getOutputStream()));
                oos.writeObject(loginPack);
                oos.flush();
                // the object stream format has a header, and the ObjectInputStream reads that
                // header on construction. therefore, it is blocking until it receives that
                // header over the socket.
                ois = new ObjectInputStream(new BufferedInputStream(login.getInputStream()));
                Output.send("[ServiceClient] Logged in.");

                onSuccessfulLogin();
                startListening();
            } catch (ConnectException e) {
                Output.send("[ServiceClient] The server is unreachable.");
                repairConnection();
            } catch (Exception e) {
                Output.send("[ServiceClient] An error occurred.");
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
                    Output.send("[ServiceClient] Pack received: " + pack);
                    handleDataPackage((DataPackage) pack);
                } else {
                    Output.send("[ServiceClient] Unknown pack: " + pack);
                }

            }
        } catch (SocketException | EOFException e) {
            Output.send("[ServiceClient] Connection lost.");
            repairConnection();
        } catch (Exception e) {
            Output.send("[ServiceClient] An error occurred.");
            e.printStackTrace();
            repairConnection();
        }
    }

    private void repairConnection() {
        if (listening) {
            Output.send("[ServiceClient] Trying to reconnect in 5 seconds.");
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
        Output.send("[ServiceClient] Closing connection...");
        listening = false;
        closeAllStreams();
        mainThread.interrupt();
        Output.send("[ServiceClient] Connection closed.");
    }

}