/** 
 * @author 吴平福 
 * E-mail:wupf@asiainfo-linkage.com 
 * @version 创建时间：2013-4-16 下午1:33:28 
 * 类说明 
 */

package org.jpf.ci.rpts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jpf.utils.dbsql.AiDBUtil;

/**
 * 
 */
public class SonarAvg
{
	private static final Logger logger = LogManager.getLogger();

	private int getPrjCount(String strCfg)
	{
		String[] strPrjids=RptDevConst.getIncludePrjId(strCfg).split(",");
		return strPrjids.length;
	}
	public SonarAvg(String strCfg)
	{
		Connection conn = null;
		try
		{
			logger.info("current prdname:"+strCfg);
			logger.info("INCLUDE_PRJ_ID={}", RptDevConst.getIncludePrjId(strCfg));
			String strEXCLUDE_PRJ_ID_Cond = "";
			if (RptDevConst.getIncludePrjId(strCfg) != null
					&& RptDevConst.getIncludePrjId(strCfg).trim().length() > 0)
			{
				strEXCLUDE_PRJ_ID_Cond = " and t1.id in(" + RptDevConst.getIncludePrjId(strCfg) + ")";
			} else
			{
				throw new Exception("no EXCLUDE_PRJ_ID config,connect to wupf@asiainfo.com");
			}
			logger.info("metric_id=" + RptDevConst.getPRJ_AVG_METRIC(strCfg));
			String[] metrics = RptDevConst.getPRJ_AVG_METRIC(strCfg).split(",");
			// String strSql = "select count(*) from projects t1 where
			// scope='PRJ' and enabled=1 and qualifier='TRK'
			// "+strEXCLUDE_PRJ_ID_Cond;
			
			conn = RptDbConn.GetInstance().GetConn(strCfg);
			conn.setAutoCommit(false);
			
			String dValue = "";
			String cValue = "";
			int iCount = getPrjCount(strCfg);
			

			logger.debug("iCount=" + iCount);
			String strSql="";
			PreparedStatement pStmt=null;

			for (int i = 0; i < metrics.length; i++)
			{
				strSql = "select sum(t3.value)/" + iCount + ",sum(t3.variation_value_1)/" + iCount
						+ " from projects t1,snapshots t2,project_measures t3"
						+ " where  t1.id=t2.project_id and t2.islast=1 and t2.id=t3.snapshot_id  and t3.metric_id ="
						+ metrics[i]
						+ " and t3.rule_id is null and t1.scope='PRJ' and t1.enabled=1 and t1.qualifier='TRK' "
						+ strEXCLUDE_PRJ_ID_Cond;
				logger.debug("strSql=" + strSql);
				pStmt = conn.prepareStatement(strSql);
				ResultSet rs = pStmt.executeQuery();
				if (rs.next())
				{
					dValue = rs.getString(1);
					cValue = rs.getString(2);
				}
				rs.close();
				strSql = "update project_measures set value=" + dValue + ",variation_value_1=" + cValue
						+ " where metric_id="
						+ metrics[i] + " and snapshot_id in (select id from snapshots where project_id="
						+ RptDevConst.getPRJ_AVG_ID(strCfg) + " and islast=1)";
				logger.debug("strSql=" + strSql);
				pStmt.executeUpdate(strSql);

				conn.commit();
			}
			
			strSql = "update snapshots set created_at=now(),build_date=CURRENT_DATE where islast=1 and project_id ="
					+ RptDevConst.getPRJ_AVG_ID(strCfg);
			logger.debug("strSql=" + strSql);
			pStmt.executeUpdate(strSql);
			pStmt.close();
			if(conn.getAutoCommit()==false)
			{
				conn.commit();
			}
			logger.info("game over");
		} catch (Exception ex)
		{
			ex.printStackTrace();
			logger.error(ex);
		} finally
		{
		    AiDBUtil.doClear(conn);
		}
	}

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		if (1==args.length )
		{
			SonarAvg cSonarAvg = new SonarAvg(args[0]);
		}
		logger.info("game over");
	}

}
