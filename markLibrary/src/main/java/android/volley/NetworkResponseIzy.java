package android.volley;

import java.util.Map;

import org.apache.http.HttpEntity;

import com.android.volley.NetworkResponse;

public class NetworkResponseIzy extends NetworkResponse {

	public final HttpEntity entity;

	public NetworkResponseIzy(int statusCode, HttpEntity entity, Map<String, String> headers, boolean notModified) {
		super(statusCode, null, headers, notModified);
		this.entity = null;
	}

}
