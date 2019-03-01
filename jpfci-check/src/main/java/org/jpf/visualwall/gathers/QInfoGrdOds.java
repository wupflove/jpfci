/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2016年3月23日 下午2:41:05 
* 类说明 
*/ 

package org.jpf.visualwall.gathers;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jpf.utils.mails.AiMail;

/**
 * 
 */
public class QInfoGrdOds extends abstractQualityInfo
{
	private static final Logger logger =  LogManager.getLogger();
	/**
	 * 
	 */
	public QInfoGrdOds()
	{
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.jpf.visualwall.gathers.abstractQualityInfo#getDBUsr()
	 */
	@Override
	String getDBUsr()
	{
		// TODO Auto-generated method stub
		return "queryuser";
	}

	/* (non-Javadoc)
	 * @see org.jpf.visualwall.gathers.abstractQualityInfo#getDBPwd()
	 */
	@Override
	String getDBPwd()
	{
		// TODO Auto-generated method stub
		return "lsdjf!sdfhdgxfhtj";
	}

	/* (non-Javadoc)
	 * @see org.jpf.visualwall.gathers.abstractQualityInfo#getDBUrl()
	 */
	@Override
	String getDBUrl()
	{
		// TODO Auto-generated method stub
		return "jdbc:postgresql://10.1.234.192:5432/sonar";
	}
	
	protected Connection getSourceConn() throws Exception
	{
		try
		{
			String driver = "org.postgresql.Driver";
			Class.forName(driver).newInstance();
			logger.info(getAreaName() + ":" + getDBUrl());
			return DriverManager.getConnection(getDBUrl(), getDBUsr(), getDBPwd());
		} catch (Exception ex)
		{
			AiMail.sendMail(getPrjOwner(), getAreaName() + " :  " + getDBUrl() + "\n" + ex.getMessage(), "GBK",
					"连接数据库失败");
			throw ex;
		}

	}
	/* (non-Javadoc)
	 * @see org.jpf.visualwall.gathers.abstractQualityInfo#getQuerySql()
	 */
	@Override
	String getQuerySql()
	{
		// TODO Auto-generated method stub
		String strSql="SELECT t1.id prj_id, to_char(to_timestamp(t2.build_date/1000), 'YYYY-MM-DD')  build_date, t1.name  prj_name,t1.kee,"
				+ " MAX(CASE t3.metric_id WHEN 93 THEN t3.value END)blocker_violations,"
				+ " MAX(CASE t3.metric_id     WHEN 94             THEN t3.value         END)critical_violations,"
				+" MAX(        CASE t3.metric_id            WHEN 95            THEN t3.value        END)major_violations ,"
				+" MAX(        CASE t3.metric_id            WHEN 3            THEN t3.value        END)prj_lines ,"
				+"MIN(        CASE t3.metric_id            WHEN 29            THEN t3.value        END)tests ,"
				+"    MAX(        CASE t3.metric_id            WHEN 34            THEN t3.value/100        END)test_success_density ,"
				+"    MAX(        CASE t3.metric_id            WHEN 42            THEN t3.value/100        END)line_coverage ,"
				+"   MAX(        CASE t3.metric_id             WHEN 49            THEN t3.value/100        END)branch_coverage"
				+ " ,min(CASE t3.metric_id WHEN 30 THEN t3.value/100  END)test_execution_time"
				+" FROM     projects t1,    snapshots t2,    project_measures t3 WHERE     t1.scope='PRJ' AND t1.enabled=true"
				+" AND t1.id=t2.project_id AND t2.islast=true AND t2.id=t3.snapshot_id AND t3.rule_id IS NULL"
				+" AND t3.metric_id IN (3,29,30,34,49,42,93,94,95) GROUP BY     t1.id, t2.build_date ORDER BY    t1.name ";
		logger.info(strSql);
		return strSql;
	}

	/* (non-Javadoc)
	 * @see org.jpf.visualwall.gathers.abstractQualityInfo#getAreaName()
	 */
	@Override
	String getAreaName()
	{
		// TODO Auto-generated method stub
		return "'GRD_ODS'";
	}

	/* (non-Javadoc)
	 * @see org.jpf.visualwall.gathers.abstractQualityInfo#getPrjOwner()
	 */
	@Override
	String getPrjOwner()
	{
		// TODO Auto-generated method stub
		return "zhangrz3";
	}

    /* (non-Javadoc)
     * @see org.jpf.visualwall.gathers.abstractQualityInfo#getTestMetricsSql(java.lang.String)
     */
    @Override
    protected String getTestMetricsSql(String strPrjids) {
        // TODO Auto-generated method stub
        return  "select t1.scope,t1.qualifier,t1.root_id prj_id,to_char(to_timestamp(t2.build_date/1000), 'YYYY-MM-DD')  build_date, t1.name prj_name, t1.kee,"
        + " min(CASE t3.metric_id WHEN 30 THEN t3.value  END)test_execution_time,"
        + "             min(CASE t3.metric_id WHEN 29 THEN t3.value  END)tests"
        +",min(CASE t3.metric_id WHEN 31 THEN t3.value  END)test_errors,"
        +"min(CASE t3.metric_id WHEN 32 THEN t3.value  END)skipped_tests,"
        +"min(CASE t3.metric_id WHEN 33 THEN t3.value  END)test_failures"
        + "             from (select * from projects where root_id in ("+strPrjids
        +") union all select * from projects where root_id in (select id from projects where root_id in ("+strPrjids+"))) t1,snapshots t2,project_measures t3"
        + " where t1.enabled=true and t1.id=t2.project_id  and t2.islast=true  and t2.id=t3.snapshot_id"
        + "             AND t3.rule_id IS NULL  and t3.metric_id in(30,29,31,32,33) group by t1.root_id, t1.scope,t1.qualifier,build_date,t1.name, t1.kee order by t1.name";
    }

}
