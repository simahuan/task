package android.izy.os;

import android.os.AsyncTask;

/**
 * 简单的异步任务
 * 
 * @param <Result>
 * @author yangyp
 * @version 1.0, 2014年11月4日 下午12:53:20
 */
public abstract class AsyncTaskExt<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	/**
	 * Executes the task with the specified parameters. The task returns itself
	 * (this) so that the caller can keep a reference to it.
	 * 
	 * <p>
	 * Note: this function schedules the task on a queue for a single background
	 * thread or pool of threads depending on the platform version. When first
	 * introduced, AsyncTasks were executed serially on a single background
	 * thread. Starting with {@link android.os.Build.VERSION_CODES#DONUT}, this
	 * was changed to a pool of threads allowing multiple tasks to operate in
	 * parallel. Starting {@link android.os.Build.VERSION_CODES#HONEYCOMB},
	 * tasks are back to being executed on a single thread to avoid common
	 * application errors caused by parallel execution. If you truly want
	 * parallel execution, you can use the {@link #executeOnExecutor} version of
	 * this method with {@link #THREAD_POOL_EXECUTOR}; however, see commentary
	 * there for warnings on its use.
	 *
	 * <p>
	 * This method must be invoked on the UI thread.
	 *
	 * @param params
	 *            The parameters of the task.
	 *
	 * @return This instance of AsyncTask.
	 *
	 * @throws IllegalStateException
	 *             If {@link #getStatus()} returns either
	 *             {@link AsyncTask.Status#RUNNING} or
	 *             {@link AsyncTask.Status#FINISHED}.
	 *
	 * @see #executeOnExecutor(java.util.concurrent.Executor, Object[])
	 * @see #execute(Runnable)
	 */
	public final AsyncTask<Params, Progress, Result> executeExt(Params... params) {
		return executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
	}

}
