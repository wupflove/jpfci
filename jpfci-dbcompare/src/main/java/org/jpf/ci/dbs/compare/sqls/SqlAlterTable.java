/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2016年5月3日 上午8:12:04 
* 类说明 
*/ 

package org.jpf.ci.dbs.compare.sqls;

import org.jpf.ci.dbs.compare.AiColumn;
import org.jpf.ci.dbs.compare.AiTable;
import org.jpf.ci.dbs.compare.AiTableIndex;


/**
 * 
 */
public class SqlAlterTable
{
	//取消表名上，数据库上名称的引号，适应ORACLE
	/**
	 * 
	 * @category 
	 * @author 吴平福 
	 * @param sb
	 * @param table
	 * @param cTableIndexPdm
	 * @param cTableIndexDev
	 * @param vSql
	 * @param iSqlType
	 * @param dbDomain
	 * @return
	 * update 2016年5月3日
	 */
	public static String[] makeAlterSql(StringBuffer sb, AiTable table, AiTableIndex cTableIndexPdm,
			AiTableIndex cTableIndexDev, String dbDomain )
	{
		String[] strSqls={"",""};
		strSqls[0]= "alter table " + dbDomain + "." + table.getTableName();
		if (cTableIndexDev.getConstraint_type() != null)
		{
			if (cTableIndexDev.getConstraint_type().equals("PRIMARY KEY"))
			{
				strSqls[0] += " drop PRIMARY key;";
			} else if (cTableIndexDev.getConstraint_type().equals("FOREIGN KEY"))
			{
				strSqls[0] += " drop foreign key " + cTableIndexDev.getIndexName() + ";";

			} else
			{
				strSqls[0] += " drop  index " + cTableIndexDev.getIndexName() + ";";
			}
		} else
		{
			strSqls[0] += " drop index " + cTableIndexDev.getIndexName() + ";";
		}
		strSqls[0] = strSqls[0].toUpperCase();
		//addSqlVector(vSql, strSql, iSqlType, table.getTableName());
		
		sb.append(strSqls[0]);
		strSqls[1] = "alter table " + dbDomain + "." + table.getTableName() + " add ";
		if (cTableIndexPdm.getConstraint_type() != null
				&& cTableIndexPdm.getConstraint_type().equalsIgnoreCase("PRIMARY KEY"))
		{
			strSqls[1] += "PRIMARY key("
					+ cTableIndexPdm.getColNames().substring(0, cTableIndexPdm.getColNames().length() - 1) + ");";
		} else
		{
			if (cTableIndexPdm.getNON_UNIQUE() == 0)
			{
				strSqls[1] += "unique ";
			}
			strSqls[1] += "index " + cTableIndexPdm.getIndexName() + "("
					+ cTableIndexPdm.getColNames().substring(0, cTableIndexPdm.getColNames().length() - 1) + ");";
		}
		strSqls[1] = strSqls[1].toUpperCase();
		//addSqlVector(vSql, strSql, iSqlType, table.getTableName());
		sb.append(strSqls[1]);
		strSqls[0]=SqlCommonUtils.checkSql(strSqls[0]);
		strSqls[1]=SqlCommonUtils.checkSql(strSqls[1]);
		return strSqls;
	}
	
	/**
	 * @category 删除字段SQL
	 * @param dbDomain
	 * @param tableDev
	 * @param pmdColumn
	 * @return
	 */
	public static String makeAlterDropColumnSql(String dbDomain,AiTable tableDev, AiColumn pmdColumn)
	{
		String strSql = "ALTER TABLE " + dbDomain + "." + tableDev.getTableName() + " DROP COLUMN "
				+ pmdColumn.getColumnName() + ";";
		return SqlCommonUtils.checkSql(strSql);
	}
	
	public static String makeModifyColumnSql(String dbDomain,AiTable tableDev, AiColumn pmdColumn)
	{
		String strSql = "ALTER TABLE " + dbDomain + "." + tableDev.getTableName() + " MODIFY " + pmdColumn.getColumnName()
				+ " " + pmdColumn.getDataType()
				+ SqlCommonUtils.addDefault(pmdColumn) + ";";
		return SqlCommonUtils.checkSql(strSql);
	}
	/**
	 * @category 增加字段
	 * @param dbDomain
	 * @param tableDev
	 * @param pmdColumn
	 * @param tablePdm
	 * @return
	 */
	public static String makeAlterAddColumnSql(String dbDomain,AiTable tableDev, AiColumn pmdColumn,AiTable tablePdm)
	{
		String strSql = "ALTER TABLE " + dbDomain + "." + tableDev.getTableName() + " ADD COLUMN "
				+ pmdColumn.getColumnName() + " "
				+ pmdColumn.getDataType() + SqlCommonUtils.addDefault(pmdColumn) + getPreColName(tablePdm, pmdColumn) + ";";
		
		return SqlCommonUtils.checkSql(strSql);
	}
	
    /**
     * 
     * @param table_pdm
     * @param pmdColumn
     * @return
     */
	private static String getPreColName(AiTable table_pdm, AiColumn pmdColumn)
	{
		/*
		 * int iPos = pmdColumn.getOrdinal_position(); if (1 == iPos) { return
		 * " first"; } else { iPos = iPos - 1; } for (Iterator iter_column =
		 * table_pdm.columns.keySet().iterator(); iter_column.hasNext();) {
		 * String key_column = (String) iter_column.next(); Column column_pdm =
		 * (Column) table_pdm.columns.get(key_column); if (iPos ==
		 * column_pdm.getOrdinal_position()) { return " after " +
		 * column_pdm.getColumnName(); } }
		 */
		return "";
	}
}
