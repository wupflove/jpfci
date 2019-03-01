/**
 * @author 吴平福 E-mail:wupf@asiainfo.com
 * @version 创建时间：2015年2月14日 上午1:26:12 类说明
 */

package org.jpf.ci.dbs.compare;

import java.io.File;
import java.sql.Connection;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.dbsql.AiDBUtil;
import org.jpf.utils.ios.AiFileUtil;
import org.jpf.utils.scms.SVNUtil;
import org.jpf.utils.web.AiUrlUtil;
import org.jpf.utils.xmls.JpfXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *  
 */
public class JpfDbCompare {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * 
   * @category PDM直接与数据库比较
   * @author 吴平福
   * @param strConfigFileName
   * @param downPdmPath update 2016年5月2日
   */
  public void pdm2DbCompare(String strConfigFileName) {
    // TODO Auto-generated constructor stub
    try {
      String downPdmPath = PdmUtils.getPdmSavePath(strConfigFileName);
      GetTablefmPdm mGetTablefmPdm = new GetTablefmPdm();
      DbDescInfo cPdmDbDescInfo = null;
      CompareInfo cCompareInfo = new CompareInfo();
      AiFileUtil.checkFile(strConfigFileName);

      NodeList n = JpfXmlUtil.getNodeList("pdmconf", strConfigFileName);
      if (1 == n.getLength()) {
        Element el = (Element) n.item(0);
        cCompareInfo.setPdmJdbcUrl("pdm");
        mGetTablefmPdm.setPDMPath(downPdmPath);
        // 下载pdm时用户名
        String svnUser = JpfXmlUtil.getParStrValue(el, "svnuser");
        // 下载pdm时密码
        String svnPassword = JpfXmlUtil.getParStrValue(el, "svnpwd");
        cCompareInfo.setSendResulType(JpfXmlUtil.getParIntValue(el, "sendresulttype", 3));
        cCompareInfo.setAllMail(JpfXmlUtil.getParStrValue(el, "dbmails2all"));
        cCompareInfo.setDbType(JpfXmlUtil.getParStrValue(el, "dbtype"));
        cCompareInfo.setIncludesubtable(JpfXmlUtil.getParStrValue(el, "includesubtable"));
        // read Pdmdbinform node global param
        NodeList ns = JpfXmlUtil.getNodeList("pdmdbinform", strConfigFileName);
        if (1 == ns.getLength()) {
          Element ePdmdbinform = (Element) ns.item(0);

          cCompareInfo.setStrCondName(JpfXmlUtil.getParStrValue(ePdmdbinform, "envname"));
          cCompareInfo.setDoExecSql(JpfXmlUtil.getParIntValue(ePdmdbinform, "doexecsql"));
        }
        LOGGER.info("mail to: {}", cCompareInfo.getAllMail());
        AiUrlUtil jpfUrlUtil = new AiUrlUtil();
        // 将pdm从svn上下载到本地
        PdmUtils.getPdmsBySvnKit(el, downPdmPath, svnUser, svnPassword);
        // oracle_mysql,oracle与mysql映射关系
        PdmUtils.getDbRelation(el, mGetTablefmPdm);

        // forceConvert强制需要转换的域
        PdmUtils.getConverDomain(el, mGetTablefmPdm);

        // forceConvertDtType 指定某个域某个表某个字段的数据类型
        PdmUtils.getConverDtType(el, mGetTablefmPdm);

        // forceConvertDefType 指定某个域某个表某个字段的默认值数据类型
        PdmUtils.getConverDefType(el, mGetTablefmPdm);

        // 将pdm中的表存入map中
        mGetTablefmPdm.getPdmsMap(cCompareInfo);

      }

      NodeList nl = JpfXmlUtil.getNodeList("pdmdbcompare", strConfigFileName);
      for (int j = 0; j < nl.getLength(); j++) {
        Element el = (Element) nl.item(j);
        String strDbUsr = JpfXmlUtil.getParStrValue(el, "dbusr");
        String strDbPwd = JpfXmlUtil.getParStrValue(el, "dbpwd");
        cCompareInfo.setDevJdbcUrl(JpfXmlUtil.getParStrValue(el, "dburl"));
        cCompareInfo.setDbDomain(JpfXmlUtil.getParStrValue(el, "dbdomain"));
        cCompareInfo.setStrMails(
            JpfXmlUtil.getParStrValue(el, "dbmails") + "," + cCompareInfo.getAllMail());
        cCompareInfo.setPdmDbName(JpfXmlUtil.getParStrValue(el, "pdmdbname"));

        // LOGGER.info("DEV:{}", cCompareInfo.getDevJdbcUrl());
        LOGGER.info(cCompareInfo.getDbDomain());
        LOGGER.debug(strDbUsr);
        LOGGER.debug(strDbPwd);
        LOGGER.info("mail to {}", cCompareInfo.getStrMails());

        NodeList nlParentChild = el.getElementsByTagName("parent_childen");

        AiTableInfoMainAndSub.add(nlParentChild);

        DbDescInfo cDbDescInfo2 = new DbDescInfo(cCompareInfo.getDevJdbcUrl(), strDbUsr, strDbPwd);

        Connection conn_develop = null;

        try {

          conn_develop = cDbDescInfo2.getConn();

          // 带分表比对

          LOGGER.info("Check sub table...");
          CompareTablesWithSub cCompareSubTables = new CompareTablesWithSub();
          cCompareInfo.setCompareType(1);
          cCompareSubTables.setDomain_table(mGetTablefmPdm.getDomainTable());
          cCompareSubTables.doCompare(null, conn_develop, cCompareInfo);
          cCompareSubTables = null;

          // 带分表比较索引

          LOGGER.info("compare sub index...");
          CompareIndexWithSub cCompareIndexSub = new CompareIndexWithSub();
          cCompareInfo.setCompareType(2);
          cCompareIndexSub.setDomain_table(mGetTablefmPdm.getDomainIndex());
          cCompareIndexSub.doCompare(null, conn_develop, cCompareInfo);
          cCompareIndexSub = null;

        } catch (Exception ex) {
          // TODO: handle exception
          ex.printStackTrace();
        } finally {
          AiDBUtil.doClear(conn_develop);
        }
      }
    } catch (Exception ex) {
      // TODO: handle exception
      ex.printStackTrace();
    }
    LOGGER.info("FINISH AND  EXIT");
  }

