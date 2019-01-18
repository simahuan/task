package android.izy.database;

public class MessageResult<T> {

	public static final int RESULT_OK = 0;

	/** 0为正常其它为错误信息 */
	public int StatusCode;

	/** 服务器消息 */
	public String Message;

	public T Data;

	public static boolean hasError(MessageResult<?> result) {
		return result == null || result.StatusCode != 0;
	}

	public static String getErrorMessage(MessageResult<?> result) {
		return result == null ? "请求数据为空!" : result.Message;
	}

	@Override
	public String toString() {
		return "MessageResult [StatusCode=" + StatusCode + ", Message=" + Message + ", Data=" + Data + "]";
	}

}
