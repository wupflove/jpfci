/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2017年7月18日 下午4:44:33 类说明
 */

package org.jpf.ci.sonarplugin;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.dbsql.AiDBUtil;
import org.jpf.utils.dbsql.AppConn;

/**
 * 
 */
public class MysqlDumpTables {
  private static final Logger logger = LogManager.getLogger();

  class runImportThread extends Thread {
    private String StrTableName = "";

    public runImportThread(String strTableName) {
      this.StrTableName = strTableName;
    }

    @Override
    public void run() {
      try {
        while (true) {
          String strCmd = "mysqldump sonar --table " + StrTableName
              + " -h10.1.234.174 -u sonar -pqmcsonar2017! -P3306 --add-drop-table | mysql sonar51 -h127.0.0.1 -P4306 -u sonar -pwupflove";
          String[] cmd = new String[] {"csh", "-c", strCmd};
          Process process = Runtime.getRuntime().exec(cmd);
          InputStreamReader ir = new InputStreamReader(process.getInputStream());
          LineNumberReader input = new LineNumberReader(ir);

          String line;
          while ((line = input.readLine()) != null) {
            System.out.println(StrTableName + ":" + line);
          }
          process.waitFor();
          System.out.println(StrTableName + ":" + process.exitValue());
          if (process.exitValue() == 0 || process.exitValue() == 2) {
            break;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();

      }
    }
  }

  /**
   * 
   */
  public MysqlDumpTables() {
    // TODO Auto-generated constructor stub
    Connection conn = null;
    try {
      String strSql =
          "select table_name from information_schema.tables where table_schema='sonar' ";
      conn = AppConn.GetInstance().GetConn("db_sonar174");
      conn.setAutoCommit(false);
      ResultSet rs = AiDBUtil.ExecSqlQuery(conn, strSql);
      int iCount = 0;

      while (rs.next()) {
        new runImportThread(rs.getString("table_name")).start();
        iCount++;
      }

      logger.debug("write into db count:" + iCount);
    } catch (Exception ex) {
      logger.error(ex);
      ex.printStackTrace();
    } finally {
      AiDBUtil.doClear(conn);
    }
  }

  /**
   * @category @author 吴平福
   * @param args update 2017年7月18日
   */

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    MysqlDumpTables cMysqlDumpTables = new MysqlDumpTables();
  }

}
