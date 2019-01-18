package android.izy.widget;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseExpandableAdapter<Group, Child> extends android.widget.BaseExpandableListAdapter {

	protected List<Group> groups;
	protected List<List<Child>> childs;

	public BaseExpandableAdapter() {
		super();
		this.groups = new ArrayList<Group>();
		this.childs = new ArrayList<List<Child>>();
	}

	public List<Child> getChild(int groupPosition) {
		return childs.get(groupPosition);
	}

	public void addItem(Group group, List<Child> child) {
		groups.add(group);
		childs.add(child);
		notifyDataSetChanged();
	}

	public void remove(Group group) {
		int index = groups.indexOf(group);
		remove(index);
		notifyDataSetChanged();
	}

	public void remove(int groupPosition) {
		groups.remove(groupPosition);
		childs.remove(groupPosition);
		notifyDataSetChanged();
	}

	public void remove(int groupPosition, int childPosition) {
		childs.get(groupPosition).remove(childPosition);
		notifyDataSetChanged();
	}

	public void clear() {
		groups.clear();
		childs.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childs.get(groupPosition).size();
	}

	@Override
	public Group getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public Child getChild(int groupPosition, int childPosition) {
		return childs.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	/**
	 * 是否指定分组视图及其子视图的ID对应的后台数据改变也会保持该ID。
	 */
	@Override
	public boolean hasStableIds() {
		return true;
	}

	/**
	 * 指定位置的子视图是否可选择。
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void notifyObservers() {
		notifyDataSetChanged();
	}

}
