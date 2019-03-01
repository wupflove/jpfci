/**
 * @author 吴平福 E-mail:wupf@asiainfo-linkage.com
 * @version 创建时间：2013年12月10日 下午3:32:01 类说明
 */

package org.jpf.ci.dbs.dbchange;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.AiDateTimeUtil;
import org.jpf.utils.dbsql.AiDBUtil;
import org.jpf.utils.dbsql.AiSqlStringUtil;
import org.jpf.utils.ios.AiFileUtil;
import org.jpf.utils.scms.SVNUtil;

/**
 * 
 */
public class DbChange {
  private static final Logger logger = LogManager.getLogger(DbChange.class);


  // 下次操作时间
  private String strNextDateTime = "";

  // 当前DDL文件名称
  private String strCurrentFileName = "";

  StringBuffer sbBuffer = new StringBuffer();
  // private int iDoChange = 0;

  // 删除的分表保存
  private Vector<TableInfo> vSubTableName = new Vector<TableInfo>();

  private boolean bWarning = false;

  // SQL文件列表
  Vector<String> g_FileVector = new Vector<String>();

  // 存放产生的SQL
  private StringBuffer sbSql = new StringBuffer();

  // 保存所有数据库信息
  DbInfos cDbInfos = new DbInfos();

  private void addTableInfo(String strDbName, String strParentTable, String strSubTable) {
    TableInfo cTableInfo = new TableInfo(strDbName, strParentTable, strSubTable);
    vSubTableName.add(cTableInfo);
  }

  public static void main(String[] args) {
    try {
      if (1 == args.length) {
        DbChange cDbChange = new DbChange(args[0]);
      } else {
        logger.error("ERROR INPUT PARAMETER");
      }
      logger.info("db change exec finish");
    } catch (Exception ex) {
      // TODO: handle exception
      ex.printStackTrace();
    }

  }

  /**
   * 
   * @category @author 吴平福
   * @param strSql
   * @param _strParentTableName
   * @throws Exception update 2016年9月23日
   */
  private void ExecSubTable(String strSql, String _strParentTableName) throws Exception {
    Connection conn = null;

    try {
      strSql = AiSqlStringUtil.TrimSql(strSql);

      String tmpTableName = _strParentTableName.replaceAll("`", "");
      String strDbName = tmpTableName.split("\\.")[0].toLowerCase();
      String strTableName = tmpTableName.split("\\.")[1];
      DbInfo cDbInfo = cDbInfos.getDbInfo(strDbName);
      if (cDbInfo == null) {
        throw new Exception("error db config:" + strDbName);
      }
      conn = DbChangeConn.GetInstance().GetConn(cDbInfo);

      conn.setAutoCommit(false);

      sbBuffer.append(strSql).append("<br><br>");

      ExecSql(strSql);

      // insert delete 不执行分表
      if (!strSql.trim().toLowerCase().startsWith("insert")
          || !strSql.trim().toLowerCase().startsWith("delete")) {
        boolean bIsParentTable = false;
        String strFindSubSql =
            "SELECT * FROM zd.sys_partition_rule t1,information_schema.TABLES t2 WHERE t1.base_name=t2.TABLE_NAME AND t2.table_schema='"
                + strDbName + "' and  t1.base_name='" + _strParentTableName + "'";
        strFindSubSql =
            "SELECT * FROM zd.sys_partition_rule t1 WHERE t1.base_name='" + strTableName + "'";

        // logger.info(strFindSubSql);
        java.sql.Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(strFindSubSql);
        if (rs.next()) {
          bIsParentTable = true;
        }
        rs.close();
        strFindSubSql = "SELECT  * FROM information_schema.TABLES WHERE table_schema='" + strDbName
            + "' and table_name REGEXP '^" + strTableName.toLowerCase() + "_[0-9]'";
        logger.debug(strFindSubSql);
        rs = stmt.executeQuery(strFindSubSql);

        boolean bSubTable = false;
        while (rs.next()) {
          if (!bSubTable) {
            sbBuffer.append("自动分析分表执行的SQL<br>");
          }
          bSubTable = true;
          String strNewTablName = rs.getString("TABLE_NAME").trim();
          // vSubTableName.add(strNewTablName);
          if (strSql.toLowerCase().startsWith("drop")) {
            addTableInfo(strDbName, _strParentTableName, strNewTablName);
          }

          String strNewSql = strSql.replaceAll(strTableName, strNewTablName);
          // add rename
          String[] strKeys = strSql.split(" ");
          if (strKeys.length > 3 && strKeys[3].equalsIgnoreCase("rename")) {
            strNewTablName = strNewTablName.toLowerCase().trim();
            strTableName = strTableName.toLowerCase().trim();
            int i = strNewTablName.indexOf(strTableName) + strTableName.length();
            String tmpStr = strNewTablName.substring(i, strNewTablName.length());
            if (strKeys[4].trim().endsWith(";")) {
              tmpStr =
                  strKeys[4].trim().subSequence(0, strKeys[4].trim().length() - 1) + tmpStr + ";";
            } else {
              tmpStr = strKeys[4].trim() + tmpStr;
            }
            strNewSql = strNewSql.replaceAll(strKeys[4].trim(), tmpStr);
            logger.info("sub table sql:=" + strNewSql);
          }

          logger.info("sub table sql:=" + strNewSql);

          sbBuffer.append(strNewSql).append("<br>");
          ExecSql(strNewSql);
        }
        if (bIsParentTable != bSubTable) {
          logger.warn("waring:应该有分表，但没有找到" + strTableName);
        }
        DoSubTable(strSql, conn);
      }
      // conn.commit();
    } catch (SQLException ex) {
      // TODO: handle exception
      ex.printStackTrace();
      if (conn != null) {
        conn.rollback();
      }
      throw ex;
    } catch (Exception ex) {
      // TODO: handle exception
      ex.printStackTrace();
      throw ex;
    } finally {
      AiDBUtil.doClear(conn);
    }

  }

