package org.jpf.ci.dbs.compare.sqls;

public class SqlDroptable {

	/**
	 * @category 产生删除表或试图DDL
	 * @param table_type
	 * @param dbDomain
	 * @param table_name
	 * @return
	 */
	public static String getDropSql(String table_type, String dbDomain,
			String table_name) {
		String strSql = "";
		if (table_type.equalsIgnoreCase("view")) {

			strSql = "drop view ";
		} else {
			strSql = "drop table ";
		}

		strSql += " if exists " + dbDomain + "." + table_name + ";";
		return strSql.toUpperCase();
	}
}
