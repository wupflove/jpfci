package org.jpf.ci.dbs.compare.sqls;

import org.jpf.ci.dbs.compare.AiColumn;

public class SqlCommonUtils {

	/**
	 * @category checksql
	 * @param strSql
	 * @return
	 */
	public static String checkSql(String strSql)
	{
		if (strSql==null || strSql.trim().length()==0)
		{
			return "";
		}
		strSql=strSql.trim();
		if (!strSql.endsWith(";"))
		{
			strSql+=";";
		}
		strSql=strSql.replaceAll("'", "");
		strSql=strSql.replaceAll("`", "");
		return strSql;
	}
	/**
	 * 
	 * @param pmdColumn
	 * @return
	 */
	public static String addDefault(AiColumn pmdColumn)
	{
		String tmpStr = "";
		if (pmdColumn.getNullable().equalsIgnoreCase("no"))
		{
			tmpStr = " NOT NULL";
		}
		tmpStr += getDefalut(pmdColumn);
		if (null != pmdColumn.getExtra() && pmdColumn.getExtra().length() > 0)
		{
			tmpStr += " " + pmdColumn.getExtra();
		}
		return tmpStr;
	}

	/**
	 * @category 获取默认值
	 * @param pmdColumn
	 * @return
	 */
	private static String getDefalut(AiColumn pmdColumn)
	{
		if (null != pmdColumn.getColumnDefault() && pmdColumn.getColumnDefault().length() > 0)
		{
			if (pmdColumn.getDataType().toLowerCase().startsWith("bigint"))
			{
				return " DEFAULT " + pmdColumn.getColumnDefault();
			}

			if (pmdColumn.getColumnDefault().equalsIgnoreCase("0000-00-00 00:00:00"))
			{
				return " DEFAULT '0000-00-00 00:00:00'";
			}
			if (pmdColumn.getDataType().toLowerCase().startsWith("char"))
			{
				return " DEFAULT '" + pmdColumn.getColumnDefault() + "'";
			}
			if (pmdColumn.getDataType().toLowerCase().startsWith("varchar"))
			{
				return " DEFAULT '" + pmdColumn.getColumnDefault() + "'";
			}
			if (pmdColumn.getDataType().toLowerCase().startsWith("text"))
			{
				return " DEFAULT '" + pmdColumn.getColumnDefault() + "'";
			}

			if (pmdColumn.getDataType().toLowerCase().startsWith("longtext"))
			{
				return " DEFAULT '" + pmdColumn.getColumnDefault() + "'";
			}
			if (pmdColumn.getDataType().toLowerCase().startsWith("mediumtext"))
			{
				return " DEFAULT '" + pmdColumn.getColumnDefault() + "'";
			}
			if (pmdColumn.getDataType().toLowerCase().startsWith("decimal"))
			{
				return " DEFAULT " + pmdColumn.getColumnDefault();
			}
			if (pmdColumn.getDataType().toLowerCase().startsWith("date"))
			{
				return " DEFAULT " + pmdColumn.getColumnDefault();
			}
			if (pmdColumn.getDataType().toLowerCase().startsWith("datetime"))
			{
				return " DEFAULT " + pmdColumn.getColumnDefault();
			}
			if (pmdColumn.getDataType().toLowerCase().startsWith("time"))
			{
				return " DEFAULT " + pmdColumn.getColumnDefault();
			}
			if (pmdColumn.getDataType().toLowerCase().startsWith("timestamp"))
			{
				return " DEFAULT " + pmdColumn.getColumnDefault();
			}
			if (pmdColumn.getDataType().toLowerCase().startsWith("int"))
			{
				return " DEFAULT " + pmdColumn.getColumnDefault();
			}
			if (pmdColumn.getDataType().toLowerCase().startsWith("float"))
			{
				return " DEFAULT " + pmdColumn.getColumnDefault();
			}

			if (pmdColumn.getDataType().toLowerCase().startsWith("smallint"))
			{
				return " DEFAULT " + pmdColumn.getColumnDefault();
			}
			if (pmdColumn.getDataType().toLowerCase().startsWith("tinyint"))
			{
				return " DEFAULT " + pmdColumn.getColumnDefault();
			}
		}
		return "";
	}
}
