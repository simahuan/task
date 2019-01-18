package android.izy.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.widget.BaseAdapter;

public abstract class BaseListAdapterOld<T> extends BaseAdapter {

	private List<T> mObjects = new ArrayList<T>();;
	private final Object mLock = new Object();
	private boolean mNotifyOnChange = true;

	public List<T> getData() {
		return mObjects;
	}

	public void setData(Collection<? extends T> collection) {
		synchronized (mLock) {
			mObjects.clear();
			mObjects.addAll(collection);
		}
		invokeNotifyDataSetChanged();
	}

	public void add(T object) {
		synchronized (mLock) {
			mObjects.add(object);
		}
		invokeNotifyDataSetChanged();
	}

	public void addAll(Collection<? extends T> collection) {
		synchronized (mLock) {
			if (collection != null) {
				mObjects.addAll(collection);
			}
		}
		invokeNotifyDataSetChanged();
	}

	public void addAll(T... items) {
		synchronized (mLock) {
			if (items != null) {
				Collections.addAll(mObjects, items);
			}
		}
		invokeNotifyDataSetChanged();
	}

	public void insert(T object, int index) {
		synchronized (mLock) {
			mObjects.add(index, object);
		}
		invokeNotifyDataSetChanged();
	}

	public void remove(T object) {
		synchronized (mLock) {
			mObjects.remove(object);
		}
		invokeNotifyDataSetChanged();
	}

	public void clear() {
		synchronized (mLock) {
			mObjects.clear();
		}
		invokeNotifyDataSetChanged();
	}

	public void sort(Comparator<? super T> comparator) {
		synchronized (mLock) {
			Collections.sort(mObjects, comparator);
		}
		invokeNotifyDataSetChanged();
	}

	private void invokeNotifyDataSetChanged() {
		if (mNotifyOnChange) {
			notifyDataSetChanged();
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		mNotifyOnChange = true;
	}

	public void setNotifyOnChange(boolean notifyOnChange) {
		mNotifyOnChange = notifyOnChange;
	}

	@Override
	public int getCount() {
		return mObjects.size();
	}

	@Override
	public T getItem(int position) {
		return mObjects.get(position);
	}

	public int getPosition(T item) {
		return mObjects.indexOf(item);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
