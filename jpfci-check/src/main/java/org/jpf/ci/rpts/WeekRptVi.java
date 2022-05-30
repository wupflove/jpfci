/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2015年7月22日 下午11:10:05 
* 类说明 
*/

package org.jpf.ci.rpts;

import java.sql.ResultSet;

/**
 * 
 */
public class WeekRptVi extends AbstractRpt
{

	/**
	 * @param strPrjName
	 * @param iType
	 * @param strMailCC
	 */
	public WeekRptVi(String strPrjName, int iType, String strMailCC,String strCfg)
	{
		super(strPrjName, iType, strMailCC,strCfg);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.jpf.ci.rpts.AbstractWeekRpt#GetTemplateName()
	 */
	@Override
	public String GetTemplateName()
	{
		// TODO Auto-generated method stub
		return "week_vi.html";
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
	 * update 2015年10月21日
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		if (4 == args.length)
		{
			int i = Integer.parseInt(args[0]);
			WeekRptVi cRpt = new WeekRptVi(args[1],i,args[2],args[3]);
		}else
		{
			logger.warn("error parameter number");
		}
	}

	/**
	 * 
	 */
	@Override
	public String GetSql(int iType, String strPrjName)
	{
		// TODO Auto-generated method stub
		String strSql = "select * from jpf_file_user where prj_name='" + strPrjName + "'";
		if (0 == iType)
		{
			strSql += " and (IFNULL(major_violations1,0)<>IFNULL(major_violations2,0) or IFNULL(critical_violations1,0)<>IFNULL(critical_violations2,0)) ";
		}
		strSql += " order by login,kee";
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
			.append("</td><td>").append(rs.getLong("critical_violations1"))
			.append("</td><td>").append(rs.getLong("major_violations1"))
			.append("</td><td>").append(rs.getLong("critical_violations2"))
			.append("</td><td>").append(rs.getLong("major_violations2"))
			.append("</td><td>").append(rs.getLong("major_violations1")-rs.getLong("major_violations2")+rs.getLong("critical_violations1")-rs.getLong("critical_violations2"))
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
		return "一周违规变化";
	}

}
