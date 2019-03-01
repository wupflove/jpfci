/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2015年8月21日 下午10:11:36 
* 类说明 
*/ 

package org.jpf.ci.dbs.dbchange;

import org.jpf.utils.ios.AiFileUtil;

/**
 * 
 */
public class ReadSqlFile
{

	/**
	 * 
	 */
	public ReadSqlFile()
	{
		// TODO Auto-generated constructor stub
		try
		{
			String strText = AiFileUtil.getFileTxt("/data01/usergrp/hudson/source/ob_dev/openbilling60/DDL/MySQL/sd/Telenor_REQ_20150804_0011_sd_sys_parameter.sql", "UTF-8").trim();
			strText = strText.replaceAll("`", "");
			
			String[] colsString = strText.split(System.getProperty("line.separator"));
			for (int n = 0; n < colsString.length; n++)
			{
				colsString[n]=colsString[n].trim();
				if (colsString[n].startsWith("--") )
				{
					continue;				
				}
				colsString[n]=colsString[n].replaceAll("commit[ ]+;", "");
				colsString[n]=colsString[n].replaceAll("commit;", "");
				if (colsString[n].length()==0)
				{
					continue;				
				}
				System.out.println(n+":"+colsString[n]);
			}
			
		} catch (Exception ex)
		{
			// TODO: handle exception
			ex.printStackTrace();
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
	 * update 2015年8月21日
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		ReadSqlFile cReadSqlFile=new ReadSqlFile();
	}

}
