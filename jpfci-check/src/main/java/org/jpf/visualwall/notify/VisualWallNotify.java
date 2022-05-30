/** 
 * @author 吴平福 
 * E-mail:421722623@qq.com 
 * @version 创建时间：2014年12月25日 下午1:31:23 
 * 类说明 
 */

package org.jpf.visualwall.notify;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.visualwall.WallsDbConn;

import org.jpf.utils.dbsql.AiDBUtil;
import org.jpf.utils.ios.AiFileUtil;
import org.jpf.utils.mails.AiMail;

/**
 * 
 */
public class VisualWallNotify
{
	private static final Logger logger = LogManager.getLogger();

	/**
	 * 
	 */
	public VisualWallNotify(String strModule)
	{
		// TODO Auto-generated constructor stub
		Connection conn = null;
		try
		{
			String strSql = "select prj_id,prj_name,leader_mail,areaname,prj_cname from bss_prj where areaname='"
					+ strModule
					+ "'";
			logger.debug(strSql);
			conn = WallsDbConn.getInstance().getConn();
			Statement stmt1 = conn.createStatement();
			ResultSet rs1 = stmt1.executeQuery(strSql);
			while (rs1.next())
			{
				String strLeaderMail = rs1.getString("leader_mail") + "," + rs1.getString("prj_cname");
				String strPrjName = rs1.getString("prj_name");
				logger.info("checking ..."+strPrjName);
				strSql = " select * from bss_rpt where prj_id="+rs1.getLong("prj_id")+" and areaname='"
						+ strModule
						+ "' order by build_date desc  limit 3  ";
				Statement stmt2 = conn.createStatement();
				ResultSet rs2 = stmt2.executeQuery(strSql);
				logger.debug(strSql);
				boolean bSendMail = true;
				String sMailText = AiFileUtil.getFileTxt("notify.html");
				sMailText = sMailText.replaceAll("#wupf1", strPrjName);
				StringBuffer sbMail = new StringBuffer();
				while (rs2.next())
				{
					logger.info(rs2.getDouble("kpi"));
					if (rs2.getDouble("kpi") > 0.8)
					{
						bSendMail = false;
						break;
					}	
						// sMailText += rs2.getString("build_date") + ":" +
						// rs2.getDouble("kpi") + "%<br>";
						sbMail.append(
								"<tr style=\"HEIGHT: 16.5pt\" height=22><td class=xl66 style=\"border:0.5pt solid windowtext; HEIGHT: 16.5pt; WIDTH: 188px;\"  height=22 align=\"center\">")
								.append(rs2.getString("build_date"))
								.append("</td><td class=xl66 style=\"border:0.5pt solid windowtext; HEIGHT: 16.5pt; WIDTH: 188px;\"  height=22 align=\"center\">")
								.append(rs2.getDouble("kpi")*100).append("%</td></tr>");
					
				}
				if (bSendMail)
				{

					sMailText = sMailText.replaceAll("#wupf2", sbMail.toString());
					AiMail.sendMail(strLeaderMail, sMailText, "UTF-8", strPrjName + "完成质量改进目标告警(自动发出)");
					//JpfMail.sendMail("", sMailText, "UTF-8", strPrjName + "完成质量改进目标告警(自动发出)");
				}
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
	 * update 2015年10月13日
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		if ( args.length>0)
		{
			VisualWallNotify cVisualWallNotify = new VisualWallNotify(args[0]);
		}
		System.out.println("game over");
	}

}
