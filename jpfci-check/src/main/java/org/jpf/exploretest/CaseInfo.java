/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2016年5月6日 下午2:31:36 
* 类说明 
*/ 

package org.jpf.exploretest;

import java.io.Serializable;

/**
 * 
 */
public class CaseInfo implements Serializable{

  //CASE的名字
    private String CaseName;
    //
    private String CaseUrl;
    private String CaseIndex;
    private String CaseResult;
    private String caseChangeString;
    
    public CaseInfo()
    {
        
    }
   
   
    /**
     * @return the caseName
     */
    public String getCaseName() {
        return CaseName;
    }
    /**
     * @param caseName the caseName to set
     */
    public void setCaseName(String caseName) {
        CaseName = caseName;
    }
    /**
     * @return the caseUrl
     */
    public String getCaseUrl() {
        return CaseUrl;
    }
    /**
     * @param caseUrl the caseUrl to set
     */
    public void setCaseUrl(String caseUrl) {
        CaseUrl = caseUrl;
    }
    /**
     * @return the caseIndex
     */
    public String getCaseIndex() {
        return CaseIndex;
    }
    /**
     * @param caseIndex the caseIndex to set
     */
    public void setCaseIndex(String caseIndex) {
        CaseIndex = caseIndex;
    }
    /**
     * @return the caseResult
     */
    public String getCaseResult() {
        return CaseResult;
    }
    /**
     * @param caseResult the caseResult to set
     */
    public void setCaseResult(String caseResult) {
        CaseResult = caseResult;
    }
    public String getCaseChange() {
        return caseChangeString;
    }


    public void setCaseChange(String caseChangeString) {
        this.caseChangeString = caseChangeString;
    }


    @Override
    public String toString() {
        return CaseUrl + "?"+CaseIndex;
    }
    
   
}
