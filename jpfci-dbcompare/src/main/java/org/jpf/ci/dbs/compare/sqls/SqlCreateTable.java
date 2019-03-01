/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2015年5月28日 上午10:41:08 
* 类说明 
*/

package org.jpf.ci.dbs.compare.sqls;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Iterator;

import org.jpf.ci.dbs.compare.AiColumn;
import org.jpf.ci.dbs.compare.CompareInfo;

import org.jpf.utils.dbsql.AiDBUtil;

import org.jpf.ci.dbs.compare.AiTable;


/**
 * 
 */
public class SqlCreateTable {

    /**
     * 
     * @category @author 吴平福
     * @param conn_pdm
     * @param strDomainName
     * @param strTableName
     * @return update 2016年5月3日
     */
    public static String getCreateTableSqlFromDb(Connection conn_pdm, String strDomainName, String strTableName, CompareInfo cCompareInfo) {
        if (cCompareInfo.getDbType().equalsIgnoreCase("oracle"))
        {
            return getCreateTableSqlFromOracleDb(conn_pdm, strDomainName, strTableName);
        }else {
            return getCreateTableSqlFromMysqlDb(conn_pdm, strDomainName, strTableName);
        }
    }
    /**
     * 
     * @category @author 吴平福
     * @param conn_pdm
     * @param strDomainName
     * @param strTableName
     * @return update 2016年5月3日
     */
    public static String getCreateTableSqlFromOracleDb(Connection conn_pdm, String strDomainName, String strTableName) {
        String strSql = "";

        return strSql;
    }
    /**
     * 
     * @category @author 吴平福
     * @param conn_pdm
     * @param strDomainName
     * @param strTableName
     * @return update 2016年5月3日
     */
    public static String getCreateTableSqlFromMysqlDb(Connection conn_pdm, String strDomainName, String strTableName) {
        String strSql = "";
        try {
            if (strTableName.indexOf(".") > 0) {
                strTableName = strTableName.substring(strTableName.indexOf(".") + 1, strTableName.length());
            }
            String mStrSql = "show create table " + strDomainName + "." + strTableName;

            ResultSet rs = AiDBUtil.execSqlQuery(conn_pdm, mStrSql);
            if (rs.next()) {
                strSql = rs.getString("Create Table").toUpperCase() + ";";
                strSql = strSql.replaceFirst("CREATE TABLE `",
                        "CREATE TABLE " + strDomainName.toUpperCase() + "." + "`");
            }
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }
        return strSql;
    }
    /**
     * 
     * @category @author 吴平福
     * @param strPdmDomain
     * @param table_pdm
     * @return update 2016年5月3日
     */
    public static String createTableSqlFromPdm(AiTable table_pdm, CompareInfo cCompareInfo) {
        // PDM数据库连接
        String strPdmDomain = cCompareInfo.getDbDomain().toLowerCase();
        
        StringBuffer msql = new StringBuffer();
        msql.append("create table " + strPdmDomain + "." + table_pdm.getTableName() + "(");
        msql.append("\r\n");
        for (Iterator iter_column = table_pdm.columns.keySet().iterator(); iter_column.hasNext();) {

            String key_column = (String) iter_column.next();
            AiColumn column_pdm = (AiColumn) table_pdm.columns.get(key_column);
            String isnull = column_pdm.getNullable();
            String columnDefault = column_pdm.getColumnDefault();
            if (isnull.equalsIgnoreCase("no")) {
                isnull = "not null";
            } else {
                isnull = "";
            }
            if (columnDefault == null) {
                columnDefault = "";
            }
            if (columnDefault != "") {
                if (isNumberic(column_pdm.getDataType(), columnDefault)) {
                    columnDefault = "default " + columnDefault;
                } else {
                    columnDefault = "default " + '\'' + columnDefault + '\'';
                }
            }

            msql.append(column_pdm.getColumnName() + "  " + column_pdm.getDataType() +
                    " " + column_pdm.getExtra() + "  ");
            
            if (cCompareInfo.getDbType().equalsIgnoreCase("oracle"))
            {
            	msql.append(
                         columnDefault+" "  + isnull+" ,");
            }else
            {
            	msql.append(isnull +
                " " + columnDefault + " ,");
        	
            }
            msql.append("\r\n");
        }
        String msqlt = msql.toString().substring(0, msql.lastIndexOf(","));
        msqlt += "\r\n" + ")";
        if (cCompareInfo.getDbType().equalsIgnoreCase("mysql")){
            msqlt += "ENGINE=InnoDB DEFAULT CHARSET=utf8";
        }
        msqlt+=";";

        return msqlt;
    }

    /**
     * 
     * @category PDM判断一个字符串是否数字
     * @author 吴平福
     * @param Type
     * @param defaultType
     * @return update 2016年5月3日
     */
    public static boolean isNumberic(String Type, String defaultType) {
        String[] types = Type.split("\\(");
        String dataType = types[0];
        dataType = dataType.trim();
        if (dataType.equalsIgnoreCase("numeric") || dataType.equalsIgnoreCase("int")
                || dataType.equalsIgnoreCase("tinyint")
                || dataType.equalsIgnoreCase("smallint") || dataType.equalsIgnoreCase("bigint")
                || dataType.equalsIgnoreCase("decimal") || dataType.equalsIgnoreCase("number")
                || defaultType.equalsIgnoreCase("null")
                || (dataType.equalsIgnoreCase("date") && defaultType != "0000-00-00 00:00:00")
                || (dataType.equalsIgnoreCase("datetime") && defaultType != "0000-00-00 00:00:00")
                || (dataType.equalsIgnoreCase("timestamp") && defaultType != "0000-00-00 00:00:00")
                || (defaultType.equalsIgnoreCase("0"))) {
            return true;
        } else
            return false;
    }
}
