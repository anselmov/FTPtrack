package src;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;

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
    public static final int BUFFER_SIZE = 64;

    DatagramChannel channel; // read/write with the server
    FileChannel fileChannel; // read client's files (ie video files)

    public FTPClient() throws IOException {
        connect();
        sendFile(findFirstFile());
        disconnect();
    }

    private void connect() throws IOException {
        channel = DatagramChannel.open();
        channel.connect(new InetSocketAddress(HOST, PORT));
        System.out.println("channel.getLocalAddress() = " + channel.getLocalAddress());
        System.out.println("channel = " + channel.getRemoteAddress());
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
        InetSocketAddress target = new InetSocketAddress(HOST, PORT);
        System.out.println("target.getAddress() = " + target.getAddress());
        System.out.println("target.getHostName() = " + target.getHostName());
        System.out.println("target = " + target);
        long send;
        send = channel.send(ByteBuffer.wrap((fileName + System.lineSeparator()).getBytes()), target);
        System.out.println("sent = " + send);

        buffer.clear();

        //Wait for start transfer signal
        System.out.println("Send SYN");
        send = channel.send(ByteBuffer.wrap("x".getBytes()), target);
        System.out.println("Waiting for signal...");
        SocketAddress receive = channel.receive(buffer);
        System.out.println("received= " + receive);
        System.out.println("receive.toString() = " + receive.toString());
        String commandReceived = new String(buffer.array()).trim();
        System.out.printf("[R] Read command [%s]%n", commandReceived);

        // transfer file
        long fileSize = fileChannel.size();
        System.out.printf("(info) Transfer of %d bytes in progress ...%n", fileSize);
        long bytesSent = 0;
        while (bytesSent < fileSize) {
            bytesSent += fileChannel.transferTo(bytesSent, BUFFER_SIZE, channel);
            channel.receive(buffer);
            buffer.flip();
        }

        System.out.println("(info) File sent successfully.");
    }

    private void disconnect() throws IOException {
        fileChannel.close();
        channel.close();
        System.out.println("[X] Client closed.");
    }

    public static void main(String args[]) throws Exception {
        new FTPClient();
    }
}
