package android.izy.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.entity.FileEntity;

import android.izy.io.CountingOutputStream.CountingListener;

/**
 * 数据上传统计流实体
 * 
 * @author yangyp
 * @version 1.0, 2014年10月28日 下午5:03:05
 */
public class CountingFileEntity extends FileEntity {

	private CountingListener listener;

	public CountingFileEntity(File file, CountingListener listener) {
		this(file, "binary/octet-stream", listener);
	}

	public CountingFileEntity(File file, String contentType, CountingListener listener) {
		super(file, contentType);
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
