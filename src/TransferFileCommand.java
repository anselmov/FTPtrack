package src;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Server-side command for file transfer from a socket.
 * The file is written in append mode.
 * <p/>
 * Created by anselmov on 04/06/16.
 */
public class TransferFileCommand implements Command {
    private static final int BUFFER_SIZE = 8192;

    String fileToWrite;
    long startTime;

    public TransferFileCommand(String fileToWrite, long startTime) {
        this.fileToWrite = fileToWrite;
        this.startTime = startTime;
    }

    @Override
    public void execute(SelectionKey selectionKey) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        SocketChannel clientSocket = (SocketChannel) selectionKey.channel();
        SeekableByteChannel fileChannel = new RandomAccessFile(fileToWrite, "rw").getChannel();

//        System.out.println("[W] Writing " + fileToWrite);
//        System.out.println("[R] Reading file from client ... Please wait.");

        if (clientHasNothingMoreToSend(clientSocket.read(buffer))) {
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            long minutes = (elapsedTime / (1000 * 60)) % 60;

            System.out.print("(info) File received successfully.");
            System.out.printf("[Elapsed Time : %d min (%d ms)] [File size : %s]%n",
                    minutes, elapsedTime, humanReadableByteCount(fileChannel.size()));
            selectionKey.cancel();

        } else { // continue writing
            buffer.flip();
            fileChannel.position(fileChannel.size());
            fileChannel.write(buffer);
        }

        fileChannel.close();
    }

    private boolean clientHasNothingMoreToSend(int read) {
        return read == -1;
    }

    @Override
    public int nextOP() {
        return SelectionKey.OP_READ;
    }

    @Override
    public Command next() {
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
