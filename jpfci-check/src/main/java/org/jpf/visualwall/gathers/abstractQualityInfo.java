/**
 * @author 吴平福 E-mail:wupf@asiainfo.com
 * @version 创建时间：2015年5月2日 下午2:31:28 类说明
 */

package org.jpf.visualwall.gathers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.visualwall.WallsDbConn;

import org.jpf.utils.dbsql.AiDBUtil;
import org.jpf.utils.mails.AiMail;


/**
 * 
 */
public abstract class abstractQualityInfo {
    private static final Logger logger = LogManager.getLogger();

    /**
     * 
     */
    public abstractQualityInfo() {
        // TODO Auto-generated constructor stub
        getQualityInfo();
    }

    abstract String getDBUsr();

    abstract String getDBPwd();

    abstract String getDBUrl();

    abstract String getQuerySql();

    abstract String getAreaName();

    abstract String getPrjOwner();

    
    /**
     * 
     * @category test metrics
     * @author 吴平福 
     * @param strPrjids
     * @return
     * update 2016年8月19日
     */
    protected String getTestMetricsSql(String strPrjids) {
        return "select t1.scope,t1.qualifier,t1.root_id prj_id,FROM_UNIXTIME( t2.build_date/1000, '%Y-%m-%d' ) build_date, t1.name prj_name, t1.kee,"
                + " min(CASE t3.metric_id WHEN 31 THEN t3.value/100  END)test_execution_time,"
                + "             min(CASE t3.metric_id WHEN 30 THEN t3.value  END)tests"
                + ",min(CASE t3.metric_id WHEN 32 THEN t3.value  END)test_errors,"
                + "min(CASE t3.metric_id WHEN 33 THEN t3.value  END)skipped_tests,"
                + "min(CASE t3.metric_id WHEN 34 THEN t3.value  END)test_failures"
                + "             from (select * from projects where root_id in (" + strPrjids
                + ") union all select * from projects where root_id in (select id from projects where root_id in ("
                + strPrjids + "))) t1,snapshots t2,project_measures t3"
                + "  where  t1.enabled=1 and t1.id=t2.project_id  and t2.islast=1  and t2.id=t3.snapshot_id"
                + "             AND t3.rule_id IS NULL  and t3.metric_id in(30,31,32,33,34) group by t1.id order by t1.name";
    }

    /**
     * 
     * @category 需要合并的项目
     * @author 吴平福
     * @return update 2016年5月18日
     */
    protected String[][] getMerge() {
        return null;
    }

    /**
     * 
     * @category 获取SONAR数据库连接
     * @author 吴平福
     * @return
     * @throws Exception update 2016年5月18日
     */
    protected Connection getSourceConn() throws Exception {
        try {
            String driver = "com.mysql.jdbc.Driver";
            Class.forName(driver).newInstance();
            logger.info(getAreaName() + ":" + getDBUrl());
            return DriverManager.getConnection(getDBUrl(), getDBUsr(), getDBPwd());
        } catch (Exception ex) {
            AiMail.sendMail(getPrjOwner(),
                    getAreaName() + " :  " + getDBUrl() + "\n" + ex.getMessage(), "GBK", "连接数据库失败");
            throw ex;
        }

    }

