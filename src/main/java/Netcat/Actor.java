package Netcat;
import java.io.IOException;

public interface Actor<A> {

	void tell(String message, Actor<A> sender) throws Exception;
	void shutdown();

}
