/** 
 * @author 吴平福 
 * E-mail:wupf@asiainfo.com 
 * @version 创建时间：2015年2月8日 下午11:07:00 
 * 类说明 
 */

package org.jpf.ci.dbs;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 */
public class MutiDbConn
{
	private static final Logger logger = LogManager.getLogger();
	public static MutiDbConn cMutiDbConn = new MutiDbConn();

	public static MutiDbConn GetInstance()
	{
		return cMutiDbConn;
	}
	public Connection GetSourceConn(String strDb)throws Exception
	{
		String driver = "com.mysql.jdbc.Driver";
		Class.forName(driver).newInstance();
		//10.10.13.90:6511  boss/boss6511
		String URL="jdbc:mysql://10.10.12.150:4306/"+strDb;
		String dbuser = "billing";
		String dbpass = "billing";
		return  DriverManager.getConnection(URL, dbuser, dbpass);
	}
	
	public Connection GetDestConn(String strDb)throws Exception
	{
		String driver = "com.mysql.jdbc.Driver";
		Class.forName(driver).newInstance();
		//10.10.13.90:6511  boss/boss6511
		String URL="jdbc:mysql://10.10.12.153:4306/"+strDb;
		String dbuser = "billing";
		String dbpass = "billing";
		return  DriverManager.getConnection(URL, dbuser, dbpass);
	}
	

}
