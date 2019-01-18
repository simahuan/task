package android.izy.database.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.BaseColumns;

/**
 * 数据库访问工具类
 * 
 * @author yangyp
 * @created 2014年8月10日 下午5:49:43
 */
public abstract class SqliteHelper extends SQLiteOpenHelper {

	public SqliteHelper(Context context, String dbName, int version) {
		super(context, getDbName(context, dbName), null, version);
	}

	/**
	 * 获取数据保存位置
	 * 
	 * @param context
	 * @param dbName
	 * @return
	 */
	private static String getDbName(Context context, String dbName) {
		boolean hasSDCard = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
		return hasSDCard ? context.getExternalFilesDir(null) + "/" + dbName : dbName;
	}

	/**
	 * 新增字段
	 * 
	 * @param db
	 * @param dbTable
	 * @param columnName
	 * @param columnDefinition
	 */
	protected void addColumn(SQLiteDatabase db, String dbTable, String columnName, String columnDefinition) {
		db.execSQL("ALTER TABLE " + dbTable + " ADD COLUMN " + columnName + " " + columnDefinition);
	}

	/**
	 * 删除字段
	 * 
	 * @param db
	 * @param dbTable
	 * @param columnName
	 */
	protected void dorpColumn(SQLiteDatabase db, String dbTable, String columnName) {
		db.execSQL("ALTER TABLE " + dbTable + " DROP COLUMN " + columnName);
	}

	/**
	 * 修改字段
	 * 
	 * @param db
	 * @param dbTable
	 * @param columnName
	 * @param columnDefinition
	 */
	protected void alterColumn(SQLiteDatabase db, String dbTable, String columnName, String columnDefinition) {
		db.execSQL("ALTER TABLE " + dbTable + " ALTER COLUMN " + columnName + " " + columnDefinition);
	}

	/**
	 * 获取主键
	 * 
	 * @param db
	 * @param tableName
	 * @param primaryKey
	 * @param rowid
	 * @return
	 */
	protected long getGeneratedKey(SQLiteDatabase db, String dbTable, String primaryKey, long rowid) {
		long result = -1;
		Cursor cursor = db.rawQuery("SELECT " + primaryKey + " FROM " + dbTable + " WHERE rowid = " + rowid, null);
		if (cursor.moveToNext()) {
			result = cursor.getLong(0);
			cursor.close();
		}
		return result;
	}

	/**
	 * 添加记录
	 * 
	 * @param table
	 * @param values
	 */
	public void insert(String table, ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		db.insert(table, null, values);
	}

	/**
	 * 添加记录带返回主键
	 * 
	 * @param table
	 * @param values
	 * @return 选择主键BaseColumns._ID值
	 */
	public long insertForGeneratedKey(String table, ContentValues values) {
		return insertForGeneratedKey(table, values, BaseColumns._ID);
	}

	/**
	 * 添加记录带返回主键
	 * 
	 * @param table
	 * @param values
	 * @param primaryKey
	 *            主键字段
	 * @return 选择主键BaseColumns._ID值
	 */
	public long insertForGeneratedKey(String table, ContentValues values, String primaryKey) {
		long generatedKey = -1;
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			long rowid = db.insert(table, null, values);
			generatedKey = getGeneratedKey(db, table, primaryKey, rowid);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return generatedKey;
	}

	/**
	 * 根据ID更新记录
	 * 
	 * @param id
	 * @param values
	 */
	public int update(String table, ContentValues values, long... ids) {
		return update(table, values, getWhereClauseForIds(ids), getWhereArgsForIds(ids));
	}

