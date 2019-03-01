/** 
 * @author 谭亮 
 * E-mail:tanliang@asiainfo.com 
 * @version 创建时间：2016-5-25 下午3:04:18 
 * 类说明 
 */

package org.jpf.exploretest;


import org.apache.http.client.methods.HttpPost;


/**
 * 
 */
public class ReadHeader {

    /**
     * @category author 谭亮
     * @param args
     *            update 2016-5-25
     * @return 
     */
    public ReadHeader()
    {
        
    }
    public static ReadHeader cReadHeader = new ReadHeader();
    public static ReadHeader GetInstance() {
        return cReadHeader;
    }
    public HttpPost rReadHeader(HttpPost loginPost)
    {
     
        loginPost.addHeader("Referer", "http://10.1.241.75:9020/iBoss/page/modules/postView/adminView.jsp");
        loginPost.addHeader("Content-Type","application/json;charset=UTF-8");
        loginPost.addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; rv:46.0) Gecko/20100101 Firefox/46.0");
        loginPost.addHeader("Host", "10.1.241.75:9020");
        return loginPost;
    }


}
