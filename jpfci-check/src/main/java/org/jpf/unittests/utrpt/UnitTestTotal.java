/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2016年5月16日 上午9:42:56 类说明
 */

package org.jpf.unittests.utrpt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.dbsql.AiDBUtil;
import org.jpf.utils.dbsql.AppConn;
import org.jpf.utils.ios.AiFileUtil;

/**
 * 
 */
public class UnitTestTotal {
  private static final Logger logger = LogManager.getLogger();

  public UnitTestTotal(String strFilePath) {
    try {
      File f1 = new File(strFilePath);
      if (f1.isDirectory()) {
        File[] f2 = f1.listFiles();

        for (int i = 0; i < f2.length; i++) {
          if (f2[i].isDirectory() && f2[i].getPath().endsWith("_sonar")) {
            logger.info(f2[i].getPath());
            logger.info(AiFileUtil.getFileName(f2[i].getPath()));
            String strNumber = AiFileUtil.getFileTxt(f2[i].getPath() + "/nextBuildNumber");
            int j = Integer.parseInt(strNumber.replace("\"", "").trim());
            // logger.info(AiFileUtil.getFileTxt(f2[i].getPath()+"/builds/"+(j-1)+"/log"));
            doWork(AiFileUtil.getFileName(f2[i].getPath()),
                f2[i].getPath() + "/builds/" + (j - 1) + "/log", j - 1);
          }
        }

      }
    } catch (Exception ex) {
      // TODO: handle exception
      ex.printStackTrace();
    }

  }

  /**
   * 
   */
  public UnitTestTotal(String strJobName, String strFileName, String strNumber) {
    doWork(strJobName, strFileName, Integer.parseInt(strFileName));
  }

  /**
   * @category @author 吴平福
   * @param strJobName
   * @param strFileName update 2016年5月16日
   */
  private void doWork(String strJobName, String strFileName, int iNumber) {
    // TODO Auto-generated constructor stub
    Connection conn = null;
    try {
      conn = AppConn.GetInstance().GetConn("utrpt_dbsource");

      File f = new File(strFileName);
      InputStreamReader read = new InputStreamReader(new FileInputStream(f), "UTF-8");

      BufferedReader reader = new BufferedReader(read);

      String line;
      String strRunningName = "";
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.startsWith("Tests run:") && line.endsWith("sec")) {
          System.out.println(line);
          insertDb(conn, strJobName, iNumber, strRunningName, line);
        }

        if (line.startsWith("Running ")) {
          strRunningName = line;
        }

      }
      reader.close();
      read.close();

      // Tests run: 26, Failures: 0, Errors: 0, Skipped: 0, Time
      // elapsed: 0.017 sec
      // System.out.println(colsString[n]);



    } catch (Exception ex) {
      ex.printStackTrace();
      logger.error(ex);
    } finally {
      AiDBUtil.doClear(conn);
    }

  }

  private void insertDb(Connection conn, String strJobName, int iNumber, String str1, String str2) {

    try {
      UnitTestInfo cUnitTestInfo = new UnitTestInfo(str1, str2);
      // DROP TABLE IF EXISTS `visualwall`.`unittesttotal`;
      String strSql =
          "insert into unittesttotal(jobname,testname,tests,time_elapsed,failures,errors,skipped,build_number) values("
              + "'" + strJobName + "'" + ",'" + cUnitTestInfo.getTestName() + "'" + ","
              + cUnitTestInfo.getTests() + "," + cUnitTestInfo.getTimeElapsed() + ","
              + cUnitTestInfo.getFailures() + "," + cUnitTestInfo.getErrors() + ","
              + cUnitTestInfo.getSkips() + "," + iNumber + ")";
      logger.debug(strSql);
      AiDBUtil.execUpdateSql(conn, strSql);
    } catch (Exception ex) {
      // TODO: handle exception
    }
  }

  /**
   * 
   * @category @author 吴平福
   * @param args update 2016年5月16日
   */
  public static void main(String[] args) {
    if (args.length == 3) {
      UnitTestTotal cUnitTestTotal = new UnitTestTotal(args[0], args[1], args[2]);
    }
    if (args.length == 1) {
      UnitTestTotal cUnitTestTotal = new UnitTestTotal(args[0]);
    }
    logger.info("game over");
  }
}