  /**
   * 
   * @category PDM与PDM比对
   * @author 吴平福
   * @param strConfigFileName
   * @param downPdmPath
   * @param versionOrDate update 2016年5月2日
   */
  public void pdm2PdmCompare(String strConfigFileName, String versionOrDate) {
    // TODO Auto-generated constructor stub
    try {
      String downPdmPath = PdmUtils.getPdmSavePath(strConfigFileName);
      versionOrDate = versionOrDate.trim();
      DbDescInfo cPdmDbDescInfo = null;
      CompareInfo cCompareInfo = new CompareInfo();
      AiFileUtil.checkFile(strConfigFileName);
      String svnurl = null;
      // pdm更新时需要用户名及密码
      String svnUser = null;
      String svnPassword = null;
      GetTablefmPdm getTable1 = new GetTablefmPdm();
      GetTablefmPdm getTable2 = new GetTablefmPdm();
      NodeList n = JpfXmlUtil.getNodeList("dbpdmandpdm", strConfigFileName);
      if (1 == n.getLength()) {
        Element el = (Element) n.item(0);
        cCompareInfo.setStrCondName(JpfXmlUtil.getParStrValue(el, "pdmname"));
        // pdm的svn路径
        svnurl = JpfXmlUtil.getParStrValue(el, "pdmsvnurl").trim();

        // 下载pdm时用户名
        svnUser = JpfXmlUtil.getParStrValue(el, "svnuser").trim();
        // 下载pdm时密码
        svnPassword = JpfXmlUtil.getParStrValue(el, "svnpwd").trim();
        cCompareInfo.setStrMails(JpfXmlUtil.getParStrValue(el, "dbmails"));
        cCompareInfo.setPdmDbName(JpfXmlUtil.getParStrValue(el, "pdmdbname"));
        cCompareInfo.setStrCondName(JpfXmlUtil.getParStrValue(el, "envname"));
        String pdmpathperf =
            downPdmPath + File.separator + JpfXmlUtil.getParStrValue(el, "pdmname");

        if (versionOrDate == null || versionOrDate.trim().length() == 0) {
          LOGGER.debug("please input version or date to compare different versions of PDM ");
        } else if (versionOrDate.contains("-")) {
          String[] date = versionOrDate.split(":");
          if (date.length == 2) {
            LOGGER.info("date compare");
            String dt = date[0];
            // 更新svn上的pdm到指定目录
            SVNUtil.checkout(svnurl, downPdmPath, svnUser, svnPassword);
            SVNUtil.updateByDate(svnurl, downPdmPath, JpfXmlUtil.getParStrValue(el, "pdmname"), dt,
                svnUser, svnPassword);
            getTable1.setPDMPath(pdmpathperf);
            getTable1.readPdmMap(cCompareInfo);

            dt = date[1];
            SVNUtil.updateByDate(svnurl, downPdmPath, JpfXmlUtil.getParStrValue(el, "pdmname"), dt,
                svnUser, svnPassword);
            getTable2.setPDMPath(pdmpathperf);
            getTable2.readPdmMap(cCompareInfo);
            cCompareInfo.setDevJdbcUrl(dt);
            cCompareInfo.setPdmDtVers("日期比对");
            cCompareInfo.setPdmJdbcUrl(dt);
          }
        } else {
          String[] version = versionOrDate.split(":");
          if (version.length == 2) {
            String vs = version[0];
            LOGGER.info("version compare");
            // 更新svn上的pdm到指定目录
            SVNUtil.checkout(svnurl, downPdmPath, svnUser, svnPassword);
            // 通过svnkit，更新到指定版本
            SVNUtil.updateByVersion(svnurl, downPdmPath, JpfXmlUtil.getParStrValue(el, "pdmname"),
                vs, svnUser, svnPassword);

            getTable1.setPDMPath(pdmpathperf);
            getTable1.readPdmMap(cCompareInfo);

            cCompareInfo.setPdmJdbcUrl(vs);
            vs = version[1];
            SVNUtil.updateByVersion(svnurl, downPdmPath, JpfXmlUtil.getParStrValue(el, "pdmname"),
                vs, svnUser, svnPassword);

            getTable2.setPDMPath(pdmpathperf);
            getTable2.readPdmMap(cCompareInfo);
            cCompareInfo.setDevJdbcUrl(vs);
            cCompareInfo.setPdmDtVers("版本比对");
          }

        }
      }
      // 获取一个pdm中用到过的Owners
      LinkedList<String> userlist1 = getTable1.getUserlist();

      LinkedList<String> userlist2 = getTable2.getUserlist();

      for (String user : userlist2) {
        if (!userlist1.contains(user)) {

          userlist1.add(user);
        }
      }
      for (String domain : userlist1) {
        cCompareInfo.setDbDomain(domain);
        LOGGER.debug("begin to compare domain: " + domain);
        try {

          // 带分表比对
          LOGGER.info("Check sub table...");
          CompareTablesWithSub cCompareSubTables = new CompareTablesWithSub();
          cCompareInfo.setCompareType(1);
          cCompareSubTables.setDomain_table(getTable1.getDomainTable());
          cCompareSubTables.setDomain_table2(getTable2.getDomainTable());
          cCompareSubTables.doCompare(null, null, cCompareInfo);
          cCompareSubTables = null;
          // 带分表比较索引
          LOGGER.info("compare sub index...");
          CompareIndexWithSub cCompareIndexSub = new CompareIndexWithSub();
          cCompareInfo.setCompareType(2);
          cCompareIndexSub.setDomain_table(getTable1.getDomainIndex());
          cCompareIndexSub.setDomain_table2(getTable2.getDomainIndex());
          cCompareIndexSub.doCompare(null, null, cCompareInfo);
          cCompareIndexSub = null;

        } catch (Exception ex) {
          // TODO: handle exception
          ex.printStackTrace();
        }

      }

    } catch (Exception ex) {
      // TODO: handle exception
      ex.printStackTrace();
    }
    LOGGER.info("FINISH AND  EXIT");
  }

