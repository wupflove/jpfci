/** 
 * @author 吴平福 
 * E-mail:421722623@qq.com 
 * @version 创建时间：2012-9-4 下午12:53:43 
 * 类说明 
 */

package org.jpf.unittests.utrpt;

import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.xmls.JpfXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.jpf.utils.AiDateTimeUtil;
import org.jpf.utils.ios.AiFileUtil;
import org.jpf.utils.mails.AiMail;
import org.jpf.utils.scms.SVNUtil;

/**
 * 
 */
public class UnitTestRpt
{
	private static final Logger logger = LogManager.getLogger();

	/**
	 * 
	 */
	public UnitTestRpt()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public UnitTestRpt(String strFileInPath, String strFileOutPath, boolean verbose)
	{
		// TODO Auto-generated constructor stub
		DoWork(strFileInPath, strFileOutPath, verbose, AiDateTimeUtil.getDay("yyMMdd", -1));
	}

	public UnitTestRpt(String strFilePath, String strFileOutPath, boolean verbose, String strPrjName)
	{
		// TODO Auto-generated constructor stub
		logger.info("strFilePath=" + strFilePath);
		logger.info("strFileOutPath=" + strFileOutPath);
		logger.info("strPrjName=" + strPrjName);
		DoWork(strFilePath, strFileOutPath, verbose, strPrjName);
	}

	private String GetTestName(String strFileName)
	{
		String tmpString = AiFileUtil.getFileName(strFileName);
		if (tmpString.length() > 18)
		{
			tmpString = tmpString.substring(0, tmpString.length() - 18);
		}
		return tmpString.replaceAll("#", ".");
	}

	private boolean IsUnitXml(String strFileName, String keyString)
	{
		if ((strFileName.indexOf("#unittest_") < 0))
			return false;
		// if ((strFileName.indexOf(keyString) < 0))
		// return false;
		if (!(strFileName.endsWith(".xml")))
			return false;
		return true;
	}

