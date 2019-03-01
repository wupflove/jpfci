/** 
 * @author 吴平福 
 * E-mail:wupf@asiainfo.com 
 * @version 创建时间：2016年5月3日 上午8:06:09 
 * 类说明 
 */

package org.jpf.ci.dbs.compare;

import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jpf.utils.AiDateTimeUtil;
import org.jpf.utils.ios.AiFileUtil;
import org.jpf.utils.mails.AiMail;

/**
 * 
 */
public class CompareMailsUtil {
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * 
	 * @category author 吴平福
	 * @param errInfoSb
	 * @param strMailTitle
	 * @param strHtmlName
	 * @param cCompareInfo
	 *            update 2016年5月3日
	 */
	public static void sendErrorMail(StringBuffer[] errInfoSb,
			String strMailTitle, String strHtmlName, CompareInfo cCompareInfo) {
		try {
			LOGGER.info("write error File...");
			String strMailText = AiFileUtil.getFileTxt(strHtmlName);
			strMailText = strMailText.replaceAll("#wupf1",
					AiDateTimeUtil.getCurrDateTime());
			String tmpStr = errInfoSb[0].toString() + errInfoSb[1].toString()
					+ errInfoSb[2].toString() + errInfoSb[3].toString()
					+ errInfoSb[4].toString() + errInfoSb[5].toString();
			tmpStr = java.util.regex.Matcher.quoteReplacement(tmpStr);
			if (tmpStr.length() > 0) {
				strMailText = strMailText.replaceAll("#wupf4", tmpStr);
				AiMail.sendMail(
						cCompareInfo.getAllMail() , strMailText,
						"GBK", strMailTitle);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// HTML方式保存，文件名称递增数变量
	private static int iFileCount = 0;

	/**
	 * 
	 * @category author 吴平福
	 * @param sb
	 * @param strMailTitle
	 * @param cCompareInfo
	 * @param strHtmlName
	 * @param strTotalInfo
	 * @throws Exception
	 *             update 2016年5月3日
	 */
	public static void sendResultMail(StringBuffer[] sb, String strMailTitle,
			CompareInfo cCompareInfo, String strHtmlName, String strTotalInfo)
			throws Exception {
		LOGGER.debug("sendResult...");

		String strMailText = AiFileUtil.getFileTxt(strHtmlName);
		strMailText = strMailText.replaceAll("#wupf1",
				AiDateTimeUtil.getCurrDateTime());
		strMailText = strMailText.replaceAll("#wupf2",
				cCompareInfo.getPdmJdbcUrl());
		strMailText = strMailText
				.replaceAll("#wupf3", cCompareInfo.getDevJdbcUrl() + "/"
						+ cCompareInfo.getDbDomain());
		String tmpStr = sb[0].toString() + sb[1].toString() + sb[2].toString()
				+ sb[3].toString() + sb[4].toString() + sb[5].toString()
				+ sb[6].toString() + sb[7].toString();

		tmpStr = java.util.regex.Matcher.quoteReplacement(tmpStr);
		strMailText = strMailText.replaceAll("#wupf4", tmpStr);

		if (strTotalInfo != null) {
			strMailText = strMailText.replaceAll("#diffs", strTotalInfo);
		}
		// strMailText+=sbAlterSql.toString();
		// console output
		if (cCompareInfo.getSendresulttype() == 0
				|| cCompareInfo.getSendresulttype() == 1) {
			// LOGGER.info(strMailText);
		}
		// ouput html
		if (cCompareInfo.getSendresulttype() == 0
				|| cCompareInfo.getSendresulttype() == 2) {

			iFileCount++;
			String downPdmPath = AiFileUtil.getCurrentPath();
			AiFileUtil
					.saveFile(downPdmPath + iFileCount + ".html", strMailText);
			// LOGGER.info(strMailText);
		}
		// send mail
		if (cCompareInfo.getSendresulttype() == 0
				|| cCompareInfo.getSendresulttype() == 3) {
			// LOGGER.debug(strMailText);
			AiMail.sendMail(
					cCompareInfo.getStrMails(),
					strMailText,
					"GBK",
					cCompareInfo.getStrCondName() + strMailTitle + " 比对库"
							+ cCompareInfo.getDevJdbcUrl() + "/"
							+ cCompareInfo.getDbDomain());
		}
	}

	/**
	 * 
	 * @category author 吴平福
	 * @param v
	 * @param strMailTitle
	 * @param cCompareInfo
	 * @param strHtmlName
	 * @param strTotalInfo
	 * @throws Exception
	 *             update 2016年5月3日
	 */
	public static void sendExecSqlMail(Vector<ErrExecSqlInfo> v,
			String strMailTitle, CompareInfo cCompareInfo, String strHtmlName,
			String strTotalInfo) throws Exception {
		if (v.size() == 0) {
			return;
		}
		LOGGER.debug("send mails...");

		String strMailText = AiFileUtil.getFileTxt(strHtmlName);
		strMailText = strMailText.replaceAll("#wupf1",
				AiDateTimeUtil.getCurrDateTime());
		strMailText = strMailText.replaceAll("#wupf2",
				cCompareInfo.getPdmJdbcUrl());
		strMailText = strMailText
				.replaceAll("#wupf3", cCompareInfo.getDevJdbcUrl() + "/"
						+ cCompareInfo.getDbDomain());

		String tmpStr = "";
		for (int i = 0; i < v.size(); i++) {
			ErrExecSqlInfo cErrExecSqlInfo = (ErrExecSqlInfo) v.get(i);
			tmpStr += "<tr><td>" + (i + 1) + "</td><td>"
					+ cErrExecSqlInfo.getExecSql() + "</td><td>"
					+ cErrExecSqlInfo.getErrMsg() + "</td></tr>";

		}
		tmpStr = java.util.regex.Matcher.quoteReplacement(tmpStr);
		strMailText = strMailText.replaceAll("#wupf4", tmpStr);

		if (strTotalInfo != null) {
			strMailText = strMailText.replaceAll("#diffs", strTotalInfo);
		}
		AiMail.sendMail(
				cCompareInfo.getStrMails(),
				strMailText,
				"GBK",
				cCompareInfo.getStrCondName() + strMailTitle + " 比对库"
						+ cCompareInfo.getDevJdbcUrl() + "/"
						+ cCompareInfo.getDbDomain());
	}

	/**
	 * 
	 * @category author 吴平福
	 * @param sb
	 * @param strMailTitle
	 * @param cCompareInfo
	 * @param strHtmlName
	 * @param strTotalInfo
	 * @throws Exception
	 *             update 2016年5月3日
	 */
	public static void pdmComSendMail(StringBuffer[] sb, String strMailTitle,
			CompareInfo cCompareInfo, String strHtmlName, String strTotalInfo)
			throws Exception {
		LOGGER.debug("write Result File...");

		String strMailText = AiFileUtil.getFileTxt(strHtmlName);
		strMailText = strMailText.replaceAll("#wupf1",
				AiDateTimeUtil.getCurrDateTime());
		strMailText = strMailText.replaceAll("#wupf2",
				cCompareInfo.getPdmJdbcUrl());
		strMailText = strMailText
				.replaceAll("#wupf3", cCompareInfo.getDevJdbcUrl() + "/"
						+ cCompareInfo.getDbDomain());
		String tmpStr = sb[0].toString() + sb[1].toString() + sb[2].toString()
				+ sb[3].toString() + sb[4].toString() + sb[5].toString()
				+ sb[6].toString() + sb[7].toString();

		tmpStr = java.util.regex.Matcher.quoteReplacement(tmpStr);
		strMailText = strMailText.replaceAll("#wupf4", tmpStr);

		if (strTotalInfo != null) {
			strMailText = strMailText.replaceAll("#diffs", strTotalInfo);
		}
		// strMailText+=sbAlterSql.toString();
		AiMail.sendMail(
				cCompareInfo.getStrMails(),
				strMailText,
				"GBK",
				cCompareInfo.getStrCondName() + strMailTitle
						+ cCompareInfo.getPdmDtVers() + "/"
						+ cCompareInfo.getDbDomain());
	}
}