    /**
     * 
     * @category @author 吴平福 update 2016年5月18日
     */
    public void getQualityInfo() {
        Connection conn_sonar = null;
        Connection conn_visualwall = null;
        try {
            conn_sonar = this.getSourceConn();
            conn_visualwall = WallsDbConn.getInstance().getConn();
            // init
            String strSql = "delete from  bss_rpt_tmp";
            AiDBUtil.execUpdateSql(conn_visualwall, strSql);

            Statement stmt1 = conn_sonar.createStatement();

            ResultSet rSet = stmt1.executeQuery(getQuerySql());
            strSql = "insert into bss_rpt_tmp (is_last,kee,areaname,prj_id,build_date,prj_name,blocker_violations,critical_violations,major_violations,prj_lines,tests,test_success_density,branch_coverage,line_coverage,test_execution_time,state_date) "
                    + "values(1,?,'tmp',?,?,?,?,?,?,?,?,?,?,?,?,now())";

            PreparedStatement pstmt = conn_visualwall.prepareStatement(strSql);
            int iPrjCount = 0;
            while (rSet.next()) {
                // strSql = getInsertSql(rSet);
                // JpfDbUtils.execUpdateSql(conn_visualwall, strSql);
                logger.debug(rSet.getString("kee") + ":" + rSet.getLong("prj_id"));
                pstmt.setString(1, rSet.getString("kee"));
                pstmt.setLong(2, rSet.getLong("prj_id"));
                pstmt.setString(3, rSet.getString("build_date"));
                pstmt.setString(4, rSet.getString("prj_name"));
                pstmt.setLong(5, rSet.getLong("blocker_violations"));
                pstmt.setLong(6, rSet.getLong("critical_violations"));
                pstmt.setLong(7, rSet.getLong("major_violations"));
                pstmt.setLong(8, rSet.getLong("prj_lines"));
                pstmt.setLong(9, rSet.getLong("tests"));
                pstmt.setDouble(10, rSet.getDouble("test_success_density"));
                pstmt.setDouble(11, rSet.getDouble("branch_coverage"));
                pstmt.setDouble(12, rSet.getDouble("line_coverage"));
                pstmt.setDouble(13, rSet.getDouble("test_execution_time"));

                pstmt.addBatch();
                iPrjCount++;
            }
            pstmt.executeBatch();
            if (!conn_visualwall.getAutoCommit()) {
                conn_visualwall.commit();
            }
            logger.info("insert into tmp table record count:" + iPrjCount);

            if (getMerge() != null) {
                String[][] strMerge = getMerge();
                for (int i = 0; i < strMerge.length; i++) {
                    String[] aStrings = strMerge[i];

                    strSql = "insert into bss_rpt_tmp(areaname,prj_id,build_date,prj_name,is_last,blocker_violations,critical_violations,major_violations,prj_lines,tests,test_success_density,branch_coverage,line_coverage,state_date) "
                            + " select 'tmp'," + aStrings[0] + ",build_date,'" + aStrings[2]
                            + "',1"
                            + ",ifnull(sum(blocker_violations),0),ifnull(sum(critical_violations),0),ifnull(sum(major_violations),0) ,ifnull(sum(prj_lines),0),ifnull(sum(tests),0),ifnull(avg(test_success_density),0)"
                            + ",ifnull(avg(branch_coverage),0),ifnull(avg(line_coverage),0),now()"
                            + " from bss_rpt_tmp where areaname='tmp' and is_last=1 AND prj_id in ("
                            + aStrings[1] + ")";
                    logger.debug(strSql);
                    AiDBUtil.execUpdateSql(conn_visualwall, strSql);

                    strSql = "delete from  bss_rpt_tmp where areaname='tmp' AND prj_id in ("
                            + aStrings[1] + ")";
                    logger.debug(strSql);
                    AiDBUtil.execUpdateSql(conn_visualwall, strSql);

                }
            }

            // 启动发现新项目
            calMissPrj(conn_visualwall);
            calMissPrj2(conn_visualwall);
            // 删除没有配置的项目
            strSql = "delete from bss_rpt_tmp  where prj_id>0 and  areaname='tmp' and prj_id not in (select prj_id from bss_prj t2 where t2.areaname in("
                    + getAreaName() + "))";
            logger.debug(strSql);
            AiDBUtil.execUpdateSql(conn_visualwall, strSql);

            //
            strSql = "update bss_rpt_tmp t1,bss_prj t2 set t1.areaname=t2.areaname where t1.areaname='tmp' and t1.prj_id=t2.prj_id and t2.areaname in ("
                    + getAreaName() + ")";
            logger.debug(strSql);
            AiDBUtil.execUpdateSql(conn_visualwall, strSql);

            // 各个模块特殊的处理
            doPrivate(conn_visualwall);

            strSql = "update bss_rpt_tmp t1,bss_rpt_date t2 set t1.refer_value1=t2.refer_value,t1.refer_value2=t2.refer_value2  ,t1.kpi=cal_kpi(t1.blocker_violations,t1.critical_violations,t1.major_violations,t1.branch_coverage, t2.refer_value , t2.refer_value2)  where t1.is_last=1 and t1.areaname=t2.areaname and t2.areaname in ("
                    + getAreaName()
                    + ") and DATE_FORMAT(t1.state_date,'%m-%d-%Y')=DATE_FORMAT(t2.build_date,'%m-%d-%Y') and t1.prj_id=t2.prj_id";
            logger.debug(strSql);
            AiDBUtil.execUpdateSql(conn_visualwall, strSql);

            
            strSql = "delete from  bss_rpt where areaname in (" + getAreaName()
                    + ") and (prj_id,build_date)  in  (select prj_id,build_date from bss_rpt_tmp where areaname in ("
                    + getAreaName() + "))";
            logger.debug(strSql);
            AiDBUtil.execUpdateSql(conn_visualwall, strSql);


            strSql = "insert into bss_rpt select * from bss_rpt_tmp t1 where (t1.prj_id,build_date) not in  (select prj_id,build_date from bss_rpt where areaname in ("
                    + getAreaName() + "))";
            logger.debug(strSql);
            AiDBUtil.execUpdateSql(conn_visualwall, strSql);
            /*
            strSql = "update  bss_rpt set is_last=0 where is_last=1 and prj_id<>0  and areaname in("
                    + getAreaName() + ")";
            logger.debug(strSql);
            JpfDbUtils.execUpdateSql(conn_visualwall, strSql);
            
            
            strSql = "update bss_rpt set is_last=1 where DATE_FORMAT(state_date,'%Y-%m-%d')=current_date and areaname in ("
                    + getAreaName() + ")  ";
            logger.debug(strSql);
            JpfDbUtils.execUpdateSql(conn_visualwall, strSql);
            */
            
            strSql = "update bss_rpt t2 set t2.is_last=0 where t2.areaname in ("
                    + getAreaName() + ") and t2.is_last=1 and (t2.build_date,t2.prj_id) not in"
                    +"  (select build_date,prj_id from (select max(build_date)build_date,prj_id from bss_rpt where areaname in("
                    + getAreaName() + ") group by prj_id)as x )";
            
            logger.debug(strSql);
            AiDBUtil.execUpdateSql(conn_visualwall, strSql);
            
            // getTestInfo(conn_sonar, conn_visualwall);

        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
            logger.error(ex);
        } finally {
            AiDBUtil.doClear(conn_sonar);
            AiDBUtil.doClear(conn_visualwall);
        }
        logger.info("success");
    }


