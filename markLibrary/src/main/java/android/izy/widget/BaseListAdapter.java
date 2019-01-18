package android.izy.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public abstract class BaseListAdapter<T> extends ArrayAdapter<T> {

	public BaseListAdapter(Context context) {
		this(context, new ArrayList<T>());
	}

	public BaseListAdapter(Context context, List<T> objects) {
		super(context, android.R.layout.simple_list_item_2, objects);
	}

	public void setData(T... data) {
		setNotifyOnChange(false);
		clear();
		if (data != null) {
			addAll(data);
		}
		notifyDataSetChanged();
	}

	public void setData(Collection<? extends T> data) {
		setNotifyOnChange(false);
		clear();
		if (data != null) {
			addAll(data);
		}
		notifyDataSetChanged();
	}

	public abstract View getView(int position, View convertView, ViewGroup parent);
}
