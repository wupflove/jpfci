/** 
 * @author 吴平福 
 * E-mail:421722623@qq.com 
 * @version 创建时间：2015年5月2日 下午2:31:28 
 * 类说明 
 */

package org.jpf.visualwall.gathers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 */
public class QInfoGrdFzSonar extends abstractQualityInfo
{
	private static final Logger logger =  LogManager.getLogger();

	/* (non-Javadoc)
	 * @see org.jpf.walls.AbstractQualityInfo#GetDBUsr()
	 */
	@Override
	String getDBUsr()
	{
		// TODO Auto-generated method stub
		return "sonar";
	}
	/* (non-Javadoc)
	 * @see org.jpf.walls.AbstractQualityInfo#GetDBPwd()
	 */
	@Override
	String getDBPwd()
	{
		// TODO Auto-generated method stub
		return "sonar";
	}
	/* (non-Javadoc)
	 * @see org.jpf.walls.AbstractQualityInfo#GetDBUrl()
	 */
	@Override
	String getDBUrl()
	{
		// TODO Auto-generated method stub
		return "jdbc:mysql://10.6.0.13:3306/sonar";
	}
	/* (non-Javadoc)
	 * @see org.jpf.walls.AbstractQualityInfo#GetAreaName()
	 */
	@Override
	String getAreaName()
	{
		// TODO Auto-generated method stub
		return "'GRD_O2P','GRD_MNT'";
	}
	/* (non-Javadoc)
	 * @see org.jpf.walls.AbstractQualityInfo#GetQuerySql()
	 */
	@Override
	String getQuerySql()
	{
		// TODO Auto-generated method stub
		String strSql="select t1.id prj_id,DATE_FORMAT(t2.build_date,'%Y-%m-%d') build_date,"
				+" t1.name prj_name,t1.kee, max(case t3.metric_id when 80 then t3.value end)blocker_violations,"
				+" max(case t3.metric_id when 81 then t3.value end)critical_violations,"
				+" max(case t3.metric_id when 82 then t3.value end)major_violations"
				+" ,max(case t3.metric_id when 3 then t3.value end)prj_lines"
				+" ,MIN(CASE t3.metric_id WHEN 30 THEN t3.value  END)tests" 
				+" ,MAX(CASE t3.metric_id WHEN 35 THEN t3.value/100  END)test_success_density" 
				+" ,MAX(CASE t3.metric_id WHEN 43 THEN t3.value/100  END)line_coverage" 
				+" ,MAX(CASE t3.metric_id WHEN 50 THEN t3.value/100  END)branch_coverage" 
				+ " ,min(CASE t3.metric_id WHEN 30 THEN t3.value/100  END)test_execution_time"
				+" from projects t1,snapshots t2,project_measures t3"
				+" where t1.scope='PRJ'  and t1.qualifier='TRK' and t1.enabled=1 and t1.id=t2.project_id "
				+" and t2.islast=1  and t2.id=t3.snapshot_id  AND t3.rule_id IS NULL  and t3.metric_id in (3,30,31,35,43,50,80,81,82)"
				+" group by t1.id order by t1.name ";
		logger.info(strSql);
		return strSql;
	}
	

	protected String[][]  getMerge()
	{
		String[][] strMerge={{"1","200581,194131,200149","logs"}
		,{"2","194128,194236,193977","broker"}
		,{"3","195099,197819,197616","plugin"}
		,{"4","196993,197152,197294,196744,197304,197312,197329","billing"}
		,{"5","193442,196223","common"}
		,{"6","193394,198897","cacheController"}
		,{"7","194745,197009,198732,194186,197351","Conf"}
		,{"8","198647,198597","workflow"}
		,{"9","195971,194310,199644","task"}};
		return  strMerge;
	}
	/* (non-Javadoc)
	 * @see org.jpf.visualwall.gathers.abstractQualityInfo#getPrjOwner()
	 */
	@Override
	String getPrjOwner()
	{
		// TODO Auto-generated method stub
		return "wangben3";
	}


}
