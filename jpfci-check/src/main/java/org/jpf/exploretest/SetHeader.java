/** 
 * @author 谭亮 
 * E-mail:tanliang@asiainfo.com 
 * @version 创建时间：2016-5-25 下午3:04:18 
 * 类说明 
 */

package org.jpf.exploretest;


import org.apache.http.client.methods.HttpPost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 
 */
public class SetHeader {

    /**
     * @category author 谭亮
     * @param args
     *            update 2016-5-25
     * @return 
     */
    private final static Logger logger = LogManager.getLogger();
    private String cookieString="";
    public String getCookieString() {
        return cookieString;
    }
    public  void setCookieString(String cookieString) {
        this.cookieString = cookieString;
    }
    public SetHeader()
    {
        
    }
    public static SetHeader cReadHeader = new SetHeader();
    public static SetHeader GetInstance() {
        return cReadHeader;
    }
    public HttpPost sSetHeader(HttpPost loginPost)
    {
     
        loginPost.addHeader("Accept", "*/*");
        loginPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        loginPost.addHeader("Accept-Encoding", "gzip, deflate");
        loginPost.addHeader("Referer", "http://10.1.241.75:9020/iBoss/page/modules/postView/adminView.jsp");
        loginPost.addHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        loginPost.addHeader("X-Requested-With","XMLHttpRequest");
        loginPost.addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; rv:46.0) Gecko/20100101 Firefox/46.0");
        loginPost.addHeader("Host", "10.1.241.75:9020");
        loginPost.addHeader("Connection", "keep-alive");
        loginPost.addHeader("cookie", getCookieString());
        logger.debug("cookie_value:"+getCookieString());
        return loginPost;
    }


}
