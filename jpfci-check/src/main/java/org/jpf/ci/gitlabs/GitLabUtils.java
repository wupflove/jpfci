/** 
* @author 吴平福 
* E-mail:wupf@asiainfo.com 
* @version 创建时间：2016年6月23日 下午4:06:35 
* 类说明 
*/

package org.jpf.ci.gitlabs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.type.TypeReference;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;

/**
 * 
 */
public class GitLabUtils {
    
    private static final Logger logger = LogManager.getLogger();
    /**
     * 
     * @param url
     * @return
     * @throws Exception
     */
    public static String getContent(String url) throws Exception {
        // String CONTENT_CHARSET = "UTF-8";
        String backContent = null;
        // private static final END_OBJECT = "0";
        // String eND_OBJECT = "0";

        HttpClient httpclient = null;
        HttpGet httpget = null;
        try {

            HttpParams params = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(params, 180 * 1000);
            HttpConnectionParams.setSoTimeout(params, 180 * 1000);
            HttpConnectionParams.setSocketBufferSize(params, 20000);

            HttpClientParams.setRedirecting(params, false);

            httpclient = new DefaultHttpClient(params);

            httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);

            HttpEntity entity = response.getEntity();
            if (entity != null) {

                InputStream is = entity.getContent();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(is));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = in.readLine()) != null) {
                    buffer.append(line);
                }

                backContent = buffer.toString();
            }
        } catch (Exception e) {
            httpget.abort();
            backContent = "";
            e.printStackTrace();

        } finally {
            if (httpclient != null)
                httpclient.getConnectionManager().shutdown();
        }
        return backContent;
    }

    /**
     * 
     * @param url
     * @return
     * @throws Exception
     */
    public static int postContent(String url) throws Exception {


        HttpPost httpRequst = new HttpPost(url);// 创建HttpPost对象

        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 10 * 1000);// 设置请求超时10秒
        HttpConnectionParams.setSoTimeout(httpParameters, 10 * 1000); // 设置等待数据超时10秒
       // HttpConnectionParams.setSocketBufferSize(httpParameters, 8192);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("name", "abc"));

        try {
            httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);
            return httpResponse.getStatusLine().getStatusCode();
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * 
     * @category @author 吴平福
     * @param iPrjId
     * @return update 2016年7月16日
     */
    public static ProjecInfo getPrjInfoById(int iPrjId) {

        try {
            // TODO Auto-generated constructor stub

            String address = GitLabUtils.getContent(GitlabInterfaceConst.HTTPURL + "/projects/"
                    + iPrjId + "?private_token=" + GitlabInterfaceConst.PRIVATE_TOKEN);
            // JsonNode cJsonNode ;
            if (address != null && address.length() > 0) {
                ObjectMapper mapper =
                        new ObjectMapper().setVisibility(JsonMethod.FIELD, Visibility.ANY);
                mapper.configure(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
                mapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                        true);
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                List<ProjecInfo> list =
                        mapper.readValue(address, new TypeReference<List<ProjecInfo>>() {});
                if (list.size() == 1) {
                    logger.info(iPrjId + ":" + list.get(0).getName());
                    return list.get(0);
                }
            }

        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * 
     * @param url
     * @return
     * @throws Exception
     */
    public static RespJsonData postContent(String url, List<NameValuePair> params)
            throws Exception {
        RespJsonData cRespPrjJsonData = new RespJsonData();

        HttpPost httpRequst = new HttpPost(url);// 鍒涘缓HttpPost瀵硅薄

        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 10 * 1000);// 璁剧疆璇锋眰瓒呮椂10绉?
        HttpConnectionParams.setSoTimeout(httpParameters, 10 * 1000); // 璁剧疆绛夊緟鏁版嵁瓒呮椂10绉?
        HttpConnectionParams.setSocketBufferSize(httpParameters, 8192);

        try {
            httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);
            logger.info(httpResponse.toString());

            cRespPrjJsonData.setResultCode(httpResponse.getStatusLine().getStatusCode());
            cRespPrjJsonData.setMsg(EntityUtils.toString(httpResponse.getEntity()));
            logger.info("postContent response status:"+httpResponse.getStatusLine().getStatusCode());
            logger.info(EntityUtils.toString(httpResponse.getEntity()));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (httpRequst != null)
                httpRequst.releaseConnection();
        }
        return cRespPrjJsonData;
    }
}
