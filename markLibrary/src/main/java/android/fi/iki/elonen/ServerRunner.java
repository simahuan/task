package android.fi.iki.elonen;

import java.io.IOException;

public class ServerRunner {

	public static void start(NanoHTTPD server) {
		try {
			server.start();
		} catch (IOException ioe) {
			System.err.println("Couldn't start server:\n" + ioe);
		}
	}

	public static void stop(NanoHTTPD server) {
		System.out.println("Server stopped.\n");
		server.stop();
	}

}
