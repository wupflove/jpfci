/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2015年5月6日 下午2:29:52 类说明
 */

package org.jpf.ci.dbs.compare.dbchecks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.ci.dbs.compare.DbDescInfo;
import org.jpf.utils.AiDateTimeUtil;
import org.jpf.utils.dbsql.AiDBUtil;
import org.jpf.utils.ios.AiFileUtil;
import org.jpf.utils.mails.AiMail;
import org.jpf.utils.xmls.JpfXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 */
public class AiDbChecks {
  private static final Logger LOGGER = LogManager.getLogger();


  private void doCheck(DbDescInfo cDbDescInfo, String strDomain, StringBuffer sbTotal,
      StringBuffer sbDetail) {
    Connection conn = null;
    try {
      long lProduce = 0;
      long lTriger = 0;
      long lView = 0;
      long lAutoIncrement = 0;
      conn = cDbDescInfo.getConn();
      String strSql =
          "select trigger_schema,trigger_name from INFORMATION_SCHEMA.TRIGGERS  Where trigger_schema=?";
      PreparedStatement pstmt = conn.prepareStatement(strSql);
      pstmt.setString(1, strDomain);
      LOGGER.info(strSql);
      ResultSet rSet = pstmt.executeQuery();
      while (rSet.next()) {
        lTriger++;
        sbDetail.append("<tr><td>").append(strDomain).append("</td>").append("<td>触发器</td>")
            .append("<td>").append(rSet.getString("trigger_name")).append("</td>")
            .append("<td>drop trigger ").append(rSet.getString("trigger_name"))
            .append(";</td></tr>");
      }


      strSql =
          "select routine_schema,routine_name from INFORMATION_SCHEMA.Routines  Where routine_schema=?";
      pstmt = conn.prepareStatement(strSql);
      pstmt.setString(1, strDomain);
      LOGGER.info(strSql);
      rSet = pstmt.executeQuery();
      while (rSet.next()) {
        lProduce++;
        sbDetail.append("<tr><td>").append(strDomain).append("</td>").append("<td>存贮过程</td>")
            .append("<td>").append(rSet.getString("routine_name")).append("</td>")
            .append("<td>drop procedure ").append(rSet.getString("routine_name"))
            .append(";</td></tr>");
      }

      strSql =
          "select table_schema,table_name from INFORMATION_SCHEMA.tables  Where table_type='view' and table_schema=?";
      pstmt = conn.prepareStatement(strSql);
      pstmt.setString(1, strDomain);
      LOGGER.info(strSql);
      rSet = pstmt.executeQuery();
      while (rSet.next()) {
        lView++;
        sbDetail.append("<tr><td>").append(strDomain).append("</td>").append("<td>视图</td>")
            .append("<td>").append(rSet.getString("table_name")).append("</td>")
            .append("<td>drop view ").append(rSet.getString("table_name")).append(";</td></tr>");
      }

      strSql =
          "select table_schema,table_name,column_name from INFORMATION_SCHEMA.COLUMNS  Where extra='auto_increment' and table_schema=?";
      pstmt = conn.prepareStatement(strSql);
      pstmt.setString(1, strDomain);
      LOGGER.info(strSql);
      rSet = pstmt.executeQuery();
      while (rSet.next()) {
        lAutoIncrement++;
        sbDetail.append("<tr><td>").append(strDomain).append("</td>").append("<td>自增长字段</td>")
            .append("<td>").append(rSet.getString("table_name")).append(".")
            .append(rSet.getString("column_name")).append("</td>").append("<td></td></tr>");
      }
      sbTotal.append("<tr><td>").append(strDomain).append("</td><td>").append(lTriger)
          .append("</td>").append("<td>").append(lProduce).append("</td>").append("<td>")
          .append(lView).append("</td>").append("<td>").append(lAutoIncrement).append("</td></tr>");
    } catch (Exception ex) {
      // TODO: handle exception
      ex.printStackTrace();
    } finally {
      AiDBUtil.doClear(conn);
    }
  }

  /**
   * 
   */
  public AiDbChecks(String strConfigFileName) {
    // TODO Auto-generated constructor stub
    try {
      StringBuffer sbTotal = new StringBuffer();
      StringBuffer sbDetail = new StringBuffer();

      AiFileUtil.checkFile(strConfigFileName);
      NodeList nl = JpfXmlUtil.getNodeList("dbsource", strConfigFileName);
      String strDefaultMail = "";


      if (1 == nl.getLength()) {
        Element el = (Element) nl.item(0);
        strDefaultMail = JpfXmlUtil.getParStrValue(el, "dbmails");
      } else {
        LOGGER.error("error source db info");
      }
      nl = JpfXmlUtil.getNodeList("dbcompare", strConfigFileName);
      LOGGER.debug(nl.getLength());
      String strJdbcUrl = "";
      for (int j = 0; j < nl.getLength(); j++) {
        // System.out.println(nl.item(j).getNodeValue());
        Element el = (Element) nl.item(j);
        strJdbcUrl = JpfXmlUtil.getParStrValue(el, "dburl");
        String strDbUsr = JpfXmlUtil.getParStrValue(el, "dbusr");
        String strDbPwd = JpfXmlUtil.getParStrValue(el, "dbpwd");
        String strDomain = JpfXmlUtil.getParStrValue(el, "dbdomain");
        strDefaultMail = JpfXmlUtil.getParStrValue(el, "dbmails") + "," + strDefaultMail;
        LOGGER.info(strDefaultMail);
        DbDescInfo cDbDescInfo = new DbDescInfo(strJdbcUrl, strDbUsr, strDbPwd);
        doCheck(cDbDescInfo, strDomain, sbTotal, sbDetail);
      }
      sendMail(sbTotal, sbDetail, strDefaultMail, strJdbcUrl);
    } catch (Exception ex) {
      // TODO: handle exception
      ex.printStackTrace();
    }
  }

  public void sendMail(StringBuffer sbTotal, StringBuffer sbDetail, String strMailers,
      String strDbInfo) throws Exception {
    LOGGER.debug("write Result File...");

    String strMailText = AiFileUtil.getFileTxt("compare_check.html");
    strMailText = strMailText.replaceAll("#wupf1", AiDateTimeUtil.getCurrDate());
    strMailText = strMailText.replaceAll("#wupf3", strDbInfo);
    strMailText = strMailText.replaceAll("#wupf4", sbDetail.toString());
    strMailText = strMailText.replaceAll("#diffs", sbTotal.toString());
    // strMailText+=sbAlterSql.toString();
    AiMail.sendMail(strMailers, strMailText, "GBK", "数据库非法的内容： 比对库" + strDbInfo);
  }

  /**
   * @param args 被测试类名：TODO 被测试接口名:TODO 测试场景：TODO 前置参数：TODO 入参： 校验值： 测试备注： update 2015年5月6日
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    AiDbChecks cDbChecks = new AiDbChecks(args[0]);
  }

}
