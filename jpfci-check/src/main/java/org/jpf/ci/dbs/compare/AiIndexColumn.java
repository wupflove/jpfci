/** 
 * @author 吴平福 
 * E-mail:wupf@asiainfo.com 
 * @version 创建时间：2015年2月8日 下午11:07:00 
 * 类说明 
 */

package org.jpf.ci.dbs.compare;

/**
 * 
 */
public class AiIndexColumn
{
	
	public AiIndexColumn(String columnName, int seqIndex)
	{
		this.columnName=columnName;
		this.seqIndex=seqIndex;
	}
	private String columnName;
	private int seqIndex;
	
	public int getSeqIndex()
	{
		return seqIndex;
	}
	public void setSeqIndex(int seqIndex)
	{
		this.seqIndex = seqIndex;
	}
	public String getColumnName()
	{
		return columnName;
	}
	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}
}