  /**
   * 
   * @category 根据SQL获取数据库+表名称
   * @author 吴平福
   * @param strSql
   * @return update 2016年9月23日
   */
  private String getFullTableNameFromSql(String strSql) {
    logger.debug("Inputsql:={}", strSql);

    strSql = AiSqlStringUtil.RemoveSqlNote(strSql);

    String[] strCols = strSql.trim().split(" +");

    String strParentTableName = "";

    if (strCols[2].toLowerCase().trim().equalsIgnoreCase("if")) {
      if (strCols[0].toLowerCase().trim().equalsIgnoreCase("create")) {
        strParentTableName = strCols[5];
      } else {
        strParentTableName = strCols[4];
      }

    } else {
      strParentTableName = strCols[2];
    }
    // 检查是否没有空格，造成连续
    if (strParentTableName.indexOf("(") > 0) {
      strParentTableName = strParentTableName.substring(0, strParentTableName.indexOf("("));
    }
    if (strParentTableName.indexOf(";") > 0) {
      strParentTableName = strParentTableName.substring(0, strParentTableName.indexOf(";"));
    }
    strParentTableName = strParentTableName.replaceAll("`", "");
    return strParentTableName;

  }

  /**
   * 
   * @category 根据SQL获取数据库名称
   * @author 吴平福
   * @param strSql
   * @return update 2016年9月23日
   */
  private String getDbNameFromSql(String strSql) {

    String strParentTableName = getFullTableNameFromSql(strSql);
    String strDbName = strParentTableName.split("\\.")[0].toLowerCase();
    return strDbName;
  }

  /**
   * 
   * @category 根据SQL获取数据库名称
   * @author 吴平福
   * @param strSql
   * @return update 2016年9月23日
   */
  private String getTableNameWithoutDbFromSql(String strSql) {

    String strParentTableName = getFullTableNameFromSql(strSql);
    String strDbName = strParentTableName.split("\\.")[1].toLowerCase();
    return strDbName;
  }