	/**
	 * 更新记录
	 * 
	 * @param id
	 * @param values
	 */
	public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = getWritableDatabase();
		return db.update(table, values, whereClause, whereArgs);
	}

	/**
	 * 根据ID删除记录
	 * 
	 * @param ids
	 * @param values
	 */
	public int delete(String table, long... ids) {
		return delete(table, getWhereClauseForIds(ids), getWhereArgsForIds(ids));
	}

	/**
	 * 删除记录
	 * 
	 * @param table
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int delete(String table, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = getWritableDatabase();
		return db.delete(table, whereClause, whereArgs);
	}

	/**
	 * 生成 SQL WHERE条件
	 */
	private static String getWhereClauseForIds(long... ids) {
		StringBuilder whereClause = new StringBuilder();
		whereClause.append("(");
		for (int i = 0; i < ids.length; i++) {
			if (i > 0) {
				whereClause.append("OR ");
			}
			whereClause.append(BaseColumns._ID);
			whereClause.append(" = ? ");
		}
		whereClause.append(")");
		return whereClause.toString();
	}

	/**
	 * 生成 SQL WHERE条件参数
	 */
	private static String[] getWhereArgsForIds(long... ids) {
		String[] whereArgs = new String[ids.length];
		for (int i = 0; i < ids.length; i++) {
			whereArgs[i] = Long.toString(ids[i]);
		}
		return whereArgs;
	}

	/**
	 * 根据条件查询单条记录
	 * 
	 * @param table
	 * @param selection
	 * @param selectionArgs
	 * @param rawQuery
	 * @return
	 */
	public <T> T queryForObject(String table, RawQuery<T> rawQuery, long id) {
		return queryForObject(table, getWhereClauseForIds(id), getWhereArgsForIds(id), rawQuery);
	}

	/**
	 * 根据条件查询单条记录
	 * 
	 * @param table
	 * @param selection
	 * @param selectionArgs
	 * @param rawQuery
	 * @return
	 */
	public <T> T queryForObject(String table, String selection, String[] selectionArgs, RawQuery<T> rawQuery) {
		List<T> results = query(table, selection, selectionArgs, null, null, rawQuery);
		return results.size() > 0 ? results.get(0) : null;
	}

	/**
	 * 根据条件查询单条记录
	 * 
	 * @param table
	 * @param selection
	 * @param selectionArgs
	 * @param rawQuery
	 * @return
	 */
	public <T> List<T> query(String table, RawQuery<T> rawQuery, long ids) {
		return query(table, getWhereClauseForIds(ids), getWhereArgsForIds(ids), null, null, rawQuery);
	}

	/**
	 * 根据条件查询数据列表
	 * 
	 * @param selection
	 * @param selectionArgs
	 * @param orderBy
	 * @param limit
	 * @param rawQuery
	 * @return
	 */
	public <T> List<T> query(String table, String selection, String[] selectionArgs, String orderBy, String limit, RawQuery<T> rawQuery) {
		List<T> result = new ArrayList<T>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(table, null, selection, selectionArgs, null, null, orderBy, limit);
		if (cursor != null) {
			int rowNum = 0;
			while (cursor.moveToNext()) {
				result.add(rawQuery.rawQuery(cursor, rowNum++));
			}
			cursor.close();
		}
		db.close();
		return result;
	}

	/**
	 * 根据条件查询单条记录
	 * 
	 * @param sql
	 * @param selectionArgs
	 * @param rawQuery
	 * @return
	 */
	public <T> T rawQueryForObject(String sql, String[] selectionArgs, RawQuery<T> rawQuery) {
		List<T> results = rawQuery(sql, selectionArgs, rawQuery);
		return results.size() > 0 ? results.get(0) : null;
	}

	/**
	 * 根据SQL查询数据列表
	 * 
	 * @param sql
	 * @param selectionArgs
	 * @param rawQuery
	 * @return
	 */
	public <T> List<T> rawQuery(String sql, String[] selectionArgs, RawQuery<T> rawQuery) {
		List<T> result = new ArrayList<T>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		if (cursor != null) {
			int rowNum = 0;
			while (cursor.moveToNext()) {
				result.add(rawQuery.rawQuery(cursor, rowNum++));
			}
			cursor.close();
		}
		db.close();
		return result;
	}

	/**
	 * 记录行查询回调
	 * 
	 * @param <T>
	 * @author yangyp
	 * @version 1.0, 2014年10月29日 下午4:51:26
	 */
	public interface RawQuery<T> {
		T rawQuery(Cursor cursor, int rowNum);
	}

}
