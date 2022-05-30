/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2017年6月3日 下午5:08:23 
* 类说明 
*/ 

package org.jpf.codeanalysis;

/**
 * 
 */
public class ParamInfo {
    String UserName = "liaocj";
    String PassWord = "liaocj";
    String strSvnUrl;
    String mails;
    
    /**
     * @return the mails
     */
    public String getMails() {
        return mails;
    }
    /**
     * @param mails the mails to set
     */
    public void setMails(String mails) {
        this.mails = mails;
    }
    /**
     * @return the userName
     */
    public String getUserName() {
        return UserName;
    }
    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        UserName = userName;
    }
    /**
     * @return the passWord
     */
    public String getPassWord() {
        return PassWord;
    }
    /**
     * @param passWord the passWord to set
     */
    public void setPassWord(String passWord) {
        PassWord = passWord;
    }
    /**
     * @return the strSvnUrl
     */
    public String getStrSvnUrl() {
        return strSvnUrl;
    }
    /**
     * @param strSvnUrl the strSvnUrl to set
     */
    public void setStrSvnUrl(String strSvnUrl) {
        this.strSvnUrl = strSvnUrl;
    }
    /**
     * 
     */
    public ParamInfo(String strCfgXmlFile) throws Exception{
        // TODO Auto-generated constructor stub
        
    }

}
