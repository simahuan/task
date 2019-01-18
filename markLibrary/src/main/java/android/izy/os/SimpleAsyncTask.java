package android.izy.os;

import android.os.AsyncTask;
import android.os.OperationCanceledException;
import android.util.Log;

/**
 * 简单的异步任务
 * 
 * @param <Result>
 * @author yangyp
 * @version 1.0, 2014年11月4日 下午12:53:20
 */
public abstract class SimpleAsyncTask<Result> {

	static final String TAG = "SimpleAsyncTask";
	private volatile LoadTask mTask;

	final class LoadTask extends AsyncTask<Void, Object, Result> {

		@Override
		protected Result doInBackground(Void... params) {
			Log.v(TAG, this + " >>> doInBackground");
			try {
				return SimpleAsyncTask.this.doInBackground();
			} catch (OperationCanceledException ex) {
				if (!isCancelled()) {
					throw ex;
				}
				Log.v(TAG, this + "  <<< doInBackground (was canceled)", ex);
				return null;
			}
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			Log.v(TAG, this + " onProgressUpdate");
			SimpleAsyncTask.this.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Result data) {
			Log.v(TAG, this + " onPostExecute");
			SimpleAsyncTask.this.onPostExecute(data);
		}

		@Override
		protected void onCancelled(Result data) {
			Log.v(TAG, this + " onCancelled");
			SimpleAsyncTask.this.onCancelled();
		}

		/**
		 * 进度更新
		 * 
		 * @param values
		 */
		public void publishProgressObject(Object... values) {
			publishProgress(values);
		}
	}

	public SimpleAsyncTask() {
		mTask = new LoadTask();
	}

	/**
	 * 后台执行，比较耗时的操作都可以放在这里。注意这里不能直接操作UI。此方法在后台线程执行，完成任务的主要工作，通常需要较长的时间。
	 * 在执行过程中可以调用publicProgress(Progress…)来更新任务的进度。
	 */
	protected abstract Result doInBackground();

	/**
	 * 这里是最终用户调用Excute时的接口，当任务执行之前开始调用此方法，可以在这里显示进度对话框。
	 */
	protected void onPreExecute() {
	}

	/**
	 * 可以使用进度条增加用户体验度
	 * 
	 * @param values
	 */
	protected void onProgressUpdate(Object... values) {
	}

	/**
	 * 相当于Handler 处理UI的方式，在这里面可以使用在doInBackground 得到的结果处理操作UI。
	 * 此方法在主线程执行，任务执行的结果作为此方法的参数返回
	 * 
	 * @param data
	 */
	protected void onPostExecute(Result data) {
	}

	/**
	 * 取消回调
	 */
	protected void onCancelled() {
	}

	/**
	 * 是否取消任务
	 * 
	 * @return
	 */
	public final boolean isCancelled() {
		return mTask.isCancelled();
	}

	/**
	 * 取消任务
	 * 
	 * @return
	 */
	public final boolean cancel() {
		return mTask.cancel(true);
	}

	public final SimpleAsyncTask<Result> execute() {
		mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
		return this;
	}

	protected final void publishProgress(Object... values) {
		mTask.publishProgressObject(values);
	}
}
