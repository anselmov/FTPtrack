package src;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.SelectionKey;

/**
 * Server-side command for file transfer from a socket.
 * The file is written in append mode.
 * <p/>
 * Created by anselmov on 04/06/16.
 */
public class TransferFileCommand implements Command {
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 5217;
    private static final int BUFFER_SIZE = 8192;

    String fileToWrite;
    long startTime;
    SocketAddress receive;
    Boolean isTransferFinished = false;

    public TransferFileCommand(String fileToWrite, long startTime) {
        this.fileToWrite = fileToWrite;
        this.startTime = startTime;
    }

    @Override
    public void execute(SelectionKey selectionKey) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        DatagramChannel clientSocket = (DatagramChannel) selectionKey.channel();
        SeekableByteChannel fileChannel = new RandomAccessFile(fileToWrite, "rw").getChannel();

//        System.out.println("[W] Writing " + fileToWrite);
//        System.out.println("[R] Reading file from client ... Please wait.");

        receive = clientSocket.receive(buffer);

        if (clientSocket.isConnected()) {
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            long minutes = (elapsedTime / (1000 * 60)) % 60;
            isTransferFinished = true;

            String ackCommand = "GIVE";
            System.out.printf("[W] Write ack command = [%s]%n", ackCommand);
            clientSocket.send(ByteBuffer.wrap(ackCommand.getBytes()), new InetSocketAddress(PORT));

            System.out.print("(info) File received successfully.");
            System.out.printf("[Elapsed Time : %d min (%d ms)] [File size : %s]%n",
                    minutes, elapsedTime, humanReadableByteCount(fileChannel.size()));

        } else { // continue writing
            buffer.flip();
            fileChannel.position(fileChannel.size());
            fileChannel.write(buffer);
        }

        fileChannel.close();
    }

    private boolean clientHasNothingMoreToSend() {
        return receive == null;
    }

    @Override
    public int nextOP() {
        return SelectionKey.OP_READ;
    }

    @Override
    public Command next() {
        if (isTransferFinished) return new SaveToDatabaseCommand();
        else
            return this;
    }

    public static String humanReadableByteCount(long bytes) {
        int unit = 1000;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        char pre = "kMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