  /**
   * @todo:执行SQL
   * @param conn
   * @param strSql
   * @throws Exception 被测试类名：TODO 被测试接口名:TODO 测试场景：TODO 前置参数：TODO 入参： 校验值： 测试备注： update 2014年3月30日
   */
  private void ExecSql(String strSql) throws Exception {
    Connection conn = null;
    try {

      String strDbName = getDbNameFromSql(strSql);
      DbInfo cDbInfo = cDbInfos.getDbInfo(strDbName);
      if (cDbInfo != null) {
        if (cDbInfo.iDoExecSql == 1) {
          logger.info("exec sql:={}", strSql);
          conn = DbChangeConn.GetInstance().GetConn(cDbInfo);
          java.sql.Statement stmt = conn.createStatement();
          stmt.executeUpdate(StringEscapeUtils.escapeJava(strSql));
          // stmt.executeUpdate(strSql);
          if (conn.getAutoCommit() == false) {
            conn.commit();
          }
        }
      } else {
        throw new Exception("erro db config:" + strDbName);
      }
      sbSql.append(strSql).append("\n");

    } catch (Exception ex) {
      // TODO: handle exception
      if (ex instanceof SQLException) {
        SQLException exSqlException = (SQLException) ex;
        logger.error("ex.getErrorCode()=" + exSqlException.getErrorCode());
        // 字段已经存在。
        if (exSqlException.getErrorCode() == 1060 || 1091 == exSqlException.getErrorCode()) {
          logger.info(exSqlException.getMessage());
        } else {
          ex.printStackTrace();
          // throw ex;
        }
      } else {
        ex.printStackTrace();
      }

    } finally {
      AiDBUtil.doClear(conn);
    }
    // ExecSqlMutiDb(strSql);
  }


  /**
   * @todo:检查是否有删除的分表需要新建
   * 
   *                      被测试类名：TODO 被测试接口名:TODO 测试场景：TODO 前置参数：TODO 入参： 校验值： 测试备注： update
   *                      2014年3月30日
   */
  private void DoSubTable(String strSql, Connection conn) throws Exception {
    if (strSql.toLowerCase().startsWith("create")) {
      for (int i = 0; i < vSubTableName.size(); i++) {
        TableInfo cTableInfo = vSubTableName.get(i);
        String strNewSql = "";
        String strRegex = cTableInfo.strDbName + "." + cTableInfo.strParentTable;

        if (strSql.toLowerCase().indexOf(strRegex) > 0) {
          strNewSql = strSql.replaceAll("(?i)" + strRegex, cTableInfo.strSubTable);
        }

        strRegex = "`" + cTableInfo.strDbName + "`." + cTableInfo.strParentTable;
        if (strSql.toLowerCase().indexOf(strRegex) > 0) {
          strNewSql = strSql.replaceAll("(?i)" + strRegex, cTableInfo.strSubTable);
        }

        strRegex = cTableInfo.strDbName + ".`" + cTableInfo.strParentTable + "`";
        if (strSql.toLowerCase().indexOf(strRegex) > 0) {
          strNewSql = strSql.replaceAll("(?i)" + strRegex, cTableInfo.strSubTable);
        }

        strRegex = "`" + cTableInfo.strDbName + "`.`" + cTableInfo.strParentTable + "`";
        if (strSql.toLowerCase().indexOf(strRegex) > 0) {
          strNewSql = strSql.replaceAll("(?i)" + strRegex, cTableInfo.strSubTable);
        }

        if (strNewSql.length() > 0) {
          logger.info("sub table sql:=" + strNewSql);
          sbBuffer.append(strNewSql).append("<br>");
          ExecSql(strNewSql);
          strNewSql = "";
        }
      }
    }
  }

  /**
   * 
   * @category @author 吴平福
   * @param strSql
   * @param iRowNum
   * @throws Exception update 2016年9月23日
   */
  private void GetTableName(String strSql, int iRowNum) throws Exception {

    String strParentTableName = getFullTableNameFromSql(strSql);

    if (strParentTableName.length() > 0) {
      DbChangeCheck.CheckTableName(strParentTableName);
      ExecSubTable(strSql, strParentTableName);

      // align table
      DbInfo cDbInfo = cDbInfos.getDbInfo(getDbNameFromSql(strSql));
      if (cDbInfo != null) {
        for (Iterator iter_table = cDbInfo.parent_child.keySet().iterator(); iter_table
            .hasNext();) {
          String key_table = (String) iter_table.next();
          String strAlignParentTableName =
              cDbInfo.domain + "." + cDbInfo.parent_child.get(key_table);

          if (strAlignParentTableName.trim().toLowerCase()
              .equalsIgnoreCase(strParentTableName.trim().toLowerCase())) {
            logger.info(strSql.replaceAll(strParentTableName, cDbInfo.domain + "." + key_table));

            ExecSubTable(strSql.replaceAll(strParentTableName, cDbInfo.domain + "." + key_table),
                cDbInfo.domain + "." + key_table);
          }
        }
      }

    }

  }