  /**
   * 
   * @category 主表与分表比对
   * @author 吴平福
   * @param strConfigFileName update 2016年5月2日
   */
  public void main2SubCompare(String strConfigFileName) {
    // TODO Auto-generated constructor stub
    try {

      CompareInfo cCompareInfo = new CompareInfo();

      AiFileUtil.checkFile(strConfigFileName);
      NodeList nl = JpfXmlUtil.getNodeList("subcompare", strConfigFileName);

      for (int j = 0; j < nl.getLength(); j++) {
        Element el = (Element) nl.item(j);
        String strDbUsr = JpfXmlUtil.getParStrValue(el, "dbusr");
        String strDbPwd = JpfXmlUtil.getParStrValue(el, "dbpwd");
        cCompareInfo.setDevJdbcUrl(JpfXmlUtil.getParStrValue(el, "dburl"));
        cCompareInfo.setDbDomain(JpfXmlUtil.getParStrValue(el, "dbdomain"));
        cCompareInfo.setStrMails(
            JpfXmlUtil.getParStrValue(el, "dbmails") + "," + cCompareInfo.getAllMail());


        LOGGER.info(cCompareInfo.getDbDomain());
        LOGGER.debug(strDbUsr);
        LOGGER.debug(strDbPwd);
        LOGGER.info("mail to {}", cCompareInfo.getStrMails());

        NodeList nlParentChild = el.getElementsByTagName("parent_childen");

        AiTableInfoMainAndSub.add(nlParentChild);

        DbDescInfo cDbDescInfo = new DbDescInfo(cCompareInfo.getDevJdbcUrl(), strDbUsr, strDbPwd);
        DbDescInfo cPdmDbDescInfo =
            new DbDescInfo(cCompareInfo.getDevJdbcUrl(), strDbUsr, strDbPwd);
        Connection conn_pdm = null;
        Connection conn_develop = null;
        try {
          conn_pdm = cPdmDbDescInfo.getConn();
          conn_develop = cDbDescInfo.getConn();


          // 带分表比对
          LOGGER.info("Check sub table...");
          CompareTablesWithSub cCompareSubTables = new CompareTablesWithSub();
          cCompareInfo.setCompareType(1);
          cCompareSubTables.doCompare(conn_pdm, conn_develop, cCompareInfo);

          // 带分表比较索引
          LOGGER.info("compare sub index...");
          CompareIndexWithSub cCompareIndexSub = new CompareIndexWithSub();
          cCompareInfo.setCompareType(2);
          cCompareIndexSub.doCompare(conn_pdm, conn_develop, cCompareInfo);

        } catch (Exception ex) {
          // TODO: handle exception
          ex.printStackTrace();
        } finally {
          AiDBUtil.doClear(conn_pdm);
          AiDBUtil.doClear(conn_develop);
        }
      }
    } catch (Exception ex) {
      // TODO: handle exception
      ex.printStackTrace();
    }
    LOGGER.info("FINISH AND  EXIT");

  }

