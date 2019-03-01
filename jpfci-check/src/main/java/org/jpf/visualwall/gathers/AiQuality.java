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
import org.jpf.visualwall.WallsDbConn;

import org.jpf.utils.dbsql.AiDBUtil;

/**
 * 
 */
public class AiQuality
{
	private static final Logger logger =  LogManager.getLogger();
	/**
	 * 
	 */
	public AiQuality()
	{
		// TODO Auto-generated constructor stub
		DoWork();
	}
    
	private void DoWork()
	{

		try
		{
		    QInfoGrdOSE cQInfoGrdOSE=new QInfoGrdOSE();
            
			QInfoBiuBjSonar cQInfoBiuBjSonar=new QInfoBiuBjSonar();
			
			//QInfoGrdNjReliance cQInfoGrdNjReliance=new QInfoGrdNjReliance();
			
			QInfoGrdFzSonar cQInfoGrdFzSonar=new QInfoGrdFzSonar();
			QInfoGrdNjAE cQInfoGrdNjAE=new QInfoGrdNjAE();
			//QInfoGrdNjSonar cQInfoGrdNjSonar=new QInfoGrdNjSonar();
			
					
			QInfoGrdOds cSonarInfoGrdOds=new QInfoGrdOds();
            
		    //QInfoGrdNjCrm7 cQInfoGrdNjCrm7 =new QInfoGrdNjCrm7();
		    QInfoGrdFzSonar2 cQInfoGrdFzSonar2=new QInfoGrdFzSonar2();
		    
            
		    QInfoGrdSonar75 cQInfoGrdHzSonar=new QInfoGrdSonar75();
            
            QInfoGrdSonar73 cQInfoGrdSonar73=new QInfoGrdSonar73();
            
		    
            updateKpi();
		    logger.info("game over");
		} catch (Exception ex)
		{
			// TODO: handle exception
			ex.printStackTrace();
			logger.error(ex);
		}
	}
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
 * update 2015年11月16日
 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		AiQuality cAiQuality=new AiQuality();
	}
    /**
     * @category 修改KPI
     * @param conn_visualwall
     * @throws Exception
     *             update 2015年8月19日
     */
    protected void updateKpi()  {
        Connection conn_visualwall=null;
        try {
            conn_visualwall = WallsDbConn.getInstance().getConn();

            
            String strSql="delete from bss_rpt where prj_id=0 and build_date=CURRENT_DATE";
            logger.debug(strSql);
            AiDBUtil.execUpdateSql(conn_visualwall, strSql);
            
            strSql="update bss_rpt set is_last=0 where prj_id=0 and build_date<CURRENT_DATE and is_last<>0";
            logger.debug(strSql);
            AiDBUtil.execUpdateSql(conn_visualwall, strSql);
            
            strSql="insert into bss_rpt(areaname,prj_id,build_date,prj_name,is_last,blocker_violations,critical_violations,major_violations,prj_lines,tests,test_success_density,branch_coverage,test_execution_time,prj_count)"
                    +" select areaname,0,CURRENT_DATE,'avg',1,ifnull(avg(blocker_violations),0),ifnull(avg(critical_violations),0),"
                    + " ifnull(avg(major_violations),0),ifnull(avg(prj_lines),0),ifnull(avg(tests),0),ifnull(avg(test_success_density),0),"
                    + " ifnull(avg(branch_coverage),0), ifnull(avg(test_execution_time),0),count(*) "
                    + " from (select distinct * from   bss_rpt   where is_last=1 and prj_id<>0 and "
                    + " areaname in (select distinct areaname from bss_prj))t1"
                    + " group by areaname ";
            
            logger.debug(strSql);
            AiDBUtil.execUpdateSql(conn_visualwall, strSql);
            strSql =
                    "update bss_rpt t1,(select * from bss_rpt_date where prj_id=0 and build_date=CURRENT_DATE)  t2 set t1.refer_value1=t2.refer_value,t1.refer_value2=t2.refer_value2  ,t1.kpi=cal_kpi(t1.blocker_violations,t1.critical_violations,t1.major_violations,t1.branch_coverage, t2.refer_value , t2.refer_value2)  where t1.is_last=1 and t1.areaname=t2.areaname and t2.areaname in "
                    +"(select distinct areaname from bss_prj) "
                    + " and DATE_FORMAT(t1.state_date,'%m-%d-%Y')=DATE_FORMAT(t2.build_date,'%m-%d-%Y') and t1.prj_id=t2.prj_id and t1.prj_id=0 and t1.build_date=CURRENT_DATE ";
            logger.debug(strSql);
            AiDBUtil.execUpdateSql(conn_visualwall, strSql);
            
             strSql = "update totalkpi t4,"
                    +" (select t1.areaname,t2.refer_value*0.8*t1.prj_count minkpi,refer_value*1.2*t1.prj_count maxkpi,(t1.blocker_violations+t1.critical_violations+t1.major_violations)*t1.prj_count currentkpi from (select * from bss_rpt where prj_id=0) t1,"
                    +" (select * from bss_rpt_date where prj_id=0)t2 where t1.is_last=1 and t1.build_date=t2.build_date and t1.prj_id=t2.prj_id"
                    +" and t1.areaname=t2.areaname)t3 set t4.minkpi=t3.minkpi,t4.maxkpi=t3.maxkpi,t4.currentkpi=t3.currentkpi where t3.areaname=t4.areaname";
            logger.debug(strSql);
            AiDBUtil.execUpdateSql(conn_visualwall, strSql);
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }finally {
            AiDBUtil.doClear(conn_visualwall);
            
        }



    }
    

}
