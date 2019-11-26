package IRC;

import java.io.IOException;

public interface Actor {
    void tell(String message, Actor sender) throws IOException;
    void shutdown() throws IOException;
}
