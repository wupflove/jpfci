/** 
 * @author 吴平福 
 * E-mail:wupf@asiainfo.com 
 * @version 创建时间：2015年2月14日 上午1:26:12 
 * 类说明 
 */

package org.jpf.ci.dbs.compare;

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.ci.dbs.compare.sqls.SqlAlterTable;
import org.jpf.ci.dbs.compare.sqls.SqlDroptable;

import org.jpf.utils.ios.AiFileUtil;


/**
 * 
 */
public class CompareUtil
{
	private static final Logger LOGGER = LogManager.getLogger();

    /**
     * 
     * @category 
     * @author 吴平福 
     * @param cTablePdm
     * @param cTableDev
     * @param cTableIndexPdm
     * @param cTableIndexDev
     * @param flag
     * @param sb
     * @param vSql
     * @throws Exception
     * update 2016年5月3日
     */
	public static void appendIndex(AiTable cTablePdm,AiTable cTableDev, AiTableIndex cTableIndexPdm, AiTableIndex cTableIndexDev, int flag,
			StringBuffer[] sb, Vector<ExecSqlInfo> vSql,String dbDomain) throws Exception
	{
		iCount++;
		String strClassTypeString = "class=\"alt\"";
		if (iCount % 2 == 0)
		{
			strClassTypeString = "";
		}
		String strSql = "";
		switch (flag)
		{
		case 10:
			LOGGER.info("11、PDM存在，开发不存在的索引：" + cTablePdm.getTableName() + "." + cTableIndexPdm.getIndexName());
			sb[0].append("<tr").append(strClassTypeString)
					.append("><th scope=\"row\" abbr=\"L2 Cache\" class=\"specalt\">11")
					.append("</th><td>").append(getParentTableName(cTablePdm.getTableName())).append("<br>(").append(AiFileUtil.getFileName(cTablePdm.pdmName))
					.append(")</td><td >").append(cTableIndexPdm.getIndexName())
					.append("</td><td >").append(cTableIndexPdm.getColNames())
					.append("</td><td >").append(cTableIndexPdm.getConstraint_type())
					.append("</td><td ></td><td ></td><td ></td><td ></td><td >");
			strSql = "alter table " + dbDomain + "." + cTablePdm.getTableName() + " add ";
			if (cTableIndexPdm.getConstraint_type() != null
					&& cTableIndexPdm.getConstraint_type().equals("PRIMARY KEY"))
			{
				strSql += " PRIMARY key("
						+ cTableIndexPdm.getColNames().substring(0, cTableIndexPdm.getColNames().length() - 1) + ");";
			} else
			{
				if (cTableIndexPdm.getNON_UNIQUE() == 0)
				{
					strSql += " unique ";
				}
				strSql += " index " + cTableIndexPdm.getIndexName() + "("
						+ cTableIndexPdm.getColNames().substring(0, cTableIndexPdm.getColNames().length() - 1) + ");";

			}
			strSql = strSql.toUpperCase();
			addSqlVector(vSql, strSql, 10, cTablePdm.getTableName());
			sb[0].append(strSql).append("</td></tr>");
			break;
		case 11:
			LOGGER.info("12、PDM不存在，开发存在的索引：" + cTableDev.getTableName()+"."+cTableIndexDev.getIndexName());// 需要人工判断脚本
			sb[1].append("<tr").append(strClassTypeString)
					.append("><th scope=\"row\" abbr=\"L2 Cache\" class=\"specalt\">12").append("</th><td>")
					.append(getParentTableName(cTableDev.getTableName())).append("</td><td >")
					.append("</td><td ></td><td ></td><td >")
					.append(cTableDev.getTableName()).append("</td><td >")
					.append(cTableIndexDev.getIndexName()).append("</td><td >")
					.append(cTableIndexDev.getColNames())
					.append("</td><td>")
					.append(cTableIndexDev.getConstraint_type())
					.append("</td><td >");
			strSql = "alter table " + dbDomain + "." + cTableDev.getTableName() + " drop ";
			if (null != cTableIndexDev.getConstraint_type())
			{
				if (cTableIndexDev.getConstraint_type().equals("PRIMARY KEY"))
				{
					strSql += "  PRIMARY key ";
				} else if (cTableIndexDev.getConstraint_type().equals("FOREIGN KEY"))
				{
					strSql += "  foreign key `" + cTableIndexDev.getIndexName() + "`";
				} else
				{
					strSql += "  index `" + cTableIndexDev.getIndexName() + "`";
				}
			} else
			{
				strSql += "  index `" + cTableIndexDev.getIndexName() + "`";
			}
			strSql += ";";
			strSql = strSql.toUpperCase();
			addSqlVector(vSql, strSql, 11, cTableDev.getTableName());
			sb[1].append(strSql).append("</td></tr>");

			break;
		case 12:
			LOGGER.info("13、 索引内容不同：" + cTablePdm.getTableName()
					+ " | " + cTableIndexPdm.getIndexName());// 需人工判断如何处理
			sb[2].append("<tr").append(strClassTypeString)
					.append("><th scope=\"row\" abbr=\"L2 Cache\" class=\"specalt\">13")
					.append("</th><td>").append(getParentTableName(cTablePdm.getTableName())).append("<br>(").append(AiFileUtil.getFileName(cTablePdm.pdmName))
					.append(")</td><td >").append(cTableIndexPdm.getIndexName())
					.append("</td><td >").append(cTableIndexPdm.getColNames())
					.append("</td><td >").append(cTableIndexPdm.getNON_UNIQUE())
					.append("</td><td>").append(cTableDev.getTableName())
					.append("</td><td >").append(cTableIndexDev.getIndexName())
					.append("</td><td >").append(cTableIndexDev.getColNames())
					.append("</td><td>")
					.append("</td><td>");

			//makeAlterSql(sb[2], cTableDev, cTableIndexPdm, cTableIndexDev, vSql, 12);
			//addSqlVector(vSql, strSql, iSqlType, table.getTableName());
			addSqlVector(vSql,SqlAlterTable.makeAlterSql(sb[2], cTableDev, cTableIndexPdm, cTableIndexDev, dbDomain),12,cTableDev.getTableName());
			sb[2].append("</td></tr>");
			break;
		case 13:
			LOGGER.info("14、 索引内容不同：" + cTablePdm.getTableName()
					+ " | " + cTableIndexPdm.getIndexName());// 需人工判断如何处理
			sb[3].append("<tr").append(strClassTypeString)
					.append("><th scope=\"row\" abbr=\"L2 Cache\" class=\"specalt\">14")
					.append("</th><td>").append(getParentTableName(cTablePdm.getTableName())).append("<br>(").append(AiFileUtil.getFileName(cTablePdm.pdmName))
					.append(")</td><td >").append(cTableIndexPdm.getIndexName())
					.append("</td><td >").append(cTableIndexPdm.getColNames())
					.append("</td><td >").append(cTableIndexPdm.getNON_UNIQUE())
					.append("</td><td>").append(cTableDev.getTableName())
					.append("</td><td >").append(cTableIndexDev.getIndexName())
					.append("</td><td >").append(cTableIndexDev.getColNames())
					.append("</td><td>").append(cTableIndexDev.getNON_UNIQUE())
					.append("</td><td>");

			addSqlVector(vSql,SqlAlterTable.makeAlterSql(sb[3], cTableDev, cTableIndexPdm, cTableIndexDev, dbDomain),13,cTableDev.getTableName());
			sb[3].append("</td></tr>");
			break;
		case 14:
			LOGGER.info("15、 索引内容不同：" + cTablePdm.getTableName()
					+ " | " + cTableIndexPdm.getIndexName());// 需人工判断如何处理
			sb[4].append("<tr").append(strClassTypeString)
					.append("><th scope=\"row\" abbr=\"L2 Cache\" class=\"specalt\">15")
					.append("</th><td>").append(getParentTableName(cTablePdm.getTableName())).append("<br>(").append(AiFileUtil.getFileName(cTablePdm.pdmName))
					.append(")</td><td >").append(cTableIndexPdm.getIndexName())
					.append("</td><td >").append(cTableIndexPdm.getColNames())
					.append("</td><td >").append(cTableIndexPdm.getConstraint_type())
					.append("</td><td>").append(cTableDev.getTableName())
					.append("</td><td >").append(cTableIndexDev.getIndexName())
					.append("</td><td >").append(cTableIndexDev.getColNames())
					.append("</td><td>").append(cTableIndexDev.getConstraint_type())
					.append("</td><td>");

			addSqlVector(vSql,SqlAlterTable.makeAlterSql(sb[4], cTableDev, cTableIndexPdm, cTableIndexDev, dbDomain),14,cTableDev.getTableName());
			sb[4].append("</td></tr>");
			break;
		case 15:
			LOGGER.info("11、PDM存在，开发不存在的索引：" + cTablePdm.getTableName() + "." + cTableIndexPdm.getIndexName());
			sb[0].append("<tr").append(strClassTypeString)
					.append("><th scope=\"row\" abbr=\"L2 Cache\" class=\"specalt\">11")
					.append("</th><td>").append(getParentTableName(cTablePdm.getTableName())).append("<br>(").append(AiFileUtil.getFileName(cTablePdm.pdmName))
					.append(")</td><td >").append(cTableIndexPdm.getIndexName())
					.append("</td><td >").append(cTableIndexPdm.getColNames())
					.append("</td><td >").append(cTableIndexPdm.getConstraint_type())
					.append("</td><td >").append(cTableDev.getTableName())
					.append("</td><td ></td><td ></td><td ></td><td >");
			strSql = "alter table " + dbDomain + "." + cTablePdm.getTableName() + " add ";
			if (cTableIndexPdm.getConstraint_type() != null
					&& cTableIndexPdm.getConstraint_type().equals("PRIMARY KEY"))
			{
				strSql += "PRIMARY key("
						+ cTableIndexPdm.getColNames().substring(0, cTableIndexPdm.getColNames().length() - 1) + ");";
			} else
			{
				if (cTableIndexPdm.getNON_UNIQUE() == 0)
				{
					strSql += "unique ";
				}
				strSql += "index " + cTableIndexPdm.getIndexName() + "("
						+ cTableIndexPdm.getColNames().substring(0, cTableIndexPdm.getColNames().length() - 1) + ");";

			}
			strSql = strSql.toUpperCase();
			addSqlVector(vSql, strSql, 10, cTablePdm.getTableName());
			sb[0].append(strSql).append("</td></tr>");
			break;
		}

	}



