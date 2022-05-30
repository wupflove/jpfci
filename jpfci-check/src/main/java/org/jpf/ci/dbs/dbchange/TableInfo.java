/** 
 * @author 吴平福 
 * E-mail:421722623@qq.com 
 * @version 创建时间：2015年5月2日 下午2:31:28 
 * 类说明 
 */

package org.jpf.ci.dbs.dbchange;

/**
 * 
 */
public class TableInfo
{
	String strParentTable;
	String strSubTable;
	String strDbName;
	
	/**
	 * 
	 */
	public TableInfo(String strDbName, String  strParentTable, String strSubTable)
	{
		// TODO Auto-generated constructor stub
		this.strParentTable=strParentTable;
		this.strDbName=strDbName;
		this.strSubTable=strSubTable;
	}

}