  /**
   * 
   * @category 数据库与数据库比对
   * @author 吴平福
   * @param strConfigFileName update 2016年5月2日
   */
  public void db2DbCompare(String strConfigFileName) {
    // TODO Auto-generated constructor stub
    try {
      DbDescInfo cPdmDbDescInfo = null;
      CompareInfo cCompareInfo = new CompareInfo();

      AiFileUtil.checkFile(strConfigFileName);
      NodeList nl = JpfXmlUtil.getNodeList("dbsource", strConfigFileName);
      if (1 == nl.getLength()) {
        Element el = (Element) nl.item(0);
        cCompareInfo.setPdmJdbcUrl((JpfXmlUtil.getParStrValue(el, "dburl")));
        String strDbUsr = JpfXmlUtil.getParStrValue(el, "dbusr");
        String strDbPwd = JpfXmlUtil.getParStrValue(el, "dbpwd");
        cCompareInfo.setAllMail(JpfXmlUtil.getParStrValue(el, "dbmails"));
        cCompareInfo.setStrExcludeTable(JpfXmlUtil.getParStrValue(el, "excludetable"));
        cCompareInfo.setStrCondName(JpfXmlUtil.getParStrValue(el, "envname"));
        cCompareInfo.setDoExecSql(JpfXmlUtil.getParIntValue(el, "doexecsql"));
        cCompareInfo.setDbType(JpfXmlUtil.getParStrValue(el, "dbtype"));
        cCompareInfo.setSendResulType(JpfXmlUtil.getParIntValue(el, "sendresulttype"));
        LOGGER.info("Source JdbcUrl:{}", cCompareInfo.getPdmJdbcUrl());
        LOGGER.debug("strDbUsr:{}", strDbUsr);
        LOGGER.debug("strDbPwd:{}", strDbPwd);
        cPdmDbDescInfo = new DbDescInfo(cCompareInfo.getPdmJdbcUrl(), strDbUsr, strDbPwd);
      } else {
        LOGGER.error("error source db info");
      }
      nl = JpfXmlUtil.getNodeList("dbcompare", strConfigFileName);
      for (int j = 0; j < nl.getLength(); j++) {
        Element el = (Element) nl.item(j);
        String strDbUsr = JpfXmlUtil.getParStrValue(el, "dbusr");
        String strDbPwd = JpfXmlUtil.getParStrValue(el, "dbpwd");
        cCompareInfo.setDevJdbcUrl(JpfXmlUtil.getParStrValue(el, "dburl"));
        cCompareInfo.setDbDomain(JpfXmlUtil.getParStrValue(el, "dbdomain"));
        cCompareInfo.setStrMails(
            JpfXmlUtil.getParStrValue(el, "dbmails") + "," + cCompareInfo.getAllMail());
        cCompareInfo.setPdmDbName(JpfXmlUtil.getParStrValue(el, "pdmdbname"));

        LOGGER.info("DEV:{}", cCompareInfo.getDevJdbcUrl());
        LOGGER.info(cCompareInfo.getDbDomain());
        LOGGER.debug(strDbUsr);
        LOGGER.debug(strDbPwd);
        LOGGER.info("mail to {}", cCompareInfo.getStrMails());

        NodeList nlParentChild = el.getElementsByTagName("parent_childen");

        AiTableInfoMainAndSub.add(nlParentChild);

        DbDescInfo cDbDescInfo2 = new DbDescInfo(cCompareInfo.getDevJdbcUrl(), strDbUsr, strDbPwd);

        Connection conn_pdm = null;
        Connection conn_develop = null;
        try {
          conn_pdm = cPdmDbDescInfo.getConn();
          conn_develop = cDbDescInfo2.getConn();

          // 先执行SQL到PDM
          NodeList nlPreSql = el.getElementsByTagName("presql");
          for (int i = 0; i < nlPreSql.getLength(); i++) {
            Node child = nlPreSql.item(i);
            if (child instanceof Element) {
              String strSql = child.getFirstChild().getNodeValue().toLowerCase().trim();
              AiDBUtil.execUpdateSql(conn_pdm, strSql);
            }
          }

          // 带分表比对
          LOGGER.info("Check sub table...");
          CompareTablesWithSub cCompareSubTables = new CompareTablesWithSub();
          cCompareInfo.setCompareType(1);
          cCompareSubTables.doCompare(conn_pdm, conn_develop, cCompareInfo);

          // 带分表比较索引
          LOGGER.info("compare sub index...");
          CompareIndexWithSub cCompareIndexSub = new CompareIndexWithSub();
          cCompareInfo.setCompareType(2);
          cCompareIndexSub.doCompare(conn_pdm, conn_develop, cCompareInfo);

        } catch (Exception ex) {
          // TODO: handle exception
          ex.printStackTrace();
        } finally {
          AiDBUtil.doClear(conn_pdm);
          AiDBUtil.doClear(conn_develop);
        }
      }
    } catch (Exception ex) {
      // TODO: handle exception
      ex.printStackTrace();
    }
    LOGGER.info("FINISH AND  EXIT");

  }


