package android.izy.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CountingOutputStream extends FilterOutputStream {

	private final CountingListener listener;
	private long counter = 0L;

	public CountingOutputStream(OutputStream out, CountingListener listener) {
		super(out);
		this.listener = listener;
	}

	@Override
	public void write(byte[] buffer, int offset, int length) throws IOException {
		out.write(buffer, offset, length);
		counter += length;
		if (listener != null) {
			listener.onChange(counter, length);
		}
	}

	@Override
	public void write(int oneByte) throws IOException {
		out.write(oneByte);
		counter++;
		if (listener != null) {
			listener.onChange(counter, 1);
		}
	}

	/**
	 * 统计数据监听
	 * 
	 * @author yangyp
	 * @version 1.0, 2014年10月28日 下午5:02:52
	 */
	public interface CountingListener {

		/**
		 * 传输
		 * 
		 * @param counterBytes
		 *            统计已读取的字节数
		 * @param bytesRead
		 *            当前读取的字节数
		 */
		void onChange(long counterBytes, int bytesRead) throws IOException;
	}

}
