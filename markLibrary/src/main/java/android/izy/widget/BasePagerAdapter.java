package android.izy.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author yangyp
 * @version 1.0, 2014-7-8 下午1:26:00
 */
public abstract class BasePagerAdapter<T extends View> extends PagerAdapter {

	private List<T> mObjects = new ArrayList<T>();
	private final Object mLock = new Object();

	public List<T> getData() {
		return mObjects;
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
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mObjects.size();
	}

	public T getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(mObjects.get(position));
		return mObjects.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mObjects.get(position));
	}

	@Override
	public boolean isViewFromObject(View paramView, Object paramObject) {
		return paramView == paramObject; // 官方提示这样写
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
}
