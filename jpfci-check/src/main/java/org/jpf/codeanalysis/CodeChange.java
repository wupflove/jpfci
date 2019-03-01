/**
 * @author wupf@asiainfo. com
 * 根据SVN编号查找代码变化情况，粒度到函数级别
 */
package org.jpf.codeanalysis;

import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CodeChange {
	private static final Logger LOGGER = LogManager.getLogger();
	
	//存放变化函数，带包名，函数中去掉变量，保留类型
	private Vector< String> vFunctions=new Vector<String>();
	public Vector<String> getvFunctions() {
		return vFunctions;
	}
	public void setvFunctions(Vector<String> vFunctions) {
		this.vFunctions = vFunctions;
	}
	/**
	 * 
	 */
	public CodeChange(String strSvnUrl,String strSvnUsr,String strSvnPwd,String strSvnNum1,String strNum2) {
		// TODO Auto-generated constructor stub
		LOGGER.debug("strSvnUrl={}",strSvnUrl);
		LOGGER.debug("strSvnUsr={}",strSvnUsr);
		LOGGER.debug("strSvnPwd={}",strSvnPwd);
		LOGGER.debug("strSvnNum1={}",strSvnNum1);
		LOGGER.debug("strNum2={}",strNum2);
	}

	class codechangeSvn
	{
		
	}
	class codechangeGit
	{

	}	
}
