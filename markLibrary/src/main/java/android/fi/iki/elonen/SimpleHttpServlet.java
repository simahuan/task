package android.fi.iki.elonen;

import java.io.ByteArrayInputStream;

import android.fi.iki.elonen.NanoHTTPD.Response.Status;

/**
 * An example of subclassing NanoHTTPD to make a custom HTTP server.
 */
public class SimpleHttpServlet extends NanoHTTPD {

	public SimpleHttpServlet() {
		super(8080);
	}

	public SimpleHttpServlet(int port) {
		super(port);
	}

	@Override
	public final Response serve(IHTTPSession session) {
		//super.serve(session);
		NanoHTTPD.Response response = new Response("HTTP OK");
		try {
			service(session, response);
		} catch (ResponseException re) {
			sendError(response, re.getStatus(), re.getMessage());
		} catch (Throwable ex) {
			sendError(response, Status.INTERNAL_ERROR, ex.getMessage());
		}
		return response;
	}

	protected void service(IHTTPSession session, Response response) throws Exception {
		Method method = session.getMethod();
		switch (method) {
		case HEAD:
			doHead(session, response);
			break;
		case GET:
			doGet(session, response);
			break;
		case POST:
			doPost(session, response);
			break;
		case PUT:
			doPut(session, response);
			break;
		case DELETE:
			doDelete(session, response);
			break;
		case OPTIONS:
			doOptions(session, response);
			break;
		default:
			sendError(response, Status.INTERNAL_ERROR, "http method not implemented!");
			break;
		}
	}

	public static void sendError(Response response, Status status, String message) {
		response.setStatus(status);
		try {
			response.setData(message != null ? new ByteArrayInputStream(message.getBytes("UTF-8")) : null);
		} catch (java.io.UnsupportedEncodingException uee) {
			uee.printStackTrace();
		}
	}

	protected void doHead(IHTTPSession session, Response response) throws Exception {

	}

	protected void doGet(IHTTPSession session, Response response) throws Exception {

	}

	protected void doPost(IHTTPSession session, Response response) throws Exception {

	}

	protected void doPut(IHTTPSession session, Response response) throws Exception {

	}

	protected void doDelete(IHTTPSession session, Response response) throws Exception {

	}

	protected void doOptions(IHTTPSession session, Response response) throws Exception {

	}

}
