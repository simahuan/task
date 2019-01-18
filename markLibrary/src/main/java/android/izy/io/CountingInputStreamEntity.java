package android.izy.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.entity.InputStreamEntity;

import android.izy.io.CountingOutputStream.CountingListener;

/**
 * 数据上传统计流实体
 * 
 * @author yangyp
 * @version 1.0, 2014年10月28日 下午5:03:05
 */
public class CountingInputStreamEntity extends InputStreamEntity {

	private CountingListener listener;

	public CountingInputStreamEntity(InputStream instream, CountingListener listener) {
		this(instream, -1, listener);
	}

	public CountingInputStreamEntity(InputStream instream, long length, CountingListener listener) {
		super(instream, length);
		this.listener = listener;
	}

	public void setUploadListener(CountingListener listener) {
		this.listener = listener;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, listener));
	}

}
