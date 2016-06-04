package src;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface Command {
    void execute(SelectionKey selectionKey) throws IOException;

    int nextOP();

    Command next();
}
