/** 
 * @author 吴平福 
 * E-mail:421722623@qq.com 
 * @version 创建时间：2015年2月8日 下午11:07:00 
 * 类说明 
 */

package org.jpf.ci.dbs.compare;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jpf.utils.AiDateTimeUtil;
import org.jpf.utils.dbsql.AiDBUtil;
import org.jpf.utils.ios.AiOsUtil;
import org.jpf.utils.mails.AiMail;

/**
 * 
 */
public abstract class AbstractDbCompare
{
	private static final Logger LOGGER = LogManager.getLogger();

	protected StringBuffer[] sb = { new StringBuffer(), new StringBuffer(),
			new StringBuffer(), new StringBuffer(), new StringBuffer(),
			new StringBuffer(), new StringBuffer(), new StringBuffer(), new StringBuffer(), new StringBuffer(),
			new StringBuffer() };

	protected StringBuffer[] errInfoSb = { new StringBuffer(), new StringBuffer(), new StringBuffer(),
			new StringBuffer(), new StringBuffer(), new StringBuffer() };

	// 域名-表1或域名-索引1
	protected LinkedHashMap<String, TreeMap<String, AiTable>> domain_table = new LinkedHashMap<String, TreeMap<String, AiTable>>();
	// 域名-表2或域名-索引2
	protected LinkedHashMap<String, TreeMap<String, AiTable>> domain_table2 = new LinkedHashMap<String, TreeMap<String, AiTable>>();

	protected Vector<ExecSqlInfo> vSql = new Vector<ExecSqlInfo>();

	// 保存执行错误的SQL
	protected Vector<ErrExecSqlInfo> vErrSql = new Vector<ErrExecSqlInfo>();

	// 数据库与数据库比对
	abstract void doWork(Connection conn_product, Connection conn_develop, CompareInfo cCompareInfo) throws Exception;

	// pdm与数据库比对
	abstract void doWork(Connection conn_develop, CompareInfo cCompareInfo) throws Exception;

	// pdm与pdm比对
	abstract void doWork(CompareInfo cCompareInfo) throws Exception;
	// abstract void DoWork(Map<String,Table> map, Connection conn_develop,
	// CompareInfo cCompareInfo) throws Exception;

	abstract String getHtmlName();

	abstract String getErrorHtmlName();

	abstract String getExecSqlHtmlName();

	abstract String getMailTitle();

	CompareResult cCompareResult=new CompareResult();
	private String strSaveDate = AiDateTimeUtil.getCurrDateTime();

	public LinkedHashMap<String, TreeMap<String, AiTable>> getDomain_table2()
	{
		return domain_table2;
	}

	public void setDomain_table2(
			LinkedHashMap<String, TreeMap<String, AiTable>> domain_table2)
	{
		this.domain_table2 = domain_table2;
	}

	public LinkedHashMap<String, TreeMap<String, AiTable>> getDomain_table()
	{
		return domain_table;
	}

	public void setDomain_table(
			LinkedHashMap<String, TreeMap<String, AiTable>> domain_table)
	{
		this.domain_table = domain_table;
	}

	abstract void insertResult(CompareInfo cCompareInfo);


	protected String showResult()
	{
		return "";
	}

	String getMailTitle_pdm()
	{
		return "PDM与PDM比对(自动发出)";
	}

	/**
	 * @todo: 保存变更记录
	 */
	private void saveExecSql(ExecSqlInfo cExecSqlInfo, Connection conn_develop, CompareInfo cCompareInfo, String strMsg)
	{
		String strSql = "";
		try
		{
			cExecSqlInfo.setStrSql(cExecSqlInfo.getStrSql().replaceAll("`", ""));
			cExecSqlInfo.setStrSql(cExecSqlInfo.getStrSql().replaceAll("'", ""));
			if (strMsg != null && strMsg.length() > 0)
			{
				strMsg = strMsg.replaceAll("'", "");
			}
			strSql = "insert into dd.dbchangehis(table_name,exectype,domain,changesql,state_date,reversesql,execmsg)values('"
					+ cExecSqlInfo.getStrTable()
					+ "',"
					+ cExecSqlInfo.getiType()
					+ ",'"
					+ cCompareInfo.getDbDomain()
					+ "','" + cExecSqlInfo.getStrSql() + "',now(),'','" + strMsg + "')";
			AiDBUtil.execUpdateSql(conn_develop, strSql, 0);

		} catch (Exception ex)
		{
			// TODO: handle exception
			LOGGER.error(strSql);
			ex.printStackTrace();
		}
	}

