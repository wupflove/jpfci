/**
 * 
 */

package org.jpf.ci.checks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jpf.utils.ios.AiFileUtil;


/**
 * 
 */
public class CheckVariable
{
	private static final Logger logger =  LogManager.getLogger(CheckVariable.class);

	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		if (2 == args.length)
		{
			CheckVariable cCheckVariable = new CheckVariable(args[0], args[1]);
		}else {
			//CheckVariable cCheckVariable = new CheckVariable("D:\\ob60\\rating_billing\\", "d:\\result_util.xml");
			test();
		}
	}
	private static void test()
	{
		String s = "��� char aa[vh]"; 
		//char 			szBuff[256] ="";
		//String regEx = ".+char"; //��ʾa��F  
		String regEx = "char +[a-zA-Z0-9]+ *\\[[a-zA-Z0-9 ]"; //��ʾa��F  
		Pattern pat = Pattern.compile(regEx);  
		Matcher mat = pat.matcher(s);  
		boolean rs = mat.find();
		if(rs)
		{
			System.out.println("a");
		}
	}
	private String strInputFilePath = "";
	private String strOutPutFileName = "";
    private String[] strFileSuffix={".cpp",".h"};
	public CheckVariable(String strInputFilePath, String strOutPutFileName)
	{
		this.strInputFilePath = strInputFilePath;
		this.strOutPutFileName = strOutPutFileName;
		DoCheck();
	}
	private boolean CheckFileType(String[] strFileSuffix, String strFileName)
	{
		for (int i = 0; i < strFileSuffix.length; i++)
		{
			if (strFileName.endsWith(strFileSuffix[i]))
				return true;
		}
		return false;
	}
	private boolean CheckFileName(String strFileName)
	{
		//
		if(strFileName.indexOf("\\test\\")>0)
		{
			return false;
		}
		if(strFileName.indexOf("\\samples\\")>0)
		{
			return false;
		}
		return true;
	}
	private void DoCheck()
	{
		try
		{
			// ������
			StringBuilder sBuffer = new StringBuilder();
			// ��ȡ�ļ�
			Vector<String> vFileName = new Vector<String>();
			AiFileUtil.getFiles(strInputFilePath, vFileName);
			logger.info("total file count="+vFileName.size());
			// ���ļ����
			
			for (int i = 0; i < vFileName.size(); i++)
			{
				String strFileName = vFileName.get(i);
				File f = new File(strFileName);
				if (!f.canRead())
				{
					continue;
				}
				if (!CheckFileType(strFileSuffix, strFileName))
				{
					continue;
				}
				if (!CheckFileName( strFileName))
				{
					continue;
				}
				//logger.info("current file name="+strFileName);
				InputStreamReader read = new InputStreamReader(new FileInputStream(f), "GBK");

				BufferedReader reader = new BufferedReader(read);

				String line;
				Pattern pattern = Pattern.compile("^[char]+$");
				int iLineNum=0;
				while ((line = reader.readLine()) != null)
				{
					iLineNum++;
					if(line.indexOf("char ")>=0 && line.indexOf("[")>0 && (line.indexOf("] ;")>0 || line.indexOf("];")>0) && line.indexOf("*")<0 && line.indexOf("//")<0 && line.indexOf("=")<0)
					{
						System.out.println(strFileName+" "+iLineNum+":"+line);
						Matcher matcher = pattern.matcher(line);
						if(matcher.find())
						{
							System.out.println(line);
						}
						sBuffer.append(strFileName).append(" ").append(iLineNum).append(":").append(line.trim()).append("\n");
					}
					// sb.append(line).append("\n");

				}
				reader.close();
				read.close();
				if (i % 10 == 0)
				{
					logger.info(i + "/" + vFileName.size());
				}
			}
				AiFileUtil.saveFile(
					strOutPutFileName,
					sBuffer);
			sBuffer.setLength(0);

		} catch (Exception ex)
		{
			// TODO: handle exception
			logger.error(ex);
		}
	}
}