	private void SaveTextXML(String strFileName, String strTestName, String strFileOutPath, String strPrjName)
			throws Exception
	{
		StringBuilder sbBuffer = new StringBuilder();
		logger.info("result file =" + strFileName);

		sbBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
				.append("<testsuites tests=\"5\" failures=\"2\" disabled=\"1\" errors=\"0\" time=\"0.261\" name=\"")
				.append(strTestName).append("\">\n");

		NodeList nl = JpfXmlUtil.getNodeList("test", strFileName);
		// System.out.println(nl.getLength());
		boolean IsAdd = false;
		String testsuite = "";
		for (int j = 0; j < nl.getLength(); j++)
		{
			// System.out.println(nl.item(j).getNodeValue());
			Element el = (Element) nl.item(j);

			// System.out.println(el.getAttribute("suite"));
			// System.out.println(el.getAttribute("name"));
			// System.out.println(el.getAttribute("time"));
			if (!testsuite.equalsIgnoreCase(el.getAttribute("suite")))
			{
				if (!testsuite.equalsIgnoreCase(""))
				{
					sbBuffer.append("</testsuite>\n");
				}
				testsuite = el.getAttribute("suite");
				sbBuffer.append("<testsuite name=\"")
						.append(strTestName)
						.append(".")
						.append(testsuite)
						.append("\" tests=\"5\" failures=\"2\" disabled=\"1\" errors=\"0\" time=\"0.192\">\n");
				IsAdd = true;
			}
			sbBuffer.append("<testcase name=\"").append(el.getAttribute("name"))
					.append("\" status=\"run\" time=\"").append(el.getAttribute("time"))
					.append("\" classname=\"").append(strTestName).append(".").append(testsuite)
					.append("\" >\n");

			if (nl.item(j).hasChildNodes())
			{
				NodeList sbunl = nl.item(j).getChildNodes();

				for (int m = 0; m < sbunl.getLength(); m++)
				{
					if (sbunl.item(m).getNodeName().equalsIgnoreCase("failure"))
					{
						Element subel = (Element) sbunl.item(m);
						String strFail = subel.getAttribute("message");
						System.out.println(strFail);
						strFail = strFail.replaceAll("\"", "");
						// for util
						strFail = strFail.replaceAll("&", "");
						strFail = strFail.replaceAll("<", " ");

						System.out.println(strFail);
						sbBuffer.append("<failure message=\"").append(strFail).append("\"></failure>");
					}
				}
			}
			sbBuffer.append("</testcase>");
		}

		AiFileUtil.delFile(strFileName);
		if (IsAdd)
		{
			sbBuffer.append("</testsuite>");
			sbBuffer.append("</testsuites>");
			AiFileUtil.saveFile(
					strFileOutPath + java.io.File.separator + strPrjName + ".unittest_"
							+ AiFileUtil.getFileName(strFileName),
					sbBuffer);
		}
		sbBuffer.setLength(0);
	}
	private void SendMail(String strFileName,String strMsg)
	{
		try
		{
		String strSvnPath="";
		int iPos=strFileName.lastIndexOf("test/report");
		if (iPos>1)
		{
			strSvnPath=strFileName.substring(0,iPos-1);
			logger.info(strSvnPath);
			strSvnPath=SVNUtil.getSvnFileAuthor(strSvnPath);
			logger.info(strSvnPath);
		}
		AiMail.sendMail(strSvnPath, strFileName+"\n\n"+strMsg, "", "单元测试解析错误");
		}catch(Exception ex)
		{
			logger.error(ex);
		}
	}
	private void DoWork(String strFilePath, String strFileOutPath, boolean verbose, String strPrjName)
	{
		logger.debug("check file path:=" + strFilePath);
		if (strFilePath == null || "".equalsIgnoreCase(strFilePath))
		{
			logger.error("input param is null");
			return;
		}
		try
		{
			Vector<String> vector = new Vector<String>();
			AiFileUtil.getFiles(strFilePath, vector);
			// 鏌ユ壘绗﹀悎鏉′欢鏂囦欢
			for (int i = 0; i < vector.size(); i++)
			{
				String tmpString = (String) vector.get(i);
				if (!IsUnitXml(tmpString, strPrjName))
				{
					vector.remove(i);
					i--;
				}
			}

			logger.debug("check file count:=" + vector.size());

			for (int i = 0; i < vector.size(); i++)
			{
				String strSourceFileName = (String) vector.get(i);
				logger.info("check file =" + strSourceFileName);
				if (AiFileUtil.isEmptyFile(strSourceFileName))
				{
					SendMail(strSourceFileName,"file length is zero");
					continue;
				}
				String strTestName = GetTestName(strSourceFileName);

				String tmpFileString = AiFileUtil.getFilePath(strSourceFileName) + i + ".xml";
				AiFileUtil.copyFile(tmpFileString, strSourceFileName);

				// 检查是否有多个xml内容在一个文件中
				try
				{
					String tmpXmlTxt = AiFileUtil.getFileTxt(tmpFileString, "UTF-8");
					String[] tmpXmlFile = tmpXmlTxt.split("<?xml version=");
					System.out.println("tmpXmlFile.length=" + tmpXmlFile.length);
					if (tmpXmlFile.length > 2)
					{
						for (int j = 1; j < tmpXmlFile.length; j++)
						{
							tmpFileString = AiFileUtil.getFilePath(strSourceFileName)+"_" +i+"_"+ j + ".xml";
							tmpXmlFile[j] = "<?xml version=" + tmpXmlFile[j];
							if (tmpXmlFile[j].endsWith("<?"))
							{
								tmpXmlFile[j] = tmpXmlFile[j].substring(0, tmpXmlFile[j].length() - 2);
							}
							System.out.println("---------------------------------------------------------------");
							System.out.println(tmpXmlFile[j]);
							AiFileUtil.saveFile(tmpFileString, tmpXmlFile[j]);
							SaveTextXML(tmpFileString, strTestName, strFileOutPath, strPrjName);
						}
						AiFileUtil.delFile(tmpFileString);
					}
					else
					{
						SaveTextXML(tmpFileString, strTestName, strFileOutPath, strPrjName);
					}
				} catch (Exception ex)
				{
					// TODO: handle exception
					ex.printStackTrace();
					SendMail(strSourceFileName,ex.getMessage());


				}

				logger.info("current file count:" + (i + 1) + "/" + vector.size());

			}

			/*
			 * sbBuffer.append("</testsuite></testsuites>"); //
			 * System.out.println(sbBuffer.toString()); FileUtil.SaveFile(
			 * strFileOutPath + java.io.File.separator + "unittest_all",
			 * sbBuffer); sbBuffer.setLength(0);
			 */
			logger.info("game over");
		} catch (Exception ex)
		{
			// TODO: handle exception
			ex.printStackTrace();
			logger.error(ex);
		}
	}

	/**
	 * @param args
	 *            update 2012-9-4
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

		if (args.length == 3)
		{
			UnitTestRpt unitTestRpt = new UnitTestRpt(args[0].trim(), args[1].trim(), true, args[2].trim());
		} else if (0 == args.length)
		{
			UnitTestRpt unitTestRpt = new UnitTestRpt("c:/temp/", "c:/temp/", false);

			String strFileName = "D:/jworkspaces/jpfapp/NewFile.xml";
			if ((strFileName.indexOf("#unittest") < 0))
				logger.debug(strFileName.indexOf("#unittest"));
			if (!strFileName.endsWith(".xml"))
				logger.debug("aa");
		} else
		{
			logger.warn("错误的参数个数:" + args.length);
		}

	}

}
