package src;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * Uploads file metadata to the database.
 */
public class SaveToDatabaseCommand implements Command {

    // TODO
    @Override
    public void execute(SelectionKey selectionKey) throws IOException {
        System.out.println("[R] Ready to write to database !");
        selectionKey.cancel();
    }

    @Override
    public int nextOP() {
        return 0;
    }

    @Override
    public Command next() {
        return null;
    }
}
