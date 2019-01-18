package android.izy.content;

import android.izy.ApplicationSupport;
import android.content.Intent;

public class IntentWrapper extends Intent {

	private static final String INTENT_EXTRAS = "Intent#extras";
	private ApplicationSupport appContext;

	public IntentWrapper(ApplicationSupport appContext, Intent intent) {
		super(intent == null ? new Intent() : intent);
		this.appContext = appContext;
	}

	/**
	 * 打包intent数据
	 * 
	 * @param applicationContext
	 * @param intent
	 * @param extras
	 */
	public void putIntentExtras(Object... extras) {
		String intentHashCode = "Intent@" + Integer.toHexString(hashCode());
		appContext.putIntentExtras(intentHashCode, extras);
		putExtra(INTENT_EXTRAS, intentHashCode);
	}

	/**
	 * 获取Intent参数
	 * 
	 * @param intent
	 * @return
	 */
	public Object[] getIntentExtras() {
		String intentHashCode = getStringExtra(INTENT_EXTRAS);
		return appContext.getIntentExtras(intentHashCode);
	}
}
