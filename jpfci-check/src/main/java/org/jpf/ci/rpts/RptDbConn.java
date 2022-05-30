/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2015年5月22日 下午12:00:58 
* 类说明 
*/

package org.jpf.ci.rpts;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 */
public class RptDbConn
{
	private static final Logger logger = LogManager.getLogger();
	public static RptDbConn cRptDbConn = new RptDbConn();

	public static RptDbConn GetInstance()
	{
		return cRptDbConn;
	}

	
	public Connection GetConn(String strCfg)
	{
		Connection conn = null;

		try
		{

			String driver = "com.mysql.jdbc.Driver";
			logger.info("DB URL:" + RptDevConst.getDBUrl(strCfg));
			Class.forName(driver).newInstance();

			conn = DriverManager.getConnection(RptDevConst.getDBUrl(strCfg),
					RptDevConst.getStrDbUsr(strCfg), RptDevConst.getStrDbPwd(strCfg));

		} catch (Exception ex)
		{

			ex.printStackTrace();

			return null;

		}
		return conn;
	}
}