    /**
     * 
     * @category 每个采集模块特有的操作
     * @author 吴平福 
     * @param conn
     * @throws Exception
     * update 2016年8月19日
     */
    protected void doPrivate(Connection conn) throws Exception {

    }



    /**
     * 
     * @category 遗漏的模块
     * @author 吴平福
     * @param conn_visualwall update 2016年5月18日
     */
    private void calMissPrj2(Connection conn_visualwall) {
        try {
            String strSql =
                    "select * from bss_prj where prj_id not in (select prj_id from bss_rpt_tmp) and areaname in("
                            + getAreaName() + ")  order by areaname,prj_name";
            ResultSet rsResultSet = AiDBUtil.execSqlQuery(conn_visualwall, strSql);
            StringBuffer sb = new StringBuffer();
            sb.append(getAreaName()).append("\r\n");
            while (rsResultSet.next()) {
                sb.append(rsResultSet.getString("areaname")).append(":").append(rsResultSet.getString("prj_name")).append(":")
                        .append(rsResultSet.getString("prj_id")).append("\r\n");
            }
            rsResultSet.close();
            logger.warn("配置了没有找到的模块");
            logger.warn(sb);
            // JpfMail.sendMail(getPrjOwner(), sb.toString(),
            // "GBK",getAreaName()+"遗漏的模块");
            sb.setLength(0);

            strSql = "insert into jpf_miss_module(areaname,prj_name,prj_id) select '"
                    + getAreaName().replaceAll("'", "")
                    + "',prj_name,prj_id from bss_prj where prj_id not in (select prj_id from bss_rpt_tmp) and areaname in("
                    + getAreaName() + ") ";
            logger.info(strSql);
            AiDBUtil.execUpdateSql(conn_visualwall, strSql);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * 
     * @category 遗漏的模块
     * @author 吴平福
     * @param conn_visualwall update 2016年5月18日
     */
    private void calMissPrj(Connection conn_visualwall) {
        try {
            String strSql =
                    "select * from bss_rpt_tmp where prj_id not in (select prj_id from bss_prj where areaname in("
                            + getAreaName() + ")) ";

            ResultSet rsResultSet = AiDBUtil.execSqlQuery(conn_visualwall, strSql);
            StringBuffer sb = new StringBuffer();
            sb.append(getAreaName()).append("\r\n");
            while (rsResultSet.next()) {
                sb.append(rsResultSet.getString("kee")).append(":")
                        .append(rsResultSet.getString("prj_id")).append("\r\n");
            }
            rsResultSet.close();
            logger.warn("没有配置的模块");
            logger.warn(sb);
            // JpfMail.sendMail(getPrjOwner(), sb.toString(),
            // "GBK",getAreaName()+"遗漏的模块");
            sb.setLength(0);

            strSql = "delete from  jpf_miss_module  where areaname ='"
                    + getAreaName().replaceAll("'", "") + "'";
            logger.info(strSql);
            AiDBUtil.execUpdateSql(conn_visualwall, strSql);

            strSql = "insert into jpf_miss_module(kee,areaname,prj_name,prj_id) select kee,'"
                    + getAreaName().replaceAll("'", "")
                    + "',prj_name,prj_id from bss_rpt_tmp where prj_id not in (select prj_id from bss_prj where areaname in("
                    + getAreaName() + ")) ";
            logger.info(strSql);
            AiDBUtil.execUpdateSql(conn_visualwall, strSql);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 
     * @category 获取测试信息
     * @author 吴平福
     * @param conn_source
     * @param conn_visualwall update 2016年5月18日
     */
    private void getTestInfo(Connection conn_source, Connection conn_visualwall) {
        try {
            String strSql = "select * from bss_prj where areaname in(" + getAreaName() + ") ";
            ResultSet rsResultSet = AiDBUtil.execSqlQuery(conn_visualwall, strSql);
            StringBuffer sb = new StringBuffer();
            while (rsResultSet.next()) {
                sb.append(rsResultSet.getLong("prj_id")).append(",");
            }
            sb.append(0);
            rsResultSet.close();
            // logger.debug(sb);
            // JpfMail.sendMail(getPrjOwner(), sb.toString(),
            // "GBK",getAreaName()+"遗漏的模块");


            strSql = "delete from  jpf_testtime  where areaname='tmp' or (DATE_FORMAT(state_date,'%Y-%m-%d')=CURDATE() and areaname in ("
                    + getAreaName() + "))";
            logger.debug(strSql);
            AiDBUtil.execUpdateSql(conn_visualwall, strSql);

            strSql = getTestMetricsSql(sb.toString());

            logger.debug(strSql);
            rsResultSet = AiDBUtil.execSqlQuery(conn_source, strSql);
            strSql = "insert into jpf_testtime (prj_id,build_date,is_last,areaname,prj_count,tests,kee,test_execution_time,test_errors,skipped_tests,test_failures,qualifier,scope) "
                    + " values(?,?,1,'tmp',0,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = conn_visualwall.prepareStatement(strSql);
            while (rsResultSet.next()) {
                // strSql = getTestInfoInsertSql(rsResultSet);
                // JpfDbUtils.execUpdateSql(conn_visualwall, strSql);
                // logger.debug(rsResultSet.getString("kee"));
                pstmt.setLong(1, rsResultSet.getLong("prj_id"));
                pstmt.setString(2, rsResultSet.getString("build_date"));
                pstmt.setLong(3, rsResultSet.getLong("tests"));
                pstmt.setString(4, rsResultSet.getString("kee"));
                pstmt.setDouble(5, rsResultSet.getDouble("test_execution_time"));
                pstmt.setLong(6, rsResultSet.getLong("test_errors"));
                pstmt.setLong(7, rsResultSet.getLong("skipped_tests"));
                pstmt.setLong(8, rsResultSet.getLong("test_failures"));
                pstmt.setString(9, rsResultSet.getString("qualifier"));
                pstmt.setString(10, rsResultSet.getString("scope"));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn_visualwall.commit();
            sb.setLength(0);

            strSql = "update jpf_testtime t1,bss_prj t2 set t1.areaname=t2.areaname where t1.areaname='tmp' and t1.prj_id=t2.prj_id and t2.areaname in ("
                    + getAreaName() + ")";
            logger.debug(strSql);
            AiDBUtil.execUpdateSql(conn_visualwall, strSql);
            // strSql="update jpf_testtime set where areaname='tmp'";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