	private static int iCount = 0;

	public static boolean isSubTable(String key_table)
	{
		String regex = ".*_[0-9].*";
		return key_table.matches(regex);
	}

	// 判断是否是分表:先判断是否有特定规则，后按照 分表=母表+_[0-9].*,进行匹配判断
	public static boolean isSubTable(String key_table, String strTableName)
	{
		HashMap<String, String> pc = AiTableInfoMainAndSub.getParentChild();
		String ParentTabelName = getParentTableNameConven(strTableName);
		if (pc.containsKey(ParentTabelName))
		{
			if (pc.get(ParentTabelName).equals(key_table))
			{
				return true;
			} else
				return false;
		}
		String regex = key_table + "_[0-9].*";
		return strTableName.matches(regex);
	}

	public static String getParentTableNameConven(String strTableName)
	{
		String regex = "_[0-9]";
		Pattern pat = Pattern.compile(regex);
		String[] str = pat.split(strTableName);
		return str[0];
	}

	// 获得母表
	public static String getParentTableName(String strTableName)
	{
		HashMap<String, String> pc = AiTableInfoMainAndSub.getParentChild();
		String ParentTabelName = getParentTableNameConven(strTableName);
		if (pc.containsKey(ParentTabelName))
		{
			return pc.get(ParentTabelName);
		} else
		{
			return ParentTabelName;
		}
	}


