package src;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;

/**
 * Server-side command for file transfer from a socket.
 * The file is written in append mode.
 * <p/>
 * Created by anselmov on 04/06/16.
 */
public class TransferFileCommand implements Command {
    public static final int BUFFER_SIZE = 254;

    String fileToWrite;
    long startTime;
    Boolean isTransferFinished = false;

    FileChannel fileChannel;
    private SocketAddress receive;

    public TransferFileCommand(String fileToWrite, long startTime) {
        this.fileToWrite = fileToWrite;
        this.startTime = startTime;
        try {
            fileChannel = new RandomAccessFile(fileToWrite, "rw").getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void execute(SelectionKey selectionKey) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        DatagramChannel clientSocket = (DatagramChannel) selectionKey.channel();

//        System.out.println("[W] Writing " + fileToWrite);
//        System.out.println("[R] Reading file from client ... Please wait.");

        buffer.clear();
        receive = clientSocket.receive(buffer);
        clientSocket.send(ByteBuffer.wrap("x".getBytes()), receive);

        if (clientHasNothingMoreToSend()) {
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            long minutes = (elapsedTime / (1000 * 60)) % 60;
            isTransferFinished = true;

            System.out.print("(info) File received successfully.");
            System.out.printf("[Elapsed Time : %d min (%d ms)] [File size : %s]%n",
                    minutes, elapsedTime, humanReadableByteCount(fileChannel.size()));

        } else { // continue writing
            buffer.flip();
            System.out.println("fileChannel.size() = " + fileChannel.size());
            fileChannel.position(fileChannel.size());
            fileChannel.write(buffer);
        }

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

        if (isTransferFinished) {
            try {
                fileChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new SaveToDatabaseCommand();
        } else {
        }
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
