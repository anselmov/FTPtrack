package src;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Receives video files from clients and updates the database
 */
public class FTPServer {
    private static final int PORT = 5217;
    private ServerSocketChannel serverSocket;
    private Selector selector;

    public FTPServer() {
        try {
            connect();
            addShutdownHook();
            processCommands();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() throws IOException {
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(PORT));
        serverSocket.configureBlocking(false);
        selector = Selector.open();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println(">>> Server Started !");
    }

    private void addShutdownHook() throws IOException {
        Runtime.getRuntime().addShutdownHook(new ShutDownHook(serverSocket, selector));
    }

    public void processCommands() throws IOException {
        while (selector.isOpen()) {

            int select = selector.select();
            if (select == 0) continue; // no socket updates
//            System.out.printf("[#] Sockets to process : [%d]%n", select);

            // process socket updates
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();

                if (selectionKey.isAcceptable()) {
                    acceptNewConnection(selectionKey);

                } else { // handle read or write commands for active keys
                    Command command = (Command) selectionKey.attachment();
                    SocketChannel clientSocket = (SocketChannel) selectionKey.channel();

                    if (selectionKey.isReadable()) {
                        command.execute(selectionKey);

                    } else if (selectionKey.isWritable()) {
                        String ackCommand = "GIVE";
                        System.out.printf("[W] Write ack command = [%s]%n", ackCommand);
                        clientSocket.write(ByteBuffer.wrap(ackCommand.getBytes()));
                    }

                    if (selectionKey.isValid()) {
                        clientSocket.register(selector, command.nextOP(), command.next());
                    }
                }
                iterator.remove();
            }
        }
    }

    private void acceptNewConnection(SelectionKey selectionKey) throws IOException {
        SocketChannel clientSocket = serverSocket.accept();

        if (clientSocket == null) {
            selectionKey.cancel();
            System.out.printf("[-] Client disconnected.");

        } else {
            System.out.printf("[+] NewClient.LocalAddress = %s%n", clientSocket.getLocalAddress());
            clientSocket.configureBlocking(false);
            clientSocket.register(selector,
                    SelectionKey.OP_READ, new ReadFileNameCommand());
        }
    }

    class ShutDownHook extends Thread {
        private ServerSocketChannel serverSocket;
        private Selector selector;

        ShutDownHook(ServerSocketChannel serverSocket, Selector selector) throws IOException {
            this.serverSocket = serverSocket;
            this.selector = selector;
        }

        @Override
        public void run() {
            try {
                selector.close();
                serverSocket.close();
                System.out.println("[X] Server closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new FTPServer();
    }
}
