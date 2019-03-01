/** 
 * @author 吴平福 
 * E-mail:wupf@asiainfo-linkage.com 
 * @version 创建时间：2014年12月25日 下午1:31:23 
 * 类说明 
 */

package org.jpf.visualwall.precals;

import java.sql.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.visualwall.WallsDbConn;

import org.jpf.utils.AiDateTimeUtil;
import org.jpf.utils.dbsql.AiDBUtil;

/**
 * 
 */
public class CalQuota
{
	private static final Logger logger = LogManager.getLogger();
	

	/**
	 * 
	 * @param args
	 * 被测试类名：TODO
	 * 被测试接口名:TODO
	 * 测试场景：TODO
	 * 前置参数：TODO
	 * 入参：
	 * 校验值：
	 * 测试备注：
	 * update 2015年8月24日
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		if (2 == args.length)
		{
			CalQuota cCalQuota = new CalQuota(args[0], args[1]);
		}
		logger.info("game over");

	}

	public CalQuota(String strStartDate, String strEndDate)
	{
	     //String AREA_KEY ="'P0_BILLING','GRD_Integration','NSG_C3','GRD_BILLING','GRD_C3','GRD_CRM6','GRD_CRM7','OSE','AE','GRD_O2P','GRD_MNT','GRD_RTSS','GRD_EDW','GRD_RELIANCE'";
	     String AREA_KEY ="'TA_HZ'";
         AREA_KEY=AREA_KEY.replaceAll("'","");
	     String[] AREA_KEYS=AREA_KEY.split(",");
	     for (int i=0;i<AREA_KEYS.length;i++)
	     {
	    	 doWork(AREA_KEYS[i].trim(),strStartDate,strEndDate);
	     }
	}
	private void doWork(String in_AREA_KEY,String strStartDate, String strEndDate)
	{

		Connection conn = null;
		try
		{
			conn = WallsDbConn.getInstance().getConn();
			conn.setAutoCommit(false);
			logger.info(in_AREA_KEY);
			System.out.println("strStartDate:=" + strStartDate);
			System.out.println("strEndDate:=" + strEndDate);
			long lDays = AiDateTimeUtil.getBetweenDays(strStartDate, strEndDate);
			// -2.54
			double dKpiAllStart1 = 0;
			double dKpiAllEnd1 = 0;
			double dKpiAllStart2 = 0;
			double dKpiAllEnd2 = 0;
			double dKpiChange1 = 0;
			double dKpiChange2 = 0;
			String strSql = "delete from bss_rpt_date where areaname='" + in_AREA_KEY
					+ "' and DATE_FORMAT(build_date,'%Y-%m-%d')>='"
					+ strStartDate + "' and DATE_FORMAT(build_date,'%Y-%m-%d')<='" + strEndDate + "'";
			logger.info(strSql);
			java.sql.Statement statement = conn.createStatement();
			statement.executeUpdate(strSql);
			strSql = "insert into bss_rpt_date(build_date,prj_id,refer_value,refer_value2,areaname)values(?,?,?,?,?)";
			logger.info(strSql);
			java.sql.PreparedStatement pStatement = conn.prepareStatement(strSql);

			int prj_id = 0;
			// java.sql.ResultSet rSet =
			strSql = "select prj_id,original_value1,target_value1,original_value2,target_value2 from bss_prj where areaname='" + in_AREA_KEY
					+ "' and target_value1 is not null and original_value1 is not null order by prj_id";

			logger.info(strSql);
			int prjCount=0;
			java.sql.ResultSet rSet = statement.executeQuery(strSql);
			
			while (rSet.next())
			{
				prj_id = rSet.getInt("prj_id");
				logger.info("prj_id=" + prj_id);
				double dKpiStart1 = rSet.getDouble(2);
				double dKpiEnd1 = rSet.getDouble(3);
				double dKpiStart2 = rSet.getDouble(4);
				double dKpiEnd2 = rSet.getDouble(5);
				
				dKpiAllStart1 += dKpiStart1;
				dKpiAllEnd1 += dKpiEnd1;		
				dKpiChange1 = (dKpiEnd1 - dKpiStart1) / lDays;
				
				dKpiAllStart2 += dKpiStart2;
				dKpiAllEnd2 += dKpiEnd2;		
				dKpiChange2 = (dKpiEnd2 - dKpiStart2) / lDays;
				
				for (int i = 0; i <= lDays; i++)
				{

					pStatement.setDate(1, AiDateTimeUtil.addSqlDate(strStartDate, i));
					pStatement.setInt(2, prj_id);
					pStatement.setDouble(3, dKpiStart1 + dKpiChange1 * i);
					pStatement.setDouble(4, dKpiStart2 + dKpiChange2 * i);
					pStatement.setString(5, in_AREA_KEY);
					logger.info(prj_id +":"+(dKpiStart1 + dKpiChange1 * i)+":"+(dKpiStart2 + dKpiChange2 * i));
					pStatement.addBatch();
				}
				prjCount++;
			}

			prj_id = 0;
			dKpiAllEnd1=dKpiAllEnd1/prjCount;
			dKpiAllStart1=dKpiAllStart1/prjCount;
			dKpiAllEnd2=dKpiAllEnd2/prjCount;
			dKpiAllStart2=dKpiAllStart2/prjCount;
			
			dKpiChange1 = (dKpiAllEnd1 - dKpiAllStart1) / lDays;
			
			dKpiChange2 = (dKpiAllEnd2 - dKpiAllStart2) / lDays;
			
			for (int i = 0; i <= lDays; i++)
			{
				pStatement.setDate(1, AiDateTimeUtil.addSqlDate(strStartDate, i));
				pStatement.setInt(2, prj_id);
				pStatement.setDouble(3, dKpiAllStart1 + dKpiChange1 * i);
				pStatement.setDouble(4, dKpiAllStart2 + dKpiChange2 * i);
				pStatement.setString(5, in_AREA_KEY);
				logger.info(prj_id +":"+(dKpiAllStart1 + dKpiChange1 * i)+"-"+(dKpiAllStart2 + dKpiChange2 * i));
				pStatement.addBatch();
			}
			pStatement.executeBatch();
			if (!conn.getAutoCommit())
			{
				conn.commit();
			}

		} catch (Exception ex)
		{
			// TODO: handle exception
			ex.printStackTrace();
		} finally
		{
		    AiDBUtil.doClear(conn);
		}
	}
}