    /**
     * 
     * @category 
     * @author 吴平福 
     * @param vSql
     * @param strSql
     * @param iType
     * @param strTableName
     * update 2016年5月3日
     */
	protected static void addSqlVector(Vector<ExecSqlInfo> vSql, String strSql, int iType, String strTableName)
	{
		ExecSqlInfo cExecSqlInfo = new ExecSqlInfo();
		cExecSqlInfo.setiType(iType);
		cExecSqlInfo.setStrSql(strSql);
		cExecSqlInfo.setStrTable(strTableName);
		vSql.add(cExecSqlInfo);
	}
   
	/**
	 * 
	 * @category 
	 * @author 吴平福 
	 * @param vSql
	 * @param strSqls
	 * @param iType
	 * @param strTableName
	 * update 2016年5月3日
	 */
	private static void addSqlVector(Vector<ExecSqlInfo> vSql, String[] strSqls, int iType, String strTableName)
	{
		for(int i=0;i<strSqls.length;i++)
		{
		ExecSqlInfo cExecSqlInfo = new ExecSqlInfo();
		cExecSqlInfo.setiType(iType);
		cExecSqlInfo.setStrSql(strSqls[i]);
		cExecSqlInfo.setStrTable(strTableName);
		vSql.add(cExecSqlInfo);
		}
	}
	
