/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2012-9-4 下午12:53:43 类说明
 */

package org.jpf.ci.sonarplugin;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.ci.rpts.RptDbConn;

import org.jpf.utils.dbsql.AiDBUtil;

/**
 * 
 */
public class SonarUsr {
    private static final Logger logger = LogManager.getLogger();

    /**
     * @param args 被测试类名：TODO 被测试接口名:TODO 测试场景：TODO 前置参数：TODO 入参： 校验值： 测试备注： update 2013-5-4
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        if (4 == args.length) {
            SonarUsr cSonarUsr = new SonarUsr(args[0], args[1], args[2], args[3]);
        } else {
            logger.warn("just 4 param");
        }
    }

    /**
     * 
     * @param strPrjName
     * @param strPath
     * @param strCfgType
     * @param strType
     */
    public SonarUsr(String strPrjName, String strPath, String strCfgType, String strType) {
        // 扫描本地目录，从数据库中获取记录
        Connection conn = null;

        Connection conn_visualwall = null;
        if (strPrjName == null || "".equalsIgnoreCase(strPrjName)) {
            logger.info("error prj name :" + strPrjName);
            return;
        }
        strPrjName = strPrjName.trim();

        if (strPath == null || "".equalsIgnoreCase(strPath)) {
            logger.info("error path name :" + strPath);
            return;
        }
        File file = new File(strPath);
        if (!file.exists() || !file.isDirectory()) {
            logger.info("path is not exist :" + strPath);
            return;
        }
        try {

            SvnUsr cSvnUsr = new SvnUsr(strPrjName, strPath, strType);

            String strSql =
                    "select a.severity,b.name,a.message,a.line,c.long_name from issues a,rules b,projects c,projects t1"
                            + " where a.severity in ('BLOCKER','CRITICAL') and a.rule_id = b.id"
                            + " and a.component_uuid = c.uuid and a.status='OPEN' and a.project_uuid = t1.uuid"
                            + " and t1.name='" + strPrjName + "'";

            logger.debug("strSql=" + strSql);
            conn = RptDbConn.GetInstance().GetConn(strCfgType);
            conn.setAutoCommit(false);
            ResultSet rs = AiDBUtil.ExecSqlQuery( conn, strSql);
            int iCount = 0;

            conn_visualwall = RptDbConn.GetInstance().GetConn("VISUALWALL");
            conn_visualwall.setAutoCommit(false);
            
            //strSql="insert into aicode_vi_his  select * from aicode_vi ";
            //AiDBUtil.ExecSqlUpdate(logger, conn_visualwall, strSql);
            
            strSql = "delete from aicode_vi where   prj_name='" + strPrjName + "'";
            logger.debug("strSql=" + strSql);
            AiDBUtil.ExecSqlUpdate( logger, conn_visualwall, strSql);
            strSql = "insert into aicode_vi(prj_name,vi_name,vi_severity,message,line,long_name) value(?,?,?,?,?,?)";
            PreparedStatement pStmt = conn_visualwall.prepareStatement(strSql);

            while (rs.next()) {

                //logger.info("current prj :" + rs.getString("name"));
                pStmt.setString(1, strPrjName);
                pStmt.setString(2, rs.getString("name"));
                pStmt.setString(3, rs.getString("severity"));
                pStmt.setString(4, rs.getString("message"));
                pStmt.setString(5, rs.getString("line"));
                pStmt.setString(6, rs.getString("long_name"));
                pStmt.addBatch();
                iCount++;
            }
            pStmt.executeBatch();
            conn_visualwall.commit();

            logger.debug("write into db count:" + iCount);
            // putfile.delete();
        } catch (Exception ex) {
            logger.error(ex);
            ex.printStackTrace();
        } finally {
            AiDBUtil.doClear(conn);
            AiDBUtil.doClear(conn_visualwall);
        }

    }
}
