/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2014年4月16日 下午4:27:18 
* 类说明 
*/ 

package org.jpf.ci.rpts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.AiDateTimeUtil;
import org.jpf.utils.dbsql.AiDBUtil;
import org.jpf.utils.ios.AiFileUtil;
import org.jpf.utils.mails.AiMail;

/**
 * 
 */
public abstract class AbstractRpt
{
	protected static final Logger logger = LogManager.getLogger();

	// �ʼ�ģ������
	public abstract String GetTemplateName();

	// �ʼ�����
	public abstract String GetMailTitle();

	/**
	 * 
	 * @return ���ظ��������SQL update 2014��7��24��
	 */
	public abstract String GetSql(int iType, String strPrjName);

	/**
	 * 
	 */

	public abstract void DoMailText(StringBuffer sb, ResultSet rs) throws Exception;

	protected Double FormatDouble(Double d)
	{
		return (double) Math.round(d * 10) / 10;
	}
	protected int FormatInt(Double d)
	{
		return (int) Math.ceil(d ) ;
	}
	public AbstractRpt(String strPrjName, int iType, String strMailCC,String strCfg)
	{
		DoWeekRpt(strPrjName, iType, strMailCC,strCfg);
	}

	String strEndDate = AiDateTimeUtil.getDay("yyyy-MM-dd", -1);
	String strBeginDate = AiDateTimeUtil.getDay("yyyy-MM-dd", -7);

	private void GetAvailableDate(String strPrjName,String strCfg)
	{

		Connection conn = null;
		try
		{
			String strSql =
					"select distinct build_date from jpf_file_user2 where prj_name=?  and build_date<=? order by build_date desc;";
			logger.info("strSql={}", strSql);
			conn = RptDbConn.GetInstance().GetConn(strCfg);
			PreparedStatement pStmt = conn.prepareStatement(strSql);
			pStmt.setString(1, strPrjName);
			pStmt.setString(2, strEndDate);
			logger.info("strPrjName={}", strPrjName);
			logger.info("strEndDate={}", strEndDate);
			ResultSet rs = pStmt.executeQuery();
			if (rs.next())
			{
				strEndDate = rs.getString(1);
			}

			pStmt.setString(1, strPrjName);
			pStmt.setString(2, strBeginDate);
			rs = pStmt.executeQuery();
			if (rs.next())
			{
				strBeginDate = rs.getString(1);
			}
			logger.info("strBeginDate={}", strBeginDate);
			logger.info("strEndDate={}", strEndDate);
		} catch (Exception ex)
		{
			logger.error(ex);
			ex.printStackTrace();
		} finally
		{
		    AiDBUtil.doClear(conn);
		}

	}

	private void DoWeekRpt(String strPrjName, int iType, String strMailCC,String strCfg)
	{
		// TODO Auto-generated constructor stub
		// ɨ�豾��Ŀ¼�������ݿ��л�ȡ��¼
		Connection conn = null;

		try
		{

			GetAvailableDate(strPrjName,strCfg);

			conn = RptDbConn.GetInstance().GetConn(strCfg);

			Statement pStmt = conn.createStatement();
			String strSql = "delete from jpf_file_user where prj_name='" + strPrjName + "'";
			logger.info("strSql={}", strSql);
			pStmt.executeUpdate(strSql);

			strSql = "insert into jpf_file_user select prj_name,login,'" + strEndDate + "','" + strBeginDate
					+ "',kee,max(case t1.build_date when '" + strEndDate
					+ "' then t1.branch_coverage  end)brach_coverage1,max(case t1.build_date when '" + strBeginDate
					+ "' then t1.branch_coverage  end)brach_coverage2,"
					+ "max(case t1.build_date when '" + strEndDate
					+ "' then t1.major_violations  end)major_violations1,"
					+ " max(case t1.build_date when '" + strBeginDate
					+ "' then t1.major_violations  end)major_violations2,"
					+ " max(case t1.build_date when '" + strEndDate
					+ "' then t1.critical_violations  end)critical_violations1,"
					+ " max(case t1.build_date when '" + strBeginDate
					+ "' then t1.critical_violations  end)critical_violations2,"
					+ " max(case t1.build_date when '" + strEndDate + "' then t1.cover_branch  end)cover_branch1,"
					+ " max(case t1.build_date when '" + strBeginDate + "' then t1.cover_branch  end)cover_branch2, "
					+ " max(case t1.build_date when '" + strEndDate + "' then t1.un_cover_branch  end)un_cover_branch1,"
					+ " max(case t1.build_date when '" + strBeginDate + "' then t1.un_cover_branch  end)un_cover_branch2 "
					+ " from jpf_file_user2 t1 where prj_name='" + strPrjName + "' and login is not null "
					+ " group by kee order by kee;";
			logger.info("strSql={}", strSql);
			pStmt.executeUpdate(strSql);
			// conn.commit();
			MakeWeekRpt(strPrjName, iType, strMailCC,strCfg);

			// logger.debug("д���ļ����,��д��������" + iCount);
		} catch (Exception ex)
		{
			logger.error(ex);
			ex.printStackTrace();
		} finally
		{
		    AiDBUtil.doClear(conn);
		}
	}

	private void MakeWeekRpt(String strPrjName, int iType, String strMailCC,String strCfg)
	{
		// TODO Auto-generated constructor stub
		// ɨ�豾��Ŀ¼�������ݿ��л�ȡ��¼
		Connection conn = null;

		try
		{
			logger.info("mail template name:{}", GetTemplateName());
			String strDevHtmlString = AiFileUtil.getFileTxt(GetTemplateName());

			conn = RptDbConn.GetInstance().GetConn(strCfg);

			Statement pStmt = conn.createStatement();
			String strSql = GetSql(iType, strPrjName);

			logger.info("strSql={}", strSql);
			ResultSet rs = pStmt.executeQuery(strSql);
			StringBuffer sb = new StringBuffer();
			sb.setLength(0);
			DoMailText(sb, rs);

			strDevHtmlString = strDevHtmlString.replaceAll("#wupf3", sb.toString());

			logger.info("strMailCC=" + strMailCC);

			AiMail.sendMail(strMailCC, strDevHtmlString, "GBK", strPrjName + " --" + GetMailTitle() +"("+strBeginDate+"-"+strEndDate+")");

		} catch (Exception ex)
		{
			logger.error(ex);
			ex.printStackTrace();
		} finally
		{
		    AiDBUtil.doClear(conn);
		}
	}

}
