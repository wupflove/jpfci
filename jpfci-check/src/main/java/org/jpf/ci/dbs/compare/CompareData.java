/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2015年2月8日 下午10:09:38 类说明
 */

package org.jpf.ci.dbs.compare;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.ios.AiFileUtil;
import org.jpf.utils.xmls.JpfXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 */
public final class CompareData extends AbstractDbCompare {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * 
   */
  public CompareData() {
    // TODO Auto-generated constructor stub
  }

  /**
   * 
   * @param strConfigFileName
   * @return
   * @throws Exception
   */
  private String getCompareTableInfo(String strConfigFileName) throws Exception {
    String strTableInfo = "";
    AiFileUtil.checkFile(strConfigFileName);
    NodeList nl = JpfXmlUtil.getNodeList("comparetables", strConfigFileName);
    LOGGER.debug(nl.getLength());

    if (1 == nl.getLength()) {
      Element el = (Element) nl.item(0);
      strTableInfo = JpfXmlUtil.getParStrValue(el, "tableinfo");
    }
    return strTableInfo;
  }

  /**
   * 
   * @param conn_pdm
   * @param conn_develop
   * @param strConfigFileName
   * @throws Exception
   */
  public void doCompare(Connection conn_pdm, Connection conn_develop,
      final String strConfigFileName) throws Exception {

    String strTableName = getCompareTableInfo(strConfigFileName);
    ResultSet sourceResultSet = getResultSet(conn_pdm, strTableName);
    ResultSet targetResultSet = getResultSet(conn_develop, strTableName);
    Map<Long, String> sourceIdHash = new HashMap<Long, String>();
    Map<Long, String> targetIdHash = new HashMap<Long, String>();

    try {
      long rows = 0;
      do {
        if (sourceResultSet.next()) {
          if (targetResultSet.next()) {
            // Compare the lines
            long sourceHash = HASH(getRowValues(sourceResultSet, sourceResultSet.getMetaData()));
            long targetHash = HASH(getRowValues(targetResultSet, targetResultSet.getMetaData()));

            sourceIdHash.put(sourceHash, sourceResultSet.getString(1));
            targetIdHash.put(targetHash, targetResultSet.getString(1));

            if (targetIdHash.containsKey(sourceHash)) {
              targetIdHash.remove(sourceHash);
              sourceIdHash.remove(sourceHash);
            }
            if (sourceIdHash.containsKey(targetHash)) {
              sourceIdHash.remove(targetHash);
              targetIdHash.remove(targetHash);
            }
          } else {
            // Add the source row
            long sourceHash = HASH(getRowValues(sourceResultSet, sourceResultSet.getMetaData()));
            sourceIdHash.put(sourceHash, sourceResultSet.getString(1));
          }
        } else {
          if (targetResultSet.next()) {
            // Add the target row
            long targetHash = HASH(getRowValues(targetResultSet, targetResultSet.getMetaData()));
            targetIdHash.put(targetHash, targetResultSet.getString(1));
          } else {
            break;
          }
        }
        if (rows++ % 10000 == 0) {
          LOGGER.debug("Rows : " + rows);
        }
      } while (true);
    } finally {
      closeAll(sourceResultSet);
      closeAll(targetResultSet);
    }

    for (final Map.Entry<Long, String> mapEntry : sourceIdHash.entrySet()) {
      if (targetIdHash.containsKey(mapEntry.getKey())) {
        targetIdHash.remove(mapEntry.getKey());
        continue;
      }
      LOGGER.info("Not in target : " + mapEntry.getValue());
    }
    for (final Map.Entry<Long, String> mapEntry : targetIdHash.entrySet()) {
      if (sourceIdHash.containsKey(mapEntry.getKey())) {
        sourceIdHash.remove(mapEntry.getKey());
        continue;
      }
      LOGGER.debug("Not in source : " + mapEntry.getValue());
    }

    LOGGER.debug("In source and not target : " + sourceIdHash.size());
    LOGGER.debug("In target and not source : " + targetIdHash.size());
  }

  /**
   * 
   * @param connection
   * @param tableName
   * @return
   */
  private ResultSet getResultSet(final Connection connection, final String tableName) {
    String query = "select * from " + tableName;
    LOGGER.debug(query);
    return executeQuery(connection, query);
  }

  /**
   * 
   * @param resultSet
   * @param resultSetMetaData
   * @return
   * @throws SQLException
   */
  private Object[] getRowValues(final ResultSet resultSet,
      final ResultSetMetaData resultSetMetaData) throws SQLException {
    List<Object> rowValues = new ArrayList<Object>();
    for (int i = 2; i < resultSetMetaData.getColumnCount(); i++) {
      rowValues.add(resultSet.getObject(i));
    }
    return rowValues.toArray(new Object[rowValues.size()]);
  }

  /**
   * 
   * @param connection
   * @param query
   * @return
   */
  private final ResultSet executeQuery(final Connection connection, final String query) {
    try {
      return connection.createStatement().executeQuery(query);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 
   * @param objects
   * @return
   */
  private final Long HASH(final Object... objects) {
    StringBuilder builder = new StringBuilder();
    for (Object object : objects) {
      builder.append(object);
    }
    return hash(builder.toString());
  }

  /**
   * 
   * @param string
   * @return
   */
  public Long hash(final String string) {
    // Must be prime of course
    long seed = 131; // 31 131 1313 13131 131313 etc..
    long hash = 0;
    char[] chars = string.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      hash = (hash * seed) + chars[i];
    }
    return Long.valueOf(Math.abs(hash));
  }

  private void closeAll(final ResultSet resultSet) {
    Statement statement = null;
    Connection connection = null;
    try {
      if (resultSet != null) {
        statement = resultSet.getStatement();
      }
      if (statement != null) {
        connection = statement.getConnection();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    close(resultSet);
    close(statement);

  }

  private void close(final Statement statement) {
    if (statement == null) {
      return;
    }
    try {
      statement.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private void close(final ResultSet resultSet) {
    if (resultSet == null) {
      return;
    }
    try {
      resultSet.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jpf.ci.dbs.compare.AbstractDbCompare#GetHtmlName()
   */
  @Override
  String getHtmlName() {
    // TODO Auto-generated method stub
    return "compare_index.html";
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jpf.ci.dbs.compare.AbstractDbCompare#GetMailTitle()
   */
  @Override
  String getMailTitle() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  void doWork(Connection conn_product, Connection conn_develop, CompareInfo cCompareInfo)
      throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  void doWork(Connection conn_develop, CompareInfo cCompareInfo) throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  void doWork(CompareInfo cCompareInfo) throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  String getExecSqlHtmlName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  String getErrorHtmlName() {
    // TODO Auto-generated method stub
    return "ErrorInformation.html";
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jpf.ci.dbs.compare.AbstractDbCompare#insertResult(org.jpf.ci.dbs.compare.CompareInfo,
   * org.jpf.ci.dbs.compare.CompareResult)
   */
  @Override
  void insertResult(CompareInfo cCompareInfo) {
    // TODO Auto-generated method stub

  }
}
