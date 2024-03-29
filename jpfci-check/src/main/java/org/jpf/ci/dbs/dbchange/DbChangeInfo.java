/** 
 * @author 吴平福 
 * E-mail:421722623@qq.com 
 * @version 创建时间：2015年5月2日 下午2:31:28 
 * 类说明 
 */

package org.jpf.ci.dbs.dbchange;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jpf.utils.dbsql.AiDBUtil;


/**
 * 
 */
public class DbChangeInfo
{
	private static final Logger logger = LogManager.getLogger(DbChangeInfo.class);
	/**
	 * 
	 */
	public DbChangeInfo(String strFileName,String strAuthor,int iStatus)
	{
		// TODO Auto-generated constructor stub
		Connection conn = null;
		logger.info("save db change info");
		logger.info("strFileName="+strFileName);
		logger.info("strAuthor="+strAuthor);
		logger.info("iStatus="+iStatus);
		try
		{
			String strInsetSql = "insert into jpf_db_change(dbfilename,dbauthor,firstok,status,count)value(?,?,?,?,1)";
			String strQuerySql="select * from jpf_db_change where dbfilename=? ";
			String strUpdateSql="update jpf_db_change set status=?,count=count+1 where dbfilename=?";
			
			if (null==strFileName)
			{
				logger.warn("input FileName is null");
				return;
			}

			conn = AppConn.GetInstance().GetConn();
			conn.setAutoCommit(false);
			java.sql.PreparedStatement pStmt=conn.prepareStatement(strQuerySql);
			pStmt.setString(1, strFileName);
			java.sql.ResultSet rSet=pStmt.executeQuery();
			if (rSet.next())
			{
				java.sql.PreparedStatement pStmt2=conn.prepareStatement(strUpdateSql);
				pStmt2.setInt(1, iStatus);
				pStmt2.setString(2, strFileName);
				logger.info(  pStmt2.executeUpdate());
			}else {
				java.sql.PreparedStatement pStmt3=conn.prepareStatement(strInsetSql);
				
				pStmt3.setString(1, strFileName);
				pStmt3.setString(2, strAuthor);
				pStmt3.setInt(3, iStatus);
				pStmt3.setInt(4, iStatus);
				
				logger.info(  pStmt3.executeUpdate());
			}
			pStmt.close();
			conn.commit();
		} catch (SQLException ex)
		{
			// TODO: handle exception
			ex.printStackTrace();
			logger.error(ex);
		} catch (Exception ex)
		{
			// TODO: handle exception
			ex.printStackTrace();
			logger.error(ex);
		} finally
		{
		    AiDBUtil.doClear(conn);
		}
	}

}
