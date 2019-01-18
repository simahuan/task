package android.izy.util.parse;

import java.io.Reader;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Gson工具类
 * 
 * @author yangyp
 * @version 1.0, 2014-7-3 下午3:15:32
 */
public abstract class GsonUtils {

	static final String TAG = GsonUtils.class.getSimpleName();

	/**
	 * 对象转json
	 * 
	 * @param obj
	 * @return
	 */
	public static String jsonSerializer(Object obj) {
		Gson gson = new Gson();
		return gson.toJson(obj);
	}

	/**
	 * json转对象
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T jsonDeserializer(String json, Class<T> clazz) {
		T result = null;
		try {
			Gson gson = new Gson();
			result = gson.fromJson(json, clazz);
		} catch (Exception e) {
			Log.e(TAG, "JsonDeserializer: ", e);
		}
		return result;
	}

	public static <T> T jsonDeserializer(Reader json, Class<T> clazz) {
		T result = null;
		try {
			Gson gson = new Gson();
			result = gson.fromJson(json, clazz);
		} catch (Exception e) {
			Log.e(TAG, "JsonDeserializer: ", e);
		}
		return result;
	}

	/**
	 * json转集合
	 * <p>
	 * Type collectionType = new TypeToken<Collection<Integer>>(){}.getType();
	 * </p>
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T jsonDeserializerCollection(String json, TypeToken<T> typeToken) {
		T result = null;
		try {
			Gson gson = new Gson();
			result = gson.fromJson(json, typeToken.getType());
		} catch (Exception e) {
			Log.e(TAG, "jsonDeserializerCollection: ", e);
		}
		return result;
	}

	public static <T> T jsonDeserializerCollection(Reader json, TypeToken<T> typeToken) {
		T result = null;
		try {
			Gson gson = new Gson();
			result = gson.fromJson(json, typeToken.getType());
		} catch (Exception e) {
			Log.e(TAG, "jsonDeserializerCollection: ", e);
		}
		return result;
	}
}
