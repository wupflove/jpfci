/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2016年5月3日 下午6:36:46 
* 类说明 
*/ 

package org.jpf.ci.dbs.compare;

/**
 * 
 */
public class CompareResult {

    protected int iCount1 = 0;
    protected int iCount2 = 0;
    protected int iCount3 = 0;
    protected int iCount4 = 0;
    protected int iCount5 = 0;
    protected int iCount6 = 0;
    protected int iCount7 = 0;
    protected int iCount8 = 0;
    protected int iCount9 = 0;
    protected int iCount10 = 0;
    
    protected String showIndexsResult() {
        return "<tr><td>" + iCount1 + "</td><td>" + iCount2 + "</td><td>"
                + iCount3 + "</td><td>" + iCount4 + "</td><td>" + iCount5
                + "</td></tr>";
    }
    
    protected String showTableResult()
    {
        return "<tr><td>" + iCount1 + "</td><td>"
                + iCount2 + "</td><td>"
                + iCount3 + "</td><td>"
                + iCount4 + "</td><td>"
                + iCount5 + "</td><td>"
                + iCount6 + "</td><td>"
                + iCount7 + "</td><td>"
                + iCount8 + "</td><td>"
                + iCount9 + "</td><td>"
                + iCount10 + "</td></tr>";
    }
}
