package android.izy.service.old;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.database.Observable;
import android.os.IBinder;

public class ServiceBinderObservable extends Observable<ServiceConnection> {

	/**
	 * Invokes {@link ServiceConnection#onServiceConnected} on each observer.
	 * Called when the contents of the data set have changed. The recipient will
	 * obtain the new contents the next time it queries the data set.
	 */
	public void notifyServiceConnected(ComponentName name, IBinder service) {
		synchronized (mObservers) {
			for (int i = mObservers.size() - 1; i >= 0; i--) {
				mObservers.get(i).onServiceConnected(name, service);
			}
		}
	}

	/**
	 * Invokes {@link ServiceConnection#onServiceDisconnected} on each observer.
	 * Called when the data set is no longer valid and cannot be queried again,
	 * such as when the data set has been closed.
	 */
	public void notifyServiceDisconnected(ComponentName name) {
		synchronized (mObservers) {
			for (int i = mObservers.size() - 1; i >= 0; i--) {
				mObservers.get(i).onServiceDisconnected(name);
			}
		}
	}

	public void notifyUnbindService(Context context, Class<? extends Service> clazz) {
		synchronized (mObservers) {
			for (int i = mObservers.size() - 1; i >= 0; i--) {
				context.unbindService(mObservers.remove(i));
			}
		}
	}

	public boolean isEmpty() {
		return mObservers.isEmpty();
	}
}