	/**
	 * @todo: 执行差异的SQL
	 */
	private void execDiffSql(Connection conn_develop, CompareInfo cCompareInfo)
	{
		if (cCompareInfo.getDoExecSql() == 1)
		{

			CompareSelfCheck.checkSaveSqlDb(conn_develop);

			long sTime = System.currentTimeMillis();
			String strMsg = "";
			for (int i = 0; i < vSql.size(); i++)
			{
				if (i % 100 == 0 && i > 0)
				{
					long eTime = System.currentTimeMillis();
					LOGGER.info(i + "/" + vSql.size() + " 处理用时(单位MS):" + (eTime - sTime));
					sTime = System.currentTimeMillis();
				}
				strMsg = "";

				ExecSqlInfo cExecSqlInfo = vSql.get(i);
				try
				{
					if (cExecSqlInfo.getStrSql().startsWith("DROP"))
					{
						ErrExecSqlInfo cErrExecSqlInfo = new ErrExecSqlInfo(cExecSqlInfo.getStrSql(), "no exec");
						vErrSql.add(cErrExecSqlInfo);
					} else
					{
					    AiDBUtil.execUpdateSql(conn_develop, cExecSqlInfo.getStrSql());
					}

				} catch (Exception ex)
				{
					// TODO: handle exception
					ex.printStackTrace();
					strMsg = ex.getMessage();
					ErrExecSqlInfo cErrExecSqlInfo = new ErrExecSqlInfo(cExecSqlInfo.getStrSql(), strMsg);
					vErrSql.add(cErrExecSqlInfo);
				}
				try
				{
					saveExecSql(vSql.get(i), conn_develop, cCompareInfo, strMsg);
				} catch (Exception ex2)
				{
					// TODO: handle exception
					ex2.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 */
	public void doCompare(Connection conn_product, Connection conn_develop, CompareInfo cCompareInfo)
	{
		// TODO Auto-generated constructor stub
		try
		{
			long sTime = System.currentTimeMillis();
			if (conn_product == null && conn_develop == null)
			{
				doWork(cCompareInfo);
				CompareMailsUtil.pdmComSendMail(sb, getMailTitle_pdm(), cCompareInfo, getHtmlName(), showResult());
			} else if (conn_product == null)
			{
				//CompareUtil.cleanBuf(cCompareInfo.getDbDomain());
				doWork(conn_develop, cCompareInfo);
				execDiffSql(conn_develop, cCompareInfo);
				if (cCompareInfo.getDoExecSql() == 1)
				{
					CompareMailsUtil.sendExecSqlMail(vErrSql, getMailTitle(), cCompareInfo, getExecSqlHtmlName(),
							showResult());
				} else
				{
					CompareMailsUtil.sendResultMail(sb, getMailTitle(), cCompareInfo, getHtmlName(), showResult());
				}

			} else
			{
				//CompareUtil.cleanBuf(cCompareInfo.getDbDomain());
				doWork(conn_product, conn_develop, cCompareInfo);
				execDiffSql(conn_develop, cCompareInfo);
				if (cCompareInfo.getDoExecSql() == 1)
				{
					CompareMailsUtil.sendExecSqlMail(vErrSql, getMailTitle(), cCompareInfo, getExecSqlHtmlName(),
							showResult());
				} else
				{
					CompareMailsUtil.sendResultMail(sb, getMailTitle(), cCompareInfo, getHtmlName(), showResult());
				}

			}

			sb = null;
			domain_table = null;
			domain_table2 = null;

			insertResult(cCompareInfo);
			long eTime = System.currentTimeMillis();
			LOGGER.info("处理用时(单位MS):" + (eTime - sTime));
		} catch (SQLException ex)
		{
			// TODO: handle exception
			ex.printStackTrace();
			AiMail.sendMail(cCompareInfo.getStrMails(), ex.getMessage() + "<br>" + cCompareInfo.getDevJdbcUrl()
					+ "<br>ErrorCode:" + ex.getErrorCode()
					+ "<br>Compare Database:" + cCompareInfo.getDbDomain()
					+ "<br>ip:" + AiOsUtil.getInstance().getLocalIP(),
					"GBK", cCompareInfo.getStrCondName() + "数据库比对SQL异常");

		} catch (Exception ex)
		{
			// TODO: handle exception
			ex.printStackTrace();
			AiMail.sendMail("", ex.getMessage() + "<br>" + cCompareInfo.getDevJdbcUrl(),
					"GBK", "数据库比对异常");
		}

	}


}
