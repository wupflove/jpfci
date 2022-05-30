/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2016年3月9日 下午9:39:09 
* 类说明 
*/ 

package org.jpf.ci.sonarplugin;


import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 
 */
public class GitUsr
{


	private static final Logger logger = LogManager.getLogger();


	/**
	 * 
	 * @param PrjName
	 * @param PrjPath
	 * @param strCfgType
	 * @param strType
	 */
	public GitUsr()
	{


	}
	
	public Vector<FileUsr> getFileUsr(String PrjName, String PrjPath, String strCfgType,String strType)
	{
		Vector<FileUsr> vFileUsr = new Vector<FileUsr>();
	
		String strCmd = "cd " + PrjPath + ";svn status -v";
		logger.info(strCmd);
		try
		{
			String[] cmd = new String[] { "csh", "-c", strCmd };
			Process process = Runtime.getRuntime().exec(cmd);
			InputStreamReader ir = new InputStreamReader(process.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			
			String line;
			while ((line = input.readLine()) != null)
			{

				if (FileUsrFilter.IsCheckFile(line.trim()))
				{
					FileUsr cFileUsr=new FileUsr();
					FormatFileUsr(cFileUsr,line);
					vFileUsr.add(cFileUsr);
					System.out.println(line);
				}
			}
			process.waitFor();
			int iRetValue = process.exitValue();


			logger.info("gVector.size=" + vFileUsr.size());
			logger.info("lOutFileCount=" + lOutFileCount);
		}catch(Exception ex)
		{
			logger.error(ex);
		}
		return vFileUsr;
	}

	static String regEx = "['   ']+";


	private long lTotalSql = 0;
	private long lOutFileCount = 0;

	private void FormatFileUsr(FileUsr cFileUsr,String strInput)
	{

		strInput = strInput.trim();

			String[] mResult = strInput.split(regEx);
			// System.out.println(mResult.length);
			if (5 == mResult.length || 4 == mResult.length)
			{

					cFileUsr.setStrFileName( mResult[mResult.length-1].substring(mResult[4].lastIndexOf("/") + 1));
					cFileUsr.setStrFilePath(mResult[mResult.length-1].trim());
					cFileUsr.setStrUsr( mResult[mResult.length-2]);
					cFileUsr.Check();

				lTotalSql++;
			} else
			{
				System.out.println(strInput);
				lOutFileCount++;
			}

		
	}


}
