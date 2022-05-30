/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2015年4月7日 下午10:45:47 类说明
 */

package org.jpf.ci.dbs.compare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.dbsql.AiDBUtil;
import org.jpf.utils.ios.AiFileUtil;
import org.jpf.utils.xmls.JpfXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 */
public class LoadSqlToDB {
  private static final Logger LOGGER = LogManager.getLogger();
  private final String DBINFOFILE = "importdbinfo.xml";

  /**
   * 
   */
  public LoadSqlToDB(String strSqlFilePath) {
    // TODO Auto-generated constructor stub
    Connection conn = null;
    try {
      DbDescInfo cDbDescInfo = null;
      AiFileUtil.checkFile(DBINFOFILE);
      NodeList nl = JpfXmlUtil.getNodeList("dbsource", DBINFOFILE);
      LOGGER.debug(nl.getLength());
      if (1 == nl.getLength()) {
        Element el = (Element) nl.item(0);
        String strJdbcUrl = JpfXmlUtil.getParStrValue(el, "dburl");
        String strDbUsr = JpfXmlUtil.getParStrValue(el, "dbusr");
        String strDbPwd = JpfXmlUtil.getParStrValue(el, "dbpwd");
        LOGGER.debug(strJdbcUrl);
        LOGGER.debug(strDbUsr);
        LOGGER.debug(strDbPwd);
        cDbDescInfo = new DbDescInfo(strJdbcUrl, strDbUsr, strDbPwd);
      } else {
        LOGGER.error("error source db info");
      }
      conn = cDbDescInfo.getConn();
      Vector<String> v_File = new Vector<String>();
      AiFileUtil.getFiles(strSqlFilePath, v_File);
      for (String strSqlFile : v_File) {
        readSQLFile(conn, strSqlFile);
      }
    } catch (Exception ex) {
      // TODO: handle exception
      ex.printStackTrace();
    } finally {
      AiDBUtil.doClear(conn);
    }
  }

  /**
   * 
   * @category @author 吴平福
   * @param args update 2016年5月5日
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    if (1 == args.length) {
      LoadSqlToDB cLoadSqlToDB = new LoadSqlToDB(args[0]);
    }
  }

  /**
   * 
   * @category @author 吴平福
   * @param conn
   * @param strSqlFileName update 2016年5月5日
   */
  public void readSQLFile(Connection conn, String strSqlFileName) {
    BufferedReader bufferedReader = null;
    try {
      File f = new File(strSqlFileName);

      bufferedReader = new BufferedReader(new FileReader(f));
      StringBuilder sBuilder = new StringBuilder("");
      String str = bufferedReader.readLine();
      while (str != null) {
        // 去掉一些注释，和一些没用的字符
        if (!str.startsWith("#") && !str.startsWith("/*") && !str.startsWith("–")
            && !str.startsWith("\n"))
          sBuilder.append(str);
        str = bufferedReader.readLine();
      }
      String[] strArr = sBuilder.toString().split(";");

      for (String strSql : strArr) {

        LOGGER.debug(strSql);
        AiDBUtil.execUpdateSql(conn, strSql);
      }
      // 创建数据连接对象，下面的DBConnection是我的一个JDBC类

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        bufferedReader.close();

      } catch (Exception e) {
      }
    }

  }


}
