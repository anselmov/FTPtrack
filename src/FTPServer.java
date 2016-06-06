package src;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Receives video files from clients and updates the database
 */
public class FTPServer {
    public static final String HOST = "127.0.0.1";
    private static final int PORT = 5217;
    private DatagramChannel serverSocket;
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
        serverSocket = DatagramChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(HOST, PORT));
        serverSocket.configureBlocking(false);
        selector = Selector.open();
        System.out.println(">>> Server Started !");
        serverSocket.register(selector,
                SelectionKey.OP_READ, new ReadFileNameCommand());
    }

    private void addShutdownHook() throws IOException {
        Runtime.getRuntime().addShutdownHook(new ShutDownHook(serverSocket, selector));
    }

    public void processCommands() throws IOException {
        while (selector.isOpen()) {

            int select = selector.select();
            System.out.printf("[#] Sockets to process : [%d]%n", select);
            if (select == 0) continue; // no socket updates

            // process socket updates
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();

                // handle read or write commands for active keys
                Command command = (Command) selectionKey.attachment();
                DatagramChannel channel = (DatagramChannel) selectionKey.channel();

                System.out.println("(info) Processing command = " + command.getClass());

                if (selectionKey.isReadable()) {
                    System.out.println("Execute " + command.getClass());
                    command.execute(selectionKey);

                } else if (selectionKey.isWritable()) {
                    String ackCommand = "GIVE";
                    System.out.printf("[W] Write ack command = [%s]%n", ackCommand);

                    SocketAddress receive = channel.receive(ByteBuffer.allocate(1));
                    channel.send(ByteBuffer.wrap(ackCommand.trim().getBytes()), receive);
                }

                if (selectionKey.isValid()) {
                    serverSocket.register(selector, command.nextOP(), command.next());
                }
                iterator.remove();
            }
        }
    }

    class ShutDownHook extends Thread {
        private DatagramChannel serverSocket;
        private Selector selector;

        ShutDownHook(DatagramChannel serverSocket, Selector selector) throws IOException {
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