	private static String getLastVectorValue(Vector<ExecSqlInfo> vSql)
	{
		ExecSqlInfo cExecSqlInfo = vSql.lastElement();
		return cExecSqlInfo.getStrSql();
	}

	public static void appendTable(AiTable tablePdm, AiTable tableDev, AiColumn pmdColumn, AiColumn developColumn, int flag,
			StringBuffer[] sb, Vector<ExecSqlInfo> vSql,String dbDomain)
					throws Exception
	{
		iCount++;
		String strClassTypeString = "class=\"alt\"";
		if (iCount % 2 == 0)
		{
			strClassTypeString = "";
		}
		String strSql = "";
		switch (flag)
		{
		case 1:
			LOGGER.info("1、PDM存在，比对库不存在的表：" + tablePdm.getTableName());// 跳过
			sb[0].append("<tr").append(strClassTypeString)
					.append("><th scope=\"row\" abbr=\"L2 Cache\" class=\"specalt\">1")
					.append("</th><td>")
					.append(tablePdm.getTableName()).append("<br>(").append(AiFileUtil.getFileName(tablePdm.pdmName)).append(")")
					.append("</td><td></td><td ></td><td ></td><td ></td><td ></td><td ></td><td ></td><td>")
					.append(getLastVectorValue(vSql)).append("</td></tr>");

			break;
		case 2:
			LOGGER.info("2、PDM不存在，比对库存在的表：" + tableDev.getTableName());// 需要人工判断脚本
			sb[1].append("<tr ").append(strClassTypeString)
					.append("><th scope=\"row\"  abbr=\"L2 Cache\" class=\"specalt\">2")
					.append("</th><td>")
					.append("</td><td></td><td ></td><td ></td><td >").append(tableDev.getTableName())
					.append("</td><td ></td><td ></td><td >");
			if (tableDev.getTable_type().equalsIgnoreCase("view"))
			{
				sb[1].append("view");

			} 
			
			strSql=SqlDroptable.getDropSql(tableDev.getTable_type(), dbDomain, tableDev.getTableName());
			addSqlVector(vSql, strSql, 2, tableDev.getTableName());
			sb[1].append("</td><td>").append(strSql).append("</td></tr>");

			break;
		case 3:
			LOGGER.info("3、PDM存在，比对库不存在的字段：" + tablePdm.getTableName()
					+ " | " + pmdColumn.getColumnName());// 需人工判断如何处理
			sb[2].append("<tr").append(strClassTypeString)
					.append("><th scope=\"row\" abbr=\"L2 Cache\" class=\"specalt\">3").append("</th><td>")
					.append(getParentTableName(tableDev.getTableName())).append("<br>(").append(AiFileUtil.getFileName(tablePdm.pdmName))
					.append(")</td><td >").append(pmdColumn.getColumnName()).append("</td><td >")
					.append(pmdColumn.getDataType()).append("</td><td ></td><td >").append(tableDev.getTableName())
					.append("</td><td ></td><td ></td><td ></td><td >");

			strSql = SqlAlterTable.makeAlterAddColumnSql(dbDomain, tableDev, pmdColumn, tablePdm);
			addSqlVector(vSql, strSql, 3, tableDev.getTableName());
			sb[2].append(strSql).append("</td></tr>");
			break;
		case 4:
			LOGGER.info("4、PDM不存在，比对库存在的字段：" + tableDev.getTableName()
					+ " | " + pmdColumn.getColumnName());// 需要人工判断脚本

			sb[3].append("<tr").append(strClassTypeString)
					.append("><th scope=\"row\" abbr=\"L2 Cache\" class=\"specalt\">4").append("</th><td>")
					.append(getParentTableName(tableDev.getTableName())).append("()")
					.append("</td><td ></td><td ></td><td ></td><td >")
					.append(tableDev.getTableName()).append("</td><td >")
					.append(pmdColumn.getColumnName()).append("</td><td >")
					.append(pmdColumn.getDataType()).append("</td><td ></td><td >");
			
			strSql = SqlAlterTable.makeAlterDropColumnSql(dbDomain, tableDev, pmdColumn);
			addSqlVector(vSql, strSql, 4, tableDev.getTableName());
			sb[3].append(strSql).append("</td></tr>");
			break;
		case 5:
			LOGGER.info("5、表和字段都相同，但字段类型不同的内容：" + tableDev.getTableName()
					+ " | " + pmdColumn.getColumnName() + " | "
					+ pmdColumn.getDataType() + "---" + developColumn.getDataType());
			sb[4].append("<tr").append(strClassTypeString)
					.append("><th scope=\"row\" abbr=\"L2 Cache\" class=\"specalt\">5").append("</th><td>")
					.append(getParentTableName(tableDev.getTableName())).append("<br>(").append(AiFileUtil.getFileName(tablePdm.pdmName))
					.append(")</td><td >").append(pmdColumn.getColumnName())
					.append("</td><td >").append(pmdColumn.getDataType()).append("</td><td >")
					.append(showDefaultValue(pmdColumn.getColumnDefault())).append("</td><td>")
					.append(tableDev.getTableName()).append("</td><td ></td><td>")
					.append(developColumn.getDataType()).append("</td><td>")
					.append(showDefaultValue(developColumn.getColumnDefault())).append("</td><td>");
			/*
			strSql = "ALTER TABLE " + dbDomain + "." + tableDev.getTableName() + " MODIFY " + pmdColumn.getColumnName()
					+ " " + pmdColumn.getDataType()
					+ addDefault(pmdColumn) + ";";
					*/
			strSql = SqlAlterTable.makeModifyColumnSql(dbDomain, tableDev, pmdColumn);
			addSqlVector(vSql, strSql, 5, tableDev.getTableName());
			sb[4].append(strSql).append("</td></tr>");
			break;
		case 6:
			LOGGER.info("6、表和字段、字段类型都相同，是否为空不同："
					+ tableDev.getTableName() + " | " + pmdColumn.getColumnName()
					+ " | " + pmdColumn.getNullable() + "---" + developColumn.getNullable());
			sb[5].append("<tr").append(strClassTypeString)
					.append("><th scope=\"row\" abbr=\"L2 Cache\" class=\"specalt\">6").append("</th><td>")
					.append(getParentTableName(tableDev.getTableName())).append("<br>(").append(AiFileUtil.getFileName(tablePdm.pdmName))
					.append(")</td><td >").append(pmdColumn.getColumnName())
					.append("</td><td >").append(pmdColumn.getNullable())
					.append("</td><td >").append(showDefaultValue(pmdColumn.getColumnDefault()))
					.append("</td><td>").append(tableDev.getTableName())
					.append("</td><td >")
					.append("</td><td>").append(developColumn.getNullable())
					.append("</td><td>").append(showDefaultValue(developColumn.getColumnDefault()))
					.append("</td><td>");
			strSql = SqlAlterTable.makeModifyColumnSql(dbDomain, tableDev, pmdColumn);
			addSqlVector(vSql, strSql, 6, tableDev.getTableName());
			sb[5].append(strSql).append("</td></tr>");
			break;
		case 7:
			LOGGER.info("7、表和字段、字段类型都相同，默认值不同："
					+ tableDev.getTableName() + " | " + pmdColumn.getColumnName()
					+ " | " + pmdColumn.getNullable() + "|"+pmdColumn.getColumnDefault()+"---" + developColumn.getNullable()+" "+developColumn.getColumnDefault());
			sb[6].append("<tr").append(strClassTypeString)
					.append("><th scope=\"row\" abbr=\"L2 Cache\" class=\"specalt\">7").append("</th><td>")
					.append(dbDomain + "." +getParentTableName(tableDev.getTableName())).append("<br>(").append(AiFileUtil.getFileName(tablePdm.pdmName))
					.append(")</td><td >").append(pmdColumn.getColumnName())
					.append("</td><td >").append(pmdColumn.getNullable())
					.append("</td><td >").append(pmdColumn.getColumnDefault())
					.append("</td><td >").append(tableDev.getTableName())
					.append("</td><td ></td><td>")
					.append(developColumn.getNullable()).append("</td><td >")
					.append(developColumn.getColumnDefault()).append("</td><td>");
			strSql = SqlAlterTable.makeModifyColumnSql(dbDomain, tableDev, pmdColumn);
			addSqlVector(vSql, strSql, 7, tableDev.getTableName());
			sb[6].append(strSql).append("</td></tr>");
			break;
		case 8:
			LOGGER.info("8、表和字段、字段类型都相同，默认值不同："
					+ tableDev.getTableName() + " | " + pmdColumn.getColumnName()
					+ " | " + pmdColumn.getNullable() + "---" + developColumn.getNullable());
			sb[7].append("<tr").append(strClassTypeString)
					.append("><th scope=\"row\" abbr=\"L2 Cache\" class=\"specalt\">8").append("</th><td>")
					.append(getParentTableName(tableDev.getTableName())).append("<br>" ).append("(").append(AiFileUtil.getFileName(tablePdm.pdmName))
					.append(")</td><td >").append(pmdColumn.getColumnName())
					.append("</td><td >").append(pmdColumn.getNullable())
					.append("</td><td >").append(pmdColumn.getExtra())
					.append("</td><td >").append(tableDev.getTableName())
					.append("</td><td ></td><td>")
					.append(developColumn.getNullable()).append("</td><td >")
					.append(developColumn.getExtra()).append("</td><td>");
			strSql = SqlAlterTable.makeModifyColumnSql(dbDomain, tableDev, pmdColumn);
			addSqlVector(vSql, strSql, 8, tableDev.getTableName());
			sb[7].append(strSql).append("</td></tr>");
			break;
		}

	}

	/**
	 * 
	 * @category 
	 * @author 吴平福 
	 * @param strInput
	 * @return
	 * update 2016年5月3日
	 */
	public static String showDefaultValue(String strInput)
	{
		if (strInput == null)
			return "";
		//ADD BY WUPF FOR ORACLE
		strInput=strInput.replaceAll("'", "");
		return strInput;
	}

}
