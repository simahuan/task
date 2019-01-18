package android.fi.iki.elonen;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class SimpleServletInputStream extends PushbackInputStream {

	public static final int BUFSIZE = 8192;
	private long contentLength;

	public SimpleServletInputStream(InputStream in, long contentLength) {
		super(in, BUFSIZE);
		
		/*long size;
		if (headers.containsKey("content-length")) {
			size = Integer.parseInt(headers.get("content-length"));
		} else if (splitbyte < rlen) {
			size = rlen - splitbyte;
		} else {
			size = 0;
		}*/
	}

	@Override
	public int read() throws IOException {
		int rlen = 0;
		while (contentLength > 0) {
			rlen = in.read(buf, 0, (int) Math.min(contentLength, BUFSIZE));
			contentLength -= rlen;
			return rlen;
		}
		return 0;
	}

}
