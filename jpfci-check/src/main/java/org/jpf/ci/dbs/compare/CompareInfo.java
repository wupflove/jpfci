/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2015年5月18日 下午4:20:59 
* 类说明 
*/ 

package org.jpf.ci.dbs.compare;

/**
 * 
 */
public class CompareInfo
{
	/**
	 * 
	 */
	public CompareInfo()
	{
		// TODO Auto-generated constructor stub
	}
	
    private String strCondName;
    private String dbDomain;
    private String devJdbcUrl;
    //数据库用户名
    private String dbUsr;
  //数据库密码
    private String dbPwd;
    
    //数据库类型
    private String dbType="mysql";
    
    private String includesubtable="1";


	private String pdmInfo;
    private String strExcludeTable;

	private String strMails;
    
	//输出类型
    private int sendResulType;
    
    private String allMail;
    //比较类型
    private int compareType;
    
	//保存差异结果到数据库
    private int doSaveSql;
    
    //执行差异结果SQL
	private int doExecSql;
	
    //比较类型 表：1--10，索引 11-15
    private String resultTypes;
    
    //版本信息或日期信息
    private String pdmDtVers;
    
    private String pdmDbName;

	private String pdmJdbcUrl;
	
	private String sqlCondPdm="";
	
	private String sqlCondDb="";
	
    /**
     * @return the sqlCondPdm
     */
    public String getSqlCondPdm() {
        return sqlCondPdm;
    }

    /**
     * @param sqlCondPdm the sqlCondPdm to set
     */
    public void setSqlCondPdm(String sqlCondPdm) {
        this.sqlCondPdm = sqlCondPdm;
    }

    /**
     * @return the sqlCondDb
     */
    public String getSqlCondDb() {
        return sqlCondDb;
    }

    /**
     * @param sqlCondDb the sqlCondDb to set
     */
    public void setSqlCondDb(String sqlCondDb) {
        this.sqlCondDb = sqlCondDb;
    }

    /**
	 * @return the sendresulttype
	 */
	public int getSendresulttype()
	{
		return sendResulType;
	}

    public String getPdmDtVers() {
		return pdmDtVers;
	}
	public void setPdmDtVers(String pdmDtVers) {
		this.pdmDtVers = pdmDtVers;
	}
    
    public int getCompareType()
	{
		return compareType;
	}
	public void setCompareType(int compareType)
	{
		this.compareType = compareType;
	}
	public String getAllMail()
	{
		return allMail;
	}
	public void setAllMail(String allMail)
	{
		this.allMail = allMail;
	}

    
    public String getPdmJdbcUrl()
	{
		return pdmJdbcUrl;
	}
	public void setPdmJdbcUrl(String strPdmJdbcUrl)
	{
		this.pdmJdbcUrl = strPdmJdbcUrl;
	}
	public int getDoSaveSql()
	{
		return doSaveSql;
	}
	public void setDoSaveSql(int doSaveSql)
	{
		this.doSaveSql = doSaveSql;
	}


    
    public String getResultTypes()
	{
		return resultTypes;
	}
	public void setResultTypes(String ResultTypes)
	{
		this.resultTypes = ResultTypes;
	}


	/**
     * @return the doExecSql
     */
    public int getDoExecSql() {
        return doExecSql;
    }

    /**
     * @param doExecSql the doExecSql to set
     */
    public void setDoExecSql(int doExecSql) {
        this.doExecSql = doExecSql;
    }

    public String getStrMails()
	{
		return strMails;
	}
	public void setStrMails(String strMails)
	{
		this.strMails = strMails;
	}
	public String getStrCondName()
	{
		return strCondName;
	}
	public void setStrCondName(String strCondName)
	{
		this.strCondName = strCondName;
	}
	public String getDbDomain()
	{
		return dbDomain;
	}
	public void setDbDomain(String strDomain)
	{
		this.dbDomain = strDomain;
	}
	public String getDevJdbcUrl()
	{
		return devJdbcUrl;
	}
	public void setDevJdbcUrl(String strJdbcUrl)
	{
		this.devJdbcUrl = strJdbcUrl;
	}
	public String getPdmInfo()
	{
		return pdmInfo;
	}
	public void setPdmInfo(String strPdmInfo)
	{
		this.pdmInfo = strPdmInfo;
	}
	public String getStrExcludeTable()
	{
		return strExcludeTable;
	}
	public void setStrExcludeTable(String strExcludeTable)
	{
		this.strExcludeTable = strExcludeTable;
	}
	public String getPdmDbName() {
		return pdmDbName;
	}
	public void setPdmDbName(String pdmDbName) {
		this.pdmDbName = pdmDbName;
	}
    /**
     * @return the includesubtable
     */
    public String getIncludesubtable() {
        return includesubtable;
    }
    /**
     * @param includesubtable the includesubtable to set
     */
    public void setIncludesubtable(String includesubtable) {
        this.includesubtable = includesubtable;
    }
    /**
     * @return the dbType
     */
    public String getDbType() {
        return dbType;
    }
    /**
     * @param dbType the dbType to set
     */
    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
    /**
     * @return the sendResulType
     */
    public int getSendResulType() {
        return sendResulType;
    }
    /**
     * @param sendResulType the sendResulType to set
     */
    public void setSendResulType(int sendResulType) {
        this.sendResulType = sendResulType;
    }
    /**
     * @return the dbUsr
     */
    public String getDbUsr()
    {
        return dbUsr;
    }
    /**
     * @param dbUsr the dbUsr to set
     */
    public void setDbUsr(String dbUsr)
    {
        this.dbUsr = dbUsr;
    }
    /**
     * @return the dbPwd
     */
    public String getDbPwd()
    {
        return dbPwd;
    }
    /**
     * @param dbPwd the dbPwd to set
     */
    public void setDbPwd(String dbPwd)
    {
        this.dbPwd = dbPwd;
    }
}