  /**
   * @category 构造函数
   * @param strFilePath
   * @throws Exception
   */
  public DbChange(String strFilePath) throws Exception {
    DoChange(strFilePath);
  }

  /**
   * @todo:获取文件列表
   * @param g_FileVector
   * @param strFilePath
   * @throws Exception update 2014年3月30日
   */
  private void getFiles(String strFilePath) throws Exception {
    // 获取文件列表，分域
    g_FileVector.clear();
    File f = new File(strFilePath);
    if (!f.exists()) {
      throw new Exception("目标文件夹不存在");
    }
    AiFileUtil.getFiles(strFilePath, g_FileVector);
    logger.info("g_FileVector.size()=" + g_FileVector.size());
    for (int i = g_FileVector.size() - 1; i >= 0; i--) {
      // logger.info("i=" + i);
      File file = new File(g_FileVector.get(i));
      String tmpFileName = file.getAbsolutePath();
      // logger.info(tmpFileName);

      if (tmpFileName.indexOf(".svn") >= 0) {
        // logger.info(tmpFileName);
        g_FileVector.remove(i);
        continue;
      }
      if (!tmpFileName.endsWith(".sql")) {
        // logger.info(tmpFileName);
        g_FileVector.remove(i);
        continue;
      }

    }

    DbFileCompare ct = new DbFileCompare();
    Collections.sort(g_FileVector, ct);
    // g_FileVector.clear();
  }

