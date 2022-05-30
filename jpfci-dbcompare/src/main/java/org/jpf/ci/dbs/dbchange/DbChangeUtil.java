/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2016年9月23日 下午2:23:40 
* 类说明 
*/ 

package org.jpf.ci.dbs.dbchange;

import org.jpf.utils.ios.AiFileUtil;

/**
 * 
 */
public class DbChangeUtil {
    
    public static void writeNewSqlFile(StringBuffer sb,String strFileName)
    {
            AiFileUtil.saveFile(strFileName+ ".wupf", sb.toString());
        // LOGGER.info(strMailText);
    }
}
