import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class FTPServer {
    private static final int PORT = 5217;
    private ServerSocketChannel serverSocket;
    private Selector selector;
    private static final int BUFFER_SIZE = 8192;
    private long startTime;

    public FTPServer() throws IOException {
        connect();
        processCommands();
    }

    public void connect() throws IOException {
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(PORT));
        serverSocket.configureBlocking(false);
        selector = Selector.open();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println(">>> Server Started !");
    }

    public void processCommands() throws IOException {
        while (true) {
            int select = selector.select();
            System.out.printf("[#] Sockets to process : [%d]%n", select);
            if (select == 0) continue; // no socket updates

            // process socket updates
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()) {
                    acceptNewConnection(selectionKey);

                } else if (selectionKey.isReadable()) {
                    readFromSocket(selectionKey);

                } else if (selectionKey.isWritable()) {
                    writeToSocket(selectionKey);
                }
                iterator.remove();
            }

        }
    }


    private void acceptNewConnection(SelectionKey selectionKey) throws IOException {
        SocketChannel clientSocket = serverSocket.accept();

        if (clientSocket == null) { // client disconnected
            selectionKey.cancel();
            System.out.printf("[-] Client disconnected.");

        } else { // new client
            System.out.printf("[+] NewClient.LocalAddress = %s%n", clientSocket.getLocalAddress());
            clientSocket.configureBlocking(false);
            clientSocket.register(selector,
                    SelectionKey.OP_READ);
        }
    }


    private void readFromSocket(SelectionKey selectionKey) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        SocketChannel clientSocket = (SocketChannel) selectionKey.channel();
        String fileToWrite = (String) selectionKey.attachment();

        if (fileToWrite != null) {
            // write file to disk
            SeekableByteChannel fileChannel = new RandomAccessFile(fileToWrite, "rw").getChannel();
            System.out.println("[W] Writing " + fileToWrite);
            System.out.println("[R] Reading file from client ... Please wait.");

            int read = clientSocket.read(buffer);
            if (read == -1) {
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                long minute = (elapsedTime / (1000 * 60)) % 60;

                System.out.printf("(info) File received successfully. [Elapsed Time : %d min (%d ms)] [File size : %s]%n", minute, elapsedTime, humanReadableByteCount(fileChannel.size(), true));
                selectionKey.cancel();
                return;

            } else {
                buffer.flip();
                fileChannel.position(fileChannel.size());
                fileChannel.write(buffer);
            }

            clientSocket.register(selector,
                    SelectionKey.OP_READ, fileToWrite);
            fileChannel.close();

        } else {
            // read fileName from clientSocket
            startTime = System.currentTimeMillis();
            int read = clientSocket.read(buffer);

            if (read == -1) {
                selectionKey.cancel();
                return;
            }

            String fileName = new String(buffer.array()).trim();
            System.out.printf("[R] File to receive [%s]%n", fileName);

            clientSocket.register(selector,
                    SelectionKey.OP_WRITE, fileName);
        }
    }

    private void writeToSocket(SelectionKey selectionKey) throws IOException {
        SocketChannel clientSocket = (SocketChannel) selectionKey.channel();
        String fileName = (String) selectionKey.attachment();
        String commandToSend = "GIVE";
        System.out.printf("[W] Write command = [%s]%n", commandToSend);
        clientSocket.write(ByteBuffer.wrap(commandToSend.getBytes()));

        clientSocket.register(selector,
                SelectionKey.OP_READ, fileName);
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static void main(String[] args) throws IOException {
        new FTPServer();
        System.out.println("[X] Server closed.");

//                channel.finishConnect();
//                channel.close();
    }
}


