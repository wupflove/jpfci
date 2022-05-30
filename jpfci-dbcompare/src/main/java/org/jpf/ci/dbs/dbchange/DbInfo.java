/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2016年9月23日 下午8:30:58 
* 类说明 
*/ 

package org.jpf.ci.dbs.dbchange;

import java.util.HashMap;

/**
 * 
 */
public class DbInfo {

        String domain = "";
        String URL = "";
        String dbuser = "";
        String dbpass = "";
        int iDoExecSql = 0;
        HashMap<String, String> parent_child = new HashMap<String, String>();
        String dbmail="";
}
