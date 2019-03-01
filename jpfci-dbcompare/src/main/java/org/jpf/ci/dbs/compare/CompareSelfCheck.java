/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2015年5月29日 下午7:00:58 
* 类说明 
*/ 

package org.jpf.ci.dbs.compare;

import java.sql.Connection;

import org.jpf.utils.dbsql.AiDBUtil;

/**
 * 
 */
public class CompareSelfCheck extends abstractSelfCheck
{

	/**
	 * @todo: 检查数据库是否存在并创建
	 */
	public static int checkSaveSqlDb(Connection conn_develop)
	{
		try
		{
			String strSql = "CREATE DATABASE IF NOT EXISTS dd default charset utf8 COLLATE utf8_general_ci;";
			AiDBUtil.execUpdateSql(conn_develop, strSql);
			strSql = "CREATE TABLE if not exists dd.dbchangehis (table_name  VARCHAR(128), exectype smallint,domain VARCHAR(45), changesql text NOT NULL,state_date DATETIME NOT NULL,reversesql VARCHAR(45),execmsg VARCHAR(512));";
			AiDBUtil.execUpdateSql(conn_develop, strSql);
			
		} catch (Exception ex)
		{
			// TODO: handle exception
			ex.printStackTrace();
			return -1;
		}
		return 0;
	}
	

	@Override
	public int checkDb() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int checkDb(Connection conn) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
}
