/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2016年5月3日 下午6:42:03 类说明
 */

package org.jpf.ci.dbs.compare;

import java.sql.Connection;

import org.jpf.ci.dbs.dbchange.AppConn;
import org.jpf.utils.dbsql.AiDBUtil;

/**
 * 
 */
public class SaveCompareResult {
  /**
   * 
   * @category 记录索引比对结果
   * @author 吴平福
   * @param cCompareInfo
   * @param cCompareResult update 2016年5月3日
   */
  protected static void insertIndexResult(CompareInfo cCompareInfo, CompareResult cCompareResult) {
    Connection conn = null;
    try {
      conn = AppConn.getInstance().getConn();
      String strSql = "update dbci set diff11=" + cCompareResult.iCount1 + ",diff12="
          + cCompareResult.iCount2 + ",diff13=" + cCompareResult.iCount3 + ",diff14="
          + cCompareResult.iCount4 + ",diff15=" + cCompareResult.iCount5 + " where dbinfo='"
          + cCompareInfo.getDevJdbcUrl() + "/" + cCompareInfo.getDbDomain()
          + "' and diffdate=current_date";
      // LOGGER.info(strSql);
      AiDBUtil.execUpdateSql(conn, strSql);
    } catch (Exception ex) {
      // TODO: handle exception
      ex.printStackTrace();
    } finally {
      AiDBUtil.doClear(conn);
    }
  }

  /**
   * 
   * @category 记录表比对结果
   * @author 吴平福
   * @param cCompareInfo
   * @param cCompareResult update 2016年5月3日
   */
  protected static void insertTableResult(CompareInfo cCompareInfo, CompareResult cCompareResult) {
    Connection conn = null;
    try {
      conn = AppConn.getInstance().getConn();
      String strSql = "delete from dbci where dbinfo='" + cCompareInfo.getDevJdbcUrl()
          + "' and diffdate=current_date";
      // logger.info(strSql);
      AiDBUtil.execUpdateSql(conn, strSql);
      strSql =
          "insert into dbci(dbmail,dbinfo,diffdate,diff1,diff2,diff3,diff4,diff5,diff6,diff7,diff8,diff9,diff10,referdbinfo) values( '"
              + cCompareInfo.getStrMails() + "','" + cCompareInfo.getDevJdbcUrl() + "/"
              + cCompareInfo.getDbDomain() + "',CURRENT_DATE," + cCompareResult.iCount1 + ","
              + cCompareResult.iCount2 + "," + cCompareResult.iCount3 + "," + cCompareResult.iCount4
              + "," + cCompareResult.iCount5 + "," + cCompareResult.iCount6 + ","
              + cCompareResult.iCount7 + "," + cCompareResult.iCount8 + "," + cCompareResult.iCount9
              + "," + cCompareResult.iCount10 + ",'" + cCompareInfo.getPdmJdbcUrl() + "')";
      // logger.info(strSql);
      AiDBUtil.execUpdateSql(conn, strSql);
    } catch (Exception ex) {
      // TODO: handle exception
      ex.printStackTrace();
    } finally {
      AiDBUtil.doClear(conn);
    }
  }
}
