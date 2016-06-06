package src;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

public class ReadFileNameCommand implements Command {
    private static final int BUFFER_SIZE = 1024;
    String fileName;
    SocketAddress receive;

    @Override
    public void execute(SelectionKey selectionKey) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        DatagramChannel clientSocket = (DatagramChannel) selectionKey.channel();

        receive = clientSocket.receive(buffer);
        System.out.println("receive from = " + receive);

        if (hasFailedToReadFromServer()) {
            selectionKey.cancel();
            return;
        }

        fileName = new String(buffer.array()).trim();
        System.out.printf("[R] File to receive [%s]%n", fileName);
    }

    private boolean hasFailedToReadFromServer() {
        return receive == null;
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
