/** 
 * @author 吴平福 
 * E-mail:421722623@qq.com 
 * @version 创建时间：2016年5月6日 下午2:44:59 
 * 类说明 
 */

package org.jpf.exploretest;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.http.HttpResponse;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.alibaba.fastjson.JSON;

/**
 * 
 */
public class RunCase {
    private static final Logger logger = LogManager.getLogger();

    private static HttpResponse response = null;
    HttpPost httpPost = null;
    BufferedReader in = null;
    public static final DefaultHttpClient httpclient = new DefaultHttpClient();

    /**
     * 
     */
    public RunCase() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     * @category 运行CASE
     * @author 吴平福
     * @param mapCase
     *            update 2016年5月6日
     */
    public void runCaseFromMap(Vector<CaseInfo> mapCase) throws Exception {

        for (int i = 0; i < mapCase.size(); i++) {
            CaseInfo cCaseInfo = (CaseInfo) mapCase.get(i);
            logger.debug(cCaseInfo.getCaseResult());
            try {
                URL realUrl = new URL(cCaseInfo.getCaseResult());
                // 打开和URL之间的连接
                URLConnection connection = realUrl.openConnection();
                // 设置通用的请求属性

                connection.setRequestProperty("Accept", "*/*");
                connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
                connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                connection.setRequestProperty("Referer",
                        "http://10.1.241.75:9020/iBoss/page/modules/postView/adminView.jsp");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                connection.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; rv:46.0) Gecko/20100101 Firefox/46.0");
                connection.setRequestProperty("Host", "10.1.241.75:9020");
                connection.setRequestProperty("Connection", "keep-alive");
                connection.setRequestProperty("cookie", SetHeader.GetInstance().getCookieString());
                // 建立实际的连接
                connection.connect();
                // 获取所有响应头字段

                /*
                 * Map<String, List<String>> map = connection.getHeaderFields();
                 * // 遍历所有的响应头字段 for (String key : map.keySet()) {
                 * logger.debug(key + "--->" + map.get(key)); } // 定义
                 * BufferedReader输入流来读取URL的响应
                 */
                in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String line = "";
                String result = "";
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                if (null != result) {

                }
                logger.debug(result);

            } catch (Exception e) {
                logger.debug("发送GET请求出现异常！" + e);
                e.printStackTrace();
            }
            // 使用finally块来关闭输入流
            finally {
                try {
                    if (null != in) {
                        in.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }

    }

    public Map<String, String> getResponse(String result) {
        Map<String, String> map = new HashMap<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> json_value = (HashMap<String, String>) JSON.parseObject(result, Map.class);
        for (Map.Entry<String, String> entry : json_value.entrySet()) {
            if ("userId" == entry.getKey()) {
                try {
                    map.put("userId", mapper.writeValueAsString(json_value));
                } catch (JsonGenerationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            }

        }

        return map;
    }

    public void runTask() {
        final long timeInterval = 110000;// 两分钟运行一次
        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        // 每隔多少时间执行一条case数据
                        logger.info("---------");
                        Thread.sleep(timeInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

    }
}
