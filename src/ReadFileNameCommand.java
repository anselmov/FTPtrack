package src;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ReadFileNameCommand implements Command {
    private static final int BUFFER_SIZE = 1024;
    String fileName;
    int bytesRead;

    @Override
    public void execute(SelectionKey selectionKey) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        SocketChannel clientSocket = (SocketChannel) selectionKey.channel();

        bytesRead = clientSocket.read(buffer);

        if (hasFailedToReadFromServer(bytesRead)) {
            selectionKey.cancel();
            return;
        }

        fileName = new String(buffer.array()).trim();
        System.out.printf("[R] File to receive [%s]%n", fileName);
    }

    private boolean hasFailedToReadFromServer(int bytesRead) {
        return bytesRead == -1;
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
