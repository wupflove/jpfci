/**
 * @author 吴平福 E-mail:wupf@asiainfo.com
 * @version 创建时间：2016年9月23日 下午10:57:35 类说明
 */

package org.jpf.ci.dbs.dbchange;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jpf.utils.conf.ConfigProp;

/**
 * 
 */
public class DbChangeHandleTime {
    // 执行操作的时间配置文件名称
    private final static String PROP_CHECK_FILE = "db_change.properties";
    private static final Logger logger = LogManager.getLogger();

    /**
     * 
     * @category 获取上次执行时间 
     * @author 吴平福 
     * @return
     * @throws Exception
     * update 2016年9月23日
     */
    public static String getLastExcludeTime() throws Exception {
        String strEXCLUDE_DATE =
                ConfigProp.getStrFromConfigWithException(PROP_CHECK_FILE, "EXCLUDE_DATE");
        logger.info("last exec time:" + strEXCLUDE_DATE);
        return strEXCLUDE_DATE;
    }
    /**
     * 
     * @category 写入这次执行完成时间 
     * @author 吴平福 
     * @param strNextDateTime
     * @throws Exception
     * update 2016年9月23日
     */
    public static void writeCurrentExcludeTime(String strNextDateTime)throws Exception
    {
        ConfigProp.saveFile(PROP_CHECK_FILE, "last db change date",
                "EXCLUDE_DATE=" + strNextDateTime);
        logger.info("next db change exec time is :" + strNextDateTime);
    }
}
