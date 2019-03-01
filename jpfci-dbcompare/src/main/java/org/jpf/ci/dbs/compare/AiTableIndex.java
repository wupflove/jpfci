/** 
* @author 吴平福
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2015年1月15日 上午8:47:45 
* 类说明
*/  

package org.jpf.ci.dbs.compare;

public class AiTableIndex
{

	private String indexName;
	private String colNames="";
	private int inon_unique=0;
	private String constraint_type="";
	
	public String getConstraint_type()
	{
		if (constraint_type!=null && constraint_type.equalsIgnoreCase("null"))
		{	
			return "";
		}
		if (constraint_type==null)
		{
			return "";
		}
		return constraint_type;
	}

	public void setConstraint_type(String constraint_type)
	{
		this.constraint_type = constraint_type;
	}

	public int getNON_UNIQUE()
	{
		return inon_unique;
	}

	public void setNON_UNIQUE(final int inNON_UNIQUE)
	{
		inon_unique = inNON_UNIQUE;
	}


	public String getIndexName()
	{
		return indexName;
	}

	public void setIndexName(String indexName)
	{
		this.indexName = indexName;
	}

	/**
	 * 
	 */
	public AiTableIndex( String indexName,final int inNON_UNIQUE)
	{
		// TODO Auto-generated constructor stub
		this.indexName = indexName;
		this.inon_unique=inNON_UNIQUE;
	}
	
	public void addColName(String columnName)
	{
		if(columnName!=null&&columnName!="")
		{
			colNames+=columnName+",";
		}
		

	}

	public String getColNames()
	{
		return colNames;
	}

	public void setColNames(String colNames)
	{
		this.colNames = colNames;
	}
}
