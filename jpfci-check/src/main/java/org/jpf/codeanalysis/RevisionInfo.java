/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2017年5月22日 上午8:04:24 
* 类说明 
*/ 

package org.jpf.codeanalysis;

/**
 * 
 */
public class RevisionInfo {

    /**
     * 
     */
    public RevisionInfo() {
        // TODO Auto-generated constructor stub
    }
    
    private static long Old_Revison;
    private static long New_Revison;
    private static String SvnUrl;
    /**
     * @return the svnUrl
     */
    public static String getSvnUrl() {
        return SvnUrl;
    }
    /**
     * @param svnUrl the svnUrl to set
     */
    public static void setSvnUrl(String svnUrl) {
        SvnUrl = svnUrl;
    }
    /**
     * @return the old_Revison
     */
    public static long getOld_Revison() {
        return Old_Revison;
    }
    /**
     * @param old_Revison the old_Revison to set
     */
    public static void setOld_Revison(long old_Revison) {
        Old_Revison = old_Revison;
    }
    /**
     * @return the new_Revison
     */
    public static long getNew_Revison() {
        return New_Revison;
    }
    /**
     * @param new_Revison the new_Revison to set
     */
    public static void setNew_Revison(long new_Revison) {
        New_Revison = new_Revison;
    }

    public static String getSvnFileOldUrl(String strFileName)
    {
        return strFileName+"?p="+Old_Revison;
    }

    public static String getSvnFileNewUrl(String strFileName)
    {
        return strFileName+"?p="+New_Revison;
    }
}
