/** 
 * @author 吴平福 
 * E-mail:wupf@asiainfo.com 
 * @version 创建时间：2015年5月2日 下午2:31:28 
 * 类说明 
 */

package org.jpf.visualwall.gathers;

import java.sql.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jpf.utils.dbsql.AiDBUtil;
/**
 * 
 */
public class QInfoGrdSonar75 extends abstractQualityInfo
{
	private static final Logger logger = LogManager.getLogger();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jpf.walls.AbstractQualityInfo#GetDBUsr()
	 */
	@Override
	String getDBUsr()
	{
		// TODO Auto-generated method stub
		return "bjsonar";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jpf.walls.AbstractQualityInfo#GetDBPwd()
	 */
	@Override
	String getDBPwd()
	{
		// TODO Auto-generated method stub
		return "bjsonar";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jpf.walls.AbstractQualityInfo#GetDBUrl()
	 */
	@Override
	String getDBUrl()
	{
		// TODO Auto-generated method stub
		return "jdbc:mysql://10.1.234.75:4306/hzsonar";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jpf.walls.AbstractQualityInfo#GetQuerySql()
	 */
	@Override
	String getQuerySql()
	{
		// TODO Auto-generated method stub
		String strSql = "select t1.id prj_id,DATE_FORMAT(t2.build_date,'%Y-%m-%d') build_date,"
				+ " t1.name prj_name, t1.kee,max(case t3.metric_id when 80 then t3.value end)blocker_violations,"
				+ " max(case t3.metric_id when 81 then t3.value end)critical_violations,"
				+ " max(case t3.metric_id when 82 then t3.value end)major_violations"
				+ " ,max(case t3.metric_id when 3 then t3.value end)prj_lines"
				+ " ,MIN(CASE t3.metric_id WHEN 30 THEN t3.value  END)tests"
				+ " ,MAX(CASE t3.metric_id WHEN 35 THEN t3.value/100  END)test_success_density"
				+ " ,MAX(CASE t3.metric_id WHEN 43 THEN t3.value/100  END)line_coverage"
				+ " ,MAX(CASE t3.metric_id WHEN 50 THEN t3.value/100  END)branch_coverage"
				+ " ,min(CASE t3.metric_id WHEN 31 THEN t3.value/100  END)test_execution_time"
				+ " from projects t1,snapshots t2,project_measures t3"
				+ " where t1.scope='PRJ' and t1.qualifier='TRK' and  t1.enabled=1 and t1.id=t2.project_id "
				+ " and t2.islast=1  and t2.id=t3.snapshot_id  AND t3.rule_id IS NULL  and t3.metric_id in (3,30,31,35,43,50,80,81,82)"
				+ " group by t1.id order by t1.name ";
		logger.info(strSql);
		return strSql;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jpf.walls.AbstractQualityInfo#GetAreaName()
	 */
	@Override
	String getAreaName()
	{
		// TODO Auto-generated method stub
		return "'GRD_BILLING','P0_BILLING','GRD_BILLING_SRD','TA_HZ'";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jpf.visualwall.gathers.abstractQualityInfo#getPrjOwner()
	 */
	@Override
	String getPrjOwner()
	{
		// TODO Auto-generated method stub
		return "wupf";
	}
	/**
	 * @category payment作为P0项目
	 */
	protected void doPrivate(Connection conn) throws Exception
	{
		String strSql = "insert into bss_rpt_tmp(prj_id,  build_date,prj_name,  blocker_violations,  critical_violations,  major_violations,  branch_coverage,"
				+ "  is_last,  areaname,  prj_lines,  tests,  test_success_density,  prj_count,  line_coverage,  state_date,  refer_value1,  refer_value2,  kpi,  kee)"
				+ " select prj_id,  build_date,prj_name,  blocker_violations,  critical_violations,  major_violations,  branch_coverage,  is_last,  'P0_BILLING',"
				+ "  prj_lines,  tests,  test_success_density,  prj_count,  line_coverage,  state_date,  refer_value1,  refer_value2,  kpi,  kee "
				+" from bss_rpt_tmp where areaname='GRD_BILLING' and PRJ_ID=262573;";
		AiDBUtil.execUpdateSql(conn, strSql);

	}

}
