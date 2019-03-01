/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2015年7月22日 下午11:10:05 
* 类说明 
*/

package org.jpf.ci.rpts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.xmls.JpfXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.jpf.utils.ios.AiFileUtil;

/**
 * 
 */
public class SonarRptInfo
{

	public static SonarRptInfo cSonarRptInfo= new SonarRptInfo();

	public static SonarRptInfo GetInstance()
	{
		return cSonarRptInfo;
	}
	
	private  String strDbUrl = "";
	private  String strDbUsr = "";
	private  String strDbPwd = "";
	private  String strCCMails = "";
	private  String strAreaInfo = "";
	private  String HtmlMail="";
	private  String PrjName="";
	//不计算覆盖率的模块数量
	private int CoverageExcludeCount=0;
	/**
	 * @return the coverageExcludeCount
	 */
	public int getCoverageExcludeCount()
	{
		return CoverageExcludeCount;
	}

	/**
	 * @param coverageExcludeCount the coverageExcludeCount to set
	 */
	public void setCoverageExcludeCount(int coverageExcludeCount)
	{
		CoverageExcludeCount = coverageExcludeCount;
	}

	/**
	 * @return the strDbUrl
	 */
	public String getStrDbUrl()
	{
		return strDbUrl;
	}

	/**
	 * @return the strDbUsr
	 */
	public String getStrDbUsr()
	{
		return strDbUsr;
	}

	/**
	 * @return the strDbPwd
	 */
	public String getStrDbPwd()
	{
		return strDbPwd;
	}

	/**
	 * @return the strCCMails
	 */
	public String getStrCCMails()
	{
		return strCCMails;
	}

	/**
	 * @return the strAreaInfo
	 */
	public String getStrAreaInfo()
	{
		return strAreaInfo;
	}
	/**
	 * @return the strhtmlmail
	 */
	public String getHtmlMail()
	{
		return HtmlMail;
	}
	
	public String getPrjName()
	{
		return PrjName;
	}
	private static final Logger logger = LogManager.getLogger();

	public void readRptInfo(String strCfgFile) throws Exception
	{
		logger.info(strCfgFile);
		AiFileUtil.checkFile(strCfgFile);

		NodeList n = JpfXmlUtil.getNodeList("sonar_rpt", strCfgFile);
		
		if (1 != n.getLength())
		{
			throw new Exception("Error Read Configxml");
		}
		// String URL = "jdbc:mysql://10.10.12.153:3333/sonar";
		Element el = (Element) n.item(0);
		strDbUrl = "jdbc:mysql://" + JpfXmlUtil.getParStrValue(el, "dburl");
		strDbUsr = JpfXmlUtil.getParStrValue(el, "dbusr");

		strDbPwd = JpfXmlUtil.getParStrValue(el, "dbpwd");
		
		strCCMails = JpfXmlUtil.getParStrValue(el, "avgccmail");
		strAreaInfo= JpfXmlUtil.getParStrValue(el, "areaconf");
		HtmlMail=JpfXmlUtil.getParStrValue(el, "htmlmail");
		PrjName=JpfXmlUtil.getParStrValue(el, "prjname");
		CoverageExcludeCount=JpfXmlUtil.getParIntValue(el, "CoverageExcludeCount",0);
		
	}
}
