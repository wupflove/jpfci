/** 
 * @author 吴平福 
 * E-mail:wupf@asiainfo.com 
 * @version 创建时间：2015年5月2日 下午2:31:28 
 * 类说明 
 */

package org.jpf.ci.dbs.dbchange;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jpf.utils.ios.AiFileUtil;
import org.jpf.utils.mails.AiMail;



/**
 * 
 */
public class DbChangeMail
{
	private static final Logger logger =LogManager.getLogger(DbChange.class);
	private static String[] strMailTitle = {"DB变更执行成功(自动发送)","DB变更执行失败(自动发送)","DB变更执行成功但有告警(自动发送)"};
	

	public static String makeMailTxt(String strFileName, String strDate) {
        return "<br>文件名称：" + strFileName + "<br>执行时间：" + strDate + "<br>文件时间："
                + AiFileUtil.getFileDate(strFileName);
    }
    
	public static void SendMail(String strFileName,int iResult,String strMsg)
	{
		try
		{
			String strAuthor=GetFileAuthor(strFileName);
			AiMail.sendMail(strAuthor+",xuedl",strMailTitle[iResult] +"<br>"+strMsg, "GBK", strMailTitle[iResult]);
			//SonarMail.sendMail("wupf",strMailTitle[iResult] +"<br>"+strMsg, "GBK", strMailTitle[iResult]);
			DbChangeInfo cDbChangeInfo=new DbChangeInfo(strFileName,strAuthor,iResult);
		} catch (Exception ex)
		{
			// TODO: handle exception
			ex.printStackTrace();
			logger.error(ex);
		}

	}	
	/**
	 * 
	 * @category 
	 * @author 吴平福 
	 * @param strFileName
	 * @return
	 * @throws Exception
	 * update 2016年9月24日
	 */
	private static String GetFileAuthor(String strFileName) throws Exception
	{
		/* 判断是否是windows */
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");

		if (os.trim().toLowerCase().startsWith("windows"))
		{
			return "";
		}
		String strResult = "";
		// logger.info(strFileName);
		String strFilePath = AiFileUtil.getFilePath(strFileName);
		// logger.info(strFilePath);
		strFileName = AiFileUtil.getFileName(strFileName);
		// logger.info(strFileName);
		String strCmd = "cd " + strFilePath + ";svn info " + strFileName;

		String[] cmd = new String[] { "/bin/sh", "-c", strCmd };
		Process process = Runtime.getRuntime().exec(cmd);
		InputStreamReader ir = new InputStreamReader(process.getInputStream());
		LineNumberReader input = new LineNumberReader(ir);

		String line;
		while ((line = input.readLine()) != null)
		{
			line = line.trim();

			// MakeSql(line);
			// Sleep(1);
			if (line.startsWith("Last Changed Author:")||line.startsWith("最后修改的作者:"))
			{

				int i = line.indexOf(":");
				strResult = line.substring(i + 1).trim();
				logger.info(strResult);
			}
		}
		process.waitFor();
		// int iRetValue = process.exitValue();
		// setstrRshRet(iRetValue);
		
		return strResult;
	}
}
