/**
 * @author 吴平福 E-mail:421722623@qq.com
 * @version 创建时间：2015年5月2日 下午2:31:28 类说明
 */

package org.jpf.ci.dbs.dbchange;

import java.sql.Connection;
import java.sql.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 
 */
public class DbChangeConn {
    private static final Logger logger = LogManager.getLogger(DbChange.class);
    public static DbChangeConn cDbChangeConn = new DbChangeConn();

    public static DbChangeConn GetInstance() {
        return cDbChangeConn;
    }



    public Connection GetConn(DbInfo cDbInfo) throws Exception {
        String driver = "com.mysql.jdbc.Driver";
        Class.forName(driver).newInstance();
        logger.debug(cDbInfo.URL);
        return DriverManager.getConnection(cDbInfo.URL, cDbInfo.dbuser, cDbInfo.dbpass);
    }

}
