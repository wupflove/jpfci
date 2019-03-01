/** 
* @author 吴平福
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2015年2月13日 下午3:50:47 
* 类说明
*/ 


package org.jpf.ci.dbs.compare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 */
public class DbDescInfo
{
	private static final Logger LOGGER = LogManager.getLogger();
	private String Url;
	private String Usr;
	private String Pwd;
	/**
	 * 
	 */
	public DbDescInfo(String Url,String Usr,String Pwd)
	{
		// TODO Auto-generated constructor stub
		if (Url.startsWith("jdbc:"))
		{
			this.Url=Url;
		}else
		{	
			this.Url="jdbc:mysql://"+Url+"/";
		}
		this.Usr=Usr;
		this.Pwd=Pwd;
	}
	public Connection getConn() throws Exception
	{
		LOGGER.debug(this.Url);
		
		Pattern JDBC_P = Pattern.compile("(jdbc:mysql://).*");
		String driver = "com.mysql.jdbc.Driver";
	    if(!JDBC_P.matcher(this.Url).matches())
	    {
	    	driver="oracle.jdbc.OracleDriver";
	     }
		
		//String driver = "com.p6spy.engine.spy.P6SpyDriver";
		Class.forName(driver).newInstance();
		return DriverManager.getConnection(Url, Usr, Pwd);
	}
	public String getUrlStr()
	{
		return Url;
	}
}
