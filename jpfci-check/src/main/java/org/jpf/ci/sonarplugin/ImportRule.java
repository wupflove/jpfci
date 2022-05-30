/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2017年11月6日 下午2:13:40 
* 类说明 
*/ 

package org.jpf.ci.sonarplugin;

import java.sql.DriverManager;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.ci.rpts.RptDevConst;

import org.jpf.utils.dbsql.AiDBUtil;

import java.sql.Connection;

/**
 * 
 */
public class ImportRule {
    private static final Logger logger = LogManager.getLogger();
    /**
     * 
     */
    public ImportRule() {
        Connection conn=null;
        try {
            conn=GetConn();
            conn.setAutoCommit(false);
            insertRulesProfiles(conn);
            
            
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }finally {
            AiDBUtil.doClear(conn);
        }
    }
    
    public Connection GetConn()throws Exception
    {
        Connection conn = null;

        try
        {

            String driver = "com.mysql.jdbc.Driver";
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sonar512",
                    "sonar", "wupflove");

        } catch (Exception ex)
        {
            ex.printStackTrace();
            throw ex;
        }
        return conn;
    }
    
    private int insertRulesProfiles(Connection conn)throws Exception
    {
        String strSql="INSERT INTO rules_profiles(name,language,kee,parent_kee,rules_updated_at,created_at,updated_at,is_default,last_used,user_updated_at) "
                +" VALUES ('ai_java_v1.0','java','java-ai_java_v1-0-57775',NULL,'2017-11-02T06:10:17+0000','2017-11-02 14:09:29','2017-11-02 14:10:18');";
        AiDBUtil.ExecSqlUpdate(logger, conn, strSql);
        
        strSql ="select * from rules_profiles where name='ai_java_v1.0'";
        ResultSet rSet=AiDBUtil.execSqlQuery(conn, strSql);
        if (rSet.next())
        {
            return rSet.getInt("id");
        }
        return -1;
    }
    
    private void insertActiveRule(Connection conn,int iRuleProfileId)throws Exception
    {
        String strSql="INSERT INTO active_rules (id,profile_id,rule_id,failure_level,inheritance,created_at,updated_at) "
                +" VALUES()";
              AiDBUtil.ExecSqlUpdate(logger, conn, strSql);
    }
    /**
     * @category 
     * @author 吴平福 
     * @param args
     * update 2017年11月6日
     */

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ImportRule cImportRule=new ImportRule();
    }

}
