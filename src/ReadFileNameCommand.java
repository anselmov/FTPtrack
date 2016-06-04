package src;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ReadFileNameCommand implements Command {
    private static final int BUFFER_SIZE = 1024;
    String fileName;

    @Override
    public void execute(SelectionKey selectionKey) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        SocketChannel clientSocket = (SocketChannel) selectionKey.channel();

        int bytesRead = clientSocket.read(buffer);

        if (bytesRead == -1) {
            selectionKey.cancel();
            return;
        }

        fileName = new String(buffer.array()).trim();
        System.out.printf("[R] File to receive [%s]%n", fileName);
    }

    @Override
    public int nextOP() {
        return SelectionKey.OP_WRITE;
    }

    @Override
    public Command next() {
        long startTime = System.currentTimeMillis();
        return new TransferFileCommand(fileName, startTime);
    }
}
