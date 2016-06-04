package src;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * Sends to the server the first listed file in a directory
 */
public class FTPClient {
    /**
     * Configuration Params
     */
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 5217;
    public static final String VIDEO_DIR = "mydir";
    public static final int BUFFER_SIZE = 4;

    FileChannel fileChannel; // read client's files (ie video files)
    SocketChannel clientSocket;  // read/write with the server

    public FTPClient() throws IOException {
        connect();
        sendFile(findFirstFile());
        disconnect();
    }

    private void connect() throws IOException {
        clientSocket = SocketChannel.open(new InetSocketAddress(HOST, PORT));
        System.out.println(">>> Client Started !");
    }

    private String findFirstFile() {
        File videoDir = new File(VIDEO_DIR);
        return String.format("%s%s%s", VIDEO_DIR, File.separator, videoDir.list()[0]);
    }

    void sendFile(String fileName) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        // open file
        File f = new File(fileName);
        fileChannel = new RandomAccessFile(f, "rw").getChannel();


        // write fileName
        System.out.printf("(info) File to transfer [%s]%n", fileName);
        clientSocket.write(ByteBuffer.wrap((fileName + System.lineSeparator()).getBytes()));
        clientSocket.read(buffer);

        String commandReceived = new String(buffer.array());
        System.out.printf("[R] Read command [%s]%n", commandReceived);

        // transfer file
        long fileSize = fileChannel.size();
        System.out.printf("(info) Transfer of %d bytes in progress ...%n", fileSize);
        long bytesSent = 0;
        while (bytesSent < fileSize) {
            bytesSent += fileChannel.transferTo(bytesSent, fileSize, clientSocket);
        }

        System.out.println("(info) File sent successfully.");
    }

    private void disconnect() throws IOException {
        fileChannel.close();
        clientSocket.close();
        System.out.println("[X] Client closed.");
    }

    public static void main(String args[]) throws Exception {
        new FTPClient();
    }
}
