/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2017年11月4日 下午6:12:52 
* 类说明 
*/ 

package org.jpf.codeanalysis.importscheck;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 */
public class CheckRule {

    private String PkgName="";
    private List<String> listExcludePkg=new LinkedList<String>();; 
    

    /**
     * @return the pkgName
     */
    public String getPkgName() {
        return PkgName;
    }


    /**
     * @param pkgName the pkgName to set
     */
    public void setPkgName(String pkgName) {
        PkgName = pkgName;
    }


    /**
     * @return the listExcludePkg
     */
    public List<String> getListExcludePkg() {
        return listExcludePkg;
    }


    /**
     * @param listExcludePkg the listExcludePkg to set
     */
    public void setListExcludePkg(List<String> listExcludePkg) {
        this.listExcludePkg = listExcludePkg;
    }


    /**
     * 
     */
    public CheckRule() {
        // TODO Auto-generated constructor stub
    }
    
    
}
