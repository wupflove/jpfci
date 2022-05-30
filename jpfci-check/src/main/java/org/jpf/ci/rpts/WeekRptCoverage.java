/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2015年7月22日 下午11:10:05 
* 类说明 
*/

package org.jpf.ci.rpts;

import java.sql.ResultSet;

import org.apache.logging.log4j.Logger;

public class WeekRptCoverage extends AbstractRpt
{

	/**
	 * @param strPrjName
	 * @param iType
	 * @param strMailCC
	 */
	public WeekRptCoverage(String strPrjName, int iType, String strMailCC,String strCfg)
	{
		super(strPrjName, iType, strMailCC,strCfg);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		if (4 == args.length)
		{
			int i = Integer.parseInt(args[0]);
			WeekRptCoverage cDailyPackageRpt = new WeekRptCoverage(args[1],i,args[2],args[3]);
		}else
		{
			logger.warn("error parameter number");
		}
	}

	/* (non-Javadoc)
	 * @see org.jpf.ci.rpts.AbstractWeekRpt#GetTemplateName()
	 */
	@Override
	public String GetTemplateName()
	{
		// TODO Auto-generated method stub
		return "week_coverage.html";
	}

	/* (non-Javadoc)
	 * @see org.jpf.ci.rpts.AbstractWeekRpt#GetSql()
	 */
	@Override
	public String GetSql(int iType,String strPrjName)
	{
		// TODO Auto-generated method stub


			String strSql="select * from jpf_file_user where prj_name='"+strPrjName+"'";
			if(1==iType)
			{
				strSql+=" and (IFNULL(brach_coverage1,0)<>IFNULL(brach_coverage2,0) or IFNULL(cover_branch1,0)<>IFNULL(cover_branch2,0))";
			}
			
			if (0==iType)
			{
				strSql+=" order by login";
			}
			
			return strSql;


	}

	/* (non-Javadoc)
	 * @see org.jpf.ci.rpts.AbstractWeekRpt#DoMailText(java.lang.StringBuffer, java.sql.ResultSet)
	 */
	@Override
	public void DoMailText(StringBuffer sb, ResultSet rs) throws Exception
	{
		// TODO Auto-generated method stub
		while (rs.next())
		{
			sb.append("<tr><td align='left'>").append(rs.getString("kee"))
			.append("</td><td>").append(rs.getString("login"))
			.append("</td><td>").append(FormatDouble(rs.getDouble("brach_coverage1")))
			.append("%</td><td>").append(rs.getLong("cover_branch1"))
			.append("</td><td>").append(rs.getLong("cover_branch1")-rs.getLong("un_cover_branch1"))
			.append("</td><td>").append(FormatDouble(rs.getDouble("brach_coverage2")))
			.append("%</td><td>").append(rs.getLong("cover_branch2"))
			.append("</td><td>").append(rs.getLong("cover_branch2")-rs.getLong("un_cover_branch2"))
			.append("</td><td>").append(rs.getLong("cover_branch1")-rs.getLong("un_cover_branch1")-rs.getLong("cover_branch2")+rs.getLong("un_cover_branch2"))
			.append("</td></tr>");
		}
	}

	/* (non-Javadoc)
	 * @see org.jpf.ci.rpts.AbstractRpt#GetMailTitle()
	 */
	@Override
	public String GetMailTitle()
	{
		// TODO Auto-generated method stub
		return " 一周覆盖率变化";
	}

}
