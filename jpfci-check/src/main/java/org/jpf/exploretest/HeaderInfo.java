/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2016年5月18日 下午12:23:44 
* 类说明 
*/ 

package org.jpf.exploretest;

/**
 * 
 */
public class HeaderInfo {

    /**
     * 
     */
    public HeaderInfo() {
        // TODO Auto-generated constructor stub
    }

    /*
     *         loginPost.addHeader("Referer", loginUrl);
        loginPost.addHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
        loginPost.addHeader("User-Agent",
                        "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; InfoPath.2; Tablet PC 2.0)");
        loginPost.addHeader("Host", "login.taobao.com");
     */
    private String loginUrl;
    private String contentType;
    private String userAgent;
    private String host;
}
