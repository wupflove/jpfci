/** 
* @author 吴平福 
* E-mail:wupf@asiainfo-linkage.com 
* @version 创建时间：2012-8-14 下午4:04:05 
* 类说明 
*/ 

package org.jpf.svn;

import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jpf.utils.ios.AiFileUtil;



/**
 * 
 */
public class CleanSvnFile
{
	private static final Logger logger = LogManager.getLogger();
	/**
	 * 
	 */
	public CleanSvnFile(String strFilePath,boolean verbose)
	{
		// TODO Auto-generated constructor stub
		DoWork(strFilePath,verbose);
	}
	   private void DoWork(String strFilePath,boolean verbose)
	   {
		   	if (verbose)
		   		logger.debug("check file path:="+strFilePath);
		   if (strFilePath==null || "".equalsIgnoreCase(strFilePath))
			{
			   logger.error("input param is null");
				return;
			}
			try
			{
				Vector<String> vector=new Vector<String>();
				AiFileUtil.getFiles(strFilePath, vector);
				if (verbose)
					logger.info("check file count:="+vector.size());
				for(int i=0;i<vector.size();i++)
				{
					String tmpString=(String)vector.get(i);
					
					//if(JpfStringUtil.IsChinese(tmpString))
					if(tmpString.indexOf(".svn")>0)
					{
						//LOGGER.info(tmpString);
						tmpString=tmpString.substring(0,tmpString.indexOf(".svn")+4);
						logger.info(tmpString);
						
						AiFileUtil.delDirWithFiles(tmpString);
					}
				}
				logger.info("game over");
			} catch (Exception ex)
			{
				// TODO: handle exception
				logger.error(ex);
			}
	   }
	/**
	 * @param args
	 * 被测试类名：TODO
	 * 被测试接口名:TODO
	 * 测试场景：TODO
	 * 前置参数：TODO
	 * 入参：
	 * 校验值：
	 * 测试备注：
	 * update 2012-8-14
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		CleanSvnFile cCleanSvnFile=new CleanSvnFile("D:\\svn\\ecommerce-branch-20170912",true);
	}

}
