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
public class QInfoGrdNjReliance extends abstractQualityInfo
{
	private static final Logger logger =  LogManager.getLogger();


	/* (non-Javadoc)
	 * @see org.jpf.walls.AbstractQualityInfo#GetDBUsr()
	 */
	@Override
	String getDBUsr()
	{
		// TODO Auto-generated method stub
		return "sonar_rl";
	}

	/* (non-Javadoc)
	 * @see org.jpf.walls.AbstractQualityInfo#GetDBPwd()
	 */
	@Override
	String getDBPwd()
	{
		// TODO Auto-generated method stub
		return "sonar_rl";
	}

	/* (non-Javadoc)
	 * @see org.jpf.walls.AbstractQualityInfo#GetDBUrl()
	 */
	@Override
	String getDBUrl()
	{
		// TODO Auto-generated method stub
		return "jdbc:mysql://10.1.242.120:3306/sonar";
	}


	/* (non-Javadoc)
	 * @see org.jpf.walls.AbstractQualityInfo#GetAreaName()
	 */
	@Override
	String getAreaName()
	{
		// TODO Auto-generated method stub
		return "'CRM_RELIANCE'";
	}
	/* (non-Javadoc)
	 * @see org.jpf.walls.AbstractQualityInfo#GetQuerySql()
	 */
	@Override
	String getQuerySql()
	{

		// TODO Auto-generated method stub
		String strSql="select t1.id prj_id,DATE_FORMAT(t2.build_date,'%Y-%m-%d') build_date,"
				+" t1.name prj_name,t1.kee, max(case t3.metric_id when 95 then t3.value end)blocker_violations,"
				+" max(case t3.metric_id when 96 then t3.value end)critical_violations,"
				+" max(case t3.metric_id when 97 then t3.value end)major_violations,"
				+" max(case t3.metric_id when 3 then t3.value end)prj_lines"
				+" ,MIN(CASE t3.metric_id WHEN 30 THEN t3.value  END)tests" 
				+" ,MAX(CASE t3.metric_id WHEN 35 THEN t3.value/100  END)test_success_density" 
				+" ,MAX(CASE t3.metric_id WHEN 43 THEN t3.value/100  END)line_coverage" 
				+" ,MAX(CASE t3.metric_id WHEN 50 THEN t3.value/100  END)branch_coverage" 
				+ " ,min(CASE t3.metric_id WHEN 31 THEN t3.value/100  END)test_execution_time"
				+" from projects t1,snapshots t2,project_measures t3"
				+" where t1.scope='PRJ'  and t1.qualifier='TRK' and t1.enabled=1 and t1.id=t2.project_id and t1.name<>'cs-ct'"
				+" and t2.islast=1  and t2.id=t3.snapshot_id  AND t3.rule_id IS NULL  and t3.metric_id in (3,30,31,35,43,50,95,96,97)"
				+" group by t1.id order by t1.name ";
		logger.info(strSql);
		return strSql;
	}


	protected String[][]  getMerge()
	{
		String[][] strMerge={{"98962","156225,156256","Order"},{"104409","155723,133076","Uip"}};
		return  strMerge;
	}

	/* (non-Javadoc)
	 * @see org.jpf.visualwall.gathers.abstractQualityInfo#getPrjOwner()
	 */
	@Override
	String getPrjOwner()
	{
		// TODO Auto-generated method stub
		return "guyuan";
	}


}
