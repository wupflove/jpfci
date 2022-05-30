/** 
 * @author 吴平福 
 * E-mail:421722623@qq.com 
 * @version 创建时间：2015年2月8日 下午11:07:00 
 * 类说明 
 */

package org.jpf.ci.dbs.compare.subtables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jpf.utils.AiDateTimeUtil;

/**
 * @category 基础类
 */
public abstract class AbstractCheckTable
{
	private static final Logger LOGGER = LogManager.getLogger();
	public StringBuffer sb = new StringBuffer();

	abstract String getCheckSql();

	abstract String getCheckTitle();

	abstract void formatOutput(ResultSet rs) throws Exception;

	/**
	 * 
	 */
	public AbstractCheckTable()
	{
	}
    /**
     * 
     * @category 
     * @author 吴平福 
     * @param conn
     * @param strSqlParam
     * update 2016年5月3日
     */
	public void doCheck(Connection conn, String strSqlParam)
	{
		// TODO Auto-generated constructor stub
		try
		{
			doWork(conn, strSqlParam); 
			//CompareUtil.writeFile(GetCheckTitle(), sb); 
		} catch (Exception ex)
		{
			// TODO: handle exception
			ex.printStackTrace();
		}

	}
    /**
     *  
     * @category 
     * @author 吴平福 
     * @param conn
     * @param strSqlParam
     * @throws Exception
     * update 2016年5月3日
     */
	private void doWork(Connection conn, String strSqlParam) throws Exception
	{
		String sSql = getCheckSql();
		LOGGER.debug(sSql);
		PreparedStatement pStmt = conn.prepareStatement(sSql);
		if (null != strSqlParam)
		{
			pStmt.setString(1, strSqlParam);
		}
		ResultSet rs = pStmt.executeQuery();

		sb.append(getCheckTitle()).append("\n").append(AiDateTimeUtil.getToday()).append("\n");

		formatOutput(rs);

	}
}
