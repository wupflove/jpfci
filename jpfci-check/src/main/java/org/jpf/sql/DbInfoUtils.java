/** 
 * @author 吴平福 
 * E-mail:wupf@asiainfo.com 
 * @version 创建时间：2015年2月8日 下午11:07:00 
 * 类说明 
 */
package org.jpf.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.jpf.utils.dbsql.AiDBUtil;


public final class DbInfoUtils {

	public static String GetTablePK(Connection conn,String TableName)throws Exception
	{
		StringBuffer sb=new StringBuffer();
		String strSql = "select * from user_cons_columns where constraint_name=(select constraint_name from user_constraints where table_name=upper('"+TableName+"') and constraint_type   ='P')";   
		
		ResultSet rs = AiDBUtil.ExecSqlQuery(conn, strSql);
		while (rs.next())
		{
			sb.append(rs.getString("COLUMN_NAME")).append(",");
		}
		rs.close();
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static  String GetTableColumn(Connection conn,String TableName) throws Exception
	{
		StringBuffer sb = new StringBuffer();

			String strSql = "select * from " + TableName;
			
			ResultSet rs = AiDBUtil.ExecSqlQuery(conn, strSql);
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++)
			{
				sb.append(rsmd.getColumnName(i)).append(",");
			}
			rs.close();

		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}	
}
