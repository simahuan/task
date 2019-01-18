package android.volley;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

/**
 * 简单的回调处理
 * 
 * @param <T>
 * @author yangyp
 * @version 1.0, 2014-7-4 上午11:26:12
 */
public interface RequestListener<T> extends Listener<T>, ErrorListener {

}