  /**
   * 
   * @category @author 吴平福
   * @param strFilePath
   * @throws Exception update 2016年9月23日
   */
  private void DoChange(String strFilePath) throws Exception {

    int iCount = 0;
    try {

      String strEXCLUDE_DATE = DbChangeHandleTime.getLastExcludeTime();

      strNextDateTime = AiDateTimeUtil.getToday("yyyy-MM-dd HH:mm:ss");

      getFiles(strFilePath);

      for (int j = 0; j < g_FileVector.size(); j++) {
        vSubTableName.clear();
        sbSql.setLength(0);
        strCurrentFileName = g_FileVector.get(j);

        iCount++;
        logger.info(iCount + ". find file: " + strCurrentFileName);
        // logger.info("file last time: " + JpfFileUtil.GetFileDate(strCurrentFileName));

        String strFileDateTime = SVNUtil.getSvnFileAuthorDate(strCurrentFileName);
        logger.info("svn file last time: {}", strFileDateTime);
        if (strFileDateTime == null || strFileDateTime.length() == 0) {
          strFileDateTime = AiFileUtil.getFileDate(strCurrentFileName);
        }

        if (1 == AiDateTimeUtil.compare_date(strEXCLUDE_DATE, strFileDateTime)) {
          logger.info("compare time: " + strEXCLUDE_DATE);
          continue;
        }
        String strSql = "";
        int iRowNum = 0;
        bWarning = false;
        try {
          String strText = AiFileUtil.getFileTxt(strCurrentFileName, "UTF-8").trim();
          strText = strText.replaceAll("`", "");

          String[] colsString = strText.split(System.getProperty("line.separator"));
          boolean b = false;
          boolean bCreate = false;
          boolean bDrop = false;
          boolean bAlter = false;
          boolean bInsert = false;
          boolean bDelete = false;
          boolean bCommit = false;

          boolean bIdxCreate = false;
          boolean bIdxDrop = false;
          boolean bIdxAlter = false;

          for (int n = 0; n < colsString.length; n++) {

            colsString[n] = colsString[n].trim().toLowerCase();
            if (colsString[n].startsWith("--")) {
              continue;
            }
            colsString[n] = colsString[n].replaceAll("commit[ ]+;", "");
            colsString[n] = colsString[n].replaceAll("commit;", "");
            if (colsString[n].length() == 0) {
              continue;
            }
            colsString[n] = colsString[n].trim().toLowerCase();

            Pattern pattern = Pattern.compile("^drop[ ]+table.*");
            Matcher matcher = pattern.matcher(colsString[n]);
            bDrop = matcher.matches();

            pattern = Pattern.compile("^alter[ ]+table.*");
            matcher = pattern.matcher(colsString[n]);
            bAlter = matcher.matches();

            pattern = Pattern.compile("^create[ ]+table.*");
            matcher = pattern.matcher(colsString[n]);
            bCreate = matcher.matches();

            pattern = Pattern.compile("^insert[ ]+into.*");
            matcher = pattern.matcher(colsString[n]);
            bInsert = matcher.matches();

            pattern = Pattern.compile("^delete[ ]+from.*");
            matcher = pattern.matcher(colsString[n]);
            bDelete = matcher.matches();

            pattern = Pattern.compile("^drop[ ]+index.*");
            matcher = pattern.matcher(colsString[n]);
            bIdxDrop = matcher.matches();

            pattern = Pattern.compile("^create[ ]+index.*");
            matcher = pattern.matcher(colsString[n]);
            bIdxCreate = matcher.matches();

            pattern = Pattern.compile("^alter[ ]+index.*");
            matcher = pattern.matcher(colsString[n]);
            bIdxAlter = matcher.matches();

            pattern = Pattern.compile("^commit.*");
            matcher = pattern.matcher(colsString[n]);
            bCommit = matcher.matches();

            if (bDrop || bAlter || bCreate || bInsert || bDelete || bIdxCreate || bIdxAlter
                || bIdxDrop) {
              if (strSql.length() > 0) {
                GetTableName(strSql, iRowNum);
              }
              strSql = colsString[n];
              b = true;
              iRowNum = n + 1;
            } else {
              if (!bCommit) {
                if (b) {
                  strSql += " " + colsString[n];
                }
              } else {
                String strMailMsg = "<br>DB变更忽略COMMIT行"
                    + DbChangeMail.makeMailTxt(strCurrentFileName, strNextDateTime) + "<br>忽略行："
                    + iRowNum + 1 + "<br>忽略SQL：" + strText;
                DbChangeMail.SendMail(strCurrentFileName, 2, strMailMsg);
              }
              // b = false;
            }
            if (bInsert || bDelete) {
              bWarning = true;
            }
          }
          if (strSql.length() > 0) {
            GetTableName(strSql, iRowNum);
          }
        } catch (Exception ex) {
          // TODO: handle exception
          ex.printStackTrace();
          String strErrMsg = "";
          if (ex instanceof SQLException) {
            strErrMsg = ((SQLException) ex).getMessage();
            strErrMsg = new String(strErrMsg.getBytes("utf-8"), "gbk");
            strErrMsg = "<br>错误代码：" + ((SQLException) ex).getErrorCode() + ";<br>错误提示：" + strErrMsg;
            // 主键问题
            if (((SQLException) ex).getErrorCode() == 1215) {
              DbFkCheck cDbFkCheck = new DbFkCheck();
              strErrMsg += cDbFkCheck.CheckSqlError(strSql);
            }
          } else {
            strErrMsg = ex.getMessage();
          }
          String strMailMsg = DbChangeMail.makeMailTxt(strCurrentFileName, strNextDateTime)
              + "<br>出错行：" + iRowNum + "<br>出错SQL：" + strSql + "<br><font color='#FF0000'>出错提示："
              + strErrMsg + "</font>";

          DbChangeMail.SendMail(strCurrentFileName, 1, strMailMsg);

          sbBuffer.setLength(0);
        }
        // GetFileAuthor(strCurrentFileName)
        if (sbBuffer.length() > 0) {
          String strMailMsg = DbChangeMail.makeMailTxt(strCurrentFileName, strNextDateTime)
              + "<br>SQL提示：<br>" + sbBuffer.toString();
          if (bWarning) {
            DbChangeMail.SendMail(strCurrentFileName, 2, strMailMsg);
          } else {
            DbChangeMail.SendMail(strCurrentFileName, 0, strMailMsg);

          }
          sbBuffer.setLength(0);
        }
        DbChangeUtil.writeNewSqlFile(sbSql, strCurrentFileName);
      }

      DbChangeHandleTime.writeCurrentExcludeTime(strNextDateTime);

      logger.info("db change exec file Count:=" + iCount);

    } catch (Exception ex) {
      ex.printStackTrace();

    }

  }

  private class DbFileCompare implements Comparator {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
      // TODO Auto-generated method stub
      String e1 = (String) o1;
      String e2 = (String) o2;
      File f1 = new File(e1);
      File f2 = new File(e2);
      if (f1.lastModified() > f2.lastModified())// 这样比较是降序,如果把-1改成1就是升序.
      {
        return 1;
      } else if (f1.lastModified() < f2.lastModified()) {
        return -1;
      } else {
        return 0;
      }
    }
  }

}
