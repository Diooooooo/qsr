package com.qsr.sdk.jfinal;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qsr.sdk.lang.PageList;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.util.ParameterUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbUtil {
	static final Object[] NULL_PARA_ARRAY = new Object[0];

	static final void closeQuietly(AutoCloseable c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception e) {
			}
		}
	}

	private static Map<String, Object> record2map(Record record) {
		if (record == null) {
			return null;
		}
		Map<String, Object> m = record.getColumns();
		return m;
	}

	private static Parameter record2param(Record record) {
		if (record == null) {
			return null;
		}
		Map<String, Object> m = record.getColumns();
		return new Parameter(m);
	}

	protected static List<Map<String, Object>> record2list(List<Record> list) {

		List<Map<String, Object>> result = new ArrayList<>();
		if (list != null && list.size() > 0) {
			for (Record r : list) {
				result.add(record2map(r));
			}
		}
		return result;
	}

	//	protected <T> Map<T, Map<String, Object>> list2map(List<Record> list,
	//			String keyField) {
	//		Map<T, Map<String, Object>> result = new LinkedHashMap<T, Map<String, Object>>();
	//		//List<Map<String, Object>> list = new ArrayList<>();
	//		if (list != null && list.size() > 0) {
	//			for (Record r : list) {
	//				Map<String, Object> m = record2map(r);
	//				T key = (T) m.get(keyField);
	//				if (key == null) {
	//					continue;
	//				}
	//				result.put(key, m);
	//			}
	//		}
	//		return result;
	//	}

	private static PageList<Map<String, Object>> page2pagelist(Page<Record> page) {
		PageList<Map<String, Object>> result = new PageList<>(
				record2list(page.getList()), page.getTotalRow(),
				page.getTotalPage(), page.getPageNumber(), page.getPageSize());
		return result;
	}

	private static Object getGeneratedKey(PreparedStatement pst)
			throws SQLException {
		ResultSet rs = pst.getGeneratedKeys();
		Object id = null;
		if (rs.next())
			id = rs.getObject(1);
		closeQuietly(rs);
		return id;
	}

	public static int update(final String sql, final int[] generated,
			final Object... paras) {

		int r = (int) Db.execute(new ICallback() {

			@Override
			public Object call(Connection conn) throws SQLException {
				PreparedStatement pst = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				for (int i = 0; i < paras.length; i++) {
					pst.setObject(i + 1, paras[i]);
				}
				int result = pst.executeUpdate();
				Object generatedKey = getGeneratedKey(pst);
				if (generated != null && generated.length > 0
						&& (generatedKey instanceof Number)) {
					Number n = (Number) generatedKey;
					generated[0] = n.intValue();
				}
				closeQuietly(pst);
				return result;
			}
		});

		return r;

	}

	public static Object insertId(final String sql, final Object... paras) {
		final Object result[] = { null };
		int r = (int) Db.execute(new ICallback() {

			@Override
			public Object call(Connection conn) throws SQLException {
				PreparedStatement pst = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				for (int i = 0; i < paras.length; i++) {
					pst.setObject(i + 1, paras[i]);
				}
				int r = pst.executeUpdate();
				Object generatedKey = getGeneratedKey(pst);
				result[0] = generatedKey;

				closeQuietly(pst);
				return r;
			}
		});

		return result[0];
	}

	public static int insertIntId(String sql, Object... paras) {
		return ParameterUtil.i(insertId(sql, paras), 0);
	}

	public static long insertLongId(String sql, Object... paras) {
		return ParameterUtil.l(insertId(sql, paras), 0);
	}

	public static int update(final String sql, final long[] generated,
			final Object... paras) {

		int r = (int) Db.execute(new ICallback() {

			@Override
			public Object call(Connection conn) throws SQLException {
				PreparedStatement pst = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				for (int i = 0; i < paras.length; i++) {
					pst.setObject(i + 1, paras[i]);
				}
				int result = pst.executeUpdate();
				Object generatedKey = getGeneratedKey(pst);
				if (generated != null && generated.length > 0
						&& (generatedKey instanceof Number)) {
					Number n = (Number) generatedKey;
					generated[0] = n.longValue();
				}
				closeQuietly(pst);
				return result;
			}
		});

		return r;

	}

	public static int getIntValue(Record record, String field) {
		int result = 0;
		if (record != null) {
			Number number = record.getNumber(field);
			if (number != null) {
				result = number.intValue();
			}
		}
		return result;
	}

	public static Page<Record> paginate(int pageNumber, int pageSize,
			String select, String sqlExceptSelect, Object... paras) {
		int offset = pageSize * (pageNumber - 1);
		StringBuffer sql = new StringBuffer();
		sql.append(select).append(" ");
		sql.append(sqlExceptSelect);
		sql.append(" limit ").append(offset).append(", ").append(pageSize);
		List<Record> list = Db.find(sql.toString(), paras);
		int totalPage = list.size() < pageSize ? pageNumber : pageNumber + 1;
		int totalRow = list.size() < pageSize ? (pageNumber - 1) * pageSize
				+ list.size() : pageNumber * pageSize + 1;
		return new Page<Record>(list, pageNumber, pageSize, totalPage, totalRow);

	}

	public static Page<Record> paginate(int pageNumber, int pageSize,
			String select, String sqlExceptSelect) {
		return paginate(pageNumber, pageSize, select, sqlExceptSelect,
				NULL_PARA_ARRAY);
	}

	public Parameter findFirst(String sql, Object... paras) {

		Record record = Db.findFirst(sql, paras);
		if (record == null) {
			return null;
		}
		return new Parameter(record.getColumns());

	}
}
