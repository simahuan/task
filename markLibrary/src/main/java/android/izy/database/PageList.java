package android.izy.database;

import java.util.List;

public class PageList<T> {

	public static final int DEFAULT_PAGESIZE = 20;
	public int currentPage = 1; // 当前页号
	public int pageSize = DEFAULT_PAGESIZE; // 每页显示的行数
	public long totalCount; // 总行数
	public List<T> rows;

	public PageList() {
	}

	public PageList(int currentPage) {
		this(currentPage, DEFAULT_PAGESIZE);
	}

	public PageList(int currentPage, int pageSize) {
		this(currentPage, pageSize, 0, null);
	}

	public PageList(int currentPage, int pageSize, long totalCount, List<T> rows) {
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.totalCount = totalCount;
		this.rows = rows;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public long getPageSize() {
		return pageSize <= 0 ? DEFAULT_PAGESIZE : pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public List<T> getRows() {
		return rows;
	}

	/**
	 * 总页数
	 * 
	 * @return
	 */
	public long getTotalPage() {
		return totalCount / getPageSize() + (totalCount % getPageSize() == 0 ? 0 : 1);
	}

	/**
	 * 是否存在上一页
	 * 
	 * @return
	 */
	public boolean isPrevPage() {
		return currentPage > 1;
	}

	/**
	 * 是否存在下一页
	 * 
	 * @return
	 */
	public boolean isNextPage() {
		return currentPage < getTotalPage();
	}

	/**
	 * 执行上一页
	 */
	public void previous() {
		if (isPrevPage()) {
			currentPage--;
		}
	}

	/**
	 * 下一页
	 */
	public void next() {
		if (isNextPage()) {
			currentPage++;
		}
	}

	/**
	 * 起始记录数
	 * 
	 * @return
	 */
	public long getFirstResult() {
		return (currentPage - 1) * getPageSize();
	}

	/**
	 * 当前页显示的行数
	 * 
	 * @return
	 */
	public long getMaxResults() {
		return getPageSize();
	}
}
