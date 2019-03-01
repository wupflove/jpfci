/** 
 * @author 吴平福 
 * E-mail:wupf@asiainfo.com 
 * @version 创建时间：2016年5月6日 下午2:46:36 
 * 类说明 
 */

package org.jpf.exploretest;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;

import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.ExecutionContext;

import org.apache.http.protocol.HttpContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpf.utils.xmls.JpfXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.jpf.utils.ios.AiFileUtil;

/**
 * 
 */
public class RunLogin {
    private final static String LOGIN_XML ="exploretest.xml"; 
    private static final Logger logger = LogManager.getLogger();
    private final static String COOLIE_VALUE = "JSESSIONID";
    final String strchar = "GBK";
    final String strNodeName = "explore_login_conf";
    private String user = "";
    private String passd = "";
    private String code = "";
    private String service = "";
    private String top_page = "";
    private String url_login = "";
    private String tbToken = "";
    private static HttpResponse response = null;
    HttpContext context = null;
    public static final DefaultHttpClient httpclient = new DefaultHttpClient();

    public boolean LoginRun() {
        try {
            AiFileUtil.checkFile(LOGIN_XML);

            NodeList n = JpfXmlUtil.getNodeList(strNodeName, LOGIN_XML);

            if (1 == n.getLength())
            {
                Element el = (Element) n.item(0);
                top_page = JpfXmlUtil.getParStrValue(el, "top_page");
                url_login = JpfXmlUtil.getParStrValue(el, "url_login");
                user = JpfXmlUtil.getParStrValue(el, "username");
                passd = JpfXmlUtil.getParStrValue(el, "password");
                code = JpfXmlUtil.getParStrValue(el, "verify_Code");
                service = JpfXmlUtil.getParStrValue(el, "service");

            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // 进行登录操作
        HttpPost httppost = new HttpPost(url_login);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", user));
        params.add(new BasicNameValuePair("password", passd));
        params.add(new BasicNameValuePair("service", service));
        params.add(new BasicNameValuePair("verifyCode", code));
   
       
        try {
            httppost.setEntity(new UrlEncodedFormEntity(params));
            // 获得一个httpresponse对象
            response = httpclient.execute(httppost);
            httppost.releaseConnection();
            // 登录到主页面
            HttpGet httpget = new HttpGet(top_page);
            // 将url发送到服务器
            response = httpclient.execute(httpget);
            // logger.debug(EntityUtils.toString(response.getEntity(),
            // "utf-8"));

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                getTbToken();
                logger.info("登陆请求成功，post返回状态:" + response.getStatusLine().getStatusCode());
            } else {

                logger.info("登陆请求出错，post返回状态:" + response.getStatusLine().getStatusCode());
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            httppost.abort();
        }

        return true;
    }

    private String getRedirectUrl() {

        HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute(
                ExecutionContext.HTTP_REQUEST);
        HttpHost currentHost = (HttpHost) context.getAttribute(
                ExecutionContext.HTTP_TARGET_HOST);
        String currentUrl = (currentReq.getURI().isAbsolute()) ? currentReq.getURI().toString()
                : (currentHost.toURI() + currentReq.getURI());
        return currentUrl;
    }

    private void getTbToken() {

        CookieStore cookieStore = ((AbstractHttpClient) httpclient).getCookieStore();
        List<Cookie> cookies = ((AbstractHttpClient) httpclient)
                .getCookieStore().getCookies();
        if (cookies.isEmpty()) {
            logger.info("cookies is null!");
        } else {
            for (int i = 0; i < cookies.size(); i++) {
                Cookie cookie = cookies.get(i);
                if (cookie.getName().equals(COOLIE_VALUE)) {

                    tbToken = cookie.getValue();
                    logger.info(COOLIE_VALUE+"cookie:" + tbToken);
                }
            }
            SetHeader.GetInstance().setCookieString(COOLIE_VALUE+"="+tbToken);
        }
    }

    public static void main(String[] args) throws Exception {
        RunLogin rRunLogin = new RunLogin();
        rRunLogin.LoginRun();

    }

}
