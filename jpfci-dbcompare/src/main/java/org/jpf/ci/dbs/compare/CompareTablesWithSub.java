/** 
 * @author 吴平福 
 * E-mail:wupf@asiainfo.com 
 * @version 创建时间：2015年2月14日 上午1:26:12 
 * 类说明 
 */

package org.jpf.ci.dbs.compare;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.ci.dbs.compare.sqls.SqlCreateTable;

import org.jpf.utils.dbsql.AiDBUtil;

/**
 * 
 */
public class CompareTablesWithSub extends AbstractDbCompare {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * 
     * @category @author 吴平福
     * @param map_dev
     *            update 2016年5月3日
     */
    private void removeSubTables(LinkedHashMap<String, AiTable> map_dev) {

        for (Iterator iter_table = map_dev.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();

            if (!CompareUtil.isSubTable(key_table)) {
                // 分表
                // logger.info(key_table);
                iter_table.remove();
            }
        }

    }

    /**
     * 
     * @category 用于PDM与PDM比对
     * @author 吴平福
     * @param transaction
     * @param inDomain
     * @param cCompareInfo
     * @return
     * @throws Exception
     *             update 2016年5月2日
     */
    @Override
    public void doWork(CompareInfo cCompareInfo) throws Exception {
        // PDM数据库连接
        String strPdmDomain = cCompareInfo.getDbDomain();
        TreeMap<String, AiTable> map_pdm = new TreeMap<String, AiTable>();
        TreeMap<String, AiTable> map_develop = new TreeMap<String, AiTable>();
        if (domain_table.containsKey(strPdmDomain)) {
            map_pdm = domain_table.get(strPdmDomain);
        }
        if (domain_table2.containsKey(strPdmDomain)) {
            map_develop = domain_table2.get(strPdmDomain);
        }

        // 遍历开发库Map
        LOGGER.info("begin compare tables... ");
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();
            // 获得PDM中的表
            AiTable table_develop = map_develop.get(key_table);

            if (table_develop.getTable_type().equalsIgnoreCase("view")) {
                CompareUtil.appendTable(null, table_develop, null, null, 2, sb, vSql, cCompareInfo.getDbDomain());
                cCompareResult.iCount2++;
                continue;
            }
            LOGGER.debug("key_table=" + key_table);
            key_table = CompareUtil.getParentTableName(key_table);
            LOGGER.debug("parent_table=" + key_table);
            AiTable table_pdm = map_pdm.get(key_table);// 尝试从比对库中获得同名表
            if (table_pdm == null) {
                // 如果获得表为空，说明PDM不存在，比对库存在
                CompareUtil.appendTable(table_pdm, table_develop, null, null, 2, sb, vSql, cCompareInfo.getDbDomain());
                cCompareResult.iCount2++;
            } else {
                // 表相同，判断字段、字段类型、字段长度
                for (Iterator iter_column = table_develop.columns.keySet().iterator(); iter_column.hasNext();) {
                    String key_column = (String) iter_column.next();
                    // 获得开发库中的列
                    AiColumn column_develop = (AiColumn) table_develop.columns.get(key_column);
                    // 尝试从生产库中获得同名列
                    AiColumn column_pdm = (AiColumn) table_pdm.columns.get(key_column);
                    if (column_pdm == null) {
                        // 如果列名为空，说明PDM不存在，比对库存在
                        CompareUtil.appendTable(table_pdm, table_develop, column_develop, null, 4, sb, vSql,
                                cCompareInfo.getDbDomain());
                        cCompareResult.iCount4++;
                    } else {// 说明两者都存在
                        /*
                         * if (!column_develop.getDataType().equalsIgnoreCase(
                         * column_pdm.getDataType()))// 字段类型不一致 {
                         * CompareUtil.append(table_develop, column_pdm,
                         * column_develop, 5, sb); iCount5++; } if
                         * (!column_develop
                         * .getNullable().equalsIgnoreCase(column_pdm
                         * .getNullable()))// 是否为空不一致 {
                         * CompareUtil.append(table_develop, column_pdm,
                         * column_develop, 6, sb); iCount6++; }
                         */
                    }
                }
            }
        }
        // 遍历生产库Map
        for (Iterator iter_table = map_pdm.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();

            AiTable table_pdm = map_pdm.get(key_table);// 尝试从比对库中获得同名表
            AiTable table_develop = map_develop.get(key_table);// 获得PDM中的表
            if (table_develop == null) { // 如果获得表为空，说明PDM存在，比对库不存在

                // String mStrSql=CreateTables.GetCreateTableSql(conn_pdm,
                // strPdmDomain,key_table);rin
                String mStrSql = SqlCreateTable.createTableSqlFromPdm( table_pdm, cCompareInfo);
                CompareUtil.addSqlVector(vSql, mStrSql, 1, key_table);
                CompareUtil.appendTable(table_pdm, table_develop, null, null, 1, sb, vSql, cCompareInfo.getDbDomain());

                cCompareResult.iCount1++;
            } else {
                compareFromPdm(table_pdm, table_develop, cCompareInfo);
            }

            for (Iterator iter_table2 = map_develop.keySet().iterator(); iter_table2.hasNext();) {
                String key_table2 = (String) iter_table2.next();

                if (CompareUtil.isSubTable(key_table, key_table2)) {
                    // 进入分表
                    table_develop = map_develop.get(key_table2);
                    // 比较
                    compareFromPdm(table_pdm, table_develop, cCompareInfo);
                }
            }

        }
        map_pdm = null;
        map_develop = null;

    }
    /**
     * 
     * @category 用于MAP数据库比对
     * @author 吴平福
     * @param transaction
     * @param inDomain
     * @param cCompareInfo
     * @return
     * @throws Exception
     *             update 2016年5月2日
     */
    public void compareWithMap(TreeMap<String, AiTable> map_pdm, LinkedHashMap<String, AiTable> map_develop ,CompareInfo cCompareInfo) throws Exception {

        LOGGER.info("map_db size:" + map_develop.size());
        // CheckSubTables(map_develop);
        LOGGER.info("map_pdm size:" + map_pdm.size());

        // 遍历开发库Map
        LOGGER.info("begin compare tables... ");
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();

            AiTable table_develop = map_develop.get(key_table);// 获得PDM中的表

            if (table_develop.getTable_type().equalsIgnoreCase("view")) {
                CompareUtil.appendTable(null, table_develop, null, null, 2, sb, vSql, cCompareInfo.getDbDomain());
                cCompareResult.iCount2++;
                continue;
            }
            LOGGER.debug("key_table=" + key_table);
            key_table = CompareUtil.getParentTableName(key_table);
            LOGGER.debug("parent_table=" + key_table);
            // 尝试从比对库中获得同名表
            AiTable table_pdm = map_pdm.get(key_table);
            if (table_pdm == null) {
                // 如果获得表为空，说明PDM不存在，比对库存在
                CompareUtil.appendTable(table_pdm, table_develop, null, null, 2, sb, vSql, cCompareInfo.getDbDomain());
                cCompareResult.iCount2++;
            } else {
                // 表相同，判断字段、字段类型、字段长度
                for (Iterator iter_column = table_develop.columns.keySet().iterator(); iter_column.hasNext();) {
                    String key_column = (String) iter_column.next();
                    // 获得开发库中的列
                    AiColumn column_develop = (AiColumn) table_develop.columns.get(key_column);
                    // 尝试从生产库中获得同名列
                    AiColumn column_pdm = (AiColumn) table_pdm.columns.get(key_column);
                    if (column_pdm == null) {
                        // 如果列名为空，说明PDM不存在，比对库存在
                        CompareUtil.appendTable(table_pdm, table_develop, column_develop, null, 4, sb, vSql,
                                cCompareInfo.getDbDomain());
                        cCompareResult.iCount4++;
                    } else {// 说明两者都存在
                        /*
                         * if (!column_develop.getDataType().equalsIgnoreCase(
                         * column_pdm.getDataType()))// 字段类型不一致 {
                         * CompareUtil.append(table_develop, column_pdm,
                         * column_develop, 5, sb); iCount5++; } if
                         * (!column_develop
                         * .getNullable().equalsIgnoreCase(column_pdm
                         * .getNullable()))// 是否为空不一致 {
                         * CompareUtil.append(table_develop, column_pdm,
                         * column_develop, 6, sb); iCount6++; }
                         */
                    }
                }
            }
        }
        // 遍历生产库Map
        for (Iterator iter_table = map_pdm.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();

            AiTable table_pdm = map_pdm.get(key_table);// 尝试从比对库中获得同名表
            AiTable table_develop = map_develop.get(key_table);// 获得PDM中的表
            if (table_develop == null) { // 如果获得表为空，说明PDM存在，比对库不存在
                String mStrSql = SqlCreateTable.createTableSqlFromPdm( table_pdm, cCompareInfo);
                CompareUtil.addSqlVector(vSql, mStrSql, 1, key_table);
                CompareUtil.appendTable(table_pdm, table_develop, null, null, 1, sb, vSql, cCompareInfo.getDbDomain());

                cCompareResult.iCount1++;
            } else {
                compareFromPdm(table_pdm, table_develop, cCompareInfo);
            }

            for (Iterator iter_table2 = map_develop.keySet().iterator(); iter_table2.hasNext();) {
                String key_table2 = (String) iter_table2.next();

                if (CompareUtil.isSubTable(key_table, key_table2)) {
                    // 进入分表
                    table_develop = map_develop.get(key_table2);
                    // 比较
                    compareFromPdm(table_pdm, table_develop, cCompareInfo);
                }
            }

        }
        map_pdm = null;
        map_develop = null;
    }
    /**
     * 
     * @category 用于PDM与数据库比对
     * @author 吴平福
     * @param transaction
     * @param inDomain
     * @param cCompareInfo
     * @return
     * @throws Exception
     *             update 2016年5月2日
     */
    @Override
    public void doWork(Connection conn_develop, CompareInfo cCompareInfo) throws Exception {
        // PDM数据库连接
        String strPdmDomain = cCompareInfo.getDbDomain().toLowerCase();

        // add by liujr 2015/10/12
        if (cCompareInfo.getPdmDbName() != null && cCompareInfo.getPdmDbName().length() > 0) {
            strPdmDomain = cCompareInfo.getPdmDbName();
        }

        TreeMap<String, AiTable> map_pdm = new TreeMap<String, AiTable>();
        if (domain_table.containsKey(strPdmDomain)) {
            map_pdm = domain_table.get(strPdmDomain);
            
        }
        
        LinkedHashMap<String, AiTable> map_develop = getTables(conn_develop, cCompareInfo);
        LOGGER.info("map_db size:" + map_develop.size());
        // CheckSubTables(map_develop);
        LOGGER.info("map_pdm size:" + map_pdm.size());

        // 遍历开发库Map
        LOGGER.info("begin compare tables... ");
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();

            AiTable table_develop = map_develop.get(key_table);// 获得PDM中的表

            if (table_develop.getTable_type().equalsIgnoreCase("view")) {
                CompareUtil.appendTable(null, table_develop, null, null, 2, sb, vSql, cCompareInfo.getDbDomain());
                cCompareResult.iCount2++;
                continue;
            }
            LOGGER.debug("key_table=" + key_table);
            key_table = CompareUtil.getParentTableName(key_table);
            LOGGER.debug("parent_table=" + key_table);
            // 尝试从比对库中获得同名表
            AiTable table_pdm = map_pdm.get(key_table);
            if (table_pdm == null) {
                // 如果获得表为空，说明PDM不存在，比对库存在
                CompareUtil.appendTable(table_pdm, table_develop, null, null, 2, sb, vSql, cCompareInfo.getDbDomain());
                cCompareResult.iCount2++;
            } else {
                // 表相同，判断字段、字段类型、字段长度
                for (Iterator iter_column = table_develop.columns.keySet().iterator(); iter_column.hasNext();) {
                    String key_column = (String) iter_column.next();
                    // 获得开发库中的列
                    AiColumn column_develop = (AiColumn) table_develop.columns.get(key_column);
                    // 尝试从生产库中获得同名列
                    AiColumn column_pdm = (AiColumn) table_pdm.columns.get(key_column);
                    if (column_pdm == null) {
                        // 如果列名为空，说明PDM不存在，比对库存在
                        CompareUtil.appendTable(table_pdm, table_develop, column_develop, null, 4, sb, vSql,
                                cCompareInfo.getDbDomain());
                        cCompareResult.iCount4++;
                    } else {// 说明两者都存在
                        /*
                         * if (!column_develop.getDataType().equalsIgnoreCase(
                         * column_pdm.getDataType()))// 字段类型不一致 {
                         * CompareUtil.append(table_develop, column_pdm,
                         * column_develop, 5, sb); iCount5++; } if
                         * (!column_develop
                         * .getNullable().equalsIgnoreCase(column_pdm
                         * .getNullable()))// 是否为空不一致 {
                         * CompareUtil.append(table_develop, column_pdm,
                         * column_develop, 6, sb); iCount6++; }
                         */
                    }
                }
            }
        }
        // 遍历生产库Map
        for (Iterator iter_table = map_pdm.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();

            AiTable table_pdm = map_pdm.get(key_table);// 尝试从比对库中获得同名表
            AiTable table_develop = map_develop.get(key_table);// 获得PDM中的表
            if (table_develop == null) { // 如果获得表为空，说明PDM存在，比对库不存在
                String mStrSql = SqlCreateTable.createTableSqlFromPdm( table_pdm, cCompareInfo);
                CompareUtil.addSqlVector(vSql, mStrSql, 1, key_table);
                CompareUtil.appendTable(table_pdm, table_develop, null, null, 1, sb, vSql, cCompareInfo.getDbDomain());

                cCompareResult.iCount1++;
            } else {
                compareFromPdm(table_pdm, table_develop, cCompareInfo);
            }

            for (Iterator iter_table2 = map_develop.keySet().iterator(); iter_table2.hasNext();) {
                String key_table2 = (String) iter_table2.next();

                if (CompareUtil.isSubTable(key_table, key_table2)) {
                    // 进入分表
                    table_develop = map_develop.get(key_table2);
                    // 比较
                    compareFromPdm(table_pdm, table_develop, cCompareInfo);
                }
            }

        }
        map_pdm = null;
        map_develop = null;
    }

    /**
     * 
     * @category 数据库与数据库比对
     * @author 吴平福
     * @param transaction
     * @param inDomain
     * @param cCompareInfo
     * @return
     * @throws Exception
     *             update 2016年5月2日
     */
    @Override
    public void doWork(Connection conn_pdm, Connection conn_develop, CompareInfo cCompareInfo) throws Exception {

        // PDM数据库连接
        String strPdmDomain = cCompareInfo.getDbDomain();

        if (cCompareInfo.getPdmDbName() != null && cCompareInfo.getPdmDbName().length() > 0) {
            strPdmDomain = cCompareInfo.getPdmDbName();
        }
        LinkedHashMap<String, AiTable> map_pdm = getTables(conn_pdm,  cCompareInfo);
        // 开发数据库连接

        LinkedHashMap<String, AiTable> map_develop = getTables(conn_develop,  cCompareInfo);
        // logger.info(map_develop.size());
        // CheckSubTables(map_develop);
        // logger.info(map_develop.size());

        // 遍历开发库Map
        LOGGER.info("begin compare tables... ");
        for (Iterator iter_table = map_develop.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();

            AiTable table_develop = map_develop.get(key_table);// 获得PDM中的表

            if (table_develop.getTable_type().equalsIgnoreCase("view")) {
                CompareUtil.appendTable(null, table_develop, null, null, 2, sb, vSql, cCompareInfo.getDbDomain());
                cCompareResult.iCount2++;
                continue;
            }
            LOGGER.debug("key_table=" + key_table);
            key_table = CompareUtil.getParentTableName(key_table);
            LOGGER.debug("parent_table=" + key_table);
            AiTable table_pdm = map_pdm.get(key_table);// 尝试从比对库中获得同名表
            if (table_pdm == null) {
                // 如果获得表为空，说明PDM不存在，比对库存在
                CompareUtil.appendTable(table_pdm, table_develop, null, null, 2, sb, vSql, cCompareInfo.getDbDomain());
                cCompareResult.iCount2++;
            } else {
                // 表相同，判断字段、字段类型、字段长度
                for (Iterator iter_column = table_develop.columns.keySet().iterator(); iter_column.hasNext();) {
                    String key_column = (String) iter_column.next();
                    AiColumn column_develop = (AiColumn) table_develop.columns.get(key_column);// 获得开发库中的列
                    AiColumn column_pdm = (AiColumn) table_pdm.columns.get(key_column);// 尝试从生产库中获得同名列
                    if (column_pdm == null) {
                        // 如果列名为空，说明PDM不存在，比对库存在
                        CompareUtil.appendTable(table_pdm, table_develop, column_develop, null, 4, sb, vSql,
                                cCompareInfo.getDbDomain());
                        cCompareResult.iCount4++;
                    } else {
                        // 说明两者都存在
                        /*
                         * if (!column_develop.getDataType().equalsIgnoreCase(
                         * column_pdm.getDataType()))// 字段类型不一致 {
                         * CompareUtil.append(table_develop, column_pdm,
                         * column_develop, 5, sb); iCount5++; } if
                         * (!column_develop
                         * .getNullable().equalsIgnoreCase(column_pdm
                         * .getNullable()))// 是否为空不一致 {
                         * CompareUtil.append(table_develop, column_pdm,
                         * column_develop, 6, sb); iCount6++; }
                         */
                    }
                }
            }
        }
        // 遍历生产库Map
        for (Iterator iter_table = map_pdm.keySet().iterator(); iter_table.hasNext();) {
            String key_table = (String) iter_table.next();

            // 尝试从比对库中获得同名表
            AiTable table_pdm = map_pdm.get(key_table);
            // 获得PDM中的表
            AiTable table_develop = map_develop.get(key_table);
            if (table_develop == null) {
                // 如果获得表为空，说明PDM存在，比对库不存在
                String mStrSql = SqlCreateTable.getCreateTableSqlFromDb(conn_pdm, strPdmDomain, key_table,cCompareInfo);
                CompareUtil.addSqlVector(vSql, mStrSql, 1, key_table);
                CompareUtil.appendTable(table_pdm, table_develop, null, null, 1, sb, vSql, cCompareInfo.getDbDomain());
                cCompareResult.iCount1++;
            } else {
                compareFromPdm(table_pdm, table_develop, cCompareInfo);
            }

            for (Iterator iter_table2 = map_develop.keySet().iterator(); iter_table2.hasNext();) {
                String key_table2 = (String) iter_table2.next();

                if (CompareUtil.isSubTable(key_table, key_table2)) {
                    // 进入分表
                    table_develop = map_develop.get(key_table2);
                    // 比较
                    compareFromPdm(table_pdm, table_develop, cCompareInfo);
                }
            }

        }
    }

    /**
     * 
     * @category @author 吴平福
     * @param table_pdm
     * @param table_develop
     * @throws Exception
     *             update 2016年5月3日
     */
    private void compareFromPdm(AiTable table_pdm, AiTable table_develop, CompareInfo cCompareInfo) throws Exception {

        // 表相同，判断字段、字段类型、字段长度
        for (Iterator iter_column = table_pdm.columns.keySet().iterator(); iter_column.hasNext();) {
            String key_column = (String) iter_column.next();
            // 获得开发库中的列
            AiColumn column_develop = (AiColumn) table_develop.columns.get(key_column);
            // 尝试从生产库中获得同名列
            AiColumn column_pdm = (AiColumn) table_pdm.columns.get(key_column);
            if (column_develop == null) {
                // 如果列名为空，说明PDM存在，比对库不存在
                CompareUtil.appendTable(table_pdm, table_develop, column_pdm, null, 3, sb, vSql,
                        cCompareInfo.getDbDomain());
                cCompareResult.iCount3++;
            } else {
                // 说明两者都存在
                if (!column_pdm.getDataType().equalsIgnoreCase(column_develop.getDataType()))// 字段类型不一致
                {
                    // 判断pdm中是否存在没有指明数据长度的数据类型
                    if ((column_pdm.getDataLength() == 0) && ((column_pdm.getDataType().contains("int")) ||
                            (column_pdm.getDataType().contains("decimal")) ||
                            (column_pdm.getDataType().contains("number")) ||
                            (column_pdm.getDataType().contains("char")))) {
                        LOGGER.error("PDM have error datatype: " + column_pdm.getDataType());
                    } else {
                        // 数据类型为decimal或number数据类型，但是精度为零，统一去掉精度
                        String pdmType = column_pdm.getDataType();
                        String developType = column_develop.getDataType();
                        if ((developType.contains("decimal")) || (developType.contains("number"))) {

                            if (developType.contains(",")) {
                                developType = developType.replaceAll(",0\\)", ")");
                            }
                        }
                        if ((pdmType.contains("decimal")) || (pdmType.contains("number"))) {

                            if (pdmType.contains(",")) {
                                pdmType = pdmType.replaceAll(",0\\)", ")");
                            }
                        }
                        if (!pdmType.equalsIgnoreCase(developType)) {
                            CompareUtil.appendTable(table_pdm, table_develop, column_pdm, column_develop, 5, sb, vSql,
                                    cCompareInfo.getDbDomain());
                            cCompareResult.iCount5++;
                        }
                    }
                }
                // 是否为空不一致
                if (!column_pdm.getNullable().equalsIgnoreCase(column_develop.getNullable())) {
                    CompareUtil.appendTable(table_pdm, table_develop, column_pdm, column_develop, 6, sb, vSql,
                            cCompareInfo.getDbDomain());
                    cCompareResult.iCount6++;
                }
                // 默认值
                if (column_develop.getColumnDefault() == null && column_pdm.getColumnDefault() != null) {
                    CompareUtil.appendTable(table_pdm, table_develop, column_pdm, column_develop, 7, sb, vSql,
                            cCompareInfo.getDbDomain());
                    cCompareResult.iCount7++;
                }
                if (column_develop.getColumnDefault() != null && column_pdm.getColumnDefault() == null) {
                    CompareUtil.appendTable(table_pdm, table_develop, column_pdm, column_develop, 7, sb, vSql,
                            cCompareInfo.getDbDomain());
                    cCompareResult.iCount7++;
                }
                if (column_develop.getColumnDefault() != null && column_pdm.getColumnDefault() != null) {

                    if (!column_develop.getColumnDefault().trim()
                            .equalsIgnoreCase(column_pdm.getColumnDefault().trim())) {
                        CompareUtil.appendTable(table_pdm, table_develop, column_pdm, column_develop, 7, sb, vSql,
                                cCompareInfo.getDbDomain());
                        cCompareResult.iCount7++;
                    }
                }

                // 是否自增长不同
                if (column_develop.getExtra() == null && column_pdm.getExtra() != null) {
                    CompareUtil.appendTable(table_pdm, table_develop, column_pdm, column_develop, 8, sb, vSql,
                            cCompareInfo.getDbDomain());
                    cCompareResult.iCount8++;
                }
                if (column_develop.getExtra() != null && column_pdm.getExtra() == null) {
                    CompareUtil.appendTable(table_pdm, table_develop, column_pdm, column_develop, 8, sb, vSql,
                            cCompareInfo.getDbDomain());
                    cCompareResult.iCount8++;
                }
                if (column_develop.getExtra() != null && column_pdm.getExtra() != null) {
                    if (!column_develop.getExtra().equalsIgnoreCase(column_pdm.getExtra())) {
                        CompareUtil.appendTable(table_pdm, table_develop, column_pdm, column_develop, 8, sb, vSql,
                                cCompareInfo.getDbDomain());
                        cCompareResult.iCount8++;
                    }
                }

                // 比较顺序
                if (column_develop.getOrdinal_position() != column_pdm.getOrdinal_position()) {
                    /*
                     * logger.debug(table_develop.tableName + ":column_develop:"
                     * + column_develop.getColumnName() + " " +
                     * column_develop.getOrdinal_position());
                     * logger.debug(table_pdm.tableName + ":column_pdm:" +
                     * column_pdm.getColumnName() + " " +
                     * column_pdm.getOrdinal_position());
                     */
                    if (!CompareUtil.getParentTableName(table_develop.getTableName()).equalsIgnoreCase(
                            table_develop.getTableName())) {
                        CompareUtil.appendTable(table_pdm, table_develop, column_pdm, column_develop, 9, sb, vSql,
                                cCompareInfo.getDbDomain());
                        cCompareResult.iCount9++;
                    }
                }

                // 比较CHARACTER_SET_NAME 字符集
                if (column_develop.getCHARACTER_SET_NAME() == null && column_pdm.getCHARACTER_SET_NAME() != null) {
                    CompareUtil.appendTable(table_pdm, table_develop, column_pdm, column_develop, 10, sb, vSql,
                            cCompareInfo.getDbDomain());
                    cCompareResult.iCount10++;
                }
                if (column_develop.getCHARACTER_SET_NAME() != null && column_pdm.getCHARACTER_SET_NAME() == null) {
                    CompareUtil.appendTable(table_pdm, table_develop, column_pdm, column_develop, 10, sb, vSql,
                            cCompareInfo.getDbDomain());
                    cCompareResult.iCount10++;
                }
                if (column_develop.getCHARACTER_SET_NAME() != null && column_pdm.getCHARACTER_SET_NAME() != null) {
                    if (!column_develop.getCHARACTER_SET_NAME().equalsIgnoreCase(column_pdm.getCHARACTER_SET_NAME())) {
                        CompareUtil.appendTable(table_pdm, table_develop, column_pdm, column_develop, 10, sb, vSql,
                                cCompareInfo.getDbDomain());
                        cCompareResult.iCount10++;
                    }
                }
            }
        }

    }

    /**
     * 
     * @category @author 吴平福
     * @param transaction
     * @param inDomain
     * @param cCompareInfo
     * @return
     * @throws Exception
     *             update 2016年5月2日
     */
    private LinkedHashMap<String, AiTable> getTablesMysql(Connection transaction, String inDomain,
            CompareInfo cCompareInfo)
            throws Exception {
        LOGGER.info("getTablesMysql:" + inDomain);
        String sSql = " select t1.TABLE_NAME,t1.COLUMN_NAME,t1.IS_NULLABLE,t1.COLUMN_TYPE,t1.Column_Default,t2.table_type,t1.extra,t1.ordinal_position,t1.CHARACTER_SET_NAME,COLLATION_NAME  "
                + " from (select * from information_schema.COLUMNS where  table_schema=? )t1, (select * from information_schema.tables where  table_schema=?) t2 "
                + " where t1.table_name=t2.table_name ";
        if (cCompareInfo.getStrExcludeTable() != null && cCompareInfo.getStrExcludeTable().length() > 0) {
            String[] excludeTables = cCompareInfo.getStrExcludeTable().split(",");
            for (int i = 0; i < excludeTables.length; i++) {
                sSql += " and t1.table_name not like '" + excludeTables[i] + "%' ";
            }
        }
        if (cCompareInfo.getIncludesubtable().equalsIgnoreCase("0"))
        {
            sSql +=" and t1.table_name not REGEXP '[a-zA-Z]_[0-9]'";
        }
        
        if (cCompareInfo.getSqlCondDb()!=null && cCompareInfo.getSqlCondDb().length()>0)
        {
            sSql +=" and t1.table_name REGEXP '[a-zA-Z]_[0-9]'";
        }
        
        if (cCompareInfo.getSqlCondPdm()!=null && cCompareInfo.getSqlCondPdm().length()>0)
        {
            sSql +=" and t1.table_name not REGEXP '[a-zA-Z]_[0-9]'";
        }

        sSql += " order By table_name,ordinal_position";
        PreparedStatement pstmt = transaction.prepareStatement(sSql);
        pstmt.setString(1, inDomain);
        pstmt.setString(2, inDomain);
        LOGGER.debug(sSql);
        pstmt.setQueryTimeout(AiDBUtil.EXECSQLTIMEOUT);

        ResultSet rs = pstmt.executeQuery();
        LinkedHashMap<String, AiTable> map = new LinkedHashMap<String, AiTable>();
        String tableName = "";
        AiTable table = null;
        while (rs.next()) {
            if (!tableName.equals(rs.getString("table_name").toLowerCase().trim())) {
                String columnType = "";
                if (rs.getString("COLUMN_TYPE") != null) {
                    columnType = rs.getString("COLUMN_TYPE").replaceAll("\\s*", "");
                    columnType = columnType.toLowerCase().trim();
                }
                tableName = rs.getString("table_name").toLowerCase().trim();
                table = new AiTable(tableName, rs.getString("table_type").toLowerCase().trim());

                AiColumn column = new AiColumn(rs.getString("Column_Name").toLowerCase().trim(),
                        columnType, rs.getString("IS_NULLABLE").toLowerCase()
                                .trim(),
                        CompareUtil.showDefaultValue(rs.getString("Column_Default")));
                column.setExtra(rs.getString("extra"));
                column.setOrdinal_position(rs.getInt("ordinal_position"));
                column.setCHARACTER_SET_NAME(rs.getString("CHARACTER_SET_NAME"));
                column.setCOLLATION_NAME(rs.getString("COLLATION_NAME"));
                table.columns.put(column.getColumnName(), column);
                map.put(tableName, table);
            } else {

                String columnType = "";
                if (rs.getString("COLUMN_TYPE") != null) {
                    columnType = rs.getString("COLUMN_TYPE").replaceAll("\\s*", "");
                    columnType = columnType.toLowerCase().trim();
                }
                AiColumn column = new AiColumn(rs.getString("Column_Name").toLowerCase().trim(),
                        columnType, rs.getString("IS_NULLABLE").toLowerCase().trim(),
                        CompareUtil.showDefaultValue(rs.getString("Column_Default")));
                column.setExtra(rs.getString("extra"));
                column.setOrdinal_position(rs.getInt("ordinal_position"));
                column.setCHARACTER_SET_NAME(rs.getString("CHARACTER_SET_NAME"));
                column.setCOLLATION_NAME(rs.getString("COLLATION_NAME"));
                table.columns.put(column.getColumnName(), column);
            }
        }
        if (null != rs)
            rs.close();
        // transaction.finalize();
        return map;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jpf.ci.dbs.compare.AbstractDbCompare#GetHtmlName()
     */
    @Override
    String getHtmlName() {
        // TODO Auto-generated method stub
        return "compare_table.html";
    }

    @Override
    protected void insertResult(CompareInfo cCompareInfo) {
        SaveCompareResult.insertTableResult(cCompareInfo, cCompareResult);
    }

    @Override
    protected String showResult() {
        return cCompareResult.showTableResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jpf.ci.dbs.compare.AbstractDbCompare#GetMailTitle()
     */
    @Override
    String getMailTitle() {
        // TODO Auto-generated method stub
        return "数据库表比对带分表结果(自动发出)";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jpf.ci.dbs.compare.AbstractDbCompare#GetExecSqlHtmlName()
     */
    @Override
    String getExecSqlHtmlName() {
        // TODO Auto-generated method stub
        return "compare_table2.html";
    }

    @Override
    String getErrorHtmlName() {
        // TODO Auto-generated method stub
        return "ErrorInformation.html";
    }

    /**
     * 
     * @category @author 吴平福
     * @param transaction
     * @param inDomain
     * @param cCompareInfo
     * @return
     * @throws Exception
     *             update 2016年5月2日
     */
    private LinkedHashMap<String, AiTable> getTablesOracle(
            Connection transaction, String inDomain, CompareInfo cCompareInfo)
            throws Exception {
        LOGGER.info("getTablesOracle:" + inDomain);
        LinkedHashMap<String, AiTable> map = new LinkedHashMap<String, AiTable>();

        String sSql = " select TABLE_NAME,COLUMN_NAME,(case when NULLABLE='Y' then 'yes' else 'no' end) IS_NULLABLE,"
                + "(case WHEN DATA_TYPE='NUMBER' then DATA_TYPE||'('||DATA_PRECISION||')'  when substr(DATA_TYPE,0,9)='TIMESTAMP' then DATA_TYPE  when DATA_TYPE='DATE' then DATA_TYPE when DATA_TYPE='CLOB' then DATA_TYPE when DATA_TYPE='BLOB' then DATA_TYPE   else DATA_TYPE||'('||DATA_LENGTH||')'  end)  COLUMN_TYPE,"
                + " DATA_DEFAULT Column_Default,COLUMN_ID ordinal_position,CHARACTER_SET_NAME  from USER_TAB_COLUMNS ";
        if (cCompareInfo.getStrExcludeTable() != null
                && cCompareInfo.getStrExcludeTable().length() > 0) {
            String[] excludeTables = cCompareInfo.getStrExcludeTable()
                    .split(",");
            for (int i = 0; i < excludeTables.length; i++) {
                sSql += " and t1.table_name not like '" + excludeTables[i]
                        + "%' ";
            }
        }
        sSql += " order By TABLE_NAME,COLUMN_ID";

        PreparedStatement pstmt = transaction.prepareStatement(sSql);

        LOGGER.debug(sSql);
        pstmt.setQueryTimeout(AiDBUtil.EXECSQLTIMEOUT);

        ResultSet rs = pstmt.executeQuery();

        String tableName = "";
        AiTable table = null;
        while (rs.next()) {
            if (!tableName.equals(rs.getString("table_name").toLowerCase()
                    .trim())) {
                String columnType = "";
                if (rs.getString("COLUMN_TYPE") != null) {
                    columnType = rs.getString("COLUMN_TYPE").replaceAll("\\s*", "");

                    columnType = columnType.toLowerCase().trim();
                }
                tableName = rs.getString("table_name").toLowerCase().trim();
                // table = new Table(tableName,
                // rs.getString("table_type").toLowerCase().trim());
                table = new AiTable(tableName, "");

                AiColumn column = new AiColumn(rs.getString("Column_Name").toLowerCase().trim(), columnType,
                        rs.getString("IS_NULLABLE").toLowerCase().trim(),
                        CompareUtil.showDefaultValue(rs.getString("Column_Default")));
                // column.setExtra(rs.getString("extra"));
                column.setOrdinal_position(rs.getInt("ordinal_position"));
                column.setCHARACTER_SET_NAME(rs
                        .getString("CHARACTER_SET_NAME"));
                // column.setCOLLATION_NAME(rs.getString("COLLATION_NAME"));
                table.columns.put(column.getColumnName(), column);
                map.put(tableName, table);
            } else {

                String columnType = "";
                if (rs.getString("COLUMN_TYPE") != null) {
                    columnType = rs.getString("COLUMN_TYPE").replaceAll(
                            "\\s*", "");
                    columnType = columnType.toLowerCase().trim();
                }
                AiColumn column = new AiColumn(rs.getString("Column_Name")
                        .toLowerCase().trim(), columnType, rs
                                .getString("IS_NULLABLE").toLowerCase().trim(),
                        CompareUtil.showDefaultValue(rs
                                .getString("Column_Default")));
                // column.setExtra(rs.getString("extra"));
                column.setOrdinal_position(rs.getInt("ordinal_position"));
                column.setCHARACTER_SET_NAME(rs
                        .getString("CHARACTER_SET_NAME"));
                // column.setCOLLATION_NAME(rs.getString("COLLATION_NAME"));
                table.columns.put(column.getColumnName(), column);
            }
        }

        // transaction.finalize();
        LOGGER.debug("read tables from db:=" + map.size());
        if (null != pstmt) {
            pstmt.close();
        }
        return map;

    }

    /**
     * 
     * @category @author 吴平福
     * @param transaction
     * @param inDomain
     * @param cCompareInfo
     * @return
     * @throws Exception
     *             update 2016年5月2日
     */
    private LinkedHashMap<String, AiTable> getTables(Connection transaction,
             CompareInfo cCompareInfo) throws Exception {
        String strPdmDomain="";
        if (cCompareInfo.getPdmDbName() != null && cCompareInfo.getPdmDbName().length() > 0) {
            strPdmDomain = cCompareInfo.getPdmDbName();
        }
        if (cCompareInfo.getDbType().equalsIgnoreCase("oracle")) {
            return getTablesOracle(transaction, strPdmDomain, cCompareInfo);
        } else {
            return getTablesMysql(transaction, strPdmDomain, cCompareInfo);
        }
    }
}
