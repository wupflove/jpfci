/**
 * @author 吴平福 E-mail:wupf@asiainfo.com
 * @version 创建时间：2016年9月23日 下午8:02:34 类说明
 */

package org.jpf.ci.dbs.dbchange;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.xmls.JpfXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 */
public class DbInfos {
  private final String ConstConfigFileName = "dbchange.xml";
  private static final Logger logger = LogManager.getLogger();

  private final int NO_EXEC_SQL = 0;
  private final int DO_EXEC_SQL = 1;

  public DbInfos() throws Exception {
    loadDbInfos();
  }

  HashMap<String, DbInfo> mapDbInfos = new HashMap<String, DbInfo>();

  private void loadDbInfos() throws Exception {

    NodeList nl = JpfXmlUtil.getNodeList("dbinfo", ConstConfigFileName);
    for (int j = 0; j < nl.getLength(); j++) {
      DbInfo cDbInfo = new DbInfo();
      Element el = (Element) nl.item(j);
      cDbInfo.domain = JpfXmlUtil.getParStrValue(el, "domain");
      cDbInfo.dbuser = JpfXmlUtil.getParStrValue(el, "dbusr");
      cDbInfo.dbpass = JpfXmlUtil.getParStrValue(el, "dbpwd");
      cDbInfo.URL = JpfXmlUtil.getParStrValue(el, "dburl");
      cDbInfo.dbmail = JpfXmlUtil.getParStrValue(el, "dbmail");
      cDbInfo.iDoExecSql = JpfXmlUtil.getParIntValue(el, "doexecsql");

      // 获得需要进行特殊处理的分表——母表对应关系
      NodeList nlParentChild = el.getElementsByTagName("parent_childen");
      cDbInfo.parent_child.clear();
      for (int i = 0; i < nlParentChild.getLength(); i++) {
        Node child = nlParentChild.item(i);
        if (child instanceof Element) {
          String s = child.getFirstChild().getNodeValue().toLowerCase().trim();
          String[] s1 = s.split(";");
          if (s1.length == 2) {
            cDbInfo.parent_child.put(s1[1], s1[0]);
          }
        }
      }
      mapDbInfos.put(cDbInfo.domain, cDbInfo);
      logger.info("add dbinfo:" + cDbInfo.domain);

      for (Iterator iter_table = cDbInfo.parent_child.keySet().iterator(); iter_table.hasNext();) {
        String key_table = (String) iter_table.next();
        String strAlignParentTableName = cDbInfo.parent_child.get(key_table);
        logger.info(key_table + ";" + strAlignParentTableName);

      }

    }

  }

  /**
   * 
   * @category 获取ALIGN表名
   * @author 吴平福
   * @param strDbName
   * @return update 2016年9月23日
   */
  public HashMap<String, String> getAlignTable(String strDbName) {
    for (Iterator iter_table = mapDbInfos.keySet().iterator(); iter_table.hasNext();) {
      String key_table = (String) iter_table.next();
      if (key_table.trim().toLowerCase().equalsIgnoreCase(strDbName.trim().toLowerCase())) {
        DbInfo cDbInfo = mapDbInfos.get(key_table);// 获得PDM中的表
        return cDbInfo.parent_child;
      }
    }
    return null;
  }

  /**
   * 
   * @category 是否执行变更的SQL
   * @author 吴平福
   * @param strDbName
   * @return update 2016年9月23日
   */
  public int getDoExecSql(String strDbName) {
    for (Iterator iter_table = mapDbInfos.keySet().iterator(); iter_table.hasNext();) {
      String key_table = (String) iter_table.next();
      if (key_table.trim().toLowerCase().equalsIgnoreCase(strDbName.trim().toLowerCase())) {
        DbInfo cDbInfo = mapDbInfos.get(key_table);// 获得PDM中的表
        return cDbInfo.iDoExecSql;
      }
    }
    return NO_EXEC_SQL;
  }

  /**
   * 
   * @category 是否执行变更的SQL
   * @author 吴平福
   * @param strDbName
   * @return update 2016年9月23日
   */
  public DbInfo getDbInfo(String strDbName) {
    for (Iterator iter_table = mapDbInfos.keySet().iterator(); iter_table.hasNext();) {
      String key_table = (String) iter_table.next();
      if (key_table.trim().toLowerCase().equalsIgnoreCase(strDbName.trim().toLowerCase())) {
        DbInfo cDbInfo = mapDbInfos.get(key_table);// 获得PDM中的表
        return cDbInfo;
      }
    }
    return null;
  }


}
