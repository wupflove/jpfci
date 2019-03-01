/**
 * @author 吴平福 E-mail:wupf@asiainfo.com
 * @version 创建时间：2017年6月12日 下午3:21:25 类说明
 */

package org.jpf.ci.sonarplugin;

import java.sql.Connection;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.ci.rpts.RptDbConn;

import org.jpf.utils.dbsql.AiDBUtil;

/**
 * 
 */
public class ExportVi {
    private static final Logger logger = LogManager.getLogger();

    /**
     * 
     */
    public ExportVi() {
        // TODO Auto-generated constructor stub
        Connection conn = null;

        try {

            conn = RptDbConn.GetInstance().GetConn("VISUALWALL");
            conn.setAutoCommit(false);
            String strSql =
                    "select t1.prj_name,t1.long_name,t3.login,t1.vi_severity,t1.message,t2.desc_cn,t2.samples from aicode_vi t1,aicode_rules t2,jpf_dev t3 where t1.vi_name=t2.name and t2.is_use=1 and t1.long_name=t3.file_path";
            ResultSet rs = AiDBUtil.ExecSqlQuery( conn, strSql);
            int iCount = 0;
            while (rs.next()) {

                iCount++;
                System.out.println(
                        "vi" + iCount + " " + rs.getString("prj_name") + rs.getString("long_name")+ rs.getString("login") 
                                + rs.getString("vi_severity") + rs.getString("message")
                                + rs.getString("desc_cn") );

            }


            logger.debug("export count:" + iCount);
            // putfile.delete();
        } catch (Exception ex) {
            logger.error(ex);
            ex.printStackTrace();
        } finally {
            AiDBUtil.doClear(conn);

        }
    }

    /**
     * @category @author 吴平福
     * @param args update 2017年6月12日
     */

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ExportVi cExportVi = new ExportVi();
    }

}