  /**
   * 
   * @category 构造函数
   * @author 吴平福
   * @param strConfigFileName
   * @param strCompareType
   * @param versionOrDate
   */
  public JpfDbCompare(String strConfigFileName, String strCompareType, String versionOrDate) {

    try {
      LOGGER.info(strConfigFileName);
      AiFileUtil.checkFile(strConfigFileName);
      // strExecute: 1:执行数据库与数据库比对，2：执行pdm与数据库比对，3：pdm与pdm比对
      if (strCompareType.trim().equals("1")) {
        LOGGER.info("Database compare with Database");
        db2DbCompare(strConfigFileName);
      } else if (strCompareType.trim().equals("2")) {
        LOGGER.info("PDM compare with Database");
        pdm2DbCompare(strConfigFileName);
      } else if (strCompareType.trim().equals("3")) {
        LOGGER.info("PDM compare with PDM");
        pdm2PdmCompare(strConfigFileName, versionOrDate);
      } else if (strCompareType.trim().equals("4")) {
        LOGGER.info("sub tables compare with main tables");
        main2SubCompare(strConfigFileName);
      } else {
        LOGGER.error("error: the type of comparison command is error");
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * 
   * @category 主函数
   * @author 吴平福
   * @param args update 2016年5月5日
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    if (3 == args.length) {

      JpfDbCompare cJpfDbCompare = new JpfDbCompare(args[0], args[1], args[2]);
    }
    if (2 == args.length) {
      JpfDbCompare cJpfDbCompare = new JpfDbCompare(args[0], args[1], null);

    }
  }

}
