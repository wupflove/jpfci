/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2016年9月5日 下午4:05:16 
* 类说明 
*/ 

package org.jpf.ci.rpts;


/**
 * 
 */
public class ModuleQualityInfo implements Comparable<Object>{
    String areaname="";
    String prj_name="";
    String prj_mail="";
    String prj_owner="";
    String build_date;
    long blocker_violations, blocker_violationsChange;
    long critical_violations, critical_violationsChange;
    long major_violations, major_violationsChange;
    double dline_coverage, dline_coverageChange;
    double dbranch_coverage, dbranch_coverageChange;
    double dtest_success_density = 0.0, dtest_success_densityChange = 0.0;
    long ltests, ltestsChange;
    long PrjLines, PrjLinesChange;
    int iType = 1;
    double dUtcaseperline, dUtcaseperlineChange;
    
    //得分
    long score=0;
    /**
     * 
     * @category 
     * @author 吴平福 
     * update 2016年9月5日
     */
    public void FormatData()
    {
        dline_coverage = (double) Math.round(dline_coverage * 10) / 10;
        dline_coverageChange = (double) Math.round(dline_coverageChange * 10) / 10;

        dbranch_coverage = (double) Math.round(dbranch_coverage * 10) / 10;
        dbranch_coverageChange = (double) Math.round(dbranch_coverageChange * 10) / 10;

        dtest_success_density = (double) Math.round(dtest_success_density * 10) / 10;
        dtest_success_densityChange = (double) Math.round(dtest_success_densityChange * 10) / 10;

        dUtcaseperline = (double) Math.round(dUtcaseperline * 10) / 10;
        dUtcaseperlineChange = (double) Math.round(dUtcaseperlineChange * 10) / 10;
        
        CalScoreByPrj();
    }
    
    public void CalScoreByPrj() {
        /*
        long blocker_violations, blocker_violationsChange;
        long critical_violations, critical_violationsChange;
        long major_violations, major_violationsChange;
        double dline_coverage, dline_coverageChange;
        double dbranch_coverage, dbranch_coverageChange;
        double dtest_success_density = 0.0, dtest_success_densityChange = 0.0;
        long ltests, ltestsChange;
        long PrjLines, PrjLinesChange;
        int iType = 1;
        double dUtcaseperline, dUtcaseperlineChange;
        */
        //违规10分
        score=0;
        if(blocker_violations+critical_violations<10)
        {
            score=  10-(blocker_violations+critical_violations);
        }
        //单元测试覆盖率、通过率（20分）
        if (dbranch_coverage>50)
        {
            score+=10;
        }else
        {
            score+=dbranch_coverage/5;
        }
        score+=dtest_success_density/10;
        if (dUtcaseperline>20)
        {
            score+=10;
        }else
        {
            score+=dUtcaseperline/2;
        }
     }

    public int compareTo(Object o) {
        // TODO Auto-generated method stub
        ModuleQualityInfo another=(ModuleQualityInfo)o;        
        return  (int)(this.score-another.score);
    }
}
