package android.izy.os;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Handler监听处理支持线程与非线程调用
 * 
 * @author yangyp
 * @version 1.0, 2014-7-17 下午1:13:11
 */
public abstract class AsyncHandler {

	private Handler handler;

	@SuppressLint("HandlerLeak")
	public AsyncHandler() {
		if (Looper.myLooper() != null) {
			handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					AsyncHandler.this.handleMessage(msg);
				}
			};
		}
	}

	/**
	 * 处理消息内容
	 * 
	 * @param msg
	 */
	protected abstract void handleMessage(Message msg);

	/**
	 * 发送消息
	 * 
	 * @param responseMessage
	 */
	public void sendMessage(int responseMessage) {
		sendMessage(obtainMessage(responseMessage, null));
	}

	/**
	 * 发送消息
	 * 
	 * @param responseMessage
	 * @param object
	 */
	public void sendMessage(int responseMessage, Object[] object) {
		sendMessage(obtainMessage(responseMessage, object));
	}

	/**
	 * 发送消息
	 * 
	 * @param msg
	 */
	private void sendMessage(Message msg) {
		if (handler != null) {
			handler.sendMessage(msg);
		} else {
			handleMessage(msg);
		}
	}

	/**
	 * 创建消息
	 * 
	 * @param responseMessage
	 * @param object
	 * @return
	 */
	private Message obtainMessage(int responseMessage, Object object) {
		Message msg = null;
		if (handler != null) {
			msg = this.handler.obtainMessage(responseMessage, object);
		} else {
			msg = Message.obtain();
			msg.what = responseMessage;
			msg.obj = object;
		}
		return msg;
	}
}
