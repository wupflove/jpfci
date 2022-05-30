/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2015年5月2日 下午2:31:28 类说明
 */


package org.jpf.ci.dbs.dbchange;


import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 * 
 */
public class AppConn {
  private static final Logger logger = LogManager.getLogger();
  public static AppConn cAppConn = new AppConn();

  public static AppConn getInstance() {
    return cAppConn;
  }

  public Connection getConn() {
    Connection conn = null;

    try {

      String driver = "com.mysql.jdbc.Driver";

      String URL = "jdbc:mysql://10.10.12.153:3333/sonar";
      // String URL = "jdbc:mysql://10.10.12.153:3333/sonar";
      String dbuser = "sonar";

      String dbpass = "sonar";
      logger.info("DB URL:" + URL);
      Class.forName(driver).newInstance();

      conn = DriverManager.getConnection(URL, dbuser, dbpass);

    } catch (Exception ex) {

      ex.printStackTrace();

      return null;

    }
    return conn;
  }


}

